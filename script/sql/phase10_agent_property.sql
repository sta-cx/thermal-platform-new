-- Phase 10: Agent Property Management
-- 添加代理商关联物业表的审核和启用状态字段
-- 对应 AgProperty 实体新增 is_audited 和 is_enabled 字段

-- 为 ag_company_property 表添加审核状态字段
ALTER TABLE `ag_company_property`
    ADD COLUMN IF NOT EXISTS `is_audited` TINYINT DEFAULT 0 COMMENT '是否审核 0未审核 1已审核' AFTER `property_company_id`;

-- 为 ag_company_property 表添加启用状态字段
ALTER TABLE `ag_company_property`
    ADD COLUMN IF NOT EXISTS `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用 0未启用 1启用' AFTER `is_audited`;
