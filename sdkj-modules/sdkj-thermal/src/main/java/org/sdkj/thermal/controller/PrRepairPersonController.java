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
import org.sdkj.thermal.domain.PrRepairPerson;
import org.sdkj.thermal.service.IPrRepairPersonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/repair-person")
public class PrRepairPersonController extends BaseController {

    private final IPrRepairPersonService repairPersonService;

    @SaCheckPermission("thermal:property:repairPerson:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrRepairPerson> list(@RequestParam(required = false) String search,
            PageQuery pageQuery) {
        Page<PrRepairPerson> page = pageQuery.build();
        LambdaQueryWrapper<PrRepairPerson> lqw = new LambdaQueryWrapper<>();
        lqw.like(search != null && !search.isEmpty(), PrRepairPerson::getName, search);
        lqw.orderByDesc(PrRepairPerson::getCreateTime);
        repairPersonService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:property:repairPerson:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrRepairPerson> getById(@PathVariable String id) {
        return R.ok(repairPersonService.getById(id));
    }

    @SaCheckPermission("thermal:property:repairPerson:add")
    @SaCheckLogin
    @Log(title = "维修人员", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrRepairPerson person) {
        return toAjax(repairPersonService.save(person));
    }

    @SaCheckPermission("thermal:property:repairPerson:edit")
    @SaCheckLogin
    @Log(title = "维修人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrRepairPerson person) {
        return toAjax(repairPersonService.updateById(person));
    }

    @SaCheckPermission("thermal:property:repairPerson:remove")
    @SaCheckLogin
    @Log(title = "维修人员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(repairPersonService.removeById(id));
    }
}
