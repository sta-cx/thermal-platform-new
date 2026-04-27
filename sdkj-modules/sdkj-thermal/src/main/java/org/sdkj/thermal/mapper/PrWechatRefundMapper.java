package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrWechatRefund;
import org.sdkj.thermal.domain.vo.PrWechatRefundVo;

/**
 * 微信退款 Mapper
 */
public interface PrWechatRefundMapper extends BaseMapperPlus<PrWechatRefund, PrWechatRefundVo> {

    PrWechatRefund selectByOutRefundNo(@Param("outRefundNo") String outRefundNo);

    PrWechatRefund selectByRefundId(@Param("refundId") String refundId);

    PrWechatRefund selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);
}
