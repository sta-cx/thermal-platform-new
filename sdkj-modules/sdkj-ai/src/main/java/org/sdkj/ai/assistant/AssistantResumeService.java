package org.sdkj.ai.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 用户在前端 confirm 后,调 /ai/tool-calls/{id}/confirm。
 * 该端点同步执行 Tool → 落库 → 调用本服务的 resume → 拉新的 SSE 流。
 */
@Service
@RequiredArgsConstructor
public class AssistantResumeService {

    private final AssistantService assistantService;
    private final SessionService sessionService;

    /**
     * @param toolResultJson 工具执行结果 JSON（非空时写入 ChatMemory 作为 ASSISTANT 上下文）
     */
    public Flux<AssistantChunk> resumeAfterToolCall(
        String tenantId, Long userId, Long sessionId, String toolResultJson) {
        // 将工具结果写入 session message（ChatMemory 通过 JDBC 已有之前的对话）
        if (toolResultJson != null && !toolResultJson.isBlank()) {
            sessionService.appendMessage(sessionId, "ASSISTANT",
                "[Tool 执行完成] 结果: " + toolResultJson, null);
        }
        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return assistantService.streamRound(
            "请基于刚才工具执行的结果继续回复。", tenantId, userId, sessionId, conversationId, 0);
    }
}
