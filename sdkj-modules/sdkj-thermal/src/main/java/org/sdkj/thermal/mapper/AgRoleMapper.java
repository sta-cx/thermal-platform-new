package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.vo.AgRoleVo;
import org.sdkj.system.domain.vo.SysMenuVo;

import java.util.List;

/**
 * 代理商角色 Mapper
 * 基础 CRUD 使用 BaseMapperPlus 提供，仅保留复杂查询
 */
public interface AgRoleMapper extends BaseMapperPlus<AgRole, AgRoleVo> {

    /**
     * 查询用户未拥有的角色
     */
    List<AgRole> selectNoRoleByUserId(@Param("companyId") String companyId, @Param("userId") String userId);

    /**
     * 查询用户已拥有的角色
     */
    List<AgRole> selectYesRoleByUserId(@Param("companyId") String companyId, @Param("userId") String userId);

    /**
     * 移除角色所有菜单权限
     */
    int removeRoleMenu(@Param("roleId") String roleId);

    /**
     * 批量插入角色菜单权限
     */
    int batchInsertRoleMenu(@Param("roleId") String roleId, @Param("menuIds") String[] menuIds);

    /**
     * 检查角色下是否有用户
     */
    int hasUser(@Param("id") String id);

    /**
     * 查询角色已分配的菜单列表
     */
    List<SysMenuVo> selectYesMenuByRoleId(@Param("roleId") String roleId);

    /**
     * 查询角色未分配的菜单列表
     */
    List<SysMenuVo> selectNoMenuByRoleId(@Param("roleId") String roleId);

    /**
     * 查询所有代理商菜单
     */
    List<SysMenuVo> selectAgentMenus();
}
