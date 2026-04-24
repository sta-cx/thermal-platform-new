package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrPrintTemplate;
import org.sdkj.thermal.service.IPrPrintTemplateService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打印模板管理
 * 迁移自旧系统 PrPrintTemplateController
 * 旧端点: /property/prPrintTemplate/* -> 新端点: /thermal/property/print-template/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/print-template")
public class PrPrintTemplateController extends BaseController {

    private final IPrPrintTemplateService printTemplateService;

    @SaCheckLogin
    @GetMapping("/list")
    public R<List<PrPrintTemplate>> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String name) {
        return R.ok(printTemplateService.listByOrgAndCompany(companyId, orgId, name));
    }

    @SaCheckLogin
    @Log(title = "打印模板", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public R<Void> uploadTemplate(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam String name,
            @RequestParam String templateContent) {
        return toAjax(printTemplateService.saveOrUpdateTemplate(companyId, orgId, name, templateContent));
    }

    @SaCheckLogin
    @GetMapping("/find")
    public R<List<PrPrintTemplate>> findTemplate(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String name) {
        return R.ok(printTemplateService.listByOrgAndCompany(companyId, orgId, name));
    }

    @SaCheckLogin
    @GetMapping("/by-serial")
    public R<PrPrintTemplate> findTemplateBySerialNum(@RequestParam String serialNum) {
        PrPrintTemplate template = printTemplateService.getBySerialNum(serialNum);
        if (template == null) {
            return R.fail("未找到模板");
        }
        return R.ok(template);
    }
}
