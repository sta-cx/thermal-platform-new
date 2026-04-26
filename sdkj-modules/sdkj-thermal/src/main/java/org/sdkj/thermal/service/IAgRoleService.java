package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.bo.AgRoleBo;
import org.sdkj.system.domain.vo.SysMenuVo;

import java.util.List;

/**
 * 代理商角色 Service
 */
public interface IAgRoleService extends IService<AgRole> {

    /**
     * 分页查询角色列表
     */
    IPage<AgRole> listRoles(Page page, String name, String companyId);

    /**
     * 新增角色
     */
    boolean insertRole(AgRoleBo roleBo);

    /**
     * 更新角色
     */
    boolean updateRole(AgRoleBo roleBo);

    /**
     * 删除角色
     */
    boolean deleteRole(String id);

    /**
     * 校验角色标识是否重复
     */
    boolean verifyIdent(String ident, String companyId);

    /**
     * 校验角色名称是否重复
     */
    boolean verifyName(String name, String companyId, String id);

    /**
     * 查询用户未拥有的角色
     */
    List<AgRole> listNoRoleByUserId(String companyId, String userId);

    /**
     * 查询用户已拥有的角色
     */
    List<AgRole> listYesRoleByUserId(String companyId, String userId);

    /**
     * 分配角色菜单权限
     */
    boolean assignPermission(String roleId, String menuIds);

    /**
     * 查询角色已分配的菜单列表
     */
    List<SysMenuVo> listYesMenus(String roleId);

    /**
     * 查询角色未分配的菜单列表
     */
    List<SysMenuVo> listNoMenus(String roleId);
}
