package org.sdkj.ai.kb;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tenant-aware RAG retrieval service.
 * <p>
 * Embeds the user query via {@link EmbeddingModel}, searches the per-tenant
 * Qdrant collection for top-k similar chunks, then resolves full text from
 * the MySQL {@code ai_knowledge_chunk} table.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KbRetrievalService {

    private final EmbeddingModel embeddingModel;
    private final QdrantClient qdrantClient;
    private final QdrantCollectionManager collectionManager;
    private final AiKnowledgeChunkMapper chunkMapper;

    @Value("${thermal.ai.kb.retrieval.top-k:4}")
    private int topK;

    @Value("${thermal.ai.kb.retrieval.score-threshold:0.7}")
    private float scoreThreshold;

    /**
     * Retrieve relevant text fragments for the given query in the tenant's collection.
     *
     * @param tenantId tenant identifier (used to derive Qdrant collection name)
     * @param query    user query text
     * @return list of chunk texts ordered by similarity (highest first), empty if no results
     */
    public List<String> retrieve(String tenantId, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        // 1. Embed the query
        float[] vec = embeddingModel.embed(query);

        // 2. Build Qdrant search request
        String collection = collectionManager.collectionFor(tenantId);
        List<Float> vecList = new ArrayList<>(vec.length);
        for (float f : vec) {
            vecList.add(f);
        }

        List<ScoredPoint> results;
        try {
            results = qdrantClient.searchAsync(
                SearchPoints.newBuilder()
                    .setCollectionName(collection)
                    .addAllVector(vecList)
                    .setLimit(topK)
                    .setScoreThreshold(scoreThreshold)
                    .setWithPayload(WithPayloadSelectorFactory.enable(true))
                    .build(),
                Duration.ofSeconds(5)
            ).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Qdrant search failed for collection {}: {}", collection, e.getMessage());
            return List.of();
        }

        if (results.isEmpty()) {
            return List.of();
        }

        // 3. Resolve full text from MySQL via point IDs
        List<String> pointIds = results.stream()
            .map(p -> p.getId().getUuid())
            .toList();

        List<AiKnowledgeChunk> chunks = chunkMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeChunk>()
                .in(AiKnowledgeChunk::getQdrantPointId, pointIds)
        );

        // 4. Preserve Qdrant ordering (by similarity) when mapping back to text
        return pointIds.stream()
            .map(pid -> chunks.stream()
                .filter(c -> pid.equals(c.getQdrantPointId()))
                .findFirst()
                .map(AiKnowledgeChunk::getText)
                .orElse(""))
            .filter(s -> !s.isEmpty())
            .toList();
    }
}
