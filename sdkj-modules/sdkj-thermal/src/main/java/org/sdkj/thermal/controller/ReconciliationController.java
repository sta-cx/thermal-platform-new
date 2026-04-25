package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/reconciliation")
public class ReconciliationController extends BaseController {

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/download")
    public R<Void> downloadBill(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate billDate,
            @RequestParam(defaultValue = "ALL") String billType,
            @RequestParam String operator) {
        // TODO: Phase 5d - 下载微信账单并存储
        return R.fail("对账下载待实现");
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/reconcile")
    public R<Map<String, Object>> reconcileBill(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate billDate,
            @RequestParam(defaultValue = "ALL") String billType,
            @RequestParam String operator) {
        // TODO: Phase 5d - 执行对账比对
        return R.fail("对账功能待实现");
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/diffs")
    public R<Void> queryDiffs(@RequestParam String billId) {
        // TODO: Phase 5d - 查询对账差异
        return R.fail("差异查询待实现");
    }

    @SaCheckPermission("thermal:wechat:reconciliation:handle")
    @SaCheckLogin
    @PostMapping("/handleDiff")
    public R<Void> handleDiff(@RequestParam String diffId,
                               @RequestParam String handleRemark,
                               @RequestParam String handler) {
        // TODO: Phase 5d - 处理对账差异
        return R.fail("差异处理待实现");
    }

    @SaCheckPermission("thermal:wechat:reconciliation:list")
    @SaCheckLogin
    @GetMapping("/unHandleDiffs")
    public R<Void> queryUnHandleDiffs() {
        // TODO: Phase 5d - 查询未处理的差异
        return R.fail("差异查询待实现");
    }
}
