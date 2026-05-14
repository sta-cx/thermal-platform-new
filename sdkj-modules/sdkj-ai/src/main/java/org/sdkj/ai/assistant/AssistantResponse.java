package org.sdkj.ai.assistant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssistantResponse {
    private Long sessionId;
    private Long assistantMessageId;
    private String content;
}
