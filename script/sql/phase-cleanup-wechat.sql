-- =====================================================
-- 清理微信相关菜单（微信管理 + 微信绑定）
-- 执行前请备份 sys_menu 和 sys_role_menu 表
-- =====================================================

-- 1. 删除子菜单（微信支付/授权/报修/对账/小程序/IoT回调日志）
DELETE FROM sys_menu WHERE parent_id IN (2005, 2324);

-- 2. 删除父菜单（微信管理、微信绑定）
DELETE FROM sys_menu WHERE menu_id IN (2005, 2324);

-- 3. 清理角色-菜单关联孤儿记录
DELETE FROM sys_role_menu WHERE menu_id NOT IN (SELECT menu_id FROM sys_menu);
