package org.sdkj.ai.tools.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdkj.ai.AiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Redis 主实现 — 使用 Redisson 提供 RBucket + RLock。
 *
 * Key 约定:ai:ptc:{callId} → JSON,TTL = 1800s。
 * 状态迁移加锁:ai:ptc:lock:{callId},避免并发 confirm/reject 竞争。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisConfirmationStore implements ConfirmationStore {

    private static final String KEY_PREFIX = "ai:ptc:";
    private static final String LOCK_PREFIX = "ai:ptc:lock:";
    private static final long TTL_SECONDS = AiConstants.PENDING_TTL_SECONDS;

    private final RedissonClient redisson;
    private final ObjectMapper objectMapper;
    private final MysqlConfirmationStoreSidecar sidecar;

    @Override
    public void save(PendingToolCall call) {
        RBucket<String> bucket = redisson.getBucket(KEY_PREFIX + call.getCallId());
        try {
            bucket.set(objectMapper.writeValueAsString(call), Duration.ofSeconds(TTL_SECONDS));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("serialize pending tool call failed", e);
        }
        sidecar.upsert(call);
    }

    @Override
    public Optional<PendingToolCall> findByCallId(String callId) {
        RBucket<String> bucket = redisson.getBucket(KEY_PREFIX + callId);
        String json = bucket.get();
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, PendingToolCall.class));
        } catch (JsonProcessingException e) {
            log.error("[Redis ConfirmationStore] deserialize failed for {}: {}", callId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<PendingToolCall> transition(String callId,
                                                 PendingToolCallStatus expected,
                                                 PendingToolCallStatus to,
                                                 Consumer<PendingToolCall> mutator) {
        RLock lock = redisson.getLock(LOCK_PREFIX + callId);
        boolean locked = false;
        try {
            locked = lock.tryLock(AiConstants.LOCK_WAIT_SECONDS, AiConstants.LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("[Redis ConfirmationStore] transition lock timeout for {}", callId);
                return Optional.empty();
            }
            Optional<PendingToolCall> current = findByCallId(callId);
            if (current.isEmpty()) return Optional.empty();

            PendingToolCall ptc = current.get();
            if (ptc.getStatus() != expected) {
                log.info("[Redis ConfirmationStore] transition rejected: {} expected={} actual={}",
                    callId, expected, ptc.getStatus());
                return Optional.empty();
            }
            ptc.setStatus(to);
            mutator.accept(ptc);
            save(ptc);
            return Optional.of(ptc);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    @Override
    public void delete(String callId) {
        redisson.getBucket(KEY_PREFIX + callId).delete();
    }
}
