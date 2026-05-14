package org.sdkj.ai.assistant;

import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * ChatMemory conversationId 工厂。
 * 格式: {tenantId}:user:{userId}:session:{sessionId}
 * 保证多租户隔离 + 多会话隔离。
 */
public final class ConversationIdFactory {

    private ConversationIdFactory() {}

    public static String of(String tenantId, Long userId, Long sessionId) {
        return tenantId + ":user:" + userId + ":session:" + sessionId;
    }

    public static String ofCurrent(Long sessionId) {
        return of(LoginHelper.getTenantId(), LoginHelper.getUserId(), sessionId);
    }
}
