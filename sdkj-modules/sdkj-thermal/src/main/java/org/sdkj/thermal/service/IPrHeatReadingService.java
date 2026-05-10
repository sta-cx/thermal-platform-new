package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatReading;
import org.sdkj.thermal.domain.vo.PrHeatReadingVo;

import java.io.Serializable;
import java.util.List;

public interface IPrHeatReadingService extends IService<PrHeatReading> {

    PrHeatReadingVo selectById(Serializable id);

    TableDataInfo<PrHeatReadingVo> selectPageList(String orgId, String buildingId,
                                                   String unitCode, String meterArcCode, String search,
                                                   String startTime, String endTime, String type,
                                                   String valveType, String parentId, PageQuery pageQuery);

    TableDataInfo<PrHeatReadingVo> selectPageListTrend(String orgId, String startTime,
                                                        String endTime, PageQuery pageQuery);

    /**
     * 查询阀门趋势数据
     * @param meterNums 表号列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态（阀门/热表/温控器）
     * @param orgId 小区ID
     * @param parentId 父ID
     * @return 趋势数据列表
     */
    List<PrHeatReadingVo> selectTrendList(List<String> meterNums, String startTime, String endTime,
                                          String status, String orgId, String parentId);

    /**
     * 查询首页户间阀门趋势图
     * @param stationId 站点ID
     * @param stationPartitionId 站点分区ID
     * @return 趋势数据列表
     */
    List<PrHeatReadingVo> selectHomeTrendList(String stationId, String stationPartitionId);
}
