package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_wechat_refund")
public class PrWechatRefund extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String outTradeNo;
    private String transactionId;
    private String outRefundNo;
    private String refundId;
    private BigDecimal totalFee;
    private BigDecimal refundFee;
    private String refundReason;
    private Integer refundStatus;
    private String refundChannel;
    private Date refundTime;
    private String openId;
    private String houseId;
    private String operator;
    private String remark;
}
