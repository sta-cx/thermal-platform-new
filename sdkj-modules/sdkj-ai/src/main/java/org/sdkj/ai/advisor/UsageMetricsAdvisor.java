package org.sdkj.ai.advisor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiUsageLog;
import org.sdkj.ai.mapper.AiUsageLogMapper;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.lang.NonNull;

import java.util.Date;

@Slf4j
public class UsageMetricsAdvisor implements CallAdvisor {

    private final AiUsageLogMapper usageLogMapper;
    private final MeterRegistry meterRegistry;

    public UsageMetricsAdvisor(AiUsageLogMapper usageLogMapper, MeterRegistry meterRegistry) {
        this.usageLogMapper = usageLogMapper;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public String getName() {
        return "UsageMetricsAdvisor";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    @NonNull
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request,
                                          @NonNull CallAdvisorChain chain) {
        long start = System.currentTimeMillis();
        boolean success = true;
        String errorMsg = null;
        ChatClientResponse response = null;
        try {
            response = chain.nextCall(request);
            return response;
        } catch (RuntimeException e) {
            success = false;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            try {
                persistUsageLog(request, response, duration, success, errorMsg);
            } catch (Exception ex) {
                log.warn("Failed to persist AI usage log", ex);
            }
        }
    }

    private void persistUsageLog(ChatClientRequest request,
                                  ChatClientResponse response,
                                  long durationMs,
                                  boolean success,
                                  String errorMsg) {
        var ctx = request.context();
        String tenantId = (String) ctx.get(TenantContextAdvisor.CTX_TENANT_ID);
        Long userId = (Long) ctx.get(TenantContextAdvisor.CTX_USER_ID);
        String route = (String) ctx.getOrDefault("ai.route", null);
        String promptName = (String) ctx.getOrDefault("ai.promptName", null);

        AiUsageLog log = new AiUsageLog();
        log.setTenantId(tenantId);
        log.setUserId(userId);
        log.setFeature("contextual");
        log.setRoute(route);
        log.setPromptName(promptName);
        log.setModel(response != null && response.chatResponse() != null
            ? response.chatResponse().getMetadata().getModel()
            : "unknown");

        if (response != null && response.chatResponse() != null) {
            Usage usage = response.chatResponse().getMetadata().getUsage();
            if (usage != null) {
                log.setPromptTokens(usage.getPromptTokens() == null ? 0 : usage.getPromptTokens().intValue());
                log.setCompletionTokens(usage.getCompletionTokens() == null ? 0 : usage.getCompletionTokens().intValue());
            }
        }
        log.setCostCents(0);
        log.setCacheHit(0);
        log.setSuccess(success ? 1 : 0);
        log.setErrorMsg(errorMsg);
        log.setDurationMs(durationMs);
        log.setCreatedAt(new Date());
        usageLogMapper.insert(log);

        Counter.builder("ai.contextual.calls")
            .tags(Tags.of("feature", "contextual",
                          "tenant", tenantId == null ? "unknown" : tenantId,
                          "success", String.valueOf(success)))
            .register(meterRegistry)
            .increment();

        Timer.builder("ai.contextual.duration")
            .tags(Tags.of("feature", "contextual"))
            .register(meterRegistry)
            .record(java.time.Duration.ofMillis(durationMs));
    }
}
