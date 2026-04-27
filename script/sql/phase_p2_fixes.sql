-- ============================================================
-- P2-T10: 缺失数据库表补充
-- 根据实体类 @TableName 和 Mapper XML 查询补充缺失的核心表
-- ============================================================

-- ----------------------------
-- 1. 微信对账单
-- 实体: PrWechatBill -> pr_wechat_bill
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_wechat_bill (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `bill_date` varchar(20) DEFAULT NULL COMMENT '账单日期',
    `bill_type` varchar(20) DEFAULT NULL COMMENT '账单类型',
    `bill_url` varchar(500) DEFAULT NULL COMMENT '账单下载地址',
    `file_md5` varchar(64) DEFAULT NULL COMMENT '文件MD5',
    `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
    `download_status` tinyint DEFAULT NULL COMMENT '下载状态: 0=未下载 1=已下载',
    `download_time` datetime DEFAULT NULL COMMENT '下载时间',
    `check_status` tinyint DEFAULT NULL COMMENT '对账状态: 0=未对账 1=已对账',
    `check_time` datetime DEFAULT NULL COMMENT '对账时间',
    `total_count` int DEFAULT NULL COMMENT '总笔数',
    `success_count` int DEFAULT NULL COMMENT '成功笔数',
    `diff_count` int DEFAULT NULL COMMENT '差异笔数',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `operator` varchar(40) DEFAULT NULL COMMENT '操作人',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `is_deleted` char(1) DEFAULT '0' COMMENT '删除标志',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_bill_date` (`bill_date`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信对账单';

-- ----------------------------
-- 2. 微信退款记录
-- 实体: PrWechatRefund -> pr_wechat_refund
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_wechat_refund (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `out_trade_no` varchar(64) DEFAULT NULL COMMENT '商户订单号',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付订单号',
    `out_refund_no` varchar(64) DEFAULT NULL COMMENT '商户退款单号',
    `refund_id` varchar(64) DEFAULT NULL COMMENT '微信退款单号',
    `total_fee` decimal(18,2) DEFAULT NULL COMMENT '订单金额',
    `refund_fee` decimal(18,2) DEFAULT NULL COMMENT '退款金额',
    `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
    `refund_status` tinyint DEFAULT NULL COMMENT '退款状态: 0=处理中 1=成功 2=失败',
    `refund_channel` varchar(32) DEFAULT NULL COMMENT '退款渠道',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `open_id` varchar(64) DEFAULT NULL COMMENT '用户标识openId',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `operator` varchar(40) DEFAULT NULL COMMENT '操作人',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_out_trade_no` (`out_trade_no`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信退款记录';

-- ----------------------------
-- 3. 微信用户
-- 实体: PrWechatUser -> pr_wechat_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_wechat_user (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `open_id` varchar(64) DEFAULT NULL COMMENT '微信openId',
    `other_code` varchar(64) DEFAULT NULL COMMENT '其他编码',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `bind_status` tinyint DEFAULT NULL COMMENT '绑定状态: 0=未绑定 1=已绑定',
    `session_key` varchar(255) DEFAULT NULL COMMENT '会话密钥',
    `union_id` varchar(64) DEFAULT NULL COMMENT '微信unionId',
    `is_deleted` char(1) DEFAULT '0' COMMENT '删除标志',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_open_id` (`open_id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户';

-- ----------------------------
-- 4. 微信绑定记录
-- 实体: PrWechatBindRecord -> pr_wechat_bind_record
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_wechat_bind_record (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `heat_pay_code` varchar(64) DEFAULT NULL COMMENT '供热缴费编码',
    `wx_open_id` varchar(64) DEFAULT NULL COMMENT '微信openId',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_wx_open_id` (`wx_open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信绑定记录';

-- ----------------------------
-- 5. 微信对账差异记录
-- 实体: PrReconciliationDiff -> pr_reconciliation_diff
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_reconciliation_diff (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `bill_id` varchar(32) DEFAULT NULL COMMENT '关联账单ID',
    `bill_date` varchar(20) DEFAULT NULL COMMENT '账单日期',
    `out_trade_no` varchar(64) DEFAULT NULL COMMENT '商户订单号',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付流水号',
    `diff_type` varchar(20) DEFAULT NULL COMMENT '差异类型: MISS-漏单 AMOUNT-金额不一致 STATUS-状态不一致',
    `local_amount` varchar(32) DEFAULT NULL COMMENT '本地金额',
    `wechat_amount` varchar(32) DEFAULT NULL COMMENT '微信金额',
    `local_status` varchar(10) DEFAULT NULL COMMENT '本地状态',
    `wechat_status` varchar(10) DEFAULT NULL COMMENT '微信状态',
    `handle_status` varchar(10) DEFAULT '0' COMMENT '处理状态: 0-未处理 1-已处理',
    `handle_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
    `handler` varchar(40) DEFAULT NULL COMMENT '处理人',
    `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
    `company_id` varchar(32) DEFAULT NULL COMMENT '所属公司',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_bill_id` (`bill_id`),
    KEY `idx_out_trade_no` (`out_trade_no`),
    KEY `idx_diff_type` (`diff_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信对账差异记录';

-- ----------------------------
-- 6. 巡检设备
-- 实体: PrInspectionEquipment -> pr_inspection_equipment
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_inspection_equipment (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `equipment_name` varchar(128) DEFAULT NULL COMMENT '设备名称',
    `equipment_code` varchar(64) DEFAULT NULL COMMENT '设备编码',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检设备';

-- ----------------------------
-- 7. 热表抄表数据副本（远传抄表）
-- Mapper XML: PrHeatReadingMapper 中 selectPageListCopy1 查询使用
-- 远传抄表数据拷贝至此表供聚合查询和报表使用
-- ----------------------------
CREATE TABLE IF NOT EXISTS pr_heat_reading_copy1 (
    `id` varchar(36) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `room_num` varchar(32) DEFAULT NULL COMMENT '房号',
    `meter_num` varchar(36) DEFAULT NULL COMMENT '表号',
    `type` varchar(20) DEFAULT NULL COMMENT '类型',
    `in_temperature` decimal(8,2) DEFAULT 0.00 COMMENT '进水温度',
    `out_temperature` decimal(8,2) DEFAULT 0.00 COMMENT '回水温度',
    `now_temperature` decimal(8,2) DEFAULT 0.00 COMMENT '当前温度',
    `setting_status` varchar(20) DEFAULT NULL COMMENT '阀门设定状态',
    `valve_status` varchar(20) DEFAULT NULL COMMENT '阀门当前状态',
    `read_time` datetime DEFAULT NULL COMMENT '抄表时间',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_num` (`room_num`),
    KEY `idx_meter_num` (`meter_num`),
    KEY `idx_read_time` (`read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热表抄表数据副本';
