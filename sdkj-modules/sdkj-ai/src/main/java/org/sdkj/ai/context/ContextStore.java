package org.sdkj.ai.context;

public interface ContextStore {
    /** Redis → DB → 空，加载后已执行 applyForgetting。永不返回 null。 */
    ConversationContext load(Long sessionId);
    /** Redis 同步写 + DB 异步落盘。 */
    void save(ConversationContext ctx);
}
