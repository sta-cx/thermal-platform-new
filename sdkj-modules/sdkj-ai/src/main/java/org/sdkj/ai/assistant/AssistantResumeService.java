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

    public Flux<AssistantChunk> resumeAfterToolCall(String tenantId, Long userId, Long sessionId) {
        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return assistantService.streamRound("", tenantId, userId, sessionId, conversationId, 0);
    }
}
