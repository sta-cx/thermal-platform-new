package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.bo.PrHeatUnitHotArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.service.IPrHeatUnitHotArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 单元热表配表管理
 * 迁移自旧系统 PrHeatUnitHotArchiveController
 * 旧端点: /ht/unitHotArchive/* -> 新端点: /thermal/ht/unit-hot-archive/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/unit-hot-archive")
public class PrHeatUnitHotArchiveController extends BaseController {

    private final IPrHeatUnitHotArchiveService unitHotArchiveService;

    /**
     * 分页查询单元热表配表列表
     * 旧端点: GET /ht/unitHotArchive/pageList
     * 新端点: GET /thermal/ht/unit-hot-archive/list
     */
    @SaCheckPermission("thermal:ht:unit-hot-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatUnitHotArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return unitHotArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询单元热表配表详情
     * 旧端点: GET /ht/unitHotArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/unit-hot-archive/{id}
     */
    @SaCheckPermission("thermal:ht:unit-hot-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatUnitHotArchiveVo> getById(@PathVariable String id) {
        return R.ok(unitHotArchiveService.selectById(id));
    }

    /**
     * 新增单元热表配表
     * 旧端点: POST /ht/unitHotArchive/insertData
     * 新端点: POST /thermal/ht/unit-hot-archive
     */
    @SaCheckPermission("thermal:ht:unit-hot-archive:add")
    @SaCheckLogin
    @Log(title = "单元热表配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatUnitHotArchiveBo bo) {
        long count = unitHotArchiveService.count(new LambdaQueryWrapper<PrHeatUnitHotArchive>()
            .eq(PrHeatUnitHotArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatUnitHotArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatUnitHotArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatUnitHotArchive entity = MapstructUtils.convert(bo, PrHeatUnitHotArchive.class);
        return toAjax(unitHotArchiveService.save(entity));
    }

    /**
     * 修改单元热表配表
     * 旧端点: POST /ht/unitHotArchive/updateData
     * 新端点: PUT /thermal/ht/unit-hot-archive
     */
    @SaCheckPermission("thermal:ht:unit-hot-archive:edit")
    @SaCheckLogin
    @Log(title = "单元热表配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatUnitHotArchiveBo bo) {
        long count = unitHotArchiveService.count(new LambdaQueryWrapper<PrHeatUnitHotArchive>()
            .eq(PrHeatUnitHotArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatUnitHotArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatUnitHotArchive::getIsChanged, 0)
            .ne(PrHeatUnitHotArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatUnitHotArchive entity = MapstructUtils.convert(bo, PrHeatUnitHotArchive.class);
        return toAjax(unitHotArchiveService.updateById(entity));
    }

    /**
     * 删除单元热表配表
     * 旧端点: POST /ht/unitHotArchive/deleteData/{id}
     * 新端点: DELETE /thermal/ht/unit-hot-archive/{id}
     */
    @SaCheckPermission("thermal:ht:unit-hot-archive:remove")
    @SaCheckLogin
    @Log(title = "单元热表配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(unitHotArchiveService.removeById(id));
    }
}
