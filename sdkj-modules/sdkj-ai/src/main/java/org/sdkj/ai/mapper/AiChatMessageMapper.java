package org.sdkj.ai.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.sdkj.ai.domain.AiChatMessage;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
@DS("master")
public interface AiChatMessageMapper extends BaseMapperPlus<AiChatMessage, AiChatMessage> {
}
