package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页统计数据
 * 迁移自旧系统 SysHomeController
 * Phase 2: 返回模拟数据骨架，Phase 3-5 业务模块迁移后补充真实查询
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/home")
public class ThermalHomeController {

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
        // TODO: Phase 3-5 迁移业务模块后，替换为真实查询
        // 旧系统通过 6 个异步 Future 查询:
        //   querHomeData1: 设备统计（热力表、电表、水表、燃气表数量）
        //   querHomeData2: 收费统计（应收、实收、欠费）
        //   querHomeData3: 告警统计（各类告警数量）
        //   querHomeData4: 策略统计（控制策略数量、执行状态）
        //   querHomeData6: 任务统计（定时任务、执行记录）
        //   querHomeData7: 用户统计（业主数量、绑定率）
        result.put("deviceCount", 0);
        result.put("chargeTotal", 0);
        result.put("alertCount", 0);
        result.put("strategyCount", 0);
        result.put("taskCount", 0);
        result.put("userCount", 0);
        result.put("userId", userId);

        return R.ok(result);
    }
}
