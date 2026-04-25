package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.vo.PrHouseVo;

/**
 * 房屋 Mapper
 */
public interface PrHouseMapper extends BaseMapperPlus<PrHouse, PrHouseVo> {

    /**
     * 查询房屋其他编码
     */
    String selectOtherCodeById(@Param("id") String id);

    /**
     * 更新房屋其他编码
     */
    int updateOtherCodeById(@Param("id") String id, @Param("otherCode") String otherCode);
}
