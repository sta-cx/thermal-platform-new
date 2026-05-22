package org.sdkj.ai.kb;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.grpc.Points.NamedVectors;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.SparseIndices;
import io.qdrant.client.grpc.Points.Vector;
import io.qdrant.client.grpc.Points.Vectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final QdrantClient qdrantClient;
    private final QdrantCollectionManager collectionManager;
    private final JinaEmbeddingClient jinaClient;
    private final BM25SparseEncoder sparseEncoder;
    private final AiProperties aiProperties;

    public Map<Integer, String> embedAndStore(String tenantId, long docId, List<ChunkResult> chunks) {
        if (chunks.isEmpty()) {
            return Map.of();
        }

        String passageTask = aiProperties.getEmbedding().getTask().getPassage();
        int batchSize = aiProperties.getRag().getUpload().getJinaEmbedBatchSize();

        // 1. Batch embed via Jina with task=passage
        List<float[]> allVectors = new ArrayList<>(chunks.size());
        List<String> allTexts = chunks.stream().map(ChunkResult::getText).toList();
        for (int offset = 0; offset < allTexts.size(); offset += batchSize) {
            int end = Math.min(offset + batchSize, allTexts.size());
            List<String> batchTexts = allTexts.subList(offset, end);
            List<float[]> batchVectors = jinaClient.embed(batchTexts, passageTask);
            allVectors.addAll(batchVectors);
            log.info("Embedded batch {}/{} ({} texts) for doc {}",
                (offset / batchSize) + 1,
                (allTexts.size() + batchSize - 1) / batchSize,
                batchTexts.size(), docId);
        }

        // 2. Ensure collection exists (dual-vector schema)
        collectionManager.ensureCollection(tenantId);
        String collection = collectionManager.collectionFor(tenantId);

        // 3. Build and upsert points with dense + sparse vectors
        Map<Integer, String> idMap = new HashMap<>();
        try {
            List<PointStruct> points = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                ChunkResult chunk = chunks.get(i);
                String pointId = UUID.randomUUID().toString();
                idMap.put(chunk.getIndex(), pointId);

                // Dense vector
                NamedVectors.Builder nvBuilder = NamedVectors.newBuilder()
                    .putVectors("dense", Vector.newBuilder()
                        .addAllData(toFloatList(allVectors.get(i)))
                        .build());

                // Sparse vector (BM25)
                BM25SparseEncoder.SparseVec sv = sparseEncoder.encode(chunk.getText());
                if (!sv.indices().isEmpty()) {
                    nvBuilder.putVectors("sparse", Vector.newBuilder()
                        .setIndices(SparseIndices.newBuilder()
                            .addAllData(sv.indices()).build())
                        .addAllData(sv.values())
                        .build());
                }

                PointStruct point = PointStruct.newBuilder()
                    .setId(PointIdFactory.id(UUID.fromString(pointId)))
                    .setVectors(Vectors.newBuilder().setVectors(nvBuilder.build()).build())
                    .putPayload("doc_id", ValueFactory.value(docId))
                    .putPayload("chunk_index", ValueFactory.value(chunk.getIndex()))
                    .build();
                points.add(point);
            }

            qdrantClient.upsertAsync(collection, points).get(30, TimeUnit.SECONDS);
            log.info("Embedded and upserted {} dual-vector chunks to collection {}",
                points.size(), collection);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to upsert points to Qdrant collection " + collection, e);
        }

        return idMap;
    }

    public float[] embedQuery(String query) {
        String queryTask = aiProperties.getEmbedding().getTask().getQuery();
        return jinaClient.embedSingle(query, queryTask);
    }

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

    private static List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) {
            list.add(f);
        }
        return list;
    }
}
