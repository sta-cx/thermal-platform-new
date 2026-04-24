package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.service.IPrBillingNotesService;
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
    @SaCheckLogin
    @Log(title = "票据重开", businessType = BusinessType.UPDATE)
    @PostMapping("/reprint")
    public R<Void> reprint(
            @RequestParam String recordId,
            @RequestParam String serialNum) {
        return toAjax(billingNotesService.reprint(recordId, serialNum));
    }
}
