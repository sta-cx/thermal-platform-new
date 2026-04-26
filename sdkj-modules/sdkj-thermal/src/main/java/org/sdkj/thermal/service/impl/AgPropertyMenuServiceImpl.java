package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgPropertyMenu;
import org.sdkj.thermal.domain.bo.AgPropertyMenuBo;
import org.sdkj.thermal.domain.vo.AgPropertyMenuVo;
import org.sdkj.thermal.mapper.AgPropertyMenuMapper;
import org.sdkj.thermal.service.IAgPropertyMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 代理商物业菜单 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AgPropertyMenuServiceImpl extends ServiceImpl<AgPropertyMenuMapper, AgPropertyMenu> implements IAgPropertyMenuService {

    private final AgPropertyMenuMapper menuMapper;

    @Override
    public List<AgPropertyMenuVo> selectAssignedMenus(String companyId) {
        return menuMapper.selectAssignedMenus(companyId);
    }

    @Override
    public List<AgPropertyMenuVo> selectUnassignedMenus(String companyId) {
        return menuMapper.selectUnassignedMenus(companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenuPermissions(AgPropertyMenuBo menuBo) {
        menuMapper.deletePropertyMenus(menuBo.getCompanyId());

        if (StrUtil.isBlank(menuBo.getMenuIds())) {
            return true;
        }

        String[] menuIdArray = menuBo.getMenuIds().split(",");
        return menuMapper.insertPropertyMenus(menuBo.getCompanyId(), menuIdArray) > 0;
    }
}
