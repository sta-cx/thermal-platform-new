package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;
import org.dromara.thermal.service.IPrTransactionRecordService;
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

    @SaCheckLogin
    @GetMapping("/detail/{mainId}")
    public R<List<PrTransactionRecordSubVo>> getDetail(@PathVariable String mainId) {
        return R.ok(transactionRecordService.getDetailByMainId(mainId));
    }

    @SaCheckLogin
    @PutMapping("/revocation")
    public R<Void> revocation(@RequestParam String recordId) {
        return toAjax(transactionRecordService.revocation(recordId));
    }

    @SaCheckLogin
    @PutMapping("/invalid")
    public R<Void> invalid(@RequestParam String recordId) {
        return toAjax(transactionRecordService.invalid(recordId));
    }

    @SaCheckLogin
    @GetMapping("/comprehensive")
    public R<Map<String, Object>> comprehensive(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return R.ok(transactionRecordService.comprehensive(companyId, orgId, startTime, endTime));
    }

    @SaCheckLogin
    @GetMapping("/received")
    public R<Map<String, Object>> received(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return R.ok(transactionRecordService.received(companyId, orgId, startTime, endTime));
    }

    @SaCheckLogin
    @GetMapping("/arrears")
    public R<Map<String, Object>> arrears(
            @RequestParam String companyId,
            @RequestParam String orgId) {
        return R.ok(transactionRecordService.arrears(companyId, orgId));
    }

    @SaCheckLogin
    @GetMapping("/daily")
    public R<List<PrTransactionRecordVo>> daily(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam String date) {
        return R.ok(transactionRecordService.daily(companyId, orgId, date));
    }
}
