package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrExpenseItem;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 费用项目 View Object
 * 迁移自旧系统 PrExpenseItem
 */
@Data
@AutoMapper(target = PrExpenseItem.class)
public class PrExpenseItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String itemCode;
    private String itemName;
    private String itemGroup;
    private Integer isShow;
    private Integer isPrintmonth;
    private Integer pricePrecision;
    private Integer qtyPrecision;
    private Integer moneyPrecision;
    private Integer isInteger;
    private String precisionType;
    private Integer startPos;
    private Integer sumPrecision;
    private Integer changeCycle;
    private String orgId;
    private String companyId;
    private Integer num;
    private Date createTime;
    private Date updateTime;
}
