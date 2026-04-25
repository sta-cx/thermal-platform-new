package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/thermal/wx/user/{appid}")
public class WxMaUserController {

    @SaIgnore
    @GetMapping("/login")
    public R<Object> login(@PathVariable String appid, @RequestParam String code) {
        if (code == null || code.isEmpty()) return R.fail("登录凭证 code 不能为空");
        log.info("小程序登录: appid={}, code={}", appid, code);
        // Phase 6: WxMaService.getUserService().getSessionInfo(code) → openId, sessionKey, unionId
        // Phase 6: 查询/创建 pr_wechat_bind_record 用户绑定记录
        // Phase 6: 生成 Sa-Token 登录态并返回
        return R.fail("小程序登录 Phase 6 实现 — 需 WxMaService SDK");
    }

    @SaIgnore
    @GetMapping("/info")
    public R<Object> info(@PathVariable String appid,
                          @RequestParam String sessionKey,
                          @RequestParam String signature,
                          @RequestParam String rawData,
                          @RequestParam String encryptedData,
                          @RequestParam String iv) {
        if (sessionKey == null || sessionKey.isEmpty()) return R.fail("sessionKey 不能为空");
        if (encryptedData == null || encryptedData.isEmpty()) return R.fail("encryptedData 不能为空");
        log.info("获取小程序用户信息: appid={}", appid);
        // Phase 6: 签名校验 — Sha1(rawData + sessionKey) 与 signature 比对
        // Phase 6: 解密 — WxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv)
        return R.fail("用户信息获取 Phase 6 实现 — 需 WxMaService SDK");
    }

    @SaIgnore
    @GetMapping("/phone")
    public R<Object> phone(@PathVariable String appid,
                           @RequestParam String sessionKey,
                           @RequestParam String encryptedData,
                           @RequestParam String iv) {
        if (sessionKey == null || sessionKey.isEmpty()) return R.fail("sessionKey 不能为空");
        if (encryptedData == null || encryptedData.isEmpty()) return R.fail("encryptedData 不能为空");
        log.info("获取小程序手机号: appid={}", appid);
        // Phase 6: WxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv)
        return R.fail("手机号获取 Phase 6 实现 — 需 WxMaService SDK");
    }
}
