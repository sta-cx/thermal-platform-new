package org.sdkj.thermal.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

/**
 * 采集平台工具类
 * 封装与外部采集平台的 HTTP 通信（获取 Token、同步设备信息、下发控制指令）
 */
@Slf4j
@UtilityClass
public class CollectPlatformUtil {

    private static final String QUERY_TOKEN_URL = "http://{0}/oauth/token?username={1}&password={2}&grant_type=password&client_id=app&client_secret=app&scope=app";
    private static final String INFO_SYNC_URL = "http://{0}/api/informationSynchronization";
    private static final String CONTROL_MSG_URL = "http://{0}/api/controlData";

    public String queryToken(String ipPort, String username, String password) {
        String url = MessageFormat.format(QUERY_TOKEN_URL, ipPort, username, password);
        try {
            String body = HttpRequest.post(url).timeout(20000).execute().body();
            JSONObject json = JSONUtil.parseObj(body);
            if (json.containsKey("access_token")) {
                return json.getStr("access_token");
            }
        } catch (Exception e) {
            log.error("获取采集平台 Token 失败: {}", e.getMessage());
        }
        return "";
    }

    public boolean informationSynchronization(JSONObject json, String ipPort, String username, String password) {
        String token = queryToken(ipPort, username, password);
        if (token.isEmpty()) {
            log.error("采集平台 Token 获取失败，无法同步");
            return false;
        }
        String url = MessageFormat.format(INFO_SYNC_URL, ipPort);
        try {
            HttpResponse resp = HttpRequest.post(url)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(json.toString())
                .timeout(20000)
                .execute();
            JSONObject result = JSONUtil.parseObj(resp.body());
            return result.containsKey("code") && result.getInt("code") == 200;
        } catch (Exception e) {
            log.error("同步数据到采集平台失败: {}", e.getMessage());
            return false;
        }
    }

    public String sendControlMsg(JSONObject json, String ipPort, String username, String password) {
        String token = queryToken(ipPort, username, password);
        if (token.isEmpty()) {
            return "-2";
        }
        String url = MessageFormat.format(CONTROL_MSG_URL, ipPort);
        try {
            HttpResponse resp = HttpRequest.post(url)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(json.toString())
                .timeout(20000)
                .execute();
            JSONObject result = JSONUtil.parseObj(resp.body());
            if (result.containsKey("code") && result.getInt("code") == 200) {
                return "1";
            }
            return "-1";
        } catch (Exception e) {
            log.error("下发控制指令失败: {}", e.getMessage());
            return "-1";
        }
    }
}
