package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrStandardPriceVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 收费标准单价
 * 迁移自旧系统 PrStandardPrice
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_standard_price")
@AutoMapper(target = PrStandardPriceVo.class)
public class PrStandardPrice extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属收费标准id */
    private String standardId;

    /** 所属阶梯 1/2 */
    private Integer step12;

    /** 级别 */
    private Integer grade;

    /** 最小值 */
    private Integer minimum;

    /** 最大值 */
    private Integer maximum;

    /** 基本单价 */
    private BigDecimal standardPrice;

    /** 单价公式 */
    private String priceFormula;

    /** 其它单价1 */
    private BigDecimal other1Price;

    /** 其它单价2 */
    private BigDecimal other2Price;

    /** 其它单价3 */
    private BigDecimal other3Price;

    /** 最高(大) */
    private BigDecimal maxPrice;

    /** 开始时间（采暖费） */
    private Date startTime;

    /** 结束时间（采暖费） */
    private Date endTime;

    /** 线损方式 */
    private String lineMode;

    /** 线损量 */
    private BigDecimal lineAmount;

    /** 金额公式 */
    private String moneyFormula;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
