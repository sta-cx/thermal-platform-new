package org.sdkj.ai.assistant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // === Phase 2B: Tool Calling ===

    /** 写 Tool 暂停 — 前端打开 ToolCallConfirm 弹窗 */
    private PendingToolCallView toolCallPending;

    /** CRITICAL Tool 拒绝 — 前端提示用户不可执行 */
    private RejectedToolView toolCallRejected;

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
}
