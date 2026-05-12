package org.sdkj.ai.cache;

import lombok.RequiredArgsConstructor;
import org.sdkj.ai.core.ContextualView;
import org.sdkj.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AiViewCache {

    public ContextualView get(String key) {
        return RedisUtils.getCacheObject(key);
    }

    public void put(String key, ContextualView view, Duration ttl) {
        RedisUtils.setCacheObject(key, view, ttl);
    }

    public void evict(String key) {
        RedisUtils.deleteObject(key);
    }
}
