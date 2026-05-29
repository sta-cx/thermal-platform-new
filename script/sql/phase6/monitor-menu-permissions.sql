-- M-BE 监控模块后端 9 端点权限菜单
-- 日期: 2026-05-29
-- 关联: docs/superpowers/specs/2026-05-29-monitor-backend-followup-spec.md
-- 在 master 库 ry-vue 执行

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(3657, '设备详情', 3654, 10, '', '', 1, 0, 'F', '0', '0', 'thermal:ht:monitor:detail', '#', 1, NOW(), 1, NOW(), ''),
(3658, '巡测',     3654, 11, '', '', 1, 0, 'F', '0', '0', 'thermal:ht:monitor:xunce', '#', 1, NOW(), 1, NOW(), ''),
(3659, '参数设置', 3654, 12, '', '', 1, 0, 'F', '0', '0', 'thermal:ht:monitor:setting', '#', 1, NOW(), 1, NOW(), ''),
(3660, '第三方编码', 3654, 13, '', '', 1, 0, 'F', '0', '0', 'thermal:ht:monitor:other-code', '#', 1, NOW(), 1, NOW(), ''),
(3661, '特殊户',   3654, 14, '', '', 1, 0, 'F', '0', '0', 'thermal:ht:monitor:special', '#', 1, NOW(), 1, NOW(), ''),
(3662, '停供',     3654, 15, '', '', 1, 0, 'F', '0', '0', 'thermal:ht:monitor:stop-supply', '#', 1, NOW(), 1, NOW(), '')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 绑定超管角色 (role_id=1)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 3657), (1, 3658), (1, 3659), (1, 3660), (1, 3661), (1, 3662)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
