package org.sdkj.ai.core;

import java.time.Duration;

public interface ContextualPrompt {

    String[] routePatterns();

    String displayName();

    PromptPayload buildPrompt(ContextualRequest ctx);

    Class<? extends ContextualView> viewSchema();

    default Duration cacheTtl() {
        return Duration.ofMinutes(5);
    }

    default String requiredPermission() {
        return null;
    }
}
