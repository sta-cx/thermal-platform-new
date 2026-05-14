package org.sdkj.ai.kb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG retrieval advisor — searches the tenant's knowledge base and injects
 * relevant fragments into the user prompt before the call proceeds.
 * <p>
 * Must run after {@link TenantContextAdvisor} (order 0) so that
 * {@code ai.tenantId} is available in the advisor context.
 * <p>
 * Phase 2A 审查 B-C1:实现 {@link StreamAdvisor} 让 SSE 路径也走 RAG —
 * 否则 Alt+K 流式对话会绕过知识库检索。
 */
@Slf4j
@RequiredArgsConstructor
public class KbAdvisor implements CallAdvisor, StreamAdvisor {

    private final KbRetrievalService retrievalService;

    @Override
    public String getName() {
        return "KbAdvisor";
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    @NonNull
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request,
                                          @NonNull CallAdvisorChain chain) {
        ChatClientRequest rewritten = rewriteWithRag(request);
        return chain.nextCall(rewritten);
    }

    @Override
    @NonNull
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request,
                                                  @NonNull StreamAdvisorChain chain) {
        ChatClientRequest rewritten = rewriteWithRag(request);
        return chain.nextStream(rewritten);
    }

    /**
     * Core RAG logic — extract last user message, retrieve fragments, prepend as reference block.
     * Returns the original request unchanged when tenantId missing, user text empty, or no fragments.
     */
    private ChatClientRequest rewriteWithRag(ChatClientRequest request) {
        String tenantId = (String) request.context().get(TenantContextAdvisor.CTX_TENANT_ID);
        if (tenantId == null) {
            log.debug("[KbAdvisor] tenantId missing in context, RAG skipped");
            return request;
        }

        Prompt prompt = request.prompt();
        String lastUserText = prompt.getInstructions().stream()
            .filter(m -> m.getMessageType() == MessageType.USER)
            .reduce((first, second) -> second)
            .map(Message::getText)
            .orElse(null);
        if (lastUserText == null || lastUserText.isBlank()) {
            return request;
        }

        List<String> fragments;
        try {
            fragments = retrievalService.retrieve(tenantId, lastUserText);
        } catch (Exception e) {
            log.warn("[KbAdvisor] RAG retrieval failed, proceeding without context: {}", e.getMessage());
            return request;
        }
        if (fragments.isEmpty()) {
            return request;
        }

        StringBuilder ref = new StringBuilder("### 参考资料(请优先依据以下内容回答):\n");
        for (int i = 0; i < fragments.size(); i++) {
            ref.append("[").append(i + 1).append("] ").append(fragments.get(i)).append("\n\n");
        }
        ref.append("### 用户问题:\n").append(lastUserText);

        List<Message> rewritten = new ArrayList<>(prompt.getInstructions());
        for (int i = rewritten.size() - 1; i >= 0; i--) {
            if (rewritten.get(i).getMessageType() == MessageType.USER) {
                rewritten.set(i, new UserMessage(ref.toString()));
                break;
            }
        }
        Prompt newPrompt = new Prompt(rewritten, prompt.getOptions());
        return request.mutate().prompt(newPrompt).build();
    }
}
