package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.core.constant.BusinessStatus;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.bo.AgRoleBo;
import org.sdkj.thermal.service.IAgRoleService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.sdkj.system.domain.vo.SysMenuVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理商角色管理
 * 迁移自旧系统 AgentRoleController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/role")
public class AgRoleController {

    private final IAgRoleService roleService;

    /**
     * 根据ID查询角色详情
     */
    @SaCheckPermission("thermal:agent:role:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<AgRole> getInfo(@PathVariable String id) {
        return R.ok(roleService.getById(id));
    }

    /**
     * 分页查询角色列表
     */
    @SaCheckPermission("thermal:agent:role:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<IPage<AgRole>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<AgRole> page = new Page<>(current, size);
        return R.ok(roleService.listRoles(page, name, companyId));
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("thermal:agent:role:add")
    @SaCheckLogin
    @Log(title = "代理商角色", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody AgRoleBo roleBo) {
        return roleService.insertRole(roleBo) ? R.ok() : R.fail("新增失败");
    }

    /**
     * 修改角色
     */
    @SaCheckPermission("thermal:agent:role:edit")
    @SaCheckLogin
    @Log(title = "代理商角色", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AgRoleBo roleBo) {
        return roleService.updateRole(roleBo) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 删除角色
     */
    @SaCheckPermission("thermal:agent:role:remove")
    @SaCheckLogin
    @Log(title = "代理商角色", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return roleService.deleteRole(id) ? R.ok() : R.fail("删除失败，请确保角色下无用户");
    }

    /**
     * 校验角色标识
     */
    @SaCheckLogin
    @GetMapping("/verifyIdent")
    public R<Boolean> verifyIdent(
            @RequestParam String ident,
            @RequestParam(required = false) String companyId) {
        return R.ok(roleService.verifyIdent(ident, companyId));
    }

    /**
     * 校验角色名称
     */
    @SaCheckLogin
    @GetMapping("/verifyName")
    public R<Boolean> verifyName(
            @RequestParam String name,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String id) {
        return R.ok(roleService.verifyName(name, companyId, id));
    }

    /**
     * 查询用户未拥有的角色
     */
    @SaCheckPermission("thermal:agent:role:list")
    @SaCheckLogin
    @GetMapping("/allRole")
    public R<List<AgRole>> allRole(
            @RequestParam String companyId,
            @RequestParam String userId) {
        return R.ok(roleService.listNoRoleByUserId(companyId, userId));
    }

    /**
     * 查询用户已拥有的角色
     */
    @SaCheckPermission("thermal:agent:role:list")
    @SaCheckLogin
    @GetMapping("/allReadlyRole")
    public R<List<AgRole>> allReadlyRole(
            @RequestParam String companyId,
            @RequestParam String userId) {
        return R.ok(roleService.listYesRoleByUserId(companyId, userId));
    }

    /**
     * 查询角色已分配的菜单列表
     * 对应旧系统 /agent/role/findYes
     */
    @SaCheckPermission("thermal:agent:role:list")
    @SaCheckLogin
    @GetMapping("/menus/assigned")
    public R<List<TreeNode>> listAssignedMenus(@RequestParam String roleId) {
        List<SysMenuVo> menus = roleService.listYesMenus(roleId);
        List<TreeNode> nodes = menus.stream()
            .map(this::convertToTreeNode)
            .collect(Collectors.toList());
        return R.ok(TreeUtil.buildByLoop(nodes, BusinessStatus.ROOT_PARENT_ID));
    }

    /**
     * 查询角色未分配的菜单列表
     * 对应旧系统 /agent/role/findNo
     */
    @SaCheckPermission("thermal:agent:role:list")
    @SaCheckLogin
    @GetMapping("/menus/unassigned")
    public R<List<TreeNode>> listUnassignedMenus(@RequestParam String roleId) {
        List<SysMenuVo> menus = roleService.listNoMenus(roleId);
        List<TreeNode> nodes = menus.stream()
            .map(this::convertToTreeNode)
            .collect(Collectors.toList());
        return R.ok(TreeUtil.buildByLoop(nodes, BusinessStatus.ROOT_PARENT_ID));
    }

    /**
     * 分配角色菜单权限
     */
    @SaCheckPermission("thermal:agent:role:edit")
    @SaCheckLogin
    @Log(title = "角色权限分配", businessType = BusinessType.UPDATE)
    @PutMapping("/permission")
    public R<Void> assignPermission(
            @RequestParam String roleId,
            @RequestParam String menuIds) {
        return roleService.assignPermission(roleId, menuIds) ? R.ok() : R.fail("权限分配失败");
    }

    private TreeNode convertToTreeNode(SysMenuVo menu) {
        TreeNode node = new TreeNode();
        node.setId(String.valueOf(menu.getMenuId()));
        node.setParentId(menu.getParentId() == null ? "0" : String.valueOf(menu.getParentId()));
        node.setLabel(menu.getMenuName());
        return node;
    }
}
