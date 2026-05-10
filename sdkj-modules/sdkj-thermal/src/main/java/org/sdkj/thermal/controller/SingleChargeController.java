package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.PrExpenseVo;
import org.sdkj.thermal.service.ISingleChargeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 单笔收费管理
 * 迁移自旧系统 SingleChargeController
 * 旧端点: /property/singleCharge/* -> 新端点: /thermal/property/single-charge/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping({"/thermal/property/single-charge", "/thermal/property/singleCharge"})
public class SingleChargeController extends BaseController {

    private final ISingleChargeService singleChargeService;

    /**
     * 根据手机号或房号获取房屋列表
     * 旧端点: POST /property/singleCharge/getHouse
     * 新端点: GET /thermal/property/single-charge/house
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @GetMapping("/house")
    public R<?> getHouse(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId) {
        return R.ok(singleChargeService.getHouse(search, orgId, buildingId));
    }

    /**
     * 根据房间号获取房屋
     * 旧端点: POST /property/singleCharge/getHouseRoomId
     * 新端点: GET /thermal/property/single-charge/house-room
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @GetMapping("/house-room")
    public R<?> getHouseRoomId(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String roomNum) {
        return R.ok(singleChargeService.getHouseRoomId(search, orgId, buildingId, unitCode, roomNum));
    }

    /**
     * 根据房屋ID查询费用明细
     * 旧端点: GET /property/singleCharge/pageList/{id}
     * 新端点: GET /thermal/property/single-charge/detail/{houseId}
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @GetMapping("/detail/{houseId}")
    public R<?> pageList(@PathVariable String houseId) {
        return R.ok(singleChargeService.pageList(houseId));
    }

    /**
     * 查询明细详情
     * 旧端点: GET /property/singleCharge/getDetail/{houseId}/{code}/{group}/{isFree}
     * 新端点: GET /thermal/property/single-charge/get-detail
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @GetMapping("/get-detail")
    public R<?> getDetail(
            @RequestParam String houseId,
            @RequestParam String code,
            @RequestParam String group,
            @RequestParam(required = false, defaultValue = "0") Integer isFree) {
        return R.ok(singleChargeService.getDetail(houseId, code, group, isFree));
    }

    /**
     * 选择计费周期
     * 旧端点: POST /property/singleCharge/selectCycle/{houseId}/{sums}/{group}/{code}/{isFree}/{index}
     * 新端点: POST /thermal/property/single-charge/select-cycle
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @PostMapping("/select-cycle")
    public R<?> selectCycle(
            @RequestParam String houseId,
            @RequestParam Integer sums,
            @RequestParam String group,
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "0") Integer isFree,
            @RequestParam Integer index,
            @RequestParam String ids) {
        return R.ok(singleChargeService.selectCycle(houseId, sums, group, code, isFree, index, ids));
    }

    /**
     * 选择计费年份
     * 旧端点: POST /property/singleCharge/selectYear/{houseId}/{year}/{index}
     * 新端点: POST /thermal/property/single-charge/select-year
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @PostMapping("/select-year")
    public R<?> selectYear(
            @RequestParam String houseId,
            @RequestParam String year,
            @RequestParam Integer index,
            @RequestParam String id) {
        return R.ok(singleChargeService.selectYear(houseId, year, index, id));
    }

    /**
     * 执行单笔收费
     * 旧端点: POST /property/singleCharge/singleCharge
     * 新端点: POST /thermal/property/single-charge
     */
    @SaCheckPermission("thermal:property:singleCharge:add")
    @SaCheckLogin
    @Log(title = "单笔收费", businessType = BusinessType.INSERT)
    @PostMapping
    public R<?> singleCharge(@RequestBody PrExpenseVo prExpenseVo) {
        return R.ok(singleChargeService.singleCharge(prExpenseVo));
    }

    /**
     * 根据房屋ID查询缴费记录
     * 旧端点: GET /property/singleCharge/queryPayByHouseId/{id}
     * 新端点: GET /thermal/property/single-charge/pay-record/{houseId}
     */
    @SaCheckPermission("thermal:property:singleCharge:query")
    @SaCheckLogin
    @GetMapping("/pay-record/{houseId}")
    public R<?> queryPayByHouseId(@PathVariable String houseId) {
        return R.ok(singleChargeService.queryPayByHouseId(houseId));
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(java.util.Collections.emptyList());
    }
}
