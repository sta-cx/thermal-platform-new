package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.vo.PrStandardVo;
import org.sdkj.thermal.service.IPrStandardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费标准管理
 * 迁移自旧系统 PrStandardController
 * 旧端点: /property/prStandard/* -> 新端点: /thermal/property/standard/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/standard")
public class PrStandardController extends BaseController {

    private final IPrStandardService standardService;

    /**
     * 分页查询收费标准列表
     * 旧端点: POST /property/prStandard/pageList
     * 新端点: GET /thermal/property/standard/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrStandardVo> list(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String type,
            PageQuery pageQuery) {
        return standardService.selectPageList(orgId, type, pageQuery);
    }

    /**
     * 新增收费标准
     * 旧端点: POST /property/prStandard/insertData
     * 新端点: POST /thermal/property/standard
     */
    @SaCheckLogin
    @Log(title = "收费标准", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody PrStandard standard) {
        return toAjax(standardService.insertData(standard));
    }

    /**
     * 修改收费标准
     * 旧端点: POST /property/prStandard/updateData
     * 新端点: PUT /thermal/property/standard
     */
    @SaCheckLogin
    @Log(title = "收费标准", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrStandard standard) {
        return toAjax(standardService.updateData(standard));
    }

    /**
     * 删除收费标准
     * 旧端点: POST /property/prStandard/deleteData
     * 新端点: DELETE /thermal/property/standard/{id}
     */
    @SaCheckLogin
    @Log(title = "收费标准", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        return toAjax(standardService.deleteData(id));
    }

    /**
     * 查询收费标准详情
     * 旧端点: POST /property/prStandard/queryPrStandard
     * 新端点: GET /thermal/property/standard/{id}
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrStandard> getById(@PathVariable String id) {
        return R.ok(standardService.selectDetailById(id));
    }

    /**
     * 根据 itemCode 查询收费标准
     * 旧端点: POST /property/prStandard/queryPrStandardByItemCode
     * 新端点: GET /thermal/property/standard/by-item-code
     */
    @SaCheckLogin
    @GetMapping("/by-item-code")
    public R<List<PrStandardVo>> getByItemCode(
            @RequestParam String itemCode,
            @RequestParam(required = false) String orgId) {
        return R.ok(standardService.selectByItemCode(itemCode, orgId));
    }

    /**
     * 查询电表收费标准
     * 旧端点: POST /property/prStandard/findEleStandard
     * 新端点: GET /thermal/property/standard/ele
     */
    @SaCheckLogin
    @GetMapping("/ele")
    public R<List<PrStandardVo>> findEle(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId) {
        return R.ok(standardService.findEleStandard(companyId, orgId));
    }

    /**
     * 查询水表收费标准
     * 旧端点: POST /property/prStandard/findWaterStandard
     * 新端点: GET /thermal/property/standard/water
     */
    @SaCheckLogin
    @GetMapping("/water")
    public R<List<PrStandardVo>> findWater(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId) {
        return R.ok(standardService.findWaterStandard(companyId, orgId));
    }

    /**
     * 查询热力收费标准
     * 旧端点: POST /property/prStandard/findHeatStandard
     * 新端点: GET /thermal/property/standard/heat
     */
    @SaCheckLogin
    @GetMapping("/heat")
    public R<List<PrStandardVo>> findHeat(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId) {
        return R.ok(standardService.findHeatStandard(companyId, orgId));
    }

    /**
     * 检查标准名称是否重复
     * 旧端点: POST /property/prStandard/isName
     * 新端点: GET /thermal/property/standard/check-name
     */
    @SaCheckLogin
    @GetMapping("/check-name")
    public R<Boolean> checkName(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String itemGroup,
            @RequestParam String name,
            @RequestParam(required = false) String id) {
        return R.ok(standardService.isName(companyId, orgId, itemGroup, name, id));
    }

    /**
     * 购买限额校验
     * 旧端点: POST /property/prStandard/purchase
     * 新端点: POST /thermal/property/standard/purchase
     */
    @SaCheckLogin
    @PostMapping("/purchase")
    public R<Boolean> purchase(
            @RequestParam String meterId,
            @RequestParam String standardId,
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam BigDecimal num) {
        IPrStandardService.PurchaseCheckResult result = standardService.checkPurchase(meterId, standardId, companyId, orgId, num);
        if (result.exceeded()) {
            return R.fail(result.message());
        }
        return R.ok(false);
    }

    /**
     * 根据标准 ID 查询关联的费用项目
     * 旧端点: POST /property/prStandard/getPrExpenseItemByStandardId
     * 新端点: GET /thermal/property/standard/expense-item
     */
    @SaCheckLogin
    @GetMapping("/expense-item")
    public R<PrExpenseItem> getExpenseItem(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam String standardId) {
        return R.ok(standardService.getExpenseItemByStandardId(companyId, orgId, standardId));
    }

    /**
     * 按项目名称查询收费标准列表
     * 旧端点: POST /property/prStandard/pageListItem
     * 新端点: GET /thermal/property/standard/by-item-name
     */
    @SaCheckLogin
    @GetMapping("/by-item-name")
    public TableDataInfo<PrStandardVo> listByItemName(
            @RequestParam(required = false) String orgIdCopy,
            @RequestParam String itemName,
            PageQuery pageQuery) {
        return standardService.selectByItemName(orgIdCopy, itemName, pageQuery);
    }
}
