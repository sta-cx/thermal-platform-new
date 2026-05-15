package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ai_tool_invocation")
public class AiToolInvocation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long messageId;
    private String pendingCallId;
    private String tenantId;
    private Long userId;
    private String toolName;
    private String riskLevel;
    private String arguments;
    private String result;
    private String status;
    private Integer latencyMs;
    private String errorMessage;
    private Date createdTime;
}
