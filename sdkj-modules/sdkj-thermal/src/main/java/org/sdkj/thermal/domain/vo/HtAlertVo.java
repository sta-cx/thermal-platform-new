package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtAlert;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 报警记录视图对象
 * 迁移自旧系统 HtAlert
 */
@Data
@AutoMapper(target = HtAlert.class)
public class HtAlertVo {

    private Long id;

    /** 楼栋ID */
    private Long buildingId;

    /** 单元ID */
    private Long unitId;

    /** 户ID */
    private Long houseId;

    /** 仪表ID */
    private Long meterId;

    /** 是否收费 */
    private Integer isCharged;

    /** 阀门状态 */
    private Integer valve;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 回水温度 */
    private BigDecimal outTemp;

    /** 室温 */
    private BigDecimal roomTemp;

    /** 报警类型 */
    private Integer alertType;

    /** 报警时间 */
    private Date alertTime;

    /** 报警状态 */
    private String alertStatus;

    /** 组织ID */
    private String orgId;


    /** 维修中标记：关联的 pr_repair_record.id，NULL 表示未在维修 */
    private String inMaintenance;

    /** 创建人名称 */
    private String createName;

}
