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
import org.sdkj.thermal.domain.PrScheduling;
import org.sdkj.thermal.service.IPrSchedulingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/scheduling")
public class PrSchedulingController extends BaseController {

    private final IPrSchedulingService schedulingService;

    @SaCheckPermission("thermal:property:scheduling:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Page<PrScheduling>> list(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PrScheduling> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrScheduling> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), PrScheduling::getCompanyId, companyId);
        lqw.eq(orgId != null && !orgId.isEmpty(), PrScheduling::getOrgId, orgId);
        lqw.orderByDesc(PrScheduling::getWorkDate);
        return R.ok(schedulingService.page(page, lqw));
    }

    @SaCheckPermission("thermal:property:scheduling:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrScheduling> getById(@PathVariable String id) {
        return R.ok(schedulingService.getById(id));
    }

    @SaCheckPermission("thermal:property:scheduling:add")
    @SaCheckLogin
    @Log(title = "排班管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrScheduling scheduling) {
        return toAjax(schedulingService.save(scheduling));
    }

    @SaCheckPermission("thermal:property:scheduling:edit")
    @SaCheckLogin
    @Log(title = "排班管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrScheduling scheduling) {
        return toAjax(schedulingService.updateById(scheduling));
    }

    @SaCheckPermission("thermal:property:scheduling:remove")
    @SaCheckLogin
    @Log(title = "排班管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(schedulingService.removeById(id));
    }
}
