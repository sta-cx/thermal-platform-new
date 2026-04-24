package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHouseExpense;

/**
 * 房屋费用项目绑定业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHouseExpense.class, reverseConvertGenerate = false)
public class PrHouseExpenseBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 房屋ID */
    private String houseId;

    /** 账户/费项分组 */
    private String itemGroup;

    /** 所属费项code */
    private String itemCode;

    /** 收费标准id */
    private String standardId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
