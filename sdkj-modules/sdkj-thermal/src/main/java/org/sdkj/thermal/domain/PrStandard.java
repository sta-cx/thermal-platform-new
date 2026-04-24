package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrStandardVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 收费标准定义
 * 迁移自旧系统 PrStandard
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_standard")
@AutoMapper(target = PrStandardVo.class)
public class PrStandard extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 标准名称 */
    private String name;

    /** 所属费项code（pr_expense_item表的code） */
    private String itemCode;

    /** 账户/费项分组 */
    private String itemGroup;

    /** 收费周期 */
    private Integer cycles;

    /** 生成规则：月对月、自然月、自定义 */
    private String generateRule;

    /** 阶梯类型：面积、楼层、购量/用量 */
    private String stepType;

    /** 统计方式：1=按月 2=按季度 3=按年 4=不统计 */
    private String statisticsType;

    /** 阶梯最大级数（为1表示无阶梯） */
    private Integer stepMaxgrade;

    /** 是否启用第二个阶梯（水费的楼层阶梯） */
    private Integer isStep2;

    /** 阶梯二类型 */
    private String step2Type;

    /** 阶梯二最大级数 */
    private Integer step2Maxgrade;

    /** 是否启用滞纳金 */
    private Integer isLatefee;

    /** 滞纳金开始计算日期 */
    private Date latefeeStartdate;

    /** 滞纳金开始计算类型：起收日期、到期日期、缴费截止日期 */
    private String latefeeType;

    /** 滞纳金开始计算天数 */
    private Integer latefeeStartdays;

    /** 滞纳金公式 */
    private String latefeeFormula;

    /** 是否限购 */
    private Integer isLimited;

    /** 限购方式：按月、按季度、按年 */
    private String limitedType;

    /** 限购条件：判断欠费、不判断欠费 */
    private String limitedCond;

    /** 限购次数 */
    private Integer limitedTimes;

    /** 限购金额 */
    private BigDecimal limitedMoney;

    /** 单次购买最大金额 */
    private BigDecimal limitedSingleMoney;

    /** 基本单价（后台保存，存储第一级阶梯值） */
    private BigDecimal standardPrice;

    /** 金额公式 */
    private String moneyFormula;

    /** 最大金额（值为0时不起作用） */
    private BigDecimal maxMoney;

    /** 小区id */
    private String orgId;

    /** 公司ID */
    private String companyId;

    // ========== 非数据库字段 ==========

    /** 单价公式（查询用） */
    @TableField(exist = false)
    private String priceFormula;

    /** 项目名称（查询用） */
    @TableField(exist = false)
    private String itemName;

    /** 收费标准单价列表 */
    @TableField(exist = false)
    private List<PrStandardPrice> prStandardPrice;
}
