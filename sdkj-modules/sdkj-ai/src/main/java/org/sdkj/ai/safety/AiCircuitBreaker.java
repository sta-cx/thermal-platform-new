package org.sdkj.ai.safety;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.exception.AiUnavailableException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiCircuitBreaker {

    private final AiProperties properties;
    private final ConcurrentMap<String, State> states = new ConcurrentHashMap<>();

    private static class State {
        AtomicInteger failures = new AtomicInteger(0);
        volatile long windowStart = System.currentTimeMillis();
        volatile long openUntil = 0L;
    }

    public void checkAllowed(String feature, String tenantId) {
        String key = feature + ":" + tenantId;
        State s = states.computeIfAbsent(key, k -> new State());
        long now = System.currentTimeMillis();
        if (s.openUntil > now) {
            throw new AiUnavailableException(
                "AI 服务暂不可用(熔断中,稍后重试)"
            );
        }
    }

    public void recordSuccess(String feature, String tenantId) {
        String key = feature + ":" + tenantId;
        State s = states.get(key);
        if (s != null) {
            s.failures.set(0);
            s.windowStart = System.currentTimeMillis();
        }
    }

    public void recordFailure(String feature, String tenantId) {
        String key = feature + ":" + tenantId;
        State s = states.computeIfAbsent(key, k -> new State());
        long now = System.currentTimeMillis();
        long windowMs = properties.getCircuitBreaker().getWindowSeconds() * 1000L;

        if (now - s.windowStart > windowMs) {
            s.failures.set(0);
            s.windowStart = now;
        }

        int n = s.failures.incrementAndGet();
        if (n >= properties.getCircuitBreaker().getFailureThreshold()) {
            s.openUntil = now + properties.getCircuitBreaker().getOpenDurationSeconds() * 1000L;
            log.warn("CircuitBreaker OPEN: feature={}, tenantId={}, failures={}",
                feature, tenantId, n);
        }
    }
}
