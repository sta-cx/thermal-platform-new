package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ai_call_record")
public class AiCallRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String tenantId;
    private Long userId;
    private String feature;
    private String conversationId;
    private String contextSummary;
    private String outputSummary;
    private String toolsInvoked;
    private Date createdAt;
}
