package org.sdkj.ai.assistant;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.sdkj.ai.domain.AiChatSession;
import org.sdkj.ai.safety.AiCircuitBreaker;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    @Resource(name = "assistantChatClient")
    private ChatClient chatClient;

    private final SessionService sessionService;
    private final AiCircuitBreaker circuitBreaker;

    private static final String FEATURE = "assistant";

    public AssistantResponse chat(AssistantRequest req) {
        String tenantId = LoginHelper.getTenantId();
        Long userId = LoginHelper.getUserId();

        circuitBreaker.checkAllowed(FEATURE, tenantId);

        Long sessionId;
        if (req.getSessionId() != null) {
            sessionService.requireOwned(req.getSessionId(), tenantId, userId);
            sessionId = req.getSessionId();
        } else {
            String title = truncate(req.getMessage(), 30);
            AiChatSession s = sessionService.create(tenantId, userId, title);
            sessionId = s.getId();
        }

        sessionService.appendMessage(sessionId, "USER", req.getMessage(), null);

        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        String reply;
        try {
            reply = chatClient.prompt()
                .user(req.getMessage())
                .advisors(spec -> spec
                    .param(TenantContextAdvisor.CTX_TENANT_ID, tenantId)
                    .param(TenantContextAdvisor.CTX_USER_ID, userId)
                    .param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
        } catch (Exception e) {
            circuitBreaker.recordFailure(FEATURE, tenantId);
            throw new RuntimeException("AI 助手调用失败", e);
        }

        sessionService.appendMessage(sessionId, "ASSISTANT", reply, null);

        List<org.sdkj.ai.domain.AiChatMessage> messages = sessionService.listMessages(sessionId);
        Long messageId = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();

        return new AssistantResponse(sessionId, messageId, reply);
    }

    public Flux<AssistantChunk> streamChat(AssistantRequest req) {
        String tenantId = LoginHelper.getTenantId();
        Long userId = LoginHelper.getUserId();

        circuitBreaker.checkAllowed(FEATURE, tenantId);

        Long sessionId;
        if (req.getSessionId() != null) {
            sessionService.requireOwned(req.getSessionId(), tenantId, userId);
            sessionId = req.getSessionId();
        } else {
            String title = truncate(req.getMessage(), 30);
            AiChatSession s = sessionService.create(tenantId, userId, title);
            sessionId = s.getId();
        }

        sessionService.appendMessage(sessionId, "USER", req.getMessage(), null);
        final Long finalSessionId = sessionId;

        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        StringBuilder fullReply = new StringBuilder();

        return chatClient.prompt()
            .user(req.getMessage())
            .advisors(spec -> spec
                .param(TenantContextAdvisor.CTX_TENANT_ID, tenantId)
                .param(TenantContextAdvisor.CTX_USER_ID, userId)
                .param(ChatMemory.CONVERSATION_ID, conversationId)
            )
            .stream()
            .content()
            .map(delta -> {
                fullReply.append(delta);
                return new AssistantChunk(delta, finalSessionId, null, false, null);
            })
            .concatWith(Flux.defer(() -> {
                sessionService.appendMessage(finalSessionId, "ASSISTANT", fullReply.toString(), null);
                var messages = sessionService.listMessages(finalSessionId);
                Long messageId = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();
                return Flux.just(new AssistantChunk(null, finalSessionId, messageId, true, null));
            }))
            .onErrorResume(e -> {
                circuitBreaker.recordFailure(FEATURE, tenantId);
                log.error("Stream chat failed", e);
                return Flux.just(new AssistantChunk(null, finalSessionId, null, true, e.getMessage()));
            });
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
