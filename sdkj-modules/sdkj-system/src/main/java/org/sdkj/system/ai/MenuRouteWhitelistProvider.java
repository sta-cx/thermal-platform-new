package org.sdkj.system.ai;

import lombok.RequiredArgsConstructor;
import org.sdkj.ai.core.RouteWhitelistProvider;
import org.sdkj.common.core.constant.SystemConstants;
import org.sdkj.system.domain.SysMenu;
import org.sdkj.system.mapper.SysMenuMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 sys_menu 查询叶子菜单，拼接为前端路由路径白名单。
 * 与前端 backMenuToVbenMenu 的 parentPath + "/" + childPath 逻辑一致。
 */
@Component
@RequiredArgsConstructor
public class MenuRouteWhitelistProvider implements RouteWhitelistProvider {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<String> getAvailableRoutes() {
        List<SysMenu> allMenus = sysMenuMapper.selectMenuTreeAll();

        Map<Long, SysMenu> menuByIdMap = new HashMap<>();
        for (SysMenu menu : allMenus) {
            menuByIdMap.put(menu.getMenuId(), menu);
        }

        List<String> routes = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (SystemConstants.TYPE_MENU.equals(menu.getMenuType())) {
                String fullPath = buildFullPath(menu, menuByIdMap);
                if (fullPath != null && !fullPath.isBlank()) {
                    routes.add(fullPath);
                }
            }
        }
        return routes;
    }

    private String buildFullPath(SysMenu menu, Map<Long, SysMenu> menuByIdMap) {
        String path = menu.getPath();
        if (path == null || path.isBlank()) {
            return null;
        }

        Long parentId = menu.getParentId();
        while (parentId != null && parentId != 0L) {
            SysMenu parent = menuByIdMap.get(parentId);
            if (parent == null) break;
            String parentPath = parent.getPath();
            if (parentPath != null && !parentPath.isBlank()) {
                path = parentPath + "/" + path;
            }
            parentId = parent.getParentId();
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}
