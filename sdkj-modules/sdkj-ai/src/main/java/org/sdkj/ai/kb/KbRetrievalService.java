package org.sdkj.ai.kb;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Tenant-aware RAG retrieval service.
 * <p>
 * Embeds the user query via {@link EmbeddingModel}, searches the per-tenant
 * Qdrant collection for top-k similar chunks, then resolves full text from
 * the MySQL {@code ai_knowledge_chunk} table.
 */
@Slf4j
@Service
@DS("master")
@RequiredArgsConstructor
public class KbRetrievalService {

    private final EmbeddingModel embeddingModel;
    private final QdrantClient qdrantClient;
    private final QdrantCollectionManager collectionManager;
    private final AiKnowledgeChunkMapper chunkMapper;
    private final AiKnowledgeDocMapper docMapper;

    @Value("${thermal.ai.kb.retrieval.top-k:4}")
    private int topK;

    @Value("${thermal.ai.kb.retrieval.score-threshold:0.7}")
    private float scoreThreshold;

    private static final int SNIPPET_MAX_LENGTH = 120;

    /**
     * Retrieve fragments and citation metadata.
     * Returns empty result (not null) when tenantId/query missing or retrieval fails.
     */
    public RetrievalResult retrieveWithCitations(String tenantId, String query) {
        if (query == null || query.isBlank()) {
            return new RetrievalResult(List.of(), List.of());
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
            return new RetrievalResult(List.of(), List.of());
        }

        if (results.isEmpty()) {
            return new RetrievalResult(List.of(), List.of());
        }

        // 3. Resolve chunks from MySQL via point IDs
        List<String> pointIds = results.stream()
            .map(p -> p.getId().getUuid())
            .toList();

        List<AiKnowledgeChunk> chunks = chunkMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeChunk>()
                .in(AiKnowledgeChunk::getQdrantPointId, pointIds)
        );

        // 4. Resolve doc titles
        Map<Long, String> docTitleMap = resolveDocTitles(chunks);

        // 5. Build pointId → chunk lookup for O(1) matching
        Map<String, AiKnowledgeChunk> chunkByPointId = chunks.stream()
            .collect(Collectors.toMap(AiKnowledgeChunk::getQdrantPointId, c -> c, (a, b) -> a));

        // 6. Preserve Qdrant ordering (by similarity)
        List<String> fragments = new ArrayList<>();
        List<Citation> citations = new ArrayList<>();

        for (ScoredPoint sp : results) {
            AiKnowledgeChunk chunk = chunkByPointId.get(sp.getId().getUuid());
            if (chunk == null || chunk.getText() == null || chunk.getText().isBlank()) {
                continue;
            }
            fragments.add(chunk.getText());
            String title = docTitleMap.getOrDefault(chunk.getDocId(), "未知文档");
            String snippet = truncate(chunk.getText(), SNIPPET_MAX_LENGTH);
            citations.add(Citation.builder()
                .docTitle(title)
                .snippet(snippet)
                .score(sp.getScore())
                .build());
        }

        return new RetrievalResult(fragments, citations);
    }

    private Map<Long, String> resolveDocTitles(List<AiKnowledgeChunk> chunks) {
        List<Long> docIds = chunks.stream()
            .map(AiKnowledgeChunk::getDocId)
            .distinct()
            .toList();
        if (docIds.isEmpty()) return Map.of();
        return docMapper.selectBatchIds(docIds).stream()
            .collect(Collectors.toMap(AiKnowledgeDoc::getId, d -> d.getTitle() != null ? d.getTitle() : "未命名"));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
