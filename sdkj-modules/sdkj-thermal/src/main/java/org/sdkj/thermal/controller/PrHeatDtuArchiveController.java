package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
}
