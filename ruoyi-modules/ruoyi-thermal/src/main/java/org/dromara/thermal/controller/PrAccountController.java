package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;
import org.dromara.thermal.service.IPrAccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 个人账户管理
 * 迁移自旧系统 PrAccountController
 * 旧端点: /property/prAccount/* -> 新端点: /thermal/property/account/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/account")
public class PrAccountController extends BaseController {

    private final IPrAccountService accountService;

    /**
     * 分页查询个人账户列表
     * 旧端点: POST /property/prAccount/pageList
     * 新端点: GET /thermal/property/account/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<PrAccountBalanceVo>> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode) {
        return R.ok(accountService.pageList(companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode));
    }

    /**
     * 开户
     * 旧端点: POST /property/prAccount/insertData
     * 新端点: POST /thermal/property/account/open
     */
    @SaCheckLogin
    @Log(title = "开户", businessType = BusinessType.INSERT)
    @PostMapping("/open")
    public R<Void> openAccount(@RequestBody List<String> houseIds,
                               @RequestParam String itemGroup,
                               @RequestParam String itemCode,
                               @RequestParam String payment) {
        return toAjax(accountService.insertData(houseIds, itemGroup, itemCode, payment));
    }

    /**
     * 查找没有开户的房屋
     * 旧端点: POST /property/prAccount/noAccount
     * 新端点: GET /thermal/property/account/no-account
     */
    @SaCheckLogin
    @GetMapping("/no-account")
    public R<List<PrAccountBalanceVo>> noAccount(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode) {
        return R.ok(accountService.noAccount(companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode));
    }

    /**
     * 充值查询
     * 旧端点: POST /property/prAccount/getAccount
     * 新端点: GET /thermal/property/account/recharge-query
     */
    @SaCheckLogin
    @GetMapping("/recharge-query")
    public R<List<PrAccountBalanceVo>> getAccount(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String userId) {
        return R.ok(accountService.getAccount(companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode, userId));
    }

    /**
     * 充值操作
     * 旧端点: POST /property/prAccount/updateData
     * 新端点: POST /thermal/property/account/recharge
     */
    @SaCheckLogin
    @Log(title = "充值", businessType = BusinessType.UPDATE)
    @PostMapping("/recharge")
    public R<Void> recharge(@RequestBody List<Map<String, String>> houses,
                            @RequestParam String itemGroup,
                            @RequestParam String itemCode,
                            @RequestParam String payment) {
        return toAjax(accountService.updateData(houses, itemGroup, itemCode, payment));
    }

    /**
     * 账户退费
     * 旧端点: POST /property/prAccount/refundData
     * 新端点: POST /thermal/property/account/refund
     */
    @SaCheckLogin
    @Log(title = "账户退费", businessType = BusinessType.UPDATE)
    @PostMapping("/refund")
    public R<Void> refundData(@RequestBody Map<String, Object> args) {
        @SuppressWarnings("unchecked")
        Map<String, String> houses = (Map<String, String>) args.get("houses");
        @SuppressWarnings("unchecked")
        Map<String, String> record = (Map<String, String>) args.get("record");
        @SuppressWarnings("unchecked")
        Map<String, String> info = (Map<String, String>) args.get("info");
        accountService.refundData(houses, record, info);
        return R.ok();
    }

    /**
     * 转存
     * 旧端点: POST /property/prAccount/transfer
     * 新端点: POST /thermal/property/account/transfer
     */
    @SaCheckLogin
    @Log(title = "转存", businessType = BusinessType.UPDATE)
    @PostMapping("/transfer")
    public R<Void> transfer(@RequestBody List<String> houseIds,
                            @RequestParam String payment,
                            @RequestParam String itemGroup,
                            @RequestParam String itemCode,
                            @RequestParam(required = false) String makeInvoice,
                            @RequestParam(required = false) String invoice) {
        return toAjax(accountService.transfer(houseIds, payment, itemGroup, itemCode, makeInvoice, invoice));
    }

    /**
     * 查询个人账户余额
     * 旧端点: POST /property/prAccount/getPersonAccount
     * 新端点: GET /thermal/property/account/balance
     */
    @SaCheckLogin
    @GetMapping("/balance")
    public R<BigDecimal> getPersonAccount(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam String userId) {
        return R.ok(accountService.getPersonAccount(companyId, orgId, userId));
    }

    /**
     * 查询账户对账单列表
     * 旧端点: POST /property/prAccount/pageAccountStatementList
     * 新端点: GET /thermal/property/account/statement
     */
    @SaCheckLogin
    @GetMapping("/statement")
    public R<List<PrAccountBalanceVo>> statementList(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String searchPhone) {
        return R.ok(accountService.pageAccountStatementList(companyId, orgId, buildingId, unitCode, itemGroup, itemCode, searchPhone));
    }
}
