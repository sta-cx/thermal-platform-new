package org.sdkj.ai.tools.invocation;

import org.sdkj.ai.tools.store.PendingToolCall;

public interface ToolInvocationRecorder {
    /** Section 4 T12 实现:写 ai_chat_message TOOL row + ai_tool_invocation 详情 */
    Long record(PendingToolCall call, String resultJson, String status, int latencyMs, String errorMessage);
}
