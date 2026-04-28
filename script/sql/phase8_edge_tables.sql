-- ============================================================
-- Stage 4A: 边缘功能表 DDL
-- pr_notice, pr_scheduling, pr_pet, pr_abnormal_record, pr_strategy
-- ============================================================

-- ----------------------------
-- 通知公告表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_notice (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `title` varchar(255) DEFAULT NULL COMMENT '标题',
    `content` text DEFAULT NULL COMMENT '内容',
    `type` varchar(32) DEFAULT NULL COMMENT '类型',
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
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ----------------------------
-- 排班管理表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_scheduling (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `person_id` varchar(32) DEFAULT NULL COMMENT '人员ID',
    `person_name` varchar(64) DEFAULT NULL COMMENT '人员姓名',
    `work_date` datetime DEFAULT NULL COMMENT '工作日期',
    `shift` varchar(32) DEFAULT NULL COMMENT '班次',
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
    KEY `idx_person_id` (`person_id`),
    KEY `idx_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班管理表';

-- ----------------------------
-- 宠物管理表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_pet (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `pet_name` varchar(64) DEFAULT NULL COMMENT '宠物名称',
    `pet_type` varchar(32) DEFAULT NULL COMMENT '宠物类型',
    `breed` varchar(64) DEFAULT NULL COMMENT '品种',
    `color` varchar(32) DEFAULT NULL COMMENT '颜色',
    `vaccine_status` varchar(10) DEFAULT NULL COMMENT '疫苗状态(0未接种/1已接种)',
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
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物管理表';

-- ----------------------------
-- 异常记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_abnormal_record (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `abnormal_type` varchar(32) DEFAULT NULL COMMENT '异常类型',
    `description` text DEFAULT NULL COMMENT '异常描述',
    `handle_status` varchar(10) DEFAULT '0' COMMENT '处理状态(0待处理/1已处理)',
    `handle_result` varchar(255) DEFAULT NULL COMMENT '处理结果',
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
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常记录表';

-- ----------------------------
-- 物业策略表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_strategy (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(128) DEFAULT NULL COMMENT '策略名称',
    `type` varchar(32) DEFAULT NULL COMMENT '策略类型',
    `content` text DEFAULT NULL COMMENT '策略内容(JSON)',
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
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物业策略表';
