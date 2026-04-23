-- sys_column 表 — 用户自定义表格列
-- Phase 2 迁移自旧系统 SysColumnController
CREATE TABLE IF NOT EXISTS `sys_column` (
    `id` bigint NOT NULL,
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `page_name` varchar(100) NOT NULL COMMENT '页面/表格名称',
    `column_name` text COMMENT '自定义列名（逗号分隔）',
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_page` (`user_id`, `page_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户自定义表格列';
