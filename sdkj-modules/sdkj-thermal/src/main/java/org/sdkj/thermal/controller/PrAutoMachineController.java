package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 自助机管理
 * 迁移自旧系统 PrAutoMachineController
 * 旧端点: /property/autoMachine/* -> 新端点: /thermal/property/auto-machine/*
 *
 * 注意：非支付端点已迁移骨架；支付端点（二维码生成、微信/支付宝回调）
 * 需要 Phase 6 第三方支付集成后方可完整实现。
 */
@Deprecated
@Hidden
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/auto-machine")
public class PrAutoMachineController extends BaseController {

    /**
     * 分页查询自助机数据
     * 旧端点: POST /property/autoMachine/pageList
     * 新端点: GET /thermal/property/auto-machine/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public R<?> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String search) {
        return R.fail("此功能需要完整实现");
    }

    /**
     * 根据ID查询数据
     * 旧端点: POST /property/autoMachine/getDataById
     * 新端点: GET /thermal/property/auto-machine/{id}
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<?> getById(@PathVariable String id) {
        return R.fail("此功能需要完整实现");
    }

    /**
     * 更新数据
     * 旧端点: POST /property/autoMachine/updateData
     * 新端点: PUT /thermal/property/auto-machine
     */
    @SaCheckLogin
    @Log(title = "自助机", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateData(@RequestBody Object data) {
        return R.fail("此功能需要完整实现");
    }

    /**
     * 生成流水号
     * 旧端点: POST /property/autoMachine/getSerialNum
     * 新端点: GET /thermal/property/auto-machine/serial-num
     */
    @GetMapping("/serial-num")
    public R<?> getSerialNum(
            @RequestParam String companyId,
            @RequestParam String orgId) {
        // TODO: Phase 5e 完整实现
        return R.fail("此功能需要完整实现");
    }

    /**
     * 生成供暖缴费二维码
     * 旧端点: POST /property/autoMachine/getQrCode
     * 新端点: POST /thermal/property/auto-machine/qr-heat
     */
    @PostMapping("/qr-heat")
    public R<?> getQrCode(@RequestBody Object data) {
        // TODO: Phase 6 - 微信/支付宝支付集成
        return R.fail("此功能需要第三方支付集成");
    }

    /**
     * 生成水费缴费二维码
     * 旧端点: POST /property/autoMachine/getQrCodeWater
     * 新端点: POST /thermal/property/auto-machine/qr-water
     */
    @PostMapping("/qr-water")
    public R<?> getQrCodeWater(@RequestBody Object data) {
        return R.fail("此功能需要第三方支付集成");
    }

    /**
     * 生成电费缴费二维码
     * 旧端点: POST /property/autoMachine/getQrCodeEle
     * 新端点: POST /thermal/property/auto-machine/qr-ele
     */
    @PostMapping("/qr-ele")
    public R<?> getQrCodeEle(@RequestBody Object data) {
        return R.fail("此功能需要第三方支付集成");
    }

    /**
     * 微信支付回调（供暖）
     * 旧端点: POST /property/autoMachine/callback
     * 新端点: POST /thermal/property/auto-machine/callback/wechat-heat
     *
     * @deprecated 支付回调功能尚未实现，存在安全风险，请勿调用此端点
     */
    @Deprecated
    @Hidden
    @SaIgnore
    @PostMapping("/callback/wechat-heat")
    public String wechatCallback(@RequestBody String xmlData) {
        throw new ServiceException("支付回调功能尚未实现，请勿调用此端点");
    }

    /**
     * 支付宝回调（供暖）
     * 旧端点: POST /property/autoMachine/aliCallBack
     * 新端点: POST /thermal/property/auto-machine/callback/ali-heat
     *
     * @deprecated 支付回调功能尚未实现，存在安全风险，请勿调用此端点
     */
    @Deprecated
    @Hidden
    @SaIgnore
    @PostMapping("/callback/ali-heat")
    public String aliCallback(@RequestBody Object data) {
        throw new ServiceException("支付回调功能尚未实现，请勿调用此端点");
    }

    /**
     * 查询支付成功状态
     * 旧端点: POST /property/autoMachine/queryPaymentSuccess
     * 新端点: GET /thermal/property/auto-machine/payment-status
     */
    @GetMapping("/payment-status")
    public R<?> queryPaymentSuccess(@RequestParam String serialNum) {
        // TODO: Phase 5e/6 完整实现
        return R.fail("此功能需要完整实现");
    }

    /**
     * 根据流水号查询交易记录
     * 旧端点: POST /property/autoMachine/getRecordBySerialNum
     * 新端点: GET /thermal/property/auto-machine/record
     */
    @GetMapping("/record")
    public R<?> getRecordBySerialNum(@RequestParam String serialNum) {
        // TODO: Phase 5e/6 完整实现
        return R.fail("此功能需要完整实现");
    }

    /**
     * 查询是否启用读卡
     * 旧端点: POST /property/autoMachine/getIsReadCard
     * 新端点: GET /thermal/property/auto-machine/read-card
     */
    @GetMapping("/read-card")
    public R<?> getIsReadCard(
            @RequestParam String companyId,
            @RequestParam String orgId) {
        // TODO: Phase 5e 完整实现
        return R.fail("此功能需要完整实现");
    }
}
