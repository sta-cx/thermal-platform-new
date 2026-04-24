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
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.bo.PrHouseBo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.service.IPrHouseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 房屋信息管理
 * 迁移自旧系统 PrHouseController
 * 旧端点: /property/prHouse/* -> 新端点: /thermal/property/house/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/house")
public class PrHouseController extends BaseController {

    private final IPrHouseService houseService;

    /**
     * 分页查询房屋列表
     * 旧端点: GET /property/prHouse/pageList
     * 新端点: GET /thermal/property/house/list
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHouseVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String companyId,
            PageQuery pageQuery) {
        return houseService.selectPageList(search, buildingId, orgId, status, companyId, pageQuery);
    }

    /**
     * 查询房屋详情
     * 旧端点: GET /property/prHouse/getDataById/{id}
     * 新端点: GET /thermal/property/house/{id}
     */
    @SaCheckPermission("thermal:property:house:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHouseVo> getById(@PathVariable String id) {
        return R.ok(houseService.selectById(id));
    }

    /**
     * 新增房屋
     * 旧端点: POST /property/prHouse/insertData
     * 新端点: POST /thermal/property/house
     */
    @SaCheckPermission("thermal:property:house:add")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHouseBo bo) {
        PrHouse house = MapstructUtils.convert(bo, PrHouse.class);
        return toAjax(houseService.save(house));
    }

    /**
     * 修改房屋
     * 旧端点: POST /property/prHouse/updateData
     * 新端点: PUT /thermal/property/house
     */
    @SaCheckPermission("thermal:property:house:edit")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHouseBo bo) {
        PrHouse house = MapstructUtils.convert(bo, PrHouse.class);
        return toAjax(houseService.updateById(house));
    }

    /**
     * 删除房屋
     * 旧端点: POST /property/prHouse/deleteData/{id}
     * 新端点: DELETE /thermal/property/house/{id}
     */
    @SaCheckPermission("thermal:property:house:remove")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(houseService.removeById(id));
    }

    /**
     * 批量删除房屋
     * 旧端点: POST /property/prHouse/deleteMulData
     * 新端点: DELETE /thermal/property/house/batch
     */
    @SaCheckPermission("thermal:property:house:remove")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestBody List<String> ids) {
        return toAjax(houseService.removeByIds(ids));
    }

    /**
     * 校验房间号是否唯一
     * 旧端点: GET /property/prHouse/validate
     * 新端点: GET /thermal/property/house/validate
     */
    @SaCheckPermission("thermal:property:house:query")
    @SaCheckLogin
    @GetMapping("/validate")
    public R<Boolean> validate(
            @RequestParam String roomNum,
            @RequestParam String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String id) {
        return R.ok(houseService.validateRoomNum(roomNum, buildingId, unitCode, id));
    }

    /**
     * 根据楼宇+单元查询房屋列表
     * 旧端点: GET /property/prHouse/getDataByBuildingUnit
     * 新端点: GET /thermal/property/house/byUnit
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/byUnit")
    public R<List<PrHouseVo>> listByUnit(
            @RequestParam String buildingId,
            @RequestParam(required = false) String unitCode) {
        return R.ok(houseService.selectByUnit(buildingId, unitCode));
    }

    /**
     * 根据小区查询房屋列表
     * 旧端点: GET /property/prHouse/getDataByOrgId
     * 新端点: GET /thermal/property/house/byOrg
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/byOrg")
    public R<List<PrHouseVo>> listByOrg(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId) {
        return R.ok(houseService.selectByOrg(companyId, orgId));
    }

    /**
     * 查询用户关联的房屋数量
     * 旧端点: GET /property/prHouse/countByUserId
     * 新端点: GET /thermal/property/house/count
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/count")
    public R<Long> countByUser(@RequestParam String userId) {
        return R.ok(houseService.countByUser(userId));
    }

    /**
     * 查询用户关联的房屋总面积
     * 旧端点: GET /property/prHouse/areaByUserId
     * 新端点: GET /thermal/property/house/area
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/area")
    public R<BigDecimal> areaByUser(@RequestParam String userId) {
        return R.ok(houseService.areaByUser(userId));
    }
}
