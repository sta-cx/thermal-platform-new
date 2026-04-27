package org.sdkj.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.system.mapper.SysHomeMapper;
import org.sdkj.system.service.ISysHomeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页仪表盘 Service 实现
 * 顺序执行数据库查询，避免跨模块异步复杂性
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysHomeServiceImpl implements ISysHomeService {

    private final SysHomeMapper sysHomeMapper;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("meterCount", sysHomeMapper.selectMeterCount());
        } catch (Exception e) {
            log.error("查询仪表数量统计失败", e);
            result.put("meterCount", java.util.Collections.emptyList());
        }

        try {
            result.put("alertCount", sysHomeMapper.selectAlertCount());
        } catch (Exception e) {
            log.error("查询报警数量统计失败", e);
            result.put("alertCount", java.util.Collections.emptyList());
        }

        try {
            result.put("expenseSummary", sysHomeMapper.selectExpenseSummary());
        } catch (Exception e) {
            log.error("查询费用汇总失败", e);
            result.put("expenseSummary", new HashMap<>());
        }

        try {
            result.put("houseStatistics", sysHomeMapper.selectHouseStatistics());
        } catch (Exception e) {
            log.error("查询房屋统计失败", e);
            result.put("houseStatistics", new HashMap<>());
        }

        try {
            result.put("deviceOnlineRate", sysHomeMapper.selectDeviceOnlineRate());
        } catch (Exception e) {
            log.error("查询设备在线率失败", e);
            result.put("deviceOnlineRate", new HashMap<>());
        }

        try {
            result.put("paymentSummary", sysHomeMapper.selectPaymentSummary());
        } catch (Exception e) {
            log.error("查询交易金额汇总失败", e);
            result.put("paymentSummary", new HashMap<>());
        }

        return result;
    }

}
