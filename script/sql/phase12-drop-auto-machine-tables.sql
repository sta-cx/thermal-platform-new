-- ============================================================
-- 删除自助缴费机相关数据库表(ag_reader_param + ag_auto_version)
-- 工程:自助缴费机模块全删(D-04 子工程之一)
-- Spec:docs/superpowers/specs/2026-05-25-auto-machine-removal-design.md
-- 日期:2026-05-25
--
-- 应用范围:所有 tenant_* 业务库(当前仅 tenant_000000)
--
-- 执行前确认:
--   1. Spring Boot 已重启,新代码不再含 AgReaderParam/AgAutoVersion Mapper
--   2. 2 个表的行数为 0(实际验证已确认 0 行)
--
-- 执行:
--   mysql -uroot -proot -h localhost tenant_000000 < phase12-drop-auto-machine-tables.sql
-- ============================================================

USE tenant_000000;

DROP TABLE IF EXISTS `ag_reader_param`;
DROP TABLE IF EXISTS `ag_auto_version`;

-- 验证
SHOW TABLES LIKE 'ag_%';
