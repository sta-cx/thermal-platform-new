package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/portal/{appid}")
public class WxPortalController {

    /** Phase 6: 从配置读取各 appid 对应的 token */
    private static final String WECHAT_TOKEN = "your_wechat_token_here";

    @SaIgnore
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String appid,
                          @RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("微信消息验证: appid={}, signature={}", appid, signature);

        if (signature == null || timestamp == null || nonce == null) {
            log.warn("微信验证参数不完整");
            return "非法请求：参数不完整";
        }

        if (echostr == null) {
            log.warn("微信验证缺少 echostr 参数");
            return "非法请求：缺少echostr";
        }

        if (checkSignature(signature, timestamp, nonce, WECHAT_TOKEN)) {
            log.info("微信消息验证成功: appid={}", appid);
            return echostr;
        }
        log.warn("微信消息验证签名失败: appid={}", appid);
        return "非法请求：签名验证失败";
    }

    @SaIgnore
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appid,
                       @RequestBody String requestBody,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature,
                       @RequestParam(name = "encrypt_type", required = false) String encryptType,
                       @RequestParam(name = "signature", required = false) String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce) {
        log.info("微信消息: appid={}, encryptType={}", appid, encryptType);

        if (requestBody == null || requestBody.isEmpty()) {
            return "success";
        }

        // Phase 6: 如果 encryptType=aes，使用 WxMaService 解密消息
        // Phase 6: 路由消息类型 — text/image/voice/video/event
        String msgType = extractMessageType(requestBody);
        log.info("微信消息类型: {}", msgType);

        String response = routeMessage(appid, msgType, requestBody);
        return response != null ? response : "success";
    }

    private String routeMessage(String appid, String msgType, String requestBody) {
        if (msgType == null) return null;
        switch (msgType) {
            case "text":
                // Phase 6: 处理文本消息 — 缴费查询/报修/投诉等
                log.info("收到文本消息: appid={}", appid);
                return null; // 返回 null 表示不回复
            case "event":
                // Phase 6: 处理事件 — 关注/取消关注/菜单点击
                String event = extractXmlValue(requestBody, "Event");
                log.info("收到事件: appid={}, event={}", appid, event);
                if ("subscribe".equals(event)) {
                    // Phase 6: 返回关注欢迎语
                }
                return null;
            default:
                log.info("未处理的消息类型: {}", msgType);
                return null;
        }
    }

    private boolean checkSignature(String signature, String timestamp, String nonce, String token) {
        String[] arr = {token, timestamp, nonce};
        Arrays.sort(arr);
        String str = String.join("", arr);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            String calculated = sb.toString();
            return calculated.equals(signature);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1 算法不可用", e);
            return false;
        }
    }

    private String extractMessageType(String xml) {
        return extractXmlValue(xml, "MsgType");
    }

    private String extractXmlValue(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        if (start < 0) {
            String cdataOpen = "<" + tag + "><![CDATA[";
            start = xml.indexOf(cdataOpen);
            if (start < 0) return null;
            start += cdataOpen.length();
            int end = xml.indexOf("]]></" + tag + ">", start);
            return end > start ? xml.substring(start, end) : null;
        }
        start += open.length();
        int end = xml.indexOf(close, start);
        return end > start ? xml.substring(start, end) : null;
    }
}
