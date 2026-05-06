package org.sdkj.thermal.domain.vo;

import org.sdkj.common.sensitive.annotation.Sensitive;
import org.sdkj.common.sensitive.core.SensitiveStrategy;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrHouseExpense;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房屋费用项目绑定 View Object
 */
@Data
@AutoMapper(target = PrHouseExpense.class)
public class PrHouseExpenseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long houseId;
    private String itemGroup;
    private String itemCode;
    private Long standardId;
    private String orgId;
    private String companyId;
    private String itemName;
    private BigDecimal standardPrice;
    private String moneyFormula;
    private String stepType;
    private String name;
    private String roomNum;
    private String userName;
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;
    private String userId;
    private BigDecimal money;
}
