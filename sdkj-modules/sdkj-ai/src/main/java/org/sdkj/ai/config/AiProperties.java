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

    /** KB 知识库参数 */
    private Kb kb = new Kb();

    /** Phase3 上下文感知配置 */
    private Context context = new Context();

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

        private Hybrid hybrid = new Hybrid();
        private Rerank rerank = new Rerank();
        private Upload upload = new Upload();

        @Data
        public static class Hybrid {
            private boolean enabled = true;
            private int densePrefetchLimit = 20;
            private int sparsePrefetchLimit = 20;
            private int fusionLimit = 10;
        }

        @Data
        public static class Rerank {
            private boolean enabled = true;
            private String model = "jina-reranker-v2-base-multilingual";
            private String baseUrl = "https://api.jina.ai";
            private String apiKey;
            private int timeoutMs = 5000;
            private double scoreThreshold = 0.3;
            private boolean fallbackOnError = true;
        }

        @Data
        public static class Upload {
            private int maxFilesPerBatch = 50;
            private int maxSizeMb = 200;
            private int jinaEmbedBatchSize = 128;
            private List<String> allowedExtensions = List.of(
                ".txt", ".md", ".markdown", ".text", ".rtf",
                ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx",
                ".odt", ".ods", ".odp",
                ".pdf", ".html", ".htm", ".xml",
                ".csv", ".tsv", ".json",
                ".eml", ".msg"
            );
        }
    }

    /** Embedding 配置（含 Jina v3 task 参数） */
    private Embedding embedding = new Embedding();

    @Data
    public static class Embedding {
        private String baseUrl = "https://api.jina.ai";
        private String apiKey;
        private String model = "jina-embeddings-v3";
        private EmbeddingTask task = new EmbeddingTask();

        @Data
        public static class EmbeddingTask {
            private String query = "retrieval.query";
            private String passage = "retrieval.passage";
        }
    }

    @Data
    public static class Kb {
        private int chunkSizeTokens = 500;
        private int chunkOverlapTokens = 50;
        private int minChunkChars = 150;
        private long maxDocBytes = 5242880;
        private int maxChunksPerDoc = 2000;
        private Retrieval retrieval = new Retrieval();

        @Data
        public static class Retrieval {
            private int topK = 4;
            private double scoreThreshold = 0.5;
        }
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

    /** B：推荐规则 */
    @Data
    public static class SuggestionRule {
        /** 断言的 fact 字段名（如 valveStatus）。 */
        private String field;
        /** 操作符：eq / in / lt / gt。 */
        private String op;
        /** 比较值；op=in 时逗号分隔多值。 */
        private String value;
        /** 按钮文案。 */
        private String label;
        /** 点击后填入输入框的话术，支持 {field} 占位（从 fact.keys / focus.attrs 取）。 */
        private String promptTemplate;
    }

    /** Phase3 上下文感知配置 */
    @Data
    public static class Context {
        private boolean enabled = true;            // 总开关，false 完全回落 Phase 2B
        private boolean argFill = true;            // A：参数补全
        private boolean suggestion = true;         // B（Plan 2 用）
        private boolean orchestration = true;      // C（Plan 3 用）
        private int maxFacts = 20;
        private Duration factTtl = Duration.ofMinutes(30);
        private int focusIdleMinutes = 10;
        private int maxOrchestrationSteps = 5;     // C（Plan 3 用）
        private boolean injectPageContext = true;
        /** 业务主键名：新增主键加一行即可（A 补全只认这些参数名）。 */
        private java.util.List<String> entityKeys = java.util.List.of(
            "houseId", "valveId", "stationId", "expenseId", "repairId", "meterNum");
        /** focus 选取优先级（entityType；对应主键 = type+"Id"）。 */
        private java.util.List<String> focusPriority = java.util.List.of("house", "station", "valve");
        /** C：阀门"关闭"判定值集合（查 tenant 库后填真实值；见 spec §6.4）。B 推荐也复用。 */
        private java.util.List<String> closedValues = java.util.List.of("2", "CLOSE", "CLOSED");
        /** C：阀门"故障"判定值集合。 */
        private java.util.List<String> errorValues = java.util.List.of("3", "ERROR");
        /** B：推荐规则（外置，加 Tool 加规则不改引擎）。 */
        private java.util.List<SuggestionRule> suggestionRules = new java.util.ArrayList<>();
    }
}
