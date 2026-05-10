package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 审批明细
 * 迁移自旧系统 PrApprovalSub
 *
 * @author sdkj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_approval_sub")
public class PrApprovalSub extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 审批单ID */
    private Long approvalId;

    /** 费用明细ID */
    private Long expenseId;

    /** 小区名称 */
    private String orgName;

    /** 楼栋名称 */
    private String buildingName;

    /** 房间号 */
    private String roomNum;

    /** 费用项目名称 */
    private String itemName;

    /** 费用项目编码 */
    private String itemCode;

    /** 标准ID */
    private Long standardId;

    /** 单价 */
    private BigDecimal standardPrice;

    /** 开始日期 */
    private Date startDate;

    /** 到期日期 */
    private Date expireDate;

    /** 最晚缴费日期 */
    private Date lastDate;

    /** 数量 */
    private Integer qty;

    /** 减免金额 */
    private BigDecimal preferential;

    /** 扣除金额 */
    private BigDecimal deduction;

    /** 滞纳金 */
    private BigDecimal latefee;

    /** 应收金额 */
    private BigDecimal receivable;

    /** 最终金额 */
    private BigDecimal finalMoney;

    /** 费用创建时间 */
    private Date expenseCreateTime;

    /** 仓库名称 */
    private String warehouseName;

    /** 仓库ID */
    private String warehouseId;

    /** 物料名称 */
    private String materialName;

    /** 物料ID */
    private String materialId;

    /** 物料使用人 */
    private String materialUser;

    /** 物料用途 */
    private String materialUse;


    /** 小区ID */
    private String orgId;
}
