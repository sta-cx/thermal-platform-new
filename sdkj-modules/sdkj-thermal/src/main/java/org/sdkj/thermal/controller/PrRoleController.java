package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.bo.AgRoleBo;
import org.sdkj.thermal.service.IAgRoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物业角色管理
 * 复用 AgRoleService，与 AgRoleController 共享同一套角色数据
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/role")
public class PrRoleController extends BaseController {

    private final IAgRoleService roleService;

    @SaCheckPermission("thermal:property:role:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<AgRole>> list() {
        return R.ok(roleService.list());
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<AgRole> getInfo(@PathVariable String id) {
        return R.ok(roleService.getById(id));
    }

    @SaCheckPermission("thermal:property:role:add")
    @SaCheckLogin
    @Log(title = "物业角色", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody AgRoleBo roleBo) {
        return toAjax(roleService.insertRole(roleBo));
    }

    @SaCheckPermission("thermal:property:role:edit")
    @SaCheckLogin
    @Log(title = "物业角色", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AgRoleBo roleBo) {
        return toAjax(roleService.updateRole(roleBo));
    }

    @SaCheckPermission("thermal:property:role:remove")
    @SaCheckLogin
    @Log(title = "物业角色", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(roleService.deleteRole(id));
    }
}
