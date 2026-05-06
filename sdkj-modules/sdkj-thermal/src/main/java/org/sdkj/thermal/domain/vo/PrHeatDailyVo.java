package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrHeatDaily;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 热表日记录 View Object
 */
@Data
@AutoMapper(target = PrHeatDaily.class)
public class PrHeatDailyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 配表ID */
    private Long meterId;

    /** 表号 */
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 热表档案编号 */
    private String meterArcCode;

    /** 上次抄表时间 */
    private Date startTime;

    /** 上次读数 */
    private BigDecimal startReading;

    /** 本次抄表时间 */
    private Date readTime;

    /** 当前读数 */
    private BigDecimal currentReading;

    /** 日用量 */
    private BigDecimal qty;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 回水温度 */
    private BigDecimal outTemperature;

    /** 供回水温差 */
    private BigDecimal diffTemperature;

    /** 阀门设定状态 */
    private String settingStatus;

    /** 阀门当前状态 */
    private String valveStatus;

    /** 电压 */
    private String voltage;

    /** 基本单价 */
    private BigDecimal standardPrice;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 日表所属日期 */
    private Date dailyDate;

    /** 是否计算用量 */
    private Integer isCalc;

    /** 费用结算时间 */
    private Date calcDate;

    /** 房屋ID */
    private Long houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
