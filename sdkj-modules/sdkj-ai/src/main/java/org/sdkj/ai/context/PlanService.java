package org.sdkj.ai.context;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.*;
import java.util.function.Function;

/**
 * 多步任务规划（混合，spec §6.3）。
 * - 高频模板快路径：供热投诉（带条件分支精排，确定性，不依赖 LLM 规划质量）。
 * - 通用 LLM 动态规划：读 {@link ToolRegistry} 全量非 CRITICAL Tool，LLM 产出线性步骤序列；
 *   新增 Tool 自动进入可编排范围，无需为每个 Tool 写模板。
 * 安全护栏：排除 CRITICAL、步数上限、未知工具丢弃、不足两步不算编排、计划必经批准、写步逐个确认。
 */
@Slf4j
@Service
public class PlanService {

    private final ChatClient planClient;     // LLM：houseId 抽取 + 通用计划生成；可为 null（测试/降级）
    private final ContextStore contextStore;
    private final AiProperties aiProperties;
    private final MeterRegistry meterRegistry;
    private final ToolRegistry registry;     // 可为 null（测试时）

    public PlanService(ChatClient.Builder builder, ContextStore contextStore,
                       AiProperties aiProperties, MeterRegistry meterRegistry,
                       ToolRegistry registry) {
        this.planClient = builder == null ? null : builder.build();
        this.contextStore = contextStore;
        this.aiProperties = aiProperties;
        this.meterRegistry = meterRegistry;
        this.registry = registry;
    }

    /** 用于 LLM 抽取 houseId 的 structured 输出。 */
    public record HouseRef(Long houseId) {}

    /** LLM 通用计划草案（structured 输出）。 */
    public record PlanDraft(boolean multiStep, String title, List<DraftStep> steps) {}
    public record DraftStep(String toolName, String desc, List<ArgKV> args) {}
    public record ArgKV(String name, String value) {}

    /**
     * 尝试规划。命中可编排任务且能装配 → 返回 AWAITING_APPROVAL 的 TaskState；否则 null（回落普通对话）。
     */
    public TaskState tryPlan(String userMessage, Long sessionId) {
        AiProperties.Context cfg = aiProperties.getContext();
        if (!cfg.isEnabled() || !cfg.isOrchestration()) return null;

        // 1) 高频模板快路径（供热投诉，带条件分支）
        if (isHeatingComplaint(userMessage)) {
            Long houseId = resolveHouseId(userMessage, sessionId);
            if (houseId != null) {
                meterRegistry.counter("ai.ctx.task.planned", "type", "handle_heating_complaint", "engine", "template").increment();
                return buildHeatingComplaintTask(houseId);
            }
            log.info("[PlanService] heating intent but no houseId; try generic LLM planning");
        }

        // 2) 通用 LLM 动态规划（粗筛命中才调 LLM，避免每条消息都多一次模型调用）
        if (!looksLikeMultiStep(userMessage)) return null;
        return tryLlmPlan(userMessage, sessionId);
    }

    /** 供热投诉意图识别（确定性，可单测）。 */
    public boolean isHeatingComplaint(String msg) {
        if (msg == null) return false;
        boolean act = msg.contains("处理") || msg.contains("解决") || msg.contains("报修处理");
        boolean topic = msg.contains("供热") || msg.contains("不热") || msg.contains("暖气") || msg.contains("不暖");
        return act && topic;
    }

    /** 通用多步意图粗筛（确定性，可单测，配置化）：连接词强信号 OR 处理类动词 + 业务名词。 */
    public boolean looksLikeMultiStep(String msg) {
        if (msg == null) return false;
        AiProperties.Context cfg = aiProperties.getContext();
        for (String k : cfg.getOrchestrationConnectors()) if (msg.contains(k)) return true;
        boolean act = false;
        for (String a : cfg.getOrchestrationActions()) if (msg.contains(a)) { act = true; break; }
        if (!act) return false;
        for (String n : cfg.getOrchestrationNouns()) if (msg.contains(n)) return true;
        return false;
    }

