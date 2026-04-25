package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/reconciliation")
public class ReconciliationController extends BaseController {

    /** Phase 6: 替换为实际数据库持久化 */
    private static final Map<String, Map<String, Object>> DIFF_STORE = new ConcurrentHashMap<>();
    private static final Map<String, List<Map<String, String>>> BILL_STORE = new ConcurrentHashMap<>();

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/download")
    public R<Map<String, Object>> downloadBill(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate billDate,
            @RequestParam(defaultValue = "ALL") String billType,
            @RequestParam String operator) {
        if (operator == null || operator.isEmpty()) return R.fail("操作人不能为空");
        String billKey = billDate + "_" + billType;
        log.info("下载微信账单: date={}, type={}, operator={}", billDate, billType, operator);

        // Phase 6: 调用微信下载账单 API（/pay/downloadbill）
        // Phase 6: 解析 CSV → List<Map<String,String>>
        // Phase 6: 存储到 reconciliation_bill 表
        List<Map<String, String>> mockRecords = new ArrayList<>();
        BILL_STORE.put(billKey, mockRecords);

        Map<String, Object> result = new HashMap<>();
        result.put("billKey", billKey);
        result.put("date", billDate.toString());
        result.put("type", billType);
        result.put("recordCount", mockRecords.size());
        result.put("status", "downloaded");
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
        String billKey = billDate + "_" + billType;
        List<Map<String, String>> billRecords = BILL_STORE.get(billKey);
        if (billRecords == null) {
            return R.fail("请先下载账单: " + billKey);
        }

        log.info("执行对账: billKey={}, operator={}", billKey, operator);

        // Phase 6: 逐条对比账单记录与 pr_transaction_record
        // Phase 6: 匹配规则 — (out_trade_no, amount, transaction_time)
        // Phase 6: 差异分类 — 本地有/微信无, 微信有/本地无, 金额不一致
        int matchCount = 0;
        int diffCount = 0;
        List<Map<String, Object>> diffs = new ArrayList<>();

        for (Map<String, String> record : billRecords) {
            String outTradeNo = record.get("out_trade_no");
            // Phase 6: query local transaction by outTradeNo
            // Phase 6: compare amounts
            matchCount++;
            String diffId = "DIFF_" + billKey + "_" + outTradeNo;
            Map<String, Object> diff = new HashMap<>();
            diff.put("diffId", diffId);
            diff.put("billKey", billKey);
            diff.put("outTradeNo", outTradeNo);
            diff.put("type", "AMOUNT_MISMATCH");
            diff.put("status", "UNHANDLED");
            DIFF_STORE.put(diffId, diff);
            diffs.add(diff);
            diffCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("billKey", billKey);
        result.put("totalRecords", billRecords.size());
        result.put("matchCount", matchCount);
        result.put("diffCount", diffCount);
        result.put("diffs", diffs);
        return R.ok(result);
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/diffs")
    public R<List<Map<String, Object>>> queryDiffs(@RequestParam String billId) {
        if (billId == null || billId.isEmpty()) return R.fail("账单ID不能为空");
        log.info("查询对账差异: billId={}", billId);
        List<Map<String, Object>> diffs = DIFF_STORE.values().stream()
            .filter(d -> billId.equals(d.get("billKey")))
            .toList();
        return R.ok(diffs);
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

        Map<String, Object> diff = DIFF_STORE.get(diffId);
        if (diff == null) return R.fail("差异记录不存在");
        diff.put("status", "HANDLED");
        diff.put("handleRemark", handleRemark);
        diff.put("handler", handler);
        // Phase 6: 更新 reconciliation_diff 表状态
        return R.ok();
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/unHandleDiffs")
    public R<List<Map<String, Object>>> queryUnHandleDiffs() {
        log.info("查询未处理对账差异");
        List<Map<String, Object>> unhandled = DIFF_STORE.values().stream()
            .filter(d -> "UNHANDLED".equals(d.get("status")))
            .toList();
        return R.ok(unhandled);
    }
}
