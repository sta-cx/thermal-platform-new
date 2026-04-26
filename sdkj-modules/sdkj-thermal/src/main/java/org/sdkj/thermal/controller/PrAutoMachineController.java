package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.service.IPrAutoMachineService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 自助缴费机管理
 * 迁移自旧系统 PrAutoMachineController
 * 旧端点: /property/autoMachine/* -> 新端点: /thermal/property/auto-machine/*
 */
@Tag(name = "自助缴费机管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/auto-machine")
public class PrAutoMachineController extends BaseController {

    private final IPrAutoMachineService autoMachineService;

    /**
     * 生成缴费流水号
     * 旧端点: POST /property/autoMachine/getSerialNum
     * 新端点: GET /thermal/property/auto-machine/serial-num
     */
    @SaCheckPermission("thermal:property:auto-machine:query")
    @SaCheckLogin
    @Log(title = "自助缴费机-生成流水号", businessType = BusinessType.OTHER)
    @GetMapping("/serial-num")
    public R<String> getSerialNum(@RequestParam String companyId) {
        return R.ok("生成成功", autoMachineService.generateSerialNum(companyId));
    }

    /**
     * 生成供暖缴费二维码
     * 旧端点: POST /property/autoMachine/getQrCode
     * 新端点: POST /thermal/property/auto-machine/qr-heat
     */
    @SaCheckPermission("thermal:property:auto-machine:pay")
    @SaCheckLogin
    @Log(title = "自助缴费机-供暖二维码", businessType = BusinessType.OTHER)
    @PostMapping("/qr-heat")
    public R<String> getQrCode(@RequestBody Map<String, String> params) {
        String type = params.getOrDefault("type", "wechat");
        String serialNum = params.get("serialNum");
        return R.ok("获取成功", autoMachineService.generateQrCode(type, serialNum));
    }

    /**
     * 生成水费缴费二维码
     * 旧端点: POST /property/autoMachine/getQrCodeWater
     * 新端点: POST /thermal/property/auto-machine/qr-water
     */
    @SaCheckPermission("thermal:property:auto-machine:pay")
    @SaCheckLogin
    @Log(title = "自助缴费机-水费二维码", businessType = BusinessType.OTHER)
    @PostMapping("/qr-water")
    public R<String> getQrCodeWater(@RequestBody Map<String, String> params) {
        String type = params.getOrDefault("type", "wechat");
        String serialNum = params.get("serialNum");
        return R.ok("获取成功", autoMachineService.generateQrCode(type, serialNum));
    }

    /**
     * 生成电费缴费二维码
     * 旧端点: POST /property/autoMachine/getQrCodeEle
     * 新端点: POST /thermal/property/auto-machine/qr-ele
     */
    @SaCheckPermission("thermal:property:auto-machine:pay")
    @SaCheckLogin
    @Log(title = "自助缴费机-电费二维码", businessType = BusinessType.OTHER)
    @PostMapping("/qr-ele")
    public R<String> getQrCodeEle(@RequestBody Map<String, String> params) {
        String type = params.getOrDefault("type", "wechat");
        String serialNum = params.get("serialNum");
        return R.ok("获取成功", autoMachineService.generateQrCode(type, serialNum));
    }

    /**
     * 查询支付成功状态
     * 旧端点: POST /property/autoMachine/queryPaymentSuccess
     * 新端点: GET /thermal/property/auto-machine/payment-status
     */
    @SaCheckPermission("thermal:property:auto-machine:query")
    @SaCheckLogin
    @GetMapping("/payment-status")
    public R<Boolean> queryPaymentSuccess(@RequestParam String serialNum) {
        return R.ok(autoMachineService.checkPaymentStatus(serialNum));
    }

    /**
     * 根据流水号查询交易记录
     * 旧端点: POST /property/autoMachine/getRecordBySerialNum
     * 新端点: GET /thermal/property/auto-machine/record
     */
    @SaCheckPermission("thermal:property:auto-machine:query")
    @SaCheckLogin
    @GetMapping("/record")
    public R<PrTransactionRecordVo> getRecordBySerialNum(@RequestParam String serialNum) {
        return R.ok(autoMachineService.getRecordBySerialNum(serialNum));
    }

    /**
     * 查询是否启用读卡
     * 旧端点: POST /property/autoMachine/getIsReadCard
     * 新端点: GET /thermal/property/auto-machine/read-card
     */
    @SaCheckPermission("thermal:property:auto-machine:query")
    @SaCheckLogin
    @GetMapping("/read-card")
    public R<Boolean> getIsReadCard(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId) {
        // 默认返回 true（读卡器可用）
        return R.ok(true);
    }

    /**
     * 微信支付回调（供暖）
     * 旧端点: POST /property/autoMachine/callback
     * 新端点: POST /thermal/property/auto-machine/callback/wechat-heat
     *
     * TODO: 待第三方支付SDK集成后实现，当前返回 fail
     */
    @SaIgnore
    @PostMapping("/callback/wechat-heat")
    public String wechatCallback(@RequestBody String xmlData) {
        log.warn("微信支付回调尚未实现，收到请求但未处理: {}", xmlData);
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[NOT_IMPLEMENTED]]></return_msg></xml>";
    }

    /**
     * 支付宝回调（供暖）
     * 旧端点: POST /property/autoMachine/aliCallBack
     * 新端点: POST /thermal/property/auto-machine/callback/ali-heat
     *
     * TODO: 待第三方支付SDK集成后实现，当前返回 fail
     */
    @SaIgnore
    @PostMapping("/callback/ali-heat")
    public String aliCallback(@RequestBody String body) {
        log.warn("支付宝回调尚未实现，收到请求但未处理");
        return "fail";
    }
}
