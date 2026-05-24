-- ============================================================
-- 删除 pr_user 表 3 个照片列(head_photo / front_photo / back_photo)
-- 工程: OSS 头像删除(D-04 子工程之一)
-- Spec: docs/superpowers/specs/2026-05-24-pr-user-photo-removal-design.md
-- 日期: 2026-05-24
--
-- 应用范围: 所有 tenant_* 业务库(当前仅 tenant_000000)
--
-- 执行前确认:
--   1. Spring Boot 已重启,新代码 SELECT 列表不再含这 3 列
--   2. pr_user 表数据为空,或已确认 3 列均为 NULL/空值
--
-- 执行:
--   mysql -uroot -proot -h localhost tenant_000000 < phase11-drop-pr-user-photos.sql
-- ============================================================

USE tenant_000000;

ALTER TABLE pr_user
    DROP COLUMN head_photo,
    DROP COLUMN front_photo,
    DROP COLUMN back_photo;

-- 验证
DESC pr_user;
