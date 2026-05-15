package org.sdkj.ai.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个 @Tool 方法是写操作。
 *
 * Spring AI 原生的 @Tool 不区分读写;我们用这个补充注解告诉 ConfirmationGateAdvisor:
 *  - confirm=true(默认):拦截到 Tool 调用 → 不立即执行 → 写 ConfirmationStore → 通过 SSE 通知前端弹确认框
 *  - risk=HIGH:前端弹窗加 3 秒倒计时
 *  - risk=CRITICAL:不论 confirm,advisor 直接拒绝,记审计后抛 ToolRejectedException
 *  - permission:执行前用 SaCheckPermission 二次校验(独立于 Tool 方法本身的注解)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WriteTool {

    RiskLevel risk() default RiskLevel.MEDIUM;

    boolean confirm() default true;

    /** SaCheckPermission 表达式,如 "thermal:property:expense:edit" */
    String permission() default "";
}
