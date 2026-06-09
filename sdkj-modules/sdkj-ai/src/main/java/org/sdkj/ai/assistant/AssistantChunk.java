package org.sdkj.ai.assistant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.sdkj.ai.kb.Citation;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssistantChunk {
    private String delta;
    private Long sessionId;
    private Long messageId;
    private Boolean finish;
    private String error;

    /** RAG 引用来源 — 在终止帧（finish / pending / rejected）中携带 */
    private List<Citation> citations;

    // === Phase 2B: Tool Calling ===

    /** 写 Tool 暂停 — 前端打开 ToolCallConfirm 弹窗 */
    private PendingToolCallView toolCallPending;

    /** CRITICAL Tool 拒绝 — 前端提示用户不可执行 */
    private RejectedToolView toolCallRejected;

    /** Tool 执行结果摘要 — 前端实时渲染 TOOL 角色消息卡片 */
    private ToolResultView toolCallResult;

    /** Phase3 能力 B：本轮回复后的下一步操作建议（空/无则不下发）。 */
    private java.util.List<org.sdkj.ai.context.SuggestedAction> suggestions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingToolCallView {
        private String callId;
        private String toolName;
        private String risk;              // LOW/MEDIUM/HIGH/CRITICAL
        private Map<String, Object> arguments;
        private String description;       // @Tool description,前端展示给用户
        private int countdownSeconds;     // HIGH=3,MEDIUM=0
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectedToolView {
        private String toolName;
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolResultView {
        private Long messageId;
        private String toolName;
        private String status;    // SUCCESS / FAILED / DRY_RUN
        private String summary;
    }
}
