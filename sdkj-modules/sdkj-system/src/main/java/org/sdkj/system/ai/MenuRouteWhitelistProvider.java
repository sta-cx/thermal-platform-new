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

        // 构建 parentId → children 映射
        Map<Long, List<SysMenu>> childrenMap = new HashMap<>();
        for (SysMenu menu : allMenus) {
            childrenMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
        }

        // 拼接叶子菜单路径
        List<String> routes = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (SystemConstants.TYPE_MENU.equals(menu.getMenuType())) {
                String fullPath = buildFullPath(menu, childrenMap);
                if (fullPath != null && !fullPath.isBlank()) {
                    routes.add(fullPath);
                }
            }
        }
        return routes;
    }

    /**
     * 构建菜单完整路径：从根目录到叶子菜单拼接 path
     */
    private String buildFullPath(SysMenu menu, Map<Long, List<SysMenu>> childrenMap) {
        String path = menu.getPath();
        if (path == null || path.isBlank()) {
            return null;
        }

        Long parentId = menu.getParentId();
        while (parentId != null && parentId != 0L) {
            SysMenu parent = findMenuById(parentId, childrenMap);
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

    private SysMenu findMenuById(Long menuId, Map<Long, List<SysMenu>> childrenMap) {
        for (List<SysMenu> menus : childrenMap.values()) {
            for (SysMenu menu : menus) {
                if (menu.getMenuId().equals(menuId)) {
                    return menu;
                }
            }
        }
        return null;
    }
}
