package org.sdkj.ai.tools.dispatcher;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Dispatcher.dispatch 的返回值。AssistantService 据此决定:
 *  - hasPending=true:发 SSE toolCallPending 帧 + 关流
 *  - hasPending=false:把 toolResults 加进历史,触发下一轮 chatClient.stream()
 */
@Data
@Builder
public class ToolCallResult {
    private boolean hasPending;
    private String pendingCallId;             // hasPending=true 时给前端
    private List<ToolExecResult> toolResults; // LOW 风险 Tool 的执行结果

    @Data
    @Builder
    public static class ToolExecResult {
        private String toolName;
        private String resultJson;
    }
}
