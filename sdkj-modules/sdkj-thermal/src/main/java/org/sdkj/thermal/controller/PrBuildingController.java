package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.bo.PrBuildingBo;
import org.sdkj.thermal.domain.vo.PrBuildingVo;
import org.sdkj.thermal.service.IPrBuildingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼宇信息管理
 * 迁移自旧系统 PrBuildingController
 * 旧端点: /property/prBuilding/* -> 新端点: /thermal/property/building/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/building")
public class PrBuildingController extends BaseController {

    private final IPrBuildingService buildingService;

    /**
     * 分页查询楼宇列表
     * 旧端点: GET /property/prBuilding/pageList
     * 新端点: GET /thermal/property/building/list
     */
    @SaCheckPermission("thermal:property:building:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrBuildingVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String used,
            @RequestParam(required = false) String orgId,
            PageQuery pageQuery) {
        return buildingService.selectPageList(search, used, orgId, pageQuery);
    }

    /**
     * 查询楼宇详情
     * 旧端点: GET /property/prBuilding/getDataById/{id}
     * 新端点: GET /thermal/property/building/{id}
     */
    @SaCheckPermission("thermal:property:building:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrBuildingVo> getById(@PathVariable String id) {
        return R.ok(buildingService.selectById(id));
    }

    /**
     * 新增楼宇
     * 旧端点: POST /property/prBuilding/insertData
     * 新端点: POST /thermal/property/building
     */
    @SaCheckPermission("thermal:property:building:add")
    @SaCheckLogin
    @Log(title = "楼宇信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrBuildingBo bo) {
        PrBuilding building = MapstructUtils.convert(bo, PrBuilding.class);
        return toAjax(buildingService.save(building));
    }

    /**
     * 修改楼宇
     * 旧端点: POST /property/prBuilding/updateData
     * 新端点: PUT /thermal/property/building
     */
    @SaCheckPermission("thermal:property:building:edit")
    @SaCheckLogin
    @Log(title = "楼宇信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrBuildingBo bo) {
        PrBuilding building = MapstructUtils.convert(bo, PrBuilding.class);
        return toAjax(buildingService.updateById(building));
    }

    /**
     * 删除楼宇
     * 旧端点: POST /property/prBuilding/deleteData/{id}
     * 新端点: DELETE /thermal/property/building/{id}
     */
    @SaCheckPermission("thermal:property:building:remove")
    @SaCheckLogin
    @Log(title = "楼宇信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(buildingService.removeById(id));
    }

    /**
     * 校验楼宇名称是否重复
     * 旧端点: GET /property/prBuilding/validateName
     * 新端点: GET /thermal/property/building/validate
     */
    @SaCheckPermission("thermal:property:building:query")
    @SaCheckLogin
    @GetMapping("/validate")
    public R<Boolean> validateName(
            @RequestParam String name,
            @RequestParam(required = false) String id) {
        return R.ok(buildingService.validateName(name, id));
    }

    /**
     * 获取楼宇用途字典值
     * 旧端点: GET /property/prBuilding/getUsed
     * 新端点: GET /thermal/property/building/used
     */
    @SaCheckPermission("thermal:property:building:list")
    @SaCheckLogin
    @GetMapping("/used")
    public R<List<String>> getUsed() {
        // 返回固定用途字典值
        return R.ok(List.of("住宅", "商用", "办公", "综合"));
    }

    /**
     * 根据小区查询楼宇列表
     * 旧端点: GET /property/prBuilding/getBuildingByCompanyIdOrgId
     * 新端点: GET /thermal/property/building/byOrg
     */
    @SaCheckPermission("thermal:property:building:list")
    @SaCheckLogin
    @GetMapping("/byOrg")
    public R<List<PrBuildingVo>> listByOrg(@RequestParam(required = false) String orgId) {
        return R.ok(buildingService.selectByOrgId(orgId));
    }

    /**
     * 根据楼宇ID获取单元编码列表
     * 旧端点: GET /property/prBuilding/getUnitCodesByBuildingId
     * 新端点: GET /thermal/property/building/unitCodes
     */
    @SaCheckPermission("thermal:property:building:list")
    @SaCheckLogin
    @GetMapping("/unitCodes")
    public R<List<String>> getUnitCodes(@RequestParam String buildingId) {
        return R.ok(buildingService.selectUnitCodesByBuildingId(buildingId));
    }

    /**
     * 根据单元编码获取房间号列表
     * 旧端点: GET /property/prBuilding/getRoomNumsByUnitCode
     * 新端点: GET /thermal/property/building/roomNums
     */
    @SaCheckPermission("thermal:property:building:list")
    @SaCheckLogin
    @GetMapping("/roomNums")
    public R<List<String>> getRoomNums(@RequestParam String unitCode) {
        return R.ok(buildingService.selectRoomNumsByUnitCode(unitCode));
    }

    /**
     * 根据热力站查询楼宇列表
     * 旧端点: GET /property/prBuilding/getBuildingByStationId
     * 新端点: GET /thermal/property/building/byStation
     */
    @SaCheckPermission("thermal:property:building:list")
    @SaCheckLogin
    @GetMapping("/byStation")
    public R<List<PrBuildingVo>> listByStation(@RequestParam String stationId) {
        return R.ok(buildingService.selectByStationId(stationId));
    }

}
