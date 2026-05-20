package org.sdkj.ai.assistant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.AiConstants;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.sdkj.ai.domain.AiChatSession;
import org.sdkj.ai.kb.Citation;
import org.sdkj.ai.kb.KbRetrievalService;
import org.sdkj.ai.kb.RetrievalResult;
import org.sdkj.ai.config.AiProperties;
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
    private final KbRetrievalService retrievalService;
    private final AiProperties aiProperties;

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
            String title = truncate(req.getMessage(), AiConstants.SESSION_TITLE_MAX_LENGTH);
            AiChatSession s = sessionService.create(tenantId, userId, title);
            sessionId = s.getId();
        }

        sessionService.appendMessage(sessionId, AiConstants.ChatRole.USER.name(), req.getMessage(), null);

        // RAG retrieval
        RetrievalResult rag = retrieveRag(tenantId, req.getMessage());
        String promptText = rag.isEmpty() ? req.getMessage() : buildRagPrompt(req.getMessage(), rag);

        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        String reply;
        try {
            reply = chatClient.prompt()
                .user(promptText)
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

        List<Citation> citations = rag.isEmpty() ? null : rag.citations();
        return new AssistantResponse(sessionId, messageId, reply, citations);
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
            String title = truncate(req.getMessage(), AiConstants.SESSION_TITLE_MAX_LENGTH);
            AiChatSession s = sessionService.create(tenantId, userId, title);
            sessionId = s.getId();
        }

        sessionService.appendMessage(sessionId, AiConstants.ChatRole.USER.name(), req.getMessage(), null);

        // RAG retrieval — done before streaming so citations are available immediately
        RetrievalResult rag = retrieveRag(tenantId, req.getMessage());
        String promptText = rag.isEmpty() ? req.getMessage() : buildRagPrompt(req.getMessage(), rag);
        List<Citation> citations = rag.isEmpty() ? null : rag.citations();

        String conversationId = ConversationIdFactory.of(tenantId, userId, sessionId);
        return streamRound(promptText, tenantId, userId, sessionId, conversationId, 0, true, citations);
    }

    /**
     * 一轮 LLM 调用 + Tool 分发。LOW Tool 全部执行成功 → 递归下一轮;MEDIUM/HIGH → 流终止 + pending 帧。
     * package-private 以便 AssistantResumeService 复用。
     */
    Flux<AssistantChunk> streamRound(String userMessage, String tenantId, Long userId,
                                      Long sessionId, String conversationId, int round,
                                      boolean enableTools, List<Citation> citations) {
        if (round >= AiConstants.MAX_TOOL_CALL_ROUNDS) {
            return Flux.just(AssistantChunk.builder().error("tool loop too deep").finish(true).build());
        }

        // Spring AI .user() 不接受空字符串,递归调用时用 toolSummary 替代
        String prompt = (userMessage == null || userMessage.isBlank()) ? "请继续。" : userMessage;

        var promptSpec = chatClient.prompt().user(prompt);

        if (enableTools) {
            Object[] toolBeans = toolRegistry.getToolBeans();
            ChatOptions toolOptions = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
            promptSpec = promptSpec.options(toolOptions).tools(toolBeans);
        }

        return promptSpec
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

                // 4) 没有 Tool 调用 → 存消息 + 结束（携带 citations）
                if (toolCalls == null || toolCalls.isEmpty()) {
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, AiConstants.ChatRole.ASSISTANT.name(), fullText, null);
                    }
                    circuitBreaker.recordSuccess(FEATURE, tenantId);
                    var messages = sessionService.listMessages(sessionId);
                    Long messageId = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();
                    return deltaFlux.concatWith(Flux.just(AssistantChunk.builder()
                        .sessionId(sessionId).messageId(messageId).citations(citations).finish(true).build()));
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
                        sessionService.appendMessage(sessionId, AiConstants.ChatRole.ASSISTANT.name(), fullText, null);
                    }
                    String toolSummary = buildToolSummary(tcr.getToolResults());
                    return deltaFlux.concatWith(
                        streamRound(toolSummary, tenantId, userId, sessionId, conversationId, round + 1, true, citations));

                } catch (PendingConfirmationException pe) {
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, AiConstants.ChatRole.ASSISTANT.name(), fullText, null);
                    }
                    var firstCall = toolCalls.get(0);
                    var md = toolRegistry.resolve(firstCall.name());
                    var view = AssistantChunk.PendingToolCallView.builder()
                        .callId(pe.getCallId())
                        .toolName(firstCall.name())
                        .risk(md == null ? RiskLevel.MEDIUM.name() : md.risk().name())
                        .arguments(parseArgs(firstCall.arguments()))
                        .description(md == null ? firstCall.name() : md.description())
                        .countdownSeconds(md != null && md.risk() == RiskLevel.HIGH ? 3 : 0)
                        .build();
                    return deltaFlux.concatWith(Flux.just(AssistantChunk.builder()
                        .sessionId(sessionId)
                        .toolCallPending(view)
                        .citations(citations)
                        .finish(true)
                        .build()));

                } catch (CriticalToolRejectedException ce) {
                    if (!fullText.isEmpty()) {
                        sessionService.appendMessage(sessionId, AiConstants.ChatRole.ASSISTANT.name(), fullText, null);
                    }
                    var view = AssistantChunk.RejectedToolView.builder()
                        .toolName(ce.getToolName())
                        .reason("CRITICAL 风险操作禁止 AI 触发")
                        .build();
                    return deltaFlux.concatWith(Flux.just(AssistantChunk.builder()
                        .sessionId(sessionId)
                        .toolCallRejected(view)
                        .citations(citations)
                        .finish(true)
                        .build()));
                }
            })
            .onErrorResume(e -> {
                if (e instanceof PendingConfirmationException || e instanceof CriticalToolRejectedException) {
                    return Flux.error(e);
                }
                circuitBreaker.recordFailure(FEATURE, tenantId);
                log.error("Stream chat failed", e);
                return Flux.just(AssistantChunk.builder()
                    .sessionId(sessionId).finish(true).error(e.getMessage()).build());
            });
    }

    // ===== RAG helpers =====

    private RetrievalResult retrieveRag(String tenantId, String message) {
        if (!aiProperties.getRag().isEnabled()) {
            return new RetrievalResult(List.of(), List.of());
        }
        try {
            return retrievalService.retrieveWithCitations(tenantId, message);
        } catch (Exception e) {
            log.warn("[Assistant] RAG retrieval failed, proceeding without context: {}", e.getMessage());
            return new RetrievalResult(List.of(), List.of());
        }
    }

    private String buildRagPrompt(String userMessage, RetrievalResult rag) {
        StringBuilder sb = new StringBuilder("### 参考资料(请优先依据以下内容回答):\n");
        for (int i = 0; i < rag.fragments().size(); i++) {
            sb.append("[").append(i + 1).append("] ").append(rag.fragments().get(i)).append("\n\n");
        }
        sb.append("### 用户问题:\n").append(userMessage);
        return sb.toString();
    }

    // ===== Utility =====

    private Map<String, Object> parseArgs(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.debug("Failed to parse tool call arguments JSON: {}", e.getMessage());
            return Map.of();
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private String buildToolSummary(List<ToolCallResult.ToolExecResult> results) {
        if (results == null || results.isEmpty()) return "请继续。";
        StringBuilder sb = new StringBuilder("以下是工具调用的结果，请基于这些结果回答用户：\n");
        for (ToolCallResult.ToolExecResult r : results) {
            sb.append("- ").append(r.getToolName()).append(": ");
            String json = r.getResultJson();
            if (json != null && json.length() > AiConstants.TOOL_SUMMARY_MAX_LENGTH) {
                json = json.substring(0, AiConstants.TOOL_SUMMARY_MAX_LENGTH) + "...(truncated)";
            }
            sb.append(json).append("\n");
        }
        return sb.toString();
    }
}
