-- 仪表盘菜单合并：4个子页面 → 1个大首页
-- 执行前请备份 sys_menu 表

-- 1. 删除4个子菜单
DELETE FROM sys_menu WHERE menu_id IN (2600, 2601, 2602, 2603);

-- 2. 删除角色-菜单关联（如果存在）
DELETE FROM sys_role_menu WHERE menu_id IN (2600, 2601, 2602, 2603);

-- 3. 将仪表盘从目录(M)改为页面(C)
UPDATE sys_menu SET
  menu_type = 'C',
  component = 'dashboard/index',
  perms = 'dashboard:list',
  icon = 'ep:data-analysis'
WHERE menu_id = 2610;
