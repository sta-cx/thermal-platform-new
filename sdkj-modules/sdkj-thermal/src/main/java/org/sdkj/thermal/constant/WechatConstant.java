package org.sdkj.thermal.constant;

/**
 * 微信接口常量
 */
public class WechatConstant {

    // 微信支付返回状态
    public static final String WXPAY_SUCCESS = "SUCCESS";
    public static final String WXPAY_FAIL = "FAIL";

    // 微信支付交易类型
    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_APP = "APP";

    // 微信支付交易状态
    public static final String TRADE_STATUS_SUCCESS = "SUCCESS";
    public static final String TRADE_STATUS_REFUND = "REFUND";
    public static final String TRADE_STATUS_NOTPAY = "NOTPAY";
    public static final String TRADE_STATUS_CLOSED = "CLOSED";
    public static final String TRADE_STATUS_REVOKED = "REVOKED";
    public static final String TRADE_STATUS_USERPAYING = "USERPAYING";
    public static final String TRADE_STATUS_PAYERROR = "PAYERROR";

    // 微信退款状态
    public static final String REFUND_STATUS_SUCCESS = "SUCCESS";
    public static final String REFUND_STATUS_REFUNDCLOSE = "REFUNDCLOSE";
    public static final String REFUND_STATUS_PROCESSING = "PROCESSING";
    public static final String REFUND_STATUS_CHANGE = "CHANGE";

    // ======================== 微信授权相关常量 ========================

    /**
     * 微信网页授权获取OpenID的URL模板
     */
    public static final String OPEN_URL_TEMPLATE =
            "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    /**
     * 通过code获取access_token和openid的接口URL
     */
    public static final String WECHAT_ACCESS_TOKEN_URL =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 授权作用域：仅获取openid（静默授权）
     */
    public static final String SCOPE_BASE = "snsapi_base";

    /**
     * 授权作用域：获取用户信息（需用户手动确认）
     */
    public static final String SCOPE_USER_INFO = "snsapi_userinfo";

    /**
     * 微信支付统一下单接口URL
     */
    public static final String WXPAY_UNIFIED_ORDER_URL =
            "https://api.mch.weixin.qq.com/pay/unifiedorder";
}
