package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.sdkj.thermal.domain.PrWechatOrder;
import org.sdkj.thermal.domain.PrWechatRefund;
import org.sdkj.thermal.domain.vo.PrWechatOrderVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付 Service
 */
public interface IPrWechatPayService extends IService<PrWechatOrder> {

    /**
     * 创建微信支付订单
     */
    Map<String, Object> createOrder(String openId, String otherCode, String houseAddress,
                                    BigDecimal totalFee, String body, String attach, String operator);

    /**
     * 处理微信支付回调通知
     */
    String handlePayNotify(HttpServletRequest request);

    /**
     * 查询订单状态
     */
    PrWechatOrder getOrderByOutTradeNo(String outTradeNo);

    /**
     * 申请退款
     */
    Map<String, Object> applyRefund(String outTradeNo, BigDecimal refundFee, String refundReason, String operator);

    /**
     * 处理微信退款回调通知
     */
    String handleRefundNotify(HttpServletRequest request);

    /**
     * 查询退款状态
     */
    PrWechatRefund getRefundByOutRefundNo(String outRefundNo);
}
