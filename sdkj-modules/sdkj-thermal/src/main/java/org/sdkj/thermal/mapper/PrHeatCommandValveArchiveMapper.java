package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHeatCommandValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatCommandValveArchiveVo;

/**
 * 热力指令阀门档案 Mapper
 */
@OrgPermission
public interface PrHeatCommandValveArchiveMapper extends BaseMapperPlus<PrHeatCommandValveArchive, PrHeatCommandValveArchiveVo> {
}
