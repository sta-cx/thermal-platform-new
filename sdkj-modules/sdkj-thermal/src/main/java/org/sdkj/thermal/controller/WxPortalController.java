package org.sdkj.thermal.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/portal/{appid}")
public class WxPortalController {

    private final WxMaService wxMaService;

    @SaIgnore
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String verify(@PathVariable String appid,
                         @RequestParam("signature") String signature,
                         @RequestParam("timestamp") String timestamp,
                         @RequestParam("nonce") String nonce,
                         @RequestParam("echostr") String echostr) {
        if (!wxMaService.switchover(appid)) {
            log.warn("微信验证失败: 未找到appid=[{}]的配置", appid);
            return "fail";
        }
        try {
            if (wxMaService.checkSignature(timestamp, nonce, signature)) {
                WxMaConfigHolder.remove();
                return echostr;
            }
        } finally {
            WxMaConfigHolder.remove();
        }
        return "fail";
    }

    @SaIgnore
    @PostMapping(produces = "application/xml;charset=UTF-8")
    public void handleMessage(@PathVariable String appid,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        if (!wxMaService.switchover(appid)) {
            response.getWriter().write("success");
            return;
        }
        try {
            WxMaMessage message = WxMaMessage.fromXml(request.getInputStream());
            log.info("收到小程序消息: appid={}, msgType={}, fromUser={}",
                    appid, message.getMsgType(), message.getFromUser());
            // TODO: 集成消息路由器 WxMaMessageRouter 处理业务消息
            response.getWriter().write("success");
        } catch (Exception e) {
            log.error("处理小程序消息失败: appid={}", appid, e);
            response.getWriter().write("success");
        } finally {
            WxMaConfigHolder.remove();
        }
    }
}
