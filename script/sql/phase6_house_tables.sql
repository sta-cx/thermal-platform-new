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

-- ========================================
-- 4. 房屋信息表（扩展版）
-- ========================================
-- 如果 pr_house 表已存在（来自 sdkj-init.sql），使用 ALTER TABLE 添加新列。
-- 对于全新安装，下方提供了完整的 CREATE TABLE。

-- ========== ALTER TABLE 增量脚本（在已有 pr_house 表上执行）==========
-- 安全地添加所有新列（IF NOT EXISTS 逻辑通过 MySQL 存储过程或手动执行）

-- 房屋编码
SET @dbname = DATABASE();
SET @tablename = 'pr_house';

-- 通用添加列的存储过程调用（MySQL 8.0 不直接支持 ADD COLUMN IF NOT EXISTS）
-- 以下每条 ALTER TABLE 需要手动忽略已存在列的错误，或直接使用完整 CREATE TABLE

-- ========== 完整 CREATE TABLE（全新安装推荐）==========
DROP PROCEDURE IF EXISTS `create_pr_house_if_not_exists`;
DELIMITER $$
CREATE PROCEDURE `create_pr_house_if_not_exists`()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pr_house'
    ) THEN
        CREATE TABLE `pr_house` (
            `id` varchar(32) NOT NULL COMMENT '主键',
            `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
            `code` varchar(64) DEFAULT NULL COMMENT '房屋编码',
            `room_num` varchar(32) DEFAULT NULL COMMENT '房号',
            `building_id` varchar(32) DEFAULT NULL COMMENT '楼宇ID',
            `building_name` varchar(128) DEFAULT NULL COMMENT '楼宇名称',
            `unit_code` varchar(32) DEFAULT NULL COMMENT '单元编码',
            `floor` int DEFAULT NULL COMMENT '楼层',
            `nfloor_area` decimal(18,4) DEFAULT NULL COMMENT '使用面积',
            `gfloor_area` decimal(18,4) DEFAULT NULL COMMENT '建筑面积',
            `heating_area` decimal(18,4) DEFAULT NULL COMMENT '供热面积',
            `frist_insidearea` decimal(18,4) DEFAULT NULL COMMENT '一楼内面积',
            `second_insidearea` decimal(18,4) DEFAULT NULL COMMENT '二楼内面积',
            `third_insidearea` decimal(18,4) DEFAULT NULL COMMENT '三楼内面积',
            `nature` varchar(32) DEFAULT NULL COMMENT '房屋性质',
            `structure` varchar(32) DEFAULT NULL COMMENT '房屋结构',
            `type` varchar(32) DEFAULT NULL COMMENT '房屋类型',
            `towards` varchar(16) DEFAULT NULL COMMENT '朝向',
            `unit_type` varchar(32) DEFAULT NULL COMMENT '单元类型',
            `unit_price` decimal(18,4) DEFAULT NULL COMMENT '单价',
            `property_term` varchar(32) DEFAULT NULL COMMENT '产权年限',
            `delivery_time` datetime DEFAULT NULL COMMENT '工程交付时间',
            `accept_time` datetime DEFAULT NULL COMMENT '物业验收时间',
            `occupancy_time` datetime DEFAULT NULL COMMENT '入住时间',
            `establish_time` datetime DEFAULT NULL COMMENT '立户时间',
            `address` varchar(255) DEFAULT NULL COMMENT '邮寄地址',
            `decoration_status` varchar(32) DEFAULT NULL COMMENT '装修状态',
            `status` varchar(16) DEFAULT NULL COMMENT '房屋状态',
            `rental_status` varchar(16) DEFAULT NULL COMMENT '出租状态',
            `seq` varchar(32) DEFAULT NULL COMMENT '排序',
            `site_type` varchar(32) DEFAULT NULL COMMENT '位置属性',
            `site_type_old` varchar(32) DEFAULT NULL COMMENT '历史位置',
            `station_type` varchar(32) DEFAULT NULL COMMENT '供热区域属性',
            `preset_angle` decimal(18,4) DEFAULT NULL COMMENT '预设角度',
            `preset_flow_rate` decimal(18,4) DEFAULT NULL COMMENT '预设流量',
            `in_temp` decimal(18,4) DEFAULT NULL COMMENT '进水温度',
            `out_temp` decimal(18,4) DEFAULT NULL COMMENT '出水温度',
            `room_temp` decimal(18,4) DEFAULT NULL COMMENT '室温',
            `valve_open` int DEFAULT NULL COMMENT '阀门开度百分比',
            `cur_flow` decimal(18,4) DEFAULT NULL COMMENT '当前流量',
            `other_code` varchar(64) DEFAULT NULL COMMENT '外部缴费编码',
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
            KEY `idx_building_unit` (`building_id`, `unit_code`),
            KEY `idx_company_org` (`company_id`, `org_id`),
            KEY `idx_room_num` (`room_num`),
            KEY `idx_code` (`code`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房屋信息表';
    END IF;
END$$
DELIMITER ;
CALL `create_pr_house_if_not_exists`();
DROP PROCEDURE IF EXISTS `create_pr_house_if_not_exists`;

-- ========== ALTER TABLE 增量迁移（在已有 sdkj-init.sql 创建的 pr_house 上执行）==========
-- 仅在表已存在但缺少新列时执行。对于全新安装，上面的 CREATE TABLE 已足够。

-- 将原有的 area 列重命名为 heating_area（如果存在 area 列且不存在 heating_area 列）
-- SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pr_house' AND COLUMN_NAME = 'area');
-- SET @col_new_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pr_house' AND COLUMN_NAME = 'heating_area');
-- 只需手动执行: ALTER TABLE pr_house CHANGE COLUMN area heating_area decimal(18,4) DEFAULT NULL COMMENT '供热面积';

-- 添加新列（逐条执行，忽略已存在列的重复错误）
ALTER TABLE `pr_house`
    ADD COLUMN IF NOT EXISTS `code` varchar(64) DEFAULT NULL COMMENT '房屋编码' AFTER `tenant_id`,
    ADD COLUMN IF NOT EXISTS `building_name` varchar(128) DEFAULT NULL COMMENT '楼宇名称' AFTER `building_id`,
    ADD COLUMN IF NOT EXISTS `floor` int DEFAULT NULL COMMENT '楼层' AFTER `unit_code`,
    ADD COLUMN IF NOT EXISTS `nfloor_area` decimal(18,4) DEFAULT NULL COMMENT '使用面积' AFTER `floor`,
    ADD COLUMN IF NOT EXISTS `gfloor_area` decimal(18,4) DEFAULT NULL COMMENT '建筑面积' AFTER `nfloor_area`,
    ADD COLUMN IF NOT EXISTS `heating_area` decimal(18,4) DEFAULT NULL COMMENT '供热面积' AFTER `gfloor_area`,
    ADD COLUMN IF NOT EXISTS `frist_insidearea` decimal(18,4) DEFAULT NULL COMMENT '一楼内面积' AFTER `heating_area`,
    ADD COLUMN IF NOT EXISTS `second_insidearea` decimal(18,4) DEFAULT NULL COMMENT '二楼内面积' AFTER `frist_insidearea`,
    ADD COLUMN IF NOT EXISTS `third_insidearea` decimal(18,4) DEFAULT NULL COMMENT '三楼内面积' AFTER `second_insidearea`,
    ADD COLUMN IF NOT EXISTS `nature` varchar(32) DEFAULT NULL COMMENT '房屋性质' AFTER `third_insidearea`,
    ADD COLUMN IF NOT EXISTS `structure` varchar(32) DEFAULT NULL COMMENT '房屋结构' AFTER `nature`,
    ADD COLUMN IF NOT EXISTS `type` varchar(32) DEFAULT NULL COMMENT '房屋类型' AFTER `structure`,
    ADD COLUMN IF NOT EXISTS `towards` varchar(16) DEFAULT NULL COMMENT '朝向' AFTER `type`,
    ADD COLUMN IF NOT EXISTS `unit_type` varchar(32) DEFAULT NULL COMMENT '单元类型' AFTER `towards`,
    ADD COLUMN IF NOT EXISTS `unit_price` decimal(18,4) DEFAULT NULL COMMENT '单价' AFTER `unit_type`,
    ADD COLUMN IF NOT EXISTS `property_term` varchar(32) DEFAULT NULL COMMENT '产权年限' AFTER `unit_price`,
    ADD COLUMN IF NOT EXISTS `delivery_time` datetime DEFAULT NULL COMMENT '工程交付时间' AFTER `property_term`,
    ADD COLUMN IF NOT EXISTS `accept_time` datetime DEFAULT NULL COMMENT '物业验收时间' AFTER `delivery_time`,
    ADD COLUMN IF NOT EXISTS `occupancy_time` datetime DEFAULT NULL COMMENT '入住时间' AFTER `accept_time`,
    ADD COLUMN IF NOT EXISTS `establish_time` datetime DEFAULT NULL COMMENT '立户时间' AFTER `occupancy_time`,
    ADD COLUMN IF NOT EXISTS `address` varchar(255) DEFAULT NULL COMMENT '邮寄地址' AFTER `establish_time`,
    ADD COLUMN IF NOT EXISTS `decoration_status` varchar(32) DEFAULT NULL COMMENT '装修状态' AFTER `address`,
    ADD COLUMN IF NOT EXISTS `status` varchar(16) DEFAULT NULL COMMENT '房屋状态' AFTER `decoration_status`,
    ADD COLUMN IF NOT EXISTS `rental_status` varchar(16) DEFAULT NULL COMMENT '出租状态' AFTER `status`,
    ADD COLUMN IF NOT EXISTS `seq` varchar(32) DEFAULT NULL COMMENT '排序' AFTER `rental_status`,
    ADD COLUMN IF NOT EXISTS `site_type` varchar(32) DEFAULT NULL COMMENT '位置属性' AFTER `seq`,
    ADD COLUMN IF NOT EXISTS `site_type_old` varchar(32) DEFAULT NULL COMMENT '历史位置' AFTER `site_type`,
    ADD COLUMN IF NOT EXISTS `station_type` varchar(32) DEFAULT NULL COMMENT '供热区域属性' AFTER `site_type_old`,
    ADD COLUMN IF NOT EXISTS `preset_angle` decimal(18,4) DEFAULT NULL COMMENT '预设角度' AFTER `station_type`,
    ADD COLUMN IF NOT EXISTS `preset_flow_rate` decimal(18,4) DEFAULT NULL COMMENT '预设流量' AFTER `preset_angle`,
    ADD COLUMN IF NOT EXISTS `in_temp` decimal(18,4) DEFAULT NULL COMMENT '进水温度' AFTER `preset_flow_rate`,
    ADD COLUMN IF NOT EXISTS `out_temp` decimal(18,4) DEFAULT NULL COMMENT '出水温度' AFTER `in_temp`,
    ADD COLUMN IF NOT EXISTS `room_temp` decimal(18,4) DEFAULT NULL COMMENT '室温' AFTER `out_temp`,
    ADD COLUMN IF NOT EXISTS `valve_open` int DEFAULT NULL COMMENT '阀门开度百分比' AFTER `room_temp`,
    ADD COLUMN IF NOT EXISTS `cur_flow` decimal(18,4) DEFAULT NULL COMMENT '当前流量' AFTER `valve_open`,
    ADD COLUMN IF NOT EXISTS `other_code` varchar(64) DEFAULT NULL COMMENT '外部缴费编码' AFTER `cur_flow`;

-- 添加索引（如果不存在）
-- ALTER TABLE pr_house ADD INDEX IF NOT EXISTS idx_room_num (room_num);
-- ALTER TABLE pr_house ADD INDEX IF NOT EXISTS idx_code (code);
