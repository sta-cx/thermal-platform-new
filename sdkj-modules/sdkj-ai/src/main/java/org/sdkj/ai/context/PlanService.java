package org.sdkj.ai.context;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 多步任务规划。初版模板驱动（供热投诉），LLM 仅用于实体抽取（houseId）。
 * 新增任务类型 = 加 buildXxxTask 模板 + isXxx 意图识别。
 */
@Slf4j
@Service
public class PlanService {

    private final ChatClient planClient;     // 仅用于 houseId 抽取；可为 null（测试/降级）
    private final ContextStore contextStore;
    private final AiProperties aiProperties;

    public PlanService(ChatClient.Builder builder, ContextStore contextStore, AiProperties aiProperties) {
        this.planClient = builder == null ? null : builder.build();
        this.contextStore = contextStore;
        this.aiProperties = aiProperties;
    }

    /** 用于 LLM 抽取 houseId 的 structured 输出。 */
    public record HouseRef(Long houseId) {}

    /**
     * 尝试规划。命中多步任务且能确定实体 → 返回 AWAITING_APPROVAL 的 TaskState；否则 null（回落普通对话）。
     */
    public TaskState tryPlan(String userMessage, Long sessionId) {
        AiProperties.Context cfg = aiProperties.getContext();
        if (!cfg.isEnabled() || !cfg.isOrchestration()) return null;
        if (!isHeatingComplaint(userMessage)) return null;
        Long houseId = resolveHouseId(userMessage, sessionId);
        if (houseId == null) {
            log.info("[PlanService] heating complaint intent but no houseId resolved; fallback to chat");
            return null;
        }
        return buildHeatingComplaintTask(houseId);
    }

    /** 意图识别（确定性，可单测）。 */
    public boolean isHeatingComplaint(String msg) {
        if (msg == null) return false;
        boolean act = msg.contains("处理") || msg.contains("解决") || msg.contains("报修处理");
        boolean topic = msg.contains("供热") || msg.contains("不热") || msg.contains("暖气") || msg.contains("不暖");
        return act && topic;
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
            // LLM 抽取（structured）。
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
}
