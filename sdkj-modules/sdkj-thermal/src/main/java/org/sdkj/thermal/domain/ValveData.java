package org.sdkj.thermal.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 阀门数据传输对象
 * 用于批量更新阀门档案和插入回读数据
 */
@Data
public class ValveData {

    /** 主键ID（Java 层预生成） */
    private Long id;

    /** GUID */
    private String guid;

    /** 仪表参数/仪表档案名称 */
    private String meterInfo;

    /** 阀门号/表号 */
    private String meterNum;

    /** 仪表档案编码 */
    private String meterArcName;

    /** 阀门状态 1 开 2 关 3 异常 */
    private String valveStatus;

    /** 设定开度 */
    private String settingStatus;

    /** 实际开度 */
    private String actualStatus;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 回水温度 */
    private BigDecimal outTemp;

    /** 电压 */
    private BigDecimal voltage;

    /** 阀门时间 */
    private String valveTime;

    /** 上报间隔 (NB系列) */
    private Integer reportInterval;

    /** 上报间隔单位(NB系列): 1分钟 2小时 3天 */
    private Integer reportingUnit;

    /** 有效时长(NB系列): 天 */
    private Integer validTime;

    /** 上报总次数(NB系列) */
    private Integer totalDegree;

    /** 信号强度 */
    private Integer csq;

    /** 唤醒次数 */
    private Integer rouseNum;

    /** 唤醒时长 秒 */
    private Integer duration;

    /** DTU状态 1在线 2掉线 */
    private Integer dtuStatus;

    /** 瞬时流量 */
    private BigDecimal insFlow;
}
