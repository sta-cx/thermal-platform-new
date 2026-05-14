package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * AI 知识库文档元数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_knowledge_doc")
public class AiKnowledgeDoc extends BaseEntity {

    @TableId
    private Long id;

    private String tenantId;
    private String title;
    private String sourceFormat;
    private Integer sourceSize;
    private String sourceHash;
    private Integer chunkCount;
    private String status;
    private String errorMessage;
}
