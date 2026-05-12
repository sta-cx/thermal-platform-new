package org.sdkj.ai.advisor;

import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.lang.NonNull;

public class TenantContextAdvisor implements CallAdvisor {

    public static final String CTX_TENANT_ID = "ai.tenantId";
    public static final String CTX_USER_ID = "ai.userId";
    public static final String CTX_USER_NAME = "ai.userName";

    @Override
    public String getName() {
        return "TenantContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    @NonNull
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request,
                                          @NonNull CallAdvisorChain chain) {
        ChatClientRequest enriched = request.mutate()
            .context(CTX_TENANT_ID, LoginHelper.getTenantId())
            .context(CTX_USER_ID, LoginHelper.getUserId())
            .context(CTX_USER_NAME, LoginHelper.getUsername())
            .build();
        return chain.nextCall(enriched);
    }
}
