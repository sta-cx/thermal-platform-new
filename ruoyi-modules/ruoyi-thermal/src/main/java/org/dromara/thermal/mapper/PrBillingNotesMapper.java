package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrBillingNotes;
import org.dromara.thermal.domain.vo.PrBillingNotesVo;

/**
 * 票据备注 Mapper
 */
public interface PrBillingNotesMapper extends BaseMapperPlus<PrBillingNotes, PrBillingNotesVo> {

    /**
     * 根据流水号查询票据备注
     */
    PrBillingNotes selectBySerialNum(@Param("serialNum") String serialNum);
}
