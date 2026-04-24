package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.vo.PrExpenseVo;

import java.util.List;

/**
 * 费用明细 Mapper
 * 迁移自旧系统 PrExpenseMapper
 */
public interface PrExpenseMapper extends BaseMapperPlus<PrExpense, PrExpenseVo> {

    /**
     * 分页查询费用明细列表
     */
    List<PrExpenseVo> selectPageList(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                     @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
                                     @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
                                     @Param("search") String search, @Param("isCharged") String isCharged,
                                     @Param("parkingId") String parkingId, @Param("startTime") String startTime,
                                     @Param("endTime") String endTime);

    /**
     * 查询房屋费用明细列表
     */
    List<PrExpenseVo> selectHouseExpenseList(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                             @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
                                             @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
                                             @Param("search") String search);

    /**
     * 查询房屋取暖费明细
     */
    PrExpenseVo selectHeatExpenseByHouseId(@Param("houseId") String houseId);

    /**
     * 查询已有费用明细的 houseId 列表
     */
    List<String> selectExistingExpenseHouseIds(@Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode);

    /**
     * 批量更新单价
     */
    int updateStepPrice(@Param("companyId") String companyId, @Param("orgId") String orgId);

    /**
     * 批量更新金额
     */
    int updateMoney(@Param("companyId") String companyId, @Param("orgId") String orgId);

    /**
     * 批量计算公式
     */
    int updateFormula(@Param("companyId") String companyId, @Param("orgId") String orgId);

    /**
     * 批量计算公式（车位）
     */
    int updateFormulaCw(@Param("companyId") String companyId, @Param("orgId") String orgId);

    /**
     * 设置房屋缴费状态
     */
    int updateHousePayStatus(@Param("houseIds") List<String> houseIds);

    /**
     * 设置计算状态
     */
    int updateCalStatus(@Param("houseId") String houseId);

    /**
     * 根据ID查询收费标准
     */
    PrStandard selectStandardById(@Param("standardId") String standardId);
}
