package org.sdkj.thermal.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 运行监控查询参数
 */
@Data
public class MonitorBo {

    // ========== 基础筛选 ==========

    /** 小区ID */
    private String orgId;

    /** 楼宇ID */
    private String buildingId;

    /** 单元编码 → pr_house.unit_code */
    private String unit;

    /** 搜索关键字（表号/档案名称） */
    private String search;

    /** 父级ID（单元级设备查询） */
    private String parentId;

    /** 换热站ID */
    private String stationId;

    /** 分区ID */
    private String partitionId;

    // ========== 房屋侧筛选（经 resolveFilteredHouseIds 解析为 house_id IN） ==========

    /** 缴费状态 → pr_house.is_charged (0未缴/1已缴/2停供/3空置) */
    private String isCharged;

    /** 分组(缴费位置属性) → pr_house.pay_sit_type (1孤岛/2上不供/3下不供/4正常)。注意 station_type 存的是分区id，不是分组 */
    private String houseType;

    /** 位置属性 → pr_house.site_type */
    private String siteType;

    /** 特殊户 → pr_house.is_special (0/1) */
    private String specialType;

    /** 楼层 → pr_house.floor */
    private String floor;

    // ========== 档案侧筛选（直接进 LambdaQueryWrapper） ==========

    /** 阀门状态 → archive.valve_status */
    private String valveStatus;

    /** 通道号 → archive.chan_num */
    private String chanNum;

    /** 电压比较符: "1"=≥, "2"=≤ */
    private String voltageOp;

    /** 电压阈值 */
    private BigDecimal voltageValue;

    // ========== runtime 模式 ==========

    /** 当前执行任务ID（§6.2 follow-up，暂不入查询） */
    private String tasksId;
}
