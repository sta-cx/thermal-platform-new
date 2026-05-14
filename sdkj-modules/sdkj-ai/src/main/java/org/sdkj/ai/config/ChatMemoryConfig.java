package org.sdkj.ai.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ChatMemory 配置 — 强制走 master 库,避免被 TenantFilter 推到租户库。
 * <p>
 * 背景(Phase 2A 审查 B-C2):Spring AI 默认装配的 {@link JdbcChatMemoryRepository}
 * 使用容器中的 primary {@code DataSource}。本项目的 primary 是 dynamic-datasource 的
 * {@code DynamicRoutingDataSource},运行时由 {@code TenantFilter} 推 {@code tenant_{code}},
 * 导致 ChatMemory 读写落到租户库(那里没有 {@code spring_ai_chat_memory} 表)。
 * <p>
 * 解决:重新装配 {@link JdbcChatMemoryRepository},直接传入按 master 的 {@link JdbcTemplate},
 * 绕过 dynamic 路由。Spring AI 用 {@code @ConditionalOnMissingBean} 自动放弃默认实例。
 */
@Configuration
public class ChatMemoryConfig {

    /**
     * 取 dynamic-datasource 内的 master 子数据源,固化到一个 master 专用 JdbcTemplate。
     */
    @Bean("aiChatMemoryJdbcTemplate")
    public JdbcTemplate aiChatMemoryJdbcTemplate(DataSource dataSource) {
        DataSource master = dataSource;
        if (dataSource instanceof DynamicRoutingDataSource ds) {
            DataSource resolved = ds.getDataSources().get("master");
            if (resolved == null) {
                throw new IllegalStateException(
                    "master DataSource not found in DynamicRoutingDataSource — required for AI ChatMemory");
            }
            master = resolved;
        }
        return new JdbcTemplate(master);
    }

    /**
     * 覆盖 Spring AI 自动装配的 JdbcChatMemoryRepository。
     * 必须 {@code @Primary} 才能压过自动配置(后者也会创建一个 bean,然后我们这个抢成 primary)。
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "chatMemoryRepository")
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate aiChatMemoryJdbcTemplate) {
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(aiChatMemoryJdbcTemplate)
            .build();
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(repository)
            .maxMessages(20)
            .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }
}
