package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.service.ISysHomeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 首页数据大屏
 * 迁移自旧系统 SysHomeController
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/home")
public class SysHomeController {

    private final ISysHomeService sysHomeService;

    @SaCheckLogin
    @PostMapping("/dashboard")
    public R<Map<String, Object>> dashboard(@RequestParam(required = false) String companyId,
                                             @RequestParam(required = false) String stationId,
                                             @RequestParam(required = false) String stationPartitionId) {
        try {
            Map<String, Object> result = sysHomeService.aggregateHomeData(companyId, stationId, stationPartitionId);
            return R.ok(result);
        } catch (Exception e) {
            log.error("查询首页数据失败", e);
            return R.fail("查询首页数据失败: " + e.getMessage());
        }
    }
}
