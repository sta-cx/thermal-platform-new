package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.dto.LtValveDataResponse;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;

import java.util.List;

/**
 * 热力阀门档案 Mapper
 */
public interface PrHeatValveArchiveMapper extends BaseMapperPlus<PrHeatValveArchive, PrHeatValveArchiveVo> {

    /**
     * 新奥阀门数据查询（JOIN pr_house）
     */
    List<LtValveDataResponse> getLTValveData(@Param("meterList") List<String> meterList);
}
