package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrHeatMonth;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 热表月记录 View Object
 */
@Data
@AutoMapper(target = PrHeatMonth.class)
public class PrHeatMonthVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    /** 表号 */
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 配表ID */
    private String meterId;

    /** 热表档案编号 */
    private String meterArcCode;

    /** 上次抄表时间 */
    private Date startTime;

    /** 本次抄表时间 */
    private Date readTime;

    /** 上次读数 */
    private BigDecimal startReading;

    /** 本次读数 */
    private BigDecimal currentReading;

    /** 当月用量 */
    private BigDecimal qty;

    /** 结算月份/年份(如202008) */
    private String recordYm;

    /** 统计方式 */
    private String statisticsType;

    /** 收费标准ID */
    private String standardId;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 是否审批 */
    private Integer isAudit;

    /** 是否参与历史累计 */
    private Integer isHiscalc;

    /** 当前余额 */
    private BigDecimal currentBalance;

    /** 当月充值金额 */
    private BigDecimal rechargeMoney;

    /** 缴至读数 */
    private BigDecimal payDegrees;

    /** 本月欠费 */
    private BigDecimal currentArrearage;

    /** 累计欠费 */
    private BigDecimal addArrearage;

    /** 累计预收 */
    private BigDecimal addAdvances;

    /** 房屋ID */
    private String houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
