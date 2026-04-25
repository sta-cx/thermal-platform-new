package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/auth")
public class WechatAuthController {

    @SaIgnore
    @GetMapping("/url")
    public R<String> getAuthUrl(@RequestParam(required = false) String state) {
        // TODO: Phase 5d - 构建微信OAuth2授权链接
        return R.fail("微信授权功能待实现");
    }

    @SaIgnore
    @GetMapping("/callback")
    public String authCallback(@RequestParam String code,
                               @RequestParam(required = false) String state) {
        // TODO: Phase 5d - 微信授权回调，获取openId，重定向前端页面
        log.info("微信授权回调 code={}, state={}", code, state);
        return "redirect:/error";
    }

    @SaIgnore
    @PostMapping("/bind")
    public R<Void> bindWechatUser(@RequestParam String openId,
                                   @RequestParam String otherCode,
                                   @RequestParam String userName,
                                   @RequestParam String phone) {
        // TODO: Phase 5d - 绑定微信用户与缴费码
        return R.fail("绑定功能待实现");
    }

    @SaIgnore
    @GetMapping("/userInfo")
    public R<Void> getUserInfo(@RequestParam String openId) {
        // TODO: Phase 5d - 查询微信用户绑定信息
        return R.fail("查询功能待实现");
    }
}
