package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.HtScope;
import org.sdkj.thermal.domain.vo.HtScopeVo;

/**
 * 控制范围Mapper
 */
@OrgPermission
public interface HtScopeMapper extends BaseMapperPlus<HtScope, HtScopeVo> {
}
