package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.vo.PrExpenseVo;

import java.util.List;

/**
 * 单笔收费 Service 接口
 * 迁移自旧系统 SingleChargeService
 */
public interface ISingleChargeService {

    /**
     * 根据手机号或房号获取房屋列表
     */
    List<?> getHouse(String search, String orgId, String buildingId);

    /**
     * 根据房间号获取房屋
     */
    List<?> getHouseRoomId(String search, String orgId, String buildingId, String unitCode, String roomNum);

    /**
     * 根据房屋ID查询费用明细
     */
    List<?> pageList(String houseId);

    /**
     * 查询明细详情
     */
    Object getDetail(String houseId, String code, String group, Integer isFree);

    /**
     * 选择计费周期
     */
    Object selectCycle(String houseId, Integer sums, String group, String code, Integer isFree, Integer index, String ids);

    /**
     * 选择计费年份
     */
    Object selectYear(String houseId, String year, Integer index, String id);

    /**
     * 查询已完成的费用数量
     */
    int getHasFinished(List<Long> ids);

    /**
     * 查询房屋缴费记录
     */
    Object queryPayByHouseId(String houseId);

    /**
     * 更新房屋缴费状态
     */
    void updateHousePayStatus(Long houseId, String orgId);

    /**
     * 执行单笔收费
     * @param expenseVo 收费请求参数
     * @return 交易记录
     */
    Object singleCharge(PrExpenseVo expenseVo);
}
