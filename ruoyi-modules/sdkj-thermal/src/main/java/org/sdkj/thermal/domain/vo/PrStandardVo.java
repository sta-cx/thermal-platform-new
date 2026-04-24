package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.PrStandardPrice;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 收费标准 View Object
 * 迁移自旧系统 PrStandard
 */
@Data
@AutoMapper(target = PrStandard.class)
public class PrStandardVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String itemCode;
    private String itemGroup;
    private Integer cycles;
    private String generateRule;
    private String stepType;
    private String statisticsType;
    private Integer stepMaxgrade;
    private Integer isStep2;
    private String step2Type;
    private Integer step2Maxgrade;
    private Integer isLatefee;
    private Date latefeeStartdate;
    private String latefeeType;
    private Integer latefeeStartdays;
    private String latefeeFormula;
    private Integer isLimited;
    private String limitedType;
    private String limitedCond;
    private Integer limitedTimes;
    private BigDecimal limitedMoney;
    private BigDecimal limitedSingleMoney;
    private BigDecimal standardPrice;
    private String moneyFormula;
    private BigDecimal maxMoney;
    private String orgId;
    private String companyId;
    private String priceFormula;
    private String itemName;
    private Date createTime;
    private Date updateTime;

    /** 收费标准单价列表 */
    private List<PrStandardPrice> prStandardPrice;
}
