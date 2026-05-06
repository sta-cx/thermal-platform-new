package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrInspectionRecord;

@OrgPermission
public interface PrInspectionRecordMapper extends BaseMapperPlus<PrInspectionRecord, PrInspectionRecord> {
}
