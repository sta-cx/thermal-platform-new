package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * AI 知识库切片元数据（向量本体存 Qdrant,此处只存 fk 与 payload）
 */
@Data
@TableName("ai_knowledge_chunk")
public class AiKnowledgeChunk implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private Long docId;
    private String tenantId;
    private Integer chunkIndex;
    private String text;
    private Integer tokenCount;
    private String qdrantPointId;
    private Date createTime;
}
