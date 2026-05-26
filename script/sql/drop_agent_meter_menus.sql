-- 删除代理商仪表菜单(后端 AgentMeterController 已删,前端目录已删)
-- 2026-05-26 配套清理
DELETE FROM sys_role_menu WHERE menu_id IN (2110, 3041, 3042);
DELETE FROM sys_menu WHERE menu_id IN (2110, 3041, 3042);
