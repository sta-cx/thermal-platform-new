package org.sdkj.ai.context;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.sdkj.ai.AiConstants;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.mapper.AiChatSessionMapper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextStoreImpl implements ContextStore {

    private static final String KEY_PREFIX = "ai:ctx:";
    private static final Duration TTL = Duration.ofHours(2);

    private final RedissonClient redisson;
    private final ObjectMapper objectMapper;
    private final AiChatSessionMapper sessionMapper;
    private final ContextDbSidecar dbSidecar;
    private final AiProperties aiProperties;

    @Override
    public ConversationContext load(Long sessionId) {
        ConversationContext ctx = readRedis(sessionId);
        if (ctx == null) ctx = readDb(sessionId);
        if (ctx == null) ctx = new ConversationContext(sessionId);
        AiProperties.Context cfg = aiProperties.getContext();
        ctx.applyForgetting(cfg.getMaxFacts(), cfg.getFocusIdleMinutes(), Instant.now());
        return ctx;
    }

    @Override
    public void save(ConversationContext ctx) {
        if (ctx == null || ctx.getSessionId() == null) return;
        ctx.setUpdatedAt(Instant.now());
        try {
            String json = objectMapper.writeValueAsString(ctx);
            RBucket<String> bucket = redisson.getBucket(KEY_PREFIX + ctx.getSessionId());
            bucket.set(json, TTL);
            dbSidecar.persist(ctx.getSessionId(), json);
        } catch (Exception e) {
            log.error("[ContextStore] save failed for session {}: {}", ctx.getSessionId(), e.getMessage());
        }
    }

    private ConversationContext readRedis(Long sessionId) {
        try {
            RBucket<String> bucket = redisson.getBucket(KEY_PREFIX + sessionId);
            String json = bucket.get();
            return json == null ? null : objectMapper.readValue(json, ConversationContext.class);
        } catch (Exception e) {
            log.warn("[ContextStore] redis read failed for {}: {}", sessionId, e.getMessage());
            return null;
        }
    }

    private ConversationContext readDb(Long sessionId) {
        DynamicDataSourceContextHolder.push(AiConstants.DS_MASTER);
        try {
            var session = sessionMapper.selectById(sessionId);
            if (session == null || session.getContextData() == null) return null;
            return objectMapper.readValue(session.getContextData(), ConversationContext.class);
        } catch (Exception e) {
            log.warn("[ContextStore] db read failed for {}: {}", sessionId, e.getMessage());
            return null;
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}
