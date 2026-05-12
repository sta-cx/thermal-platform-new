package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ai_usage_log")
public class AiUsageLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String tenantId;
    private Long userId;
    private String feature;
    private String route;
    private String conversationId;
    private String promptName;
    private String toolName;
    private String model;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer costCents;
    private Integer cacheHit;
    private Integer success;
    private String errorMsg;
    private Long durationMs;
    private Date createdAt;
}
