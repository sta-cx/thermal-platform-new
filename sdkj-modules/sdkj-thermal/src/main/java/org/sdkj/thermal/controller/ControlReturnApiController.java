package org.sdkj.thermal.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 设备控制反馈回调 Controller
 * <p>
 * 接收采集平台推送的设备控制反馈、阀门状态查询、温控器数据、
 * 热表数据、DTU上下线等回调数据。
 * <p>
 * 迁移自旧系统 ControlReturnApi (869行)。
 * 此端点不使用 @SaCheckLogin / @SaCheckPermission，
 * 因为它是接收外部采集平台推送的回调。
 */
@Slf4j
@RestController
@RequestMapping("/api/returnControl")
@RequiredArgsConstructor
public class ControlReturnApiController {

    private final HtTasksPerformMapper htTasksPerformMapper;
    private final IPrOptionsHeatService prOptionsHeatService;

    // ==================== 控制反馈 (开/关/开度调整/制动) ====================

    /**
     * 控制反馈接口
     * <p>
     * 接收采集平台对控制指令(开阀、关阀、开度调整、制动)的执行结果反馈。
     * 请求体为 JSON，顶层包含 code(200=成功) 和 data 数组。
     * 每条 data 包含 type(设备类型)、meterNum、guid、command、valveStatus、message 等字段。
     * <p>
     * type 枚举: 1=阀门, 2=热表, 3=温控器, 4=无线跳转阀, 5=采集器
     */
    @RequestMapping("/returnControl")
    public R<Boolean> returnControl(@RequestBody String msg) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            log.info("接收到采集平台控制反馈: {}", jsonObject);

            if (!jsonObject.containsKey("code")) {
                log.warn("控制反馈缺少 code 字段");
                return R.fail("缺少code字段");
            }

