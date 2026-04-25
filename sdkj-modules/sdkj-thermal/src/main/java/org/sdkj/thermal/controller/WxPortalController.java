package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/portal/{appid}")
public class WxPortalController {

    @SaIgnore
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String appid,
                          @RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        // TODO: Phase 5d - 微信消息服务器验证 (WxMaService.checkSignature)
        log.info("微信消息验证 appid={}", appid);
        return echostr != null ? echostr : "非法请求";
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
        // TODO: Phase 5d - 微信消息路由处理
        log.info("微信消息 appid={}, body={}", appid, requestBody);
        return "success";
    }
}
