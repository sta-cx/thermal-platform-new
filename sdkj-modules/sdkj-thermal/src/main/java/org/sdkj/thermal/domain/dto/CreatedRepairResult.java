package org.sdkj.thermal.domain.dto;

/**
 * AI Tool 创建报修工单的返回结果
 */
public record CreatedRepairResult(
    Long repairId,
    Long houseId,
    String summary,
    String status
) {}
