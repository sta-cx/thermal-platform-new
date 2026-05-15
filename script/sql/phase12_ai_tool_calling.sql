-- 在 master 库 (ry-vue) 执行
USE `ry-vue`;

-- 1) 待确认 Tool Call(状态机)
CREATE TABLE IF NOT EXISTS `ai_pending_tool_call` (
  `id`            BIGINT       NOT NULL COMMENT '雪花 ID',
  `call_id`       VARCHAR(64)  NOT NULL COMMENT 'UUID,前端 ↔ Redis Key 一致',
  `tenant_id`     VARCHAR(20)  NOT NULL,
  `user_id`       BIGINT       NOT NULL,
  `session_id`    BIGINT       NOT NULL COMMENT 'ai_chat_session.id',
  `message_id`    BIGINT       NULL COMMENT '触发的 ASSISTANT 消息 id',
  `tool_name`     VARCHAR(128) NOT NULL COMMENT '@Tool 方法 Bean.method',
  `risk_level`    VARCHAR(16)  NOT NULL COMMENT 'LOW/MEDIUM/HIGH/CRITICAL',
  `arguments`     TEXT         NOT NULL COMMENT 'LLM 决策的入参 JSON',
  `effective_args` TEXT        NULL COMMENT '权限覆盖后的实际入参 JSON(如 dryRun 强制改写)',
  `status`        VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/EXPIRED/EXECUTED/FAILED',
  `result`        TEXT         NULL COMMENT '执行后返回 JSON(成功)或异常 message(失败)',
  `created_time`  DATETIME     NOT NULL,
  `decided_time`  DATETIME     NULL COMMENT '用户点确认/拒绝时间',
  `executed_time` DATETIME     NULL,
  `expire_time`   DATETIME     NOT NULL COMMENT '创建+30 min,过期自动 EXPIRED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_call_id` (`call_id`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_tenant_created` (`tenant_id`, `created_time`),
  KEY `idx_expire_pending` (`status`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 待确认 Tool 调用(双写审计源)';

-- 2) Tool 调用详情(消息流可展开卡)
CREATE TABLE IF NOT EXISTS `ai_tool_invocation` (
  `id`             BIGINT       NOT NULL,
  `message_id`     BIGINT       NOT NULL COMMENT 'ai_chat_message.id (role=TOOL 那一行)',
  `pending_call_id` VARCHAR(64) NULL COMMENT '若经过 ConfirmationGate,关联 ai_pending_tool_call.call_id',
  `tenant_id`      VARCHAR(20)  NOT NULL,
  `user_id`        BIGINT       NOT NULL,
  `tool_name`      VARCHAR(128) NOT NULL,
  `risk_level`     VARCHAR(16)  NOT NULL,
  `arguments`      TEXT         NOT NULL,
  `result`         TEXT         NULL,
  `status`         VARCHAR(16)  NOT NULL COMMENT 'SUCCESS/FAILED/DRY_RUN',
  `latency_ms`     INT          NOT NULL DEFAULT 0,
  `error_message`  VARCHAR(512) NULL,
  `created_time`   DATETIME     NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`),
  KEY `idx_pending_call_id` (`pending_call_id`),
  KEY `idx_tenant_tool` (`tenant_id`, `tool_name`, `created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Tool 调用详情(对话消息流的展开内容)';

-- 3) ai_chat_message.role 兼容 TOOL(已是 VARCHAR,无 schema 改动,只是文档约定)
-- (无 ALTER 必要)
