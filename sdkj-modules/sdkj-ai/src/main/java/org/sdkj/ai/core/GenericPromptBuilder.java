package org.sdkj.ai.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenericPromptBuilder {

    private static final String SYSTEM_PROMPT = """
        你是 SDKJ 智慧供热平台的 AI 助手。
        根据用户当前所在页面，生成 1-2 个简洁的旁注 section。
        可用 section 类型：NARRATIVE / SUGGESTION / LINK。
        严格遵循 JSON Schema，不要输出额外字段。
        如果要生成跳转链接（SUGGESTION 的 actionUrl 或 LINK 的 url），
        只能从下方"可用页面路径"列表中选择，不要编造不存在的路径。
        """;

    private GenericPromptBuilder() {}

    public static PromptPayload build(String route, String routeName,
                                       List<String> availableRoutes,
                                       Map<String, Object> extraContext) {
        String userPrompt = """
            当前页面: {route} ({routeName})
            可用页面路径: {availableRoutes}
            额外上下文: {extraContext}

            请生成旁注。
            """;

        Map<String, Object> vars = new HashMap<>();
        vars.put("route", route);
        vars.put("routeName", routeName != null ? routeName : route);
        vars.put("availableRoutes", String.join(", ", availableRoutes));
        vars.put("extraContext", extraContext != null ? extraContext.toString() : "无");

        return PromptPayload.builder()
            .systemPrompt(SYSTEM_PROMPT)
            .userPromptTemplate(userPrompt)
            .templateVars(vars)
            .build();
    }
}
