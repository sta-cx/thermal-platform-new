package org.sdkj.ai.tools.dispatcher;

import lombok.Getter;

@Getter
public class CriticalToolRejectedException extends RuntimeException {
    private final String toolName;

    public CriticalToolRejectedException(String toolName) {
        super("CRITICAL risk tool rejected by ConfirmationGate: " + toolName);
        this.toolName = toolName;
    }
}
