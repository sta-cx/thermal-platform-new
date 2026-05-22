package org.sdkj.ai.kb;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@DS("master")
@RequiredArgsConstructor
public class JinaRerankerClient {

    private final WebClient.Builder webClientBuilder;
    private final AiProperties aiProperties;
    private final AiKnowledgeChunkMapper chunkMapper;

    public record ScoredPointWithContext(io.qdrant.client.grpc.Points.ScoredPoint point,
                                         AiKnowledgeChunk chunk, double score) {}

    public List<ScoredPointWithContext> resolveAndRerank(String query,
            List<io.qdrant.client.grpc.Points.ScoredPoint> candidates, int topN) {
        // 1. Resolve chunk texts from MySQL
        List<String> pointIds = candidates.stream()
            .map(p -> p.getId().getUuid())
            .toList();

        Map<String, AiKnowledgeChunk> chunkMap = chunkMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeChunk>()
                .in(AiKnowledgeChunk::getQdrantPointId, pointIds)
        ).stream()
            .collect(Collectors.toMap(AiKnowledgeChunk::getQdrantPointId, c -> c, (a, b) -> a));

        List<ScoredPointWithContext> withChunks = candidates.stream()
            .map(p -> new ScoredPointWithContext(p, chunkMap.get(p.getId().getUuid()), p.getScore()))
            .toList();

        // 2. Rerank if enabled
        var cfg = aiProperties.getRag().getRerank();
        if (!cfg.isEnabled() || withChunks.isEmpty()) {
            return withChunks.size() <= topN ? withChunks : new ArrayList<>(withChunks.subList(0, topN));
        }

        List<String> texts = withChunks.stream()
            .map(c -> c.chunk() != null && c.chunk().getText() != null ? c.chunk().getText() : "")
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
                return fallback(withChunks, topN);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) resp.get("results");
            if (results == null) {
                return fallback(withChunks, topN);
            }

            return results.stream()
                .map(r -> {
                    int idx = ((Number) r.get("index")).intValue();
                    double score = ((Number) r.get("relevance_score")).doubleValue();
                    return new AbstractMap.SimpleEntry<>(idx, score);
                })
                .filter(e -> e.getValue() >= cfg.getScoreThreshold())
                .map(e -> withChunks.get(e.getKey()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Reranker failed, fallback to RRF top-{}", topN, e);
            if (!cfg.isFallbackOnError()) throw new RuntimeException(e);
            return fallback(withChunks, topN);
        }
    }

    private List<ScoredPointWithContext> fallback(List<ScoredPointWithContext> candidates, int topN) {
        return candidates.size() <= topN ? candidates : new ArrayList<>(candidates.subList(0, topN));
    }
}
