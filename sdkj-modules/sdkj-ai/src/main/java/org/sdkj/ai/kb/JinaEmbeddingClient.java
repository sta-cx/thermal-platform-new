package org.sdkj.ai.kb;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JinaEmbeddingClient {

    private final WebClient webClient;
    private final AiProperties.Embedding embeddingConfig;

    public JinaEmbeddingClient(AiProperties aiProperties) {
        this.embeddingConfig = aiProperties.getEmbedding();
        this.webClient = WebClient.builder()
            .baseUrl(embeddingConfig.getBaseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + embeddingConfig.getApiKey())
            .build();
    }

    public List<float[]> embed(List<String> inputs, String task) {
        Map<String, Object> body = Map.of(
            "model", embeddingConfig.getModel(),
            "input", inputs,
            "task", task,
            "late_chunking", false
        );
        EmbedResponse resp = webClient.post()
            .uri("/v1/embeddings")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(EmbedResponse.class)
            .timeout(Duration.ofSeconds(30))
            .block();
        if (resp == null || resp.data() == null) {
            throw new RuntimeException("Jina embedding API returned null response");
        }
        return resp.data().stream()
            .sorted((a, b) -> Integer.compare(a.index(), b.index()))
            .map(EmbedItem::embedding)
            .toList();
    }

    public float[] embedSingle(String input, String task) {
        List<float[]> results = embed(List.of(input), task);
        if (results.isEmpty()) {
            throw new RuntimeException("Jina embedding returned empty result");
        }
        return results.get(0);
    }

    record EmbedResponse(List<EmbedItem> data) {}
    record EmbedItem(int index, float[] embedding) {}
}
