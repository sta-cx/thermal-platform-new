package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.PrHouseExpenseVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋费用项目绑定表
 * 迁移自旧系统 PrHouseExpense
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_house_expense")
@AutoMapper(target = PrHouseExpenseVo.class)
public class PrHouseExpense extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
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

    // ========== 非数据库字段 ==========

    /** 收费标准（嵌套） */
    @TableField(exist = false)
    private PrStandard prStandard;

    /** 房屋（嵌套） */
    @TableField(exist = false)
    private PrHouse house;

    /** 单价类型 */
    @TableField(exist = false)
    private String type;

    /** 生成月数 */
    @TableField(exist = false)
    private Integer qty;

    /** 到期日期 */
    @TableField(exist = false)
    private Date formula;

    /** 起收日期 */
    @TableField(exist = false)
    private Date openTime;

    /** 止收日期 */
    @TableField(exist = false)
    private Date closeTime;

    /** 提前天数 */
    @TableField(exist = false)
    private Integer openEarlyDays;

    /** 延后天数 */
    @TableField(exist = false)
    private Integer closeLaterDays;

    /** 费项名称 */
    @TableField(exist = false)
    private String itemName;

    /** 标准名称 */
    @TableField(exist = false)
    private String name;

    /** 金额 */
    @TableField(exist = false)
    private BigDecimal money;

    /** 单价公式 */
    @TableField(exist = false)
    private String priceFormula;

    /** 起收周期改变 */
    @TableField(exist = false)
    private Integer changeCycle;

    /** 标准单价 */
    @TableField(exist = false)
    private BigDecimal standardPrice;

    /** 车位ID */
    @TableField(exist = false)
    private String parkingSpaceId;
}
