package org.sdkj.ai.tools.store;

import lombok.Builder;
import lombok.Data;
import org.sdkj.ai.tools.annotation.RiskLevel;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class PendingToolCall {
    /** UUID,前后端共享 ID */
    private String callId;
    private String tenantId;
    private Long userId;
    private Long sessionId;
    private Long messageId;       // 触发的 ASSISTANT message,可空
    private String toolName;      // beanName.methodName
    private RiskLevel risk;
    /** LLM 决策的入参(已转成 Map<String, Object>) */
    private Map<String, Object> arguments;
    /** 权限覆盖后实际入参(如 dryRun 强改);未覆盖时与 arguments 相同 */
    private Map<String, Object> effectiveArgs;
    private PendingToolCallStatus status;
    /** 执行结果 JSON(成功)或错误消息(失败) */
    private String result;
    private Instant createdAt;
    private Instant decidedAt;
    private Instant executedAt;
    private Instant expireAt;
}
