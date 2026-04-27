package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.AgReaderParam;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.service.IAgReaderParamService;
import org.sdkj.thermal.service.IPrAutoMachineService;
import org.sdkj.thermal.wechat.wxPay.WXPayUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.LinkedHashMap;
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
    private final IAgReaderParamService readerParamService;

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
     * 微信支付回调（供暖自助机）
     * 迁移自旧系统 POST /property/autoMachine/callback
     */
    @SaIgnore
    @PostMapping("/callback/wechat-heat")
    public String wechatCallback(@RequestBody String xmlData) {
        try {
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlData);
            if (!"SUCCESS".equals(resultMap.get("return_code"))) {
                return buildXmlResponse("FAIL", resultMap.get("return_msg"));
            }
            String outTradeNo = resultMap.get("out_trade_no");
            log.info("自助机微信支付回调: outTradeNo={}, result_code={}", outTradeNo, resultMap.get("result_code"));
            return buildXmlResponse("SUCCESS", "OK");
        } catch (Exception e) {
            log.error("自助机微信支付回调处理失败", e);
            return buildXmlResponse("FAIL", "处理异常");
        }
    }

    private String buildXmlResponse(String code, String msg) {
        return "<xml><return_code><![CDATA[" + code + "]]></return_code>" +
               "<return_msg><![CDATA[" + msg + "]]></return_msg></xml>";
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

    /**
     * 获取读卡器参数配置
     * 旧端点: GET /property/autoMachine/getReaderParam
     * 新端点: GET /thermal/property/auto-machine/reader-param
     *
     * @param code 读卡器编码，默认为 "auto-machine"
     * @return 读卡器参数配置
     */
    @SaIgnore
    @GetMapping("/reader-param")
    @Operation(summary = "获取读卡器参数")
    public R<AgReaderParam> getReaderParam(
            @RequestParam(required = false, defaultValue = "auto-machine") String code) {
        AgReaderParam param = readerParamService.getOne(
                new LambdaQueryWrapper<AgReaderParam>()
                        .eq(AgReaderParam::getCode, code)
                        .last("LIMIT 1"));
        if (param == null) {
            log.warn("未找到读卡器参数配置, code={}", code);
            // 返回默认配置，确保自助机可以正常启动
            param = new AgReaderParam();
            param.setCode(code);
            param.setIsAutoUpdate(1);
        }
        return R.ok(param);
    }

    /**
     * 获取客户端最新版本信息
     * 旧端点: GET /property/autoMachine/getClientVersion
     * 新端点: GET /thermal/property/auto-machine/client-version
     *
     * @return 版本信息（version, downloadUrl, updateNotes）
     */
    @SaIgnore
    @GetMapping("/client-version")
    @Operation(summary = "获取客户端最新版本")
    public R<Map<String, Object>> getClientVersion() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("version", "1.0.0");
        result.put("downloadUrl", "/thermal/property/auto-machine/client-download");
        result.put("updateNotes", "初始版本");
        result.put("forceUpdate", false);
        return R.ok(result);
    }

    /**
     * 获取客户端下载地址
     * 旧端点: GET /property/autoMachine/getClientDownload
     * 新端点: GET /thermal/property/auto-machine/client-download
     *
     * @return 下载信息（url, fileName, fileSize）
     */
    @SaIgnore
    @GetMapping("/client-download")
    @Operation(summary = "获取客户端下载地址")
    public R<Map<String, Object>> getClientDownload() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("url", "/download/auto-machine-client.exe");
        result.put("fileName", "auto-machine-client.exe");
        result.put("fileSize", "50MB");
        result.put("md5", "");
        return R.ok(result);
    }

    /**
     * 更新自助缴费机配置
     * 旧端点: PUT /property/autoMachine/updateData
     * 新端点: PUT /thermal/property/auto-machine
     *
     * @param param 自助机配置参数
     * @return 更新结果
     */
    @SaIgnore
    @PutMapping
    @Operation(summary = "更新自助缴费机配置")
    @Log(title = "自助缴费机-更新配置", businessType = BusinessType.UPDATE)
    public R<Void> updateData(@RequestBody AgReaderParam param) {
        if (param == null || StrUtil.isBlank(param.getCode())) {
            return R.fail("设备编码不能为空");
        }

        AgReaderParam existing = readerParamService.getOne(
                new LambdaQueryWrapper<AgReaderParam>()
                        .eq(AgReaderParam::getCode, param.getCode())
                        .last("LIMIT 1"));

        if (existing == null) {
            // 不存在则新增
            readerParamService.save(param);
            log.info("新增自助机配置, code={}", param.getCode());
        } else {
            // 存在则更新
            param.setId(existing.getId());
            readerParamService.updateById(param);
            log.info("更新自助机配置, code={}", param.getCode());
        }
        return R.ok();
    }
}
