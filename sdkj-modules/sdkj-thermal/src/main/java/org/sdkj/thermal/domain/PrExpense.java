package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrExpenseVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用明细档案
 * 迁移自旧系统 PrExpense
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_expense")
@AutoMapper(target = PrExpenseVo.class)
public class PrExpense extends BaseEntity {

    @TableId(value = "id")
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

    /** 公司ID */
    private String companyId;

    /** 车位ID */
    private Long parkingSpaceId;

    /** 修改标准用-新标准ID */
    @TableField(exist = false)
    private Long standardIdNew;

    /** 修改标准用-新单价 */
    @TableField(exist = false)
    private BigDecimal standardPriceNew;

    /** 修改标准用-新最大金额 */
    @TableField(exist = false)
    private BigDecimal maxMoneyNew;

    /** 修改标准用-新金额公式 */
    @TableField(exist = false)
    private String moneyFormulaNew;

    /** 合计月数（查询用） */
    @TableField(exist = false)
    private Integer sums;

    /** 数量（查询用） */
    @TableField(exist = false)
    private Integer nums;

    /** 房间号（查询用） */
    @TableField(exist = false)
    private String roomNum;

    /** 小区名称（查询用） */
    @TableField(exist = false)
    private String orgName;

    /** 楼宇名称（查询用） */
    @TableField(exist = false)
    private String buildingName;

    /** 用户名（查询用） */
    @TableField(exist = false)
    private String userName;

    /** 车位编号（查询用） */
    @TableField(exist = false)
    private String parkingCode;

    /** 停车场名称（查询用） */
    @TableField(exist = false)
    private String parkinglotName;
}
