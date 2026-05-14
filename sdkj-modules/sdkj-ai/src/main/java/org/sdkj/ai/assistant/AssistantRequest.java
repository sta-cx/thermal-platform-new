package org.sdkj.ai.assistant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssistantRequest {
    private Long sessionId;
    @NotBlank
    private String message;
}
