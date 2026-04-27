-- Phase 3: 仪表模块表迁移
-- 迁移自旧系统仪表模块

-- ========================================
-- 1. 仪表厂商表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_vendor` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(32) DEFAULT NULL COMMENT '厂商编码',
    `name` varchar(32) DEFAULT NULL COMMENT '厂商名称',
    `contacts` varchar(32) DEFAULT NULL COMMENT '厂商联系人',
    `tele` varchar(32) DEFAULT NULL COMMENT '联系人电话',
    `address` varchar(125) DEFAULT NULL COMMENT '厂商地址',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表厂商表';

-- ========================================
-- 2. 仪表分类表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_sort` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(32) DEFAULT NULL COMMENT '编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `model` varchar(32) DEFAULT NULL COMMENT '型号',
    `vendor_id` varchar(32) DEFAULT NULL COMMENT '厂商',
    `is_onecard` tinyint DEFAULT '0' COMMENT '是否一卡通',
    `measure_type` varchar(2) DEFAULT NULL COMMENT '计费模式 0按量 1按金额 2按时间',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_type` varchar(10) NOT NULL COMMENT '仪表类型',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表分类表';

-- ========================================
-- 3. 电表仪表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_electric_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '电表仪表分类ID',
    `msg_type` tinyint(1) DEFAULT NULL COMMENT '通讯方式 1卡式 2远传 3手工抄表',
    `code` varchar(32) DEFAULT NULL COMMENT '电表类型编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '表型号',
    `rated_voltage` varchar(32) DEFAULT NULL COMMENT '额定电压(V)',
    `rated_current` varchar(32) DEFAULT NULL COMMENT '额定电流(A)',
    `voltage_ratio` varchar(32) DEFAULT NULL COMMENT '电压变比',
    `current_ratio` varchar(32) DEFAULT NULL COMMENT '电流变比',
    `load_limit` varchar(32) DEFAULT NULL COMMENT '负荷限制(kw.h)',
    `alarm_value` varchar(32) DEFAULT NULL COMMENT '报警值(kw.h)',
    `display_value` varchar(32) DEFAULT NULL COMMENT '长显报警值(kw.h)',
    `constant` varchar(32) DEFAULT NULL COMMENT '常数(imp/kw.h)',
    `is_enabled` int DEFAULT NULL COMMENT '是否启用',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_num_required` tinyint(1) DEFAULT NULL COMMENT '表号是否必填',
    `max_amount` decimal(18,2) DEFAULT NULL COMMENT '最大购量',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电表仪表档案';

-- ========================================
-- 4. 水表仪表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_water_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '水表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '水表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `msg_type` tinyint(1) DEFAULT NULL COMMENT '通讯方式 1卡式 2远传 3手工抄表',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格(A)',
    `model` varchar(32) DEFAULT NULL COMMENT '表型号',
    `constant` varchar(10) DEFAULT NULL COMMENT '常数(脉冲)',
    `close_val` varchar(32) DEFAULT NULL COMMENT '关阀值',
    `alarm_val` varchar(32) DEFAULT NULL COMMENT '报警值',
    `load_limit` varchar(32) DEFAULT NULL COMMENT '囤积量',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_num_required` tinyint(1) DEFAULT NULL COMMENT '表号是否必填',
    `is_enabled` int DEFAULT '0' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='水表仪表档案';

-- ========================================
-- 5. 热力表仪表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_heat_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='热力表仪表档案';

-- ========================================
-- 6. 燃气表档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_gas_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) DEFAULT NULL COMMENT '燃气表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '燃气表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '燃气表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '燃气表型号',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT NULL COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='燃气表档案表';

-- ========================================
-- 7. 集中器档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_centrator_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    KEY `idx_centrator_name` (`name`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='集中器档案';

-- ========================================
-- 8. 温控器档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_tc_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='温控器档案';

-- ========================================
-- 9. 阀门表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_tc_valve` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    KEY `idx_tc_valve_type` (`id`, `type`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='阀门表档案';

-- ========================================
-- 10. 仪表分配表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_match` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `archive_id` varchar(32) NOT NULL COMMENT '仪表档案ID',
    `company_id` varchar(32) NOT NULL COMMENT '分配公司ID',
    `meter_type` varchar(10) NOT NULL COMMENT '仪表类型',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表分配表';

-- ========================================
-- 11. 公式档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_formula_file` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(64) DEFAULT NULL COMMENT '公式名称',
    `type` varchar(32) DEFAULT NULL COMMENT '公式类型',
    `cformula` varchar(500) DEFAULT NULL COMMENT '中文公式',
    `eformula` varchar(500) DEFAULT NULL COMMENT '英文公式',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` varchar(1) DEFAULT '1' COMMENT '是否启用 0=禁用 1=启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公式档案表';
