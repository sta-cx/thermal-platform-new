package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.dto.LtValveDataResponse;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 热力阀门档案 Mapper
 */
@OrgPermission
public interface PrHeatValveArchiveMapper extends BaseMapperPlus<PrHeatValveArchive, PrHeatValveArchiveVo> {

    /**
     * 新奥阀门数据查询（JOIN pr_house）
     */
    List<LtValveDataResponse> getLTValveData(@Param("meterList") List<String> meterList);

    /**
     * 温度反写到 pr_house 表
     * 用于 NB/MBus 阀门数据接收后更新进水/出水温度
     */
    void updateHouse(@Param("houseId") Long houseId,
                     @Param("inTemperature") BigDecimal inTemperature,
                     @Param("outTemperature") BigDecimal outTemperature,
                     @Param("actualOpen") Integer actualOpen);
}
