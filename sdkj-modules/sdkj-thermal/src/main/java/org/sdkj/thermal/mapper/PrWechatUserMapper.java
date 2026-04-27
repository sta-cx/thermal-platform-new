package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrWechatUser;
import org.sdkj.thermal.domain.vo.PrWechatUserVo;

/**
 * 微信用户 Mapper
 */
public interface PrWechatUserMapper extends BaseMapperPlus<PrWechatUser, PrWechatUserVo> {

    PrWechatUser selectByOpenId(@Param("openId") String openId);

    PrWechatUser selectByOpenIdAndOtherCode(@Param("openId") String openId, @Param("otherCode") String otherCode);
}
