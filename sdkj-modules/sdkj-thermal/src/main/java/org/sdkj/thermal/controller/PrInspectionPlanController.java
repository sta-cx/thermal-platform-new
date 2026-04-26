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
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrInspectionPlan;
import org.sdkj.thermal.service.IPrInspectionPlanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/inspection-plan")
public class PrInspectionPlanController extends BaseController {

    private final IPrInspectionPlanService inspectionPlanService;

    @SaCheckPermission("thermal:property:inspection:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrInspectionPlan> list(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        Page<PrInspectionPlan> page = pageQuery.build();
        LambdaQueryWrapper<PrInspectionPlan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(orgId != null && !orgId.isEmpty(), PrInspectionPlan::getOrgId, orgId);
        lqw.like(search != null && !search.isEmpty(), PrInspectionPlan::getName, search);
        lqw.orderByDesc(PrInspectionPlan::getCreateTime);
        inspectionPlanService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:property:inspection:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrInspectionPlan> getById(@PathVariable String id) {
        return R.ok(inspectionPlanService.getById(id));
    }

    @SaCheckPermission("thermal:property:inspection:add")
    @SaCheckLogin
    @Log(title = "巡检计划", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrInspectionPlan plan) {
        return toAjax(inspectionPlanService.save(plan));
    }

    @SaCheckPermission("thermal:property:inspection:edit")
    @SaCheckLogin
    @Log(title = "巡检计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrInspectionPlan plan) {
        return toAjax(inspectionPlanService.updateById(plan));
    }

    @SaCheckPermission("thermal:property:inspection:remove")
    @SaCheckLogin
    @Log(title = "巡检计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(inspectionPlanService.removeById(id));
    }
}
