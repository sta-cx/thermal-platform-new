package org.sdkj.ai.context;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties.SuggestionRule;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 通用规则引擎：对最近 facts 按配置规则（绑字段，不绑 tool 名）匹配，产出推荐动作。
 * 新增 Tool 只需加 suggestionRules 配置，不改本类。
 */
@Slf4j
@Component
public class SuggestionEngine {

    private static final int MAX_SUGGESTIONS = 3;

    public List<SuggestedAction> evaluate(List<EntityFact> facts, FocusEntity focus,
                                          List<SuggestionRule> rules) {
        List<SuggestedAction> out = new ArrayList<>();
        if (facts == null || facts.isEmpty() || rules == null || rules.isEmpty()) return out;
        Set<String> seenLabels = new HashSet<>();
        for (int i = facts.size() - 1; i >= 0 && out.size() < MAX_SUGGESTIONS; i--) {
            EntityFact fact = facts.get(i);
            for (SuggestionRule rule : rules) {
                if (matches(fact, rule) && seenLabels.add(rule.getLabel())) {
                    out.add(new SuggestedAction(rule.getLabel(), render(rule.getPromptTemplate(), fact, focus)));
                    if (out.size() >= MAX_SUGGESTIONS) break;
                }
            }
        }
        return out;
    }

    private boolean matches(EntityFact fact, SuggestionRule rule) {
        Object v = fact.keys().get(rule.getField());
        if (v == null || rule.getOp() == null || rule.getValue() == null) return false;
        String s = String.valueOf(v);
        return switch (rule.getOp()) {
            case "eq" -> s.equals(rule.getValue());
            case "in" -> Arrays.asList(rule.getValue().split(",")).contains(s);
            case "lt" -> cmp(v, rule.getValue()) < 0;
            case "gt" -> cmp(v, rule.getValue()) > 0;
            default -> false;
        };
    }

    /** 数值比较；任一不可解析为数值时返回 0（不命中 lt/gt）。 */
    private int cmp(Object v, String target) {
        try {
            return Double.compare(Double.parseDouble(String.valueOf(v)), Double.parseDouble(target));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String render(String template, EntityFact fact, FocusEntity focus) {
        if (template == null) return "";
        Map<String, Object> src = new HashMap<>();
        if (focus != null && focus.attrs() != null) src.putAll(focus.attrs());
        src.putAll(fact.keys());   // fact 优先级高于 focus
        String out = template;
        for (Map.Entry<String, Object> e : src.entrySet()) {
            out = out.replace("{" + e.getKey() + "}", String.valueOf(e.getValue()));
        }
        return out;
    }
}
