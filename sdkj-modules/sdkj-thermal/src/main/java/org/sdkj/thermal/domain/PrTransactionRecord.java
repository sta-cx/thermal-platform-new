package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 交易记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_transaction_record")
@AutoMapper(target = PrTransactionRecordVo.class)
public class PrTransactionRecord extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 流水号 */
    private String serialNum;

    /** 交易类型 1-收费 2-退费 3-转移 */
    private Integer transactionType;

    /** 支付方式 1-现金 2-微信 3-支付宝 4-刷卡 */
    private Integer paymentType;

    /** 应收金额 */
    private BigDecimal amount;

    /** 实收金额 */
    private BigDecimal paidAmount;

    /** 状态 0-正常 1-冲销 2-作废 */
    private Integer status;

    /** 房屋ID */
    private Long houseId;

    /** 用户ID */
    private Long userId;

    /** 机构ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 费用分组 */
    private String itemGroup;

    /** 费用编码 */
    private String itemCode;

    /** 交易时间 */
    private Date transactionTime;

    /** 操作员ID */
    private String operatorId;

    /** 备注 */
    private String notes;

    /** 原记录ID（退费/冲销关联） */
    private Long originalRecordId;

    /** 发票号 */
    private String invoiceNo;

    /** 子记录列表（非数据库字段） */
    @TableField(exist = false)
    private List<PrTransactionRecordSub> subList;
}
