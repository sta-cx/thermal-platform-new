package org.sdkj.thermal.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.thermal.utils.HeatMeterControl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 手动阀门控制
 * 迁移自旧系统 PrHeatControlController
 * 注意: 硬件通信依赖 MBus 中间件，需配置 thermal.mbus.url
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/control")
public class PrHeatControlController extends BaseController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String CMD_OPEN_VALVE = "2001";
    private static final String CMD_CLOSE_VALVE = "2002";
    private static final String CMD_SET_ANGLE = "2003";
    private static final String CMD_QUERY_STATUS = "2004";
    private static final String CMD_BRAKE = "2005";
    private static final String CMD_SET_CYCLE = "2006";
    private static final String CMD_UNLOCK = "2007";

    private final HeatMeterControl heatMeterControl;
    private final IPrHeatValveArchiveService valveArchiveService;
    private final IHtTasksPerformService tasksPerformService;

    private String now() {
        return LocalDateTime.now().format(DATE_FMT);
    }

    /** 发送 MBus 指令，失败时记录日志但不抛异常。 */
    private boolean sendCommand(String command) {
        return sendCommand(new String[]{command});
    }

    private boolean sendCommand(String[] commands) {
        try {
            return heatMeterControl.postData(commands);
        } catch (Exception e) {
            log.warn("MBus下发失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 手动控制阀门开度
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/manual")
    public R<Void> handControl(@RequestParam String meterNum,
                               @RequestParam String valveOpening) {
        String command = heatMeterControl.mbusControl(meterNum, CMD_SET_ANGLE, valveOpening, now());
        boolean success = sendCommand(command);
        writeControlLog(Collections.singletonList(meterNum), 3, Convert.toInt(valveOpening));
        return success ? R.ok() : R.fail("阀门控制指令发送失败");
    }

    /**
     * 查询仪表状态
     */
    @SaCheckPermission("thermal:ht:control:query")
    @SaCheckLogin
    @GetMapping("/query")
    public R<String> selectMeter(@RequestParam String meterNum) {
        String command = heatMeterControl.mbusControl(meterNum, CMD_QUERY_STATUS, "", now());
        boolean success = sendCommand(command);
        return success ? R.ok("状态查询指令已发送") : R.fail("状态查询指令发送失败");
    }

    /**
     * 开阀（开度100）
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String meterNum) {
        String command = heatMeterControl.mbusControl(meterNum, CMD_OPEN_VALVE, "100", now());
        boolean success = sendCommand(command);
        writeControlLog(Collections.singletonList(meterNum), 1, 100);
        return success ? R.ok() : R.fail("开阀指令发送失败");
    }

    /**
     * 关阀（开度0）
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String meterNum) {
        String command = heatMeterControl.mbusControl(meterNum, CMD_CLOSE_VALVE, "0", now());
        boolean success = sendCommand(command);
        writeControlLog(Collections.singletonList(meterNum), 2, 0);
        return success ? R.ok() : R.fail("关阀指令发送失败");
    }

    /**
     * 增加阀门开度
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/add")
    public R<Void> add(@RequestParam String meterNum,
                       @RequestParam(defaultValue = "5") Integer adjustStep,
                       @RequestParam(defaultValue = "99") Integer maxAdjust,
                       @RequestParam Integer currentAngle) {
        int newAngle = Math.min(currentAngle + adjustStep, maxAdjust);
        String command = heatMeterControl.mbusControl(meterNum, CMD_SET_ANGLE, String.valueOf(newAngle), now());
        boolean success = sendCommand(command);
        writeControlLog(Collections.singletonList(meterNum), 3, newAngle);
        return success ? R.ok("开度调整为: " + newAngle) : R.fail("开度调整失败");
    }

    /**
     * 减少阀门开度
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/sub")
    public R<Void> sub(@RequestParam String meterNum,
                       @RequestParam(defaultValue = "5") Integer adjustStep,
                       @RequestParam(defaultValue = "0") Integer minAdjust,
                       @RequestParam Integer currentAngle) {
        int newAngle = Math.max(currentAngle - adjustStep, minAdjust);
        String command = heatMeterControl.mbusControl(meterNum, CMD_SET_ANGLE, String.valueOf(newAngle), now());
        boolean success = sendCommand(command);
        writeControlLog(Collections.singletonList(meterNum), 3, newAngle);
        return success ? R.ok("开度调整为: " + newAngle) : R.fail("开度调整失败");
    }

    /**
     * 生成十六进制控制指令（用于调试）
     */
    @SaCheckPermission("thermal:ht:control:query")
    @SaCheckLogin
    @GetMapping("/generateCommand")
    public R<String> generateCommand(@RequestParam String meterNum,
                                     @RequestParam String type,
                                     @RequestParam(defaultValue = "0") Integer num,
                                     @RequestParam(defaultValue = "0") Integer interval,
                                     @RequestParam(defaultValue = "0") String unit,
                                     @RequestParam(defaultValue = "1") Integer effect) {
        String cmd = heatMeterControl.generateCommand(num, meterNum, type, interval, unit, effect);
        return R.ok(cmd.toUpperCase());
    }

    @SaCheckLogin
    @GetMapping("/valveList")
    public TableDataInfo<PrHeatValveArchiveVo> valveList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            PageQuery pageQuery) {
        return valveArchiveService.selectPageList(orgId, buildingId, unit, search, null, pageQuery);
    }

    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatValveArchiveVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            PageQuery pageQuery) {
        return valveArchiveService.selectPageList(orgId, null, null, search, null, pageQuery);
    }

    /**
     * 批量开阀
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/batchOpen")
    public R<Void> batchOpen(@RequestBody List<String> meterNums) {
        return executeBatch(meterNums, CMD_OPEN_VALVE, "100");
    }

    /**
     * 批量关阀
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/batchClose")
    public R<Void> batchClose(@RequestBody List<String> meterNums) {
        return executeBatch(meterNums, CMD_CLOSE_VALVE, "0");
    }

    /**
     * 批量开度调整
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/batchSetAngle")
    public R<Void> batchSetAngle(@RequestBody List<String> meterNums,
                                  @RequestParam String angle) {
        return executeBatch(meterNums, CMD_SET_ANGLE, angle);
    }

    /**
     * 批量采集（查询状态）
     */
    @SaCheckPermission("thermal:ht:control:query")
    @SaCheckLogin
    @PostMapping("/batchQuery")
    public R<Void> batchQuery(@RequestBody List<String> meterNums) {
        return executeBatch(meterNums, CMD_QUERY_STATUS, "");
    }

    /**
     * 批量制动
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/batchBrake")
    public R<Void> batchBrake(@RequestBody List<String> meterNums) {
        return executeBatch(meterNums, CMD_BRAKE, "");
    }

    /**
     * 批量周期设定
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/batchSetCycle")
    public R<Void> batchSetCycle(@RequestBody List<String> meterNums,
                                  @RequestParam Integer cycle) {
        return executeBatch(meterNums, CMD_SET_CYCLE, String.valueOf(cycle));
    }

    /**
     * 批量自由控制（解锁）
     */
    @SaCheckPermission("thermal:ht:control:edit")
    @SaCheckLogin
    @PostMapping("/batchUnlock")
    public R<Void> batchUnlock(@RequestBody List<String> meterNums) {
        return executeBatch(meterNums, CMD_UNLOCK, "");
    }

    private R<Void> executeBatch(List<String> meterNums, String cmd, String param) {
        if (meterNums == null || meterNums.isEmpty()) {
            return R.fail("阀门列表不能为空");
        }
        String[] commands = meterNums.stream()
            .map(m -> heatMeterControl.mbusControl(m, cmd, param, now()))
            .toArray(String[]::new);
        boolean success = sendCommand(commands);
        // 操作日志独立于下发成败，始终写入
        mapCmdToLogType(cmd, param).ifPresent(logType ->
            writeControlLog(meterNums, logType.instructionType, logType.instruction));
        return success ? R.ok() : R.fail("批量指令发送失败");
    }

    /** MBus cmd 代码 → 日志 instructionType 映射。 */
    private record CmdLogType(int instructionType, Integer instruction) {}

    private Optional<CmdLogType> mapCmdToLogType(String cmd, String param) {
        return switch (cmd) {
            case "2001" -> Optional.of(new CmdLogType(1, 100));                    // 开阀
            case "2002" -> Optional.of(new CmdLogType(2, 0));                      // 关阀
            case "2003" -> Optional.of(new CmdLogType(3, Convert.toInt(param)));   // 设开度
            case "2005" -> Optional.of(new CmdLogType(5, null));                    // 制动
            case "2006" -> Optional.of(new CmdLogType(6, Convert.toInt(param)));   // 周期设定
            case "2007" -> Optional.of(new CmdLogType(3, null));                    // 解锁
            default     -> Optional.empty();                                        // 2004=查询 → 不写日志
        };
    }

    /**
     * 阀门控制审计日志写入（独立于 MBus 下发成败）。
     * 根据 meterNum 查找阀门配表获取 meterId / orgId，写入 pr_use_card_log。
     */
    private void writeControlLog(List<String> meterNums, int instructionType, Integer instruction) {
        try {
            Long operatorId = LoginHelper.getUserId();
            List<HtTasksPerform> logs = meterNums.stream().map(meterNum -> {
                HtTasksPerform task = new HtTasksPerform();
                task.setMeterNum(meterNum);
                task.setInstructionType(instructionType);
                task.setInstruction(instruction);
                task.setCreateBy(operatorId);
                List<PrHeatValveArchiveVo> archives = valveArchiveService.queryValveByMeterNum(meterNum);
                if (CollUtil.isNotEmpty(archives)) {
                    PrHeatValveArchiveVo archive = archives.get(0);
                    task.setMeterId(archive.getId());
                    task.setOrgId(archive.getOrgId());
                }
                return task;
            }).toList();
            tasksPerformService.insertValveOCLog(logs);
        } catch (Exception e) {
            log.error("阀门控制操作日志写入失败", e);
        }
    }
}
