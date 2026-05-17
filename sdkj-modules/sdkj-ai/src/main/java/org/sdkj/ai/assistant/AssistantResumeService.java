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

    public Flux<AssistantChunk> resumeAfterToolCall(
        String tenantId, Long userId, Long sessionId, String toolResultJson) {
        // 写入 ai_chat_message 仅供历史审计（前端过滤 SYSTEM+[Tool 不渲染）。
        // LLM 通过下方 streamRound() 的 prompt 参数获取工具结果上下文。
        if (toolResultJson != null && !toolResultJson.isBlank()) {
            sessionService.appendMessage(sessionId, "SYSTEM",
                "[Tool 执行完成] 结果: " + toolResultJson, null);
        }
        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return assistantService.streamRound(
            "请基于刚才工具执行的结果继续回复。", tenantId, userId, sessionId, conversationId, 0,
            false);
    }

    /**
     * 用户拒绝 Tool 调用后，拉起第二阶段 SSE 让 LLM 告知用户操作已取消。
     */
    public Flux<AssistantChunk> resumeAfterRejection(
        String tenantId, Long userId, Long sessionId, String toolName) {
        sessionService.appendMessage(sessionId, "SYSTEM",
            "[Tool 调用被拒绝] 用户拒绝了 " + toolName + " 的执行。", null);
        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return assistantService.streamRound(
            "用户刚才拒绝了你请求执行的 Tool 操作，请告知用户操作已取消，并询问是否需要其他帮助。不要重试该操作。",
            tenantId, userId, sessionId, conversationId, 0, false);
    }
}
