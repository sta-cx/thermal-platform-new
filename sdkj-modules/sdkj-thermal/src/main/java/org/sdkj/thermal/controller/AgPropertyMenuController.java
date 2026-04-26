package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.thermal.domain.bo.AgPropertyMenuBo;
import org.sdkj.thermal.domain.vo.AgPropertyMenuVo;
import org.sdkj.thermal.service.IAgPropertyMenuService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理商物业菜单管理
 * 迁移自旧系统 AgentPropertyMenuController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/property-menu")
public class AgPropertyMenuController {

    private final IAgPropertyMenuService menuService;

    /**
     * 查询已分配的菜单列表
     */
    @SaCheckPermission("thermal:agent:property:menu:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<TreeNode>> list(@RequestParam String companyId) {
        List<AgPropertyMenuVo> menus = menuService.selectAssignedMenus(companyId);
        List<TreeNode> trees = menus.stream()
            .map(this::convertToTreeNode)
            .collect(Collectors.toList());
        return R.ok(TreeUtil.buildByLoop(trees, "-1"));
    }

    /**
     * 查询未分配的菜单列表
     */
    @SaCheckPermission("thermal:agent:property:menu:list")
    @SaCheckLogin
    @GetMapping("/unassigned")
    public R<List<TreeNode>> unassigned(@RequestParam String companyId) {
        List<AgPropertyMenuVo> menus = menuService.selectUnassignedMenus(companyId);
        List<TreeNode> trees = menus.stream()
            .map(this::convertToTreeNode)
            .collect(Collectors.toList());
        return R.ok(TreeUtil.buildByLoop(trees, "-1"));
    }

    /**
     * 更新菜单权限
     */
    @SaCheckPermission("thermal:agent:property:menu:edit")
    @SaCheckLogin
    @Log(title = "物业菜单权限", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody AgPropertyMenuBo menuBo) {
        return menuService.updateMenuPermissions(menuBo) ? R.ok() : R.fail("更新失败");
    }

    private TreeNode convertToTreeNode(AgPropertyMenuVo vo) {
        TreeNode node = new TreeNode();
        node.setId(vo.getMenuId());
        node.setParentId("-1");
        node.setLabel(vo.getMenuName());
        return node;
    }
}
