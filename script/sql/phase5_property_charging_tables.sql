-- Phase 5: 物业收费模块表迁移 + 菜单系统迁移
-- 从旧库 rltk_pro 迁移到新库 ry-vue

-- ========================================
-- 1. 交易记录主表
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_transaction_record (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `serial_num` varchar(32) DEFAULT NULL COMMENT '流水号',
    `transaction_type` tinyint DEFAULT NULL COMMENT '交易类型: 1=收费 2=退费 3=转存 4=优惠',
    `payment_type` tinyint DEFAULT NULL COMMENT '缴费方式: 1=现金 2=微信 3=支付宝 4=刷卡',
    `amount` decimal(18,4) DEFAULT 0.0000 COMMENT '交易金额',
    `paid_amount` decimal(18,4) DEFAULT 0.0000 COMMENT '实收金额',
    `status` tinyint DEFAULT 0 COMMENT '交易状态: 0=正常 1=撤销 2=作废',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `transaction_time` datetime DEFAULT NULL COMMENT '交易时间',
    `operator_id` varchar(32) DEFAULT NULL COMMENT '操作人',
    `notes` varchar(255) DEFAULT NULL COMMENT '备注',
    `original_record_id` varchar(32) DEFAULT NULL COMMENT '原交易记录ID',
    `invoice_no` varchar(32) DEFAULT NULL COMMENT '发票号',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_serial_num` (`serial_num`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_transaction_time` (`transaction_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易记录主表';

-- ========================================
-- 2. 交易记录子表明细
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_transaction_record_sub (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `main_id` varchar(32) DEFAULT NULL COMMENT '主表ID',
    `expense_id` varchar(32) DEFAULT NULL COMMENT '费用明细ID',
    `amount` decimal(18,4) DEFAULT 0.0000 COMMENT '交易金额',
    `balance_before` decimal(18,4) DEFAULT 0.0000 COMMENT '交易前余额',
    `balance_after` decimal(18,4) DEFAULT 0.0000 COMMENT '交易后余额',
    `item_name` varchar(64) DEFAULT NULL COMMENT '费项名称',
    `notes` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_main_id` (`main_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易记录子表明细';

-- ========================================
-- 3. 个人账户余额
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_account_balance (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `balance` decimal(18,4) DEFAULT 0.0000 COMMENT '账户余额',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_house` (`user_id`, `house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='个人账户余额';

-- ========================================
-- 4. 票据备注
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_billing_notes (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `serial_num` varchar(32) NOT NULL COMMENT '流水号',
    `notes` varchar(255) DEFAULT NULL COMMENT '票据备注',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_serial_num` (`serial_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='票据备注';

-- ========================================
-- 5. 写卡日志
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_use_card_log (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `meter_num` varchar(32) DEFAULT NULL COMMENT '仪表号',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `card_num` varchar(32) DEFAULT NULL COMMENT '卡号',
    `valve_status` int DEFAULT NULL COMMENT '阀门状态',
    `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `operator_id` varchar(32) DEFAULT NULL COMMENT '操作人',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='写卡日志';

-- ========================================
-- 6. 供热选项
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_options_heat (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `option_key` varchar(64) DEFAULT NULL COMMENT '选项键',
    `option_value` varchar(255) DEFAULT NULL COMMENT '选项值',
    `option_type` varchar(32) DEFAULT NULL COMMENT '选项类型',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='供热选项';

-- ========================================
-- 7. 打印模板
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_print_template (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `name` varchar(64) DEFAULT NULL COMMENT '模板名称',
    `template_content` text DEFAULT NULL COMMENT '模板内容',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打印模板';

-- ========================================
-- 8. 物业选项
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_options (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `option_key` varchar(64) DEFAULT NULL COMMENT '选项键',
    `option_value` varchar(255) DEFAULT NULL COMMENT '选项值',
    `option_type` varchar(32) DEFAULT NULL COMMENT '选项类型',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物业选项';

-- ========================================
-- 9. 收费标准
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_standard (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(64) DEFAULT NULL COMMENT '标准名称',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `cycles` int DEFAULT NULL COMMENT '收费周期',
    `generate_rule` varchar(32) DEFAULT NULL COMMENT '生成规则',
    `step_type` varchar(32) DEFAULT NULL COMMENT '阶梯类型',
    `statistics_type` varchar(5) DEFAULT NULL COMMENT '统计方式',
    `step_maxgrade` int DEFAULT NULL COMMENT '阶梯最大级数',
    `is_step2` tinyint DEFAULT NULL COMMENT '是否启用第二阶梯',
    `step2_type` varchar(32) DEFAULT NULL COMMENT '阶梯二类型',
    `step2_maxgrade` int DEFAULT NULL COMMENT '阶梯二最大级数',
    `is_latefee` tinyint DEFAULT NULL COMMENT '是否启用滞纳金',
    `latefee_startdate` datetime DEFAULT NULL COMMENT '滞纳金开始日期',
    `latefee_type` varchar(32) DEFAULT NULL COMMENT '滞纳金类型',
    `latefee_startdays` int DEFAULT NULL COMMENT '滞纳金开始天数',
    `latefee_formula` varchar(255) DEFAULT NULL COMMENT '滞纳金公式',
    `is_limited` tinyint DEFAULT NULL COMMENT '是否限购',
    `limited_type` varchar(32) DEFAULT NULL COMMENT '限购方式',
    `limited_cond` varchar(32) DEFAULT NULL COMMENT '限购条件',
    `limited_times` int DEFAULT NULL COMMENT '限购次数',
    `limited_money` decimal(18,4) DEFAULT NULL COMMENT '限购金额',
    `limited_single_money` decimal(18,4) DEFAULT NULL COMMENT '单次购买最大金额',
    `standard_price` decimal(18,4) DEFAULT NULL COMMENT '基本单价',
    `money_formula` varchar(255) DEFAULT NULL COMMENT '金额公式',
    `max_money` decimal(18,4) DEFAULT NULL COMMENT '最大金额',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_item_code` (`item_code`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收费标准';

-- ========================================
-- 10. 收费标准价格阶梯
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_standard_price (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `standard_id` varchar(32) DEFAULT NULL COMMENT '收费标准ID',
    `level` int DEFAULT NULL COMMENT '阶梯级别',
    `min_qty` decimal(18,4) DEFAULT NULL COMMENT '最小数量',
    `max_qty` decimal(18,4) DEFAULT NULL COMMENT '最大数量',
    `price` decimal(18,4) DEFAULT NULL COMMENT '单价',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_standard_id` (`standard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收费标准价格阶梯';

-- ========================================
-- 11. 费目
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_expense_item (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `item_code` varchar(32) DEFAULT NULL COMMENT '项目编号',
    `item_name` varchar(64) DEFAULT NULL COMMENT '项目名称',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `is_show` tinyint DEFAULT NULL COMMENT '在票据是否长显',
    `is_printmonth` tinyint DEFAULT NULL COMMENT '打印是否显示月',
    `price_precision` int DEFAULT NULL COMMENT '单价精度',
    `qty_precision` int DEFAULT NULL COMMENT '数量精度',
    `money_precision` int DEFAULT NULL COMMENT '金额精度',
    `is_integer` tinyint DEFAULT NULL COMMENT '是否取整',
    `precision_type` varchar(32) DEFAULT NULL COMMENT '金额小数计算类型',
    `start_pos` int DEFAULT NULL COMMENT '开始位数',
    `sum_precision` int DEFAULT NULL COMMENT '费项合计精度',
    `change_cycle` int DEFAULT NULL COMMENT '起始周期改变',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_item_code` (`item_code`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费目';

-- ========================================
-- 12. 房屋费用绑定
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_house_expense (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `standard_id` varchar(32) DEFAULT NULL COMMENT '收费标准ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房屋费用绑定';

-- ========================================
-- 13. 费用明细
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_expense (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `item_name` varchar(64) DEFAULT NULL COMMENT '费项名称',
    `standard_id` varchar(32) DEFAULT NULL COMMENT '收费标准ID',
    `start_date` datetime DEFAULT NULL COMMENT '起收日期',
    `expire_date` datetime DEFAULT NULL COMMENT '截止日期',
    `last_date` datetime DEFAULT NULL COMMENT '最迟缴费日期',
    `last_reading` decimal(18,4) DEFAULT NULL COMMENT '上次读数',
    `this_reading` decimal(18,4) DEFAULT NULL COMMENT '本次读数',
    `qty` int DEFAULT NULL COMMENT '用量/周期数',
    `money` decimal(18,4) DEFAULT 0.0000 COMMENT '金额',
    `standard_price` decimal(18,4) DEFAULT NULL COMMENT '单价',
    `max_price` decimal(18,4) DEFAULT NULL COMMENT '最大单价',
    `price_formula` varchar(255) DEFAULT NULL COMMENT '单价计算公式',
    `trade_times` int DEFAULT NULL COMMENT '购买倍数',
    `max_money` decimal(18,4) DEFAULT NULL COMMENT '最大金额',
    `money_formula` varchar(255) DEFAULT NULL COMMENT '金额计算公式',
    `is_free` tinyint DEFAULT NULL COMMENT '是否免收',
    `reason` varchar(255) DEFAULT NULL COMMENT '原因',
    `preferential` decimal(18,4) DEFAULT 0.0000 COMMENT '优惠金额',
    `deduction` decimal(18,4) DEFAULT 0.0000 COMMENT '抵扣金额',
    `latefee` decimal(18,4) DEFAULT 0.0000 COMMENT '滞纳金',
    `receivable` decimal(18,4) DEFAULT 0.0000 COMMENT '应收金额',
    `paid_in` decimal(18,4) DEFAULT 0.0000 COMMENT '实收金额',
    `final_money` decimal(18,4) DEFAULT 0.0000 COMMENT '费项合并金额',
    `overdue_day` int DEFAULT 0 COMMENT '逾期天数',
    `is_charged` tinyint DEFAULT NULL COMMENT '是否已收费',
    `charged_time` datetime DEFAULT NULL COMMENT '收费时间',
    `record_id` varchar(32) DEFAULT NULL COMMENT '交易记录主表ID',
    `delay_date` datetime DEFAULT NULL COMMENT '延期日期',
    `heat_usage` tinyint DEFAULT NULL COMMENT '暖气使用情况',
    `is_calc` varchar(5) DEFAULT NULL COMMENT '是否计算',
    `is_closed` tinyint DEFAULT NULL COMMENT '是否轧账',
    `year` varchar(10) DEFAULT NULL COMMENT '年份',
    `month` varchar(10) DEFAULT NULL COMMENT '月份',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `parking_space_id` varchar(32) DEFAULT NULL COMMENT '车位ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_is_charged` (`is_charged`),
    KEY `idx_standard_id` (`standard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用明细';

-- ========================================
-- 14. 客户
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_user (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `id_no` varchar(32) DEFAULT NULL COMMENT '身份证号',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户';

-- ========================================
-- 15. 房屋
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_house (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `room_num` varchar(32) DEFAULT NULL COMMENT '房号',
    `building_id` varchar(32) DEFAULT NULL COMMENT '楼宇ID',
    `unit_code` varchar(32) DEFAULT NULL COMMENT '单元编码',
    `area` decimal(18,4) DEFAULT NULL COMMENT '面积',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `is_charged` tinyint DEFAULT NULL COMMENT '是否已收费',
    `is_calc` varchar(5) DEFAULT NULL COMMENT '是否计算',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_building_unit` (`building_id`, `unit_code`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房屋';

-- ========================================
-- 16. 车位
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pm_parking_space (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `parking_code` varchar(32) DEFAULT NULL COMMENT '车位编号',
    `parkinglot_name` varchar(64) DEFAULT NULL COMMENT '停车场名称',
    `area` decimal(18,4) DEFAULT NULL COMMENT '面积',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='车位';

-- ========================================
-- 菜单系统迁移
-- 从 rltk_pro.sys_menu 迁移到 ry-vue.sys_menu
-- ========================================

-- 供热平衡管理 (一级菜单)
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2000, '供热平衡管理', 0, 5, 'thermal', 'Layout', 0, 0, 'M', '0', '0', NULL, 'thermometer', 103, 1, NOW(), '供热平衡管理模块')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 控制策略
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2001, '控制策略', 2000, 1, 'strategy', 'thermal/ht/strategy/index', 0, 0, 'C', '0', '0', 'thermal:ht:strategy:list', 'strategy', 103, 1, NOW(), '控制策略管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 控制指令
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2002, '控制指令', 2000, 2, 'instruction', 'thermal/ht/instruction/index', 0, 0, 'C', '0', '0', 'thermal:ht:instruction:list', 'edit', 103, 1, NOW(), '控制指令管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 报警记录
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2003, '报警记录', 2000, 3, 'alert', 'thermal/ht/alert/index', 0, 0, 'C', '0', '0', 'thermal:ht:alert:list', 'bug', 103, 1, NOW(), '报警记录管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 报修记录
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2004, '报修记录', 2000, 4, 'repair', 'thermal/ht/repair/index', 0, 0, 'C', '0', '0', 'thermal:ht:repair:list', 'tool', 103, 1, NOW(), '报修记录管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 调控任务
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2005, '调控任务', 2000, 5, 'tasks', 'thermal/ht/tasks/index', 0, 0, 'C', '0', '0', 'thermal:ht:tasks:list', 'tree-table', 103, 1, NOW(), '调控任务管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 房屋策略
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2006, '房屋策略', 2000, 6, 'houseStrategy', 'thermal/ht/houseStrategy/index', 0, 0, 'C', '0', '0', 'thermal:ht:houseStrategy:list', 'nested', 103, 1, NOW(), '房屋策略管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 仪表管理 (一级菜单)
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2100, '仪表管理', 0, 6, 'meter', 'Layout', 0, 0, 'M', '0', '0', NULL, 'component', 103, 1, NOW(), '仪表管理模块')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 仪表厂商
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2101, '仪表厂商', 2100, 1, 'vendor', 'thermal/meter/vendor/index', 0, 0, 'C', '0', '0', 'thermal:meter:vendor:list', 'peoples', 103, 1, NOW(), '仪表厂商管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 仪表分类
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2102, '仪表分类', 2100, 2, 'sort', 'thermal/meter/sort/index', 0, 0, 'C', '0', '0', 'thermal:meter:sort:list', 'tree', 103, 1, NOW(), '仪表分类管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 电表档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2103, '电表档案', 2100, 3, 'electric', 'thermal/meter/electric/index', 0, 0, 'C', '0', '0', 'thermal:meter:electric:list', 'form', 103, 1, NOW(), '电表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 水表档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2104, '水表档案', 2100, 4, 'water', 'thermal/meter/water/index', 0, 0, 'C', '0', '0', 'thermal:meter:water:list', 'form', 103, 1, NOW(), '水表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 热力表档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2105, '热力表档案', 2100, 5, 'heat', 'thermal/meter/heat/index', 0, 0, 'C', '0', '0', 'thermal:meter:heat:list', 'form', 103, 1, NOW(), '热力表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 燃气表档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2106, '燃气表档案', 2100, 6, 'gas', 'thermal/meter/gas/index', 0, 0, 'C', '0', '0', 'thermal:meter:gas:list', 'form', 103, 1, NOW(), '燃气表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 集中器档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2107, '集中器档案', 2100, 7, 'centrator', 'thermal/meter/centrator/index', 0, 0, 'C', '0', '0', 'thermal:meter:centrator:list', 'form', 103, 1, NOW(), '集中器档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 温控器档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2108, '温控器档案', 2100, 8, 'tc', 'thermal/meter/tc/index', 0, 0, 'C', '0', '0', 'thermal:meter:tc:list', 'form', 103, 1, NOW(), '温控器档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 阀门档案
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2109, '阀门档案', 2100, 9, 'valve', 'thermal/meter/valve/index', 0, 0, 'C', '0', '0', 'thermal:meter:valve:list', 'form', 103, 1, NOW(), '阀门档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 收费管理 (一级菜单)
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2200, '收费管理', 0, 7, 'charge', 'Layout', 0, 0, 'M', '0', '0', NULL, 'money', 103, 1, NOW(), '收费管理模块')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 日常收费
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2201, '日常收费', 2200, 1, 'singleCharge', 'thermal/property/singleCharge/index', 0, 0, 'C', '0', '0', 'thermal:property:singleCharge:charge', 'guide', 103, 1, NOW(), '日常收费')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 收费标准
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2202, '收费标准', 2200, 2, 'standard', 'thermal/property/standard/index', 0, 0, 'C', '0', '0', 'thermal:property:standard:list', 'documentation', 103, 1, NOW(), '收费标准管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 费目管理
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2203, '费目管理', 2200, 3, 'expenseItem', 'thermal/property/expenseItem/index', 0, 0, 'C', '0', '0', 'thermal:property:expenseItem:list', 'edit', 103, 1, NOW(), '费目管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 费用明细
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2204, '费用明细', 2200, 4, 'expense', 'thermal/property/expense/index', 0, 0, 'C', '0', '0', 'thermal:property:expense:list', 'list', 103, 1, NOW(), '费用明细管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 房屋费用
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2205, '房屋费用', 2200, 5, 'houseExpense', 'thermal/property/houseExpense/index', 0, 0, 'C', '0', '0', 'thermal:property:houseExpense:list', 'nested', 103, 1, NOW(), '房屋费用绑定')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 客户管理
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2206, '客户管理', 2200, 6, 'user', 'thermal/property/user/index', 0, 0, 'C', '0', '0', 'thermal:property:user:list', 'user', 103, 1, NOW(), '客户管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 账户管理
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2207, '账户管理', 2200, 7, 'account', 'thermal/property/account/index', 0, 0, 'C', '0', '0', 'thermal:property:account:list', 'account', 103, 1, NOW(), '个人账户管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 交易记录
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2208, '交易记录', 2200, 8, 'transaction', 'thermal/property/transaction/index', 0, 0, 'C', '0', '0', 'thermal:property:transaction:list', 'clipboard', 103, 1, NOW(), '交易记录管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 票据备注
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2209, '票据备注', 2200, 9, 'billingNotes', 'thermal/property/billingNotes/index', 0, 0, 'C', '0', '0', 'thermal:property:billingNotes:list', 'notebook', 103, 1, NOW(), '票据备注管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 系统选项
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2210, '系统选项', 2200, 10, 'options', 'thermal/property/options/index', 0, 0, 'C', '0', '0', 'thermal:property:options:list', 'setting', 103, 1, NOW(), '系统选项管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 写卡记录
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2211, '写卡记录', 2200, 11, 'useCardLog', 'thermal/property/useCardLog/index', 0, 0, 'C', '0', '0', 'thermal:property:useCardLog:list', 'log', 103, 1, NOW(), '写卡日志管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 房屋变更
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2212, '房屋变更', 2200, 12, 'houseChange', 'thermal/property/houseChange/index', 0, 0, 'C', '0', '0', 'thermal:property:houseChange:list', 'input', 103, 1, NOW(), '房屋入住/迁出管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 打印模板
INSERT INTO `ry-vue`.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2213, '打印模板', 2200, 13, 'printTemplate', 'thermal/property/printTemplate/index', 0, 0, 'C', '0', '0', 'thermal:property:printTemplate:list', 'printer', 103, 1, NOW(), '打印模板管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);
