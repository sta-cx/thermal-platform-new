package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtMeterSort;
import org.sdkj.meter.domain.vo.MtMeterSortVo;

/**
 * 仪表分类Mapper
 */
public interface MtMeterSortMapper extends BaseMapperPlus<MtMeterSort, MtMeterSortVo> {

    /**
     * 检查分类是否被档案表引用
     */
    int countBySortId(@Param("sortId") String sortId, @Param("meterType") String meterType);
}
