-- =====================================================
-- AI Phase 1 schema — 在 master 库 ry-vue 中执行
-- 2026-05-11
-- =====================================================

USE `ry-vue`;

-- 1. AI 用量日志(每次调用一条)
CREATE TABLE IF NOT EXISTS `ai_usage_log` (
    `id`                BIGINT       NOT NULL COMMENT '主键(雪花)',
    `tenant_id`         VARCHAR(6)   NOT NULL COMMENT '租户ID',
    `user_id`           BIGINT       NOT NULL COMMENT '用户ID',
    `feature`           VARCHAR(50)  NOT NULL COMMENT 'contextual|assistant|tool|rag',
    `route`             VARCHAR(200) NULL     COMMENT '触发路由(旁注)',
    `conversation_id`   VARCHAR(100) NULL     COMMENT '会话ID(助手)',
    `prompt_name`       VARCHAR(100) NULL     COMMENT '匹配到的 ContextualPrompt 名称',
    `tool_name`         VARCHAR(100) NULL     COMMENT '调用的 Tool 名称',
    `model`             VARCHAR(50)  NOT NULL COMMENT 'LLM 模型名',
    `prompt_tokens`     INT          NOT NULL DEFAULT 0,
    `completion_tokens` INT          NOT NULL DEFAULT 0,
    `cost_cents`        INT          NOT NULL DEFAULT 0 COMMENT '成本(分)',
    `cache_hit`         TINYINT      NOT NULL DEFAULT 0,
    `success`           TINYINT      NOT NULL DEFAULT 1,
    `error_msg`         VARCHAR(500) NULL,
    `duration_ms`       BIGINT       NOT NULL,
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_tenant_feature_time` (`tenant_id`, `feature`, `created_at`),
    KEY `idx_user_time`           (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 用量日志';

-- 2. AI 调用记录(含脱敏后的上下文 & 输出摘要,用于审计与排障)
CREATE TABLE IF NOT EXISTS `ai_call_record` (
    `id`               BIGINT        NOT NULL COMMENT '主键(雪花)',
    `tenant_id`        VARCHAR(6)    NOT NULL,
    `user_id`          BIGINT        NOT NULL,
    `feature`          VARCHAR(50)   NOT NULL,
    `conversation_id`  VARCHAR(100)  NULL,
    `context_summary`  VARCHAR(2000) NULL COMMENT '上下文摘要(脱敏后,2K 截断)',
    `output_summary`   VARCHAR(2000) NULL COMMENT '输出摘要(脱敏后,2K 截断)',
    `tools_invoked`    VARCHAR(500)  NULL COMMENT 'tool1,tool2,...',
    `created_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_tenant_user` (`tenant_id`, `user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 调用记录';

-- 3. 待确认 Tool Call(Phase 3 才用,Phase 1 先建好表避免后续迁移)
CREATE TABLE IF NOT EXISTS `ai_pending_tool_call` (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花)',
    `tool_call_id`    VARCHAR(64)  NOT NULL COMMENT 'LLM 返回的 ToolCall ID',
    `tenant_id`       VARCHAR(6)   NOT NULL,
    `user_id`         BIGINT       NOT NULL,
    `conversation_id` VARCHAR(100) NOT NULL,
    `tool_name`       VARCHAR(100) NOT NULL,
    `arguments_json`  TEXT         NOT NULL,
    `risk_level`      VARCHAR(20)  NOT NULL COMMENT 'LOW|MEDIUM|HIGH|CRITICAL',
    `status`          VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT 'pending|confirmed|cancelled|expired',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `expires_at`      DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tool_call_id` (`tool_call_id`),
    KEY `idx_user_status` (`user_id`, `status`, `expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待确认 Tool 调用';
