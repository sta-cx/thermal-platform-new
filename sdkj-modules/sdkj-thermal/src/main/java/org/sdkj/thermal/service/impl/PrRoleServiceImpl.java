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
import java.util.stream.Collectors;

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
    public List<AgRole> getAllRoles(String companyId) {
        return agRoleService.lambdaQuery()
            .eq(StrUtil.isNotBlank(companyId), AgRole::getCompanyId, companyId)
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
    public List<AgRole> getRoleByUserId(Long userId) {
        return agRoleService.lambdaQuery()
            .inSql(AgRole::getId,
                "SELECT ur.role_id FROM sys_user_role ur WHERE ur.user_id = " + userId)
            .list();
    }

    /**
     * 批量删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRolesData(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<String> idStrs = ids.stream()
            .map(String::valueOf)
            .collect(Collectors.toList());
        agRoleService.removeByIds(idStrs);
    }

    /**
     * 校验角色标识是否重复（排除自身）
     */
    @Override
    public boolean verifyIdent(String roleKey, Long roleId) {
        return agRoleService.lambdaQuery()
            .eq(AgRole::getIdentifying, roleKey)
            .ne(roleId != null, AgRole::getId, String.valueOf(roleId))
            .exists();
    }

    /**
     * 校验角色名称是否重复（排除自身）
     */
    @Override
    public boolean verifyName(String roleName, Long roleId) {
        return agRoleService.lambdaQuery()
            .eq(AgRole::getName, roleName)
            .ne(roleId != null, AgRole::getId, String.valueOf(roleId))
            .exists();
    }

    /**
     * 查询角色已分配的菜单列表
     */
    @Override
    public List<SysMenuVo> findAllocatedMenus(Long roleId) {
        return agRoleService.listYesMenus(String.valueOf(roleId));
    }

    /**
     * 查询角色未分配的菜单列表
     */
    @Override
    public List<SysMenuVo> findUnallocatedMenus(Long roleId) {
        return agRoleService.listNoMenus(String.valueOf(roleId));
    }

    /**
     * 更新角色权限（清除旧权限 → 批量插入新权限 → 清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permissionUpd(Long roleId, List<Long> menuIds) {
        // 1. 删除角色已有菜单关联
        agRoleMapper.removeRoleMenu(String.valueOf(roleId));

        // 2. 批量插入新菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            String[] ids = menuIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
            agRoleMapper.batchInsertRoleMenu(String.valueOf(roleId), ids);
        }

        // 权限变更后，已被加载到 SaSession 的用户需重新登录才能获取新权限
        // Sa-Token 1.44.0 暂未提供公开的权限缓存清理 API
    }
}
