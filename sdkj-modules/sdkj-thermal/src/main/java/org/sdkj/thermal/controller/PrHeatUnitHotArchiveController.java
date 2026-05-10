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
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.bo.PrHeatUnitHotArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.service.IPrHeatUnitHotArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    @SaCheckPermission("thermal:ht:unitHotArchive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatUnitHotArchiveVo> list(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return unitHotArchiveService.selectPageList(orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询单元热表配表详情
     * 旧端点: GET /ht/unitHotArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/unit-hot-archive/{id}
     */
    @SaCheckPermission("thermal:ht:unitHotArchive:query")
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
    @SaCheckPermission("thermal:ht:unitHotArchive:add")
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
    @SaCheckPermission("thermal:ht:unitHotArchive:edit")
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
    @SaCheckPermission("thermal:ht:unitHotArchive:remove")
    @SaCheckLogin
    @Log(title = "单元热表配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(unitHotArchiveService.removeById(id));
    }

    // ========== 批量操作端点 ==========

    /**
     * 同步单元热表信息到采集平台
     * POST /thermal/ht/unit-hot-archive/sync
     */
    @SaCheckPermission("thermal:ht:unitHotArchive:edit")
    @SaCheckLogin
    @Log(title = "单元热表配表-同步采集平台", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public R<Boolean> valveInformationSynchronization(
            @RequestParam String orgId) {
        boolean result = unitHotArchiveService.valveInformationSynchronization(orgId);
        return result ? R.ok(true) : R.fail("同步失败，请检查采集平台配置");
    }

    /**
     * 下载同步信息Excel
     * GET /thermal/ht/unit-hot-archive/sync-download
     */
    @SaCheckPermission("thermal:ht:unitHotArchive:list")
    @SaCheckLogin
    @Log(title = "单元热表配表-同步信息下载", businessType = BusinessType.EXPORT)
    @GetMapping("/sync-download")
    public void downloadInfoSync(HttpServletResponse response,
                                  @RequestParam String orgId) throws IOException {
        List<PrHeatUnitHotArchiveVo> list = unitHotArchiveService.listSyncData(orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("单元热表同步信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatUnitHotArchiveVo.class).sheet("同步信息").doWrite(list);
    }

    /**
     * 导出单元热表配表 Excel
     * GET /thermal/ht/unit-hot-archive/export
     */
    @SaCheckPermission("thermal:ht:unitHotArchive:list")
    @SaCheckLogin
    @Log(title = "单元热表配表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void exportAll(HttpServletResponse response,
                           @RequestParam(required = false) String orgId) throws IOException {
        List<PrHeatUnitHotArchiveVo> list = unitHotArchiveService.listAll(orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("单元热表配表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatUnitHotArchiveVo.class).sheet("单元热表配表").doWrite(list);
    }

    /**
     * 导入单元热表配表 Excel
     * POST /thermal/ht/unit-hot-archive/import
     */
    @SaCheckPermission("thermal:ht:unitHotArchive:add")
    @SaCheckLogin
    @Log(title = "单元热表配表-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importUnitHotArchive(@RequestParam("file") MultipartFile file) throws IOException {
        return unitHotArchiveService.importUnitHotArchive(file);
    }
}
