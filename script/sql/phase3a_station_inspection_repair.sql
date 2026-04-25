-- ============================================================
-- Stage 3A: 热力站 + 巡检 + 报修模块 DDL
-- ============================================================

-- ----------------------------
-- 换热站表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_heat_station (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(64) DEFAULT NULL COMMENT '编号',
    `name` varchar(128) DEFAULT NULL COMMENT '换热站名称',
    `type` varchar(32) DEFAULT NULL COMMENT '种类',
    `tel` varchar(32) DEFAULT NULL COMMENT '联系方式',
    `principal` varchar(64) DEFAULT NULL COMMENT '负责人',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `company_name` varchar(128) DEFAULT NULL COMMENT '热力公司名称',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换热站表';

-- ----------------------------
-- 换热站分区表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_heat_station_partition (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `station_id` varchar(32) DEFAULT NULL COMMENT '换热站ID',
    `name` varchar(128) DEFAULT NULL COMMENT '分区名称',
    `tel` varchar(32) DEFAULT NULL COMMENT '联系方式',
    `principal` varchar(64) DEFAULT NULL COMMENT '负责人',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_station_id` (`station_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换热站分区表';

-- ----------------------------
-- 巡检人员表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_inspection_person (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(64) DEFAULT NULL COMMENT '巡检人姓名',
    `phone` varchar(32) DEFAULT NULL COMMENT '巡检人手机号',
    `type` varchar(32) DEFAULT NULL COMMENT '工种',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检人员表';

-- ----------------------------
-- 巡检计划表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_inspection_plan (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(128) DEFAULT NULL COMMENT '计划名称',
    `start_time` time DEFAULT NULL COMMENT '开始时间',
    `end_time` time DEFAULT NULL COMMENT '结束时间',
    `equipment_id` text DEFAULT NULL COMMENT '设备ID集合',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检计划表';

-- ----------------------------
-- 巡检记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_inspection_record (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `plan_id` varchar(32) DEFAULT NULL COMMENT '巡检计划ID',
    `person_id` varchar(32) DEFAULT NULL COMMENT '巡检人员ID',
    `person_name` varchar(64) DEFAULT NULL COMMENT '巡检人员姓名',
    `equipment_id` varchar(32) DEFAULT NULL COMMENT '设备ID',
    `equipment_name` varchar(128) DEFAULT NULL COMMENT '设备名称',
    `result` varchar(32) DEFAULT NULL COMMENT '巡检结果',
    `content` text DEFAULT NULL COMMENT '巡检内容',
    `images` text DEFAULT NULL COMMENT '巡检图片',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检记录表';

-- ----------------------------
-- 维修人员表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_repair_person (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(64) DEFAULT NULL COMMENT '维修人姓名',
    `phone` varchar(32) DEFAULT NULL COMMENT '维修人手机号',
    `type` varchar(32) DEFAULT NULL COMMENT '工种',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修人员表';

-- ----------------------------
-- 报修记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_repair_record (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_id` varchar(32) DEFAULT NULL COMMENT '业主ID',
    `user_name` varchar(64) DEFAULT NULL COMMENT '业主姓名',
    `phone` varchar(32) DEFAULT NULL COMMENT '报修人电话',
    `house_id` varchar(32) DEFAULT NULL COMMENT '业主房屋ID',
    `repair_time` datetime DEFAULT NULL COMMENT '报修时间',
    `create_by_name` varchar(64) DEFAULT NULL COMMENT '接待人',
    `repair_name` varchar(64) DEFAULT NULL COMMENT '报修人',
    `repair_phone` varchar(32) DEFAULT NULL COMMENT '报修人联系电话',
    `repair_room_num` varchar(64) DEFAULT NULL COMMENT '报修房屋',
    `in_user_name` varchar(64) DEFAULT NULL COMMENT '住户联系人',
    `in_phone` varchar(32) DEFAULT NULL COMMENT '住户联系电话',
    `repair_address` varchar(255) DEFAULT NULL COMMENT '报修地址',
    `service_type` varchar(32) DEFAULT NULL COMMENT '服务类型',
    `repair_type` varchar(32) DEFAULT NULL COMMENT '报修类型',
    `repair_content` text DEFAULT NULL COMMENT '报修内容',
    `urgent_type` varchar(32) DEFAULT NULL COMMENT '紧急状况',
    `appoint_time` datetime DEFAULT NULL COMMENT '预约时间',
    `repair_no` varchar(64) DEFAULT NULL COMMENT '报修单号',
    `repair_status` tinyint DEFAULT NULL COMMENT '报修单状态(1待派单/2待确认/3待评价/4撤销作废/5已评价)',
    `is_reject` tinyint DEFAULT NULL COMMENT '是否拒接',
    `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒接原因',
    `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
    `evaluation_time` datetime DEFAULT NULL COMMENT '评价时间',
    `completion_time` datetime DEFAULT NULL COMMENT '完成时间',
    `dispatch_id` varchar(32) DEFAULT NULL COMMENT '派单人ID',
    `dispatch_money` decimal(18,4) DEFAULT NULL COMMENT '派单费用',
    `dispatch_time` datetime DEFAULT NULL COMMENT '派单时间',
    `service_attitude` varchar(10) DEFAULT NULL COMMENT '服务态度评分',
    `service_quality` varchar(10) DEFAULT NULL COMMENT '服务质量评分',
    `service_efficiency` varchar(10) DEFAULT NULL COMMENT '服务效率评分',
    `get_material` varchar(255) DEFAULT NULL COMMENT '领取物料',
    `service_object` varchar(255) DEFAULT NULL COMMENT '维修事物',
    `service_result` varchar(10) DEFAULT NULL COMMENT '完成情况(0已完成/1未完成)',
    `why_failure` varchar(255) DEFAULT NULL COMMENT '失败原因',
    `alert_status` varchar(32) DEFAULT NULL COMMENT '处理结果',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dispatch_id` (`dispatch_id`),
    KEY `idx_repair_no` (`repair_no`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修记录表';
