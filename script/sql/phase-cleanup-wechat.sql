-- =====================================================
-- 清理微信相关菜单、表、字段
-- 执行前请备份相关表
-- =====================================================

-- ========== master 库 (ry-vue) ==========

-- 1. 删除子菜单（微信支付/授权/报修/对账/小程序/IoT回调日志）
DELETE FROM sys_menu WHERE parent_id IN (2005, 2324);

-- 2. 删除父菜单（微信管理、微信绑定）
DELETE FROM sys_menu WHERE menu_id IN (2005, 2324);

-- 3. 清理角色-菜单关联孤儿记录
DELETE FROM sys_role_menu WHERE menu_id NOT IN (SELECT menu_id FROM sys_menu);

-- ========== 租户库 (tenant_*) ==========

-- 4. 删除微信相关表
DROP TABLE IF EXISTS pr_wechat_bill;
DROP TABLE IF EXISTS pr_wechat_bind_record;
DROP TABLE IF EXISTS pr_wechat_order;
DROP TABLE IF EXISTS pr_wechat_refund;
DROP TABLE IF EXISTS pr_wechat_user;
DROP TABLE IF EXISTS pr_reconciliation_diff;

-- 5. 删除 ag_user 表的微信字段
ALTER TABLE ag_user DROP COLUMN IF EXISTS wx_openid;
ALTER TABLE ag_user DROP COLUMN IF EXISTS wx_number;
