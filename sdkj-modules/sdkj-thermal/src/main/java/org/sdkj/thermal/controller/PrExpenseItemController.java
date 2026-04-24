package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.bo.PrExpenseItemBo;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;
import org.sdkj.thermal.service.IPrExpenseItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 费用项目管理
 * 迁移自旧系统 PrExpenseItemController
 * 旧端点: /property/expenseItem/* -> 新端点: /thermal/property/expense-item/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/expense-item")
public class PrExpenseItemController extends BaseController {

    private final IPrExpenseItemService expenseItemService;

    /**
     * 分页查询费用项目列表
     * 旧端点: POST /property/expenseItem/pageList
     * 新端点: GET /thermal/property/expense-item/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrExpenseItemVo> list(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String itemGroup,
            PageQuery pageQuery) {
        return expenseItemService.selectPageList(orgId, itemGroup, pageQuery);
    }

    /**
     * 新增费用项目
     * 旧端点: POST /property/expenseItem/insertData
     * 新端点: POST /thermal/property/expense-item
     */
    @SaCheckLogin
    @Log(title = "费用项目", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody PrExpenseItemBo bo) {
        PrExpenseItem item = MapstructUtils.convert(bo, PrExpenseItem.class);
        return toAjax(expenseItemService.insertData(item));
    }

    /**
     * 根据 ID 查询费用项目详情
     * 旧端点: POST /property/expenseItem/querypPrExpenseItem
     * 新端点: GET /thermal/property/expense-item/{id}
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrExpenseItemVo> getById(@PathVariable String id) {
        return R.ok(expenseItemService.selectById(id));
    }

    /**
     * 修改费用项目
     * 旧端点: POST /property/expenseItem/updateData
     * 新端点: PUT /thermal/property/expense-item
     */
    @SaCheckLogin
    @Log(title = "费用项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrExpenseItemBo bo) {
        PrExpenseItem item = MapstructUtils.convert(bo, PrExpenseItem.class);
        return toAjax(expenseItemService.updateById(item));
    }

    /**
     * 按编号查询费用项目
     * 旧端点: POST /property/expenseItem/getDataByItemCode
     * 新端点: GET /thermal/property/expense-item/by-code
     */
    @SaCheckLogin
    @GetMapping("/by-code")
    public R<List<PrExpenseItemVo>> getByItemCode(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode) {
        return R.ok(expenseItemService.selectByItemCode(companyId, orgId, itemGroup, itemCode));
    }

    /**
     * 按费项分组查询列表
     * 旧端点: POST /property/expenseItem/getDataByItemGroup
     * 新端点: GET /thermal/property/expense-item/by-group
     */
    @SaCheckLogin
    @GetMapping("/by-group")
    public R<List<PrExpenseItemVo>> getByItemGroup(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String userId) {
        return R.ok(expenseItemService.selectByItemGroup(companyId, orgId, itemGroup, userId));
    }

    /**
     * 获取费项分组下的 itemCode 列表
     * 旧端点: POST /property/expenseItem/getItemCodesByItemGroup
     * 新端点: GET /thermal/property/expense-item/codes
     */
    @SaCheckLogin
    @GetMapping("/codes")
    public R<List<PrExpenseItemVo>> getItemCodes(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String itemGroup) {
        return R.ok(expenseItemService.getItemCodesByItemGroup(companyId, orgId, itemGroup));
    }

    /**
     * 按公司/小区查询费用项目
     * 旧端点: POST /property/expenseItem/getDataByCompanyIdOrgId
     * 新端点: GET /thermal/property/expense-item/by-org
     */
    @SaCheckLogin
    @GetMapping("/by-org")
    public R<List<PrExpenseItemVo>> getByOrg(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId) {
        return R.ok(expenseItemService.selectByCompanyAndOrg(companyId, orgId));
    }

    /**
     * 删除费用项目
     * 旧端点: POST /property/expenseItem/deleteData
     * 新端点: DELETE /thermal/property/expense-item/{id}
     */
    @SaCheckLogin
    @Log(title = "费用项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id,
                          @RequestParam(required = false) String itemCode,
                          @RequestParam(required = false) String orgId) {
        return toAjax(expenseItemService.deleteData(id, itemCode, orgId));
    }

    /**
     * 检查项目名称是否重复
     * 旧端点: POST /property/expenseItem/isItemName
     * 新端点: GET /thermal/property/expense-item/check-name
     */
    @SaCheckLogin
    @GetMapping("/check-name")
    public R<Boolean> checkName(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam String itemName,
            @RequestParam(required = false) String id) {
        return R.ok(expenseItemService.isItemName(companyId, orgId, itemName, id));
    }
}
