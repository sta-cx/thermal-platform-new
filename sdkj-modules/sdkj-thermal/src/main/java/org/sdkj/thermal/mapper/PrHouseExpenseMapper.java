package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.vo.PrHouseExpenseVo;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;

import java.util.List;

/**
 * 房屋费用项目绑定 Mapper
 * 迁移自旧系统 PrHouseExpenseMapper
 */
public interface PrHouseExpenseMapper extends BaseMapperPlus<PrHouseExpense, PrHouseExpenseVo> {

    /**
     * 分页查询房屋-费目绑定列表
     */
    List<PrHouseExpenseVo> selectPageList(Page<PrHouseExpenseVo> page,
                                          @Param("companyId") String companyId, @Param("orgId") String orgId,
                                          @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
                                          @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
                                          @Param("search") String search);

    /**
     * 查询未绑定该费项的房屋
     */
    List<PrHouseVo> selectUnboundHouses(@Param("orgId") String orgId, @Param("buildingId") String buildingId,
                                        @Param("unitCode") String unitCode, @Param("itemGroup") String itemGroup,
                                        @Param("itemCode") String itemCode, @Param("search") String search);

    /**
     * 查询单个房屋所有未绑定费项
     */
    List<PrExpenseItemVo> selectUnboundItems(@Param("orgId") String orgId, @Param("roomNum") String roomNum);
}
