package org.sdkj.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.system.service.ISysHomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 首页仪表盘
 * 提供系统级首页数据统计接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController("systemSysHomeController")
@RequestMapping("/system/home")
public class SysHomeController {

    private final ISysHomeService sysHomeService;

    /**
     * 获取仪表盘统计数据
     * 返回6项统计指标：meterCount, alertCount, expenseSummary,
     * houseStatistics, deviceOnlineRate, paymentSummary
     */
    @SaCheckLogin
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        try {
            Map<String, Object> result = sysHomeService.getDashboardData();
            return R.ok(result);
        } catch (Exception e) {
            log.error("查询仪表盘数据失败", e);
            return R.fail("查询仪表盘数据失败: " + e.getMessage());
        }
    }

}
