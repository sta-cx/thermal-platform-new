package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrStandardPrice;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 收费标准单价 View Object
 * 迁移自旧系统 PrStandardPrice
 */
@Data
@AutoMapper(target = PrStandardPrice.class)
public class PrStandardPriceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String standardId;
    private Integer step12;
    private Integer grade;
    private Integer minimum;
    private Integer maximum;
    private BigDecimal standardPrice;
    private String priceFormula;
    private BigDecimal other1Price;
    private BigDecimal other2Price;
    private BigDecimal other3Price;
    private BigDecimal maxPrice;
    private Date startTime;
    private Date endTime;
    private String lineMode;
    private BigDecimal lineAmount;
    private String moneyFormula;
    private String orgId;
    private String companyId;
    private Date createTime;
}
