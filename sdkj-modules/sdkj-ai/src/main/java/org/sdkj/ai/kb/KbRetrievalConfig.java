package org.sdkj.ai.kb;

import org.sdkj.ai.config.AiProperties;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KbRetrievalConfig {

    @Bean
    public TokenTextSplitter tokenTextSplitter(AiProperties props) {
        var kb = props.getKb();
        return TokenTextSplitter.builder()
            .withChunkSize(kb.getChunkSizeTokens())
            .withMinChunkSizeChars(kb.getMinChunkChars())
            .withMinChunkLengthToEmbed(20)
            .withMaxNumChunks(10000)
            .withKeepSeparator(true)
            .build();
    }
}
