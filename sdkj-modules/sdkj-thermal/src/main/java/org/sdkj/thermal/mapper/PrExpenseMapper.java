package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.vo.PrExpenseVo;

import java.util.List;
import java.util.Map;

/**
 * 费用明细 Mapper
 * 迁移自旧系统 PrExpenseMapper
 */
@OrgPermission
public interface PrExpenseMapper extends BaseMapperPlus<PrExpense, PrExpenseVo> {

    /**
     * 分页查询费用明细列表
     */
    List<PrExpenseVo> selectPageList(Page<PrExpenseVo> page,
                                     @Param("orgId") String orgId,
                                     @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
                                     @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
                                     @Param("search") String search, @Param("isCharged") String isCharged,
                                     @Param("parkingId") String parkingId, @Param("startTime") String startTime,
                                     @Param("endTime") String endTime);

    /**
     * 查询房屋费用明细列表
     */
    List<PrExpenseVo> selectHouseExpenseList(@Param("orgId") String orgId,
                                             @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
                                             @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
                                             @Param("search") String search);

    /**
     * 查询房屋取暖费明细
     */
    PrExpenseVo selectHeatExpenseByHouseId(@Param("houseId") Long houseId);

    /**
     * 查询已有费用明细的 houseId 列表
     */
    List<String> selectExistingExpenseHouseIds(@Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode);

    /**
     * 批量更新单价
     */
    int updateStepPrice(@Param("orgId") String orgId);

    /** 查询不同的价格公式 */
    List<String> selectDistinctPriceFormula(@Param("orgId") String orgId);

    /** 查询不同的金额公式 */
    List<String> selectDistinctMoneyFormula(@Param("orgId") String orgId);

    /**
     * 按公式批量更新单价
     */
    int updateMoney(@Param("orgId") String orgId, @Param("priceFormula") String priceFormula);

    /**
     * 按公式批量计算应收
     */
    int updateFormula(@Param("orgId") String orgId, @Param("moneyFormula") String moneyFormula);

    /**
     * 按公式批量计算应收（车位）
     */
    int updateFormulaCw(@Param("orgId") String orgId, @Param("moneyFormula") String moneyFormula);

    /**
     * 设置房屋缴费状态
     */
    int updateHousePayStatus(@Param("houseIds") List<Long> houseIds);

    /**
     * 设置计算状态
     */
    int updateCalStatus(@Param("houseId") Long houseId);

    /**
     * 根据ID查询收费标准
     */
    PrStandard selectStandardById(@Param("standardId") Long standardId);

    /** 车位费用分页查询 */
    List<PrExpenseVo> selectParkingPageList(
        Page<PrExpenseVo> page,
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
        @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
        @Param("search") String search, @Param("isCharged") String isCharged,
        @Param("parkingId") String parkingId,
        @Param("startTime") String startTime, @Param("endTime") String endTime);

    /** 车位空间费用查询 */
    List<Map<String, Object>> selectParkingSpaceExpenseList(
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
        @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
        @Param("parkingId") String parkingId, @Param("search") String search);

    /** 全部房屋费用查询 */
    List<Map<String, Object>> selectHouseExpenseAllList(
        @Param("orgId") String orgId,
        @Param("search") String search);

    /** 费用操作日志 */
    List<Map<String, Object>> selectExpenseLog(
        Page<Map<String, Object>> page,
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
        @Param("parentId") String parentId, @Param("type") String type,
        @Param("startTime") String startTime, @Param("endTime") String endTime,
        @Param("search") String search);

    // ========== 阶梯单价计算方法 ==========

    /**
     * 设置建筑面积阶梯单价
     */
    int setStandardPriceJzmj(@Param("standardId") Long standardId);

    /**
     * 设置使用面积阶梯单价
     */
    int setStandardPriceSymj(@Param("standardId") Long standardId);

    /**
     * 设置楼层阶梯单价
     */
    int setStandardPriceLc(@Param("standardId") Long standardId);

    // ========== 滞纳金计算方法 ==========

    /**
     * 滞纳金计算 - 起收日期
     */
    int updateLatefeeQs(@Param("orgId") String orgId,
                        @Param("latefeeFormula") String latefeeFormula, @Param("standardId") Long standardId);

    /**
     * 滞纳金计算 - 到期日期
     */
    int updateLatefeeJs(@Param("orgId") String orgId,
                        @Param("latefeeFormula") String latefeeFormula, @Param("standardId") Long standardId);

    /**
     * 滞纳金计算 - 指定日期
     */
    int updateLatefeeZd(@Param("orgId") String orgId,
                        @Param("latefeeFormula") String latefeeFormula, @Param("standardId") Long standardId,
                        @Param("latefeeStartdate") java.util.Date latefeeStartdate);

    /**
     * 滞纳金计算 - 数据核查
     */
    int updateLatefeeSJHC(@Param("orgId") String orgId,
                          @Param("latefeeFormula") String latefeeFormula, @Param("standardId") Long standardId,
                          @Param("year") String year, @Param("month") String month);

    /**
     * 滞纳金计算后更新最终金额
     */
    int updateFinalMoneyAfterLateFee(@Param("orgId") String orgId,
                                      @Param("standardId") Long standardId);
}
