package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.bo.AgRoleBo;
import org.sdkj.thermal.service.IAgRoleService;
import org.sdkj.thermal.service.IPrRoleService;
import org.sdkj.system.domain.vo.SysMenuVo;
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
    private final IPrRoleService prRoleService;

    // ========== 基础 CRUD ==========

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

    // ========== 扩展查询 ==========

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/allRoles")
    public R<List<AgRole>> allRoles() {
        return R.ok(prRoleService.getAllRoles());
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/allRolesByCreate")
    public R<List<AgRole>> allRolesByCreate() {
        return R.ok(prRoleService.getAllRolesByCreate(LoginHelper.getUserId()));
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/byUser/{userId}")
    public R<List<AgRole>> byUser(@PathVariable String userId) {
        return R.ok(prRoleService.getRoleByUserId(userId));
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/verifyIdent")
    public R<Boolean> verifyIdent(@RequestParam String roleKey,
                                  @RequestParam(required = false) String roleId) {
        return R.ok(!prRoleService.verifyIdent(roleKey, roleId));
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/verifyName")
    public R<Boolean> verifyName(@RequestParam String roleName,
                                 @RequestParam(required = false) String roleId) {
        return R.ok(!prRoleService.verifyName(roleName, roleId));
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/allocatedMenus/{roleId}")
    public R<List<SysMenuVo>> allocatedMenus(@PathVariable String roleId) {
        return R.ok(prRoleService.findAllocatedMenus(roleId));
    }

    @SaCheckPermission("thermal:property:role:query")
    @SaCheckLogin
    @GetMapping("/unallocatedMenus/{roleId}")
    public R<List<SysMenuVo>> unallocatedMenus(@PathVariable String roleId) {
        return R.ok(prRoleService.findUnallocatedMenus(roleId));
    }

    // ========== 权限分配 ==========

    @SaCheckPermission("thermal:property:role:edit")
    @SaCheckLogin
    @Log(title = "物业角色权限", businessType = BusinessType.UPDATE)
    @PutMapping("/permission/{roleId}")
    public R<Void> permission(@PathVariable String roleId, @RequestBody List<Long> menuIds) {
        prRoleService.permissionUpd(roleId, menuIds);
        return R.ok();
    }

    // ========== 批量操作 ==========

    @SaCheckPermission("thermal:property:role:remove")
    @SaCheckLogin
    @Log(title = "物业角色", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> batchDelete(@RequestBody List<String> ids) {
        prRoleService.deleteRolesData(ids);
        return R.ok();
    }
}
