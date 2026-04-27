package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrWechatBill;
import org.sdkj.thermal.domain.vo.PrWechatBillVo;

/**
 * 微信账单 Mapper
 */
public interface PrWechatBillMapper extends BaseMapperPlus<PrWechatBill, PrWechatBillVo> {

    PrWechatBill selectByDateAndType(@Param("billDate") String billDate, @Param("billType") String billType);
}
