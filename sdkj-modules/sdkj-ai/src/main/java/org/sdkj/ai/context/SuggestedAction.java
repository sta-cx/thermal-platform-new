package org.sdkj.ai.context;

/** 一条推荐：label=按钮文案，prompt=点击后填入输入框的话术。 */
public record SuggestedAction(String label, String prompt) {}
