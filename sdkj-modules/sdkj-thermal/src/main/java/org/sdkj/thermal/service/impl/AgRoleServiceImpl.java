package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.bo.AgRoleBo;
import org.sdkj.thermal.mapper.AgRoleMapper;
import org.sdkj.thermal.service.IAgRoleService;
import org.sdkj.system.domain.vo.SysMenuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理商角色 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AgRoleServiceImpl extends ServiceImpl<AgRoleMapper, AgRole> implements IAgRoleService {

    private final AgRoleMapper roleMapper;

    @Override
    public IPage<AgRole> listRoles(Page page, String name, String companyId) {
        LambdaQueryWrapper<AgRole> wrapper = new LambdaQueryWrapper<AgRole>()
            .like(StrUtil.isNotBlank(name), AgRole::getName, name)
            .eq(StrUtil.isNotBlank(companyId), AgRole::getCompanyId, companyId)
            .orderByDesc(AgRole::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean insertRole(AgRoleBo roleBo) {
        AgRole role = new AgRole();
        BeanUtils.copyProperties(roleBo, role);
        role.setIsSuper(0);
        if (StrUtil.isBlank(role.getIdentifying())) {
            role.setIdentifying("ROLE_" + role.getName());
        }
        return save(role);
    }

    @Override
    public boolean updateRole(AgRoleBo roleBo) {
        AgRole role = new AgRole();
        BeanUtils.copyProperties(roleBo, role);
        if (StrUtil.isBlank(role.getIdentifying())) {
            role.setIdentifying("ROLE_" + role.getName());
        }
        return updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String id) {
        if (hasUser(id)) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public boolean verifyIdent(String ident, String companyId) {
        return lambdaQuery()
            .eq(AgRole::getIdentifying, ident)
            .eq(StrUtil.isNotBlank(companyId), AgRole::getCompanyId, companyId)
            .exists();
    }

    @Override
    public boolean verifyName(String name, String companyId, String id) {
        return lambdaQuery()
            .eq(AgRole::getName, name)
            .eq(StrUtil.isNotBlank(companyId), AgRole::getCompanyId, companyId)
            .ne(StrUtil.isNotBlank(id), AgRole::getId, id)
            .exists();
    }

    @Override
    public List<AgRole> listNoRoleByUserId(String companyId, String userId) {
        return roleMapper.selectNoRoleByUserId(companyId, userId);
    }

    @Override
    public List<AgRole> listYesRoleByUserId(String companyId, String userId) {
        return roleMapper.selectYesRoleByUserId(companyId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermission(String roleId, String menuIds) {
        roleMapper.removeRoleMenu(roleId);
        if (StrUtil.isBlank(menuIds)) {
            return true;
        }
        String[] ids = menuIds.split(",");
        return roleMapper.batchInsertRoleMenu(roleId, ids) > 0;
    }

    @Override
    public List<SysMenuVo> listYesMenus(String roleId) {
        return roleMapper.selectYesMenuByRoleId(roleId);
    }

    @Override
    public List<SysMenuVo> listNoMenus(String roleId) {
        List<SysMenuVo> noMenus = roleMapper.selectNoMenuByRoleId(roleId);
        if (noMenus.isEmpty()) {
            return noMenus;
        }
        // 获取所有菜单并构建父节点关系
        List<SysMenuVo> allMenus = roleMapper.selectAgentMenus();
        Map<Long, SysMenuVo> menuMap = new HashMap<>();
        for (SysMenuVo menu : allMenus) {
            menuMap.put(menu.getMenuId(), menu);
        }
        // 为未分配的菜单添加所有父节点
        Map<Long, SysMenuVo> parentMap = new HashMap<>();
        for (SysMenuVo menu : noMenus) {
            addParentMenus(menu, menuMap, parentMap);
        }
        return List.copyOf(parentMap.values());
    }

    private void addParentMenus(SysMenuVo menu, Map<Long, SysMenuVo> menuMap, Map<Long, SysMenuVo> parentMap) {
        if (menu.getMenuId().equals(0L) || menu.getParentId() == null) {
            parentMap.put(menu.getMenuId(), menu);
            return;
        }
        parentMap.put(menu.getMenuId(), menu);
        Long parentId = menu.getParentId();
        while (parentId != null && !parentId.equals(0L)) {
            SysMenuVo parent = menuMap.get(parentId);
            if (parent == null) {
                break;
            }
            if (!parentMap.containsKey(parent.getMenuId())) {
                parentMap.put(parent.getMenuId(), parent);
            }
            parentId = parent.getParentId();
        }
    }

    private boolean hasUser(String roleId) {
        return roleMapper.hasUser(roleId) > 0;
    }
}
