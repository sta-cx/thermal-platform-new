package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrReconciliationDiff;

import java.util.List;

/**
 * 对账差异记录 Mapper
 */
public interface PrReconciliationDiffMapper extends BaseMapperPlus<PrReconciliationDiff, PrReconciliationDiff> {

    /**
     * 通过账单ID查询差异记录
     */
    List<PrReconciliationDiff> selectByBillId(@Param("billId") String billId);

    /**
     * 查询未处理的差异记录
     */
    List<PrReconciliationDiff> selectUnHandleDiffs();
}
