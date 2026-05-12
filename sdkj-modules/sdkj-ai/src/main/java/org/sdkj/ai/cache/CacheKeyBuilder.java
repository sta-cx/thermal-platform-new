package org.sdkj.ai.cache;

import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class CacheKeyBuilder {

    private static final String PREFIX = "ai:contextual:";

    public String build(String tenantId, ContextualRequest req, ContextualPrompt prompt) {
        String raw = tenantId + "|" + prompt.getClass().getSimpleName() + "|" + req.getRoute()
            + "|" + req.getFilters() + "|" + req.getSelectedEntityIds()
            + "|" + req.getQuery();
        return PREFIX + tenantId + ":" + prompt.getClass().getSimpleName() + ":" + sha1(raw);
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
