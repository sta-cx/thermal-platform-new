package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class ConversationContextTest {

    private EntityFact fact(String id, Instant created, Instant expire) {
        return new EntityFact(id, "getByHouseId", Map.of("houseId", 1L), created, expire);
    }

    @Test
    void addFactsKeepsNewestWhenOverCapacity() {
        ConversationContext ctx = new ConversationContext(100L);
        Instant base = Instant.parse("2026-06-08T10:00:00Z");
        List<EntityFact> batch = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            batch.add(fact("f" + i, base.plusSeconds(i), base.plusSeconds(3600)));
        }
        ctx.addFacts(batch, 3);
        assertEquals(3, ctx.getFacts().size());
        assertEquals("f2", ctx.getFacts().get(0).factId(), "应保留最新的 3 条");
        assertEquals("f4", ctx.getFacts().get(2).factId());
    }

    @Test
    void applyForgettingRemovesExpiredFacts() {
        ConversationContext ctx = new ConversationContext(100L);
        Instant now = Instant.parse("2026-06-08T10:00:00Z");
        ctx.getFacts().add(fact("alive", now, now.plusSeconds(60)));
        ctx.getFacts().add(fact("dead", now.minusSeconds(120), now.minusSeconds(60)));
        ctx.applyForgetting(20, 10, now);
        assertEquals(1, ctx.getFacts().size());
        assertEquals("alive", ctx.getFacts().get(0).factId());
    }

    @Test
    void applyForgettingClearsIdleFocus() {
        ConversationContext ctx = new ConversationContext(100L);
        Instant now = Instant.parse("2026-06-08T10:00:00Z");
        ctx.setFocus(new FocusEntity("house", 1L, "house#1", Map.of(), now.minusSeconds(20 * 60)));
        ctx.applyForgetting(20, 10, now); // focusIdleMinutes=10，已闲置 20min
        assertNull(ctx.getFocus());
    }
}
