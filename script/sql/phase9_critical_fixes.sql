-- Phase 9: 关键缺失表补充 (审核发现)
-- 补充 ht_tasks_perform_ls 和 ht_tasks_perform_last
-- 审核报告: docs/audit/database-audit.md

-- ========================================
-- 1. 调控任务执行历史表
-- 旧系统对应: ht_tasks_perform_ls
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_tasks_perform_ls` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `tasks_id` varchar(32) DEFAULT NULL COMMENT '控制任务ID',
    `instruction_id` varchar(32) DEFAULT NULL COMMENT '指令ID',
    `orderr` int DEFAULT NULL COMMENT '指令顺序',
    `instruction_type` int DEFAULT NULL COMMENT '指令类型',
    `instruction` varchar(256) DEFAULT NULL COMMENT '指令内容',
    `number` int DEFAULT NULL COMMENT '指令执行次数',
    `intervall` int DEFAULT NULL COMMENT '间隔(上报周期需要)',
    `unit` varchar(8) DEFAULT NULL COMMENT '单位 01分钟 02小时 03天',
    `duration` tinyint(1) DEFAULT NULL COMMENT '作用时长',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `concentrator_code` varchar(64) DEFAULT NULL COMMENT '集中器编号',
    `tele_product_id` varchar(64) DEFAULT NULL COMMENT '电信产品ID',
    `tele_api_key` varchar(256) DEFAULT NULL COMMENT '电信平台Master-APIkey',
    `tele_app_key` varchar(256) DEFAULT NULL COMMENT '电信平台AppKey',
    `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
    `meter_num` varchar(64) DEFAULT NULL COMMENT '仪表号',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `meter_arc_code` varchar(64) DEFAULT NULL COMMENT '档案编号',
    `status` int DEFAULT NULL COMMENT '执行状态',
    `in_temp` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
    `out_temp` decimal(10,2) DEFAULT NULL COMMENT '当前回水温度',
    `room_temp` decimal(10,2) DEFAULT NULL COMMENT '当前室温',
    `valve_open` int DEFAULT NULL COMMENT '当前开度',
    `instruction_status` int DEFAULT NULL COMMENT '执行结果',
    `send_time` datetime DEFAULT NULL COMMENT '指令发送时间',
    `is_type` int DEFAULT NULL COMMENT '类型标识',
    `alert_type` varchar(32) DEFAULT NULL COMMENT '报警类型',
    `imei` varchar(64) DEFAULT NULL COMMENT '设备IMEI号码',
    `dtu_num` varchar(64) DEFAULT NULL COMMENT 'DTU编号',
    `chan_num` varchar(32) DEFAULT NULL COMMENT '通道号',
    `fore_start` int DEFAULT NULL COMMENT '是否开始新的循环 1是 0否',
    `out_temp_pj` decimal(10,2) DEFAULT NULL COMMENT '平均回水温度',
    `cur_flow_compute` decimal(10,2) DEFAULT NULL COMMENT '计算流量',
    `ref_heat` decimal(10,2) DEFAULT NULL COMMENT '参考热量',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_tasks_id` (`tasks_id`),
    KEY `idx_meter_num` (`meter_num`),
    KEY `idx_send_time` (`send_time`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调控任务执行历史表';

-- ========================================
-- 2. 调控任务上次执行记录表
-- 旧系统对应: ht_tasks_perform_last
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_tasks_perform_last` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `tasks_id` varchar(32) DEFAULT NULL COMMENT '控制任务ID',
    `instruction_id` varchar(32) DEFAULT NULL COMMENT '指令ID',
    `orderr` int DEFAULT NULL COMMENT '指令顺序',
    `instruction_type` int DEFAULT NULL COMMENT '指令类型',
    `instruction` varchar(256) DEFAULT NULL COMMENT '指令内容',
    `number` int DEFAULT NULL COMMENT '指令执行次数',
    `intervall` int DEFAULT NULL COMMENT '间隔',
    `unit` varchar(8) DEFAULT NULL COMMENT '单位',
    `duration` tinyint(1) DEFAULT NULL COMMENT '作用时长',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `concentrator_code` varchar(64) DEFAULT NULL COMMENT '集中器编号',
    `tele_product_id` varchar(64) DEFAULT NULL COMMENT '电信产品ID',
    `tele_api_key` varchar(256) DEFAULT NULL COMMENT '电信平台Master-APIkey',
    `tele_app_key` varchar(256) DEFAULT NULL COMMENT '电信平台AppKey',
    `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
    `meter_num` varchar(64) DEFAULT NULL COMMENT '仪表号',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `meter_arc_code` varchar(64) DEFAULT NULL COMMENT '档案编号',
    `status` int DEFAULT NULL COMMENT '执行状态',
    `instruction_status` int DEFAULT NULL COMMENT '执行结果',
    `send_time` datetime DEFAULT NULL COMMENT '指令发送时间',
    `fore_start` int DEFAULT NULL COMMENT '是否开始新的循环',
    `in_temp` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
    `out_temp` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
    `room_temp` decimal(10,2) DEFAULT NULL COMMENT '室温',
    `valve_open` int DEFAULT NULL COMMENT '当前开度',
    `imei` varchar(64) DEFAULT NULL COMMENT '设备IMEI',
    `dtu_num` varchar(64) DEFAULT NULL COMMENT 'DTU编号',
    `chan_num` varchar(32) DEFAULT NULL COMMENT '通道号',
    `out_temp_pj` decimal(10,2) DEFAULT NULL COMMENT '平均回水温度',
    `cur_flow_compute` decimal(10,2) DEFAULT NULL COMMENT '计算流量',
    `ref_heat` decimal(10,2) DEFAULT NULL COMMENT '参考热量',
    `group_id` varchar(32) DEFAULT NULL COMMENT '策略ID',
    `strategy_id` varchar(32) DEFAULT NULL COMMENT '任务组ID',
    `command_index` int DEFAULT NULL COMMENT '任务指令顺序号',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_tasks_id` (`tasks_id`),
    KEY `idx_meter_num` (`meter_num`),
    KEY `idx_send_time` (`send_time`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调控任务上次执行记录表';

-- ========================================
-- 3. 热力实时数据表
-- 旧系统对应: pr_heat_real_data
-- 用于存储阀门/热表的实时IoT数据
-- ========================================
CREATE TABLE IF NOT EXISTS `pr_heat_real_data` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `building_id` varchar(32) DEFAULT NULL COMMENT '楼栋ID',
    `building_name` varchar(128) DEFAULT NULL COMMENT '楼栋名称',
    `org_name` varchar(128) DEFAULT NULL COMMENT '小区名称',
    `room_num` varchar(32) DEFAULT NULL COMMENT '房间号',
    `unit_code` varchar(32) DEFAULT NULL COMMENT '单元编号',
    `floor` varchar(32) DEFAULT NULL COMMENT '楼层',
    `station_name` varchar(128) DEFAULT NULL COMMENT '换热站名称',
    `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度(阀门)',
    `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度(阀门)',
    `setting_status` int DEFAULT NULL COMMENT '设定开度',
    `valve_status` int DEFAULT NULL COMMENT '实际开度',
    `create_time` datetime DEFAULT NULL COMMENT '阀门更新时间',
    `rb_create_time` datetime DEFAULT NULL COMMENT '热表更新时间',
    `total_heat` decimal(12,2) DEFAULT NULL COMMENT '累计热量',
    `total_flow` decimal(12,2) DEFAULT NULL COMMENT '累计流量',
    `total_worktime` decimal(12,2) DEFAULT NULL COMMENT '累计时长',
    `attack_status` varchar(32) DEFAULT NULL COMMENT '阀门设备状态',
    `meter_num` varchar(64) DEFAULT NULL COMMENT '阀门编号',
    `rb_status1` varchar(32) DEFAULT NULL COMMENT '热表状态1',
    `rb_status2` varchar(32) DEFAULT NULL COMMENT '热表状态2',
    `rb_attack_status` varchar(32) DEFAULT NULL COMMENT '热表设备状态',
    `rb_meter_num` varchar(64) DEFAULT NULL COMMENT '热表编号',
    `rb_voltage` varchar(32) DEFAULT NULL COMMENT '热表电量',
    `voltage` varchar(32) DEFAULT NULL COMMENT '阀门电量',
    `rb_in` varchar(32) DEFAULT NULL COMMENT '进水温度(热表)',
    `rb_out` decimal(10,2) DEFAULT NULL COMMENT '回水温度(热表)',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_meter_num` (`meter_num`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='热力实时数据表';

-- ========================================
-- 4. 审批单表
-- ========================================
CREATE TABLE IF NOT EXISTS `pr_approval` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `no` varchar(64) DEFAULT NULL COMMENT '流程编号',
    `type` varchar(32) DEFAULT NULL COMMENT '申请类型',
    `approval_user` varchar(64) DEFAULT NULL COMMENT '申请人',
    `approval_time` datetime DEFAULT NULL COMMENT '申请时间',
    `title` varchar(256) DEFAULT NULL COMMENT '任务名称',
    `preferential_type` int DEFAULT NULL COMMENT '减免类型',
    `preferential` decimal(10,2) DEFAULT NULL COMMENT '减免金额',
    `preferential_reason` varchar(255) DEFAULT NULL COMMENT '减免原因',
    `approval_link` int DEFAULT NULL COMMENT '审批环节',
    `approval_type` int DEFAULT NULL COMMENT '审批类型',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `approval_users` varchar(512) DEFAULT NULL COMMENT '审批人(当前环节)',
    `approval_users_all` varchar(1024) DEFAULT NULL COMMENT '所有审批人',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_approval_user` (`approval_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批单表';

-- ========================================
-- 5. 审批明细表
-- ========================================
CREATE TABLE IF NOT EXISTS `pr_approval_sub` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `approval_id` varchar(32) DEFAULT NULL COMMENT '审批单ID',
    `expense_id` varchar(32) DEFAULT NULL COMMENT '费用明细ID',
    `org_name` varchar(128) DEFAULT NULL,
    `building_name` varchar(128) DEFAULT NULL,
    `room_num` varchar(32) DEFAULT NULL,
    `item_name` varchar(128) DEFAULT NULL,
    `item_code` varchar(32) DEFAULT NULL,
    `standard_id` varchar(32) DEFAULT NULL,
    `standard_price` decimal(10,2) DEFAULT NULL,
    `start_date` datetime DEFAULT NULL,
    `expire_date` datetime DEFAULT NULL,
    `last_date` datetime DEFAULT NULL,
    `qty` int DEFAULT NULL,
    `preferential` decimal(10,2) DEFAULT NULL COMMENT '减免金额',
    `deduction` decimal(10,2) DEFAULT NULL COMMENT '扣除金额',
    `latefee` decimal(10,2) DEFAULT NULL COMMENT '滞纳金',
    `receivable` decimal(10,2) DEFAULT NULL COMMENT '应收金额',
    `final_money` decimal(10,2) DEFAULT NULL COMMENT '最终金额',
    `expense_create_time` datetime DEFAULT NULL,
    `warehouse_name` varchar(128) DEFAULT NULL,
    `warehouse_id` varchar(32) DEFAULT NULL,
    `material_name` varchar(128) DEFAULT NULL,
    `material_id` varchar(32) DEFAULT NULL,
    `material_user` varchar(64) DEFAULT NULL,
    `material_use` varchar(255) DEFAULT NULL,
    `company_id` varchar(32) DEFAULT NULL,
    `org_id` varchar(32) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_approval_id` (`approval_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批明细表';

-- ========================================
-- 6. 审批意见表
-- ========================================
CREATE TABLE IF NOT EXISTS `pr_approval_opinion` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `approval_id` varchar(32) DEFAULT NULL COMMENT '审批单ID',
    `approval_user` varchar(64) DEFAULT NULL COMMENT '审批人',
    `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
    `opinions` varchar(512) DEFAULT NULL COMMENT '意见',
    `approval_status` int DEFAULT NULL COMMENT '审批状态 0待审批 1通过 2驳回',
    `approval_link` int DEFAULT NULL COMMENT '审批环节',
    `company_id` varchar(32) DEFAULT NULL,
    `org_id` varchar(32) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_approval_id` (`approval_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审批意见表';

-- ========================================
-- 7. pr_house 数据迁移提示
-- 旧 area 列 → 新 heating_area 列
-- 执行前确认旧数据已导入新系统
-- ========================================
-- UPDATE pr_house SET heating_area = CAST(area AS DECIMAL(10,2))
-- WHERE area IS NOT NULL AND area != '' AND heating_area IS NULL;

