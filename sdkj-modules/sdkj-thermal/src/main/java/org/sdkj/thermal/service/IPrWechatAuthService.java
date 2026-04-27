package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrWechatUser;
import org.sdkj.thermal.domain.vo.PrWechatUserVo;

import java.util.Map;

/**
 * 微信授权 Service
 */
public interface IPrWechatAuthService extends IService<PrWechatUser> {

    /**
     * 构建微信授权URL
     */
    String buildAuthUrl(String redirectUri, String state);

    /**
     * 通过 code 获取 OpenID
     */
    String getOpenIdByCode(String code);

    /**
     * 绑定微信用户与缴费码
     */
    PrWechatUser bindWechatUser(String openId, String otherCode, String userName, String phone);

    /**
     * 查询用户绑定信息
     */
    PrWechatUserVo getUserBindInfo(String openId);

    /**
     * 解绑用户
     */
    boolean unbindUser(String openId);
}
