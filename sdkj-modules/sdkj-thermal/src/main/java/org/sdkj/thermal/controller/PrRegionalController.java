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
    public R<Map<String, Object>> regionalStats(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Map<String, Object> result = new HashMap<>();
        result.put("arrears", transactionService.arrears(orgId));
        result.put("received", transactionService.received(orgId, startTime, endTime));
        result.put("comprehensive", transactionService.comprehensive(orgId, startTime, endTime));
        return R.ok(result);
    }

    @SaCheckPermission("thermal:property:regional:list")
    @SaCheckLogin
    @GetMapping("/daily")
    public R<Map<String, Object>> dailyStats(@RequestParam(required = false) String orgId,
            @RequestParam String date) {
        Map<String, Object> result = new HashMap<>();
        result.put("daily", transactionService.daily(orgId, date));
        return R.ok(result);
    }

    @SaCheckLogin
    @GetMapping("/list")
    public R<?> list() {
        return R.ok(transactionService.list());
    }
}
