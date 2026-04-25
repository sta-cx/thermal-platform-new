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
import org.sdkj.thermal.domain.PrHeatStation;
import org.sdkj.thermal.service.IPrHeatStationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/station")
public class PrHeatStationController extends BaseController {

    private final IPrHeatStationService stationService;

    @SaCheckPermission("thermal:ht:station:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatStation> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String companyId,
            PageQuery pageQuery) {
        Page<PrHeatStation> page = pageQuery.build();
        LambdaQueryWrapper<PrHeatStation> lqw = new LambdaQueryWrapper<>();
        lqw.like(search != null && !search.isEmpty(), PrHeatStation::getName, search);
        lqw.eq(companyId != null && !companyId.isEmpty(), PrHeatStation::getCompanyId, companyId);
        lqw.orderByAsc(PrHeatStation::getSeq);
        stationService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:ht:station:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatStation> getById(@PathVariable String id) {
        return R.ok(stationService.getById(id));
    }

    @SaCheckPermission("thermal:ht:station:add")
    @SaCheckLogin
    @Log(title = "换热站管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatStation station) {
        return toAjax(stationService.save(station));
    }

    @SaCheckPermission("thermal:ht:station:edit")
    @SaCheckLogin
    @Log(title = "换热站管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrHeatStation station) {
        return toAjax(stationService.updateById(station));
    }

    @SaCheckPermission("thermal:ht:station:remove")
    @SaCheckLogin
    @Log(title = "换热站管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(stationService.removeById(id));
    }

    @SaCheckPermission("thermal:ht:station:list")
    @SaCheckLogin
    @GetMapping("/company/{companyId}")
    public R<List<PrHeatStation>> listByCompany(@PathVariable String companyId) {
        return R.ok(stationService.selectByCompanyId(companyId));
    }

    @SaCheckPermission("thermal:ht:station:list")
    @SaCheckLogin
    @GetMapping("/org/{orgId}")
    public R<List<PrHeatStation>> listByOrg(@PathVariable String orgId) {
        return R.ok(stationService.selectByOrgId(orgId));
    }
}
