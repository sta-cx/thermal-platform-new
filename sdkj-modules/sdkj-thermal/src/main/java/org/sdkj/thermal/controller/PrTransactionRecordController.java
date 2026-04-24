package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.PrTransactionRecordSubVo;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.service.IPrTransactionRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/transaction")
public class PrTransactionRecordController extends BaseController {

    private final IPrTransactionRecordService transactionRecordService;

    @SaCheckPermission("thermal:property:transaction:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrTransactionRecordVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String status,
            PageQuery pageQuery) {
        return transactionRecordService.pageList(
            search, companyId, orgId, buildingId, unitCode, startTime, endTime, status, pageQuery);
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/detail/{mainId}")
    public R<List<PrTransactionRecordSubVo>> getDetail(@PathVariable String mainId) {
        return R.ok(transactionRecordService.getDetailByMainId(mainId));
    }

    @SaCheckPermission("thermal:property:transaction:edit")
    @SaCheckLogin
    @PutMapping("/revocation")
    public R<Void> revocation(@RequestParam String recordId) {
        return toAjax(transactionRecordService.revocation(recordId));
    }

    @SaCheckPermission("thermal:property:transaction:edit")
    @SaCheckLogin
    @PutMapping("/invalid")
    public R<Void> invalid(@RequestParam String recordId) {
        return toAjax(transactionRecordService.invalid(recordId));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/comprehensive")
    public R<Map<String, Object>> comprehensive(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return R.ok(transactionRecordService.comprehensive(companyId, orgId, startTime, endTime));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/received")
    public R<Map<String, Object>> received(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return R.ok(transactionRecordService.received(companyId, orgId, startTime, endTime));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/arrears")
    public R<Map<String, Object>> arrears(
            @RequestParam String companyId,
            @RequestParam String orgId) {
        return R.ok(transactionRecordService.arrears(companyId, orgId));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/daily")
    public R<List<PrTransactionRecordVo>> daily(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam String date) {
        return R.ok(transactionRecordService.daily(companyId, orgId, date));
    }
}
