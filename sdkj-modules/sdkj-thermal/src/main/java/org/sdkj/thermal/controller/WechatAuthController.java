package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/auth")
public class WechatAuthController {

    /** Phase 6: 从配置读取 appId / appSecret */
    private static final String WECHAT_APP_ID = "wx_app_id_placeholder";
    private static final String OAUTH_REDIRECT_URI = "https://your-domain.com/thermal/wechat/auth/callback";
    private static final String OAUTH_BASE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    @SaIgnore
    @GetMapping("/url")
    public R<String> getAuthUrl(@RequestParam(required = false) String state) {
        String redirectUri = URLEncoder.encode(OAUTH_REDIRECT_URI, StandardCharsets.UTF_8);
        String s = state != null && !state.isEmpty() ? state : "sdkj";
        String url = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect",
            OAUTH_BASE_URL, WECHAT_APP_ID, redirectUri, s);
        log.info("构建微信授权URL: state={}", s);
        return R.ok(url);
    }

    @SaIgnore
    @GetMapping("/callback")
    public String authCallback(@RequestParam String code,
                               @RequestParam(required = false) String state) {
        log.info("微信授权回调 code={}, state={}", code, state);
        if (code == null || code.isEmpty()) {
            return "redirect:/error?msg=missing_auth_code";
        }
        // Phase 6: 调用微信 /sns/oauth2/access_token 获取 openId + accessToken
        // Phase 6: 调用微信 /sns/userinfo 获取用户信息
        // Phase 6: 查询 pr_wechat_bind_record 判断是否已绑定
        // Phase 6: 已绑定 → redirect 前端首页 + token
        // Phase 6: 未绑定 → redirect 前端绑定页面 + openId
        log.info("授权回调待实现完整流程，code={}", code);
        return "redirect:/error?msg=wechat_auth_pending_phase6";
    }

    @SaIgnore
    @PostMapping("/bind")
    public R<Void> bindWechatUser(@RequestParam String openId,
                                   @RequestParam String otherCode,
                                   @RequestParam String userName,
                                   @RequestParam String phone) {
        if (openId == null || openId.isEmpty()) return R.fail("openId 不能为空");
        if (otherCode == null || otherCode.isEmpty()) return R.fail("缴费码不能为空");
        if (phone == null || phone.isEmpty()) return R.fail("手机号不能为空");
        log.info("绑定微信用户: openId={}, otherCode={}, phone={}", openId, otherCode, phone);
        // Phase 6: 插入 pr_wechat_bind_record 表
        return R.fail("绑定功能 Phase 6 实现 — 需创建 pr_wechat_bind_record 表");
    }

    @SaIgnore
    @GetMapping("/userInfo")
    public R<Object> getUserInfo(@RequestParam String openId) {
        if (openId == null || openId.isEmpty()) return R.fail("openId 不能为空");
        log.info("查询微信用户信息: openId={}", openId);
        // Phase 6: 从 pr_wechat_bind_record + pr_user 联表查询
        return R.fail("查询功能 Phase 6 实现 — 需创建 pr_wechat_bind_record 表");
    }
}
