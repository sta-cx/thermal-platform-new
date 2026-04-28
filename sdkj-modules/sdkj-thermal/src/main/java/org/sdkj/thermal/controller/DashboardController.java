package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.service.IHtAlertService;
import org.sdkj.thermal.service.ISysHomeService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard 仪表盘
 *
 * 数据源说明：
 * - ISysHomeService.aggregateHomeData() 对应老系统 /home/querHomeData，
 *   返回 6 个 Block 的聚合 JSON（Block1-4 基础/阀门/单元/温度，Block6 换热站，Block7 缴费）
 * - IHtAlertService 提供实时告警数据
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final ISysHomeService sysHomeService;
    private final IHtAlertService htAlertService;

    // ==================== 综合分析 ====================

    /** 统计概览 — 基础统计 + 缴费统计 */
    @SaCheckLogin
    @GetMapping("/analytics/overview")
    public R<Map<String, Object>> analyticsOverview(
            @RequestParam(required = false) String companyId) {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> full = sysHomeService.aggregateHomeData(userId, companyId, null, null);
        Map<String, Object> result = new LinkedHashMap<>();
        // Block 1: 基础统计
        result.put("orgNum", full.get("orgNum"));
        result.put("houseNum", full.get("houseNum"));
        result.put("prUserNum", full.get("prUserNum"));
        result.put("buildingNum", full.get("buildingNum"));
        result.put("heatingArea", full.get("heatingArea"));
        result.put("heatingAreaSum", full.get("heatingAreaSum"));
        // Block 7: 缴费统计
        result.put("jfNum", full.get("jfNum"));
        result.put("qfNum", full.get("qfNum"));
        result.put("kzNum", full.get("kzNum"));
        result.put("tgNum", full.get("tgNum"));
        // 设备统计 (Block 2+3 中的数量)
        result.put("valveNum", full.get("valveNum"));
        result.put("hotArchiveNum", full.get("hotArchiveNum"));
        result.put("tempArchiveNum", full.get("tempArchiveNum"));
        result.put("dtuNum", full.get("dtuNum"));
        result.put("valveNumZ", full.get("valveNumZ"));
        result.put("hotArchiveNumZ", full.get("hotArchiveNumZ"));
        result.put("tempArchiveNumZ", full.get("tempArchiveNumZ"));
        result.put("dtuNumZ", full.get("dtuNumZ"));
        return R.ok(result);
    }

    /** 聚合统计 — 返回全部 6 个 Block 数据 */
    @SaCheckLogin
    @GetMapping("/analytics/summary")
    public R<Map<String, Object>> analyticsSummary(
            @RequestParam(required = false) String companyId) {
        Long userId = LoginHelper.getUserId();
        return R.ok(sysHomeService.aggregateHomeData(userId, companyId, null, null));
    }

    // ==================== 热量平衡 ====================

    /** 热量平衡总览 — Block 4 (温度/热量) + Block 6 (换热站) */
    @SaCheckLogin
    @GetMapping("/balance/overview")
    public R<Map<String, Object>> heatBalanceOverview(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String stationId) {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> full = sysHomeService.aggregateHomeData(userId, companyId, stationId, null);
        Map<String, Object> result = new LinkedHashMap<>();
        // Block 4: 温度/热量
        result.put("valveArchiveOut", full.get("valveArchiveOut"));
        result.put("valveArchiveIn", full.get("valveArchiveIn"));
        result.put("valveArchiveActualStatus", full.get("valveArchiveActualStatus"));
        result.put("unitValveArchiveOut", full.get("unitValveArchiveOut"));
        result.put("unitValveArchiveIn", full.get("unitValveArchiveIn"));
        result.put("unitValveArchiveActualStatus", full.get("unitValveArchiveActualStatus"));
        result.put("tempArchiveTemper", full.get("tempArchiveTemper"));
        result.put("hotArchiveTotal", full.get("hotArchiveTotal"));
        result.put("hotArchiveTotalAll", full.get("hotArchiveTotalAll"));
        result.put("unitHotArchiveTotal", full.get("unitHotArchiveTotal"));
        result.put("unitHotArchiveTotalAll", full.get("unitHotArchiveTotalAll"));
        // Block 6: 换热站
        result.put("stationData", full.get("stationData"));
        return R.ok(result);
    }

    /** 按换热站热量平衡 */
    @SaCheckLogin
    @GetMapping("/balance/station/{stationId}")
    public R<Map<String, Object>> stationHeatBalance(
            @PathVariable String stationId,
            @RequestParam(required = false) String companyId) {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> full = sysHomeService.aggregateHomeData(userId, companyId, stationId, null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stationData", full.get("stationData"));
        result.put("valveArchiveOut", full.get("valveArchiveOut"));
        result.put("valveArchiveIn", full.get("valveArchiveIn"));
        result.put("tempArchiveTemper", full.get("tempArchiveTemper"));
        result.put("hotArchiveTotal", full.get("hotArchiveTotal"));
        result.put("hotArchiveTotalAll", full.get("hotArchiveTotalAll"));
        return R.ok(result);
    }

    // ==================== 能耗分析 ====================

    /** 能耗分析 — Block 4 热量数据 */
    @SaCheckLogin
    @GetMapping("/energy/analysis")
    public R<Map<String, Object>> energyAnalysis(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> full = sysHomeService.aggregateHomeData(userId, companyId, null, null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hotArchiveTotal", full.get("hotArchiveTotal"));
        result.put("hotArchiveTotalAll", full.get("hotArchiveTotalAll"));
        result.put("unitHotArchiveTotal", full.get("unitHotArchiveTotal"));
        result.put("unitHotArchiveTotalAll", full.get("unitHotArchiveTotalAll"));
        result.put("valveArchiveIn", full.get("valveArchiveIn"));
        result.put("valveArchiveOut", full.get("valveArchiveOut"));
        result.put("heatingArea", full.get("heatingArea"));
        result.put("heatingAreaSum", full.get("heatingAreaSum"));
        return R.ok(result);
    }

    /** 楼栋能耗 — Block 1 楼栋数据 */
    @SaCheckLogin
    @GetMapping("/energy/buildings")
    public R<Map<String, Object>> buildingEnergyList(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> full = sysHomeService.aggregateHomeData(userId, companyId, null, null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("buildingNum", full.get("buildingNum"));
        result.put("buildingAllNum", full.get("buildingAllNum"));
        result.put("heatingArea", full.get("heatingArea"));
        result.put("heatingAreaSum", full.get("heatingAreaSum"));
        return R.ok(result);
    }

    // ==================== 实时监控 ====================

    /** 实时设备数据 — Block 2 (阀门/户表) + Block 3 (单元表/温采器/DTU) */
    @SaCheckLogin
    @GetMapping("/realtime/devices")
    public R<Map<String, Object>> realtimeDevices(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String stationId) {
        Long userId = LoginHelper.getUserId();
        Map<String, Object> full = sysHomeService.aggregateHomeData(userId, companyId, stationId, null);
        Map<String, Object> result = new LinkedHashMap<>();
        // Block 2: 阀门/户表
        result.put("valveNum", full.get("valveNum"));
        result.put("valveNumZ", full.get("valveNumZ"));
        result.put("valveNumList", full.get("valveNumList"));
        result.put("unitValveNum", full.get("unitValveNum"));
        result.put("unitValveNumZ", full.get("unitValveNumZ"));
        result.put("unitValveNumList", full.get("unitValveNumList"));
        result.put("hotArchiveNum", full.get("hotArchiveNum"));
        result.put("hotArchiveNumZ", full.get("hotArchiveNumZ"));
        result.put("hotArchiveNumList", full.get("hotArchiveNumList"));
        // Block 3: 单元表/温采器/DTU
        result.put("unitHotArchiveNum", full.get("unitHotArchiveNum"));
        result.put("unitHotArchiveNumZ", full.get("unitHotArchiveNumZ"));
        result.put("unitHotArchiveNumList", full.get("unitHotArchiveNumList"));
        result.put("tempArchiveNum", full.get("tempArchiveNum"));
        result.put("tempArchiveNumZ", full.get("tempArchiveNumZ"));
        result.put("tempArchiveNumList", full.get("tempArchiveNumList"));
        result.put("dtuNum", full.get("dtuNum"));
        result.put("dtuNumZ", full.get("dtuNumZ"));
        // Block 4: 实时温度
        result.put("valveArchiveOut", full.get("valveArchiveOut"));
        result.put("valveArchiveIn", full.get("valveArchiveIn"));
        result.put("tempArchiveTemper", full.get("tempArchiveTemper"));
        return R.ok(result);
    }

    /** 实时告警 — 从 HtAlert 表查询最近告警 */
    @SaCheckLogin
    @GetMapping("/realtime/alerts")
    public R<List<Map<String, Object>>> realtimeAlerts(
            @RequestParam(required = false) String companyId) {
        LambdaQueryWrapper<HtAlert> wrapper = new LambdaQueryWrapper<HtAlert>()
            .orderByDesc(HtAlert::getCreateTime)
            .last("LIMIT 50");
        if (companyId != null && !companyId.isEmpty()) {
            wrapper.eq(HtAlert::getCompanyId, companyId);
        }
        List<HtAlert> alerts = htAlertService.list(wrapper);
        List<Map<String, Object>> result = alerts.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("deviceId", a.getMeterId());
            m.put("alertType", a.getAlertType());
            m.put("alertStatus", a.getAlertStatus());
            m.put("inTemp", a.getInTemp());
            m.put("outTemp", a.getOutTemp());
            m.put("roomTemp", a.getRoomTemp());
            m.put("alertTime", a.getCreateTime());
            return m;
        }).collect(Collectors.toList());
        return R.ok(result);
    }
}
