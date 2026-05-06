package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrExpenseItem;

/**
 * 费用项目业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrExpenseItem.class, reverseConvertGenerate = false)
public class PrExpenseItemBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 项目编号 */
    private String itemCode;

    /** 项目名称 */
    private String itemName;

    /** 账户/费项分组 */
    private String itemGroup;

    /** 在票据是否长显 */
    private Integer isShow;

    /** 打印是否显示月 */
    private Integer isPrintmonth;

    /** 单价精度 */
    private Integer pricePrecision;

    /** 数量精度 */
    private Integer qtyPrecision;

    /** 金额精度 */
    private Integer moneyPrecision;

    /** 是否取整 */
    private Integer isInteger;

    /** 金额小数计算类型 */
    private String precisionType;

    /** 开始位数 */
    private Integer startPos;

    /** 费项合计精度 */
    private Integer sumPrecision;

    /** 起始周期改变 */
    private Integer changeCycle;

    /** 小区id */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
