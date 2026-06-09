package org.sdkj.ai.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import org.sdkj.ai.domain.AiChatSession;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
@DS("master")
public interface AiChatSessionMapper extends BaseMapperPlus<AiChatSession, AiChatSession> {

    /** 只更新 context_data 列，不触碰其他字段。 */
    default void updateContextData(Long sessionId, String json) {
        update(null, Wrappers.<AiChatSession>lambdaUpdate()
            .set(AiChatSession::getContextData, json)
            .eq(AiChatSession::getId, sessionId));
    }
}
