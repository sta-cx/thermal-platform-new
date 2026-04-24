package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrBillingNotes;
import org.sdkj.thermal.domain.vo.PrBillingNotesVo;

/**
 * 票据备注 Mapper
 */
public interface PrBillingNotesMapper extends BaseMapperPlus<PrBillingNotes, PrBillingNotesVo> {

    /**
     * 根据流水号查询票据备注
     */
    PrBillingNotes selectBySerialNum(@Param("serialNum") String serialNum);
}
