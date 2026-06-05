package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;

@OrgPermission
public interface PrValveOperationLogMapper extends BaseMapperPlus<PrValveOperationLog, PrValveOperationLogVo> {
}
