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
import org.sdkj.thermal.domain.PrInspectionPerson;
import org.sdkj.thermal.service.IPrInspectionPersonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/inspection-person")
public class PrInspectionPersonController extends BaseController {

    private final IPrInspectionPersonService inspectionPersonService;

    @SaCheckPermission("thermal:property:inspection:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrInspectionPerson> list(@RequestParam(required = false) String search,
            PageQuery pageQuery) {
        Page<PrInspectionPerson> page = pageQuery.build();
        LambdaQueryWrapper<PrInspectionPerson> lqw = new LambdaQueryWrapper<>();
        lqw.like(search != null && !search.isEmpty(), PrInspectionPerson::getName, search);
        lqw.orderByDesc(PrInspectionPerson::getCreateTime);
        inspectionPersonService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:property:inspection:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrInspectionPerson> getById(@PathVariable String id) {
        return R.ok(inspectionPersonService.getById(id));
    }

    @SaCheckPermission("thermal:property:inspection:add")
    @SaCheckLogin
    @Log(title = "巡检人员", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrInspectionPerson person) {
        return toAjax(inspectionPersonService.save(person));
    }

    @SaCheckPermission("thermal:property:inspection:edit")
    @SaCheckLogin
    @Log(title = "巡检人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrInspectionPerson person) {
        return toAjax(inspectionPersonService.updateById(person));
    }

    @SaCheckPermission("thermal:property:inspection:remove")
    @SaCheckLogin
    @Log(title = "巡检人员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(inspectionPersonService.removeById(id));
    }
}
