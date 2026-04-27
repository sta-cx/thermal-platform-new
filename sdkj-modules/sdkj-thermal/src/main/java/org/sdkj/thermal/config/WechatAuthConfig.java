package org.sdkj.thermal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信授权配置 - 从 application-thermal.yml 读取 appid/appSecret
 */
@Data
@Component
@ConfigurationProperties(prefix = "thermal.wechat.auth")
public class WechatAuthConfig {

    /**
     * 微信公众号/小程序 AppID
     */
    private String appid;

    /**
     * 微信公众号/小程序 AppSecret
     */
    private String appSecret;
}
