package org.sdkj.ai.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 单会话级上下文记忆。可变（供 mutate）。整体经 Jackson 序列化进 Redis / context_data。 */
@Data
@NoArgsConstructor
public class ConversationContext {

    private Long sessionId;
    private FocusEntity focus;
    private List<EntityFact> facts = new ArrayList<>();
    private Instant updatedAt;

    public ConversationContext(Long sessionId) {
        this.sessionId = sessionId;
    }

    /** 追加 facts，超出 maxFacts 时按 createdAt 保留最新。 */
    public void addFacts(List<EntityFact> incoming, int maxFacts) {
        if (incoming == null || incoming.isEmpty()) return;
        facts.addAll(incoming);
        trim(maxFacts);
    }

    /** 主动遗忘：清过期 fact + 裁剪超量 + 清闲置 focus。 */
    public void applyForgetting(int maxFacts, int focusIdleMinutes, Instant now) {
        facts.removeIf(f -> f.expireAt() != null && f.expireAt().isBefore(now));
        trim(maxFacts);
        if (focus != null && focus.updatedAt() != null
            && Duration.between(focus.updatedAt(), now).toMinutes() > focusIdleMinutes) {
            focus = null;
        }
    }

    private void trim(int maxFacts) {
        if (facts.size() > maxFacts) {
            facts.sort(Comparator.comparing(EntityFact::createdAt));
            facts = new ArrayList<>(facts.subList(facts.size() - maxFacts, facts.size()));
        }
    }
}