            int code = jsonObject.getInt("code");
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray == null || jsonArray.isEmpty()) {
                log.warn("控制反馈 data 数组为空");
                return R.ok(true);
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                int type = json.getInt("type", 0);
                String meterNum = json.getStr("meterNum");
                String guid = json.getStr("guid");
                String meterInfo = json.getStr("meterInfo");
                int command = json.getInt("command", 0);
                int valveStatus = json.getInt("valveStatus", 0);
                String message = json.getStr("message");
                String dtuNum = json.getStr("dtuNum");
                int channelNum = json.getInt("channelNum", 0);

                switch (type) {
                    case 1: // 阀门
                        handleValveControl(code, guid, meterNum, command, valveStatus, message, channelNum, meterInfo);
                        break;
                    case 2: // 热表
                        handleHeatMeterControl(code, guid, meterNum, command, valveStatus, message);
                        break;
                    case 3: // 温控器
                        handleThermostatControl(code, guid, meterNum, command, valveStatus, message);
                        break;
                    case 4: // 无线跳转阀
                        handleRadioBypassValveControl(code, guid, meterNum, command, valveStatus, message);
                        break;
                    case 5: // 采集器
                        handleCollectorControl(code, guid, meterNum, dtuNum, command, valveStatus, channelNum, message);
                        break;
                    default:
                        log.warn("未知的设备类型: type={}", type);
                        break;
                }
            }
            return R.ok(true);
        } catch (Exception e) {
            log.error("控制反馈处理异常", e);
            return R.fail("处理异常: " + e.getMessage());
        }
    }

    // ==================== 阀门状态查询反馈 ====================

    /**
     * 阀门状态查询结果反馈
     * <p>
     * 接收采集平台返回的阀门状态查询结果，包括阀门开度、温度、电压、信号等。
     * 根据 dataFeild 字段区分不同协议(31D1/1F90/2190)进行差异化处理。
     * 同时进行异常检测(缴费未开阀、未缴费开阀、温度异常)并插入告警。
     */
    @RequestMapping("/valveData")
    public R<Boolean> valveData(@RequestBody String msg) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            log.info("接收到采集平台阀门状态查询结果反馈: {}", jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray == null || jsonArray.isEmpty()) {
                return R.ok(true);
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                JSONObject json;
                try {
                    if (item instanceof String) {
                        json = JSONUtil.parseObj((String) item);
                    } else {
                        json = (JSONObject) item;
                    }
                } catch (Exception e) {
                    log.warn("valveData 条目解析失败: {}", item, e);
                    continue;
                }

                processValveDataItem(json);
            }
            return R.ok(true);
        } catch (Exception e) {
            log.error("阀门状态查询反馈处理异常", e);
            return R.fail("处理异常: " + e.getMessage());
        }
    }

    // ==================== 温控器数据反馈 ====================

    /**
     * 温控器数据反馈
     * <p>
     * 接收采集平台返回的温控器(室内温度采集器)数据，
     * 包含温度、湿度、电压、上报间隔、采集间隔等信息。
     */
    @RequestMapping("/tempData")
    public R<Boolean> tempData(@RequestBody String msg) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            log.info("接收到采集平台温控器结果反馈: {}", jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray == null || jsonArray.isEmpty()) {
                return R.ok(true);
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                JSONObject json;
                try {
                    if (item instanceof String) {
                        json = JSONUtil.parseObj((String) item);
                    } else {
                        json = (JSONObject) item;
                    }
                } catch (Exception e) {
                    log.warn("tempData 条目解析失败: {}", item, e);
                    continue;
                }

                processTempDataItem(json);
            }
            return R.ok(true);
        } catch (Exception e) {
            log.error("温控器数据反馈处理异常", e);
            return R.fail("处理异常: " + e.getMessage());
        }
    }

    // ==================== 热表数据反馈 ====================

    /**
     * 热表数据反馈
     * <p>
     * 接收采集平台返回的热力表数据，包含温度、流量、热量、功率等信息。
     */
    @RequestMapping("/hotData")
    public R<Boolean> hotData(@RequestBody String msg) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            log.info("接收到采集平台热表结果反馈: {}", jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray == null || jsonArray.isEmpty()) {
                return R.ok(true);
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                JSONObject json;
                try {
                    if (item instanceof String) {
                        json = JSONUtil.parseObj((String) item);
                    } else {
                        json = (JSONObject) item;
                    }
                } catch (Exception e) {
                    log.warn("hotData 条目解析失败: {}", item, e);
                    continue;
                }

                processHotDataItem(json);
            }
            return R.ok(true);
        } catch (Exception e) {
            log.error("热表数据反馈处理异常", e);
            return R.fail("处理异常: " + e.getMessage());
        }
    }

    // ==================== DTU上下线反馈 ====================

    /**
     * DTU上下线状态反馈
     * <p>
     * 接收采集平台推送的DTU设备上下线状态变更通知。
     * 更新所有关联档案(热表、阀门)的DTU在线状态。
     */
    @RequestMapping("/dtuData")
    public R<Boolean> dtuData(@RequestBody String msg) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            log.info("接收到采集平台DTU上下线结果反馈: {}", jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray == null || jsonArray.isEmpty()) {
                return R.ok(true);
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                JSONObject json;
                try {
                    if (item instanceof String) {
                        json = JSONUtil.parseObj((String) item);
                    } else {
                        json = (JSONObject) item;
                    }
                } catch (Exception e) {
                    log.warn("dtuData 条目解析失败: {}", item, e);
                    continue;
                }

                String dtuNum = json.getStr("dtuNum");
                Integer status = json.containsKey("status") ? json.getInt("status") : null;
                String collectDate = json.getStr("dtuTime");

                // 更新户热表配表
                htTasksPerformMapper.updateHotArchiveDtu(dtuNum, status, collectDate);
                // 更新单元热表配表
                htTasksPerformMapper.updateUnitHotArchiveDtu(dtuNum, status, collectDate);
                // 更新户间配表
                htTasksPerformMapper.updateValveArchiveDtu(dtuNum, status, collectDate);
                // 更新单元配表
                htTasksPerformMapper.updateUnitiValveArchiveDtu(dtuNum, status, collectDate);
                // 更新DTU档案
                htTasksPerformMapper.updateDtuArchive(dtuNum, status, collectDate);
            }
            return R.ok(true);
        } catch (Exception e) {
            log.error("DTU上下线反馈处理异常", e);
            return R.fail("处理异常: " + e.getMessage());
        }
    }

    // ==================== returnControl 内部分发方法 ====================

    /**
     * 处理阀门(type=1)控制反馈
     */
    private void handleValveControl(int code, String guid, String meterNum, int command,
                                    int valveStatus, String message, int channelNum, String meterInfo) {
        if (code == 200) {
            // 成功
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateHeatValveScopeStatus(meterNum, meterInfo, valveStatus);
            }
            if (command == 27 || command == 28 || command == 18) {
                htTasksPerformMapper.updataHeatValve(meterNum, channelNum, meterInfo, valveStatus);
                htTasksPerformMapper.updataUnitValve(meterNum, channelNum, meterInfo, valveStatus);
            }
        } else {
            // 失败 - 根据消息内容修正状态码
            valveStatus = adjustValveStatusByMessage(valveStatus, message);
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            // 更新户间阀门
            htTasksPerformMapper.updataHeatValveS(meterNum, valveStatus);
            // 更新单元阀门
            htTasksPerformMapper.updataUnitValveS(meterNum, valveStatus);
            // 更新控制范围表
            htTasksPerformMapper.updateHtScope(guid, meterNum, valveStatus);
            // 插入异常信息
            if (valveStatus == 2 || valveStatus == 3) {
                htTasksPerformMapper.inserHtAlert(meterNum, valveStatus);
            }
        }
    }

    /**
     * 处理热表(type=2)控制反馈
     */
    private void handleHeatMeterControl(int code, String guid, String meterNum,
                                         int command, int valveStatus, String message) {
        if (code == 200) {
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            log.info("热表控制成功: meterNum={}, message={}", meterNum, message);
        } else {
            if (valveStatus == 2) {
                if ("DTU掉线".equals(message)) {
                    valveStatus = 15;
                }
                if ("采集平台无此阀门信息".equals(message)) {
                    valveStatus = 14;
                }
            }
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            log.info("热表控制失败: meterNum={}, message={}", meterNum, message);
        }
    }

    /**
     * 处理温控器(type=3)控制反馈
     */
    private void handleThermostatControl(int code, String guid, String meterNum,
                                          int command, int valveStatus, String message) {
        if (code == 200) {
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            htTasksPerformMapper.updataTemp(meterNum, valveStatus);
            log.info("温控器控制成功: meterNum={}, message={}", meterNum, message);
        } else {
            htTasksPerformMapper.updataTempS(meterNum, valveStatus);
            log.info("温控器控制失败: meterNum={}, message={}", meterNum, message);
        }
    }

    /**
     * 处理无线跳转阀(type=4)控制反馈
     */
    private void handleRadioBypassValveControl(int code, String guid, String meterNum,
                                                int command, int valveStatus, String message) {
        if (code == 200) {
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            htTasksPerformMapper.updateHtScope(guid, meterNum, valveStatus);
            htTasksPerformMapper.updataHeatValveS(meterNum, valveStatus);
            htTasksPerformMapper.updataUnitValveS(meterNum, valveStatus);
            log.info("无线跳转阀控制成功: meterNum={}, message={}", meterNum, message);
        } else {
            valveStatus = adjustValveStatusByMessage(valveStatus, message);
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            htTasksPerformMapper.updataHeatValveS(meterNum, valveStatus);
            htTasksPerformMapper.updataUnitValveS(meterNum, valveStatus);
            htTasksPerformMapper.updateHtScope(guid, meterNum, valveStatus);
            if (valveStatus == 2 || valveStatus == 3) {
                htTasksPerformMapper.inserHtAlert(meterNum, valveStatus);
            }
            log.info("无线跳转阀控制失败: meterNum={}, message={}", meterNum, message);
        }
    }

    /**
     * 处理采集器(type=5)控制反馈
     */
    private void handleCollectorControl(int code, String guid, String meterNum, String dtuNum,
                                         int command, int valveStatus, int channelNum, String message) {
        if (code == 200) {
            if (StrUtil.isNotBlank(guid)) {
                htTasksPerformMapper.updateByreturnControl(guid, meterNum, command, valveStatus);
                htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, command, valveStatus);
            }
            if (command == 27 || command == 28) {
                htTasksPerformMapper.updataHeatDtu(dtuNum, channelNum, valveStatus);
            }
            log.info("采集器控制成功: meterNum={}, message={}", meterNum, message);
        } else {
            htTasksPerformMapper.updataHeatDtuS(dtuNum, valveStatus);
            log.info("采集器控制失败: meterNum={}, message={}", meterNum, message);
        }
    }

    // ==================== valveData 内部处理方法 ====================

    /**
     * 处理单条阀门状态查询数据
     */
    private void processValveDataItem(JSONObject json) {
        String guid = json.getStr("guid");
        String meterInfo = json.getStr("meterInfo");
        String meterNum = json.getStr("meterNum");
        String valveStatus = json.containsKey("valveStatus") ? json.getStr("valveStatus") : null;
        String settingStatus = json.containsKey("settingStatus") ? json.getStr("settingStatus") : null;
        String actualStatus = json.containsKey("actualStatus") ? json.getStr("actualStatus") : null;

        BigDecimal inTemp = json.containsKey("inTemp") ? json.getBigDecimal("inTemp") : BigDecimal.ZERO;
        BigDecimal outTemp = json.containsKey("outTemp") ? json.getBigDecimal("outTemp") : BigDecimal.ZERO;
        BigDecimal voltage = json.containsKey("voltage") ? json.getBigDecimal("voltage") : BigDecimal.ZERO;
        String valveTime = json.containsKey("valveTime") ? json.getStr("valveTime") : null;
        Integer reportInterval = json.containsKey("reportInterval") ? json.getInt("reportInterval") : null;
        Integer reportingUnit = json.containsKey("reportingUnit") ? json.getInt("reportingUnit") : null;
        Integer validTime = json.containsKey("validTime") ? json.getInt("validTime") : null;
        Integer totalDegree = json.containsKey("totalDegree") ? json.getInt("totalDegree") : null;
        Integer csq = json.containsKey("csq") ? json.getInt("csq") : null;
        Integer rouseNum = json.containsKey("rouseNum") ? json.getInt("rouseNum") : null;
        Integer duration = json.containsKey("duration") ? json.getInt("duration") : null;
        Integer dtuStatus = json.containsKey("dtuStatus") ? json.getInt("dtuStatus") : null;
        String valveModel = json.containsKey("valveModel") ? json.getStr("valveModel") : null;
        BigDecimal userSetTemp = json.containsKey("userSetTemp") ? json.getBigDecimal("userSetTemp") : BigDecimal.ZERO;
        BigDecimal roomTemp = json.containsKey("roomTemp") ? json.getBigDecimal("roomTemp") : BigDecimal.ZERO;
        BigDecimal avgTemp = json.containsKey("avgTemp") ? json.getBigDecimal("avgTemp") : BigDecimal.ZERO;
        Integer workTime = json.containsKey("workTime") ? json.getInt("workTime") : 0;
        Integer totalOpenTime = json.containsKey("totalOpenTime") ? json.getInt("totalOpenTime") : 0;
        Integer coldFlg = json.containsKey("coldFlg") ? json.getInt("coldFlg") : 0;
        Integer wkqLock = json.containsKey("wkqLock") ? json.getInt("wkqLock") : 0;
        Integer tempLow = json.containsKey("tempLow") ? json.getInt("tempLow") : 0;
        Integer tempHigh = json.containsKey("tempHigh") ? json.getInt("tempHigh") : 0;
        BigDecimal insFlow = json.containsKey("insFlow") ? json.getBigDecimal("insFlow") : BigDecimal.ZERO;

        // 信号强度兼容: signalStrength 字段映射到 csq
        if (json.containsKey("signalStrength")) {
            csq = json.getInt("signalStrength");
        }

        // 协议类型判断
        String dataFeild = json.containsKey("dataFeild") ? json.getStr("dataFeild") : "A5A5";
        if ("31D1".equals(dataFeild) || "2190".equals(dataFeild)) {
            reportingUnit = 1;
        }

        // 插入抄表记录
        if ("31D1".equals(dataFeild) || "1F90".equals(dataFeild) || "2190".equals(dataFeild)) {
            htTasksPerformMapper.insertYTReading(meterNum, valveStatus, settingStatus, actualStatus, inTemp, outTemp,
                voltage, valveTime, reportInterval, reportingUnit, csq, valveModel, userSetTemp, roomTemp, avgTemp,
                workTime, totalOpenTime, coldFlg, wkqLock, tempLow, tempHigh, insFlow);
        } else {
            htTasksPerformMapper.insertReading(meterNum, valveStatus, settingStatus, actualStatus, inTemp, outTemp,
                voltage, valveTime, reportInterval, reportingUnit, validTime, totalDegree, csq, rouseNum, duration, insFlow);
        }

        // 更新户间配表
        if ("31D1".equals(dataFeild)) {
            htTasksPerformMapper.updateYTValveArchive31D1(meterNum, valveTime, reportInterval, reportingUnit,
                coldFlg, wkqLock, tempLow, tempHigh, meterInfo);
        } else if ("1F90".equals(dataFeild)) {
            htTasksPerformMapper.updateYTValveArchive1F90(meterNum, valveTime, userSetTemp, roomTemp, avgTemp,
                workTime, totalOpenTime, meterInfo);
        } else if ("2190".equals(dataFeild)) {
            htTasksPerformMapper.updateYTValveArchive2190(meterNum, valveStatus, settingStatus, actualStatus, inTemp,
                outTemp, voltage, valveTime, csq, reportInterval, reportingUnit, valveModel, userSetTemp, roomTemp,
                avgTemp, workTime, totalOpenTime, coldFlg, wkqLock, tempLow, tempHigh, meterInfo);
        } else {
            htTasksPerformMapper.updateValveArchive(meterNum, valveStatus, settingStatus, actualStatus, inTemp,
                outTemp, voltage, valveTime, csq, reportInterval, reportingUnit, validTime, totalDegree, dtuStatus,
                meterInfo, insFlow);
        }

        // 更新单元配表
        htTasksPerformMapper.updateUnitiValveArchive(meterNum, valveStatus, settingStatus, actualStatus, inTemp,
            outTemp, voltage, valveTime, csq, reportInterval, reportingUnit, validTime, totalDegree, dtuStatus, meterInfo);

        // 更新户间开关阀配表
        htTasksPerformMapper.updateCommandValveArchive(meterNum, valveStatus, settingStatus, actualStatus, inTemp,
            outTemp, voltage, valveTime, csq, reportInterval, reportingUnit, validTime, totalDegree, dtuStatus, meterInfo);

        // 更新单元开关阀配表
        htTasksPerformMapper.updateCommandUnitiValveArchive(meterNum, valveStatus, settingStatus, actualStatus, inTemp,
            outTemp, voltage, valveTime, csq, reportInterval, reportingUnit, validTime, totalDegree, dtuStatus, meterInfo);

        // 更新房屋信息
        htTasksPerformMapper.updateValveHouse(meterNum, actualStatus, inTemp, outTemp, meterInfo);

        // 更新单元信息
        htTasksPerformMapper.updateValveUnit(meterNum, actualStatus, inTemp, outTemp, meterInfo);

        // 更新DTU信息
        htTasksPerformMapper.updateDtu(meterNum, dtuStatus, meterInfo);
        htTasksPerformMapper.updateDtuUnit(meterNum, dtuStatus, meterInfo);

        // 更新查询指令状态为已完成(9)
        if (StrUtil.isNotBlank(guid)) {
            htTasksPerformMapper.updateByreturnControl(guid, meterNum, 4, 9);
            htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, 4, 9);
        }

        // 异常检测
        detectAndInsertAlerts(meterNum, valveStatus, outTemp, meterInfo);
    }

    /**
     * 异常检测: 缴费未开阀、未缴费开阀、温度异常
     */
    private void detectAndInsertAlerts(String meterNum, String valveStatus, BigDecimal outTemp, String meterInfo) {
        try {
            PrHeatValveArchive archive = htTasksPerformMapper.querHeatValveArchive(meterNum);
            if (archive == null || archive.getPrHouse() == null) {
                return;
            }
            // 只对非特殊户进行异常检测
            if (archive.getPrHouse().getIsSpecial() != null && archive.getPrHouse().getIsSpecial() != 0) {
                return;
            }

            PrOptionsHeat optionsHeat = prOptionsHeatService.getDataById(
                archive.getOrgId(), archive.getCompanyId(), "2");
            if (optionsHeat == null || optionsHeat.getIsAbnormalEnable() == null
                || optionsHeat.getIsAbnormalEnable() != 1) {
                return;
            }

            String payStatus = archive.getPrHouse().getPayStatus();

            // 缴费未开阀
            if ("1".equals(payStatus) && !"1".equals(valveStatus)) {
                htTasksPerformMapper.inserHtAlert(meterNum, 11);
            }
            // 未缴费开阀
            if (!"1".equals(payStatus) && "1".equals(valveStatus)) {
                htTasksPerformMapper.inserHtAlert(meterNum, 12);
            }
            // 未缴费回水温度异常
            if (!"1".equals(payStatus) && optionsHeat.getWjfhswd() != null
                && outTemp != null && outTemp.compareTo(optionsHeat.getWjfhswd()) > 0) {
                htTasksPerformMapper.inserHtAlert(meterNum, 7);
            }
            // 回水温度超出正常范围
            if (outTemp != null && optionsHeat.getWdbjd() != null && optionsHeat.getWdbjx() != null) {
                if (outTemp.compareTo(optionsHeat.getWdbjd()) > 0 || outTemp.compareTo(optionsHeat.getWdbjx()) < 0) {
                    htTasksPerformMapper.inserHtAlert(meterNum, 5);
                }
            }
        } catch (Exception e) {
            log.error("阀门异常检测处理异常: meterNum={}", meterNum, e);
        }
    }

    // ==================== tempData 内部处理方法 ====================

    /**
     * 处理单条温控器数据
     */
    private void processTempDataItem(JSONObject json) {
        String guid = json.getStr("guid");
        String meterInfo = json.getStr("meterInfo");
        String meterNum = json.getStr("meterNum");

        BigDecimal temperature = json.containsKey("temperature") ? json.getBigDecimal("temperature") : null;
        Integer humi = json.containsKey("humidity") ? json.getInt("humidity") : null;
        BigDecimal voltage = json.containsKey("voltage") ? json.getBigDecimal("voltage") : null;
        String readTime = json.getStr("valveTime");
        Integer reportingInterval = json.containsKey("reportInterval") ? json.getInt("reportInterval") : null;
        Integer reportingUnit = json.containsKey("reportUnit") ? json.getInt("reportUnit") : null;
        Integer validTime = json.containsKey("reportTime") ? json.getInt("reportTime") : null;
        Integer csq = json.containsKey("csq") ? json.getInt("csq") : null;
        Integer totalDegree = json.containsKey("reportTotalNum") ? json.getInt("reportTotalNum") : null;
        Integer reportSuccNum = json.containsKey("reportSuccNum") ? json.getInt("reportSuccNum") : null;
        Integer collectInterval = json.containsKey("collectInterval") ? json.getInt("collectInterval") : null;
        Integer collectUnit = json.containsKey("collectUnit") ? json.getInt("collectUnit") : null;
        Integer collectTime = json.containsKey("collectTime") ? json.getInt("collectTime") : null;
        String collectDate = json.getStr("collectDate");
        String movPlace = json.containsKey("movPlace") ? json.getStr("movPlace") : null;
        String valveStatus = json.containsKey("valveStatus") ? json.getStr("valveStatus") : null;

        // 插入抄表
        htTasksPerformMapper.insertTempReading(meterNum, temperature, humi, voltage, readTime, reportingInterval,
            reportingUnit, validTime, csq, totalDegree, reportSuccNum, collectInterval, collectUnit, collectTime,
            collectDate, movPlace, valveStatus);

        // 更新配表
        htTasksPerformMapper.updateTempArchive(meterNum, temperature, humi, voltage, readTime, reportingInterval,
            reportingUnit, validTime, csq, totalDegree, reportSuccNum, collectInterval, collectUnit, collectTime,
            collectDate, movPlace, meterInfo, valveStatus);

        // 更新房屋信息
        htTasksPerformMapper.updateTempHouse(meterNum, temperature);

        // 更新查询指令状态
        if (StrUtil.isNotBlank(guid)) {
            htTasksPerformMapper.updateByreturnControl(guid, meterNum, 4, 9);
            htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, 4, 9);
        }
    }

    // ==================== hotData 内部处理方法 ====================

    /**
     * 处理单条热表数据
     */
    private void processHotDataItem(JSONObject json) {
        String guid = json.getStr("guid");
        String meterInfo = json.getStr("meterInfo");
        String meterNum = json.getStr("meterNum");

        BigDecimal outTemp = json.containsKey("outTemp") ? json.getBigDecimal("outTemp") : null;
        BigDecimal inTemp = json.containsKey("inTemp") ? json.getBigDecimal("inTemp") : null;
        BigDecimal voltage = json.containsKey("voltage") ? json.getBigDecimal("voltage") : null;
        String readTime = json.getStr("deviceTime");
        Integer voltageStatus = json.containsKey("voltageStatus") ? json.getInt("voltageStatus") : null;
        Integer valveStatus = json.containsKey("valveStatus") ? json.getInt("valveStatus") : null;
        BigDecimal totalFlow = json.containsKey("totalFlow") ? json.getBigDecimal("totalFlow") : null;
        Integer csq = json.containsKey("csq") ? json.getInt("csq") : null;
        BigDecimal totalHeat = json.containsKey("totalHeat") ? json.getBigDecimal("totalHeat") : null;
        BigDecimal flowRate = json.containsKey("flowRate") ? json.getBigDecimal("flowRate") : null;
        BigDecimal currentFlow = json.containsKey("currentFlow") ? json.getBigDecimal("currentFlow") : null;
        BigDecimal thermalPower = json.containsKey("thermalPower") ? json.getBigDecimal("thermalPower") : null;
        BigDecimal currentPower = json.containsKey("currentPower") ? json.getBigDecimal("currentPower") : null;
        Integer dtuStatus = json.containsKey("dtuStatus") ? json.getInt("dtuStatus") : null;
        String status1 = json.containsKey("st") ? json.getStr("st") : null;

        // 插入抄表
        htTasksPerformMapper.insertHotReading(meterNum, outTemp, inTemp, voltage, readTime, voltageStatus,
            valveStatus, totalFlow, csq, totalHeat, flowRate, currentFlow, thermalPower, currentPower, status1);

        // 更新配表
        htTasksPerformMapper.updateHotArchive(meterNum, outTemp, inTemp, voltage, readTime, voltageStatus,
            valveStatus, totalFlow, csq, totalHeat, flowRate, currentFlow, thermalPower, currentPower,
            dtuStatus, status1, meterInfo);

        // 更新单元配表
        htTasksPerformMapper.updateUnitHotArchive(meterNum, outTemp, inTemp, voltage, readTime, voltageStatus,
            valveStatus, totalFlow, csq, totalHeat, flowRate, currentFlow, thermalPower, currentPower,
            dtuStatus, status1, meterInfo);

        // 更新房屋信息
        htTasksPerformMapper.updateHotHouse(meterNum, flowRate);

        // 更新DTU信息
        htTasksPerformMapper.updateDtuHot(meterNum, dtuStatus);
        htTasksPerformMapper.updateDtuHotUnit(meterNum, dtuStatus);

        // 更新查询指令状态
        if (StrUtil.isNotBlank(guid)) {
            htTasksPerformMapper.updateByreturnControl(guid, meterNum, 4, 9);
            htTasksPerformMapper.updateByreturnControlByRadio(guid, meterNum, 4, 9);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 根据错误消息内容修正阀门状态码
     * <p>
     * - valveStatus==2 时: DTU掉线->15, 无此阀门->14, error->3
     * - valveStatus==3 时: timeout->2
     */
    private int adjustValveStatusByMessage(int valveStatus, String message) {
        if (valveStatus == 2) {
            if ("DTU掉线".equals(message)) {
                return 15;
            }
            if ("采集平台无此阀门信息".equals(message)) {
                return 14;
            }
            if ("error".equals(message)) {
                return 3;
            }
        }
        if (valveStatus == 3) {
            if ("timeout".equals(message)) {
                return 2;
            }
        }
        return valveStatus;
    }
}
