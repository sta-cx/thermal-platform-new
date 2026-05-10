package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.domain.bo.PrOptionsHeatBo;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供热系统选项管理
 * 迁移自旧系统 PrOptionsHeatController
 * 旧端点: /property/prOptionsHeat/* -> 新端点: /thermal/property/options-heat/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/options-heat")
public class PrOptionsHeatController extends BaseController {

    private final IPrOptionsHeatService optionsHeatService;

    @SaCheckPermission("thermal:property:optionsHeat:query")
    @SaCheckLogin
    @GetMapping
    public R<PrOptionsHeat> getDataById(
            @RequestParam String orgId,
            @RequestParam(required = false) String level) {
        PrOptionsHeat result = optionsHeatService.getByOrgAndCompany(orgId, level);
        if (result == null) {
            return R.fail("未找到配置数据");
        }
        return R.ok(result);
    }

    @SaCheckPermission("thermal:property:optionsHeat:add")
    @SaCheckLogin
    @Log(title = "供热选项", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertData(@RequestBody PrOptionsHeatBo bo) {
        PrOptionsHeat options = MapstructUtils.convert(bo, PrOptionsHeat.class);
        return toAjax(optionsHeatService.save(options));
    }

    @SaCheckPermission("thermal:property:optionsHeat:edit")
    @SaCheckLogin
    @Log(title = "供热选项", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateData(@RequestBody PrOptionsHeatBo bo) {
        PrOptionsHeat options = MapstructUtils.convert(bo, PrOptionsHeat.class);
        return toAjax(optionsHeatService.updateById(options));
    }

    @SaCheckPermission("thermal:property:optionsHeat:edit")
    @SaCheckLogin
    @PostMapping("/upsert")
    public R<Void> upsertCompanyData(@RequestBody PrOptionsHeatBo bo) {
        PrOptionsHeat options = MapstructUtils.convert(bo, PrOptionsHeat.class);
        PrOptionsHeat existing = optionsHeatService.getByOrgAndCompany(
                options.getOrgId(), options.getLevel());
        if (existing != null) {
            options.setId(existing.getId());
            return toAjax(optionsHeatService.updateById(options));
        }
        return toAjax(optionsHeatService.save(options));
    }

    @SaCheckPermission("thermal:property:optionsHeat:add")
    @SaCheckLogin
    @PostMapping("/init")
    public R<Void> initData(@RequestParam String orgId) {
        return toAjax(optionsHeatService.initData(orgId));
    }
}
