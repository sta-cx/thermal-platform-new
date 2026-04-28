-- ============================================================
-- Phase 7: 缺失表 DDL 补充
-- pr_operate_card_log, pr_expense_log, pr_wechat_order,
-- sys_organization, pr_user_house
-- ============================================================

-- ----------------------------
-- 写卡操作日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_operate_card_log (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `type` varchar(32) DEFAULT NULL COMMENT '操作类型',
    `card_type` varchar(32) DEFAULT NULL COMMENT '卡类型',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='写卡操作日志表';

-- ----------------------------
-- 费用操作日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_expense_log (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `log_type` varchar(32) DEFAULT NULL COMMENT '日志类型',
    `content` text DEFAULT NULL COMMENT '日志内容',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用操作日志表';

-- ----------------------------
-- 微信订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_wechat_order (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信交易号',
    `order_status` tinyint DEFAULT NULL COMMENT '订单状态',
    `total_amount` decimal(18,4) DEFAULT NULL COMMENT '订单金额',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `pay_type` varchar(32) DEFAULT NULL COMMENT '支付类型',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信订单表';

-- ----------------------------
-- 组织架构表（小区/区域管理）
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_organization (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `parent_id` varchar(32) DEFAULT '0' COMMENT '父级ID',
    `name` varchar(128) DEFAULT NULL COMMENT '组织名称',
    `code` varchar(64) DEFAULT NULL COMMENT '组织编码',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID（兼容字段）',
    `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
    `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `sort` int DEFAULT 0 COMMENT '排序',
    `status` tinyint DEFAULT 0 COMMENT '状态（0正常 1停用）',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织架构表';

-- ----------------------------
-- 用户-房屋关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_user_house (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `relation_type` varchar(10) DEFAULT NULL COMMENT '关系类型',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-房屋关联表';

-- ----------------------------
-- 房屋变更日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_house_log (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `change_type` varchar(32) DEFAULT NULL COMMENT '变更类型',
    `change_val` int DEFAULT NULL COMMENT '变更值',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋变更日志表';

-- ----------------------------
-- 交易明细表（采购充值等）
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_transaction_detail (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `transaction_type` varchar(32) DEFAULT NULL COMMENT '交易类型',
    `receivable` decimal(18,4) DEFAULT 0.0000 COMMENT '应收金额',
    `paid_in` decimal(18,4) DEFAULT 0.0000 COMMENT '实收金额',
    `qty` decimal(18,4) DEFAULT 0.0000 COMMENT '用量',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `record_time` datetime DEFAULT NULL COMMENT '交易时间',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易明细表';

-- ----------------------------
-- 导入暂存表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_import_account (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `type` tinyint DEFAULT NULL COMMENT '导入类型(0账户/1交易)',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `amount` decimal(18,4) DEFAULT NULL COMMENT '金额',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导入暂存表';

-- ----------------------------
-- pr_transaction_record_sub 补充字段
-- ----------------------------
ALTER TABLE pr_transaction_record_sub
    ADD COLUMN `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组' AFTER `notes`,
    ADD COLUMN `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码' AFTER `item_group`,
    ADD COLUMN `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID' AFTER `item_code`;
