package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.AgPropertyMenu;
import org.sdkj.thermal.domain.bo.AgPropertyMenuBo;
import org.sdkj.thermal.domain.vo.AgPropertyMenuVo;

import java.util.List;

/**
 * 代理商物业菜单 Service
 */
public interface IAgPropertyMenuService extends IService<AgPropertyMenu> {

    /**
     * 查询已分配的菜单列表
     */
    List<AgPropertyMenuVo> selectAssignedMenus(String companyId);

    /**
     * 查询未分配的菜单列表
     */
    List<AgPropertyMenuVo> selectUnassignedMenus(String companyId);

    /**
     * 更新菜单权限
     */
    boolean updateMenuPermissions(AgPropertyMenuBo menuBo);
}
