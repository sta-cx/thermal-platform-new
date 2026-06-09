package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sdkj.ai.config.AiProperties.SuggestionRule;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class SuggestionEngineTest {

    private final SuggestionEngine engine = new SuggestionEngine();

    private SuggestionRule rule(String field, String op, String value, String label, String tpl) {
        SuggestionRule r = new SuggestionRule();
        r.setField(field); r.setOp(op); r.setValue(value); r.setLabel(label); r.setPromptTemplate(tpl);
        return r;
    }

    private EntityFact fact(Map<String, Object> keys) {
        Instant now = Instant.now();
        return new EntityFact("f", "getByHouseId", keys, now, now.plusSeconds(60));
    }

    @Test
    void matchesInOperatorAndRendersPlaceholder() {
        var facts = List.of(fact(Map.of("valveStatus", "2", "houseId", 123L)));
        var rules = List.of(rule("valveStatus", "in", "2,CLOSE,CLOSED", "一键开阀",
            "请给 houseId={houseId} 开阀"));
        var actions = engine.evaluate(facts, null, rules);
        assertEquals(1, actions.size());
        assertEquals("一键开阀", actions.get(0).label());
        assertEquals("请给 houseId=123 开阀", actions.get(0).prompt());
    }

    @Test
    void matchesGtOperator() {
        var facts = List.of(fact(Map.of("overdueDay", 120L, "expenseId", 9L)));
        var rules = List.of(rule("overdueDay", "gt", "90", "登记缴费", "缴费 expenseId={expenseId}"));
        var actions = engine.evaluate(facts, null, rules);
        assertEquals(1, actions.size());
        assertEquals("缴费 expenseId=9", actions.get(0).prompt());
    }

    @Test
    void noMatchReturnsEmpty() {
        var facts = List.of(fact(Map.of("valveStatus", "1")));   // 开，不匹配关闭规则
        var rules = List.of(rule("valveStatus", "in", "2,CLOSE", "开阀", "x"));
        assertTrue(engine.evaluate(facts, null, rules).isEmpty());
    }

    @Test
    void dedupesByLabel() {
        var facts = List.of(
            fact(Map.of("valveStatus", "2", "houseId", 1L)),
            fact(Map.of("valveStatus", "CLOSE", "houseId", 1L))
        );
        var rules = List.of(rule("valveStatus", "in", "2,CLOSE", "一键开阀", "开阀 {houseId}"));
        assertEquals(1, engine.evaluate(facts, null, rules).size(), "同 label 不重复");
    }

    @Test
    void fallsBackToFocusAttrsForPlaceholder() {
        var facts = List.of(fact(Map.of("valveStatus", "2")));   // fact 无 houseId
        var focus = new FocusEntity("house", 77L, "house#77", Map.of("houseId", 77L), Instant.now());
        var rules = List.of(rule("valveStatus", "in", "2", "开阀", "开阀 {houseId}"));
        var actions = engine.evaluate(facts, focus, rules);
        assertEquals("开阀 77", actions.get(0).prompt());
    }
}
