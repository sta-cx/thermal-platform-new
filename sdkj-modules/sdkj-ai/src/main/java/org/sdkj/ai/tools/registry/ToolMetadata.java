package org.sdkj.ai.tools.registry;

import org.sdkj.ai.tools.annotation.RiskLevel;

import java.lang.reflect.Method;

public record ToolMetadata(
    String beanName,
    String methodName,
    String fullName,         // beanName + "." + methodName,作为 Tool 名传给 LLM
    String description,      // @Tool description
    RiskLevel risk,
    boolean requireConfirm,
    String permission,
    Method method,
    Object bean
) {
    public boolean isWrite() {
        return risk != RiskLevel.LOW;
    }
}
