package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;

/**
 * 热力单元阀门档案 Mapper
 */
@OrgPermission
public interface PrHeatUnitValveArchiveMapper extends BaseMapperPlus<PrHeatUnitValveArchive, PrHeatUnitValveArchiveVo> {
}
