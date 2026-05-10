package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrExpense;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 费用明细 View Object
 */
@Data
@AutoMapper(target = PrExpense.class)
public class PrExpenseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long houseId;
    private String itemGroup;
    private String itemCode;
    private String itemName;
    private Long standardId;
    private Date startDate;
    private Date expireDate;
    private Date lastDate;
    private BigDecimal lastReading;
    private BigDecimal thisReading;
    private Integer qty;
    private BigDecimal money;
    private BigDecimal standardPrice;
    private BigDecimal maxPrice;
    private String priceFormula;
    private Integer tradeTimes;
    private BigDecimal maxMoney;
    private String moneyFormula;
    private Integer isFree;
    private String reason;
    private BigDecimal preferential;
    private BigDecimal deduction;
    private BigDecimal latefee;
    private BigDecimal receivable;
    private BigDecimal paidIn;
    private BigDecimal finalMoney;
    private Integer overdueDay;
    private Integer isCharged;
    private Date chargedTime;
    private Long recordId;
    private Date delayDate;
    private Integer heatUsage;
    private String isCalc;
    private Integer isClosed;
    private String year;
    private String month;
    private String orgId;
    private Long parkingSpaceId;
    private String roomNum;
    private String orgName;
    private String buildingName;
    private String userName;
    private String parkingCode;
    private String parkinglotName;
    private Date createTime;
    private Date updateTime;

    // --- 单笔收费专用字段 ---

    /** 费用明细列表（收费请求中的待收费用） */
    private List<PrExpenseVo> lists;

    /** 业主ID */
    private Long userId;

    /** 是否打印 0否 1是 */
    private String isPrint;

    /** 支付方式 1-现金 2-微信 3-支付宝 4-刷卡 */
    private Integer paymentType;

    /** 优惠合计 */
    private BigDecimal discounts;

    /** 个人账户扣除 */
    private BigDecimal personAccount;

    /** 是否补录 0否 1是 */
    private String isCollection;

    /** 补录时间 */
    private Date collectionTime;
}
