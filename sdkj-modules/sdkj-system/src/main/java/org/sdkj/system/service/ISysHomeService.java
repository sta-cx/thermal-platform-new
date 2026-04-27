package org.sdkj.system.service;

import java.util.Map;

/**
 * 首页仪表盘 Service 接口
 */
public interface ISysHomeService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 包含6项统计指标的 Map
     */
    Map<String, Object> getDashboardData();

}
