package org.sdkj.ai.config;

import org.sdkj.ai.advisor.SafetyAuditAdvisor;
import org.sdkj.ai.advisor.TenantContextAdvisor;
import org.sdkj.ai.advisor.UsageMetricsAdvisor;
import org.sdkj.ai.core.ContextualPromptRegistry;
import org.sdkj.ai.mapper.AiCallRecordMapper;
import org.sdkj.ai.mapper.AiUsageLogMapper;
import org.sdkj.ai.safety.PiiMasker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
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

    @Bean("assistantChatClient")
    public ChatClient assistantChatClient(ChatClient.Builder builder,
                                           TenantContextAdvisor tenantAdvisor,
                                           SafetyAuditAdvisor auditAdvisor,
                                           MessageChatMemoryAdvisor memoryAdvisor,
                                           UsageMetricsAdvisor usageAdvisor) {
        return builder
            .defaultSystem("""
                你是 SDKJ 智慧供热平台的助手。回答与执行操作时遵循以下规则:

                【信息来源】
                1. 优先使用"参考资料"中的事实;参考资料里没有的内容不要编造
                2. 涉及数据库实际数据时,必须调用提供的 readonly Tool 查询,不要靠记忆回答

                【写操作规则(创建/修改/下发指令)】
                3. 调用写 Tool 前,先确保所有 ID 类参数都已通过 readonly Tool 确认;
                   不要凭对话上下文猜测 ID
                4. 若关键参数无法确认(如用户只说了"3号楼"但拿不到 buildingId),
                   告诉用户:"无法定位到具体房屋,请到业务页面手动操作,
                   或提供更精确的信息(如完整地址、热户编号)"
                5. 写 Tool 返回错误时,把错误消息直接转述给用户,不要重试同样的参数

                【行为约束】
                6. 不回答与供热平台无关的话题
                7. 一次回答中最多调用一个写 Tool;多个写操作分轮次执行,每次都让用户确认
                8. 对敏感写操作(阀门控制、单笔退费),即使用户表达了"全部""所有""批量"等意图,
                   也仅对已明确指定的目标执行一次,不要扩大范围
                """)
            .defaultAdvisors(
                tenantAdvisor,    // 1. 注入 tenantId/userId
                memoryAdvisor,    // 2. 多轮上下文加载
                auditAdvisor,     // 3. PII 脱敏 + 落审计
                usageAdvisor      // 4. token 用量统计
            )
            .build();
    }
}
