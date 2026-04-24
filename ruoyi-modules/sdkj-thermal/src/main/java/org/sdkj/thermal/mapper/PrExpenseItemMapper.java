package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;

import java.util.List;

/**
 * 费用项目 Mapper
 * 迁移自旧系统 PrExpenseItemMapper
 */
public interface PrExpenseItemMapper extends BaseMapperPlus<PrExpenseItem, PrExpenseItemVo> {

    /**
     * 查询小区收费项目列表（关联收费标准数量）
     */
    List<PrExpenseItemVo> selectPageList(@Param("orgId") String orgId, @Param("itemGroup") String itemGroup);

    /**
     * 查询项目类别最大 itemCode
     */
    String queryMaxItemCode(@Param("itemGroup") String itemGroup, @Param("orgId") String orgId);

    /**
     * 根据 ID 查询费用项目详情
     */
    PrExpenseItemVo selectById(@Param("id") String id);

    /**
     * 按条件查询费用项目
     */
    List<PrExpenseItemVo> selectByItemCode(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                           @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode);

    /**
     * 按费项分组查询列表
     */
    List<PrExpenseItemVo> selectByItemGroup(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                            @Param("itemGroup") String itemGroup);

    /**
     * 按公司/小区查询费用项目
     */
    List<PrExpenseItemVo> selectByCompanyAndOrg(@Param("companyId") String companyId, @Param("orgId") String orgId);

    /**
     * 按用户查询费用项目（关联账户表）
     */
    List<PrExpenseItemVo> selectByItemGroupAndUserId(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                                     @Param("itemGroup") String itemGroup, @Param("userId") String userId);

    /**
     * 检查项目名称是否重复
     */
    List<PrExpenseItemVo> checkItemName(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                        @Param("itemName") String itemName, @Param("id") String id);
}
