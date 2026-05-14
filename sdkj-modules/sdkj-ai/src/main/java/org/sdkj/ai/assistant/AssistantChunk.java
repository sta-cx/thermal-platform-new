package org.sdkj.ai.assistant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssistantChunk {
    private String delta;
    private Long sessionId;
    private Long messageId;
    private Boolean finish;
    private String error;
}
