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
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.bo.PrHeatTempArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.service.IPrHeatTempArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    @SaCheckPermission("thermal:ht:tempArchive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatTempArchiveVo> list(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return tempArchiveService.selectPageList(orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询温采器配表详情
     */
    @SaCheckPermission("thermal:ht:tempArchive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatTempArchiveVo> getById(@PathVariable String id) {
        return R.ok(tempArchiveService.selectById(id));
    }

    /**
     * 新增温采器配表
     */
    @SaCheckPermission("thermal:ht:tempArchive:add")
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
    @SaCheckPermission("thermal:ht:tempArchive:edit")
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
    @SaCheckPermission("thermal:ht:tempArchive:remove")
    @SaCheckLogin
    @Log(title = "温采器配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(tempArchiveService.removeById(id));
    }

    // ========== 批量操作端点 ==========

    /**
     * 同步户温采器信息到采集平台
     * 旧端点: POST /property/prHeatTempArchive/valveInformationSynchronization
     * 新端点: POST /thermal/ht/temp-archive/sync
     */
    @SaCheckPermission("thermal:ht:tempArchive:edit")
    @SaCheckLogin
    @Log(title = "温采器配表-同步采集平台", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public R<Boolean> valveInformationSynchronization(
            @RequestParam String orgId) {
        boolean result = tempArchiveService.valveInformationSynchronization(orgId);
        return result ? R.ok(true) : R.fail("同步失败，请检查采集平台配置");
    }

    /**
     * 下载同步信息Excel
     * 旧端点: POST /property/prHeatTempArchive/downloadInfoSync
     * 新端点: GET /thermal/ht/temp-archive/sync-download
     */
    @SaCheckPermission("thermal:ht:tempArchive:list")
    @SaCheckLogin
    @Log(title = "温采器配表-同步信息下载", businessType = BusinessType.EXPORT)
    @GetMapping("/sync-download")
    public void downloadInfoSync(HttpServletResponse response,
                                  @RequestParam String orgId) throws IOException {
        List<PrHeatTempArchiveVo> list = tempArchiveService.listSyncData(orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("户温采器同步信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatTempArchiveVo.class).sheet("同步信息").doWrite(list);
    }

    /**
     * 导出温采器配表 Excel
     * 旧端点: POST /property/prHeatTempArchive/exportAll
     * 新端点: GET /thermal/ht/temp-archive/export
     */
    @SaCheckPermission("thermal:ht:tempArchive:list")
    @SaCheckLogin
    @Log(title = "温采器配表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void exportAll(HttpServletResponse response,
                           @RequestParam(required = false) String orgId) throws IOException {
        List<PrHeatTempArchiveVo> list = tempArchiveService.listAll(orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("温采器配表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatTempArchiveVo.class).sheet("温采器配表").doWrite(list);
    }

    /**
     * 导入温采器配表 Excel
     * POST /thermal/ht/temp-archive/import
     */
    @SaCheckPermission("thermal:ht:tempArchive:add")
    @SaCheckLogin
    @Log(title = "温采器配表-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importTempArchive(@RequestParam("file") MultipartFile file) throws IOException {
        return tempArchiveService.importTempArchive(file);
    }
}
