package org.sdkj.ai.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.sdkj.ai.domain.AiCallRecord;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
@DS("master")
public interface AiCallRecordMapper extends BaseMapperPlus<AiCallRecord, AiCallRecord> {
}
