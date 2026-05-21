-- Phase 清理：删除迁移审计发现的多做模块菜单
-- 电表档案(2102)、水表档案(2101)、燃气表档案(2103)、写卡日志(2317)
-- 这些功能在老系统前端已去掉/废弃，新系统不应实现
-- 执行环境：master 库 ry-vue

-- 1. 删除子菜单（按钮/权限）
DELETE FROM sys_menu WHERE parent_id IN (2101, 2102, 2103, 2317);

-- 2. 删除父菜单
DELETE FROM sys_menu WHERE menu_id IN (2101, 2102, 2103, 2317);

-- 3. 清理角色-菜单关联表中的孤立记录
DELETE FROM sys_role_menu WHERE menu_id NOT IN (SELECT menu_id FROM sys_menu);
