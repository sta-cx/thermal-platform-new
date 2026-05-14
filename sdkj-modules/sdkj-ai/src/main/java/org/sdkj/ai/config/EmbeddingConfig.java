package org.sdkj.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Embedding 模型配置。
 * <p>
 * Spring AI auto-configures {@code OpenAiEmbeddingModel} when the starter is present
 * and {@code spring.ai.openai.embedding.options.model} is set.
 * This class provides a {@code @Primary} alias so that Phase 4 can swap implementations
 * without changing injection sites.
 */
@Configuration
public class EmbeddingConfig {

    @Bean
    @Primary
    @ConditionalOnBean(EmbeddingModel.class)
    public EmbeddingModel primaryEmbeddingModel(EmbeddingModel openAiEmbeddingModel) {
        return openAiEmbeddingModel;
    }
}
