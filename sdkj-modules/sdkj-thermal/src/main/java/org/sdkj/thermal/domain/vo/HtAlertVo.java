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

    /** 报警内容（旧系统 alert_status 列实际存的是报警描述文字，非状态码） */
    private String alertStatus;

    /** 组织ID */
    private String orgId;


    /** 维修中标记：非空表示已转报修（已处理），NULL 表示未处理 */
    private String inMaintenance;

    // ========== enrich 回填字段（非数据库列，selectPageList 中 Stream 组装，无 JOIN） ==========

    /** 小区名称（org_id → sys_organization.name） */
    private String orgName;

    /** 楼栋名称（house_id → pr_house.building_name） */
    private String buildingName;

    /** 房号（house_id → pr_house.room_num） */
    private String roomNum;

    /** 表号（meter_id → pr_heat_valve_archive.meter_num） */
    private String meterNum;

}
