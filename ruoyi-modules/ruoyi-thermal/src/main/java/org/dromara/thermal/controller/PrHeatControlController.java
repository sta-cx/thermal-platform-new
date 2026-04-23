package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.utils.HeatMeterControl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final HeatMeterControl heatMeterControl;

    /**
     * 手动控制阀门开度
     */
    @SaCheckLogin
    @PostMapping("/manual")
    public R<Void> handControl(@RequestParam String meterNum,
                               @RequestParam String valveOpening) {
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = heatMeterControl.mbusControl(meterNum, "2003", valveOpening, dateTime);
        boolean success = heatMeterControl.postData(new String[]{command});
        return success ? R.ok() : R.fail("阀门控制指令发送失败");
    }

    /**
     * 查询仪表状态
     */
    @SaCheckLogin
    @GetMapping("/query")
    public R<String> selectMeter(@RequestParam String meterNum) {
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = heatMeterControl.mbusControl(meterNum, "2004", "", dateTime);
        boolean success = heatMeterControl.postData(new String[]{command});
        return success ? R.ok("状态查询指令已发送") : R.fail("状态查询指令发送失败");
    }

    /**
     * 开阀（开度100）
     */
    @SaCheckLogin
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String meterNum) {
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = heatMeterControl.mbusControl(meterNum, "2001", "100", dateTime);
        boolean success = heatMeterControl.postData(new String[]{command});
        return success ? R.ok() : R.fail("开阀指令发送失败");
    }

    /**
     * 关阀（开度0）
     */
    @SaCheckLogin
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String meterNum) {
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = heatMeterControl.mbusControl(meterNum, "2002", "0", dateTime);
        boolean success = heatMeterControl.postData(new String[]{command});
        return success ? R.ok() : R.fail("关阀指令发送失败");
    }

    /**
     * 增加阀门开度
     */
    @SaCheckLogin
    @PostMapping("/add")
    public R<Void> add(@RequestParam String meterNum,
                       @RequestParam(defaultValue = "5") Integer adjustStep,
                       @RequestParam(defaultValue = "99") Integer maxAdjust,
                       @RequestParam Integer currentAngle) {
        int newAngle = Math.min(currentAngle + adjustStep, maxAdjust);
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = heatMeterControl.mbusControl(meterNum, "2003", String.valueOf(newAngle), dateTime);
        boolean success = heatMeterControl.postData(new String[]{command});
        return success ? R.ok("开度调整为: " + newAngle) : R.fail("开度调整失败");
    }

    /**
     * 减少阀门开度
     */
    @SaCheckLogin
    @PostMapping("/sub")
    public R<Void> sub(@RequestParam String meterNum,
                       @RequestParam(defaultValue = "5") Integer adjustStep,
                       @RequestParam(defaultValue = "0") Integer minAdjust,
                       @RequestParam Integer currentAngle) {
        int newAngle = Math.max(currentAngle - adjustStep, minAdjust);
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = heatMeterControl.mbusControl(meterNum, "2003", String.valueOf(newAngle), dateTime);
        boolean success = heatMeterControl.postData(new String[]{command});
        return success ? R.ok("开度调整为: " + newAngle) : R.fail("开度调整失败");
    }

    /**
     * 生成十六进制控制指令（用于调试）
     */
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
}
