package org.sdkj.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.HtStrategy;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.mapper.ThermalSysHomeMapper;
import org.sdkj.thermal.service.IHtAlertService;
import org.sdkj.thermal.service.IHtStrategyService;
import org.sdkj.thermal.service.IHtTasksService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页统计数据
 * 迁移自旧系统 SysHomeController
 * 6 项统计:设备/收费/告警/策略/任务/用户。
 * 行级权限:ThermalSysHomeMapper 的 SQL 内已 JOIN pr_data_grant 按 userId 过滤。
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/home")
public class ThermalHomeController {

    private final ThermalSysHomeMapper sysHomeMapper;
    private final IHtAlertService htAlertService;
    private final IHtStrategyService htStrategyService;
    private final IHtTasksService htTasksService;

    /**
     * 首页统计数据
     * 旧端点: POST /home/querHomeData
     * 新端点: GET /thermal/home/statistics
     */
    @SaCheckLogin
    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) String stationPartitionId) {
        Long userId = LoginHelper.getUserId();

        Map<String, Object> result = new HashMap<>();

        // 1. 设备数(6 类仪表总和:户阀/户表/单元阀/单元表/温采器/DTU)
        long deviceCount = parseLongSafe(sysHomeMapper.queryValveNum(userId, stationId, stationPartitionId))
                + parseLongSafe(sysHomeMapper.queryHotArchiveNum(userId, stationId, stationPartitionId))
                + parseLongSafe(sysHomeMapper.queryUnitValveNum(userId, stationId, stationPartitionId))
                + parseLongSafe(sysHomeMapper.queryUnitHotArchiveNum(userId, stationId, stationPartitionId))
                + parseLongSafe(sysHomeMapper.queryTempArchiveNum(userId, stationId, stationPartitionId))
                + parseLongSafe(sysHomeMapper.queryDtuNum(userId, stationId, stationPartitionId));
        result.put("deviceCount", deviceCount);

        // 2. 收费金额(户表累计热量,旧系统以热量近似收费金额)
        result.put("chargeTotal", parseDecimalSafe(sysHomeMapper.queryHotArchiveTotalAll(userId, stationId, stationPartitionId)));

        // 3. 告警数(ht_alert 无 orgId 字段，按租户库全局 count)
        result.put("alertCount", htAlertService.count());

        // 4. 策略数(ht_strategy 无 orgId 字段，按租户库全局 count)
        result.put("strategyCount", htStrategyService.count());

        // 5. 任务数(ht_tasks 有 orgId，按 companyId 过滤)
        LambdaQueryWrapper<HtTasks> taskWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(companyId)) {
            taskWrapper.eq(HtTasks::getOrgId, companyId);
        }
        result.put("taskCount", htTasksService.count(taskWrapper));

        // 6. 用户数(房屋业主数,行级权限按 userId 过滤)
        result.put("userCount", parseLongSafe(sysHomeMapper.queryPrUserNum(userId, stationId, stationPartitionId)));

        result.put("userId", userId);
        return R.ok(result);
    }

    private long parseLongSafe(String s) {
        if (s == null || s.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            log.warn("parseLongSafe failed for value='{}', fallback to 0", s);
            return 0L;
        }
    }

    private BigDecimal parseDecimalSafe(String s) {
        if (s == null || s.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            log.warn("parseDecimalSafe failed for value='{}', fallback to 0", s);
            return BigDecimal.ZERO;
        }
    }
}
