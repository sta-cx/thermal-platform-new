package org.sdkj.ai.tools.invocation;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.tools.store.PendingToolCall;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NoopToolInvocationRecorderConfig {

    @Bean
    @ConditionalOnMissingBean(ToolInvocationRecorder.class)
    public ToolInvocationRecorder noopRecorder() {
        return (call, result, status, latency, err) ->
            log.warn("[NoopRecorder] tool {} → status={} ({}ms) — REAL recorder pending (Section 4 T12)",
                call.getToolName(), status, latency);
    }
}
