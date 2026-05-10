package org.sdkj.thermal.service;

import java.util.Map;

public interface ISysHomeService {

    /**
     * 聚合首页大屏数据（7个数据源并行查询）
     */
    Map<String, Object> aggregateHomeData(Long userId, String stationId, String stationPartitionId);
}
