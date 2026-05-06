package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.vo.PrBuildingVo;

/**
 * 楼宇信息 Mapper
 * 迁移自旧系统 PrBuildingMapper
 */
@OrgPermission
public interface PrBuildingMapper extends BaseMapperPlus<PrBuilding, PrBuildingVo> {

}
