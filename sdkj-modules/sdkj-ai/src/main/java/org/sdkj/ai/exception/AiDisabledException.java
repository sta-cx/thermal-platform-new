package org.sdkj.ai.exception;

public class AiDisabledException extends RuntimeException {

    public AiDisabledException() {
        super("该租户暂未开通 AI 功能");
    }
}
