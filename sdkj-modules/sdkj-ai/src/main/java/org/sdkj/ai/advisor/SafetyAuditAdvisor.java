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
import org.springframework.lang.NonNull;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class SafetyAuditAdvisor implements CallAdvisor {

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
            persistAuditRecord(request, response);
        } catch (Exception e) {
            log.warn("Failed to persist AI audit record", e);
        }
        return response;
    }

    private void persistAuditRecord(ChatClientRequest request, ChatClientResponse response) {
        var ctx = request.context();
        String tenantId = (String) ctx.get(TenantContextAdvisor.CTX_TENANT_ID);
        Long userId = (Long) ctx.get(TenantContextAdvisor.CTX_USER_ID);

        String contextSummary = truncate(piiMasker.mask(request.prompt().getContents()), 2000);
        String outputSummary = response.chatResponse() == null ? null
            : truncate(piiMasker.mask(response.chatResponse().getResult().getOutput().getText()), 2000);

        AiCallRecord rec = new AiCallRecord();
        rec.setTenantId(tenantId);
        rec.setUserId(userId);
        rec.setFeature("contextual");
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
