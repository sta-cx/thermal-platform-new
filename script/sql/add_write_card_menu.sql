-- 写卡工作台 + 写卡记录（沿用 thermal:ht:valveArchive 权限码）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time)
VALUES
(3663, '写卡工作台', 2002, 90, 'write-card-console', 'thermal/ht/write-card-console/index', NULL, '1', '0', 'C', '0', '0', 'thermal:ht:valveArchive:edit', 'credit-card', NOW()),
(3664, '写卡记录',  2002, 91, 'write-card-log',     'thermal/ht/write-card-log/index',     NULL, '1', '0', 'C', '0', '0', 'thermal:ht:valveArchive:list', 'profile',     NOW());
