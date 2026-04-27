package org.sdkj.thermal.wechat.wxPay;

import org.sdkj.thermal.config.WechatPayConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * WXPay SDK 配置实现 - 将 Spring 配置 WechatPayConfig 桥接到 WXPay SDK 抽象类。
 * 用于 JSAPI 统一下单（不需要商户证书）。
 */
public class WXPayConfigImpl extends WXPayConfig {

    private final String appid;
    private final String mchId;
    private final String key;

    public WXPayConfigImpl(WechatPayConfig config) {
        this.appid = config.getAppid();
        this.mchId = config.getMchId();
        this.key = config.getKey();
    }

    @Override
    String getAppID() {
        return appid;
    }

    @Override
    String getMchID() {
        return mchId;
    }

    @Override
    String getKey() {
        return key;
    }

    @Override
    InputStream getCertStream() {
        // 统一下单不需要商户证书，返回空流
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
                // 域名健康上报 - 不做处理
            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 6 * 1000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 8 * 1000;
    }
}
