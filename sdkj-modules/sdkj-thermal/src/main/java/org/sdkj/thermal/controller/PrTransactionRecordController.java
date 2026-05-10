package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrTransactionRecord;
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

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrTransactionRecord> getInfo(@PathVariable String id) {
        return R.ok(transactionRecordService.getById(id));
    }

    @SaCheckPermission("thermal:property:transaction:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrTransactionRecordVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String status,
            PageQuery pageQuery) {
        return transactionRecordService.pageList(
            search, orgId, buildingId, unitCode, startTime, endTime, status, pageQuery);
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
    public R<Map<String, Object>> comprehensive(@RequestParam String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return R.ok(transactionRecordService.comprehensive(orgId, startTime, endTime));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/received")
    public R<Map<String, Object>> received(@RequestParam String orgId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return R.ok(transactionRecordService.received(orgId, startTime, endTime));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/arrears")
    public R<Map<String, Object>> arrears(@RequestParam String orgId) {
        return R.ok(transactionRecordService.arrears(orgId));
    }

    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/daily")
    public R<List<PrTransactionRecordVo>> daily(@RequestParam String orgId,
            @RequestParam String date) {
        return R.ok(transactionRecordService.daily(orgId, date));
    }

    /**
     * 退费记录查询
     * 旧端点: POST /property/prTransactionRecord/refund
     * 新端点: GET /thermal/property/transaction/refund
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/refund")
    public R<Map<String, Object>> refund(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search) {
        return R.ok(transactionRecordService.refund(orgId, buildingId, startTime, endTime, search));
    }

    /**
     * 水费交易查询
     * 旧端点: POST /property/prTransactionRecord/getWater
     * 新端点: GET /thermal/property/transaction/water
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/water")
    public R<Map<String, Object>> getWater(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search) {
        return R.ok(transactionRecordService.getWater(orgId, buildingId, startTime, endTime, search));
    }

    /**
     * 电费交易查询
     * 旧端点: POST /property/prTransactionRecord/getEle
     * 新端点: GET /thermal/property/transaction/ele
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/ele")
    public R<Map<String, Object>> getEle(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search) {
        return R.ok(transactionRecordService.getEle(orgId, buildingId, startTime, endTime, search));
    }

    /**
     * 写卡日志查询
     * 旧端点: POST /property/prTransactionRecord/cardLog
     * 新端点: GET /thermal/property/transaction/card-log
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/card-log")
    public R<Map<String, Object>> cardLog(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String createBy) {
        return R.ok(transactionRecordService.cardLog(orgId, buildingId, startTime, endTime, search, type, createBy));
    }

    /**
     * 写卡操作人列表
     * 旧端点: POST /property/prTransactionRecord/getCardLogCreateByName
     * 新端点: GET /thermal/property/transaction/card-log-operators
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/card-log-operators")
    public R<List<Map<String, Object>>> cardLogCreateByName(@RequestParam(required = false) String orgId) {
        return R.ok(transactionRecordService.cardLogCreateByName(orgId));
    }

    /**
     * 未收款查询
     * 旧端点: POST /property/prTransactionRecord/uncoll
     * 新端点: GET /thermal/property/transaction/uncoll
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/uncoll")
    public R<Map<String, Object>> uncoll(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search) {
        return R.ok(transactionRecordService.uncoll(orgId, buildingId, startTime, endTime, search));
    }

    /**
     * 本月收款统计
     * 旧端点: POST /property/prTransactionRecord/getThisMonth
     * 新端点: GET /thermal/property/transaction/monthly
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/monthly")
    public R<Map<String, Object>> getThisMonth(@RequestParam(required = false) String userId) {
        return R.ok(transactionRecordService.getThisMonth(userId));
    }

    /**
     * 本月分类统计
     * 旧端点: POST /property/prTransactionRecord/getThisMonthVarious
     * 新端点: GET /thermal/property/transaction/monthly-various
     */
    @SaCheckPermission("thermal:property:transaction:query")
    @SaCheckLogin
    @GetMapping("/monthly-various")
    public R<Map<String, Object>> getThisMonthVarious(@RequestParam(required = false) String userId) {
        return R.ok(transactionRecordService.getThisMonthVarious(userId));
    }
}
