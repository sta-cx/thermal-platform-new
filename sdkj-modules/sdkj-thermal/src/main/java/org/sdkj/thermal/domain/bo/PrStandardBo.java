package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrStandard;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 收费标准业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrStandard.class, reverseConvertGenerate = false)
public class PrStandardBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 标准名称 */
    private String name;

    /** 所属费项code */
    private String itemCode;

    /** 账户/费项分组 */
    private String itemGroup;

    /** 收费周期 */
    private Integer cycles;

    /** 生成规则 */
    private String generateRule;

    /** 阶梯类型 */
    private String stepType;

    /** 统计方式 */
    private String statisticsType;

    /** 阶梯最大级数 */
    private Integer stepMaxgrade;

    /** 是否启用第二个阶梯 */
    private Integer isStep2;

    /** 阶梯二类型 */
    private String step2Type;

    /** 阶梯二最大级数 */
    private Integer step2Maxgrade;

    /** 是否启用滞纳金 */
    private Integer isLatefee;

    /** 滞纳金开始计算日期 */
    private Date latefeeStartdate;

    /** 滞纳金开始计算类型 */
    private String latefeeType;

    /** 滞纳金开始计算天数 */
    private Integer latefeeStartdays;

    /** 滞纳金公式 */
    private String latefeeFormula;

    /** 是否限购 */
    private Integer isLimited;

    /** 限购方式 */
    private String limitedType;

    /** 限购条件 */
    private String limitedCond;

    /** 限购次数 */
    private Integer limitedTimes;

    /** 限购金额 */
    private BigDecimal limitedMoney;

    /** 单次购买最大金额 */
    private BigDecimal limitedSingleMoney;

    /** 基本单价 */
    private BigDecimal standardPrice;

    /** 金额公式 */
    private String moneyFormula;

    /** 最大金额 */
    private BigDecimal maxMoney;

    /** 小区id */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
