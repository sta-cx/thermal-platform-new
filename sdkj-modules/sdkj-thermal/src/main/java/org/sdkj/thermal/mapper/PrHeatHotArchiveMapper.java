package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;

import java.util.List;

/**
 * 热力热量档案 Mapper
 */
public interface PrHeatHotArchiveMapper extends BaseMapperPlus<PrHeatHotArchive, PrHeatHotArchiveVo> {

    /**
     * 云谷数据同步：按 meterNum 列表查询热表+阀门数据
     */
    List<PrHeatHotArchive> getValveHotDataByList(@Param("meterNumList") List<String> meterNumList);
}
