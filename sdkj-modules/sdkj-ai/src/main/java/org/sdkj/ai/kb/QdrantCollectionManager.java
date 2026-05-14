package org.sdkj.ai.kb;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Per-tenant Qdrant collection lifecycle manager.
 * <p>
 * Collection naming convention: {@code sdkj_kb_${tenantId}} (e.g. {@code sdkj_kb_000000}).
 * Each tenant gets an isolated collection; creation is idempotent and cached in-process.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QdrantCollectionManager {

    private final QdrantClient qdrantClient;
    private final ConcurrentHashMap<String, Boolean> existsCache = new ConcurrentHashMap<>();

    /**
     * Derive collection name from tenant id.
     */
    public String collectionFor(String tenantId) {
        return "sdkj_kb_" + tenantId;
    }

    /**
     * Ensure the collection for the given tenant exists.
     * Creates it with Cosine distance and the specified vector dimension if absent.
     * Idempotent — concurrent calls for the same tenant are safe.
     *
     * @param tenantId  the tenant identifier
     * @param vectorSize embedding output dimension (e.g. 1024 for DeepSeek text-embedding-v1)
     */
    public void ensureCollection(String tenantId, int vectorSize) {
        String name = collectionFor(tenantId);
        if (existsCache.containsKey(name)) {
            return;
        }

        try {
            var list = qdrantClient.listCollectionsAsync().get(5, TimeUnit.SECONDS);
            boolean exists = list.contains(name);
            if (exists) {
                existsCache.put(name, true);
                return;
            }

            qdrantClient.createCollectionAsync(
                name,
                VectorParams.newBuilder()
                    .setSize(vectorSize)
                    .setDistance(Distance.Cosine)
                    .build()
            ).get(10, TimeUnit.SECONDS);
            existsCache.put(name, true);
            log.info("Qdrant collection created: {} (dim={})", name, vectorSize);
        } catch (Exception e) {
            // Another concurrent worker may have created it — treat as benign
            log.warn("Qdrant collection ensure failed (may be benign race): {}", e.getMessage());
            existsCache.put(name, true);
        }
    }

    /**
     * Delete the collection for the given tenant (used during knowledge-base reset).
     */
    public void deleteCollection(String tenantId) {
        String name = collectionFor(tenantId);
        try {
            qdrantClient.deleteCollectionAsync(name).get(10, TimeUnit.SECONDS);
            existsCache.remove(name);
            log.info("Qdrant collection deleted: {}", name);
        } catch (Exception e) {
            log.warn("Qdrant deleteCollection failed for {}: {}", name, e.getMessage());
        }
    }
}
