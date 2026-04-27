package org.sdkj.thermal.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 微信支付订单 VO
 */
@Data
public class PrWechatOrderVo {
    private String id;
    private String outTradeNo;
    private String transactionId;
    private String openId;
    private String otherCode;
    private String houseId;
    private String houseAddress;
    private BigDecimal totalFee;
    private String body;
    private Integer orderStatus;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime expireTime;
    private String tradeType;
    private String bankType;
    private String attach;
    private String companyId;
    private String operator;
    private String remark;
}
