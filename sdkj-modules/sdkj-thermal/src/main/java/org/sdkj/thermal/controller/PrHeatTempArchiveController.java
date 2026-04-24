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
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.bo.PrHeatTempArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.service.IPrHeatTempArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 温采器配表管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/temp-archive")
public class PrHeatTempArchiveController extends BaseController {

    private final IPrHeatTempArchiveService tempArchiveService;

    /**
     * 分页查询温采器配表列表
     */
    @SaCheckPermission("thermal:ht:temp-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatTempArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return tempArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询温采器配表详情
     */
    @SaCheckPermission("thermal:ht:temp-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatTempArchiveVo> getById(@PathVariable String id) {
        return R.ok(tempArchiveService.selectById(id));
    }

    /**
     * 新增温采器配表
     */
    @SaCheckPermission("thermal:ht:temp-archive:add")
    @SaCheckLogin
    @Log(title = "温采器配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatTempArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0
        long count = tempArchiveService.count(new LambdaQueryWrapper<PrHeatTempArchive>()
            .eq(PrHeatTempArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatTempArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatTempArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatTempArchive entity = MapstructUtils.convert(bo, PrHeatTempArchive.class);
        return toAjax(tempArchiveService.save(entity));
    }

    /**
     * 修改温采器配表
     */
    @SaCheckPermission("thermal:ht:temp-archive:edit")
    @SaCheckLogin
    @Log(title = "温采器配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatTempArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0（排除自身）
        long count = tempArchiveService.count(new LambdaQueryWrapper<PrHeatTempArchive>()
            .eq(PrHeatTempArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatTempArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatTempArchive::getIsChanged, 0)
            .ne(PrHeatTempArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatTempArchive entity = MapstructUtils.convert(bo, PrHeatTempArchive.class);
        return toAjax(tempArchiveService.updateById(entity));
    }

    /**
     * 删除温采器配表
     */
    @SaCheckPermission("thermal:ht:temp-archive:remove")
    @SaCheckLogin
    @Log(title = "温采器配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(tempArchiveService.removeById(id));
    }
}
