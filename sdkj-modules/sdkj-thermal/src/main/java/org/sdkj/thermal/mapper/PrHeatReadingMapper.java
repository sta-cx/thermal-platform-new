package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHeatReading;
import org.sdkj.thermal.domain.vo.PrHeatReadingVo;

import java.util.List;

/**
 * 热表抄表记录 Mapper
 */
public interface PrHeatReadingMapper extends BaseMapperPlus<PrHeatReading, PrHeatReadingVo> {

    /**
     * 查询阀门趋势数据
     * @param meterNums 表号列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param companyId 公司ID
     * @param status 状态（阀门/热表/温控器）
     * @param orgId 小区ID
     * @param parentId 父ID
     * @return 趋势数据列表
     */
    List<PrHeatReadingVo> selectTrendList(@Param("meterNums") List<String> meterNums,
                                          @Param("startTime") String startTime,
                                          @Param("endTime") String endTime,
                                          @Param("companyId") String companyId,
                                          @Param("status") String status,
                                          @Param("orgId") String orgId,
                                          @Param("parentId") String parentId);

    /**
     * 查询首页户间阀门趋势图
     * @param stationId 站点ID
     * @param stationPartitionId 站点分区ID
     * @param companyId 公司ID
     * @return 趋势数据列表
     */
    List<PrHeatReadingVo> selectHomeTrendList(@Param("stationId") String stationId,
                                              @Param("stationPartitionId") String stationPartitionId,
                                              @Param("companyId") String companyId);
}
