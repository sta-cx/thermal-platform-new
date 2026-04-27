package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHeatDtuArchive;
import org.sdkj.thermal.domain.bo.PrHeatDtuArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;
import org.sdkj.thermal.service.IPrHeatDtuArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * DTU采集器配表管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/dtu-archive")
public class PrHeatDtuArchiveController extends BaseController {

    private final IPrHeatDtuArchiveService dtuArchiveService;

    /**
     * 分页查询DTU采集器配表列表
     */
    @SaCheckPermission("thermal:ht:dtu-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatDtuArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            PageQuery pageQuery) {
        return dtuArchiveService.selectPageList(companyId, orgId, search, status, pageQuery);
    }

    /**
     * 查询DTU采集器配表详情
     */
    @SaCheckPermission("thermal:ht:dtu-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatDtuArchiveVo> getById(@PathVariable String id) {
        return R.ok(dtuArchiveService.selectById(id));
    }

    /**
     * 新增DTU采集器配表
     */
    @SaCheckPermission("thermal:ht:dtu-archive:add")
    @SaCheckLogin
    @Log(title = "DTU采集器配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatDtuArchiveBo bo) {
        PrHeatDtuArchive entity = MapstructUtils.convert(bo, PrHeatDtuArchive.class);
        return toAjax(dtuArchiveService.save(entity));
    }

    /**
     * 修改DTU采集器配表
     */
    @SaCheckPermission("thermal:ht:dtu-archive:edit")
    @SaCheckLogin
    @Log(title = "DTU采集器配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatDtuArchiveBo bo) {
        PrHeatDtuArchive entity = MapstructUtils.convert(bo, PrHeatDtuArchive.class);
        return toAjax(dtuArchiveService.updateById(entity));
    }

    /**
     * 删除DTU采集器配表
     */
    @SaCheckPermission("thermal:ht:dtu-archive:remove")
    @SaCheckLogin
    @Log(title = "DTU采集器配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(dtuArchiveService.removeById(id));
    }

    /**
     * 查询DTU下所有仪表信息并生成查询指令
     */
    @SaCheckPermission("thermal:ht:dtu-archive:query")
    @SaCheckLogin
    @Log(title = "查询DTU仪表", businessType = BusinessType.OTHER)
    @PostMapping("/query-meter")
    public R<Void> queryMeter(@Validated @RequestBody PrHeatDtuArchiveBo bo) {
        return toAjax(dtuArchiveService.queryMeter(bo));
    }

    // ========== 批量操作端点 ==========

    /**
     * 导出DTU采集器配表 Excel
     * 旧端点: POST /property/prHeatDtuArchive/exportAll
     * 新端点: GET /thermal/ht/dtu-archive/export
     */
    @SaCheckPermission("thermal:ht:dtu-archive:list")
    @SaCheckLogin
    @Log(title = "DTU采集器配表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void exportAll(HttpServletResponse response,
                           @RequestParam(required = false) String companyId,
                           @RequestParam(required = false) String orgId) throws IOException {
        List<PrHeatDtuArchiveVo> list = dtuArchiveService.listAll(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("DTU采集器配表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatDtuArchiveVo.class).sheet("DTU采集器配表").doWrite(list);
    }

    /**
     * 导入DTU采集器配表 Excel
     * POST /thermal/ht/dtu-archive/import
     */
    @SaCheckPermission("thermal:ht:dtu-archive:add")
    @SaCheckLogin
    @Log(title = "DTU采集器配表-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importDtuArchive(@RequestParam("file") MultipartFile file) throws IOException {
        return dtuArchiveService.importDtuArchive(file);
    }
}
