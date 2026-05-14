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
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用量统计 + Micrometer 指标 advisor。
 * <p>
 * Phase 2A 审查 B-C1:同时实现 {@link StreamAdvisor},让 SSE 流式响应也累积 token 与时延 → 写 ai_usage_log。
 * 否则 Alt+K 流式响应永远不计费、不进监控。
 */
@Slf4j
public class UsageMetricsAdvisor implements CallAdvisor, StreamAdvisor {

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

    @Override
    @NonNull
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request,
                                                  @NonNull StreamAdvisorChain chain) {
        long start = System.currentTimeMillis();
        AtomicReference<ChatClientResponse> lastSeen = new AtomicReference<>();
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        return chain.nextStream(request)
            .doOnNext(lastSeen::set)
            .doOnError(errorRef::set)
            .doFinally(signal -> {
                long duration = System.currentTimeMillis() - start;
                Throwable err = errorRef.get();
                boolean success = err == null;
                String errMsg = err == null ? null : err.getMessage();
                try {
                    persistUsageLog(request, lastSeen.get(), duration, success, errMsg);
                } catch (Exception ex) {
                    log.warn("Failed to persist AI usage log (stream)", ex);
                }
            });
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
        String feature = (String) ctx.getOrDefault("ai.feature", "contextual");

        AiUsageLog logRow = new AiUsageLog();
        logRow.setTenantId(tenantId);
        logRow.setUserId(userId);
        logRow.setFeature(feature);
        logRow.setRoute(route);
        logRow.setPromptName(promptName);
        logRow.setModel(response != null && response.chatResponse() != null
            ? response.chatResponse().getMetadata().getModel()
            : "unknown");

        if (response != null && response.chatResponse() != null) {
            Usage usage = response.chatResponse().getMetadata().getUsage();
            if (usage != null) {
                logRow.setPromptTokens(usage.getPromptTokens() == null ? 0 : usage.getPromptTokens().intValue());
                logRow.setCompletionTokens(usage.getCompletionTokens() == null ? 0 : usage.getCompletionTokens().intValue());
            }
        }
        logRow.setCostCents(0);
        logRow.setCacheHit(0);
        logRow.setSuccess(success ? 1 : 0);
        logRow.setErrorMsg(errorMsg);
        logRow.setDurationMs(durationMs);
        logRow.setCreatedAt(new Date());
        usageLogMapper.insert(logRow);

        Counter.builder("ai.calls")
            .tags(Tags.of("feature", feature,
                          "tenant", tenantId == null ? "unknown" : tenantId,
                          "success", String.valueOf(success)))
            .register(meterRegistry)
            .increment();

        Timer.builder("ai.duration")
            .tags(Tags.of("feature", feature))
            .register(meterRegistry)
            .record(java.time.Duration.ofMillis(durationMs));
    }
}
