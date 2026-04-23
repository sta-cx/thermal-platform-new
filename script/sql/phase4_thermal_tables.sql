-- Phase 4: 热力调控模块表迁移
-- 迁移自旧系统调控模块

-- ========================================
-- 1. 控制指令表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_instruction` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `name` varchar(64) NOT NULL COMMENT '指令名称',
    `type` tinyint NOT NULL COMMENT '指令类型',
    `instruction` varchar(256) DEFAULT NULL COMMENT '指令内容',
    `remark` varchar(60) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制指令表';

-- ========================================
-- 2. 控制策略主表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_strategy` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `name` varchar(64) NOT NULL COMMENT '策略名称',
    `type` tinyint DEFAULT NULL,
    `company_id` varchar(32) DEFAULT NULL,
    `remark` varchar(255) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制策略主表';

-- ========================================
-- 3. 控制策略子表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_strategy_sub` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `strategy_id` varchar(32) DEFAULT NULL,
    `instruction_id` varchar(32) DEFAULT NULL,
    `sort` int DEFAULT NULL,
    `valve_angle` varchar(32) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制策略子表';

-- ========================================
-- 4. 报警表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_alert` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `building_id` varchar(32) DEFAULT NULL,
    `unit_id` varchar(32) DEFAULT NULL,
    `house_id` varchar(32) DEFAULT NULL,
    `meter_id` varchar(32) NOT NULL,
    `is_charged` tinyint DEFAULT NULL,
    `valve` tinyint DEFAULT NULL,
    `in_temp` decimal(6,2) DEFAULT NULL,
    `out_temp` decimal(6,2) DEFAULT NULL,
    `room_temp` decimal(6,2) DEFAULT NULL,
    `alert_type` tinyint NOT NULL,
    `alert_time` datetime NOT NULL,
    `alert_status` varchar(500) DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `company_id` varchar(32) NOT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `in_maintenance` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `meter_id` (`meter_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报警表';

-- ========================================
-- 5. 报修表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_repair` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `building_id` varchar(32) DEFAULT NULL,
    `building_name` varchar(255) DEFAULT NULL,
    `unit_code` varchar(255) DEFAULT NULL,
    `house_id` varchar(32) DEFAULT NULL,
    `room_num` varchar(255) DEFAULT NULL,
    `meter_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(20) DEFAULT NULL,
    `is_charged` tinyint DEFAULT '0',
    `valve_status` varchar(10) DEFAULT NULL,
    `valve` tinyint DEFAULT NULL,
    `in_temp` decimal(6,2) DEFAULT NULL,
    `out_temp` decimal(6,2) DEFAULT NULL,
    `room_temp` decimal(6,2) DEFAULT NULL,
    `repair_type` tinyint NOT NULL,
    `repair_time` datetime NOT NULL,
    `repair_info` varchar(500) DEFAULT NULL,
    `repair_status` tinyint DEFAULT '0',
    `repair_result` varchar(500) DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `org_name` varchar(255) DEFAULT NULL,
    `company_id` varchar(32) NOT NULL,
    `is_delete` tinyint DEFAULT '0',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_name` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `in_maintenance` varchar(255) DEFAULT NULL,
    `dispatch_id` varchar(50) DEFAULT NULL,
    `dispatch_name` varchar(50) DEFAULT NULL,
    `dispatch_time` datetime DEFAULT NULL,
    `repair_no` varchar(25) DEFAULT NULL,
    `fix_id` varchar(50) DEFAULT NULL,
    `fix_name` varchar(50) DEFAULT NULL,
    `fix_time` datetime DEFAULT NULL,
    `user_name` varchar(255) DEFAULT NULL,
    `user_phone` varchar(20) DEFAULT NULL,
    `appoint_time` datetime DEFAULT NULL,
    `urgent_type` tinyint DEFAULT NULL,
    `service_type` tinyint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `meter_id` (`meter_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报修表';

-- ========================================
-- 6. 控制任务表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_tasks` (
    `id` int NOT NULL AUTO_INCREMENT,
    `tenant_id` varchar(20) DEFAULT '000000',
    `cu_group_id` varchar(32) NOT NULL DEFAULT '',
    `name` varchar(64) NOT NULL COMMENT '任务名称',
    `type` tinyint NOT NULL COMMENT '执行方式',
    `cron_expression` varchar(255) NOT NULL COMMENT '时间表达式',
    `strategy_id` varchar(32) DEFAULT NULL,
    `priority` tinyint DEFAULT NULL,
    `status` tinyint NOT NULL COMMENT '0停止 1启动',
    `number` tinyint NOT NULL DEFAULT '0',
    `last_time` datetime DEFAULT NULL,
    `total` tinyint DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `company_id` varchar(32) NOT NULL,
    `adjust_basis` tinyint NOT NULL,
    `scope_type` tinyint NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `bean_class` varchar(255) NOT NULL,
    `job_group` varchar(32) NOT NULL,
    `days` tinyint DEFAULT NULL,
    `nums` tinyint DEFAULT NULL,
    `standard` tinyint DEFAULT NULL,
    `end_time` datetime DEFAULT NULL,
    `execution_time` int DEFAULT '0',
    `out_temp_pj` decimal(6,2) DEFAULT NULL,
    `is_use_report_rate` tinyint(1) NOT NULL DEFAULT '0',
    `report_rate` int NOT NULL DEFAULT '0',
    `is_use_first_control` tinyint(1) NOT NULL DEFAULT '0',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(32) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(32) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制任务表';

-- ========================================
-- 7. 调控执行记录表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_tasks_perform` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `instruction_id` varchar(32) DEFAULT NULL,
    `group_id` varchar(32) DEFAULT NULL,
    `strategy_id` varchar(32) DEFAULT NULL,
    `command_index` int DEFAULT NULL,
    `orderr` int DEFAULT NULL,
    `instruction_type` int DEFAULT NULL,
    `instruction` int DEFAULT NULL,
    `number` int DEFAULT NULL,
    `intervall` int DEFAULT NULL,
    `fore_start` int DEFAULT NULL,
    `unit` int DEFAULT NULL,
    `duration` int DEFAULT NULL,
    `org_id` varchar(32) DEFAULT NULL,
    `company_id` varchar(32) DEFAULT NULL,
    `concentrator_code` varchar(32) DEFAULT NULL,
    `device_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(32) DEFAULT NULL,
    `meter_id` varchar(32) DEFAULT NULL,
    `meter_arc_code` varchar(32) DEFAULT NULL,
    `status` int DEFAULT NULL,
    `instruction_status` int DEFAULT NULL,
    `send_time` datetime DEFAULT NULL,
    `tasks_id` varchar(32) DEFAULT NULL,
    `in_temp` decimal(6,2) DEFAULT NULL,
    `out_temp` decimal(6,2) DEFAULT NULL,
    `room_temp` decimal(6,2) DEFAULT NULL,
    `valve_open` int DEFAULT NULL,
    `imei` varchar(30) DEFAULT NULL,
    `dtu_num` varchar(20) DEFAULT NULL,
    `chan_num` varchar(20) DEFAULT NULL,
    `out_temp_pj` decimal(6,2) DEFAULT NULL,
    `ref_heat` decimal(10,2) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_tasks_id` (`tasks_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调控执行记录表';

-- ========================================
-- 8. 控制范围表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_scope` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `tasks_id` varchar(32) NOT NULL,
    `org_id` varchar(32) NOT NULL,
    `building_id` varchar(32) DEFAULT NULL,
    `unit_id` varchar(32) DEFAULT NULL,
    `company_id` varchar(32) NOT NULL,
    `house_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(20) NOT NULL,
    `meter_id` varchar(32) NOT NULL,
    `meter_arc_code` varchar(10) NOT NULL,
    `concentrator_code` varchar(10) DEFAULT NULL,
    `imei` varchar(30) DEFAULT NULL,
    `device_id` varchar(32) DEFAULT NULL,
    `status` tinyint DEFAULT NULL,
    `is_special` tinyint(1) DEFAULT '0',
    `dtu_num` varchar(20) DEFAULT NULL,
    `chan_num` varchar(20) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制范围表';

-- ========================================
-- 9. 单元房屋策略表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_house_strategy` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `tasks_id` varchar(32) NOT NULL,
    `type` tinyint NOT NULL,
    `strategy_id` varchar(32) NOT NULL,
    `name` varchar(64) DEFAULT NULL,
    `remark` varchar(125) DEFAULT NULL,
    `adjust_basis` tinyint DEFAULT NULL,
    `stride` tinyint DEFAULT NULL,
    `priority` tinyint DEFAULT NULL,
    `intervall` int DEFAULT '30',
    `number` tinyint DEFAULT '0',
    `valve_min` tinyint DEFAULT '0',
    `valve_max` tinyint DEFAULT '100',
    `in_temp` decimal(6,2) DEFAULT '0.00',
    `in_temp_deviation` tinyint DEFAULT '0',
    `out_temp` decimal(6,2) DEFAULT '0.00',
    `out_temp_deviation` tinyint DEFAULT NULL,
    `is_in_temp_alert_min` decimal(6,2) DEFAULT '0.00',
    `is_in_temp_alert_max` decimal(6,2) DEFAULT '100.00',
    `room_temp` decimal(6,2) DEFAULT '0.00',
    `room_temp_deviation` tinyint DEFAULT NULL,
    `scope_type` tinyint DEFAULT NULL,
    `is_report_police` tinyint DEFAULT '0',
    `report_police_number` tinyint DEFAULT NULL,
    `is_manage_police` tinyint DEFAULT '0',
    `manage_police_number` tinyint DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `company_id` varchar(32) NOT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `preset_angle` tinyint DEFAULT NULL,
    `preset_flow_rate` decimal(10,2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='单元房屋策略表';

-- ========================================
-- 10. 任务执行设定历史表(主表)
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_task_setting_log` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `task_id` varchar(5) NOT NULL,
    `scope_type` varchar(2) NOT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) NOT NULL,
    `create_time` datetime NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务执行设定历史表';

-- ========================================
-- 11. 任务执行设定历史表(子表)
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_task_setting_log_item` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `main_id` varchar(32) NOT NULL,
    `scope_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(32) DEFAULT NULL,
    `old_angle` int DEFAULT NULL,
    `new_angle` int DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务执行设定历史子表';
