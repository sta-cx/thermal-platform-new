package org.sdkj.ai.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ContextualPromptRegistry {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final ConcurrentMap<String, ContextualPrompt> patternToPrompt = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ContextualPrompt> nameToPrompt = new ConcurrentHashMap<>();

    public synchronized void scanAndRegister(java.util.Collection<ContextualPrompt> prompts) {
        int count = 0;
        for (ContextualPrompt prompt : prompts) {
            register(prompt);
            count++;
        }
        log.info("ContextualPromptRegistry: loaded {} ContextualPrompt(s) from Spring", count);
    }

    public void register(ContextualPrompt prompt) {
        for (String pattern : prompt.routePatterns()) {
            ContextualPrompt prev = patternToPrompt.putIfAbsent(pattern, prompt);
            if (prev != null && prev != prompt) {
                throw new IllegalStateException(
                    "Route pattern '" + pattern + "' is already registered by "
                    + prev.getClass().getName() + ", conflicts with " + prompt.getClass().getName()
                );
            }
        }
        nameToPrompt.put(prompt.getClass().getSimpleName(), prompt);
        log.info("ContextualPromptRegistry: registered {} for patterns {}",
            prompt.getClass().getSimpleName(), List.of(prompt.routePatterns()));
    }

    public ContextualPrompt match(String route) {
        if (route == null || route.isBlank()) {
            return null;
        }
        for (var entry : patternToPrompt.entrySet()) {
            if (matcher.match(entry.getKey(), route)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public List<ContextualPrompt> listAll() {
        return new ArrayList<>(nameToPrompt.values());
    }

    public List<String> allPatterns() {
        return Collections.unmodifiableList(new ArrayList<>(patternToPrompt.keySet()));
    }
}
