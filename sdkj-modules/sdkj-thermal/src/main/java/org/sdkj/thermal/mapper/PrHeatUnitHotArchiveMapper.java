package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;

/**
 * 热力单元热量档案 Mapper
 */
@OrgPermission
public interface PrHeatUnitHotArchiveMapper extends BaseMapperPlus<PrHeatUnitHotArchive, PrHeatUnitHotArchiveVo> {
}
