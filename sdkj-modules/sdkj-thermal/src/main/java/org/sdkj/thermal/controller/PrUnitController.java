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
import org.sdkj.thermal.domain.PrUnit;
import org.sdkj.thermal.domain.bo.PrUnitBo;
import org.sdkj.thermal.domain.vo.PrUnitVo;
import org.sdkj.thermal.service.IPrUnitService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单元信息管理
 * 迁移自旧系统 PrUnitController
 * 旧端点: /property/prUnit/* -> 新端点: /thermal/property/unit/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/unit")
public class PrUnitController extends BaseController {

    private final IPrUnitService unitService;

    /**
     * 分页查询单元列表
     * 旧端点: GET /property/prUnit/pageList
     * 新端点: GET /thermal/property/unit/list
     */
    @SaCheckPermission("thermal:property:unit:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrUnitVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String orgId,
            PageQuery pageQuery) {
        return unitService.selectPageList(search, buildingId, orgId, pageQuery);
    }

    /**
     * 查询单元详情
     * 旧端点: GET /property/prUnit/getDataById/{id}
     * 新端点: GET /thermal/property/unit/{id}
     */
    @SaCheckPermission("thermal:property:unit:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrUnitVo> getById(@PathVariable String id) {
        return R.ok(unitService.selectVoById(id));
    }

    /**
     * 新增单元
     * 旧端点: POST /property/prUnit/insertData
     * 新端点: POST /thermal/property/unit
     */
    @SaCheckPermission("thermal:property:unit:add")
    @SaCheckLogin
    @Log(title = "单元信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrUnitBo bo) {
        PrUnit unit = MapstructUtils.convert(bo, PrUnit.class);
        return toAjax(unitService.save(unit));
    }

    /**
     * 修改单元
     * 旧端点: POST /property/prUnit/updateData
     * 新端点: PUT /thermal/property/unit
     */
    @SaCheckPermission("thermal:property:unit:edit")
    @SaCheckLogin
    @Log(title = "单元信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrUnitBo bo) {
        PrUnit unit = MapstructUtils.convert(bo, PrUnit.class);
        return toAjax(unitService.updateById(unit));
    }

    /**
     * 删除单元
     * 旧端点: POST /property/prUnit/deleteData/{id}
     * 新端点: DELETE /thermal/property/unit/{id}
     */
    @SaCheckPermission("thermal:property:unit:remove")
    @SaCheckLogin
    @Log(title = "单元信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(unitService.removeById(id));
    }

    /**
     * 校验单元名称是否重复
     * 旧端点: GET /property/prUnit/validateName
     * 新端点: GET /thermal/property/unit/validate
     */
    @SaCheckPermission("thermal:property:unit:query")
    @SaCheckLogin
    @GetMapping("/validate")
    public R<Boolean> validateName(
            @RequestParam String name,
            @RequestParam(required = false) String id) {
        return R.ok(unitService.validateName(name, id));
    }

    /**
     * 根据楼宇ID查询单元列表
     * 旧端点: GET /property/prUnit/getListByBuildingId
     * 新端点: GET /thermal/property/unit/byBuilding
     */
    @SaCheckPermission("thermal:property:unit:list")
    @SaCheckLogin
    @GetMapping("/byBuilding")
    public R<List<PrUnitVo>> listByBuilding(@RequestParam String buildingId) {
        return R.ok(unitService.selectByBuildingId(buildingId));
    }

}
