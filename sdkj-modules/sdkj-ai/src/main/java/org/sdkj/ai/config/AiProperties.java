package org.sdkj.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 模块配置入口。
 * 对应 application-thermal.yml 中 `thermal.ai.*` 节。
 */
@Data
@ConfigurationProperties(prefix = "thermal.ai")
public class AiProperties {

    /** 模块总开关 */
    private boolean enabled = true;

    /** 默认模型 */
    private String defaultModel = "deepseek-chat";

    /** Contextual Overlay 配置(Alt+A 旁注) */
    private Contextual contextual = new Contextual();

    /** 熔断 */
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    /** 限流 */
    private RateLimit rateLimit = new RateLimit();

    /** 审计 */
    private Audit audit = new Audit();

    /** Tool Calling 配置(Phase 2B) */
    private Tools tools = new Tools();

    /** RAG 配置 */
    private Rag rag = new Rag();

    @Data
    public static class Contextual {
        private Duration cacheDefaultTtl = Duration.ofMinutes(5);
        private int maxTokens = 4000;
        private boolean fallbackToGeneric = true;
    }

    @Data
    public static class CircuitBreaker {
        private int failureThreshold = 5;
        private int windowSeconds = 60;
        private int openDurationSeconds = 60;
    }

    @Data
    public static class RateLimit {
        private int contextualPerUserPerMinute = 30;
        private int contextualPerTenantPerMinute = 200;
    }

    @Data
    public static class Audit {
        private boolean logContext = true;
        private boolean logOutput = true;
        private List<String> piiMaskPatterns = List.of(
            "(?<!\\d)\\d{17}[\\dXx](?!\\d)",
            "(?<!\\d)1[3-9]\\d{9}(?!\\d)"
        );
    }

    @Data
    public static class Rag {
        /** 是否启用 RAG 知识库检索 */
        private boolean enabled = true;
    }

    @Data
    public static class Tools {
        /** Tool Bean 黑名单,如 ["valveControlTool"];运维侧禁用 */
        private List<String> disabled = new ArrayList<>();

        /** 每用户每分钟最多写 Tool 调用数,默认 10 */
        private int writeRateLimitPerMinute = 10;

        /** 阀门 Tool dryRun 默认值(IoT 未就绪期建议 true) */
        private Valve valve = new Valve();

        @Data
        public static class Valve {
            private boolean dryRun = true;
        }
    }
}
