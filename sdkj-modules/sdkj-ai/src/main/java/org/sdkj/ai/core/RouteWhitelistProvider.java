package org.sdkj.ai.core;

import java.util.List;

/**
 * 提供前端可用路由白名单，用于通用 AI 旁注模式。
 * <p>
 * 实现类位于业务模块（如 sdkj-system），通过 SPI 注入。
 */
public interface RouteWhitelistProvider {

    /**
     * 返回所有可用的前端路由路径列表
     */
    List<String> getAvailableRoutes();
}
