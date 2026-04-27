package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrWechatOrder;
import org.sdkj.thermal.domain.vo.PrWechatOrderVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 微信支付订单 Mapper
 */
public interface PrWechatOrderMapper extends BaseMapperPlus<PrWechatOrder, PrWechatOrderVo> {

    PrWechatOrder selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    PrWechatOrder selectByTransactionId(@Param("transactionId") String transactionId);

    List<PrWechatOrder> selectByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<PrWechatOrder> selectExpiredOrders(
            @Param("pendingStatus") Integer pendingStatus,
            @Param("now") LocalDateTime now);

    Integer insertPrTransactionRecordByWechat(
            @Param("id") String id, @Param("houseId") String houseId,
            @Param("paidIn") String paidIn, @Param("billNum") String billNum,
            @Param("transactionId") String transactionId, @Param("openId") String openId);

    Integer updatePrExponseByWechat(
            @Param("houseId") String houseId, @Param("paidIn") String paidIn,
            @Param("recordId") String recordId, @Param("openId") String openId,
            @Param("year") String year);

    Integer updatePrHouse(@Param("houseId") String houseId);
}
