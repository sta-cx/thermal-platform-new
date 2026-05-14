package org.sdkj.ai.kb;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Points.PointStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Embeds text chunks via Spring AI {@link EmbeddingModel} and upserts vectors
 * into the per-tenant Qdrant collection.
 * <p>
 * Returns a map of {@code chunkIndex -> qdrantPointId (UUID string)} so that
 * callers can persist the point IDs in {@code ai_knowledge_chunk.qdrant_point_id}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final QdrantClient qdrantClient;
    private final QdrantCollectionManager collectionManager;

    /**
     * Batch-embed chunks and upsert to Qdrant.
     *
     * @param tenantId tenant identifier (used to derive collection name)
     * @param docId    knowledge document id (stored as payload for retrieval)
     * @param chunks   text chunks from {@link ChunkingService}
     * @return chunk index → Qdrant point UUID string
     */
    public Map<Integer, String> embedAndStore(String tenantId, long docId, List<ChunkResult> chunks) {
        if (chunks.isEmpty()) {
            return Map.of();
        }

        // 1. Batch embed
        List<String> texts = chunks.stream().map(ChunkResult::getText).toList();
        EmbeddingResponse embedResp = embeddingModel.embedForResponse(texts);
        List<float[]> vectors = embedResp.getResults().stream()
            .map(r -> r.getOutput())
            .toList();
        int vecSize = vectors.get(0).length;

        // 2. Ensure collection exists
        collectionManager.ensureCollection(tenantId, vecSize);
        String collection = collectionManager.collectionFor(tenantId);

        // 3. Build and upsert points
        Map<Integer, String> idMap = new HashMap<>();
        try {
            List<PointStruct> points = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                ChunkResult chunk = chunks.get(i);
                String pointId = UUID.randomUUID().toString();
                idMap.put(chunk.getIndex(), pointId);

                PointStruct point = PointStruct.newBuilder()
                    .setId(PointIdFactory.id(UUID.fromString(pointId)))
                    .setVectors(VectorsFactory.vectors(toFloatList(vectors.get(i))))
                    .putPayload("doc_id", ValueFactory.value(docId))
                    .putPayload("chunk_index", ValueFactory.value(chunk.getIndex()))
                    .build();
                points.add(point);
            }

            qdrantClient.upsertAsync(collection, points).get(30, TimeUnit.SECONDS);
            log.info("Embedded and upserted {} chunks to collection {}", points.size(), collection);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to upsert points to Qdrant collection " + collection, e);
        }

        return idMap;
    }

    /**
     * Delete specific points by their Qdrant point IDs.
     *
     * @param tenantId tenant identifier
     * @param pointIds list of UUID strings (from {@code ai_knowledge_chunk.qdrant_point_id})
     */
    public void deletePoints(String tenantId, List<String> pointIds) {
        if (pointIds.isEmpty()) {
            return;
        }
        String collection = collectionManager.collectionFor(tenantId);
        List<io.qdrant.client.grpc.Points.PointId> ids = pointIds.stream()
            .map(s -> PointIdFactory.id(UUID.fromString(s)))
            .toList();
        try {
            qdrantClient.deleteAsync(collection, ids).get(10, TimeUnit.SECONDS);
            log.info("Deleted {} points from collection {}", ids.size(), collection);
        } catch (Exception e) {
            log.warn("Failed to delete points from collection {}", collection, e);
        }
    }

    /**
     * Convert a float array to a List<Float> for {@link VectorsFactory#vectors(List)}.
     */
    private static List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) {
            list.add(f);
        }
        return list;
    }
}
