package org.sdkj.ai.assistant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.sdkj.ai.domain.AiChatSession;
import org.sdkj.ai.safety.AiCircuitBreaker;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.dispatcher.*;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    @Resource(name = "assistantChatClient")
    private ChatClient chatClient;

    private final SessionService sessionService;
    private final AiCircuitBreaker circuitBreaker;
    private final ToolRegistry toolRegistry;
    private final ToolCallDispatcher dispatcher;
    private final ObjectMapper objectMapper;

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
                    .param("ai.feature", FEATURE)
                    .param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
        } catch (Exception e) {
            circuitBreaker.recordFailure(FEATURE, tenantId);
            throw new RuntimeException("AI 助手调用失败", e);
        }

        sessionService.appendMessage(sessionId, "ASSISTANT", reply, null);
        circuitBreaker.recordSuccess(FEATURE, tenantId);

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

        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return streamRound(req.getMessage(), tenantId, userId, sessionId, conversationId, 0);
    }

    /**
     * 一轮 LLM 调用 + Tool 分发。LOW Tool 全部执行成功 → 递归下一轮;MEDIUM/HIGH → 流终止 + pending 帧。
     * package-private 以便 AssistantResumeService 复用。
     */
    Flux<AssistantChunk> streamRound(String userMessage, String tenantId, Long userId,
                                      Long sessionId, String conversationId, int round) {
        if (round >= 5) {
            return Flux.just(AssistantChunk.builder().error("tool loop too deep").finish(true).build());
        }

        // Tool callbacks 从 ToolRegistry 动态获取(避免 Bean 创建时序问题)
        Object[] toolBeans = toolRegistry.getToolBeans();

        ChatOptions toolOptions = ToolCallingChatOptions.builder()
            .internalToolExecutionEnabled(false)
            .build();

        return chatClient.prompt()
            .user(userMessage)
            .options(toolOptions)
            .tools(toolBeans)
            .advisors(spec -> spec
                .param(TenantContextAdvisor.CTX_TENANT_ID, tenantId)
                .param(TenantContextAdvisor.CTX_USER_ID, userId)
                .param("ai.feature", FEATURE)
                .param(ChatMemory.CONVERSATION_ID, conversationId)
            )
            .stream()
            .chatResponse()
            .collectList()
            .flatMapMany(responses -> {
                // 1) 合并文本 delta
                String fullText = responses.stream()
                    .map(r -> r.getResult() != null && r.getResult().getOutput() != null
                        ? r.getResult().getOutput().getText() : "")
                    .reduce("", String::concat);

                // 2) 提取最后一个 response 的 ToolCalls
                var last = responses.get(responses.size() - 1);
                var toolCalls = (last.getResult() != null && last.getResult().getOutput() != null)
                    ? last.getResult().getOutput().getToolCalls() : null;

                // 3) 生成 delta 帧
                Flux<AssistantChunk> deltaFlux = fullText.isEmpty()
                    ? Flux.empty()
                    : Flux.just(AssistantChunk.builder()
                        .delta(fullText).sessionId(sessionId).finish(false).build());

                // 4) 没有 Tool 调用 → 存消息 + 结束
                if (toolCalls == null || toolCalls.isEmpty()) {
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, "ASSISTANT", fullText, null);
                    }
                    circuitBreaker.recordSuccess(FEATURE, tenantId);
                    var messages = sessionService.listMessages(sessionId);
                    Long messageId = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();
                    return deltaFlux.concatWith(Flux.just(AssistantChunk.builder()
                        .sessionId(sessionId).messageId(messageId).finish(true).build()));
                }

                // 5) 构建 ToolCallRequest 列表
                List<ToolCallDispatcher.ToolCallRequest> reqs = toolCalls.stream()
                    .map(tc -> new ToolCallDispatcher.ToolCallRequest(
                        tc.name(),
                        parseArgs(tc.arguments()),
                        tenantId,
                        userId,
                        sessionId,
                        null
                    )).toList();

                // 6) Dispatcher 分流
                try {
                    ToolCallResult tcr = dispatcher.dispatch(reqs);
                    // 全 LOW:存储 assistant 文本 + 递归下一轮
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, "ASSISTANT", fullText, null);
                    }
                    return deltaFlux.concatWith(
                        streamRound("", tenantId, userId, sessionId, conversationId, round + 1));

                } catch (PendingConfirmationException pe) {
                    // 暂停 — 存已有文本 + 发 pending 帧 + 结束流
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, "ASSISTANT", fullText, null);
                    }
                    var firstCall = toolCalls.get(0);
                    var md = toolRegistry.byName(firstCall.name());
                    var view = AssistantChunk.PendingToolCallView.builder()
                        .callId(pe.getCallId())
                        .toolName(firstCall.name())
                        .risk(md == null ? "MEDIUM" : md.risk().name())
                        .arguments(parseArgs(firstCall.arguments()))
                        .description(md == null ? firstCall.name() : md.description())
                        .countdownSeconds(md != null && md.risk() == RiskLevel.HIGH ? 3 : 0)
                        .build();
                    return deltaFlux.concatWith(Flux.just(AssistantChunk.builder()
                        .sessionId(sessionId)
                        .toolCallPending(view)
                        .finish(true)
                        .build()));

                } catch (CriticalToolRejectedException ce) {
                    // CRITICAL 拒绝
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, "ASSISTANT", fullText, null);
                    }
                    var view = AssistantChunk.RejectedToolView.builder()
                        .toolName(ce.getToolName())
                        .reason("CRITICAL 风险操作禁止 AI 触发")
                        .build();
                    return deltaFlux.concatWith(Flux.just(AssistantChunk.builder()
                        .sessionId(sessionId)
                        .toolCallRejected(view)
                        .finish(true)
                        .build()));
                }
            })
            .onErrorResume(e -> {
                if (e instanceof PendingConfirmationException || e instanceof CriticalToolRejectedException) {
                    return Flux.error(e); // 让控制流异常透传
                }
                circuitBreaker.recordFailure(FEATURE, tenantId);
                log.error("Stream chat failed", e);
                return Flux.just(AssistantChunk.builder()
                    .sessionId(sessionId).finish(true).error(e.getMessage()).build());
            });
    }

    private Map<String, Object> parseArgs(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
