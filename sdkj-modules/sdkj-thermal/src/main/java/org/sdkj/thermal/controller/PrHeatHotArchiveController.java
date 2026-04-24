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
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.bo.PrHeatHotArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋热量表配表管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/hot-archive")
public class PrHeatHotArchiveController extends BaseController {

    private final IPrHeatHotArchiveService hotArchiveService;

    /**
     * 分页查询房屋热量表配表列表
     */
    @SaCheckPermission("thermal:ht:hot-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatHotArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return hotArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询房屋热量表配表详情
     */
    @SaCheckPermission("thermal:ht:hot-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatHotArchiveVo> getById(@PathVariable String id) {
        return R.ok(hotArchiveService.selectById(id));
    }

    /**
     * 新增房屋热量表配表
     */
    @SaCheckPermission("thermal:ht:hot-archive:add")
    @SaCheckLogin
    @Log(title = "房屋热量表配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatHotArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0
        long count = hotArchiveService.count(new LambdaQueryWrapper<PrHeatHotArchive>()
            .eq(PrHeatHotArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatHotArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatHotArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatHotArchive entity = MapstructUtils.convert(bo, PrHeatHotArchive.class);
        return toAjax(hotArchiveService.save(entity));
    }

    /**
     * 修改房屋热量表配表
     */
    @SaCheckPermission("thermal:ht:hot-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热量表配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatHotArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0（排除自身）
        long count = hotArchiveService.count(new LambdaQueryWrapper<PrHeatHotArchive>()
            .eq(PrHeatHotArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatHotArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatHotArchive::getIsChanged, 0)
            .ne(PrHeatHotArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatHotArchive entity = MapstructUtils.convert(bo, PrHeatHotArchive.class);
        return toAjax(hotArchiveService.updateById(entity));
    }

    /**
     * 删除房屋热量表配表
     */
    @SaCheckPermission("thermal:ht:hot-archive:remove")
    @SaCheckLogin
    @Log(title = "房屋热量表配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(hotArchiveService.removeById(id));
    }

    /**
     * 根据房屋ID新增热量表配表
     * 同时校验房屋是否已有热量表配表
     */
    @SaCheckPermission("thermal:ht:hot-archive:add")
    @SaCheckLogin
    @Log(title = "房屋热量表配表", businessType = BusinessType.INSERT)
    @PostMapping("/insertByHouseId")
    public R<Void> insertByHouseId(@Validated @RequestBody PrHeatHotArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0
        long count = hotArchiveService.count(new LambdaQueryWrapper<PrHeatHotArchive>()
            .eq(PrHeatHotArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatHotArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatHotArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        // 校验房屋是否已有热量表配表
        if (bo.getHouseId() != null) {
            long houseCount = hotArchiveService.count(new LambdaQueryWrapper<PrHeatHotArchive>()
                .eq(PrHeatHotArchive::getHouseId, bo.getHouseId())
                .eq(PrHeatHotArchive::getIsChanged, 0));
            if (houseCount > 0) {
                return R.fail("该房屋已有热量表配表");
            }
        }
        PrHeatHotArchive entity = MapstructUtils.convert(bo, PrHeatHotArchive.class);
        return toAjax(hotArchiveService.save(entity));
    }
}
