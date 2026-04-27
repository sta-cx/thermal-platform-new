package org.sdkj.thermal.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 微信退款 VO
 */
@Data
public class PrWechatRefundVo {
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
    private LocalDateTime refundTime;
    private String openId;
    private String houseId;
    private String operator;
}
