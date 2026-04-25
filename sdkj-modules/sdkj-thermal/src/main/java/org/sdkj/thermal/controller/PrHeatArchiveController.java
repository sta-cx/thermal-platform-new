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
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.bo.PrHeatArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.service.IPrHeatArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋热表配表管理
 * 迁移自旧系统 PrHeatArchiveController
 * 旧端点: /ht/heatArchive/* -> 新端点: /thermal/ht/heat-archive/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/heat-archive")
public class PrHeatArchiveController extends BaseController {

    private final IPrHeatArchiveService heatArchiveService;

    /**
     * 分页查询房屋热表配表列表
     * 旧端点: GET /ht/heatArchive/pageList
     * 新端点: GET /thermal/ht/heat-archive/list
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String archiveId,
            PageQuery pageQuery) {
        return heatArchiveService.selectPageList(companyId, orgId, buildingId, unitCode, search, archiveId, pageQuery);
    }

    /**
     * 查询房屋热表配表详情
     * 旧端点: GET /ht/heatArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/heat-archive/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatArchiveVo> getById(@PathVariable String id) {
        return R.ok(heatArchiveService.selectById(id));
    }

    /**
     * 新增房屋热表配表
     * 旧端点: POST /ht/heatArchive/insertData
     * 新端点: POST /thermal/ht/heat-archive
     */
    @SaCheckPermission("thermal:ht:heat-archive:add")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatArchiveBo bo) {
        long count = heatArchiveService.count(new LambdaQueryWrapper<PrHeatArchive>()
            .eq(PrHeatArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatArchive entity = MapstructUtils.convert(bo, PrHeatArchive.class);
        return toAjax(heatArchiveService.save(entity));
    }

    /**
     * 修改房屋热表配表
     * 旧端点: POST /ht/heatArchive/updateData
     * 新端点: PUT /thermal/ht/heat-archive
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatArchiveBo bo) {
        long count = heatArchiveService.count(new LambdaQueryWrapper<PrHeatArchive>()
            .eq(PrHeatArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatArchive::getIsChanged, 0)
            .ne(PrHeatArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatArchive entity = MapstructUtils.convert(bo, PrHeatArchive.class);
        return toAjax(heatArchiveService.updateById(entity));
    }

    /**
     * 删除房屋热表配表（逻辑删除）
     * 旧端点: POST /ht/heatArchive/deleteData/{id}
     * 新端点: DELETE /thermal/ht/heat-archive/{id}
     * 注意: PrHeatArchive 实体使用 @TableLogic 软删除
     */
    @SaCheckPermission("thermal:ht:heat-archive:remove")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(heatArchiveService.removeById(id));
    }

    /**
     * 查询公司下所有热表档案
     * 旧端点: GET /ht/heatArchive/queryCompanyHeat
     * 新端点: GET /thermal/ht/heat-archive/queryCompanyHeat
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @GetMapping("/queryCompanyHeat")
    public R<List<PrHeatArchiveVo>> queryCompanyHeat(@RequestParam String companyId) {
        return R.ok(heatArchiveService.queryCompanyHeat(companyId));
    }

    /**
     * 停表（设置 isStop=1）
     * 旧端点: POST /ht/heatArchive/stopMeter/{id}
     * 新端点: POST /thermal/ht/heat-archive/stopMeter/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表-停表", businessType = BusinessType.UPDATE)
    @PostMapping("/stopMeter/{id}")
    public R<Void> stopMeter(@PathVariable String id) {
        PrHeatArchive entity = new PrHeatArchive();
        entity.setId(id);
        entity.setIsStop(1);
        return toAjax(heatArchiveService.updateById(entity));
    }

    /**
     * 开表（设置 isStop=0）
     * 旧端点: POST /ht/heatArchive/startMeter/{id}
     * 新端点: POST /thermal/ht/heat-archive/startMeter/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表-开表", businessType = BusinessType.UPDATE)
    @PostMapping("/startMeter/{id}")
    public R<Void> startMeter(@PathVariable String id) {
        PrHeatArchive entity = new PrHeatArchive();
        entity.setId(id);
        entity.setIsStop(0);
        return toAjax(heatArchiveService.updateById(entity));
    }

    /**
     * 计算平衡
     * 旧端点: GET /ht/heatArchive/calculate/{id}
     * 新端点: GET /thermal/ht/heat-archive/calculate/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:query")
    @SaCheckLogin
    @GetMapping("/calculate/{id}")
    public R<Void> calculate(@PathVariable String id) {
        return R.fail("计算平衡功能尚未实现，请等待后续版本");
    }
}
