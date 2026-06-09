package org.sdkj.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ai_chat_session")
public class AiChatSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String tenantId;
    private Long userId;
    private String title;
    private Date lastActiveAt;
    private Date createTime;

    /** Phase3 会话上下文记忆(JSON 字符串: focus + facts)。null 表示无记忆。 */
    private String contextData;
}
