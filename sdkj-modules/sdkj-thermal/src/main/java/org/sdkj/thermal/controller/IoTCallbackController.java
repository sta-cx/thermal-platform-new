package org.sdkj.thermal.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.dto.NbValvePayload;
import org.sdkj.thermal.service.IIoTDataService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * IoT设备数据回调 Controller
 * <p>
 * 接收外部IoT平台推送的设备数据并存储到数据库。
 * 此端点不使用 @SaCheckLogin / @SaCheckPermission，因为它是接收外部IoT平台推送的回调。
 */
@RestController
@RequestMapping("/api/iot")
@Slf4j
@RequiredArgsConstructor
public class IoTCallbackController {

    private final IIoTDataService ioTDataService;

    /**
     * 电信NB阀门回调 — 迁移自旧系统 insertDataNbValve
     * <p>
     * 接收电信平台推送的NB阀门数据，payload.APPdata 为 Base64 编码，
     * 解码后为十六进制字符串，按位解析阀门各项参数。
     */
    @PostMapping("/nb-valve")
    public R<Integer> nbValve(HttpServletResponse response, @RequestBody String msg) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "close");

        try {
            JSONObject jsonObject = JSONUtil.parseObj(msg);
            log.info("NB阀门接收电信平台数据: {}", jsonObject);

            // 推送时间
            String timestamp = jsonObject.getStr("timestamp");

            // IMSI
            String imsi = jsonObject.getStr("IMSI");

            // IMEI
            String imei = jsonObject.getStr("IMEI");

            // productId
            String productId = jsonObject.getStr("productId");

            // deviceId
            String deviceId = jsonObject.getStr("deviceId");

            // 仪表上报的数据 (Base64 编码)
            String payload = jsonObject.getJSONObject("payload").getStr("APPdata");
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = payload.trim().getBytes(StandardCharsets.UTF_8);
            String text = new String(decoder.decode(bytes), StandardCharsets.UTF_8);

            NbValvePayload nbPayload = parseNbValveHex(text);
            if (nbPayload == null) {
                log.warn("NB阀门数据解析失败: text长度不足, text={}", text);
                return R.ok(0);
            }

            // 格式化时间戳
            String formattedDate = formatTimestamp(timestamp);

            ioTDataService.processNbValveData(formattedDate, imei, imsi, productId, deviceId, nbPayload);

        } catch (Exception e) {
            log.error("NB阀门数据处理异常", e);
        }
        return R.ok(200);
    }

    /**
     * 世达Mbus回调 — 迁移自旧系统 insertDataMBusValve
     * <p>
     * 接收世达平台推送的Mbus阀门/热表数据，每条数据有 type 字段:
     * "1"=阀门, "2"=热表
     */
    @PostMapping("/mbus-valve")
    public R<Boolean> mbusValve(HttpServletResponse response, @RequestBody String args) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "close");

        JSONObject jsonObject = JSONUtil.parseObj(args);
        log.info("接收世达数据: {}", jsonObject);

        try {
            if (jsonObject.containsKey("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if (dataArray != null && !dataArray.isEmpty()) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        Object item = dataArray.get(i);
                        JSONObject itemJson;
                        try {
                            if (item instanceof String) {
                                itemJson = JSONUtil.parseObj((String) item);
                            } else if (item instanceof JSONObject) {
                                itemJson = (JSONObject) item;
                            } else {
                                itemJson = JSONUtil.parseObj(item.toString());
                            }
                        } catch (Exception e) {
                            log.warn("Mbus数据解析条目失败: {}", item, e);
                            continue;
                        }

                        String type = itemJson.getStr("type");
                        String meterNum = itemJson.getStr("valveNo");
                        String valveStatus = itemJson.getStr("valveStatus");
                        String valveOpening = itemJson.getStr("valveOpening");
                        BigDecimal supplyTemp = itemJson.getBigDecimal("supplyTemp");
                        BigDecimal returnTemp = itemJson.getBigDecimal("returnTemp");
                        String meterArcCode;

                        if ("2".equals(type)) {
                            // 热表: 温度字段合并, 使用 rbSupplyTemp/rbReturnTemp
                            if (supplyTemp == null) {
                                supplyTemp = itemJson.getBigDecimal("rbSupplyTemp");
                            }
                            if (returnTemp == null) {
                                returnTemp = itemJson.getBigDecimal("rbReturnTemp");
                            }
                            meterArcCode = "04030301";
                        } else if ("1".equals(type)) {
                            // 阀门
                            meterArcCode = "04310401";
                            valveStatus = convertMbusValveStatus(valveStatus);
                            // 开度大于100按100处理
                            if (valveOpening != null) {
                                try {
                                    int opening = Integer.parseInt(valveOpening);
                                    if (opening > 100) {
                                        valveOpening = "100";
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else {
                            meterArcCode = null;
                        }

                        if (StrUtil.isNotBlank(meterArcCode)) {
                            ioTDataService.processMbusValveData(
                                meterNum, valveStatus, valveOpening,
                                supplyTemp, returnTemp, meterArcCode,
                                null, null, null, null, null
                            );
                        }
                    }
                    return R.ok(true);
                }
            }
        } catch (Exception e) {
            log.error("Mbus数据处理异常", e);
            return R.fail("解析错误 failure");
        }
        return R.fail("解析错误 failure");
    }

    /**
     * 移动平台回调 — 迁移自旧系统 signature
     * <p>
     * 支持两种模式:
     * 1. 平台验证模式: 只传 msg 参数，直接返回 msg 字符串
     * 2. 数据接收模式: 通过 RequestBody 传递 args，解析其中的设备数据
     */
    @RequestMapping("/mobile-valve")
    public String mobileValve(String msg, String nonce, String signature,
                              @RequestBody(required = false) String args) {
        try {
            if (StrUtil.isNotBlank(args)) {
                log.info("NB阀门接收移动平台数据: {}", args);
                JSONObject jsonObject = JSONUtil.parseObj(args);

                if (jsonObject.containsKey("msg")) {
                    JSONObject msgObj = jsonObject.getJSONObject("msg");
                    String imei = msgObj.getStr("imei");
                    String deviceId = msgObj.getStr("dev_id");
                    String date = formatTimestamp(msgObj.getStr("at"));

                    if (msgObj.containsKey("value")) {
                        // 仪表上报的数据 — 十六进制字符串(非Base64)
                        String text = msgObj.getStr("value");
                        NbValvePayload payload = parseNbValveHex(text);
                        if (payload != null) {
                            ioTDataService.processMobileValveData(date, imei, deviceId, payload);
                        }
                    }
                }
            }

            if (StrUtil.isNotBlank(msg)) {
                return msg;
            } else {
                return "success";
            }
        } catch (Exception e) {
            log.error("移动平台数据处理异常", e);
            return msg;
        }
    }

    // ========== 解析工具方法 ==========

    /**
     * 解析NB阀门十六进制数据
     * <p>
     * 解析规则(迁移自旧系统):
     * text[4,20)   → meterNum (阀门编号)
     * text[28,30)  → valveStatus (阀门状态)
     * text[30,32)  → settingStatus (设定开度, 十六进制)
     * text[32,34)  → actualOpen (实际开度, 十六进制)
     * text[34,38)  → inTemperature (进水温度, 十六进制/100)
     * text[38,42)  → outTemperature (回水温度, 十六进制/100)
     * text[42,46)  → voltage (电压, 十六进制/100)
     * text[46,48)  → csq (信号强度, 十进制)
     * text[48,60)  → valveTime (年月日时分秒)
     * text[60,62)  → reportInterval (上报间隔, 十六进制)
     * text[62,64)  → intervalUnit (间隔单位, 十六进制)
     * text[64,66)  → validTime (有效时长, 十六进制)
     * text[66,70)  → totalDegree (总上报次数, 十六进制)
     *
     * @param text 十六进制字符串(移动平台直接传入) 或 Base64 解码后的十六进制字符串
     * @return 解析结果, 如果数据长度不足则返回 null
     */
    private NbValvePayload parseNbValveHex(String text) {
        if (text == null || text.length() < 70) {
            return null;
        }
        // 旧系统: if (text.startsWith("0")) text = text.substring(1);
        // 这意味着实际解析偏移基于去掉前导"0"后的字符串
        if (text.startsWith("0")) {
            text = text.substring(1);
        }
        if (text.length() < 70) {
            return null;
        }

        NbValvePayload payload = new NbValvePayload();

        // 阀门编号
        payload.setMeterNum(text.substring(4, 20));

        // 阀门状态
        String valveStatus = text.substring(28, 30);
        if ("00".equals(valveStatus)) {
            valveStatus = "02";
        }
        payload.setValveStatus(valveStatus);

        // 设定开度
        payload.setSettingStatus(Integer.parseInt(text.substring(30, 32), 16));

        // 实际开度
        payload.setActualOpen(Integer.parseInt(text.substring(32, 34), 16));

        // 进水温度
        BigDecimal inTemp = new BigDecimal(Integer.parseInt(text.substring(34, 38), 16));
        payload.setInTemperature(inTemp.divide(new BigDecimal("100"), 2, RoundingMode.CEILING));

        // 回水温度
        BigDecimal outTemp = new BigDecimal(Integer.parseInt(text.substring(38, 42), 16));
        payload.setOutTemperature(outTemp.divide(new BigDecimal("100"), 2, RoundingMode.CEILING));

        // 电压
        BigDecimal voltage = new BigDecimal(Integer.parseInt(text.substring(42, 46), 16));
        payload.setVoltage(voltage.divide(new BigDecimal("100"), 2, RoundingMode.CEILING));

        // 阀门时间: 年月日时分秒
        String valveTime = parseValveTime(text);
        payload.setValveTime(valveTime);

        // 上报间隔
        payload.setReportInterval(Integer.valueOf(text.substring(60, 62), 16));

        // 间隔单位
        payload.setIntervalUnit(Integer.valueOf(text.substring(62, 64), 16));

        // 有效时长
        payload.setValidTime(Integer.valueOf(text.substring(64, 66), 16));

        // 总上报次数
        payload.setTotalDegree(Integer.valueOf(text.substring(66, 70), 16));

        // 信号强度
        payload.setCsq(Integer.valueOf(text.substring(46, 48)));

        return payload;
    }

    /**
     * 解析阀门时间: text[48,60) → yyyy-MM-dd HH:mm:ss
     */
    private String parseValveTime(String text) {
        Calendar calendar = Calendar.getInstance();
        // 年份: 当前世纪前两位 + text[48,50)
        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
        String yearPrefix = currentYear.substring(0, 2);
        calendar.set(Calendar.YEAR, Integer.parseInt(yearPrefix + text.substring(48, 50)));
        // 月份 (0-based)
        calendar.set(Calendar.MONTH, Integer.parseInt(text.substring(50, 52)) - 1);
        // 日期
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text.substring(52, 54)));
        // 时
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text.substring(54, 56)));
        // 分
        calendar.set(Calendar.MINUTE, Integer.parseInt(text.substring(56, 58)));
        // 秒
        calendar.set(Calendar.SECOND, Integer.parseInt(text.substring(58, 60)));

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    /**
     * Mbus阀门状态转换
     * "0"→"02"(关), "1"→"01"(开), "FF"→"FF"(超时), 其他→十六进制转换
     */
    private String convertMbusValveStatus(String valveStatus) {
        if (valveStatus == null) {
            return null;
        }
        switch (valveStatus) {
            case "0":
                return "02";
            case "1":
                return "01";
            case "FF":
                return "FF";
            default:
                try {
                    return Integer.toHexString(Integer.parseInt(valveStatus));
                } catch (NumberFormatException e) {
                    return valveStatus;
                }
        }
    }

    /**
     * 格式化时间戳(秒级或毫秒级)
     */
    private String formatTimestamp(String timestamp) {
        if (StrUtil.isBlank(timestamp)) {
            return null;
        }
        try {
            long ts = Long.parseLong(timestamp);
            // 如果是秒级时间戳(10位), 转换为毫秒
            if (ts < 1_000_000_000_000L) {
                ts = ts * 1000;
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ts));
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
}
