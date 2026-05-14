package org.sdkj.ai.advisor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiCallRecord;
import org.sdkj.ai.mapper.AiCallRecordMapper;
import org.sdkj.ai.safety.PiiMasker;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 安全 + 审计 advisor — PII 脱敏 + 落 ai_call_record。
 * <p>
 * Phase 2A 审查 B-C1:同时实现 {@link StreamAdvisor},让 SSE 流式响应也累积输出 → 写审计。
 * 否则 Alt+K 主路径裸奔,PII 未脱敏直接外泄到 LLM,审计也丢失。
 */
@Slf4j
@RequiredArgsConstructor
public class SafetyAuditAdvisor implements CallAdvisor, StreamAdvisor {

    private final PiiMasker piiMasker;
    private final AiCallRecordMapper callRecordMapper;

    @Override
    public String getName() {
        return "SafetyAuditAdvisor";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    @NonNull
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request,
                                          @NonNull CallAdvisorChain chain) {
        ChatClientResponse response = chain.nextCall(request);
        try {
            String outputText = extractOutputText(response);
            persistAuditRecord(request, outputText);
        } catch (Exception e) {
            log.warn("Failed to persist AI audit record", e);
        }
        return response;
    }

    @Override
    @NonNull
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request,
                                                  @NonNull StreamAdvisorChain chain) {
        StringBuilder accumulated = new StringBuilder();
        return chain.nextStream(request)
            .doOnNext(resp -> {
                String chunk = extractOutputText(resp);
                if (chunk != null && !chunk.isEmpty()) {
                    accumulated.append(chunk);
                }
            })
            .doFinally(signal -> {
                try {
                    persistAuditRecord(request, accumulated.toString());
                } catch (Exception e) {
                    log.warn("Failed to persist AI audit record (stream)", e);
                }
            });
    }

    private String extractOutputText(ChatClientResponse resp) {
        if (resp == null || resp.chatResponse() == null
            || resp.chatResponse().getResult() == null
            || resp.chatResponse().getResult().getOutput() == null) {
            return null;
        }
        return resp.chatResponse().getResult().getOutput().getText();
    }

    private void persistAuditRecord(ChatClientRequest request, String outputText) {
        var ctx = request.context();
        String tenantId = (String) ctx.get(TenantContextAdvisor.CTX_TENANT_ID);
        Long userId = (Long) ctx.get(TenantContextAdvisor.CTX_USER_ID);

        // 只 audit USER 消息文本。SYSTEM prompt 是静态的,不写到 audit 让 2000 字预算全部留给用户上下文。
        String userText = request.prompt().getInstructions().stream()
            .filter(m -> m.getMessageType() == MessageType.USER)
            .map(Message::getText)
            .collect(Collectors.joining("\n"));
        if (userText.isEmpty()) {
            userText = request.prompt().getContents();
        }
        String contextSummary = truncate(piiMasker.mask(userText), 2000);
        String outputSummary = outputText == null || outputText.isEmpty()
            ? null : truncate(piiMasker.mask(outputText), 2000);

        AiCallRecord rec = new AiCallRecord();
        rec.setTenantId(tenantId);
        rec.setUserId(userId);
        rec.setFeature((String) ctx.getOrDefault("ai.feature", "contextual"));
        rec.setContextSummary(contextSummary);
        rec.setOutputSummary(outputSummary);
        rec.setCreatedAt(new Date());
        callRecordMapper.insert(rec);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
