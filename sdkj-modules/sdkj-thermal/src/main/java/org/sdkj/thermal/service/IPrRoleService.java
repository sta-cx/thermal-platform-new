package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.AgRole;
import org.sdkj.system.domain.vo.SysMenuVo;

import java.util.List;

/**
 * 物业角色管理 Service 接口
 * 提供物业角色权限相关的扩展操作
 */
public interface IPrRoleService {

    /**
     * 根据公司ID查询所有角色
     */
    List<AgRole> getAllRoles(String companyId);

    /**
     * 查询当前用户创建的角色
     */
    List<AgRole> getAllRolesByCreate(Long userId);

    /**
     * 查询用户已拥有的角色
     */
    List<AgRole> getRoleByUserId(Long userId);

    /**
     * 批量删除角色
     */
    void deleteRolesData(List<Long> ids);

    /**
     * 校验角色标识是否重复（排除自身）
     */
    boolean verifyIdent(String roleKey, Long roleId);

    /**
     * 校验角色名称是否重复（排除自身）
     */
    boolean verifyName(String roleName, Long roleId);

    /**
     * 查询角色已分配的菜单列表
     */
    List<SysMenuVo> findAllocatedMenus(Long roleId);

    /**
     * 查询角色未分配的菜单列表
     */
    List<SysMenuVo> findUnallocatedMenus(Long roleId);

    /**
     * 更新角色权限（清除旧权限 → 批量插入新权限 → 清除缓存）
     */
    void permissionUpd(Long roleId, List<Long> menuIds);
}
