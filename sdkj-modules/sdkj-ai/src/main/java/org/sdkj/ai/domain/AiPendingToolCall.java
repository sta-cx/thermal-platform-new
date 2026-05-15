package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.ai.domain.vo.AiPendingToolCallVo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ai_pending_tool_call")
@AutoMapper(target = AiPendingToolCallVo.class)
public class AiPendingToolCall implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String callId;
    private String tenantId;
    private Long userId;
    private Long sessionId;
    private Long messageId;
    private String toolName;
    private String riskLevel;
    private String arguments;
    private String effectiveArgs;
    private String status;
    private String result;
    private Date createdTime;
    private Date decidedTime;
    private Date executedTime;
    private Date expireTime;
}
