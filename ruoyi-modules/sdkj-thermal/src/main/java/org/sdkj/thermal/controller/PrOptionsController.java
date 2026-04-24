package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrOptions;
import org.sdkj.thermal.service.IPrOptionsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统选项管理
 * 迁移自旧系统 PrOptionsController
 * 旧端点: /property/options/* -> 新端点: /thermal/property/options/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/options")
public class PrOptionsController extends BaseController {

    private final IPrOptionsService optionsService;

    /**
     * 根据组织ID查找系统选项
     * 旧端点: POST /property/options/getDataById
     * 新端点: GET /thermal/property/options
     */
    @SaCheckLogin
    @GetMapping
    public R<?> getDataById(
            @RequestParam String orgId,
            @RequestParam String companyId,
            @RequestParam(required = false) String level) {
        return R.ok(optionsService.selectByOrgId(orgId));
    }

    /**
     * 新增系统选项
     * 旧端点: POST /property/options/insertData
     * 新端点: POST /thermal/property/options
     */
    @SaCheckLogin
    @Log(title = "系统选项", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertData(@RequestBody PrOptions options) {
        return toAjax(optionsService.save(options));
    }

    /**
     * 修改系统选项
     * 旧端点: POST /property/options/updateData
     * 新端点: PUT /thermal/property/options
     */
    @SaCheckLogin
    @Log(title = "系统选项", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateData(@RequestBody PrOptions options) {
        return toAjax(optionsService.updateById(options));
    }

    /**
     * 初始化系统选项
     * 旧端点: POST /property/options/initData
     * 新端点: POST /thermal/property/options/init
     */
    @SaCheckLogin
    @PostMapping("/init")
    public R<Void> initData(@RequestParam String orgId, @RequestParam String companyId) {
        return toAjax(optionsService.initOptions(orgId));
    }

    /**
     * 检查是否禁止购电/购水
     * 旧端点: POST /property/options/forbiddenToBuy
     * 新端点: GET /thermal/property/options/forbidden
     */
    @SaCheckLogin
    @GetMapping("/forbidden")
    public R<?> forbiddenToBuy(
            @RequestParam String meterId,
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam(required = false) String type) {
        return R.ok(optionsService.forbiddenToBuyCheck(meterId));
    }
}
