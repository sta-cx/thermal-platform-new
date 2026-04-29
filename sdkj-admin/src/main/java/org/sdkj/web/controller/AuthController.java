package org.sdkj.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.sdkj.common.core.constant.SystemConstants;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.domain.model.LoginBody;
import org.sdkj.common.core.domain.model.RegisterBody;
import org.sdkj.common.core.domain.model.SocialLoginBody;
import org.sdkj.common.core.utils.*;
import org.sdkj.common.encrypt.annotation.ApiEncrypt;
import org.sdkj.common.json.utils.JsonUtils;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.social.config.properties.SocialLoginConfigProperties;
import org.sdkj.common.social.config.properties.SocialProperties;
import org.sdkj.common.social.utils.SocialUtils;
import org.sdkj.common.sse.dto.SseMessageDto;
import org.sdkj.common.sse.utils.SseMessageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.sdkj.system.domain.SysTenantUser;
import org.sdkj.system.domain.vo.SysClientVo;
import org.sdkj.system.domain.vo.SysTenantVo;
import org.sdkj.system.mapper.SysTenantUserMapper;
import org.sdkj.system.service.ISysClientService;
import org.sdkj.system.service.ISysConfigService;
import org.sdkj.system.service.ISysSocialService;
import org.sdkj.system.service.ISysTenantService;
import org.sdkj.web.domain.vo.LoginVo;
import org.sdkj.web.service.IAuthStrategy;
import org.sdkj.web.service.SysLoginService;
import org.sdkj.web.service.SysRegisterService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 认证
 *
 * @author Lion Li
 */
