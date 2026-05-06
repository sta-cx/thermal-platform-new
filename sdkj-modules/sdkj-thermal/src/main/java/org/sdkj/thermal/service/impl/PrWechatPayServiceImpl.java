package org.sdkj.thermal.service.impl;

import org.sdkj.common.core.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.SpringUtils;
import org.sdkj.common.tenant.core.TenantDataSourceHelper;
import org.sdkj.thermal.config.WechatPayConfig;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.PrWechatOrder;
import org.sdkj.thermal.domain.PrWechatRefund;
import org.sdkj.thermal.mapper.PrExpenseMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.mapper.PrWechatOrderMapper;
import org.sdkj.thermal.mapper.PrWechatRefundMapper;
import org.sdkj.thermal.service.IPrWechatPayService;
import org.sdkj.thermal.wechat.wxPay.WXPay;
import org.sdkj.thermal.wechat.wxPay.WXPayConfigImpl;
import org.sdkj.thermal.wechat.wxPay.WXPayConstants;
import org.sdkj.thermal.wechat.wxPay.WXPayUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrWechatPayServiceImpl extends ServiceImpl<PrWechatOrderMapper, PrWechatOrder>
        implements IPrWechatPayService {

    private final PrWechatOrderMapper baseMapper;
    private final PrWechatRefundMapper wechatRefundMapper;
    private final WechatPayConfig payConfig;
    private final PrTransactionRecordMapper transactionRecordMapper;
    private final PrExpenseMapper expenseMapper;
    private final PrHouseMapper houseMapper;

    private WXPay wxPay;

    @PostConstruct
    public void init() {
        try {
            WXPayConfigImpl wxPayConfig = new WXPayConfigImpl(payConfig);
            this.wxPay = new WXPay(wxPayConfig, payConfig.getNotifyUrl(), false);
            log.info("微信支付 WXPay 实例初始化成功，appid={}, mchId={}", payConfig.getAppid(), payConfig.getMchId());
        } catch (Exception e) {
            log.error("微信支付 WXPay 实例初始化失败", e);
            throw new ServiceException("微信支付初始化失败: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> createOrder(String openId, String otherCode, String houseAddress,
                                           BigDecimal totalFee, String body, String attach, String operator) {
        try {
            // 1. 从 attach 中解析订单号和 houseId
            String outTradeNo = "WX" + System.currentTimeMillis();
            Long houseId = null;
            if (attach != null && !attach.isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> jsonMap = objectMapper.readValue(attach, Map.class);
                    if (jsonMap.get("orderNo") != null) {
                        outTradeNo = (String) jsonMap.get("orderNo");
                    }
                    if (jsonMap.get("houseId") != null) {
                        houseId = Long.valueOf(jsonMap.get("houseId").toString());
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
                throw new ServiceException("创建支付订单失败: " + wxResult.get("return_msg"));
            }
            if (!WXPayConstants.SUCCESS.equals(wxResult.get("result_code"))) {
                throw new ServiceException("创建支付订单失败: " + wxResult.get("err_code_des"));
            }

            // 5. 保存订单到数据库
            PrWechatOrder order = new PrWechatOrder();
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
            order.setDelFlag("0");
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
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            throw new ServiceException("创建支付订单失败: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handlePayNotify(HttpServletRequest request) {
        try {
            // 1. 读取回调数据
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String xmlData = sb.toString();
            log.info("收到微信支付回调通知: {}", xmlData);

            // 2. 解析XML
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlData);

            // 3. 验证签名 - 防止伪造回调
            if (!WXPayUtil.isSignatureValid(resultMap, payConfig.getKey())) {
                log.error("微信支付回调签名验证失败");
                return buildXmlResponse("FAIL", "签名验证失败");
            }

            // 4. 验证返回状态
            if (!WXPayConstants.SUCCESS.equals(resultMap.get("return_code"))) {
                log.error("微信支付回调返回失败: {}", resultMap.get("return_msg"));
                return buildXmlResponse("FAIL", resultMap.get("return_msg"));
            }

            // 5. 处理支付结果
            if (WXPayConstants.SUCCESS.equals(resultMap.get("result_code"))) {
                String outTradeNo = resultMap.get("out_trade_no");
                String transactionId = resultMap.get("transaction_id");
                String totalFeeFen = resultMap.get("total_fee");
                String openId = resultMap.get("openid");
                String bankType = resultMap.get("bank_type");
                String timeEnd = resultMap.get("time_end");

                // 6. 查询本地订单
                PrWechatOrder order = baseMapper.selectByOutTradeNo(outTradeNo);
                if (order == null) {
                    log.error("微信支付回调处理失败，订单不存在: {}", outTradeNo);
                    return buildXmlResponse("FAIL", "订单不存在");
                }

                // 7. 幂等性检查 - 已处理过的通知直接返回成功
                if (order.getOrderStatus() != null && order.getOrderStatus() == 1) {
                    log.info("微信支付回调重复通知，订单已处理: {}", outTradeNo);
                    return buildXmlResponse("SUCCESS", "OK");
                }

                // 8. 验证金额 - 防止金额篡改
                BigDecimal orderAmount = order.getTotalFee();
                BigDecimal notifyAmount = new BigDecimal(totalFeeFen).divide(new BigDecimal(100));
                if (orderAmount.compareTo(notifyAmount) != 0) {
                    log.error("微信支付回调金额不匹配，订单: {}, 订单金额: {}，回调金额: {}",
                            outTradeNo, orderAmount, notifyAmount);
                    return buildXmlResponse("FAIL", "金额不匹配");
                }

                // 9. 更新订单状态
                order.setOrderStatus(1);
                order.setTransactionId(transactionId);
                order.setBankType(bankType);
                order.setPayTime(new Date());
                order.setUpdateTime(new Date());
                baseMapper.updateById(order);
                log.info("微信支付回调处理成功，订单: {}, 交易号: {}", outTradeNo, transactionId);

                // 10. 插入交易流水记录 (PrTransactionRecord)
                PrTransactionRecord record = new PrTransactionRecord();
                record.setSerialNum(transactionId);
                record.setTransactionType(1);      // 1=收费
                record.setPaymentType(2);          // 2=微信
                record.setAmount(notifyAmount);
                record.setPaidAmount(notifyAmount);
                record.setStatus(0);               // 0=正常
                record.setHouseId(order.getHouseId());
                record.setTransactionTime(new Date());
                record.setOperatorId(openId);
                record.setNotes("微信支付自动入账，订单号: " + outTradeNo);
                record.setCreateTime(new Date());
                transactionRecordMapper.insert(record);
                Long recordId = record.getId();
                log.info("微信支付回调：交易流水已创建，recordId: {}", recordId);

                // 11. 更新费用明细已缴金额 (PrExpense)
                String yearStr = String.valueOf(LocalDate.now().getYear());
                int expenseRows = expenseMapper.updateExpenseByWechat(
                    order.getHouseId(), notifyAmount, recordId, openId, yearStr);
                log.info("微信支付回调：费用明细已更新，houseId: {}, year: {}, 更新行数: {}",
                    order.getHouseId(), yearStr, expenseRows);

                // 12. 更新房屋缴费状态 (PrHouse)
                int houseRows = houseMapper.updateHouseIsCharged(order.getHouseId());
                log.info("微信支付回调：房屋缴费状态已更新，houseId: {}, 更新行数: {}",
                    order.getHouseId(), houseRows);
            }

            // 10. 返回处理结果
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
        try {
            // 1. 查询原订单
            PrWechatOrder order = baseMapper.selectByOutTradeNo(outTradeNo);
            if (order == null) {
                throw new ServiceException("原订单不存在");
            }

            // 2. 验证订单状态（1=支付成功）
            if (order.getOrderStatus() != 1) {
                throw new ServiceException("只有支付成功的订单才能申请退款");
            }

            // 3. 验证退款金额不超过订单总金额
            if (refundFee.compareTo(order.getTotalFee()) > 0) {
                throw new ServiceException("退款金额不能超过订单总金额");
            }

            // 4. 检查是否已有成功的退款记录
            PrWechatRefund existingRefund = wechatRefundMapper.selectByOutTradeNo(outTradeNo);
            if (existingRefund != null && existingRefund.getRefundStatus() == 1) {
                throw new ServiceException("该订单已完成退款");
            }

            // 5. 生成退款单号
            String outRefundNo = "RF" + System.currentTimeMillis() +
                    String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));

            // 6. 准备微信退款接口参数（金额单位为分）
            Map<String, String> params = new HashMap<>();
            params.put("appid", payConfig.getAppid());
            params.put("mch_id", payConfig.getMchId());
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            params.put("out_trade_no", outTradeNo);
            params.put("out_refund_no", outRefundNo);
            params.put("total_fee", String.valueOf(order.getTotalFee().multiply(new BigDecimal(100)).intValue()));
            params.put("refund_fee", String.valueOf(refundFee.multiply(new BigDecimal(100)).intValue()));
            params.put("refund_desc", refundReason != null ? refundReason : "正常退款");
            if (payConfig.getRefundNotifyUrl() != null) {
                params.put("notify_url", payConfig.getRefundNotifyUrl());
            }

            // 7. 调用微信退款接口
            log.info("调用微信退款接口，订单号: {}, 退款单号: {}, 退款金额(分): {}",
                    outTradeNo, outRefundNo, refundFee.multiply(new BigDecimal(100)).intValue());
            Map<String, String> wxResult = wxPay.refund(params);
            log.info("微信退款接口返回结果: {}", wxResult);

            // 8. 校验返回结果
            if (!WXPayConstants.SUCCESS.equals(wxResult.get("return_code"))) {
                throw new ServiceException("申请退款失败: " + wxResult.get("return_msg"));
            }
            if (!WXPayConstants.SUCCESS.equals(wxResult.get("result_code"))) {
                throw new ServiceException("申请退款失败: " + wxResult.get("err_code_des"));
            }

            // 9. 保存退款记录（状态=0 退款处理中）
            PrWechatRefund refund = new PrWechatRefund();
            refund.setOutTradeNo(outTradeNo);
            refund.setTransactionId(order.getTransactionId());
            refund.setOutRefundNo(outRefundNo);
            refund.setRefundId(wxResult.get("refund_id"));
            refund.setTotalFee(order.getTotalFee());
            refund.setRefundFee(refundFee);
            refund.setRefundReason(refundReason);
            refund.setRefundStatus(0); // 0=退款处理中
            refund.setRefundChannel(wxResult.get("refund_channel"));
            refund.setOpenId(order.getOpenId());
            refund.setHouseId(order.getHouseId());
            refund.setOperator(operator);
            refund.setCreateTime(new Date());
            wechatRefundMapper.insert(refund);

            // 10. 更新订单状态为退款中（4=退款中）
            order.setOrderStatus(4);
            order.setUpdateTime(new Date());
            baseMapper.updateById(order);

            // 11. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("outTradeNo", outTradeNo);
            result.put("outRefundNo", outRefundNo);
            result.put("refundId", wxResult.get("refund_id"));
            result.put("refundFee", refundFee);
            result.put("refundStatus", 0);
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("申请微信退款失败", e);
            throw new ServiceException("申请退款失败: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handleRefundNotify(HttpServletRequest request) {
        try {
            // 1. 读取回调数据
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String xmlData = sb.toString();
            log.info("收到微信退款回调通知: {}", xmlData);

            // 2. 解析XML
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlData);

            // 3. 验证签名 - 防止伪造回调
            if (!WXPayUtil.isSignatureValid(resultMap, payConfig.getKey())) {
                log.error("微信退款回调签名验证失败");
                return buildXmlResponse("FAIL", "签名验证失败");
            }

            // 4. 获取退款单号和退款状态
            String outRefundNo = resultMap.get("out_refund_no");
            String refundStatus = resultMap.get("refund_status");

            // 5. 查找本地退款记录
            PrWechatRefund refund = wechatRefundMapper.selectByOutRefundNo(outRefundNo);
            if (refund == null) {
                log.error("微信退款回调处理失败，退款记录不存在: {}", outRefundNo);
                return buildXmlResponse("FAIL", "退款记录不存在");
            }

            // 6. 根据退款状态更新本地记录
            //    SUCCESS=1(退款成功), CHANGE=2(退款异常), REFUNDCLOSE=3(退款关闭), FAIL=3(退款失败)
            if ("SUCCESS".equals(refundStatus)) {
                refund.setRefundStatus(1);
                refund.setRefundTime(new Date());
            } else if ("CHANGE".equals(refundStatus)) {
                refund.setRefundStatus(2);
                refund.setRefundTime(new Date());
            } else if ("REFUNDCLOSE".equals(refundStatus)) {
                refund.setRefundStatus(3);
            } else if ("FAIL".equals(refundStatus)) {
                refund.setRefundStatus(3);
            }

            refund.setUpdateTime(new Date());
            wechatRefundMapper.updateById(refund);

            log.info("微信退款回调处理成功，退款单号: {}, 退款状态: {}", outRefundNo, refundStatus);

            // 7. 返回处理结果
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

    /**
     * 定时取消超过24小时未支付的订单
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cancelExpiredOrders() {
        List<String> tenantCodes = queryEnabledTenantCodes();
        for (String tenantCode : tenantCodes) {
            boolean tenantPushed = TenantDataSourceHelper.pushTenant(tenantCode);
            try {
                cancelExpiredOrdersForTenant(tenantCode);
            } finally {
                TenantDataSourceHelper.clearTenant(tenantPushed);
            }
        }
    }

    private void cancelExpiredOrdersForTenant(String tenantCode) {
        try {
            Date expireThreshold = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            List<PrWechatOrder> expiredOrders = lambdaQuery()
                    .eq(PrWechatOrder::getOrderStatus, 0)
                    .lt(PrWechatOrder::getCreateTime, expireThreshold)
                    .list();

            if (expiredOrders.isEmpty()) {
                log.debug("租户 {} 没有需要取消的过期订单", tenantCode);
                return;
            }

            for (PrWechatOrder order : expiredOrders) {
                order.setOrderStatus(3); // 3 = 已取消
                order.setUpdateTime(new Date());
                baseMapper.updateById(order);
                log.info("取消过期微信支付订单: outTradeNo={}, createTime={}",
                        order.getOutTradeNo(), order.getCreateTime());
            }

            log.info("租户 {} 过期订单取消完成，共取消 {} 笔订单", tenantCode, expiredOrders.size());
        } catch (Exception e) {
            log.error("租户 {} 取消过期订单任务执行失败", tenantCode, e);
        }
    }

    private List<String> queryEnabledTenantCodes() {
        try {
            DataSource dataSource = SpringUtils.getBean(DataSource.class);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            return jdbcTemplate.queryForList(
                "SELECT tenant_id FROM sys_tenant WHERE status = '0' AND del_flag = '0' AND db_url IS NOT NULL",
                String.class
            );
        } catch (Exception e) {
            log.error("查询启用租户列表失败，跳过微信过期订单取消任务", e);
            return List.of();
        }
    }

    private String buildXmlResponse(String code, String msg) {
        return "<xml><return_code><![CDATA[" + code + "]]></return_code>" +
               "<return_msg><![CDATA[" + msg + "]]></return_msg></xml>";
    }
}
