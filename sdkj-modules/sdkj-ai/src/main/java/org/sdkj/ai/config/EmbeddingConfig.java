package org.sdkj.ai.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 使用 Jina AI 创建 EmbeddingModel bean。
 * <p>
 * Spring AI 自动配置的 OpenAiEmbeddingModel 复用 chat 的 base-url（DeepSeek），
 * 但 DeepSeek 不提供 embedding API。这里手动指向 Jina AI 端点。
 */
@Configuration
public class EmbeddingConfig {

    @Bean
    @Primary
    public OpenAiEmbeddingModel jinaEmbeddingModel(
        @Value("${thermal.ai.embedding.base-url:https://api.jina.ai}") String baseUrl,
        @Value("${thermal.ai.embedding.api-key:}") String apiKey,
        @Value("${thermal.ai.embedding.model:jina-embeddings-v3}") String model
    ) {
        OpenAiApi api = OpenAiApi.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .build();
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
            .model(model)
            .build();
        return new OpenAiEmbeddingModel(api, MetadataMode.EMBED, options,
            RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }
}
