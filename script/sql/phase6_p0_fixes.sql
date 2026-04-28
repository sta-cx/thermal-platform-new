-- ----------------------------
-- Table structure for ht_strategy_perform
-- 策略执行明细表（策略指令执行记录）
-- ----------------------------
DROP TABLE IF EXISTS `ht_strategy_perform`;
CREATE TABLE `ht_strategy_perform` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `tasks_id` bigint DEFAULT NULL COMMENT '任务ID',
    `command_index` int DEFAULT NULL COMMENT '指令顺序',
    `strategy_id` bigint DEFAULT NULL COMMENT '策略主表ID',
    `strategy_sub_id` bigint DEFAULT NULL COMMENT '策略子表ID（指令数据ID）',
    `instruction_id` bigint DEFAULT NULL COMMENT '指令ID',
    `instruction` varchar(500) DEFAULT NULL COMMENT '指令内容',
    `name` varchar(100) DEFAULT NULL COMMENT '指令名称',
    `type` int DEFAULT NULL COMMENT '指令类型：1=控制命令，2=采集命令',
    `intervall` int DEFAULT NULL COMMENT '执行间隔',
    `unit` int DEFAULT NULL COMMENT '间隔单位：1=分钟，2=小时，3=天',
    `duration` int DEFAULT NULL COMMENT '作用时长（分钟）',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `orderr` int DEFAULT NULL COMMENT '指令执行顺序',
    `xunhuan` int DEFAULT NULL COMMENT '是否循环执行：1=是，0=否',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志：0=存在，2=删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY `idx_tasks_id` (`tasks_id`) USING BTREE,
    KEY `idx_strategy_id` (`strategy_id`) USING BTREE,
    KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='策略执行明细表';

-- ----------------------------
-- Table structure for sys_area
-- 省市区表
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area` (
    `id` varchar(64) NOT NULL COMMENT '主键ID',
    `parent_id` varchar(64) DEFAULT '1' COMMENT '父级ID',
    `parent_ids` varchar(2000) DEFAULT NULL COMMENT '所有父级ID',
    `name` varchar(100) DEFAULT NULL COMMENT '名称',
    `sort` int DEFAULT NULL COMMENT '排序',
    `code` varchar(50) DEFAULT NULL COMMENT '区划代码',
    `code2` varchar(50) DEFAULT NULL COMMENT '备用代码',
    `type` varchar(1) DEFAULT NULL COMMENT '类型：1=省，2=市，3=区县',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志：0=存在，2=删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_parent_id` (`parent_id`) USING BTREE,
    KEY `idx_code` (`code`) USING BTREE,
    KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='省市区表';

-- ----------------------------
-- Records of sys_area (基础省级数据)
-- ----------------------------
INSERT INTO `sys_area` (`id`, `parent_id`, `parent_ids`, `name`, `sort`, `code`, `code2`, `type`, `create_by`, `create_time`, `del_flag`) VALUES
('2', '1', '0,1,', '北京市', 1, '110000', NULL, '1', 'admin', NOW(), '0'),
('3', '1', '0,1,', '天津市', 2, '120000', NULL, '1', 'admin', NOW(), '0'),
('4', '1', '0,1,', '河北省', 3, '130000', NULL, '1', 'admin', NOW(), '0'),
('5', '1', '0,1,', '山西省', 4, '140000', NULL, '1', 'admin', NOW(), '0'),
('6', '1', '0,1,', '内蒙古自治区', 5, '150000', NULL, '1', 'admin', NOW(), '0'),
('7', '1', '0,1,', '辽宁省', 6, '210000', NULL, '1', 'admin', NOW(), '0'),
('8', '1', '0,1,', '吉林省', 7, '220000', NULL, '1', 'admin', NOW(), '0'),
('9', '1', '0,1,', '黑龙江省', 8, '230000', NULL, '1', 'admin', NOW(), '0'),
('10', '1', '0,1,', '上海市', 9, '310000', NULL, '1', 'admin', NOW(), '0'),
('11', '1', '0,1,', '江苏省', 10, '320000', NULL, '1', 'admin', NOW(), '0'),
('12', '1', '0,1,', '浙江省', 11, '330000', NULL, '1', 'admin', NOW(), '0'),
('13', '1', '0,1,', '安徽省', 12, '340000', NULL, '1', 'admin', NOW(), '0'),
('14', '1', '0,1,', '福建省', 13, '350000', NULL, '1', 'admin', NOW(), '0'),
('15', '1', '0,1,', '江西省', 14, '360000', NULL, '1', 'admin', NOW(), '0'),
('16', '1', '0,1,', '山东省', 15, '370000', NULL, '1', 'admin', NOW(), '0'),
('17', '1', '0,1,', '河南省', 16, '410000', NULL, '1', 'admin', NOW(), '0'),
('18', '1', '0,1,', '湖北省', 17, '420000', NULL, '1', 'admin', NOW(), '0'),
('19', '1', '0,1,', '湖南省', 18, '430000', NULL, '1', 'admin', NOW(), '0'),
('20', '1', '0,1,', '广东省', 19, '440000', NULL, '1', 'admin', NOW(), '0'),
('21', '1', '0,1,', '广西壮族自治区', 20, '450000', NULL, '1', 'admin', NOW(), '0'),
('22', '1', '0,1,', '海南省', 21, '460000', NULL, '1', 'admin', NOW(), '0'),
('23', '1', '0,1,', '重庆市', 22, '500000', NULL, '1', 'admin', NOW(), '0'),
('24', '1', '0,1,', '四川省', 23, '510000', NULL, '1', 'admin', NOW(), '0'),
('25', '1', '0,1,', '贵州省', 24, '520000', NULL, '1', 'admin', NOW(), '0'),
('26', '1', '0,1,', '云南省', 25, '530000', NULL, '1', 'admin', NOW(), '0'),
('27', '1', '0,1,', '西藏自治区', 26, '540000', NULL, '1', 'admin', NOW(), '0'),
('28', '1', '0,1,', '陕西省', 27, '610000', NULL, '1', 'admin', NOW(), '0'),
('29', '1', '0,1,', '甘肃省', 28, '620000', NULL, '1', 'admin', NOW(), '0'),
('30', '1', '0,1,', '青海省', 29, '630000', NULL, '1', 'admin', NOW(), '0'),
('31', '1', '0,1,', '宁夏回族自治区', 30, '640000', NULL, '1', 'admin', NOW(), '0'),
('32', '1', '0,1,', '新疆维吾尔自治区', 31, '650000', NULL, '1', 'admin', NOW(), '0'),
('33', '1', '0,1,', '台湾省', 32, '710000', NULL, '1', 'admin', NOW(), '0'),
('34', '1', '0,1,', '香港特别行政区', 33, '810000', NULL, '1', 'admin', NOW(), '0'),
('35', '1', '0,1,', '澳门特别行政区', 34, '820000', NULL, '1', 'admin', NOW(), '0');
