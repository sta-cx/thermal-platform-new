package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
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

    @SaCheckPermission("thermal:property:repair:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Page<PrRepairPerson>> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PrRepairPerson> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrRepairPerson> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), PrRepairPerson::getCompanyId, companyId);
        lqw.like(search != null && !search.isEmpty(), PrRepairPerson::getName, search);
        lqw.orderByDesc(PrRepairPerson::getCreateTime);
        return R.ok(repairPersonService.page(page, lqw));
    }

    @SaCheckPermission("thermal:property:repair:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrRepairPerson> getById(@PathVariable String id) {
        return R.ok(repairPersonService.getById(id));
    }

    @SaCheckPermission("thermal:property:repair:add")
    @SaCheckLogin
    @Log(title = "维修人员", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrRepairPerson person) {
        return toAjax(repairPersonService.save(person));
    }

    @SaCheckPermission("thermal:property:repair:edit")
    @SaCheckLogin
    @Log(title = "维修人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrRepairPerson person) {
        return toAjax(repairPersonService.updateById(person));
    }

    @SaCheckPermission("thermal:property:repair:remove")
    @SaCheckLogin
    @Log(title = "维修人员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(repairPersonService.removeById(id));
    }
}