    /** houseId：先 focus.attrs，再 LLM 抽取。 */
    private Long resolveHouseId(String userMessage, Long sessionId) {
        try {
            ConversationContext ctx = contextStore.load(sessionId);
            if (ctx.getFocus() != null && ctx.getFocus().attrs() != null) {
                Object h = ctx.getFocus().attrs().get("houseId");
                if (h instanceof Number n) return n.longValue();
            }
            if (planClient == null) return null;
            HouseRef ref = planClient.prompt()
                .system("从用户消息中提取热户 houseId（纯数字）。没有明确数字 ID 时 houseId 返回 null。只输出 JSON。")
                .user(userMessage)
                .call()
                .entity(HouseRef.class);
            return ref == null ? null : ref.houseId();
        } catch (Exception e) {
            log.warn("[PlanService] resolveHouseId failed: {}", e.getMessage());
            return null;
        }
    }

    /** 通用 LLM 动态规划：读注册表 → LLM 产出线性步骤 → 校验装配。失败返回 null（降级普通对话）。 */
    private TaskState tryLlmPlan(String userMessage, Long sessionId) {
        if (planClient == null || registry == null) return null;
        AiProperties.Context cfg = aiProperties.getContext();
        try {
            String catalog = buildToolCatalog();
            if (catalog.isBlank()) return null;
            String focusHint = describeFocus(sessionId);
            PlanDraft draft = planClient.prompt()
                .system(plannerSystem(catalog, focusHint, cfg.getMaxOrchestrationSteps()))
                .user(userMessage)
                .call()
                .entity(PlanDraft.class);
            if (draft == null || !draft.multiStep()) return null;
            TaskState ts = assembleLinearPlan(draft.title(), draft.steps(),
                registry::resolve, cfg.getMaxOrchestrationSteps());
            if (ts != null) {
                meterRegistry.counter("ai.ctx.task.planned", "type", "llm_dynamic", "engine", "llm").increment();
            } else {
                log.info("[PlanService] LLM draft rejected by validation (unknown/critical/too-few steps)");
            }
            return ts;
        } catch (Exception e) {
            log.warn("[PlanService] LLM dynamic plan failed: {}", e.getMessage());
            return null;
        }
    }

    /** 工具目录（非 CRITICAL），喂给规划 LLM。动态取自注册表 → 新增 Tool 即刻可编排。 */
    private String buildToolCatalog() {
        StringBuilder sb = new StringBuilder();
        for (ToolMetadata md : registry.all()) {
            if (md.risk() == RiskLevel.CRITICAL) continue;
            String desc = md.description() == null ? "" : md.description().replaceAll("\\s+", " ").trim();
            sb.append("- ").append(md.methodName())
              .append(md.risk() == RiskLevel.LOW ? "（只读）" : "（写操作，需确认）")
              .append("：").append(desc).append("\n");
        }
        return sb.toString();
    }

    private String describeFocus(Long sessionId) {
        try {
            ConversationContext ctx = contextStore.load(sessionId);
            FocusEntity f = ctx.getFocus();
            if (f == null) return "当前无已知焦点实体。";
            return "当前焦点实体：" + f.entityType() + " id=" + f.entityId() + "（可作为相关步骤的 ID）。";
        } catch (Exception e) {
            return "当前无已知焦点实体。";
        }
    }

    private String plannerSystem(String catalog, String focusHint, int maxSteps) {
        return """
            你是供热管理系统的任务规划器。判断用户请求是否需要【多个工具按顺序执行】才能完成，并规划步骤。
            只能使用下列工具（用方法名，禁止杜撰、禁止使用未列出的名字）：
            %s
            %s
            规则：
            1. 仅当确实需要 ≥2 个步骤时 multiStep=true；单步、纯咨询或无法用上述工具完成时 multiStep=false 且 steps 为空。
            2. steps 为线性先后顺序；每个 toolName 必须严格等于上面列出的方法名之一。
            3. 已知的 ID（如 houseId）放进该步 args（name 用参数名，value 用值）；不确定的 ID 留空，系统会用上下文补全。
            4. 写操作步骤会逐个请用户确认，你只负责规划顺序，不要假设已执行。
            5. 步数不超过 %d。
            6. 只输出 JSON，不要解释。
            """.formatted(catalog, focusHint, maxSteps);
    }

