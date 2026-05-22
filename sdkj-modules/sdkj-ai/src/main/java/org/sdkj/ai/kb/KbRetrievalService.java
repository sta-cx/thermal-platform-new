package org.sdkj.ai.kb;

import com.baomidou.dynamic.datasource.annotation.DS;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.VectorInputFactory;
import io.qdrant.client.grpc.Points.Fusion;
import io.qdrant.client.grpc.Points.PrefetchQuery;
import io.qdrant.client.grpc.Points.Query;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@DS("master")
@RequiredArgsConstructor
public class KbRetrievalService {

    private final EmbeddingService embeddingService;
    private final BM25SparseEncoder sparseEncoder;
    private final QdrantClient qdrantClient;
    private final QdrantCollectionManager collectionManager;
    private final AiKnowledgeDocMapper docMapper;
    private final AiProperties aiProperties;
    private final JinaRerankerClient rerankerClient;

    private static final int SNIPPET_MAX_LENGTH = 120;

    public RetrievalResult retrieveWithCitations(String tenantId, String query) {
        if (query == null || query.isBlank()) {
            return new RetrievalResult(List.of(), List.of());
        }
        if (!aiProperties.getRag().isEnabled()) {
            return new RetrievalResult(List.of(), List.of());
        }

        var hybridCfg = aiProperties.getRag().getHybrid();
        String collection = collectionManager.collectionFor(tenantId);

        // 1. Dual-path embed
        float[] denseVec;
        try {
            denseVec = embeddingService.embedQuery(query);
        } catch (Exception e) {
            log.warn("Dense embed failed, fallback to sparse-only", e);
            denseVec = null;
        }
        BM25SparseEncoder.SparseVec sparseVec = sparseEncoder.encode(query);

        // 2. Qdrant hybrid query (dense+sparse prefetch + RRF fusion)
        List<ScoredPoint> candidates;
        try {
            candidates = qdrantHybridQuery(collection, denseVec, sparseVec, hybridCfg);
        } catch (Exception e) {
            log.error("Qdrant hybrid query failed, return empty RAG", e);
            return new RetrievalResult(List.of(), List.of());
        }
        if (candidates.isEmpty()) {
            return new RetrievalResult(List.of(), List.of());
        }

        log.info("[KB-Retrieval] collection={}, query='{}', candidates={}",
            collection,
            query.length() > 40 ? query.substring(0, 40) + "..." : query,
            candidates.size());

        // 3. Resolve chunks from MySQL + rerank via Jina (single DB query, not two)
        int topK = aiProperties.getKb().getRetrieval().getTopK();
        List<JinaRerankerClient.ScoredPointWithContext> reranked =
            rerankerClient.resolveAndRerank(query, candidates, topK);

        // 4. Build citations using already-resolved chunks
        return buildCitations(reranked);
    }

    private RetrievalResult buildCitations(List<JinaRerankerClient.ScoredPointWithContext> results) {
        List<AiKnowledgeChunk> chunks = results.stream()
            .map(JinaRerankerClient.ScoredPointWithContext::chunk)
            .filter(Objects::nonNull)
            .toList();

        Map<Long, String> docTitleMap = resolveDocTitles(chunks);

        List<String> fragments = new ArrayList<>();
        List<Citation> citations = new ArrayList<>();

        for (JinaRerankerClient.ScoredPointWithContext ctx : results) {
            AiKnowledgeChunk chunk = ctx.chunk();
            if (chunk == null || chunk.getText() == null || chunk.getText().isBlank()) {
                continue;
            }
            fragments.add(chunk.getText());
            String title = docTitleMap.getOrDefault(chunk.getDocId(), "未知文档");
            String snippet = truncate(chunk.getText(), SNIPPET_MAX_LENGTH);
            citations.add(Citation.builder()
                .docTitle(title)
                .snippet(snippet)
                .score(ctx.score())
                .build());
        }

        return new RetrievalResult(fragments, citations);
    }

    private List<ScoredPoint> qdrantHybridQuery(String collection, float[] dense,
            BM25SparseEncoder.SparseVec sparse, AiProperties.Rag.Hybrid cfg) throws Exception {
        QueryPoints.Builder qb = QueryPoints.newBuilder()
            .setCollectionName(collection)
            .setLimit(cfg.getFusionLimit())
            .setWithPayload(WithPayloadSelectorFactory.enable(true));

        if (dense != null) {
            qb.addPrefetch(PrefetchQuery.newBuilder()
                .setQuery(Query.newBuilder()
                    .setNearest(VectorInputFactory.vectorInput(dense))
                    .build())
                .setUsing("dense")
                .setLimit(cfg.getDensePrefetchLimit())
                .build());
        }
        if (!sparse.indices().isEmpty()) {
            qb.addPrefetch(PrefetchQuery.newBuilder()
                .setQuery(Query.newBuilder()
                    .setNearest(VectorInputFactory.vectorInput(sparse.values(), sparse.indices()))
                    .build())
                .setUsing("sparse")
                .setLimit(cfg.getSparsePrefetchLimit())
                .build());
        }
        qb.setQuery(Query.newBuilder().setFusion(Fusion.RRF).build());

        return qdrantClient.queryAsync(qb.build(), Duration.ofSeconds(5))
            .get(5, TimeUnit.SECONDS);
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
