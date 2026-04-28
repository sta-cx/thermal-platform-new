package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.service.IPrBillingNotesService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 票据备注管理
 * 迁移自旧系统 PrBillingNotesController
 * 旧端点: /property/prBillingNotes/* -> 新端点: /thermal/property/billing-notes/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/billing-notes")
public class PrBillingNotesController extends BaseController {

    private final IPrBillingNotesService billingNotesService;

    /**
     * 保存票据流水号/备注
     * 旧端点: POST /property/prBillingNotes/saveSerialNum
     * 新端点: POST /thermal/property/billing-notes/serial
     */
    @SaCheckPermission("thermal:property:billingNotes:edit")
    @SaCheckLogin
    @Log(title = "票据备注", businessType = BusinessType.UPDATE)
    @PostMapping("/serial")
    public R<Void> saveSerialNum(
            @RequestParam String serialNum,
            @RequestParam(required = false) String notes) {
        return toAjax(billingNotesService.saveSerialNum(serialNum, notes));
    }

    /**
     * 重开票据
     * 旧端点: POST /property/prBillingNotes/reprint
     * 新端点: POST /thermal/property/billing-notes/reprint
     */
    @SaCheckPermission("thermal:property:billingNotes:edit")
    @SaCheckLogin
    @Log(title = "票据重开", businessType = BusinessType.UPDATE)
    @PostMapping("/reprint")
    public R<Void> reprint(
            @RequestParam String recordId,
            @RequestParam String serialNum) {
        return toAjax(billingNotesService.reprint(recordId, serialNum));
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(billingNotesService.list());
    }
}
