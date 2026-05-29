package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.excel.utils.ExcelUtil;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.bo.MonitorBo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo;
import org.sdkj.thermal.domain.vo.MonitorExportVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.service.IPrMonitorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运行监控
 * 采集器 radio 调用 /thermal/ht/dtu-archive/list（DTU 复用，不重复实现）
 *
 * @see docs/superpowers/plans/2026-05-28-meter-phase3-monitor-plan.md
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/monitor")
public class PrMonitorController extends BaseController {

    private final IPrMonitorService monitorService;

    // ========== 5 个 TK 列表 ==========

    /** 热表实时列表（户级） */
    @SaCheckPermission("thermal:ht:monitor:list")
    @SaCheckLogin
    @PostMapping("/heat")
    public TableDataInfo<PrHeatArchiveVo> heat(@RequestBody MonitorBo bo, PageQuery pageQuery) {
        return monitorService.heatList(bo, pageQuery);
    }

    /** 阀门实时列表（户级） */
    @SaCheckPermission("thermal:ht:monitor:list")
    @SaCheckLogin
    @PostMapping("/valve")
    public TableDataInfo<PrHeatValveArchiveVo> valve(@RequestBody MonitorBo bo, PageQuery pageQuery) {
        return monitorService.valveList(bo, pageQuery);
    }

    /** 单元热表实时列表 */
    @SaCheckPermission("thermal:ht:monitor:list")
    @SaCheckLogin
    @PostMapping("/unit-heat")
    public TableDataInfo<PrHeatUnitHotArchiveVo> unitHeat(@RequestBody MonitorBo bo, PageQuery pageQuery) {
        return monitorService.unitHeatList(bo, pageQuery);
    }

    /** 单元阀门实时列表 */
    @SaCheckPermission("thermal:ht:monitor:list")
    @SaCheckLogin
    @PostMapping("/unit-valve")
    public TableDataInfo<PrHeatUnitValveArchiveVo> unitValve(@RequestBody MonitorBo bo, PageQuery pageQuery) {
        return monitorService.unitValveList(bo, pageQuery);
    }

    /** 温采器实时列表 */
    @SaCheckPermission("thermal:ht:monitor:list")
    @SaCheckLogin
    @PostMapping("/temp")
    public TableDataInfo<PrHeatTempArchiveVo> temp(@RequestBody MonitorBo bo, PageQuery pageQuery) {
        return monitorService.tempList(bo, pageQuery);
    }

    // ========== 聚合统计 ==========

    /** 聚合统计（平均回水温度等指标） */
    @SaCheckPermission("thermal:ht:monitor:list")
    @SaCheckLogin
    @PostMapping("/aggregate")
    public R<MonitorAggregateVo> aggregate(@RequestBody MonitorBo bo) {
        return R.ok(monitorService.aggregate(bo));
    }

    // ========== 操作端点 ==========

    /**
     * 读阀状态（mock 实现）
     * // TODO: AI Phase 3 IoT 真发，当前返回假数据
     */
    @SaCheckPermission("thermal:ht:monitor:read-status")
    @SaCheckLogin
    @Log(title = "运行监控-读阀状态", businessType = BusinessType.OTHER)
    @PostMapping("/read-valve-status")
    public R<Map<String, Object>> readValveStatus(@RequestParam String meterNum) {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("success", true);
        mockData.put("meterNum", meterNum);
        mockData.put("valveStatus", "开");
        mockData.put("opening", 100);
        return R.ok(mockData);
    }

    /** 户间生成虚拟设备 */
    @SaCheckPermission("thermal:ht:monitor:virtual-device")
    @SaCheckLogin
    @Log(title = "运行监控-生成虚拟设备", businessType = BusinessType.INSERT)
    @PostMapping("/virtual-device")
    public R<Integer> virtualDevice(@RequestParam String orgId) {
        int count = monitorService.generateVirtualDevice(orgId);
        return R.ok("已生成 " + count + " 个虚拟温采器", count);
    }

    /** 导出户间数据 */
    @SaCheckPermission("thermal:ht:monitor:export")
    @SaCheckLogin
    @Log(title = "运行监控-导出户间数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody MonitorBo bo, HttpServletResponse response) {
        List<MonitorExportVo> list = monitorService.exportList(bo);
        ExcelUtil.exportExcel(list, "户间数据", MonitorExportVo.class, response);
    }

    /** 打开网关（采集器按钮） */
    @SaCheckPermission("thermal:ht:monitor:gateway")
    @SaCheckLogin
    @Log(title = "运行监控-打开网关", businessType = BusinessType.OTHER)
    @PostMapping("/gateway/open")
    public R<Void> openGateway(@RequestParam String dtuNum) {
        return R.ok();
    }

    /** 关闭网关（采集器按钮） */
    @SaCheckPermission("thermal:ht:monitor:gateway")
    @SaCheckLogin
    @Log(title = "运行监控-关闭网关", businessType = BusinessType.OTHER)
    @PostMapping("/gateway/close")
    public R<Void> closeGateway(@RequestParam String dtuNum) {
        return R.ok();
    }

    /** 读取信道（采集器按钮） */
    @SaCheckPermission("thermal:ht:monitor:gateway")
    @SaCheckLogin
    @Log(title = "运行监控-读取信道", businessType = BusinessType.OTHER)
    @PostMapping("/read-channel")
    public R<Void> readChannel(@RequestParam String dtuNum) {
        return R.ok();
    }
}