    /** 供热投诉模板：查阀门 →(关)开阀 / (故障)报修。 */
    public TaskState buildHeatingComplaintTask(Long houseId) {
        TaskStep s1 = new TaskStep("s1", "getByHouseId", "查询该户阀门状态",
            Map.of("houseId", houseId),
            List.of(new Branch("valveClosed", "s2"),
                    new Branch("valveError", "s3"),
                    new Branch("always", "END")), "WAITING");
        TaskStep s2 = new TaskStep("s2", "dispatch", "阀门关闭 → 开阀",
            Map.of("houseId", houseId, "action", "OPEN"),
            List.of(new Branch("always", "END")), "WAITING");
        TaskStep s3 = new TaskStep("s3", "create", "阀门故障 → 创建报修单",
            Map.of("houseId", houseId, "repairInfo", "阀门反馈异常，系统自动报修"),
            List.of(new Branch("always", "END")), "WAITING");

        TaskState ts = new TaskState();
        ts.setTaskId(UUID.randomUUID().toString());
        ts.setTaskType("handle_heating_complaint");
        ts.setTitle("处理供热问题（房屋 " + houseId + "）");
        ts.setSteps(List.of(s1, s2, s3));
        ts.setCurrentStepId("s1");
        ts.setStatus("AWAITING_APPROVAL");
        return ts;
    }

    // ===== 纯静态：草案校验 + 线性装配（可单测，不依赖 Spring/LLM）=====

    /**
     * 把 LLM 草案校验并装配成线性 TaskState。
     * 规则：未知工具丢弃；含 CRITICAL 整体拒绝（返回 null）；有效步 &lt; 2 返回 null；步数封顶 maxSteps。
     */
    static TaskState assembleLinearPlan(String title, List<DraftStep> steps,
                                        Function<String, ToolMetadata> resolver, int maxSteps) {
        if (steps == null || resolver == null) return null;
        List<TaskStep> built = new ArrayList<>();
        int idx = 0;
        for (DraftStep ds : steps) {
            if (built.size() >= maxSteps) break;
            if (ds == null || ds.toolName() == null || ds.toolName().isBlank()) continue;
            ToolMetadata md = resolver.apply(ds.toolName().trim());
            if (md == null) continue;                          // 未知工具 → 丢弃
            if (md.risk() == RiskLevel.CRITICAL) return null;  // 含禁止操作 → 整体拒绝
            idx++;
            TaskStep st = new TaskStep();
            st.setStepId("s" + idx);
            st.setToolName(md.methodName());
            st.setDesc(ds.desc() == null || ds.desc().isBlank() ? md.methodName() : ds.desc().trim());
            st.setPresetArgs(coerceArgs(ds.args()));
            st.setStepStatus("WAITING");
            built.add(st);
        }
        if (built.size() < 2) return null;                     // 不足两步不算编排
        for (int i = 0; i < built.size(); i++) {
            String go = (i == built.size() - 1) ? "END" : built.get(i + 1).getStepId();
            built.get(i).setBranches(List.of(new Branch("always", go)));
        }
        TaskState ts = new TaskState();
        ts.setTaskId(UUID.randomUUID().toString());
        ts.setTaskType("llm_dynamic");
        ts.setTitle(title == null || title.isBlank() ? "多步任务" : title.trim());
        ts.setSteps(built);
        ts.setCurrentStepId(built.get(0).getStepId());
        ts.setStatus("AWAITING_APPROVAL");
        return ts;
    }

    static Map<String, Object> coerceArgs(List<ArgKV> args) {
        Map<String, Object> m = new HashMap<>();
        if (args == null) return m;
        for (ArgKV kv : args) {
            if (kv == null || kv.name() == null || kv.name().isBlank()) continue;
            m.put(kv.name().trim(), coerceVal(kv.value()));
        }
        return m;
    }

    /** 数值串转 Long/Double（ToolArgBinder 再按形参类型 Long→Integer 等收敛）；true/false 转布尔；其余原样。 */
    static Object coerceVal(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.matches("-?\\d{1,18}")) { try { return Long.valueOf(s); } catch (NumberFormatException ignored) {} }
        if (s.matches("-?\\d+\\.\\d+")) { try { return Double.valueOf(s); } catch (NumberFormatException ignored) {} }
        if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) return Boolean.valueOf(s);
        return v;
    }
}
