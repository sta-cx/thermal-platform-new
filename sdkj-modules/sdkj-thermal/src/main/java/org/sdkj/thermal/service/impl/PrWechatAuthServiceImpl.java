package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.config.WechatAuthConfig;
import org.sdkj.thermal.domain.PrWechatUser;
import org.sdkj.thermal.domain.vo.PrWechatUserVo;
import org.sdkj.thermal.mapper.PrWechatUserMapper;
import org.sdkj.thermal.service.IPrWechatAuthService;
import org.sdkj.thermal.wechat.entity.rest.OauthInfo;
import org.sdkj.thermal.wechat.utils.WeixinAPIHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 微信授权 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrWechatAuthServiceImpl extends ServiceImpl<PrWechatUserMapper, PrWechatUser>
        implements IPrWechatAuthService {

    private final PrWechatUserMapper baseMapper;
    private final WechatAuthConfig wechatAuthConfig;
    private static final String SCOPE_BASE = "snsapi_base";
    private static final String OPEN_URL_TEMPLATE =
        "https://open.weixin.qq.com/connect/oauth2/authorize" +
        "?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    @Override
    public String buildAuthUrl(String redirectUri, String state) {
        try {
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
            return String.format(OPEN_URL_TEMPLATE,
                    wechatAuthConfig.getAppid(), encodedRedirectUri, SCOPE_BASE, state);
        } catch (Exception e) {
            log.error("构建微信授权URL失败", e);
            throw new RuntimeException("授权链接生成失败");
        }
    }

    @Override
    public String getOpenIdByCode(String code) {
        log.info("获取OpenID开始...code:{}", code);
        try {
            // Phase 6: 集成微信 SDK 后调用
            // 当前使用 WeixinAPIHelper 工具
            OauthInfo oauthInfo = WeixinAPIHelper.getOauthInfo(
                    wechatAuthConfig.getAppid(), wechatAuthConfig.getAppSecret(), code);

            if (oauthInfo == null || oauthInfo.getOpenId() == null) {
                log.error("获取OpenID失败");
                throw new RuntimeException("获取用户信息失败");
            }

            return oauthInfo.getOpenId();
        } catch (Exception e) {
            log.error("获取OpenID异常", e);
            throw new RuntimeException("获取用户信息异常");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PrWechatUser bindWechatUser(String openId, String otherCode, String userName, String phone) {
        // 检查是否已绑定
        LambdaQueryWrapper<PrWechatUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrWechatUser::getOpenId, openId)
                .eq(PrWechatUser::getOtherCode, otherCode)
                .eq(PrWechatUser::getIsDeleted, 0);
        PrWechatUser existingUser = baseMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            return existingUser;
        }

        // 创建新绑定关系
        PrWechatUser wechatUser = new PrWechatUser();
        wechatUser.setOpenId(openId);
        wechatUser.setOtherCode(otherCode);
        wechatUser.setUserName(userName);
        wechatUser.setPhone(phone);
        wechatUser.setBindStatus(1);
        wechatUser.setIsDeleted(0);
        wechatUser.setCreateTime(new java.util.Date());
        wechatUser.setUpdateTime(new java.util.Date());

        baseMapper.insert(wechatUser);
        return wechatUser;
    }

    @Override
    public PrWechatUserVo getUserBindInfo(String openId) {
        PrWechatUser user = baseMapper.selectByOpenId(openId);
        if (user == null) {
            return null;
        }
        PrWechatUserVo vo = new PrWechatUserVo();
        vo.setId(user.getId());
        vo.setOpenId(user.getOpenId());
        vo.setOtherCode(user.getOtherCode());
        vo.setHouseId(user.getHouseId());
        vo.setUserName(user.getUserName());
        vo.setPhone(user.getPhone());
        vo.setBindStatus(user.getBindStatus());
        vo.setUnionId(user.getUnionId());
        return vo;
    }

    @Override
    public boolean unbindUser(String openId) {
        LambdaQueryWrapper<PrWechatUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrWechatUser::getOpenId, openId);
        PrWechatUser user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            return false;
        }
        user.setBindStatus(0);
        user.setIsDeleted(1);
        user.setUpdateTime(new java.util.Date());
        return baseMapper.updateById(user) > 0;
    }
}
