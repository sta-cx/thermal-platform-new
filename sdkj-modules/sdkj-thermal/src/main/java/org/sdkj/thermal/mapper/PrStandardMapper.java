package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.PrStandardPrice;
import org.sdkj.thermal.domain.vo.PrStandardVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费标准 Mapper
 * 迁移自旧系统 PrStandardMapper
 */
@OrgPermission
public interface PrStandardMapper extends BaseMapperPlus<PrStandard, PrStandardVo> {

    /**
     * 分页查询收费标准列表（关联费目名称和单价公式）
     */
    List<PrStandardVo> selectPageList(Page<PrStandardVo> page, @Param("orgId") String orgId, @Param("type") String type);

    /**
     * 按 itemCode 统计收费标准数量
     */
    long countByItemCode(@Param("itemCode") String itemCode, @Param("orgId") String orgId);

    /**
     * 根据 ID 查询收费标准详情
     */
    PrStandardVo selectDetailById(@Param("id") String id);

    /**
     * 根据 itemCode 查询收费标准列表
     */
    List<PrStandardVo> selectByItemCode(@Param("itemCode") String itemCode, @Param("orgId") String orgId);

    /**
     * 查询电表收费标准
     */
    List<PrStandardVo> selectEleStandard(@Param("orgId") String orgId);

    /**
     * 查询水表收费标准
     */
    List<PrStandardVo> selectWaterStandard(@Param("orgId") String orgId);

    /**
     * 查询热力收费标准
     */
    List<PrStandardVo> selectHeatStandard(@Param("orgId") String orgId);

    /**
     * 查询标准单价列表（grade=1）
     */
    List<PrStandardPrice> selectPriceList(@Param("standardId") Long standardId);

    /**
     * 查询标准单价完整列表（所有 grade，包含 step12）
     */
    List<PrStandardPrice> selectPriceListAll(@Param("standardId") Long standardId);

    /**
     * 检查标准名称是否重复
     */
    List<PrStandardVo> checkName(@Param("orgId") String orgId,
                                 @Param("itemGroup") String itemGroup, @Param("name") String name, @Param("id") String id);

    /**
     * 按月购买次数
     */
    int getPurchaseNumMonth(@Param("meterId") String meterId, @Param("orgId") String orgId);

    /**
     * 按月已用金额
     */
    BigDecimal getPurchaseAmountMonth(@Param("meterId") String meterId, @Param("orgId") String orgId);

    /**
     * 按季度购买次数
     */
    int getPurchaseNumQuarter(@Param("meterId") String meterId, @Param("orgId") String orgId);

    /**
     * 按季度已用金额
     */
    BigDecimal getPurchaseAmountQuarter(@Param("meterId") String meterId, @Param("orgId") String orgId);

    /**
     * 按年购买次数
     */
    int getPurchaseNumYear(@Param("meterId") String meterId, @Param("orgId") String orgId);

    /**
     * 按年已用金额
     */
    BigDecimal getPurchaseAmountYear(@Param("meterId") String meterId, @Param("orgId") String orgId);

    /**
     * 根据标准 ID 查询关联的费用项目
     */
    PrExpenseItem selectExpenseItemByStandardId(@Param("orgId") String orgId, @Param("standardId") String standardId);

    /**
     * 按项目名称查询收费标准列表
     */
    List<PrStandardVo> selectByItemName(Page<PrStandardVo> page, @Param("orgIdCopy") String orgIdCopy, @Param("itemName") String itemName);

    /**
     * 检查收费标准是否被房屋关联
     */
    int countHouseBindings(@Param("id") Long id);

    /**
     * 删除标准单价
     */
    int deletePricesByStandardId(@Param("id") Long id);

    /**
     * 批量插入标准单价
     */
    int insertPriceList(@Param("list") List<PrStandardPrice> priceList);
}
