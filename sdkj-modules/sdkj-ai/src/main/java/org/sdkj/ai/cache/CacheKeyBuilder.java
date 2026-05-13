package org.sdkj.ai.cache;

import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

@Component
public class CacheKeyBuilder {

    private static final String PREFIX = "ai:contextual:";

    public String build(String tenantId, ContextualRequest req, ContextualPrompt prompt) {
        // Map.toString() 顺序依赖具体实现(HashMap 不稳定),
        // 前端 JS Object 在不同分支下可能 key 顺序略不同,
        // 直接拼会让相同语义的 filters/query 产生不同 cacheKey,缓存命中率下降。
        // 用 TreeMap 强制按 key 字典序排序,保证 toString 稳定。
        String filtersStable = sortedToString(req.getFilters());
        String queryStable = sortedToString(req.getQuery());

        String raw = tenantId + "|" + prompt.getClass().getSimpleName() + "|" + req.getRoute()
            + "|" + filtersStable + "|" + req.getSelectedEntityIds()
            + "|" + queryStable;
        return PREFIX + tenantId + ":" + prompt.getClass().getSimpleName() + ":" + sha1(raw);
    }

    public String buildGenericKey(String tenantId, String route) {
        String raw = tenantId + "|GenericPrompt|" + route;
        return PREFIX + tenantId + ":GenericPrompt:" + sha1(raw);
    }

    private static String sortedToString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        return new TreeMap<>(map).toString();
    }

    private String sha1(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return Integer.toHexString(s.hashCode());
        }
    }
}
