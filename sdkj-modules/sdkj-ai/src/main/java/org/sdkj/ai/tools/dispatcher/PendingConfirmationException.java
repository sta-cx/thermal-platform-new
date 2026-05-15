package org.sdkj.ai.tools.dispatcher;

import lombok.Getter;

/**
 * 写 Tool 拦截后抛出。AssistantService 捕获后转 SSE toolCallPending 帧。
 * 这不是真正的"异常情况" — 是合法的控制流信号(不要包装为 5xx)。
 */
@Getter
public class PendingConfirmationException extends RuntimeException {
    private final String callId;

    public PendingConfirmationException(String callId) {
        super("tool call pending user confirmation: " + callId);
        this.callId = callId;
    }
}
