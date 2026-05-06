package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 热力调控请求 DTO
 * 迁移自旧系统 PrHeatVo
 * 用于 manualControl、xunce 等方法的请求参数
 */
@Data
public class PrHeatVo implements Serializable {

    /** 阀门档案信息 */
    private PrHeatValveArchiveDto prHeatValveArchive;

    /** 命令阀门档案信息 */
    private PrHeatCommandValveArchiveDto prHeatCommandValveArchive;

    /** 热表档案信息 */
    private PrHeatHotArchiveDto prHeatHotArchive;

    /** DTU 档案信息 */
    private PrHeatDtuArchiveDto prHeatDtuArchive;

    /** ID */
    private Long id;

    /** 表号 */
    private String meterNum;
}
