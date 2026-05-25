package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHouseExpense;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋费用项目绑定业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHouseExpense.class, reverseConvertGenerate = false)
public class PrHouseExpenseBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 房屋ID */
    private Long houseId;

    /** 账户/费项分组 */
    private String itemGroup;

    /** 所属费项code */
    private String itemCode;

    /** 收费标准id */
    private Long standardId;

    /** 小区ID */
    private String orgId;

    // ========== 服务必需的瞬态字段 ==========

    /** 起收日期 */
    private Date openTime;

    /** 止收日期 */
    private Date closeTime;

    /** 金额 */
    private BigDecimal money;

    /** 费项名称 */
    private String itemName;

}
