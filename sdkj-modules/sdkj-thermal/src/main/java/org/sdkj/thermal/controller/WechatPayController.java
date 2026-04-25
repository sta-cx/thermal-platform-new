package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/pay")
public class WechatPayController {

    @SaIgnore
    @PostMapping("/createOrder")
    public R<Map<String, Object>> createOrder(@RequestBody Map<String, Object> payParam) {
        // TODO: Phase 5d - 调用微信支付统一下单接口
        return R.fail("微信支付功能待实现");
    }

    @SaIgnore
    @PostMapping("/notify")
    public void handlePayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: Phase 5d - 微信支付回调通知验签+业务处理
        log.warn("微信支付回调未实现签名校验");
        response.setContentType("application/xml;charset=UTF-8");
        response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[未实现]]></return_msg></xml>");
    }

    @SaIgnore
    @GetMapping("/queryOrder")
    public R<Void> queryOrder(@RequestParam String outTradeNo) {
        // TODO: Phase 5d - 查询微信支付订单状态
        return R.fail("查询功能待实现");
    }

    @SaIgnore
    @PostMapping("/applyRefund")
    public R<Void> applyRefund(@RequestBody Map<String, Object> refundParam) {
        // TODO: Phase 5d - 申请微信退款
        return R.fail("退款功能待实现");
    }

    @SaIgnore
    @PostMapping("/refundNotify")
    public void handleRefundNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: Phase 5d - 微信退款回调通知处理
        response.setContentType("application/xml;charset=UTF-8");
        response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[未实现]]></return_msg></xml>");
    }

    @SaIgnore
    @GetMapping("/queryRefund")
    public R<Void> queryRefund(@RequestParam String outRefundNo) {
        // TODO: Phase 5d - 查询退款状态
        return R.fail("退款查询待实现");
    }
}
