package org.sdkj.ai.kb;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@DS("master")
@RequiredArgsConstructor
public class JinaRerankerClient {

    private final WebClient.Builder webClientBuilder;
    private final AiProperties aiProperties;
    private final AiKnowledgeChunkMapper chunkMapper;

    public record RerankResult(int index, double relevanceScore) {}

    public List<ScoredPointWithContext> rerank(String query, List<ScoredPointWithContext> candidates, int topN) {
        var cfg = aiProperties.getRag().getRerank();
        if (!cfg.isEnabled() || candidates.isEmpty()) {
            return candidates.size() <= topN ? candidates : candidates.subList(0, topN);
        }

        List<String> texts = candidates.stream()
            .map(c -> c.chunkText() != null ? c.chunkText() : "")
            .toList();

        Map<String, Object> body = Map.of(
            "model", cfg.getModel(),
            "query", query,
            "documents", texts,
            "top_n", topN,
            "return_documents", false
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = webClientBuilder.baseUrl(cfg.getBaseUrl()).build()
                .post().uri("/v1/rerank")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getApiKey())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(cfg.getTimeoutMs()))
                .block();

            if (resp == null) {
                log.warn("Reranker returned null, fallback to RRF top-{}", topN);
                return fallback(candidates, topN, cfg);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) resp.get("results");
            if (results == null) {
                return fallback(candidates, topN, cfg);
            }

            return results.stream()
                .map(r -> {
                    int idx = ((Number) r.get("index")).intValue();
                    double score = ((Number) r.get("relevance_score")).doubleValue();
                    return new AbstractMap.SimpleEntry<>(idx, score);
                })
                .filter(e -> e.getValue() >= cfg.getScoreThreshold())
                .map(e -> candidates.get(e.getKey()))
                .toList();
        } catch (Exception e) {
            log.warn("Reranker failed, fallback to RRF top-{}", topN, e);
            if (!cfg.isFallbackOnError()) throw new RuntimeException(e);
            return fallback(candidates, topN, cfg);
        }
    }

    private List<ScoredPointWithContext> fallback(List<ScoredPointWithContext> candidates, int topN, AiProperties.Rag.Rerank cfg) {
        return candidates.size() <= topN ? candidates : candidates.subList(0, topN);
    }

    /**
     * Wraps a ScoredPoint with its chunk text resolved from MySQL.
     */
    public record ScoredPointWithContext(io.qdrant.client.grpc.Points.ScoredPoint point,
                                         String chunkText, double score) {}

    /**
     * Resolve chunk texts from MySQL for reranking, returning wrapped results.
     */
    public List<ScoredPointWithContext> resolveChunkTexts(List<io.qdrant.client.grpc.Points.ScoredPoint> candidates) {
        List<String> pointIds = candidates.stream()
            .map(p -> p.getId().getUuid())
            .toList();

        Map<String, AiKnowledgeChunk> chunkMap = chunkMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeChunk>()
                .in(AiKnowledgeChunk::getQdrantPointId, pointIds)
        ).stream()
            .collect(java.util.stream.Collectors.toMap(AiKnowledgeChunk::getQdrantPointId, c -> c, (a, b) -> a));

        return candidates.stream()
            .map(p -> {
                AiKnowledgeChunk chunk = chunkMap.get(p.getId().getUuid());
                String text = chunk != null ? chunk.getText() : null;
                return new ScoredPointWithContext(p, text, p.getScore());
            })
            .toList();
    }
}
