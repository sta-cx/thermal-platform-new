package org.sdkj.ai.context;

import java.time.Instant;
import java.util.Map;

/** 情景记忆：一条 Tool 结果对象抽出的全部标量字段。 */
public record EntityFact(
    String factId,
    String sourceTool,            // 产生该 fact 的 Tool 方法名（仅备查）
    Map<String, Object> keys,     // 全部标量字段：{houseId:123, valveId:456, valveStatus:"2", …}
    Instant createdAt,
    Instant expireAt
) {}
