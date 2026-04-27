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
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.bo.PrHeatHotArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    // ========== 批量操作端点 ==========

    /**
     * 同步户热表信息到采集平台
     * 旧端点: POST /property/prHeatHotArchive/valveInformationSynchronization
     * 新端点: POST /thermal/ht/hot-archive/sync
     */
    @SaCheckPermission("thermal:ht:hot-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热量表配表-同步采集平台", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public R<Boolean> valveInformationSynchronization(
            @RequestParam String orgId,
            @RequestParam String companyId) {
        boolean result = hotArchiveService.valveInformationSynchronization(orgId, companyId);
        return result ? R.ok(true) : R.fail("同步失败，请检查采集平台配置");
    }

    /**
     * 下载同步信息Excel
     * 旧端点: POST /property/prHeatHotArchive/downloadInfoSync
     * 新端点: GET /thermal/ht/hot-archive/sync-download
     */
    @SaCheckPermission("thermal:ht:hot-archive:list")
    @SaCheckLogin
    @Log(title = "房屋热量表配表-同步信息下载", businessType = BusinessType.EXPORT)
    @GetMapping("/sync-download")
    public void downloadInfoSync(HttpServletResponse response,
                                  @RequestParam String companyId,
                                  @RequestParam String orgId) throws IOException {
        List<PrHeatHotArchiveVo> list = hotArchiveService.listSyncData(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("户热同步信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatHotArchiveVo.class).sheet("同步信息").doWrite(list);
    }

    /**
     * 导出热量表配表 Excel
     * 旧端点: POST /property/prHeatHotArchive/exportAll
     * 新端点: GET /thermal/ht/hot-archive/export
     */
    @SaCheckPermission("thermal:ht:hot-archive:list")
    @SaCheckLogin
    @Log(title = "房屋热量表配表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void exportAll(HttpServletResponse response,
                           @RequestParam(required = false) String companyId,
                           @RequestParam(required = false) String orgId) throws IOException {
        List<PrHeatHotArchiveVo> list = hotArchiveService.listAll(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("热量表配表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatHotArchiveVo.class).sheet("热量表配表").doWrite(list);
    }

    /**
     * 导入热量表配表 Excel
     * POST /thermal/ht/hot-archive/import
     */
    @SaCheckPermission("thermal:ht:hot-archive:add")
    @SaCheckLogin
    @Log(title = "房屋热量表配表-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importHotArchive(@RequestParam("file") MultipartFile file) throws IOException {
        return hotArchiveService.importHotArchive(file);
    }
}
