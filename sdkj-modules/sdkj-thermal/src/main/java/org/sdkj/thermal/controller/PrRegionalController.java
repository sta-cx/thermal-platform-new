package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.service.IPrTransactionRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/regional")
public class PrRegionalController extends BaseController {

    private final IPrTransactionRecordService transactionService;

    @SaCheckPermission("thermal:property:regional:list")
    @SaCheckLogin
    @GetMapping("/stats")
    public R<Map<String, Object>> regionalStats(
            @RequestParam String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Map<String, Object> result = new HashMap<>();
        result.put("arrears", transactionService.arrears(companyId, orgId));
        result.put("received", transactionService.received(companyId, orgId, startTime, endTime));
        result.put("comprehensive", transactionService.comprehensive(companyId, orgId, startTime, endTime));
        return R.ok(result);
    }

    @SaCheckPermission("thermal:property:regional:list")
    @SaCheckLogin
    @GetMapping("/daily")
    public R<Map<String, Object>> dailyStats(
            @RequestParam String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam String date) {
        Map<String, Object> result = new HashMap<>();
        result.put("daily", transactionService.daily(companyId, orgId, date));
        return R.ok(result);
    }
}