@Slf4j
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SocialProperties socialProperties;
    private final SysLoginService loginService;
    private final SysRegisterService registerService;
    private final ISysConfigService configService;
    private final ISysTenantService tenantService;
    private final ISysSocialService socialUserService;
    private final ISysClientService clientService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final SysTenantUserMapper tenantUserMapper;


    /**
     * 登录方法
     */
    @ApiEncrypt
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody String body) {
        LoginBody loginBody = JsonUtils.parseObject(body, LoginBody.class);
        ValidatorUtils.validate(loginBody);
        String clientId = loginBody.getClientId();
        String grantType = loginBody.getGrantType();
        SysClientVo client = clientService.queryByClientId(clientId);
        if (ObjectUtil.isNull(client) || !StringUtils.contains(client.getGrantType(), grantType)) {
            log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType);
            return R.fail(MessageUtils.message("auth.grant.type.error"));
        } else if (!SystemConstants.NORMAL.equals(client.getStatus())) {
            return R.fail(MessageUtils.message("auth.grant.type.blocked"));
        }
        LoginVo loginVo = IAuthStrategy.login(body, client, grantType);

        // 登录成功后查询租户绑定
        Long userId = LoginHelper.getUserId();
        List<SysTenantUser> tenantUsers = tenantUserMapper.selectList(
            new LambdaQueryWrapper<SysTenantUser>()
                .eq(SysTenantUser::getUserId, userId));
        if (CollUtil.isEmpty(tenantUsers)) {
            StpUtil.logout();
            return R.fail("当前用户未绑定租户");
        }
        List<SysTenantVo> tenants = tenantUsers.stream()
            .map(tu -> tenantService.queryByTenantId(tu.getTenantId()))
            .filter(t -> t != null && SystemConstants.NORMAL.equals(t.getStatus()))
            .toList();
        if (CollUtil.isEmpty(tenants)) {
            StpUtil.logout();
            return R.fail("当前用户绑定的租户已停用");
        }
        if (tenants.size() == 1) {
            SysTenantVo tenant = tenants.get(0);
            StpUtil.getSession().set("tenantCode", tenant.getTenantId());
            StpUtil.getSession().set("tenantName", tenant.getCompanyName());
            StpUtil.getTokenSession().set("tenantCode", tenant.getTenantId());
            StpUtil.getTokenSession().set("tenantName", tenant.getCompanyName());
        } else {
            loginVo.setNeedSelectTenant(true);
            loginVo.setTenantList(tenants.stream().map(t -> {
                Map<String, String> item = new HashMap<>();
                item.put("tenantId", t.getTenantId());
                item.put("companyName", t.getCompanyName());
                return item;
            }).toList());
        }
        scheduledExecutorService.schedule(() -> {
            SseMessageDto dto = new SseMessageDto();
            dto.setMessage(DateUtils.getTodayHour(new Date()) + "好，欢迎登录 SDKJ 智慧供热综合管理平台");
            dto.setUserIds(List.of(userId));
            SseMessageUtils.publishMessage(dto);
        }, 5, TimeUnit.SECONDS);
        return R.ok(loginVo);
    }

    /**
     * 获取跳转URL
     */
    @GetMapping("/binding/{source}")
    public R<String> authBinding(@PathVariable("source") String source,
                                 @RequestParam String tenantId, @RequestParam String domain) {
        SocialLoginConfigProperties obj = socialProperties.getType().get(source);
        if (ObjectUtil.isNull(obj)) {
            return R.fail(source + "平台账号暂不支持");
        }
        AuthRequest authRequest = SocialUtils.getAuthRequest(source, socialProperties);
        Map<String, String> map = new HashMap<>();
        map.put("tenantId", tenantId);
        map.put("domain", domain);
        map.put("state", AuthStateUtils.createState());
        String authorizeUrl = authRequest.authorize(Base64.encode(JsonUtils.toJsonString(map), StandardCharsets.UTF_8));
        return R.ok("操作成功", authorizeUrl);
    }

    /**
     * 前端回调绑定授权(需要token)
     */
    @PostMapping("/social/callback")
    public R<Void> socialCallback(@RequestBody SocialLoginBody loginBody) {
        StpUtil.checkLogin();
        AuthResponse<AuthUser> response = SocialUtils.loginAuth(
            loginBody.getSource(), loginBody.getSocialCode(),
            loginBody.getSocialState(), socialProperties);
        AuthUser authUserData = response.getData();
        if (!response.ok()) {
            return R.fail(response.getMsg());
        }
        loginService.socialRegister(authUserData);
        return R.ok();
    }

    /**
     * 取消授权(需要token)
     */
    @DeleteMapping(value = "/unlock/{socialId}")
    public R<Void> unlockSocial(@PathVariable Long socialId) {
        StpUtil.checkLogin();
        Boolean rows = socialUserService.deleteWithValidById(socialId);
        return rows ? R.ok() : R.fail("取消授权失败");
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功");
    }

    /**
     * 用户注册
     */
    @ApiEncrypt
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        if (!configService.selectRegisterEnabled(user.getTenantId())) {
            return R.fail("当前系统没有开启注册功能！");
        }
        registerService.register(user);
        return R.ok();
    }

    /**
     * 查询当前用户可用的租户列表（需登录）
     */
    @SaCheckLogin
    @GetMapping("/tenant/list")
    public R<List<Map<String, String>>> tenantList() {
        Long userId = LoginHelper.getUserId();
        List<SysTenantUser> tenantUsers = tenantUserMapper.selectList(
            new LambdaQueryWrapper<SysTenantUser>()
                .eq(SysTenantUser::getUserId, userId));
        List<Map<String, String>> list = tenantUsers.stream()
            .map(tu -> {
                SysTenantVo t = tenantService.queryByTenantId(tu.getTenantId());
                if (t != null && SystemConstants.NORMAL.equals(t.getStatus())) {
                    Map<String, String> item = new HashMap<>();
                    item.put("tenantId", t.getTenantId());
                    item.put("companyName", t.getCompanyName());
                    return item;
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toList();
        return R.ok(list);
    }

    /**
     * 登录后选择租户（多租户用户）
     */
    @PostMapping("/tenant/select")
    public R<Void> selectTenant(@RequestBody Map<String, String> params) {
        String tenantId = params.get("tenantId");
        if (StringUtils.isBlank(tenantId)) {
            return R.fail("请选择租户");
        }
        Long userId = LoginHelper.getUserId();
        SysTenantUser tenantUser = tenantUserMapper.selectByUserIdAndTenantId(userId, tenantId);
        if (tenantUser == null) {
            return R.fail("当前用户未绑定该租户");
        }
        SysTenantVo tenant = tenantService.queryByTenantId(tenantId);
        if (tenant == null || !SystemConstants.NORMAL.equals(tenant.getStatus())) {
            return R.fail("租户不存在或已停用");
        }
        if (tenant.getExpireTime() != null && new Date().after(tenant.getExpireTime())) {
            return R.fail("租户服务已过期");
        }
        StpUtil.getSession().set("tenantCode", tenant.getTenantId());
        StpUtil.getSession().set("tenantName", tenant.getCompanyName());
        StpUtil.getTokenSession().set("tenantCode", tenant.getTenantId());
        StpUtil.getTokenSession().set("tenantName", tenant.getCompanyName());
        return R.ok();
    }

}
