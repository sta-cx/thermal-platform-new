package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.PmParkingSpace;
import org.sdkj.thermal.domain.bo.PrExpenseBo;
import org.sdkj.thermal.domain.bo.PrHouseExpenseBo;
import org.sdkj.thermal.domain.bo.PmParkingSpaceBo;
import org.sdkj.thermal.domain.vo.PrExpenseVo;
import org.sdkj.thermal.service.IPrExpenseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 费用明细管理
 * 迁移自旧系统 PrExpenseController
 * 旧端点: /property/expense/* -> 新端点: /thermal/property/expense/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/expense")
public class PrExpenseController extends BaseController {

    private final IPrExpenseService expenseService;

    /**
     * 分页查询费用明细列表
     * 旧端点: POST /property/expense/pageList
     * 新端点: GET /thermal/property/expense/list
     */
    @SaCheckPermission("thermal:property:expense:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrExpenseVo> list(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String isCharged,
            @RequestParam(required = false) String parkingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            PageQuery pageQuery) {
        return expenseService.selectPageList(orgId, buildingId, unitCode, itemGroup, itemCode,
            search, isCharged, parkingId, startTime, endTime, startDate, endDate, pageQuery);
    }

    /**
     * 根据ID查询费用详情
     */
    @SaCheckPermission("thermal:property:expense:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrExpense> getInfo(@PathVariable String id) {
        return R.ok(expenseService.getById(id));
    }

    /**
     * 查询房屋费用明细列表
     * 旧端点: POST /property/expense/queryHouseExpenseList
     * 新端点: GET /thermal/property/expense/house-list
     */
    @SaCheckPermission("thermal:property:expense:list")
    @SaCheckLogin
    @GetMapping("/house-list")
    public R<List<PrExpenseVo>> queryHouseExpenseList(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String search) {
        return R.ok(expenseService.selectHouseExpenseList(orgId, buildingId, unitCode, itemGroup, itemCode, search));
    }

    /**
     * 查询房屋取暖费明细
     * 旧端点: POST /property/expense/queryHeatExpenseByHouseId
     * 新端点: GET /thermal/property/expense/heat/{houseId}
     */
    @SaCheckPermission("thermal:property:expense:query")
    @SaCheckLogin
    @GetMapping("/heat/{houseId}")
    public R<PrExpenseVo> queryHeatExpense(@PathVariable Long houseId) {
        return R.ok(expenseService.selectHeatExpenseByHouseId(houseId));
    }

    /**
     * 生成取暖费明细
     * 旧端点: POST /property/expense/insertData
     * 新端点: POST /thermal/property/expense/heat
     */
    @SaCheckPermission("thermal:property:expense:add")
    @SaCheckLogin
    @Log(title = "取暖费生成", businessType = BusinessType.INSERT)
    @PostMapping("/heat")
    public R<Void> insertHeat(@RequestBody List<PrHouseExpenseBo> boList) {
        List<PrHouseExpense> list = MapstructUtils.convert(boList, PrHouseExpense.class);
        return toAjax(expenseService.insertData(list));
    }

    /**
     * 批量生成费用明细
     * 旧端点: POST /property/expense/insertDatall
     * 新端点: POST /thermal/property/expense/batch
     */
    @SaCheckPermission("thermal:property:expense:add")
    @SaCheckLogin
    @Log(title = "批量生成费用", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<Void> insertBatch(@RequestBody List<PrHouseExpenseBo> boList) {
        List<PrHouseExpense> list = MapstructUtils.convert(boList, PrHouseExpense.class);
        return toAjax(expenseService.insertDatall(list));
    }

    /**
     * 生成临时费用明细
     * 旧端点: POST /property/expense/insertDataLs
     * 新端点: POST /thermal/property/expense/temporary
     */
    @SaCheckPermission("thermal:property:expense:add")
    @SaCheckLogin
    @Log(title = "临时费用生成", businessType = BusinessType.INSERT)
    @PostMapping("/temporary")
    public R<Void> insertTemporary(@RequestBody List<PrHouseExpenseBo> boList) {
        List<PrHouseExpense> list = MapstructUtils.convert(boList, PrHouseExpense.class);
        return toAjax(expenseService.insertDataLs(list));
    }

    /**
     * 批量生成车位费用明细
     * 旧端点: POST /property/expense/insertDatallCw
     * 新端点: POST /thermal/property/expense/parking
     */
    @SaCheckPermission("thermal:property:expense:add")
    @SaCheckLogin
    @Log(title = "车位费用生成", businessType = BusinessType.INSERT)
    @PostMapping("/parking")
    public R<Void> insertParking(@RequestBody List<PmParkingSpaceBo> boList) {
        List<PmParkingSpace> list = MapstructUtils.convert(boList, PmParkingSpace.class);
        return toAjax(expenseService.insertDatallCw(list));
    }

    /**
     * 设置优惠
     * 旧端点: POST /property/expense/setPreferential
     * 新端点: PUT /thermal/property/expense/preferential
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用优惠", businessType = BusinessType.UPDATE)
    @PutMapping("/preferential")
    public R<Void> setPreferential(@RequestBody List<PrExpenseBo> boList,
                                   @RequestParam String type,
                                   @RequestParam(required = false) String scale,
                                   @RequestParam(required = false) String price,
                                   @RequestParam(required = false) String reason,
                                   @RequestParam(required = false) String times) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.setPreferential(list, type, scale, price, reason, times));
    }

    /**
     * 设置免收
     * 旧端点: POST /property/expense/setIsFree
     * 新端点: PUT /thermal/property/expense/free
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用免收", businessType = BusinessType.UPDATE)
    @PutMapping("/free")
    public R<Void> setIsFree(@RequestBody List<PrExpenseBo> boList,
                             @RequestParam String reason,
                             @RequestParam String times) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.setIsFree(list, reason, times));
    }

    /**
     * 设置延期
     * 旧端点: POST /property/expense/setLastDate
     * 新端点: PUT /thermal/property/expense/delay
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用延期", businessType = BusinessType.UPDATE)
    @PutMapping("/delay")
    public R<Void> setLastDate(@RequestBody List<PrExpenseBo> boList,
                               @RequestParam String reason,
                               @RequestParam String days) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.setLastDate(list, reason, days));
    }

    /**
     * 设置报停
     * 旧端点: POST /property/expense/setBaotingDate
     * 新端点: PUT /thermal/property/expense/suspend
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用报停", businessType = BusinessType.UPDATE)
    @PutMapping("/suspend")
    public R<Void> setBaotingDate(@RequestBody List<PrExpenseBo> boList,
                                  @RequestParam String type,
                                  @RequestParam(required = false) String scale,
                                  @RequestParam(required = false) String price,
                                  @RequestParam(required = false) String reason) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.setBaotingDate(list, type, scale, price, reason));
    }

    /**
     * 设置复供
     * 旧端点: POST /property/expense/setFugongDate
     * 新端点: PUT /thermal/property/expense/resume
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用复供", businessType = BusinessType.UPDATE)
    @PutMapping("/resume")
    public R<Void> setFugongDate(@RequestBody List<PrExpenseBo> boList,
                                 @RequestParam String reason,
                                 @RequestParam String days) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.setFugongDate(list, reason, days));
    }

    /**
     * 设置退费
     * 旧端点: POST /property/expense/setTuifei
     * 新端点: PUT /thermal/property/expense/refund
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用退费", businessType = BusinessType.UPDATE)
    @PutMapping("/refund")
    public R<Void> setTuifei(@RequestBody List<PrExpenseBo> boList,
                             @RequestParam String type,
                             @RequestParam(required = false) String scale,
                             @RequestParam(required = false) String price,
                             @RequestParam(required = false) String reason) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.setTuifei(list, type, scale, price, reason));
    }

    /**
     * 删除费用明细
     * 旧端点: POST /property/expense/deleteDate
     * 新端点: DELETE /thermal/property/expense/batch
     */
    @SaCheckPermission("thermal:property:expense:remove")
    @SaCheckLogin
    @Log(title = "费用明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<PrExpenseBo> boList) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.deleteDate(list));
    }

    /**
     * 修改费用明细标准
     * 旧端点: POST /property/expense/updateDatall
     * 新端点: PUT /thermal/property/expense/standard
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @Log(title = "费用标准", businessType = BusinessType.UPDATE)
    @PutMapping("/standard")
    public R<Void> updateStandard(@RequestBody List<PrExpenseBo> boList) {
        List<PrExpense> list = MapstructUtils.convert(boList, PrExpense.class);
        return toAjax(expenseService.updateDatall(list));
    }

    /**
     * 重新计算费用明细
     * 旧端点: POST /property/expense/recalculate
     * 新端点: POST /thermal/property/expense/recalculate
     */
    @SaCheckPermission("thermal:property:expense:recalculate")
    @SaCheckLogin
    @Log(title = "费用重算", businessType = BusinessType.UPDATE)
    @PostMapping("/recalculate")
    public R<Void> recalculate(@RequestParam String orgId) {
        return toAjax(expenseService.recalculate(orgId));
    }

    /**
     * 重新计算车位费用明细
     * 旧端点: POST /property/expense/recalculateCw
     * 新端点: POST /thermal/property/expense/recalculate-parking
     */
    @SaCheckPermission("thermal:property:expense:recalculate")
    @SaCheckLogin
    @Log(title = "车位费用重算", businessType = BusinessType.UPDATE)
    @PostMapping("/recalculate-parking")
    public R<Void> recalculateParking(@RequestParam String orgId) {
        return toAjax(expenseService.recalculateCw(orgId));
    }

    /**
     * 设置计算状态
     * 旧端点: POST /property/expense/setCalStatus
     * 新端点: PUT /thermal/property/expense/calc-status
     */
    @SaCheckPermission("thermal:property:expense:edit")
    @SaCheckLogin
    @PutMapping("/calc-status")
    public R<Void> setCalStatus(@RequestParam Long houseId) {
        return toAjax(expenseService.setCalStatus(houseId));
    }

    /**
     * 车位费用分页查询
     * 旧端点: POST /property/expense/pageListCw
     * 新端点: GET /thermal/property/expense/parking-list
     */
    @SaCheckPermission("thermal:property:expense:list")
    @SaCheckLogin
    @GetMapping("/parking-list")
    public TableDataInfo<PrExpenseVo> parkingList(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String isCharged,
            @RequestParam(required = false) String parkingId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            PageQuery pageQuery) {
        return expenseService.parkingList(orgId, buildingId, unitCode, itemGroup, itemCode,
            search, isCharged, parkingId, startTime, endTime, pageQuery);
    }

    /**
     * 车位空间费用查询
     * 旧端点: POST /property/expense/queryParkinglotExpenseList
     * 新端点: GET /thermal/property/expense/parking-expense
     */
    @SaCheckPermission("thermal:property:expense:query")
    @SaCheckLogin
    @GetMapping("/parking-expense")
    public R<List<Map<String, Object>>> parkingExpenseList(@RequestParam String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String itemGroup,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String parkingId,
            @RequestParam(required = false) String search) {
        return R.ok(expenseService.parkingExpenseList(orgId, buildingId, unitCode,
            itemGroup, itemCode, parkingId, search));
    }

    /**
     * 全部房屋费用查询
     * 旧端点: POST /property/expense/queryHouseExpenseAllList
     * 新端点: GET /thermal/property/expense/house-expense-all
     */
    @SaCheckPermission("thermal:property:expense:query")
    @SaCheckLogin
    @GetMapping("/house-expense-all")
    public R<List<Map<String, Object>>> houseExpenseAllList(@RequestParam String orgId,
            @RequestParam(required = false) String search) {
        return R.ok(expenseService.houseExpenseAllList(orgId, search));
    }

    /**
     * 费用操作日志
     * 旧端点: POST /property/expense/pageListLog
     * 新端点: GET /thermal/property/expense/log
     */
    @SaCheckPermission("thermal:property:expense:list")
    @SaCheckLogin
    @GetMapping("/log")
    public TableDataInfo<Map<String, Object>> expenseLog(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return expenseService.expenseLog(orgId, buildingId, unitCode,
            parentId, type, startTime, endTime, search, pageQuery);
    }
}
