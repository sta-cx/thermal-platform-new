package org.sdkj.ai.assistant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class AssistantRequest {
    private Long sessionId;
    @NotBlank
    private String message;
    /** Phase3：前端页面上下文(route/params/selectedEntityType/selectedEntityIds…)，可空，旧端兼容。 */
    private Map<String, Object> pageContext;
}
