package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/user/{appid}")
public class WxMaUserController {

    @SaIgnore
    @GetMapping("/login")
    public R<Void> login(@PathVariable String appid, @RequestParam String code) {
        // TODO: Phase 5d - 微信小程序登录 (WxMaService.getUserService().getSessionInfo)
        return R.fail("小程序登录待实现");
    }

    @SaIgnore
    @GetMapping("/info")
    public R<Void> info(@PathVariable String appid,
                        @RequestParam String sessionKey,
                        @RequestParam String signature,
                        @RequestParam String rawData,
                        @RequestParam String encryptedData,
                        @RequestParam String iv) {
        // TODO: Phase 5d - 获取微信小程序用户信息
        return R.fail("用户信息获取待实现");
    }

    @SaIgnore
    @GetMapping("/phone")
    public R<Void> phone(@PathVariable String appid,
                         @RequestParam String sessionKey,
                         @RequestParam String signature,
                         @RequestParam String rawData,
                         @RequestParam String encryptedData,
                         @RequestParam String iv) {
        // TODO: Phase 5d - 获取微信小程序用户手机号
        return R.fail("手机号获取待实现");
    }
}
