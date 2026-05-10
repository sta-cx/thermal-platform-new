package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.vo.PrHouseExpenseVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;

import java.util.List;

/**
 * 房屋费用项目绑定 Service 接口
 * 迁移自旧系统 PrHouseExpenseService
 */
public interface IPrHouseExpenseService extends IService<PrHouseExpense> {

    /**
     * 分页查询房屋-费目绑定列表
     */
    TableDataInfo<PrHouseExpenseVo> selectPageList(String orgId, String buildingId,
                                                   String unitCode, String itemGroup, String itemCode,
                                                   String search, PageQuery pageQuery);

    /**
     * 查询未绑定该费项的房屋
     */
    List<PrHouseVo> selectUnboundHouses(String orgId, String buildingId, String unitCode,
                                        String itemGroup, String itemCode, String search);

    /**
     * 查询单个房屋所有未绑定费项
     */
    List<PrExpenseItemVo> selectUnboundItems(String orgId, String roomNum);

    /**
     * 批量保存房屋-费目绑定
     */
    boolean batchInsert(List<PrHouseExpense> list, String itemGroup, String itemCode, String orgId);

    /**
     * 批量更新绑定信息
     */
    boolean batchUpdate(List<PrHouseExpense> list);

    /**
     * 批量删除绑定
     */
    boolean batchDelete(List<PrHouseExpense> list);
}
