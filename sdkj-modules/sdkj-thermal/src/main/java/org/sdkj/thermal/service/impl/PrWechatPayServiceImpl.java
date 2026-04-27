package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.config.WechatPayConfig;
import org.sdkj.thermal.domain.PrWechatOrder;
import org.sdkj.thermal.domain.PrWechatRefund;
import org.sdkj.thermal.mapper.PrWechatOrderMapper;
import org.sdkj.thermal.mapper.PrWechatRefundMapper;
import org.sdkj.thermal.service.IPrWechatPayService;
import org.sdkj.thermal.wechat.wxPay.WXPay;
import org.sdkj.thermal.wechat.wxPay.WXPayConfigImpl;
import org.sdkj.thermal.wechat.wxPay.WXPayConstants;
import org.sdkj.thermal.wechat.wxPay.WXPayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrWechatPayServiceImpl extends ServiceImpl<PrWechatOrderMapper, PrWechatOrder>
        implements IPrWechatPayService {

    private final PrWechatOrderMapper baseMapper;
    private final PrWechatRefundMapper wechatRefundMapper;
    private final WechatPayConfig payConfig;

    private WXPay wxPay;

    @PostConstruct
    public void init() {
        try {
            WXPayConfigImpl wxPayConfig = new WXPayConfigImpl(payConfig);
            this.wxPay = new WXPay(wxPayConfig, payConfig.getNotifyUrl(), false);
            log.info("微信支付 WXPay 实例初始化成功，appid={}, mchId={}", payConfig.getAppid(), payConfig.getMchId());
        } catch (Exception e) {
            log.error("微信支付 WXPay 实例初始化失败", e);
            throw new RuntimeException("微信支付初始化失败: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> createOrder(String openId, String otherCode, String houseAddress,
                                           BigDecimal totalFee, String body, String attach, String operator) {
        try {
            // 1. 从 attach 中解析订单号和 houseId
            String outTradeNo = "WX" + System.currentTimeMillis();
            String houseId = "";
            if (attach != null && !attach.isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> jsonMap = objectMapper.readValue(attach, Map.class);
                    if (jsonMap.get("orderNo") != null) {
                        outTradeNo = (String) jsonMap.get("orderNo");
                    }
                    if (jsonMap.get("houseId") != null) {
                        houseId = (String) jsonMap.get("houseId");
                    }
                } catch (Exception e) {
                    log.warn("解析 attach 参数失败，使用默认订单号: {}", e.getMessage());
                }
            }

            // 2. 准备微信统一下单参数
            String bodyText = body != null ? body : "供热费支付";
            int totalFeeFen = totalFee.multiply(new BigDecimal(100)).intValue();

            Map<String, String> params = new HashMap<>();
            params.put("body", bodyText);
            params.put("out_trade_no", outTradeNo);
            params.put("total_fee", String.valueOf(totalFeeFen));
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("trade_type", "JSAPI");
            params.put("openid", openId);

            // 3. 调用微信统一下单接口
            log.info("调用微信统一下单接口，订单号: {}, 金额(分): {}", outTradeNo, totalFeeFen);
            Map<String, String> wxResult = wxPay.unifiedOrder(params);
            log.info("微信统一下单返回结果: {}", wxResult);

            // 4. 校验返回结果
            if (!WXPayConstants.SUCCESS.equals(wxResult.get("return_code"))) {
                throw new RuntimeException("创建支付订单失败: " + wxResult.get("return_msg"));
            }
            if (!WXPayConstants.SUCCESS.equals(wxResult.get("result_code"))) {
                throw new RuntimeException("创建支付订单失败: " + wxResult.get("err_code_des"));
            }

            // 5. 保存订单到数据库
            PrWechatOrder order = new PrWechatOrder();
            order.setId(UUID.randomUUID().toString().replace("-", ""));
            order.setOutTradeNo(outTradeNo);
            order.setOpenId(openId);
            order.setOtherCode(otherCode);
            order.setHouseId(houseId);
            order.setHouseAddress(houseAddress);
            order.setTotalFee(totalFee);
            order.setBody(bodyText);
            order.setOrderStatus(0);
            order.setSpBillCreateIp("127.0.0.1");
            order.setCreateTime(new Date());
            order.setExpireTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            order.setNotifyUrl(payConfig.getNotifyUrl());
            order.setTradeType("JSAPI");
            order.setAttach(attach);
            order.setOperator(operator);
            order.setIsDeleted(0);
            baseMapper.insert(order);

            // 6. 生成前端调起 JSAPI 支付的参数
            String prepayId = wxResult.get("prepay_id");
            Map<String, String> jsApiParam = new HashMap<>();
            jsApiParam.put("appId", payConfig.getAppid());
            jsApiParam.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            jsApiParam.put("nonceStr", WXPayUtil.generateNonceStr());
            jsApiParam.put("package", "prepay_id=" + prepayId);
            jsApiParam.put("signType", "MD5");
            jsApiParam.put("paySign", WXPayUtil.generateSignature(jsApiParam, payConfig.getKey(),
                    WXPayConstants.SignType.MD5));

            log.info("生成前端支付参数完成，prepay_id: {}", prepayId);

            // 7. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("outTradeNo", outTradeNo);
            result.put("totalFee", totalFee);
            result.put("jsApiParam", jsApiParam);
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            throw new RuntimeException("创建支付订单失败: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handlePayNotify(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Map<String, String> resultMap = WXPayUtil.xmlToMap(sb.toString());
            String outTradeNo = resultMap.get("out_trade_no");
            if (outTradeNo != null) {
                PrWechatOrder order = baseMapper.selectByOutTradeNo(outTradeNo);
                if (order != null && order.getOrderStatus() == 0) {
                    order.setOrderStatus(1);
                    order.setTransactionId(resultMap.get("transaction_id"));
                    order.setPayTime(new Date());
                    baseMapper.updateById(order);
                }
            }
            return buildXmlResponse("SUCCESS", "OK");
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return buildXmlResponse("FAIL", "处理异常");
        }
    }

    @Override
    public PrWechatOrder getOrderByOutTradeNo(String outTradeNo) {
        return baseMapper.selectByOutTradeNo(outTradeNo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> applyRefund(String outTradeNo, BigDecimal refundFee,
                                           String refundReason, String operator) {
        PrWechatOrder order = baseMapper.selectByOutTradeNo(outTradeNo);
        if (order == null) throw new RuntimeException("原订单不存在");
        if (order.getOrderStatus() != 1) throw new RuntimeException("只有支付成功的订单才能申请退款");

        String outRefundNo = "RF" + System.currentTimeMillis();
        PrWechatRefund refund = new PrWechatRefund();
        refund.setId(UUID.randomUUID().toString().replace("-", ""));
        refund.setOutTradeNo(outTradeNo);
        refund.setTransactionId(order.getTransactionId());
        refund.setOutRefundNo(outRefundNo);
        refund.setTotalFee(order.getTotalFee());
        refund.setRefundFee(refundFee);
        refund.setRefundReason(refundReason);
        refund.setRefundStatus(0);
        refund.setOpenId(order.getOpenId());
        refund.setHouseId(order.getHouseId());
        refund.setOperator(operator);
        refund.setCreateTime(new Date());
        wechatRefundMapper.insert(refund);

        order.setOrderStatus(4);
        order.setUpdateTime(new Date());
        baseMapper.updateById(order);

        Map<String, Object> result = new HashMap<>();
        result.put("outTradeNo", outTradeNo);
        result.put("outRefundNo", outRefundNo);
        result.put("refundFee", refundFee);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handleRefundNotify(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Map<String, String> resultMap = WXPayUtil.xmlToMap(sb.toString());
            String outRefundNo = resultMap.get("out_refund_no");
            if (outRefundNo != null) {
                PrWechatRefund refund = wechatRefundMapper.selectByOutRefundNo(outRefundNo);
                if (refund != null) {
                    refund.setRefundStatus(1);
                    refund.setRefundTime(new Date());
                    wechatRefundMapper.updateById(refund);
                }
            }
            return buildXmlResponse("SUCCESS", "OK");
        } catch (Exception e) {
            log.error("处理微信退款回调失败", e);
            return buildXmlResponse("FAIL", "处理异常");
        }
    }

    @Override
    public PrWechatRefund getRefundByOutRefundNo(String outRefundNo) {
        return wechatRefundMapper.selectByOutRefundNo(outRefundNo);
    }

    private String buildXmlResponse(String code, String msg) {
        return "<xml><return_code><![CDATA[" + code + "]]></return_code>" +
               "<return_msg><![CDATA[" + msg + "]]></return_msg></xml>";
    }
}
