package org.sdkj.ai.kb;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class QdrantCollectionManager {

    private static final int DENSE_VECTOR_SIZE = 1024;

    private final QdrantClient qdrantClient;
    private final ConcurrentHashMap<String, Boolean> existsCache = new ConcurrentHashMap<>();

    public String collectionFor(String tenantId) {
        return "sdkj_kb_" + tenantId;
    }

    public void ensureCollection(String tenantId) {
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

            CreateCollection createCollection = CreateCollection.newBuilder()
                .setCollectionName(name)
                .setVectorsConfig(VectorsConfig.newBuilder()
                    .setParamsMap(VectorParamsMap.newBuilder()
                        .putMap("dense", VectorParams.newBuilder()
                            .setSize(DENSE_VECTOR_SIZE)
                            .setDistance(Distance.Cosine)
                            .build())
                        .build())
                    .build())
                .setSparseVectorsConfig(SparseVectorConfig.newBuilder()
                    .putMap("sparse", SparseVectorParams.newBuilder().build())
                    .build())
                .build();

            qdrantClient.createCollectionAsync(createCollection).get(10, TimeUnit.SECONDS);
            existsCache.put(name, true);
            log.info("Qdrant collection created: {} (dense={}, sparse)", name, DENSE_VECTOR_SIZE);
        } catch (Exception e) {
            log.warn("Qdrant collection ensure failed (may be benign race): {}", e.getMessage());
            existsCache.put(name, true);
        }
    }

    /**
     * @deprecated Use {@link #ensureCollection(String)} instead — vector size is now fixed.
     */
    @Deprecated
    public void ensureCollection(String tenantId, int vectorSize) {
        ensureCollection(tenantId);
    }

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

    public void invalidateCache(String tenantId) {
        existsCache.remove(collectionFor(tenantId));
    }
}
