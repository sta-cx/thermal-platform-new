package org.sdkj.ai.config;

import lombok.RequiredArgsConstructor;
import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualPromptRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PromptRegistrationListener {

    private final ContextualPromptRegistry registry;
    private final List<ContextualPrompt> prompts;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        registry.scanAndRegister(prompts);
    }
}
