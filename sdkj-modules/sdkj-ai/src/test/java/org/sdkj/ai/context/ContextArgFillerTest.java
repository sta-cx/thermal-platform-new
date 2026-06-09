package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class ContextArgFillerTest {

    private final ContextArgFiller filler = new ContextArgFiller();
    private final List<String> entityKeys = List.of("houseId", "valveId", "expenseId");

    private ConversationContext ctxWithFocus(Map<String, Object> attrs) {
        ConversationContext ctx = new ConversationContext(1L);
        ctx.setFocus(new FocusEntity("house", 123L, "house#123", attrs, Instant.now()));
        return ctx;
    }

    @Test
    void fillsMissingIdFromFocusAttrs() {
        var ctx = ctxWithFocus(Map.of("houseId", 123L, "valveId", 456L));
        var out = filler.fill(Set.of("houseId", "action"),
            new HashMap<>(Map.of("action", "CLOSE")), ctx, entityKeys);
        assertEquals(123L, ((Number) out.get("houseId")).longValue());
        assertEquals("CLOSE", out.get("action"));
    }

    @Test
    void doesNotOverrideProvidedValue() {
        var ctx = ctxWithFocus(Map.of("houseId", 123L));
        var out = filler.fill(Set.of("houseId"),
            new HashMap<>(Map.of("houseId", 999L)), ctx, entityKeys);
        assertEquals(999L, ((Number) out.get("houseId")).longValue(), "LLM 已给的值不覆盖");
    }

    @Test
    void doesNotFillNonEntityKeyParam() {
        var ctx = ctxWithFocus(Map.of("houseId", 123L, "note", "hello"));
        var out = filler.fill(Set.of("note"), new HashMap<>(), ctx, entityKeys);
        assertFalse(out.containsKey("note"), "note 不是 entityKey，不补");
    }

    @Test
    void skipsFillWhenFactsConflict() {
        // 无 focus，facts 里 expenseId 有两个不同值 → 歧义，不补
        ConversationContext ctx = new ConversationContext(1L);
        Instant now = Instant.now();
        ctx.getFacts().add(new EntityFact("a", "queryUnpaid", Map.of("expenseId", 11L), now, now.plusSeconds(60)));
        ctx.getFacts().add(new EntityFact("b", "queryUnpaid", Map.of("expenseId", 22L), now, now.plusSeconds(60)));
        var out = filler.fill(Set.of("expenseId"), new HashMap<>(), ctx, entityKeys);
        assertFalse(out.containsKey("expenseId"), "多个不同值时不补全，避免张冠李戴");
    }

    @Test
    void fillsFromFactsWhenUnique() {
        ConversationContext ctx = new ConversationContext(1L);
        Instant now = Instant.now();
        ctx.getFacts().add(new EntityFact("a", "queryUnpaid", Map.of("expenseId", 11L), now, now.plusSeconds(60)));
        var out = filler.fill(Set.of("expenseId"), new HashMap<>(), ctx, entityKeys);
        assertEquals(11L, ((Number) out.get("expenseId")).longValue());
    }

    @Test
    void returnsArgsUnchangedWhenNoContext() {
        var out = filler.fill(Set.of("houseId"), new HashMap<>(), new ConversationContext(1L), entityKeys);
        assertTrue(out.isEmpty());
    }
}
