package org.sdkj.ai;

public final class AiConstants {

    private AiConstants() {}

    // ===== Data Source =====
    public static final String DS_MASTER = "master";
    public static final String DS_TENANT_PREFIX = "tenant_";

    // ===== Feature Names =====
    public static final String FEATURE_CONTEXTUAL = "contextual";

    // ===== SSE Timeouts =====
    public static final long SSE_TIMEOUT_MS = 60_000L;
    public static final long SSE_LONG_TIMEOUT_MS = 300_000L;

    // ===== Chat Roles =====
    public enum ChatRole {
        USER, ASSISTANT, SYSTEM, TOOL;
        @Override public String toString() { return name(); }
    }

    // ===== Tool Execution Status =====
    public enum ToolExecStatus {
        SUCCESS, FAILED, DRY_RUN;
        @Override public String toString() { return name(); }
    }

    // ===== Thresholds =====
    public static final int MAX_TOOL_CALL_ROUNDS = 5;
    public static final int SESSION_TITLE_MAX_LENGTH = 30;
    public static final int TOOL_SUMMARY_MAX_LENGTH = 800;
    public static final int ERROR_MESSAGE_MAX_LENGTH = 500;
    public static final int MAX_SESSIONS_PER_USER = 50;
    public static final int MAX_CHAT_MEMORY_MESSAGES = 20;

    // ===== Prompt Templates =====
    public static final String PROMPT_RESUME_AFTER_TOOL = "请基于刚才工具执行的结果继续回复。";
    public static final String PROMPT_RESUME_AFTER_REJECTION =
        "用户刚才拒绝了你请求执行的 Tool 操作，请告知用户操作已取消，并询问是否需要其他帮助。不要重试该操作。";

    // ===== Internal System Message Prefixes =====
    public static final String MSG_TOOL_COMPLETED_PREFIX = "[Tool 执行完成] 结果: ";
    public static final String MSG_TOOL_REJECTED_PREFIX = "[Tool 调用被拒绝] 用户拒绝了 ";

    // ===== Pending Tool Call TTL =====
    public static final long PENDING_TTL_SECONDS = 1800L;

    // ===== Redis Lock =====
    public static final long LOCK_WAIT_SECONDS = 2L;
    public static final long LOCK_LEASE_SECONDS = 10L;
}
