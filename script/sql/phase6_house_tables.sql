-- Phase 6: 房屋结构模块表 (PrBuilding + PrUnit + PrFamily)
-- 从旧系统迁移楼宇/单元/家庭成员表结构

-- ========================================
-- 1. 楼宇信息表
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_building (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(64) DEFAULT NULL COMMENT '楼宇编码',
    `name` varchar(128) DEFAULT NULL COMMENT '楼宇名称',
    `on_floor` int DEFAULT NULL COMMENT '地上楼层',
    `up_floor` int DEFAULT NULL COMMENT '地下楼层',
    `floor` int DEFAULT NULL COMMENT '总楼层',
    `unit_nums` int DEFAULT NULL COMMENT '总单元数',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `used` varchar(64) DEFAULT NULL COMMENT '用途',
    `delivery_time` datetime DEFAULT NULL COMMENT '交付时间',
    `station_id` varchar(32) DEFAULT NULL COMMENT '热力站ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='楼宇信息表';

-- ========================================
-- 2. 单元信息表
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_unit (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(64) DEFAULT NULL COMMENT '单元编码',
    `name` varchar(128) DEFAULT NULL COMMENT '单元名称',
    `building_id` varchar(32) DEFAULT NULL COMMENT '楼宇ID',
    `on_floor` int DEFAULT NULL COMMENT '地上楼层',
    `heating_area` decimal(18,4) DEFAULT NULL COMMENT '供热面积',
    `up_floor` int DEFAULT NULL COMMENT '地下楼层',
    `floor` int DEFAULT NULL COMMENT '总楼层',
    `site` varchar(255) DEFAULT NULL COMMENT '位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `station_id` varchar(32) DEFAULT NULL COMMENT '热力站ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_building_id` (`building_id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='单元信息表';

-- ========================================
-- 3. 家庭成员信息表
-- ========================================
CREATE TABLE IF NOT EXISTS `ry-vue`.pr_family (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_id_no` varchar(64) DEFAULT NULL COMMENT '客户证件号',
    `name` varchar(64) DEFAULT NULL COMMENT '家庭成员姓名',
    `sex` int DEFAULT NULL COMMENT '性别',
    `contact_addr` varchar(255) DEFAULT NULL COMMENT '联系地址',
    `employer` varchar(128) DEFAULT NULL COMMENT '工作单位',
    `family_id_no` varchar(64) DEFAULT NULL COMMENT '家庭成员证件号',
    `relation_type` varchar(32) DEFAULT NULL COMMENT '与户主关系',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_user_id_no` (`user_id_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家庭成员信息表';
