package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * NB阀门数据解析结果
 */
@Data
public class NbValvePayload {
    /** 阀门编号 */
    private String meterNum;
    /** 阀门状态(十六进制) */
    private String valveStatus;
    /** 设定开度 */
    private Integer settingStatus;
    /** 实际开度 */
    private Integer actualOpen;
    /** 进水温度 */
    private BigDecimal inTemperature;
    /** 回水温度 */
    private BigDecimal outTemperature;
    /** 电压 */
    private BigDecimal voltage;
    /** 阀门时间(格式化字符串) */
    private String valveTime;
    /** 上报间隔 */
    private Integer reportInterval;
    /** 间隔单位 */
    private Integer intervalUnit;
    /** 有效时长 */
    private Integer validTime;
    /** 总上报次数 */
    private Integer totalDegree;
    /** 信号强度 */
    private Integer csq;
}
