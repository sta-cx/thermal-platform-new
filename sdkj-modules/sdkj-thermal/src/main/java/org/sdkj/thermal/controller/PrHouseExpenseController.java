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
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.bo.PrHouseExpenseBo;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;
import org.sdkj.thermal.domain.vo.PrHouseExpenseVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.service.IPrHouseExpenseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋费用项目绑定管理
 * 迁移自旧系统 PrHouseExpenseController
 * 旧端点: /property/houseExpense/* -> 新端点: /thermal/property/house-expense/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/house-expense")
public class PrHouseExpenseController extends BaseController {

    private final IPrHouseExpenseService houseExpenseService;

    /**
     * 分页查询房屋-费目绑定列表
     * 旧端点: POST /property/houseExpense/pageList
     * 新端点: GET /thermal/property/house-expense/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHouseExpenseVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return houseExpenseService.selectPageList(companyId, orgId, buildingId, unitCode, itemGroup, itemCode, search, pageQuery);
    }

    /**
     * 查询未绑定该费项的房屋
     * 旧端点: POST /property/houseExpense/queryPrHouse
     * 新端点: GET /thermal/property/house-expense/unbound-houses
     */
    @SaCheckLogin
    @GetMapping("/unbound-houses")
    public R<List<PrHouseVo>> queryUnboundHouses(
            @RequestParam String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String search) {
        return R.ok(houseExpenseService.selectUnboundHouses(orgId, buildingId, unitCode, itemGroup, itemCode, search));
    }

    /**
     * 查询单个房屋所有未绑定费项
     * 旧端点: POST /property/houseExpense/queryPrHouseD
     * 新端点: GET /thermal/property/house-expense/unbound-items
     */
    @SaCheckLogin
    @GetMapping("/unbound-items")
    public R<List<PrExpenseItemVo>> queryUnboundItems(
            @RequestParam String orgId,
            @RequestParam String roomNum) {
        return R.ok(houseExpenseService.selectUnboundItems(orgId, roomNum));
    }

    /**
     * 批量保存房屋-费目绑定
     * 旧端点: POST /property/houseExpense/insertData
     * 新端点: POST /thermal/property/house-expense/batch
     */
    @SaCheckLogin
    @Log(title = "房屋费目绑定", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<Void> batchInsert(@RequestBody List<PrHouseExpenseBo> boList,
                               @RequestParam(required = false) String itemGroup,
                               @RequestParam(required = false) String itemCode,
                               @RequestParam(required = false) String orgId,
                               @RequestParam(required = false) String companyId) {
        List<PrHouseExpense> list = MapstructUtils.convert(boList, PrHouseExpense.class);
        return toAjax(houseExpenseService.batchInsert(list, itemGroup, itemCode, orgId, companyId));
    }

    /**
     * 批量修改绑定信息
     * 旧端点: POST /property/houseExpense/updateData
     * 新端点: PUT /thermal/property/house-expense/batch
     */
    @SaCheckLogin
    @Log(title = "房屋费目绑定", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public R<Void> batchUpdate(@RequestBody List<PrHouseExpenseBo> boList) {
        List<PrHouseExpense> list = MapstructUtils.convert(boList, PrHouseExpense.class);
        return toAjax(houseExpenseService.batchUpdate(list));
    }

    /**
     * 批量删除绑定
     * 旧端点: POST /property/houseExpense/deleteHouseExpense
     * 新端点: DELETE /thermal/property/house-expense/batch
     */
    @SaCheckLogin
    @Log(title = "房屋费目绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> batchDelete(@RequestBody List<PrHouseExpenseBo> boList) {
        List<PrHouseExpense> list = MapstructUtils.convert(boList, PrHouseExpense.class);
        return toAjax(houseExpenseService.batchDelete(list));
    }
}
