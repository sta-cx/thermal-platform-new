package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.domain.vo.PrTransactionRecordSubVo;

import java.util.List;
import java.util.Map;

/**
 * 交易记录 Mapper
 */
@OrgPermission
public interface PrTransactionRecordMapper extends BaseMapperPlus<PrTransactionRecord, PrTransactionRecordVo> {

    /**
     * 分页查询交易记录
     */
    List<PrTransactionRecordVo> selectPageList(
        Page<PrTransactionRecordVo> page,
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

    /** 退费记录查询 */
    List<Map<String, Object>> selectRefundList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("startTime") String startTime, @Param("endTime") String endTime,
        @Param("search") String search);

    /** 水费交易查询 */
    List<Map<String, Object>> selectWaterList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("startTime") String startTime, @Param("endTime") String endTime,
        @Param("search") String search);

    /** 电费交易查询 */
    List<Map<String, Object>> selectEleList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("startTime") String startTime, @Param("endTime") String endTime,
        @Param("search") String search);

    /** 写卡日志查询 */
    List<Map<String, Object>> selectCardLogList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("startTime") String startTime, @Param("endTime") String endTime,
        @Param("search") String search, @Param("type") String type, @Param("createBy") String createBy);

    /** 写卡操作人列表 */
    List<Map<String, Object>> selectCardLogOperators(
        @Param("companyId") String companyId, @Param("orgId") String orgId);

    /** 未收款查询 */
    List<Map<String, Object>> selectUncollList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("startTime") String startTime, @Param("endTime") String endTime,
        @Param("search") String search);

    /** 本月收款总额 */
    Map<String, Object> selectThisMonthTotal(
        @Param("companyId") String companyId, @Param("userId") String userId);

    /** 本月收款明细（按费用项） */
    List<Map<String, Object>> selectThisMonthDetails(
        @Param("companyId") String companyId, @Param("userId") String userId);

    /** 本月收款明细（按费用分类） */
    List<Map<String, Object>> selectThisMonthDetailsVarious(
        @Param("companyId") String companyId, @Param("userId") String userId);
}
