package org.sdkj.thermal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "wx.miniapp")
@Data
public class WxMaProperties {

    private List<Config> configs;

    @Data
    public static class Config {
        private String appid;
        private String secret;
        private String token;
        private String aesKey;
        private String msgDataFormat;
    }
}
