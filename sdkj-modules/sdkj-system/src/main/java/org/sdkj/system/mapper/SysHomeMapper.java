package org.sdkj.system.mapper;

import java.util.List;
import java.util.Map;

/**
 * 首页仪表盘 Mapper
 * 通过原始 SQL 跨模块查询热力/物业相关表
 */
public interface SysHomeMapper {

    /**
     * 按仪表类型统计数量
     */
    List<Map<String, Object>> selectMeterCount();

    /**
     * 按报警类型统计数量
     */
    List<Map<String, Object>> selectAlertCount();

    /**
     * 费用汇总统计（总金额、已收、未收）
     */
    Map<String, Object> selectExpenseSummary();

    /**
     * 房屋统计（总户数、已收费、未收费）
     */
    Map<String, Object> selectHouseStatistics();

    /**
     * 设备在线率统计
     */
    Map<String, Object> selectDeviceOnlineRate();

    /**
     * 交易金额汇总
     */
    Map<String, Object> selectPaymentSummary();

}
