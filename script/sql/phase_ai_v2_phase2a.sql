-- ============================================================
-- Phase AI V2 — Phase 2A: 租户 AI 总闸 + 知识库 + 对话表
-- 执行顺序: 在 phase_ai_v2_phase1.sql 之后
-- 目标库: master (ry-vue)
-- ============================================================

-- 1. 租户表加 AI 总闸字段
ALTER TABLE sys_tenant
ADD COLUMN ai_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用 AI 功能(0=关 1=开)' AFTER status;

-- 2. 知识库文档元数据
CREATE TABLE ai_knowledge_doc (
    id BIGINT PRIMARY KEY COMMENT '雪花 ID',
    tenant_id VARCHAR(20) NOT NULL COMMENT '租户 ID',
    title VARCHAR(255) NOT NULL COMMENT '文档标题',
    source_format VARCHAR(20) NOT NULL COMMENT 'MARKDOWN|PLAIN_TEXT|PDF',
    source_size INT NOT NULL COMMENT '原文字节数',
    source_hash VARCHAR(64) NOT NULL COMMENT '原文 sha256,用于去重',
    chunk_count INT NOT NULL DEFAULT 0 COMMENT '切片数',
    status VARCHAR(20) NOT NULL DEFAULT 'UPLOADED' COMMENT 'UPLOADED|CHUNKED|EMBEDDED|FAILED',
    error_message TEXT NULL COMMENT '失败原因',
    create_by BIGINT NULL, update_by BIGINT NULL,
    create_time DATETIME NOT NULL, update_time DATETIME NULL,
    create_dept BIGINT NULL,
    INDEX idx_tenant_status (tenant_id, status),
    UNIQUE KEY uk_tenant_hash (tenant_id, source_hash)
) COMMENT='AI 知识库文档元数据';

-- 3. 切片元数据(向量本体存 Qdrant,这里只存 fk 与 payload)
CREATE TABLE ai_knowledge_chunk (
    id BIGINT PRIMARY KEY,
    doc_id BIGINT NOT NULL COMMENT '所属文档',
    tenant_id VARCHAR(20) NOT NULL,
    chunk_index INT NOT NULL COMMENT '切片在文档内的顺序',
    text TEXT NOT NULL COMMENT '切片纯文本(供 RAG 命中后 prompt 用)',
    token_count INT NOT NULL COMMENT '切片 token 数',
    qdrant_point_id VARCHAR(64) NOT NULL COMMENT 'Qdrant 中对应 point 的 UUID',
    create_time DATETIME NOT NULL,
    INDEX idx_doc (doc_id),
    INDEX idx_tenant (tenant_id)
) COMMENT='AI 知识库切片元数据';

-- 4. 多轮对话会话
CREATE TABLE ai_chat_session (
    id BIGINT PRIMARY KEY,
    tenant_id VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NULL COMMENT '会话标题',
    last_active_at DATETIME NOT NULL,
    create_time DATETIME NOT NULL,
    INDEX idx_user_last (user_id, last_active_at DESC),
    INDEX idx_tenant (tenant_id)
) COMMENT='AI 对话会话';

-- 5. 对话消息
CREATE TABLE ai_chat_message (
    id BIGINT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'USER|ASSISTANT|SYSTEM',
    content TEXT NOT NULL,
    token_count INT NULL COMMENT 'LLM 返回的本条 token 计数',
    create_time DATETIME NOT NULL,
    INDEX idx_session (session_id, create_time)
) COMMENT='AI 对话消息';

-- 6. Spring AI ChatMemory 表由框架启动时自动创建

-- 7. AI 知识库管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_time, create_dept)
VALUES ('AI 知识库', (SELECT menu_id FROM sys_menu WHERE menu_name='系统管理' LIMIT 1),
        100, 'ai-kb', 'system/ai-kb/index', 'C', '0', '0', 'ai:kb:manage', 'cloud', NOW(), 1);
