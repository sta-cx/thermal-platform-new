package org.sdkj.ai.tools.invocation;

import org.sdkj.ai.tools.store.PendingToolCall;

import java.util.Map;

public interface ToolInvocationRecorder {
    /** Section 4 T12 实现:写 ai_chat_message TOOL row + ai_tool_invocation 详情 */
    Long record(PendingToolCall call, String resultJson, String status, int latencyMs, String errorMessage);

    /**
     * 编排只读步专用:无 PendingToolCall(只读不走确认流),直接写 TOOL message + invocation
     * (pending_call_id=null, risk_level=LOW),返回 message id 供前端展开看查询数据。
     */
    Long recordReadonly(Long sessionId, String tenantId, Long userId, String toolName,
                        String summaryContent, Map<String, Object> args,
                        String resultJson, String status, int latencyMs);
}
