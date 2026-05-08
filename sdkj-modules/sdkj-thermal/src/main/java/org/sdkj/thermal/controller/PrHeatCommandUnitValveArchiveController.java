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
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrHeatCommandUnitValveArchive;
import org.sdkj.thermal.domain.bo.PrHeatCommandUnitValveArchiveBo;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.PrHeatCommandUnitValveArchiveVo;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatCommandUnitValveArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单元控制阀门配表管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/command-unit-valve-archive")
public class PrHeatCommandUnitValveArchiveController extends BaseController {

    private final IPrHeatCommandUnitValveArchiveService commandUnitValveArchiveService;
    private final IHtTasksPerformService tasksPerformService;

    /**
     * 分页查询单元控制阀门配表列表
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatCommandUnitValveArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return commandUnitValveArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询单元控制阀门配表详情
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatCommandUnitValveArchiveVo> getById(@PathVariable String id) {
        return R.ok(commandUnitValveArchiveService.selectById(id));
    }

    /**
     * 新增单元控制阀门配表
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:add")
    @SaCheckLogin
    @Log(title = "单元控制阀门配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatCommandUnitValveArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0
        long count = commandUnitValveArchiveService.count(new LambdaQueryWrapper<PrHeatCommandUnitValveArchive>()
            .eq(PrHeatCommandUnitValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatCommandUnitValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatCommandUnitValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatCommandUnitValveArchive entity = MapstructUtils.convert(bo, PrHeatCommandUnitValveArchive.class);
        return toAjax(commandUnitValveArchiveService.save(entity));
    }

    /**
     * 修改单元控制阀门配表
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:edit")
    @SaCheckLogin
    @Log(title = "单元控制阀门配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatCommandUnitValveArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0（排除自身）
        long count = commandUnitValveArchiveService.count(new LambdaQueryWrapper<PrHeatCommandUnitValveArchive>()
            .eq(PrHeatCommandUnitValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatCommandUnitValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatCommandUnitValveArchive::getIsChanged, 0)
            .ne(PrHeatCommandUnitValveArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatCommandUnitValveArchive entity = MapstructUtils.convert(bo, PrHeatCommandUnitValveArchive.class);
        return toAjax(commandUnitValveArchiveService.updateById(entity));
    }

    /**
     * 删除单元控制阀门配表
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:remove")
    @SaCheckLogin
    @Log(title = "单元控制阀门配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(commandUnitValveArchiveService.removeById(id));
    }

    /**
     * 开阀操作
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:edit")
    @SaCheckLogin
    @Log(title = "单元控制阀门配表-开阀", businessType = BusinessType.UPDATE)
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatCommandUnitValveArchive> archives = commandUnitValveArchiveService.listByIds(ids);
        if (archives.isEmpty()) { return R.fail("未找到配表记录"); }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), null, null))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 100));
    }

    /**
     * 关阀操作
     */
    @SaCheckPermission("thermal:ht:commandUnitValveArchive:edit")
    @SaCheckLogin
    @Log(title = "单元控制阀门配表-关阀", businessType = BusinessType.UPDATE)
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatCommandUnitValveArchive> archives = commandUnitValveArchiveService.listByIds(ids);
        if (archives.isEmpty()) { return R.fail("未找到配表记录"); }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), null, null))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 0));
    }
}
