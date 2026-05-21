package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.thermal.utils.HeatMeterControl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    private String now() {
        return LocalDateTime.now().format(DATE_FMT);
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
        boolean success = heatMeterControl.postData(new String[]{command});
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
        boolean success = heatMeterControl.postData(new String[]{command});
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
        boolean success = heatMeterControl.postData(new String[]{command});
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
        boolean success = heatMeterControl.postData(new String[]{command});
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
        boolean success = heatMeterControl.postData(new String[]{command});
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
        boolean success = heatMeterControl.postData(new String[]{command});
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
            PageQuery pageQuery) {
        return valveArchiveService.selectPageList(orgId, buildingId, null, null, search, pageQuery);
    }

    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatValveArchiveVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            PageQuery pageQuery) {
        return valveArchiveService.selectPageList(orgId, null, null, null, search, pageQuery);
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
        boolean success = heatMeterControl.postData(commands);
        return success ? R.ok() : R.fail("批量指令发送失败");
    }
}
