package org.sdkj.ai.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.ai.domain.AiPendingToolCall;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = AiPendingToolCall.class)
public class AiPendingToolCallVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
