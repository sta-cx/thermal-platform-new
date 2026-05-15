package org.sdkj.ai.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.ai.domain.AiToolInvocation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = AiToolInvocation.class)
public class AiToolInvocationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
