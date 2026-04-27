package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHeatReading;
import org.sdkj.thermal.domain.vo.PrHeatReadingCopy1Vo;
import org.sdkj.thermal.domain.vo.PrHeatReadingLabelVo;
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

    // ========== PrHeatReadingCopy1 远传抄表副本查询 ==========

    /**
     * 根据标签列表查询抄表副本数据（聚合查询）
     * @param labels   标签列表
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 抄表副本数据列表
     */
    List<PrHeatReadingCopy1Vo> selectPageListCopy1(@Param("labels") List<PrHeatReadingLabelVo> labels,
                                                   @Param("startTime") String startTime,
                                                   @Param("endTime") String endTime);

    /**
     * 分页查询热表配表读数情况
     * @param page        分页参数
     * @param companyId   公司ID
     * @param orgId       小区ID
     * @param buildingId  楼栋ID
     * @param unitCode    单元编码
     * @param meterArcCode 热表档案编号
     * @param search      搜索关键字
     * @return 分页结果
     */
    Page<PrHeatReadingCopy1Vo> selectPageHeatReadingList(Page<?> page,
                                                         @Param("companyId") String companyId,
                                                         @Param("orgId") String orgId,
                                                         @Param("buildingId") String buildingId,
                                                         @Param("unitCode") String unitCode,
                                                         @Param("meterArcCode") String meterArcCode,
                                                         @Param("search") String search);
}
