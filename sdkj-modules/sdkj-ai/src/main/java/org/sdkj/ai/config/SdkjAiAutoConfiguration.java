package org.sdkj.ai.config;

import org.sdkj.ai.advisor.SafetyAuditAdvisor;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.sdkj.ai.advisor.UsageMetricsAdvisor;
import org.sdkj.ai.core.ContextualPromptRegistry;
import org.sdkj.ai.mapper.AiCallRecordMapper;
import org.sdkj.ai.mapper.AiUsageLogMapper;
import org.sdkj.ai.safety.PiiMasker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

@AutoConfiguration
@ComponentScan("org.sdkj.ai")
@MapperScan("org.sdkj.ai.mapper")
@EnableConfigurationProperties(AiProperties.class)
@ConditionalOnProperty(prefix = "thermal.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SdkjAiAutoConfiguration {

    @Bean
    public ContextualPromptRegistry contextualPromptRegistry() {
        return new ContextualPromptRegistry();
    }

    @Bean
    public TenantContextAdvisor tenantContextAdvisor() {
        return new TenantContextAdvisor();
    }

    @Bean
    public SafetyAuditAdvisor safetyAuditAdvisor(PiiMasker piiMasker,
                                                  AiCallRecordMapper callRecordMapper) {
        return new SafetyAuditAdvisor(piiMasker, callRecordMapper);
    }

    @Bean
    public UsageMetricsAdvisor usageMetricsAdvisor(AiUsageLogMapper usageLogMapper) {
        return new UsageMetricsAdvisor(usageLogMapper);
    }

    @Bean("contextualChatClient")
    public ChatClient contextualChatClient(ChatClient.Builder builder,
                                            TenantContextAdvisor tenantAdvisor,
                                            SafetyAuditAdvisor auditAdvisor,
                                            UsageMetricsAdvisor usageAdvisor) {
        return builder
            .defaultSystem("""
                你是 SDKJ 智慧供热平台的 AI 旁注助手。
                你的任务:基于当前页面的数据快照,产出简洁、基于事实、不编造的解读。
                输出必须严格遵循指定的 JSON Schema,不要添加额外字段或自然语言前后缀。
                """)
            .defaultAdvisors(tenantAdvisor, auditAdvisor, usageAdvisor)
            .build();
    }
}
