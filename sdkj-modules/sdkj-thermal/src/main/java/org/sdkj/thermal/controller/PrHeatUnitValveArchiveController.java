package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.bo.PrHeatUnitValveArchiveBo;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatUnitValveArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 单元阀门配表管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/unit-valve-archive")
public class PrHeatUnitValveArchiveController extends BaseController {

    private final IPrHeatUnitValveArchiveService unitValveArchiveService;
    private final IHtTasksPerformService tasksPerformService;

    /**
     * 分页查询单元阀门配表列表
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatUnitValveArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return unitValveArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询单元阀门配表详情
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatUnitValveArchiveVo> getById(@PathVariable String id) {
        return R.ok(unitValveArchiveService.selectById(id));
    }

    /**
     * 新增单元阀门配表
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:add")
    @SaCheckLogin
    @Log(title = "单元阀门配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatUnitValveArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0
        long count = unitValveArchiveService.count(new LambdaQueryWrapper<PrHeatUnitValveArchive>()
            .eq(PrHeatUnitValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatUnitValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatUnitValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatUnitValveArchive entity = MapstructUtils.convert(bo, PrHeatUnitValveArchive.class);
        return toAjax(unitValveArchiveService.save(entity));
    }

    /**
     * 修改单元阀门配表
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "单元阀门配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatUnitValveArchiveBo bo) {
        // 重复校验：meterNum + meterArcCode + isChanged=0（排除自身）
        long count = unitValveArchiveService.count(new LambdaQueryWrapper<PrHeatUnitValveArchive>()
            .eq(PrHeatUnitValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatUnitValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatUnitValveArchive::getIsChanged, 0)
            .ne(PrHeatUnitValveArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatUnitValveArchive entity = MapstructUtils.convert(bo, PrHeatUnitValveArchive.class);
        return toAjax(unitValveArchiveService.updateById(entity));
    }

    /**
     * 删除单元阀门配表
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:remove")
    @SaCheckLogin
    @Log(title = "单元阀门配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(unitValveArchiveService.removeById(id));
    }

    /**
     * 开阀操作
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "单元阀门配表-开阀", businessType = BusinessType.UPDATE)
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatUnitValveArchive> archives = unitValveArchiveService.listByIds(ids);
        if (archives.isEmpty()) { return R.fail("未找到配表记录"); }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), null, null))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 100));
    }

    /**
     * 关阀操作
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "单元阀门配表-关阀", businessType = BusinessType.UPDATE)
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatUnitValveArchive> archives = unitValveArchiveService.listByIds(ids);
        if (archives.isEmpty()) { return R.fail("未找到配表记录"); }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), null, null))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 0));
    }

    // ========== 批量操作端点 ==========

    /**
     * 同步单元阀门信息到采集平台
     * POST /thermal/ht/unit-valve-archive/sync
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:edit")
    @SaCheckLogin
    @Log(title = "单元阀门配表-同步采集平台", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public R<Boolean> valveInformationSynchronization(
            @RequestParam String orgId,
            @RequestParam String companyId) {
        boolean result = unitValveArchiveService.valveInformationSynchronization(orgId, companyId);
        return result ? R.ok(true) : R.fail("同步失败，请检查采集平台配置");
    }

    /**
     * 下载同步信息Excel
     * GET /thermal/ht/unit-valve-archive/sync-download
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:list")
    @SaCheckLogin
    @Log(title = "单元阀门配表-同步信息下载", businessType = BusinessType.EXPORT)
    @GetMapping("/sync-download")
    public void downloadInfoSync(HttpServletResponse response,
                                  @RequestParam String companyId,
                                  @RequestParam String orgId) throws IOException {
        List<PrHeatUnitValveArchiveVo> list = unitValveArchiveService.listSyncData(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("单元阀门同步信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatUnitValveArchiveVo.class).sheet("同步信息").doWrite(list);
    }

    /**
     * 导出单元阀门配表 Excel
     * GET /thermal/ht/unit-valve-archive/export
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:list")
    @SaCheckLogin
    @Log(title = "单元阀门配表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void exportAll(HttpServletResponse response,
                           @RequestParam(required = false) String companyId,
                           @RequestParam(required = false) String orgId) throws IOException {
        List<PrHeatUnitValveArchiveVo> list = unitValveArchiveService.listAll(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("单元阀门配表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatUnitValveArchiveVo.class).sheet("单元阀门配表").doWrite(list);
    }

    /**
     * 导入单元阀门配表 Excel
     * POST /thermal/ht/unit-valve-archive/import
     */
    @SaCheckPermission("thermal:ht:unit-valve-archive:add")
    @SaCheckLogin
    @Log(title = "单元阀门配表-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importUnitValveArchive(@RequestParam("file") MultipartFile file) throws IOException {
        return unitValveArchiveService.importUnitValveArchive(file);
    }
}
