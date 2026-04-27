package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrReconciliationDiff;
import org.sdkj.thermal.domain.PrWechatBill;
import org.sdkj.thermal.service.IPrReconciliationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/reconciliation")
public class ReconciliationController extends BaseController {

    private final IPrReconciliationService reconciliationService;

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/download")
    public R<Map<String, Object>> downloadBill(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate billDate,
            @RequestParam(defaultValue = "ALL") String billType,
            @RequestParam String operator) {
        if (operator == null || operator.isEmpty()) return R.fail("操作人不能为空");
        log.info("下载微信账单: date={}, type={}, operator={}", billDate, billType, operator);

        PrWechatBill bill = reconciliationService.downloadBill(billDate, billType, operator);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("billId", bill.getId());
        result.put("billDate", bill.getBillDate());
        result.put("billType", bill.getBillType());
        result.put("downloadStatus", bill.getDownloadStatus());
        result.put("checkStatus", bill.getCheckStatus());
        result.put("operator", bill.getOperator());
        return R.ok(result);
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/reconcile")
    public R<Map<String, Object>> reconcileBill(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate billDate,
            @RequestParam(defaultValue = "ALL") String billType,
            @RequestParam String operator) {
        if (operator == null || operator.isEmpty()) return R.fail("操作人不能为空");
        log.info("执行对账: date={}, type={}, operator={}", billDate, billType, operator);

        Map<String, Object> result = reconciliationService.reconcileBill(billDate, billType, operator);
        return R.ok(result);
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/diffs")
    public R<List<PrReconciliationDiff>> queryDiffs(@RequestParam String billId) {
        if (billId == null || billId.isEmpty()) return R.fail("账单ID不能为空");
        log.info("查询对账差异: billId={}", billId);
        return R.ok(reconciliationService.queryDiffs(billId));
    }

    @SaCheckPermission("thermal:wechat:reconciliation:handle")
    @SaCheckLogin
    @PostMapping("/handleDiff")
    public R<Void> handleDiff(@RequestParam String diffId,
                               @RequestParam String handleRemark,
                               @RequestParam String handler) {
        if (diffId == null || diffId.isEmpty()) return R.fail("差异ID不能为空");
        if (handler == null || handler.isEmpty()) return R.fail("处理人不能为空");
        log.info("处理对账差异: diffId={}, handler={}, remark={}", diffId, handler, handleRemark);

        try {
            reconciliationService.handleDiff(diffId, handleRemark, handler);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/unHandleDiffs")
    public R<List<PrReconciliationDiff>> queryUnHandleDiffs() {
        log.info("查询未处理对账差异");
        return R.ok(reconciliationService.queryUnHandleDiffs());
    }
}
