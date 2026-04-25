package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHeatStationPartition;
import org.sdkj.thermal.service.IPrHeatStationPartitionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/station-partition")
public class PrHeatStationPartitionController extends BaseController {

    private final IPrHeatStationPartitionService partitionService;

    @SaCheckPermission("thermal:ht:station:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatStationPartition> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String stationId,
            PageQuery pageQuery) {
        Page<PrHeatStationPartition> page = pageQuery.build();
        LambdaQueryWrapper<PrHeatStationPartition> lqw = new LambdaQueryWrapper<>();
        lqw.like(search != null && !search.isEmpty(), PrHeatStationPartition::getName, search);
        lqw.eq(companyId != null && !companyId.isEmpty(), PrHeatStationPartition::getCompanyId, companyId);
        lqw.eq(stationId != null && !stationId.isEmpty(), PrHeatStationPartition::getStationId, stationId);
        lqw.orderByAsc(PrHeatStationPartition::getSeq);
        partitionService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:ht:station:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatStationPartition> getById(@PathVariable String id) {
        return R.ok(partitionService.getById(id));
    }

    @SaCheckPermission("thermal:ht:station:add")
    @SaCheckLogin
    @Log(title = "换热站分区", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatStationPartition partition) {
        return toAjax(partitionService.save(partition));
    }

    @SaCheckPermission("thermal:ht:station:edit")
    @SaCheckLogin
    @Log(title = "换热站分区", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrHeatStationPartition partition) {
        return toAjax(partitionService.updateById(partition));
    }

    @SaCheckPermission("thermal:ht:station:remove")
    @SaCheckLogin
    @Log(title = "换热站分区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(partitionService.removeById(id));
    }

    @SaCheckPermission("thermal:ht:station:list")
    @SaCheckLogin
    @GetMapping("/company/{companyId}")
    public R<List<PrHeatStationPartition>> listByCompany(@PathVariable String companyId) {
        return R.ok(partitionService.selectByCompanyId(companyId));
    }

    @SaCheckPermission("thermal:ht:station:list")
    @SaCheckLogin
    @GetMapping("/station/{stationId}")
    public R<List<PrHeatStationPartition>> listByStation(@PathVariable String stationId) {
        return R.ok(partitionService.selectByStationId(stationId));
    }
}
