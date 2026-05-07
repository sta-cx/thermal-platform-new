-- =============================================
-- 基础档案菜单重组
-- =============================================

-- ========== 新增目录菜单 ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2613, '基础档案', 2000, 2, 'basic-archive', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'ep:folder-opened', 1, NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2614, '客户房产管理', 2613, 1, 'customer-property', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'ep:house', 1, NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2615, '设备安装', 2613, 2, 'device-install', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'ep:set-up', 1, NOW());

-- ========== 新增页面菜单（设备安装子菜单） ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2620, '户间卡阀', 2615, 7, 'card-valve-archive', 'thermal/ht/card-valve-archive/index', NULL, 1, 0, 'C', '0', '0', 'thermal:ht:command-valve-archive:list', 'ep:key', 1, NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2621, '户间开关阀', 2615, 8, 'switch-valve-archive', 'thermal/ht/switch-valve-archive/index', NULL, 1, 0, 'C', '0', '0', 'thermal:ht:command-valve-archive:list', '#', 1, NOW());

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2622, '单元开关阀', 2615, 9, 'command-unit-valve-archive', 'thermal/ht/command-unit-valve-archive/index', NULL, 1, 0, 'C', '0', '0', 'thermal:ht:command-unit-valve-archive:list', '#', 1, NOW());

-- ========== 移动 + 重命名：物业收费 → 客户房产管理 ==========
UPDATE sys_menu SET parent_id = 2614, order_num = 1 WHERE menu_id = 2302;
UPDATE sys_menu SET parent_id = 2614, order_num = 2 WHERE menu_id = 2303;
UPDATE sys_menu SET parent_id = 2614, order_num = 3 WHERE menu_id = 2304;
UPDATE sys_menu SET parent_id = 2614, order_num = 4 WHERE menu_id = 2306;
UPDATE sys_menu SET parent_id = 2614, order_num = 5, menu_name = '入住登记' WHERE menu_id = 2313;

-- ========== 移动 + 重命名：热力调控 → 客户房产管理 ==========
UPDATE sys_menu SET parent_id = 2614, order_num = 6 WHERE menu_id = 2200;
UPDATE sys_menu SET parent_id = 2614, order_num = 7 WHERE menu_id = 2201;

-- ========== 移动 + 重命名：热力调控 → 设备安装 ==========
UPDATE sys_menu SET parent_id = 2615, order_num = 1, menu_name = 'DTU采集器' WHERE menu_id = 2217;
UPDATE sys_menu SET parent_id = 2615, order_num = 2, menu_name = '户间调节阀' WHERE menu_id = 2214;
UPDATE sys_menu SET parent_id = 2615, order_num = 3, menu_name = '单元调节阀' WHERE menu_id = 2216;
UPDATE sys_menu SET parent_id = 2615, order_num = 4, menu_name = '户间热表' WHERE menu_id = 2213;
UPDATE sys_menu SET parent_id = 2615, order_num = 5 WHERE menu_id = 2215;
UPDATE sys_menu SET parent_id = 2615, order_num = 6, menu_name = '温采器' WHERE menu_id = 2218;

-- ========== 调整同级排序 ==========
UPDATE sys_menu SET order_num = 3 WHERE menu_id = 2001;
UPDATE sys_menu SET order_num = 4 WHERE menu_id = 2002;
UPDATE sys_menu SET order_num = 5 WHERE menu_id = 2003;
UPDATE sys_menu SET order_num = 6 WHERE menu_id = 2004;
UPDATE sys_menu SET order_num = 7 WHERE menu_id = 2005;
