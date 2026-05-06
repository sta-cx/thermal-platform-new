package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.bo.RefundDataBo;
import org.sdkj.thermal.domain.vo.PrAccountBalanceVo;
import org.sdkj.thermal.service.IPrAccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
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
    @SaCheckPermission("thermal:property:account:list")
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
    @SaCheckPermission("thermal:property:account:add")
    @SaCheckLogin
    @Log(title = "开户", businessType = BusinessType.INSERT)
    @PostMapping("/open")
    public R<Void> openAccount(@RequestBody List<Long> houseIds,
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
    @SaCheckPermission("thermal:property:account:list")
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
    @SaCheckPermission("thermal:property:account:query")
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
    @SaCheckPermission("thermal:property:account:edit")
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
    @SaCheckPermission("thermal:property:account:edit")
    @SaCheckLogin
    @Log(title = "账户退费", businessType = BusinessType.UPDATE)
    @PostMapping("/refund")
    public R<Void> refundData(@Validated @RequestBody RefundDataBo bo) {
        accountService.refundData(bo);
        return R.ok();
    }

    /**
     * 转存
     * 旧端点: POST /property/prAccount/transfer
     * 新端点: POST /thermal/property/account/transfer
     */
    @SaCheckPermission("thermal:property:account:edit")
    @SaCheckLogin
    @Log(title = "转存", businessType = BusinessType.UPDATE)
    @PostMapping("/transfer")
    public R<Void> transfer(@RequestBody List<Long> houseIds,
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
    @SaCheckPermission("thermal:property:account:query")
    @SaCheckLogin
    @GetMapping("/balance")
    public R<BigDecimal> getPersonAccount(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam Long userId) {
        return R.ok(accountService.getPersonAccount(companyId, orgId, userId));
    }

    /**
     * 查询账户对账单列表
     * 旧端点: POST /property/prAccount/pageAccountStatementList
     * 新端点: GET /thermal/property/account/statement
     */
    @SaCheckPermission("thermal:property:account:list")
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

    /**
     * 查询房屋押金信息
     * 旧端点: POST /property/prAccount/getHouseDeposit
     * 新端点: GET /thermal/property/account/deposit
     */
    @SaCheckPermission("thermal:property:account:query")
    @SaCheckLogin
    @GetMapping("/deposit")
    public R<Map<String, Object>> getHouseDeposit(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search) {
        return R.ok(accountService.getHouseDeposit(companyId, orgId, buildingId, unitCode, search));
    }

    /**
     * 保存押金缴费
     * 旧端点: POST /property/prAccount/saveDeposit
     * 新端点: POST /thermal/property/account/deposit
     */
    @SaCheckPermission("thermal:property:account:edit")
    @SaCheckLogin
    @Log(title = "押金缴费", businessType = BusinessType.INSERT)
    @PostMapping("/deposit")
    public R<Map<String, Object>> saveDeposit(@RequestBody Map<String, Object> depositVo) {
        return R.ok(accountService.saveDeposit(depositVo));
    }

    /**
     * 导入数据预览（含校验）
     * 旧端点: POST /property/prAccount/pageListImportData
     * 新端点: GET /thermal/property/account/import-preview
     */
    @SaCheckPermission("thermal:property:account:list")
    @SaCheckLogin
    @GetMapping("/import-preview")
    public R<Map<String, Object>> pageListImportData(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(accountService.pageListImportData(pageNum, pageSize));
    }

    /**
     * 导入Excel数据
     * 旧端点: POST /property/prAccount/importData
     * 新端点: POST /thermal/property/account/import
     */
    @SaCheckPermission("thermal:property:account:add")
    @SaCheckLogin
    @Log(title = "账户数据导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Map<String, Object>> importData(@RequestParam("file") MultipartFile file) {
        return R.ok(accountService.importExcelData(file));
    }

    /**
     * 删除导入的临时数据
     * 旧端点: POST /property/prAccount/deleteImportData
     * 新端点: DELETE /thermal/property/account/import
     */
    @SaCheckPermission("thermal:property:account:remove")
    @SaCheckLogin
    @Log(title = "删除导入数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/import")
    public R<Void> deleteImportData() {
        return toAjax(accountService.deleteImportData());
    }

    /**
     * 下载押金导入模板
     * 旧端点: POST /property/prAccount/downloadExcel
     * 新端点: GET /thermal/property/account/import-template
     */
    @SaCheckPermission("thermal:property:account:list")
    @SaCheckLogin
    @GetMapping("/import-template")
    public R<String> downloadExcel() {
        return R.ok("请使用 /thermal/property/account/import 端点上传Excel文件，模板列: 小区/楼宇/房号/业主名称/手机号/押金名称/金额");
    }
}
