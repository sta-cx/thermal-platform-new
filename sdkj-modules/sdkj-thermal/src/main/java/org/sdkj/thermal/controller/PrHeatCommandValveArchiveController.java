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
import org.sdkj.thermal.domain.PrHeatCommandValveArchive;
import org.sdkj.thermal.domain.bo.PrHeatCommandValveArchiveBo;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.PrHeatCommandValveArchiveVo;
import org.sdkj.thermal.service.IPrHeatCommandValveArchiveService;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 户间控制阀门配表管理
 * 迁移自旧系统 PrHeatCommandValveArchiveController
 * 旧端点: /ht/commandValveArchive/* -> 新端点: /thermal/ht/command-valve-archive/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/command-valve-archive")
public class PrHeatCommandValveArchiveController extends BaseController {

    private final IPrHeatCommandValveArchiveService commandValveArchiveService;
    private final IHtTasksPerformService tasksPerformService;

    /**
     * 分页查询户间控制阀门配表列表
     * 旧端点: GET /ht/commandValveArchive/pageList
     * 新端点: GET /thermal/ht/command-valve-archive/list
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatCommandValveArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String valveCategory,
            PageQuery pageQuery) {
        return commandValveArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, valveCategory, pageQuery);
    }

    /**
     * 查询户间控制阀门配表详情
     * 旧端点: GET /ht/commandValveArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/command-valve-archive/{id}
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatCommandValveArchiveVo> getById(@PathVariable String id) {
        return R.ok(commandValveArchiveService.selectById(id));
    }

    /**
     * 新增户间控制阀门配表
     * 旧端点: POST /ht/commandValveArchive/insertData
     * 新端点: POST /thermal/ht/command-valve-archive
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间控制阀门配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatCommandValveArchiveBo bo) {
        long count = commandValveArchiveService.count(new LambdaQueryWrapper<PrHeatCommandValveArchive>()
            .eq(PrHeatCommandValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatCommandValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatCommandValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatCommandValveArchive entity = MapstructUtils.convert(bo, PrHeatCommandValveArchive.class);
        return toAjax(commandValveArchiveService.save(entity));
    }

    /**
     * 修改户间控制阀门配表
     * 旧端点: POST /ht/commandValveArchive/updateData
     * 新端点: PUT /thermal/ht/command-valve-archive
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间控制阀门配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatCommandValveArchiveBo bo) {
        long count = commandValveArchiveService.count(new LambdaQueryWrapper<PrHeatCommandValveArchive>()
            .eq(PrHeatCommandValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatCommandValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatCommandValveArchive::getIsChanged, 0)
            .ne(PrHeatCommandValveArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatCommandValveArchive entity = MapstructUtils.convert(bo, PrHeatCommandValveArchive.class);
        return toAjax(commandValveArchiveService.updateById(entity));
    }

    /**
     * 删除户间控制阀门配表
     * 旧端点: POST /ht/commandValveArchive/deleteData/{id}
     * 新端点: DELETE /thermal/ht/command-valve-archive/{id}
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:remove")
    @SaCheckLogin
    @Log(title = "户间控制阀门配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(commandValveArchiveService.removeById(id));
    }

    /**
     * 开阀
     * 旧端点: POST /ht/commandValveArchive/openValve
     * 新端点: POST /thermal/ht/command-valve-archive/openValve
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间控制阀门配表-开阀", businessType = BusinessType.UPDATE)
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatCommandValveArchive> archives = commandValveArchiveService.listByIds(ids);
        if (archives.isEmpty()) {
            return R.fail("未找到配表记录");
        }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), null, null))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 100));
    }

    /**
     * 关阀
     * 旧端点: POST /ht/commandValveArchive/closeValve
     * 新端点: POST /thermal/ht/command-valve-archive/closeValve
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间控制阀门配表-关阀", businessType = BusinessType.UPDATE)
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatCommandValveArchive> archives = commandValveArchiveService.listByIds(ids);
        if (archives.isEmpty()) {
            return R.fail("未找到配表记录");
        }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), null, null))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 0));
    }

    /**
     * 换表
     * 旧端点: POST /ht/commandValveArchive/exchangeMeter
     * 新端点: POST /thermal/ht/command-valve-archive/exchangeMeter
     */
    @SaCheckPermission("thermal:ht:command-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间控制阀门配表-换表", businessType = BusinessType.UPDATE)
    @PostMapping("/exchangeMeter/{oldId}")
    public R<Void> exchangeMeter(@Validated @RequestBody PrHeatCommandValveArchiveBo bo, @PathVariable String oldId) {
        PrHeatCommandValveArchive oldArchive = commandValveArchiveService.getById(oldId);
        if (oldArchive == null) {
            return R.fail("未找到旧配表记录");
        }
        oldArchive.setIsChanged(1);
        commandValveArchiveService.updateById(oldArchive);

        PrHeatCommandValveArchive newArchive = MapstructUtils.convert(bo, PrHeatCommandValveArchive.class);
        newArchive.setIsChanged(0);
        newArchive.setIsOpen(0);
        return toAjax(commandValveArchiveService.save(newArchive));
    }
}
