package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrUserHouse;
import org.sdkj.thermal.domain.vo.PrUserHouseVo;

import java.util.List;

@OrgPermission
public interface PrUserHouseMapper extends BaseMapperPlus<PrUserHouse, PrUserHouseVo> {

    /**
     * 查询房屋变更历史（含已删除记录，绕过逻辑删除）
     */
    List<PrUserHouseVo> selectChangeHistory(@Param("houseId") Long houseId,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate);
}
