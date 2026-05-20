package org.sdkj.ai.assistant;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sdkj.ai.kb.Citation;

import java.util.List;

@Data
@AllArgsConstructor
public class AssistantResponse {
    private Long sessionId;
    private Long assistantMessageId;
    private String content;
    private List<Citation> citations;
}
