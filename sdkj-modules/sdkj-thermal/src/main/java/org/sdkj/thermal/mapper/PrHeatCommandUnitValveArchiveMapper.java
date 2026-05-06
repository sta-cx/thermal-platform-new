package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHeatCommandUnitValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatCommandUnitValveArchiveVo;

/**
 * 热力指令单元阀门档案 Mapper
 */
@OrgPermission
public interface PrHeatCommandUnitValveArchiveMapper extends BaseMapperPlus<PrHeatCommandUnitValveArchive, PrHeatCommandUnitValveArchiveVo> {
}
