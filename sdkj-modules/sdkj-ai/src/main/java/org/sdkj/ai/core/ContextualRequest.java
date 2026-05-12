package org.sdkj.ai.core;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class ContextualRequest {

    @NotBlank
    private String route;

    private String routeName;

    private Map<String, Object> params = Collections.emptyMap();

    private Map<String, Object> query = Collections.emptyMap();

    private String selectedEntityType;

    private List<Long> selectedEntityIds = Collections.emptyList();

    private Map<String, Object> filters = Collections.emptyMap();

    private Integer visibleCount;

    private Map<String, Object> extraContext = Collections.emptyMap();
}
