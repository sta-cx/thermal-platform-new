package org.sdkj.ai.config;

import org.sdkj.ai.advisor.SafetyAuditAdvisor;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.sdkj.ai.advisor.UsageMetricsAdvisor;
import org.sdkj.ai.core.ContextualPromptRegistry;
import org.sdkj.ai.kb.KbAdvisor;
import org.sdkj.ai.kb.KbRetrievalService;
import org.sdkj.ai.mapper.AiCallRecordMapper;
import org.sdkj.ai.mapper.AiUsageLogMapper;
import org.sdkj.ai.safety.PiiMasker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.mybatis.spring.annotation.MapperScan;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;

@AutoConfiguration
@ComponentScan("org.sdkj.ai")
@MapperScan("org.sdkj.ai.mapper")
@EnableConfigurationProperties(AiProperties.class)
@EnableAsync
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
    public UsageMetricsAdvisor usageMetricsAdvisor(AiUsageLogMapper usageLogMapper,
                                                    MeterRegistry meterRegistry) {
        return new UsageMetricsAdvisor(usageLogMapper, meterRegistry);
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

    @Bean
    @ConditionalOnProperty(prefix = "thermal.ai.rag", name = "enabled", havingValue = "true", matchIfMissing = true)
    public KbAdvisor kbAdvisor(KbRetrievalService retrievalService) {
        return new KbAdvisor(retrievalService);
    }

    @Bean("assistantChatClient")
    public ChatClient assistantChatClient(ChatClient.Builder builder,
                                           TenantContextAdvisor tenantAdvisor,
                                           @Autowired(required = false) KbAdvisor kbAdvisor,
                                           SafetyAuditAdvisor auditAdvisor,
                                           MessageChatMemoryAdvisor memoryAdvisor,
                                           UsageMetricsAdvisor usageAdvisor) {
        return builder
            .defaultSystem("""
                你是 SDKJ 智慧供热平台的助手。回答问题时:
                1. 如果有"参考资料",优先依据这些资料回答;参考资料里没有的事实不要编造
                2. 答案简明,中文回答
                3. 涉及数据库实际数据时,可以调用提供的 Tool 查询(只读)
                4. 不回答与供热平台无关的话题
                """)
            .defaultAdvisors(
                tenantAdvisor,    // 1. 注入 tenantId/userId
                memoryAdvisor,    // 2. 多轮上下文加载
                auditAdvisor,     // 3. PII 脱敏 + 落审计
                usageAdvisor      // 4. token 用量统计
            )
            .defaultAdvisors(kbAdvisor != null
                ? List.of(kbAdvisor) : List.of())
            .build();
    }
}
