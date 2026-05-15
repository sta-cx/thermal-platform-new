package org.sdkj.ai.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.sdkj.ai.domain.AiPendingToolCall;
import org.sdkj.ai.domain.vo.AiPendingToolCallVo;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
@DS("master")
public interface AiPendingToolCallMapper extends BaseMapperPlus<AiPendingToolCall, AiPendingToolCallVo> {
}
