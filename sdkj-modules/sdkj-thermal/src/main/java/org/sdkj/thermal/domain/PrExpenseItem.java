package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;

/**
 * 费用项目定义
 * 迁移自旧系统 PrExpenseItem
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_expense_item")
@AutoMapper(target = PrExpenseItemVo.class)
public class PrExpenseItem extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

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

    /** 收费标准数量（非数据库字段） */
    private Integer num;
}
