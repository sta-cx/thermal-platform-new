package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.domain.vo.PrTransactionRecordSubVo;

import java.util.List;
import java.util.Map;

/**
 * 交易记录 Mapper
 */
public interface PrTransactionRecordMapper extends BaseMapperPlus<PrTransactionRecord, PrTransactionRecordVo> {

    /**
     * 分页查询交易记录
     */
    List<PrTransactionRecordVo> selectPageList(
        @Param("search") String search,
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime,
        @Param("status") String status);

    /**
     * 根据主记录ID查询子记录明细
     */
    List<PrTransactionRecordSubVo> selectDetailByMainId(@Param("mainId") String mainId);

    /**
     * 冲销记录（当月可冲销）
     */
    int revokeRecord(@Param("recordId") String recordId);

    /**
     * 作废记录
     */
    int invalidRecord(@Param("recordId") String recordId);

    /**
     * 综合统计
     */
    Map<String, Object> selectComprehensiveStats(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime);

    /**
     * 收款统计
     */
    Map<String, Object> selectReceivedStats(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime);

    /**
     * 欠费统计
     */
    Map<String, Object> selectArrearsStats(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId);

    /**
     * 日报查询
     */
    List<PrTransactionRecordVo> selectDailyReport(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("date") String date);
}
