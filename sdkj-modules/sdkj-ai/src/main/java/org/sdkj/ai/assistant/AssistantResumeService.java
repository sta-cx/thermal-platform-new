package org.sdkj.ai.assistant;

import org.sdkj.ai.AiConstants;
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
            sessionService.appendMessage(sessionId, AiConstants.ChatRole.SYSTEM.name(),
                AiConstants.MSG_TOOL_COMPLETED_PREFIX + toolResultJson, null);
        }
        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return assistantService.streamRound(
            AiConstants.PROMPT_RESUME_AFTER_TOOL, tenantId, userId, sessionId, conversationId, 0,
            false, null);
    }

    /**
     * 用户拒绝 Tool 调用后，拉起第二阶段 SSE 让 LLM 告知用户操作已取消。
     */
    public Flux<AssistantChunk> resumeAfterRejection(
        String tenantId, Long userId, Long sessionId, String toolName) {
        sessionService.appendMessage(sessionId, AiConstants.ChatRole.SYSTEM.name(),
            AiConstants.MSG_TOOL_REJECTED_PREFIX + toolName + " 的执行。", null);
        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return assistantService.streamRound(
            AiConstants.PROMPT_RESUME_AFTER_REJECTION,
            tenantId, userId, sessionId, conversationId, 0, false, null);
    }
}
