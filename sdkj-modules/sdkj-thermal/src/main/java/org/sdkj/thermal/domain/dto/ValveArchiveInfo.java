package org.sdkj.thermal.domain.dto;

/**
 * 阀门配表通用信息，用于从不同类型配表实体中提取公共字段创建执行任务
 */
public record ValveArchiveInfo(
    String meterId,
    String meterArcCode,
    String meterNum,
    String deviceId,
    String concentratorCode,
    String imei,
    String dtuNum,
    String chanNum
) {}
