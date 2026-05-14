package org.sdkj.ai.kb;

import lombok.RequiredArgsConstructor;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG retrieval advisor — searches the tenant's knowledge base and injects
 * relevant fragments into the user prompt before the call proceeds.
 * <p>
 * Must run after {@link TenantContextAdvisor} (order 0) so that
 * {@code ai.tenantId} is available in the advisor context.
 */
@RequiredArgsConstructor
public class KbAdvisor implements CallAdvisor {

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
        // 1. Read tenantId from context (set by TenantContextAdvisor)
        String tenantId = (String) request.context().get(TenantContextAdvisor.CTX_TENANT_ID);
        if (tenantId == null) {
            return chain.nextCall(request);
        }

        // 2. Extract last user message text
        Prompt prompt = request.prompt();
        String lastUserText = prompt.getInstructions().stream()
            .filter(m -> m.getMessageType() == MessageType.USER)
            .reduce((first, second) -> second)
            .map(Message::getText)
            .orElse(null);
        if (lastUserText == null || lastUserText.isBlank()) {
            return chain.nextCall(request);
        }

        // 3. Retrieve relevant fragments
        List<String> fragments = retrievalService.retrieve(tenantId, lastUserText);
        if (fragments.isEmpty()) {
            return chain.nextCall(request);
        }

        // 4. Rewrite user message with reference material prefix
        StringBuilder ref = new StringBuilder("### 参考资料(请优先依据以下内容回答):\n");
        for (int i = 0; i < fragments.size(); i++) {
            ref.append("[").append(i + 1).append("] ").append(fragments.get(i)).append("\n\n");
        }
        ref.append("### 用户问题:\n").append(lastUserText);

        // 5. Replace last user message and forward
        List<Message> rewritten = new ArrayList<>(prompt.getInstructions());
        for (int i = rewritten.size() - 1; i >= 0; i--) {
            if (rewritten.get(i).getMessageType() == MessageType.USER) {
                rewritten.set(i, new UserMessage(ref.toString()));
                break;
            }
        }
        Prompt newPrompt = new Prompt(rewritten, prompt.getOptions());
        ChatClientRequest newRequest = request.mutate().prompt(newPrompt).build();
        return chain.nextCall(newRequest);
    }
}
