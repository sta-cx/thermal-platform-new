package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHouseLog;

import java.util.List;

@OrgPermission
public interface PrHouseLogMapper extends BaseMapper<PrHouseLog> {

    List<PrHouseLog> selectHouseChangeData(@Param("changeType") String changeType);

    List<PrHouseLog> selectUnitChangeData(@Param("changeType") String changeType);

    /**
     * 按 changeType + houseId 查询（带 LIMIT），避免全表扫描
     */
    List<PrHouseLog> selectByHouseIdAndType(@Param("houseId") Long houseId,
                                             @Param("changeType") String changeType);
}
