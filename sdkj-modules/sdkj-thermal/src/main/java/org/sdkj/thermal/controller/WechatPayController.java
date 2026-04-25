package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/pay")
public class WechatPayController {

    /** Phase 6: 替换为实际数据库持久化 */
    private static final Map<String, Map<String, Object>> LOCAL_ORDERS = new ConcurrentHashMap<>();

    @SaCheckLogin
    @PostMapping("/createOrder")
    public R<Map<String, Object>> createOrder(@RequestBody Map<String, Object> payParam) {
        Object amountObj = payParam.get("amount");
        if (amountObj == null) return R.fail("金额不能为空");
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountObj.toString());
        } catch (NumberFormatException e) {
            return R.fail("金额格式无效");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return R.fail("金额必须大于0");

        String outTradeNo = "WX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        Map<String, Object> order = new ConcurrentHashMap<>();
        order.put("outTradeNo", outTradeNo);
        order.put("amount", amount);
        order.put("houseId", payParam.getOrDefault("houseId", ""));
        order.put("status", "CREATED");
        order.put("createTime", LocalDateTime.now().toString());
        LOCAL_ORDERS.put(outTradeNo, order);

        log.info("创建支付订单: outTradeNo={}, amount={}", outTradeNo, amount);
        // Phase 6: 调用微信统一下单 API，返回 JSAPI 支付参数
        return R.fail("微信支付 Phase 6 实现 — 订单 " + outTradeNo + " 已创建，需 SDK 完成支付");
    }

    @SaIgnore
    @PostMapping("/notify")
    public void handlePayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        log.info("微信支付回调: body={}", body);
        response.setContentType("application/xml;charset=UTF-8");

        try {
            // Phase 6: 验签 — WxPayUtil.verifySignature(body, config.getMchKey())
            // Phase 6: 解析 XML → outTradeNo, transactionId, resultCode
            // Phase 6: 更新 LOCAL_ORDERS / 数据库订单状态
            // Phase 6: 更新 pr_transaction_record 交易记录状态
            String outTradeNo = extractXmlValue(body, "out_trade_no");
            if (outTradeNo != null && !outTradeNo.isEmpty()) {
                Map<String, Object> order = LOCAL_ORDERS.get(outTradeNo);
                if (order != null) {
                    order.put("status", "PAID");
                    order.put("paidTime", LocalDateTime.now().toString());
                }
            }
            response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + e.getMessage() + "]]></return_msg></xml>");
        }
    }

    @SaCheckLogin
    @GetMapping("/queryOrder")
    public R<Map<String, Object>> queryOrder(@RequestParam String outTradeNo) {
        if (outTradeNo == null || outTradeNo.isEmpty()) return R.fail("订单号不能为空");
        Map<String, Object> order = LOCAL_ORDERS.get(outTradeNo);
        if (order == null) return R.fail("订单不存在");
        // Phase 6: 调用微信订单查询 API 同步最新状态
        return R.ok(order);
    }

    @SaCheckLogin
    @PostMapping("/applyRefund")
    public R<Map<String, Object>> applyRefund(@RequestBody Map<String, Object> refundParam) {
        String outTradeNo = (String) refundParam.get("outTradeNo");
        if (outTradeNo == null || outTradeNo.isEmpty()) return R.fail("原订单号不能为空");
        Map<String, Object> order = LOCAL_ORDERS.get(outTradeNo);
        if (order == null) return R.fail("原订单不存在");
        if (!"PAID".equals(order.get("status"))) return R.fail("订单状态不允许退款");

        String outRefundNo = "RF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        log.info("申请退款: outTradeNo={}, outRefundNo={}", outTradeNo, outRefundNo);
        // Phase 6: 调用微信退款 API
        // Phase 6: 创建 pr_transaction_record 退款记录
        return R.fail("退款功能 Phase 6 实现 — 退款单 " + outRefundNo + " 待处理");
    }

    @SaIgnore
    @PostMapping("/refundNotify")
    public void handleRefundNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        log.info("微信退款回调: body={}", body);
        response.setContentType("application/xml;charset=UTF-8");
        // Phase 6: 验签 + 解析退款状态
        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
    }

    @SaCheckLogin
    @GetMapping("/queryRefund")
    public R<Map<String, Object>> queryRefund(@RequestParam String outRefundNo) {
        if (outRefundNo == null || outRefundNo.isEmpty()) return R.fail("退款单号不能为空");
        // Phase 6: 调用微信退款查询 API
        return R.fail("退款查询 Phase 6 实现");
    }

    private String extractXmlValue(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        if (start < 0) {
            String cdataOpen = "<" + tag + "><![CDATA[";
            start = xml.indexOf(cdataOpen);
            if (start < 0) return null;
            start += cdataOpen.length();
            int end = xml.indexOf("]]></" + tag + ">", start);
            return end > start ? xml.substring(start, end) : null;
        }
        start += open.length();
        int end = xml.indexOf(close, start);
        return end > start ? xml.substring(start, end) : null;
    }
}
