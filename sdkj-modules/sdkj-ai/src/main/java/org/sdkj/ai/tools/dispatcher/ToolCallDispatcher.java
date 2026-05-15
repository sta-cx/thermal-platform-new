package org.sdkj.ai.tools.dispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.sdkj.ai.tools.store.ConfirmationStore;
import org.sdkj.ai.tools.store.PendingToolCall;
import org.sdkj.ai.tools.store.PendingToolCallStatus;

import java.time.Instant;
import java.util.*;

/**
 * 接收 LLM 决策的 ToolCall 列表,按 RiskLevel 分流执行 / 拒绝 / 暂存。
 *
 * 调用约定:AssistantService.streamChat 把每一轮 LLM 响应里的 ToolCall 全部传进来。
 * 返回 ToolCallResult:
 *  - 全部 LOW → toolResults 装好,hasPending=false,Service 继续下一轮
 *  - 任一 MEDIUM/HIGH → 该 Tool 写 store + 抛 PendingConfirmationException
 *  - 任一 CRITICAL → 抛 CriticalToolRejectedException
 *
 * 注意:本类不负责权限校验、不负责 dryRun 覆盖 — 这两件事在 /confirm 端点处理。
 * Dispatcher 只做"拦截 / 立即执行"二选一。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolCallDispatcher {

    private final ToolRegistry registry;
    private final ConfirmationStore store;
    private final ObjectMapper objectMapper;

    public record ToolCallRequest(
        String toolName,
        Map<String, Object> arguments,
        String tenantId,
        Long userId,
        Long sessionId,
        Long messageId
    ) {}

    public ToolCallResult dispatch(List<ToolCallRequest> calls) {
        List<ToolCallResult.ToolExecResult> lowResponses = new ArrayList<>();
        List<ToolCallRequest> writeCalls = new ArrayList<>();

        // 第一遍：解析元数据，执行所有 LOW 工具，收集写操作
        for (ToolCallRequest call : calls) {
            ToolMetadata md = registry.resolve(call.toolName());
            if (md == null) {
                log.warn("[Dispatcher] tool {} not registered (disabled or unknown)", call.toolName());
                lowResponses.add(ToolCallResult.ToolExecResult.builder()
                    .toolName(call.toolName())
                    .resultJson("{\"error\":\"tool not available\"}")
                    .build());
                continue;
            }

            // CRITICAL:直接拒
            if (md.risk() == RiskLevel.CRITICAL) {
                throw new CriticalToolRejectedException(md.fullName());
            }

            // LOW:立即执行
            if (md.risk() == RiskLevel.LOW) {
                String result = executeSafely(md, call.arguments());
                lowResponses.add(ToolCallResult.ToolExecResult.builder()
                    .toolName(call.toolName())
                    .resultJson(result)
                    .build());
                continue;
            }

            // MEDIUM / HIGH:暂存到写操作列表
            writeCalls.add(call);
        }

        // 第二遍：处理写操作（逐个确认，当前架构每次只处理第一个）
        if (!writeCalls.isEmpty()) {
            if (writeCalls.size() > 1) {
                log.warn("[Dispatcher] {} write tool calls in single turn, only first will be confirmed this round (remaining: {})",
                    writeCalls.size(), writeCalls.subList(1, writeCalls.size()).stream().map(ToolCallRequest::toolName).toList());
            }
            ToolCallRequest first = writeCalls.get(0);
            ToolMetadata md = registry.resolve(first.toolName());
            String callId = UUID.randomUUID().toString();
            PendingToolCall ptc = PendingToolCall.builder()
                .callId(callId)
                .tenantId(first.tenantId())
                .userId(first.userId())
                .sessionId(first.sessionId())
                .messageId(first.messageId())
                .toolName(md.fullName())
                .risk(md.risk())
                .arguments(first.arguments())
                .effectiveArgs(first.arguments())
                .status(PendingToolCallStatus.PENDING)
                .createdAt(Instant.now())
                .expireAt(Instant.now().plusSeconds(1800))
                .build();
            store.save(ptc);
            log.info("[Dispatcher] pending tool call persisted: callId={} tool={} risk={}",
                callId, md.fullName(), md.risk());
            throw new PendingConfirmationException(callId);
        }

        return ToolCallResult.builder()
            .hasPending(false)
            .toolResults(lowResponses)
            .build();
    }

    /** LOW Tool 立即执行 — 异常吃掉转 error JSON,不让单个 Tool 失败 break 整条对话 */
    private String executeSafely(ToolMetadata md, Map<String, Object> args) {
        try {
            Object[] params = ToolArgBinder.bind(md, args);
            Object out = md.method().invoke(md.bean(), params);
            return out == null ? "null" : objectMapper.writeValueAsString(out);
        } catch (Exception e) {
            log.error("[Dispatcher] LOW tool {} failed: {}", md.fullName(), e.getMessage(), e);
            try {
                return objectMapper.writeValueAsString(Map.of("error", e.getMessage()));
            } catch (JsonProcessingException ignored) {
                return "{\"error\":\"unexpected\"}";
            }
        }
    }
}
