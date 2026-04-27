package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.thermal.domain.PrWechatOrder;
import org.sdkj.thermal.domain.PrWechatRefund;
import org.sdkj.thermal.service.IPrWechatPayService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付控制器
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/pay")
public class WechatPayController {

    private final IPrWechatPayService wechatPayService;

    @SaCheckLogin
    @Log(title = "微信支付", businessType = BusinessType.INSERT)
    @PostMapping("/createOrder")
    public R<Map<String, Object>> createOrder(@RequestBody Map<String, Object> params) {
        log.info("创建微信支付订单开始: {}", params);

        String openId = (String) params.get("openId");
        String otherCode = (String) params.get("otherCode");
        String houseAddress = (String) params.get("houseAddress");
        String body = (String) params.get("body");
        String attach = (String) params.get("attach");
        String operator = (String) params.get("operator");

        Object amountObj = params.get("totalFee");
        if (amountObj == null) {
            return R.fail("金额不能为空");
        }
        BigDecimal totalFee;
        try {
            totalFee = new BigDecimal(amountObj.toString());
        } catch (NumberFormatException e) {
            return R.fail("金额格式无效");
        }
        if (totalFee.compareTo(BigDecimal.ZERO) <= 0) {
            return R.fail("金额必须大于0");
        }

        try {
            Map<String, Object> result = wechatPayService.createOrder(
                    openId, otherCode, houseAddress, totalFee, body, attach, operator);
            log.info("创建微信支付订单成功: {}", result.get("outTradeNo"));
            return R.ok(result);
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            return R.fail(e.getMessage());
        }
    }

    @SaIgnore
    @PostMapping("/notify")
    public void handlePayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("微信支付回调处理开始");
        String result = wechatPayService.handlePayNotify(request);
        log.info("微信支付回调处理结束: {}", result);
        response.setContentType("application/xml;charset=UTF-8");
        response.getWriter().write(result);
    }

    @SaCheckLogin
    @GetMapping("/queryOrder")
    public R<PrWechatOrder> queryOrder(@RequestParam String outTradeNo) {
        log.info("查询订单状态: outTradeNo={}", outTradeNo);
        if (outTradeNo == null || outTradeNo.isEmpty()) {
            return R.fail("订单号不能为空");
        }
        PrWechatOrder order = wechatPayService.getOrderByOutTradeNo(outTradeNo);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.ok(order);
    }

    @SaCheckLogin
    @Log(title = "微信退款", businessType = BusinessType.INSERT)
    @PostMapping("/applyRefund")
    public R<Map<String, Object>> applyRefund(@RequestBody Map<String, Object> params) {
        log.info("申请微信退款开始: {}", params);

        String outTradeNo = (String) params.get("outTradeNo");
        String refundReason = (String) params.get("refundReason");
        String operator = (String) params.get("operator");

        Object refundFeeObj = params.get("refundFee");
        if (outTradeNo == null || outTradeNo.isEmpty()) {
            return R.fail("原订单号不能为空");
        }
        if (refundFeeObj == null) {
            return R.fail("退款金额不能为空");
        }

        BigDecimal refundFee;
        try {
            refundFee = new BigDecimal(refundFeeObj.toString());
        } catch (NumberFormatException e) {
            return R.fail("退款金额格式无效");
        }
        if (refundFee.compareTo(BigDecimal.ZERO) <= 0) {
            return R.fail("退款金额必须大于0");
        }

        try {
            Map<String, Object> result = wechatPayService.applyRefund(
                    outTradeNo, refundFee, refundReason, operator);
            log.info("申请微信退款成功: outRefundNo={}", result.get("outRefundNo"));
            return R.ok(result);
        } catch (Exception e) {
            log.error("申请微信退款失败", e);
            return R.fail(e.getMessage());
        }
    }

    @SaIgnore
    @PostMapping("/refundNotify")
    public void handleRefundNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("微信退款回调处理开始");
        String result = wechatPayService.handleRefundNotify(request);
        log.info("微信退款回调处理结束: {}", result);
        response.setContentType("application/xml;charset=UTF-8");
        response.getWriter().write(result);
    }

    @SaCheckLogin
    @GetMapping("/queryRefund")
    public R<PrWechatRefund> queryRefund(@RequestParam String outRefundNo) {
        log.info("查询退款状态: outRefundNo={}", outRefundNo);
        if (outRefundNo == null || outRefundNo.isEmpty()) {
            return R.fail("退款单号不能为空");
        }
        PrWechatRefund refund = wechatPayService.getRefundByOutRefundNo(outRefundNo);
        if (refund == null) {
            return R.fail("退款记录不存在");
        }
        return R.ok(refund);
    }
}
