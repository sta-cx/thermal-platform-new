package org.sdkj.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.web.service.ThermalCodeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表编码生成器
 * 迁移自旧系统 AccessCodeController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/code")
public class ThermalCodeController {

    private final ThermalCodeService thermalCodeService;

    /**
     * 获取仪表厂商最新编码
     * 旧端点: GET /accessCode/accessMtVendorCode
     * 新端点: GET /thermal/code/accessMtVendorCode
     */
    @SaCheckLogin
    @GetMapping("/accessMtVendorCode")
    public R<String> accessMtVendorCode() {
        return R.ok(thermalCodeService.accessMtVendorCode());
    }

    /**
     * 获取仪表分类最新编码
     * 格式: vendorCode + meterType + 序号
     * 旧端点: GET /accessCode/accessMtSortCode
     * 新端点: GET /thermal/code/accessMtSortCode
     */
    @SaCheckLogin
    @GetMapping("/accessMtSortCode")
    public R<String> accessMtSortCode(
            @RequestParam @NotBlank String vendorId,
            @RequestParam @NotBlank String meterType,
            @RequestParam @NotBlank String vendorCode) {
        return R.ok(thermalCodeService.accessMtSortCode(vendorId, meterType, vendorCode));
    }

    /**
     * 获取仪表设备编码
     * 旧端点: GET /accessCode/accessMeterCode
     * 新端点: GET /thermal/code/accessMeterCode
     */
    @SaCheckLogin
    @GetMapping("/accessMeterCode")
    public R<String> accessMeterCode(
            @RequestParam @NotBlank String sortCode,
            @RequestParam @NotBlank String sortId,
            @RequestParam @NotBlank String meterType) {
        String code = switch (meterType) {
            case "01" -> thermalCodeService.accessEleCode(sortId, sortCode);
            case "02" -> thermalCodeService.accessWaterCode(sortCode, sortId);
            case "03" -> thermalCodeService.accessHotCode(sortCode, sortId);
            case "04" -> thermalCodeService.accessGasCode(sortCode, sortId);
            case "31" -> thermalCodeService.accessValveCode(sortCode, sortId);
            case "21" -> thermalCodeService.accessTempCode(sortCode, sortId);
            default -> null;
        };
        return code != null ? R.ok(code) : R.fail("不支持的仪表类型: " + meterType);
    }
}
