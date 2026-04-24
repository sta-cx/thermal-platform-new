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
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.bo.PrHeatValveArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * 户间阀门配表管理
 * 迁移自旧系统 PrHeatValveArchiveController
 * 旧端点: /ht/valveArchive/* -> 新端点: /thermal/ht/valve-archive/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/valve-archive")
public class PrHeatValveArchiveController extends BaseController {

    private final IPrHeatValveArchiveService valveArchiveService;
    private final IHtTasksPerformService tasksPerformService;

    /**
     * 分页查询户间阀门配表列表
     * 旧端点: GET /ht/valveArchive/pageList
     * 新端点: GET /thermal/ht/valve-archive/list
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatValveArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return valveArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询户间阀门配表详情
     * 旧端点: GET /ht/valveArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/valve-archive/{id}
     */
    @SaCheckPermission("thermal:ht:valve-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatValveArchiveVo> getById(@PathVariable String id) {
        return R.ok(valveArchiveService.selectById(id));
    }

    /**
     * 新增户间阀门配表
     * 旧端点: POST /ht/valveArchive/insertData
     * 新端点: POST /thermal/ht/valve-archive
     */
    @SaCheckPermission("thermal:ht:valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间阀门配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatValveArchiveBo bo) {
        long count = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatValveArchive entity = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        return toAjax(valveArchiveService.save(entity));
    }

    /**
     * 修改户间阀门配表
     * 旧端点: POST /ht/valveArchive/updateData
     * 新端点: PUT /thermal/ht/valve-archive
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatValveArchiveBo bo) {
        long count = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatValveArchive::getIsChanged, 0)
            .ne(PrHeatValveArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatValveArchive entity = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        return toAjax(valveArchiveService.updateById(entity));
    }

    /**
     * 删除户间阀门配表
     * 旧端点: POST /ht/valveArchive/deleteData/{id}
     * 新端点: DELETE /thermal/ht/valve-archive/{id}
     */
    @SaCheckPermission("thermal:ht:valve-archive:remove")
    @SaCheckLogin
    @Log(title = "户间阀门配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(valveArchiveService.removeById(id));
    }

    /**
     * 开阀
     * 旧端点: POST /ht/valveArchive/openValve
     * 新端点: POST /thermal/ht/valve-archive/openValve
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-开阀", businessType = BusinessType.UPDATE)
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatValveArchive> archives = valveArchiveService.listByIds(ids);
        if (archives.isEmpty()) {
            return R.fail("未找到对应的阀门配表记录");
        }
        List<HtTasksPerform> tasks = new LinkedList<>();
        for (PrHeatValveArchive archive : archives) {
            HtTasksPerform task = new HtTasksPerform();
            task.setInstructionType(3);
            task.setInstruction(100);
            task.setNumber(1);
            task.setOrgId(orgId);
            task.setCompanyId(companyId);
            task.setConcentratorCode(archive.getConcentratorCode());
            task.setDeviceId(archive.getDeviceId());
            task.setMeterNum(archive.getMeterNum());
            task.setMeterId(archive.getId());
            task.setMeterArcCode(archive.getMeterArcCode());
            task.setStatus(0);
            task.setInstructionStatus(0);
            task.setImei(archive.getImeiNum());
            task.setDtuNum(archive.getDtuNum());
            task.setChanNum(archive.getChanNum());
            tasks.add(task);
        }
        return toAjax(tasksPerformService.saveBatch(tasks));
    }

    /**
     * 关阀
     * 旧端点: POST /ht/valveArchive/closeValve
     * 新端点: POST /thermal/ht/valve-archive/closeValve
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-关阀", businessType = BusinessType.UPDATE)
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatValveArchive> archives = valveArchiveService.listByIds(ids);
        if (archives.isEmpty()) {
            return R.fail("未找到对应的阀门配表记录");
        }
        List<HtTasksPerform> tasks = new LinkedList<>();
        for (PrHeatValveArchive archive : archives) {
            HtTasksPerform task = new HtTasksPerform();
            task.setInstructionType(3);
            task.setInstruction(0);
            task.setNumber(1);
            task.setOrgId(orgId);
            task.setCompanyId(companyId);
            task.setConcentratorCode(archive.getConcentratorCode());
            task.setDeviceId(archive.getDeviceId());
            task.setMeterNum(archive.getMeterNum());
            task.setMeterId(archive.getId());
            task.setMeterArcCode(archive.getMeterArcCode());
            task.setStatus(0);
            task.setInstructionStatus(0);
            task.setImei(archive.getImeiNum());
            task.setDtuNum(archive.getDtuNum());
            task.setChanNum(archive.getChanNum());
            tasks.add(task);
        }
        return toAjax(tasksPerformService.saveBatch(tasks));
    }

    /**
     * 换表
     * 旧端点: POST /ht/valveArchive/exchangeMeter
     * 新端点: POST /thermal/ht/valve-archive/exchangeMeter/{oldId}
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-换表", businessType = BusinessType.UPDATE)
    @PostMapping("/exchangeMeter/{oldId}")
    public R<Void> exchangeMeter(@Validated @RequestBody PrHeatValveArchiveBo bo, @PathVariable String oldId) {
        // 1. 查出旧记录，标记为已变更
        PrHeatValveArchive oldArchive = valveArchiveService.getById(oldId);
        if (oldArchive == null) {
            return R.fail("未找到原阀门配表记录");
        }
        oldArchive.setIsChanged(1);
        valveArchiveService.updateById(oldArchive);
        // 2. 创建新记录
        PrHeatValveArchive newArchive = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        newArchive.setIsChanged(0);
        newArchive.setIsOpen(0);
        return toAjax(valveArchiveService.save(newArchive));
    }

    /**
     * 根据房屋ID新增阀门配表（校验房屋唯一性）
     * 旧端点: POST /ht/valveArchive/insertByHouseId
     * 新端点: POST /thermal/ht/valve-archive/insertByHouseId
     */
    @SaCheckPermission("thermal:ht:valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间阀门配表-按房屋新增", businessType = BusinessType.INSERT)
    @PostMapping("/insertByHouseId")
    public R<Void> insertByHouseId(@Validated @RequestBody PrHeatValveArchiveBo bo) {
        // 校验 houseId 唯一性
        long houseCount = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getHouseId, bo.getHouseId())
            .eq(PrHeatValveArchive::getIsChanged, 0));
        if (houseCount > 0) {
            return R.fail("该房屋已绑定阀门配表");
        }
        // 校验设备编号唯一性
        long count = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatValveArchive entity = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        return toAjax(valveArchiveService.save(entity));
    }
}
