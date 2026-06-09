package org.sdkj.ai.context;

/** 条件边：condition 命中则跳到 gotoStepId（"END" 表示结束）。 */
public record Branch(String condition, String gotoStepId) {}
