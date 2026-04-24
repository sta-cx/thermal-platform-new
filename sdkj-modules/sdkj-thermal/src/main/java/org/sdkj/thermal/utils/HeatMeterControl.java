package org.sdkj.thermal.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * MBus阀门控制
 * 迁移自旧系统 HeatMeterControl
 * 注意: 硬件通信需配置 thermal.mbus.url 中间件地址
 */
@Slf4j
@Component
public class HeatMeterControl {

    @Value("${thermal.mbus.url:http://127.0.0.1/middle/property/heatreading/operation}")
    private String mbusUrl;

    /**
     * MBus阀门组装单个控制参数
     */
    public String mbusControl(String meterNum, String command, String valveOpening, String dateTime) {
        return String.format("{\"valveNo\":\"%s\",\"type\":\"1\",\"command\":\"%s\",\"valveOpening\":\"%s\",\"commandTime\":\"%s\"}",
                meterNum, command, valveOpening, dateTime);
    }

    /**
     * 发送数据到中间系统
     * TODO: 需要 HTTP 客户端实现
     */
    public boolean postData(String[] array) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"counts\":").append(array.length).append(",\"data\":[");
            for (int i = 0; i < array.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(array[i]);
            }
            sb.append("]}");
            String payload = sb.toString();
            log.info("MBus 发送: {}", payload);

            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> request =
                new org.springframework.http.HttpEntity<>(payload, headers);

            org.springframework.http.ResponseEntity<String> response =
                restTemplate.postForEntity(mbusUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("MBus 发送成功: {}", response.getBody());
                return true;
            } else {
                log.error("MBus 发送失败, HTTP状态: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("MBus 发送失败", e);
            return false;
        }
    }

    private static final String PREFIX = "68C0";
    private static final String SUFFIX = "16";

    /**
     * 生成阀门控制十六进制指令
     */
    public String generateCommand(int num, String meterNum, String type, int interval, String unit, int effect) {
        String arg = PREFIX + meterNum;
        return switch (type) {
            case "01" -> {
                arg += "010401110001";
                yield HexadecimalConversion.conversion16(arg) + SUFFIX;
            }
            case "02" -> {
                arg += "010402120001";
                yield HexadecimalConversion.conversion16(arg) + SUFFIX;
            }
            case "03" -> {
                arg += "01040313" + String.format("%02X", num) + "01";
                yield HexadecimalConversion.conversion16(arg) + SUFFIX;
            }
            case "04" -> {
                arg += "010404140001";
                yield HexadecimalConversion.conversion16(arg) + SUFFIX;
            }
            case "05" -> {
                arg += "02060222" + String.format("%02X", interval) + unit + String.format("%02X", effect) + "01";
                yield HexadecimalConversion.conversion16(arg) + SUFFIX;
            }
            default -> "";
        };
    }
}
