package org.sdkj.ai.context;

import java.time.Instant;
import java.util.Map;

/** 工作记忆：当前焦点实体（A 指代复用）。attrs 含该实体的全部已知字段。 */
public record FocusEntity(
    String entityType,            // house / station / valve …
    Long entityId,
    String label,                 // 展示用，如 "house#123"
    Map<String, Object> attrs,    // 该实体的标量字段：{houseId, valveId, valveStatus, roomTemp, …}
    Instant updatedAt
) {}
