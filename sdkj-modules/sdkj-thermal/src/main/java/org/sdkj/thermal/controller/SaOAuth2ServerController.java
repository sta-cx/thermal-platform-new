package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.service.IAuthServerService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Sa-Token OAuth2 Server 多租户认证控制器
 * 提供多租户登录、小程序登录和 Token 登出功能
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class SaOAuth2ServerController {

    private final IAuthServerService authServerService;

    /**
     * 多租户用户名密码登录
     */
    @SaIgnore
    @PostMapping("/login")
    public R<Map<String, Object>> login(
            @RequestParam String tenantId,
            @RequestParam String username,
            @RequestParam String password) {
        Map<String, Object> token = authServerService.login(tenantId, username, password);
        return R.ok(token);
    }

    /**
     * 微信小程序登录（待集成）
     */
    @SaIgnore
    @PostMapping("/miniapp")
    public R<Map<String, Object>> miniappLogin(
            @RequestParam String tenantId,
            @RequestParam String code) {
        Map<String, Object> token = authServerService.miniappLogin(tenantId, code);
        return R.ok(token);
    }

    /**
     * 登出
     */
    @SaCheckLogin
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization") String token) {
        authServerService.logout(token);
        return R.ok();
    }
}
