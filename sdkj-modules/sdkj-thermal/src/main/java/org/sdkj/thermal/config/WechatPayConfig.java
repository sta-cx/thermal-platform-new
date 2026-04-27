package org.sdkj.thermal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置 - 从 application-thermal.yml 读取支付相关参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "thermal.wechat.pay")
public class WechatPayConfig {

    /**
     * 微信支付 AppID
     */
    private String appid;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户支付密钥
     */
    private String key;

    /**
     * 支付结果通知回调 URL
     */
    private String notifyUrl;

    /**
     * 退款结果通知回调 URL
     */
    private String refundNotifyUrl;
}
