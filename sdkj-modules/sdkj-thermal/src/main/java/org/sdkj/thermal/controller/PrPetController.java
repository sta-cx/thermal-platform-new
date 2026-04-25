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
import org.sdkj.thermal.domain.PrPet;
import org.sdkj.thermal.service.IPrPetService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/pet")
public class PrPetController extends BaseController {

    private final IPrPetService petService;

    @SaCheckPermission("thermal:property:pet:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Page<PrPet>> list(
            @RequestParam(required = false) String houseId,
            @RequestParam(required = false) String orgId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PrPet> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrPet> lqw = new LambdaQueryWrapper<>();
        lqw.eq(houseId != null && !houseId.isEmpty(), PrPet::getHouseId, houseId);
        lqw.eq(orgId != null && !orgId.isEmpty(), PrPet::getOrgId, orgId);
        lqw.orderByDesc(PrPet::getCreateTime);
        return R.ok(petService.page(page, lqw));
    }

    @SaCheckPermission("thermal:property:pet:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrPet> getById(@PathVariable String id) {
        return R.ok(petService.getById(id));
    }

    @SaCheckPermission("thermal:property:pet:add")
    @SaCheckLogin
    @Log(title = "宠物管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrPet pet) {
        return toAjax(petService.save(pet));
    }

    @SaCheckPermission("thermal:property:pet:edit")
    @SaCheckLogin
    @Log(title = "宠物管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrPet pet) {
        return toAjax(petService.updateById(pet));
    }

    @SaCheckPermission("thermal:property:pet:remove")
    @SaCheckLogin
    @Log(title = "宠物管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(petService.removeById(id));
    }
}
