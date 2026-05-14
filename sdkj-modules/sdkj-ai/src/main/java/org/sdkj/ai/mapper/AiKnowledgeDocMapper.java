package org.sdkj.ai.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
@DS("master")
public interface AiKnowledgeDocMapper extends BaseMapperPlus<AiKnowledgeDoc, AiKnowledgeDoc> {

    /**
     * 显式 UPDATE,绕开 MyBatis-Plus FieldStrategy.NOT_NULL — 否则 error_message 永远清不掉。
     * 审查 I7。
     */
    @Update("UPDATE ai_knowledge_doc SET status = #{status}, error_message = #{errMsg} WHERE id = #{docId}")
    int updateStatusWithErrorMessage(@Param("docId") Long docId,
                                      @Param("status") String status,
                                      @Param("errMsg") String errMsg);
}
