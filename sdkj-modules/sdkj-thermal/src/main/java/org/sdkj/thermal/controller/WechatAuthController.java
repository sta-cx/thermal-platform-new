package org.sdkj.thermal.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.thermal.domain.PrWechatUser;
import org.sdkj.thermal.domain.vo.PrWechatUserVo;
import org.sdkj.thermal.service.IPrWechatAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信授权控制器
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/auth")
public class WechatAuthController {

    private final IPrWechatAuthService wechatAuthService;
    private final WxMaService wxMaService;

    @Value("${wechat.auth.redirect-uri:}")
    private String redirectUri;

    @Value("${wechat.auth.frontend-query-page:}")
    private String frontendQueryPage;

    @SaIgnore
    @GetMapping("/url")
    public R<String> getAuthUrl(@RequestParam(required = false) String state) {
        String s = state != null && !state.isEmpty() ? state : "sdkj";
        String authUrl = wechatAuthService.buildAuthUrl(redirectUri, s);
        return R.ok(authUrl);
    }

    @SaIgnore
    @GetMapping("/callback")
    public void authCallback(@RequestParam String code,
                             @RequestParam(required = false) String state,
                             HttpServletResponse response) throws IOException {
        log.info("微信授权回调: code={}, state={}", code, state);
        try {
            String openId = wechatAuthService.getOpenIdByCode(code);

            StringBuilder callbackUrl = new StringBuilder(frontendQueryPage);
            callbackUrl.append("?openId=").append(URLEncoder.encode(openId, StandardCharsets.UTF_8));

            if (state != null && !state.isEmpty()) {
                callbackUrl.append("&state=").append(URLEncoder.encode(state, StandardCharsets.UTF_8));
            }

            response.sendRedirect(callbackUrl.toString());
        } catch (Exception e) {
            log.error("微信授权回调处理异常", e);
            response.sendRedirect(frontendQueryPage + "?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    @SaIgnore
    @Log(title = "微信绑定", businessType = BusinessType.INSERT)
    @PostMapping("/bind")
    public R<PrWechatUser> bindWechatUser(@RequestParam String openId,
                                           @RequestParam String otherCode,
                                           @RequestParam String userName,
                                           @RequestParam String phone) {
        log.info("绑定微信用户: openId={}, otherCode={}, userName={}, phone={}",
                openId, otherCode, userName, phone);
        if (openId == null || openId.isEmpty()) return R.fail("openId不能为空");
        if (otherCode == null || otherCode.isEmpty()) return R.fail("缴费码不能为空");
        if (phone == null || phone.isEmpty()) return R.fail("手机号不能为空");

        try {
            PrWechatUser user = wechatAuthService.bindWechatUser(openId, otherCode, userName, phone);
            return R.ok(user);
        } catch (Exception e) {
            log.error("绑定微信用户失败", e);
            return R.fail(e.getMessage());
        }
    }

    @SaIgnore
    @PostMapping("/unbind")
    public R<Void> unbindWechatUser(@RequestParam String openId) {
        log.info("解绑微信用户: openId={}", openId);
        if (openId == null || openId.isEmpty()) return R.fail("openId不能为空");

        boolean result = wechatAuthService.unbindUser(openId);
        if (result) {
            return R.ok();
        }
        return R.fail("解绑失败，用户不存在");
    }

    @SaIgnore
    @GetMapping("/userInfo")
    public R<PrWechatUserVo> getUserInfo(@RequestParam String openId) {
        log.info("查询微信用户信息: openId={}", openId);
        if (openId == null || openId.isEmpty()) return R.fail("openId不能为空");

        PrWechatUserVo userVO = wechatAuthService.getUserBindInfo(openId);
        if (userVO == null) {
            return R.fail("用户不存在");
        }
        return R.ok(userVO);
    }

    @SaIgnore
    @GetMapping("/bindStatus")
    public R<Boolean> getUserBindStatus(@RequestParam String openId) {
        if (openId == null || openId.isEmpty()) return R.fail("openId不能为空");
        PrWechatUserVo userVO = wechatAuthService.getUserBindInfo(openId);
        return R.ok(userVO != null && userVO.getBindStatus() != null && userVO.getBindStatus() == 1);
    }

    /**
     * 微信小程序登录
     * 流程: code → openId/sessionKey → 查用户 → 生成 Sa-Token JWT
     */
    @SaIgnore
    @PostMapping("/miniapp/login")
    public R<Map<String, Object>> miniappLogin(@RequestParam String appid, @RequestParam String code) {
        if (!wxMaService.switchover(appid)) {
            return R.fail("未找到对应appid=[" + appid + "]的小程序配置");
        }
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openId = session.getOpenid();
            String sessionKey = session.getSessionKey();
            String unionId = session.getUnionid();

            PrWechatUserVo userVo = wechatAuthService.getUserBindInfo(openId);
            if (userVo == null || userVo.getBindStatus() != 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("openid", openId);
                result.put("sessionKey", sessionKey);
                result.put("unionid", unionId);
                result.put("bound", false);
                return R.ok(result);
            }

            // 使用 Sa-Token 创建登录会话
            StpUtil.login(userVo.getId());
            String token = StpUtil.getTokenValue();

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("openid", openId);
            result.put("bound", true);
            result.put("userInfo", userVo);
            return R.ok(result);
        } catch (Exception e) {
            log.error("小程序登录失败: appid={}, code={}", appid, code, e);
            return R.fail("登录失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(wechatAuthService.list());
    }
}
