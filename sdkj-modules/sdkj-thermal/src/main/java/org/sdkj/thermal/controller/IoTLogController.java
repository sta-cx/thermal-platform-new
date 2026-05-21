package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/iot-log")
public class IoTLogController extends BaseController {

    private final PrHeatValveArchiveMapper valveArchiveMapper;

    private static final long ONLINE_THRESHOLD_MINUTES = 30;

    /**
     * 集中器在线状态监控
     * 按 concentratorCode 分组统计阀门数据（SQL聚合，避免全表加载）
     */
    @SaCheckPermission("thermal:ht:control:query")
    @SaCheckLogin
    @GetMapping("/centrator-monitor")
    public R<List<Map<String, Object>>> centratorMonitor(
            @RequestParam(required = false) String search) {
        return R.ok(valveArchiveMapper.selectCentratorMonitor(search, ONLINE_THRESHOLD_MINUTES));
    }
}
