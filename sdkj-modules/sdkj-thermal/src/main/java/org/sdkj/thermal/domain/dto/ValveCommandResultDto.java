package org.sdkj.thermal.domain.dto;

/**
 * AI Tool 阀门指令下发的返回结果
 */
public record ValveCommandResultDto(
    Long houseId,
    String action,
    Integer openness,
    boolean dispatched,
    String summary,
    String message
) {}
