package org.sdkj.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
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
}
