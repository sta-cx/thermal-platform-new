package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 微信对账差异记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_reconciliation_diff")
public class PrReconciliationDiff extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 关联账单ID */
    private String billId;

    /** 账单日期 */
    private String billDate;

    /** 商户订单号 */
    private String outTradeNo;

    /** 微信支付流水号 */
    private String transactionId;

    /** 差异类型：MISS-漏单，AMOUNT-金额不一致，STATUS-状态不一致 */
    private String diffType;

    /** 本地金额 */
    private String localAmount;

    /** 微信金额 */
    private String wechatAmount;

    /** 本地状态 */
    private String localStatus;

    /** 微信状态 */
    private String wechatStatus;

    /** 处理状态：0-未处理，1-已处理 */
    private String handleStatus;

    /** 处理备注 */
    private String handleRemark;

    /** 处理人 */
    private String handler;

    /** 处理时间 */
    private Date handleTime;

    /** 所属公司 */
    private String companyId;
}
