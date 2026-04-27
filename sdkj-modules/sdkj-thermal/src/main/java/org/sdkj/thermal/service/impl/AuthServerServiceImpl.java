package org.sdkj.thermal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.mapper.AgUserMapper;
import org.sdkj.thermal.service.IAuthServerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 认证服务实现
 * 支持多租户用户名密码登录和Token登出
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServerServiceImpl implements IAuthServerService {

    private final AgUserMapper agUserMapper;

    @Override
    public Map<String, Object> login(String tenantId, String username, String password) {
        // 查询用户（按 userName）
        AgUser user = agUserMapper.selectOne(
            new LambdaQueryWrapper<AgUser>()
                .eq(AgUser::getUserName, username));

        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("用户不存在");
        }

        // 检查用户是否启用
        if (user.getIsEnabled() != null && user.getIsEnabled() == 0) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("该账户尚未启用");
        }

        // BCrypt 校验密码（与 RuoYi-Vue-Plus 新系统密码验证方式一致）
        if (!BCrypt.checkpw(password, user.getUserPwd())) {
            log.info("登录用户：{} 密码错误.", username);
            throw new ServiceException("账号名或密码错误");
        }

        // Sa-Token 登录
        StpUtil.login(user.getId());

        // 在 Session 中存储租户信息
        StpUtil.getSession().set("tenantId", tenantId);
        StpUtil.getSession().set("tenantCode", tenantId);

        // 返回 token 信息
        Map<String, Object> result = new HashMap<>();
        result.put("tokenName", StpUtil.getTokenName());
        result.put("tokenValue", StpUtil.getTokenValue());
        return result;
    }

    @Override
    public Map<String, Object> miniappLogin(String tenantId, String code) {
        throw new UnsupportedOperationException("小程序登录待集成微信SDK");
    }

    @Override
    public void logout(String tokenValue) {
        // 处理 Authorization 头中的 "Bearer " 前缀
        String token = tokenValue;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        StpUtil.logoutByTokenValue(token);
    }
}
