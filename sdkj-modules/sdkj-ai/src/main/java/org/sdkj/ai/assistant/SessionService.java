package org.sdkj.ai.assistant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.sdkj.ai.AiConstants;
import lombok.RequiredArgsConstructor;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.sdkj.ai.domain.AiChatMessage;
import org.sdkj.ai.domain.AiChatSession;
import org.sdkj.ai.mapper.AiChatMessageMapper;
import org.sdkj.ai.mapper.AiChatSessionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@DS("master")
@RequiredArgsConstructor
public class SessionService {

    private final AiChatSessionMapper sessionMapper;
    private final AiChatMessageMapper messageMapper;

    public AiChatSession create(String tenantId, Long userId, String title) {
        AiChatSession s = new AiChatSession();
        s.setTenantId(tenantId);
        s.setUserId(userId);
        s.setTitle(title);
        s.setLastActiveAt(new Date());
        s.setCreateTime(new Date());
        sessionMapper.insert(s);
        return s;
    }

    public List<AiChatSession> listByUser(String tenantId, Long userId) {
        return sessionMapper.selectList(
            new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getTenantId, tenantId)
                .eq(AiChatSession::getUserId, userId)
                .orderByDesc(AiChatSession::getLastActiveAt)
                .last("limit " + AiConstants.MAX_SESSIONS_PER_USER)
        );
    }

    public AiChatSession requireOwned(Long sessionId, String tenantId, Long userId) {
        AiChatSession s = sessionMapper.selectById(sessionId);
        if (s == null
            || !tenantId.equals(s.getTenantId())
            || !userId.equals(s.getUserId())) {
            throw new IllegalArgumentException("session 不存在或无权访问");
        }
        return s;
    }

    /**
     * 插入消息 + 更新 session.lastActiveAt 必须原子,否则失败时:
     * 消息插入成功但 session 时钟未刷新,会话排序会失真。
     */
    @Transactional(rollbackFor = Exception.class)
    public void appendMessage(Long sessionId, String role, String content, Integer tokenCount) {
        AiChatMessage m = new AiChatMessage();
        m.setSessionId(sessionId);
        m.setRole(role);
        m.setContent(content);
        m.setTokenCount(tokenCount);
        m.setCreateTime(new Date());
        messageMapper.insert(m);
        AiChatSession update = new AiChatSession();
        update.setId(sessionId);
        update.setLastActiveAt(new Date());
        sessionMapper.updateById(update);
    }

    public List<AiChatMessage> listMessages(Long sessionId) {
        return messageMapper.selectList(
            new LambdaQueryWrapper<AiChatMessage>()
                .eq(AiChatMessage::getSessionId, sessionId)
                .orderByAsc(AiChatMessage::getCreateTime)
        );
    }

    /**
     * 落库一条 ASSISTANT 消息并返回其 id(供 SSE 流末尾下发 messageId,前端落地为真消息、刷新不丢)。
     * 能力 C 编排收尾语走此方法(见 spec §6.5「一句收尾」)。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long appendAssistantMessage(Long sessionId, String content) {
        AiChatMessage row = new AiChatMessage();
        row.setSessionId(sessionId);
        row.setRole(AiConstants.ChatRole.ASSISTANT.name());
        row.setContent(content);
        row.setCreateTime(new Date());
        messageMapper.insert(row);
        AiChatSession update = new AiChatSession();
        update.setId(sessionId);
        update.setLastActiveAt(new Date());
        sessionMapper.updateById(update);
        return row.getId();
    }

    /**
     * 把 Tool 调用结果作为对话消息落库(role=TOOL)。
     * Content 是给前端流里展示的"短摘要"(如"已创建报修 #123"),完整入参出参在 ai_tool_invocation 表。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long appendToolMessage(Long sessionId, String summaryContent) {
        AiChatMessage row = new AiChatMessage();
        row.setSessionId(sessionId);
        row.setRole(AiConstants.ChatRole.TOOL.name());
        row.setContent(summaryContent);
        row.setCreateTime(new Date());
        messageMapper.insert(row);
        // 更新 session lastActiveAt
        AiChatSession update = new AiChatSession();
        update.setId(sessionId);
        update.setLastActiveAt(new Date());
        sessionMapper.updateById(update);
        return row.getId();
    }

    /**
     * 删除 session + 所有附属 message 必须原子,否则失败时孤儿 message 永留(snowflake ID
     * 不复用,但储存膨胀)。
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long sessionId) {
        messageMapper.delete(
            new LambdaQueryWrapper<AiChatMessage>().eq(AiChatMessage::getSessionId, sessionId)
        );
        sessionMapper.deleteById(sessionId);
    }
}
