package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrExpense;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用明细业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrExpense.class, reverseConvertGenerate = false)
public class PrExpenseBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 房屋ID */
    private Long houseId;

    /** 账户/费项分组 */
    private String itemGroup;

    /** 所属费项code */
    private String itemCode;

    /** 所属费项名称 */
    private String itemName;

    /** 收费标准id */
    private Long standardId;

    /** 起收日期 */
    private Date startDate;

    /** 截止日期 */
    private Date expireDate;

    /** 最迟缴费日期 */
    private Date lastDate;

    /** 上次读数 */
    private BigDecimal lastReading;

    /** 本次读数 */
    private BigDecimal thisReading;

    /** 用量/周期数 */
    private Integer qty;

    /** 金额 */
    private BigDecimal money;

    /** 单价 */
    private BigDecimal standardPrice;

    /** 最大单价 */
    private BigDecimal maxPrice;

    /** 单价计算公式 */
    private String priceFormula;

    /** 购买倍数 */
    private Integer tradeTimes;

    /** 最大金额 */
    private BigDecimal maxMoney;

    /** 金额计算公式 */
    private String moneyFormula;

    /** 是否免收 */
    private Integer isFree;

    /** 原因 */
    private String reason;

    /** 优惠金额 */
    private BigDecimal preferential;

    /** 抵扣金额 */
    private BigDecimal deduction;

    /** 滞纳金 */
    private BigDecimal latefee;

    /** 应收金额 */
    private BigDecimal receivable;

    /** 实收金额 */
    private BigDecimal paidIn;

    /** 费项合并金额 */
    private BigDecimal finalMoney;

    /** 逾期天数 */
    private Integer overdueDay;

    /** 是否已收费 */
    private Integer isCharged;

    /** 收费时间 */
    private Date chargedTime;

    /** 交易记录主表id */
    private Long recordId;

    /** 延期日期 */
    private Date delayDate;

    /** 暖气使用情况 */
    private Integer heatUsage;

    /** 是否计算 */
    private String isCalc;

    /** 是否轧账 */
    private Integer isClosed;

    /** 年份 */
    private String year;

    /** 月份 */
    private String month;

    /** 小区id */
    private String orgId;


    /** 车位ID */
    private Long parkingSpaceId;
}
