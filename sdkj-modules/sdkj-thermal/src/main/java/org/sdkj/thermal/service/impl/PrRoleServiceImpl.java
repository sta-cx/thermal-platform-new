package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.mapper.AgRoleMapper;
import org.sdkj.thermal.service.IAgRoleService;
import org.sdkj.thermal.service.IPrRoleService;
import org.sdkj.system.domain.vo.SysMenuVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物业角色管理 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrRoleServiceImpl implements IPrRoleService {

    private final IAgRoleService agRoleService;
    private final AgRoleMapper agRoleMapper;

    /**
     * 根据公司ID查询所有角色
     */
    @Override
    public List<AgRole> getAllRoles() {
        return agRoleService.lambdaQuery()
            .list();
    }

    /**
     * 查询当前用户创建的角色
     */
    @Override
    public List<AgRole> getAllRolesByCreate(Long userId) {
        return agRoleService.lambdaQuery()
            .eq(userId != null, AgRole::getCreateBy, userId)
            .list();
    }

    /**
     * 查询用户已拥有的角色
     */
    @Override
    public List<AgRole> getRoleByUserId(String userId) {
        return agRoleService.lambdaQuery()
            .apply("id IN (SELECT ur.role_id FROM sys_user_role ur WHERE ur.user_id = {0})", userId)
            .list();
    }

    /**
     * 批量删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRolesData(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        agRoleService.removeByIds(ids);
    }

    /**
     * 校验角色标识是否重复（排除自身）
     */
    @Override
    public boolean verifyIdent(String roleKey, String roleId) {
        return agRoleService.lambdaQuery()
            .eq(AgRole::getIdentifying, roleKey)
            .ne(roleId != null, AgRole::getId, roleId)
            .exists();
    }

    /**
     * 校验角色名称是否重复（排除自身）
     */
    @Override
    public boolean verifyName(String roleName, String roleId) {
        return agRoleService.lambdaQuery()
            .eq(AgRole::getName, roleName)
            .ne(roleId != null, AgRole::getId, roleId)
            .exists();
    }

    /**
     * 查询角色已分配的菜单列表
     */
    @Override
    public List<SysMenuVo> findAllocatedMenus(String roleId) {
        return agRoleService.listYesMenus(roleId);
    }

    /**
     * 查询角色未分配的菜单列表
     */
    @Override
    public List<SysMenuVo> findUnallocatedMenus(String roleId) {
        return agRoleService.listNoMenus(roleId);
    }

    /**
     * 更新角色权限（清除旧权限 → 批量插入新权限 → 清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permissionUpd(String roleId, List<Long> menuIds) {
        // 1. 删除角色已有菜单关联
        agRoleMapper.removeRoleMenu(roleId);

        // 2. 批量插入新菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            String[] ids = menuIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
            agRoleMapper.batchInsertRoleMenu(roleId, ids);
        }

        // 权限变更后，已被加载到 SaSession 的用户需重新登录才能获取新权限
        // Sa-Token 1.44.0 暂未提供公开的权限缓存清理 API
    }
}
