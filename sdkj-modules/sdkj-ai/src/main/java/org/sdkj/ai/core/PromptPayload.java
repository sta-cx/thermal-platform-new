package org.sdkj.ai.core;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PromptPayload {

    private String systemPrompt;

    private String userPromptTemplate;

    private Map<String, Object> templateVars;

    private String cacheKey;
}
