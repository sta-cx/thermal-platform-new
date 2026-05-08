-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: tenant_000000
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ag_auto_version`
--

DROP TABLE IF EXISTS `ag_auto_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_auto_version` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` varchar(32) DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商自动版本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_auto_version`
--

LOCK TABLES `ag_auto_version` WRITE;
/*!40000 ALTER TABLE `ag_auto_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_auto_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_company_property`
--

DROP TABLE IF EXISTS `ag_company_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_company_property` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `agent_company_id` varchar(64) DEFAULT NULL COMMENT '代理商公司ID',
  `property_company_id` varchar(64) DEFAULT NULL COMMENT '物业公司ID',
  `is_audited` tinyint DEFAULT '0' COMMENT '是否审核 0未审核 1已审核',
  `is_enabled` tinyint DEFAULT '1' COMMENT '是否启用 0未启用 1启用',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商关联物业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_company_property`
--

LOCK TABLES `ag_company_property` WRITE;
/*!40000 ALTER TABLE `ag_company_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_company_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_property_menu`
--

DROP TABLE IF EXISTS `ag_property_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_property_menu` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `menu_id` varchar(64) DEFAULT NULL COMMENT '菜单ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商物业菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_property_menu`
--

LOCK TABLES `ag_property_menu` WRITE;
/*!40000 ALTER TABLE `ag_property_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_property_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_reader_param`
--

DROP TABLE IF EXISTS `ag_reader_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_reader_param` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `code` varchar(64) DEFAULT NULL COMMENT '编码',
  `ic_isstart` tinyint DEFAULT '0' COMMENT 'IC卡是否启用',
  `ic_type` varchar(32) DEFAULT NULL COMMENT 'IC卡类型',
  `ic_port` int DEFAULT NULL COMMENT 'IC卡端口',
  `ic_baud` int DEFAULT NULL COMMENT 'IC卡波特率',
  `dp_isstart` tinyint DEFAULT '0' COMMENT '显示屏是否启用',
  `dp_type` varchar(32) DEFAULT NULL COMMENT '显示屏类型',
  `dp_port` int DEFAULT NULL COMMENT '显示屏端口',
  `dp_baud` int DEFAULT NULL COMMENT '显示屏波特率',
  `id_isstart` tinyint DEFAULT '0' COMMENT '身份证是否启用',
  `id_type` varchar(32) DEFAULT NULL COMMENT '身份证类型',
  `id_port` int DEFAULT NULL COMMENT '身份证端口',
  `id_baud` int DEFAULT NULL COMMENT '身份证波特率',
  `recog_isstart` tinyint DEFAULT '0' COMMENT '识别是否启用',
  `recog_type` varchar(32) DEFAULT NULL COMMENT '识别类型',
  `recog_port` int DEFAULT NULL COMMENT '识别端口',
  `recog_baud` int DEFAULT NULL COMMENT '识别波特率',
  `is_auto_update` tinyint DEFAULT '0' COMMENT '是否自动更新',
  `scan_time` varchar(32) DEFAULT NULL COMMENT '扫描时间',
  `scan_interval` int DEFAULT NULL COMMENT '扫描间隔',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `user_pwd` varchar(64) DEFAULT NULL COMMENT '用户密码',
  `is_read_water` tinyint DEFAULT '0' COMMENT '是否读水表',
  `is_read_ele` tinyint DEFAULT '0' COMMENT '是否读电表',
  `is_receipt_printer` tinyint DEFAULT '0' COMMENT '是否小票打印机',
  `exchange_service_ip` varchar(64) DEFAULT NULL COMMENT '交换服务IP',
  `is_auto_restart` tinyint DEFAULT '0' COMMENT '是否自动重启',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商抄表参数表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_reader_param`
--

LOCK TABLES `ag_reader_param` WRITE;
/*!40000 ALTER TABLE `ag_reader_param` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_reader_param` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_role`
--

DROP TABLE IF EXISTS `ag_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '角色名称',
  `identifying` varchar(32) NOT NULL COMMENT '角色标识',
  `nature` tinyint DEFAULT NULL COMMENT '角色性质',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `role_desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `is_super` tinyint DEFAULT '0' COMMENT '是否超级管理员',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商/物业角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_role`
--

LOCK TABLES `ag_role` WRITE;
/*!40000 ALTER TABLE `ag_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_role_menu`
--

DROP TABLE IF EXISTS `ag_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_role_menu` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `role_id` varchar(32) NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_role_menu`
--

LOCK TABLES `ag_role_menu` WRITE;
/*!40000 ALTER TABLE `ag_role_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_user`
--

DROP TABLE IF EXISTS `ag_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_user` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `user_pwd` varchar(128) DEFAULT NULL COMMENT '密码',
  `idcard` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `nick_name` varchar(64) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `sex` int DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `is_enabled` int DEFAULT '1' COMMENT '是否启用',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `wx_openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
  `wx_number` varchar(64) DEFAULT NULL COMMENT '微信号',
  `qq_number` varchar(64) DEFAULT NULL COMMENT 'QQ号',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `nation` varchar(20) DEFAULT NULL COMMENT '民族',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `native_place` varchar(64) DEFAULT NULL COMMENT '籍贯',
  `nationality` varchar(20) DEFAULT NULL COMMENT '国籍',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `is_realname` int DEFAULT '0' COMMENT '是否实名',
  `id_startdate` datetime DEFAULT NULL COMMENT '证件有效期开始',
  `id_enddate` datetime DEFAULT NULL COMMENT '证件有效期结束',
  `id_department` varchar(64) DEFAULT NULL COMMENT '签发机关',
  `is_super` int DEFAULT '0' COMMENT '是否超级管理员',
  `dept_id` varchar(32) DEFAULT NULL COMMENT '部门ID',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_user`
--

LOCK TABLES `ag_user` WRITE;
/*!40000 ALTER TABLE `ag_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ag_user_role`
--

DROP TABLE IF EXISTS `ag_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ag_user_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `role_id` varchar(32) NOT NULL COMMENT '角色ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代理商用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ag_user_role`
--

LOCK TABLES `ag_user_role` WRITE;
/*!40000 ALTER TABLE `ag_user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `ag_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_alert`
--

DROP TABLE IF EXISTS `ht_alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_alert` (
  `id` bigint NOT NULL COMMENT '主键',
  `building_id` bigint DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `meter_id` bigint DEFAULT NULL,
  `is_charged` tinyint DEFAULT NULL,
  `valve` tinyint DEFAULT NULL,
  `in_temp` decimal(6,2) DEFAULT NULL,
  `out_temp` decimal(6,2) DEFAULT NULL,
  `room_temp` decimal(6,2) DEFAULT NULL,
  `alert_type` tinyint NOT NULL,
  `alert_time` datetime NOT NULL,
  `alert_status` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `in_maintenance` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `meter_id` (`meter_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报警表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_alert`
--

LOCK TABLES `ht_alert` WRITE;
/*!40000 ALTER TABLE `ht_alert` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_alert` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_house_strategy`
--

DROP TABLE IF EXISTS `ht_house_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_house_strategy` (
  `id` bigint NOT NULL COMMENT '主键',
  `tasks_id` bigint DEFAULT NULL,
  `type` tinyint NOT NULL,
  `strategy_id` bigint DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `remark` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `adjust_basis` tinyint DEFAULT NULL,
  `stride` tinyint DEFAULT NULL,
  `priority` tinyint DEFAULT NULL,
  `intervall` int DEFAULT '30',
  `number` tinyint DEFAULT '0',
  `valve_min` tinyint DEFAULT '0',
  `valve_max` tinyint DEFAULT '100',
  `in_temp` decimal(6,2) DEFAULT '0.00',
  `in_temp_deviation` tinyint DEFAULT '0',
  `out_temp` decimal(6,2) DEFAULT '0.00',
  `out_temp_deviation` tinyint DEFAULT NULL,
  `is_in_temp_alert_min` decimal(6,2) DEFAULT '0.00',
  `is_in_temp_alert_max` decimal(6,2) DEFAULT '100.00',
  `room_temp` decimal(6,2) DEFAULT '0.00',
  `room_temp_deviation` tinyint DEFAULT NULL,
  `scope_type` tinyint DEFAULT NULL,
  `is_report_police` tinyint DEFAULT '0',
  `report_police_number` tinyint DEFAULT NULL,
  `is_manage_police` tinyint DEFAULT '0',
  `manage_police_number` tinyint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `preset_angle` tinyint DEFAULT NULL,
  `preset_flow_rate` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='单元房屋策略表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_house_strategy`
--

LOCK TABLES `ht_house_strategy` WRITE;
/*!40000 ALTER TABLE `ht_house_strategy` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_house_strategy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_instruction`
--

DROP TABLE IF EXISTS `ht_instruction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_instruction` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指令名称',
  `type` tinyint NOT NULL COMMENT '指令类型',
  `instruction` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令内容',
  `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='控制指令表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_instruction`
--

LOCK TABLES `ht_instruction` WRITE;
/*!40000 ALTER TABLE `ht_instruction` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_instruction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_repair`
--

DROP TABLE IF EXISTS `ht_repair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_repair` (
  `id` bigint NOT NULL COMMENT '主键',
  `building_id` bigint DEFAULT NULL,
  `building_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `unit_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `room_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_id` bigint DEFAULT NULL,
  `meter_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_charged` tinyint DEFAULT '0',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `valve` tinyint DEFAULT NULL,
  `in_temp` decimal(6,2) DEFAULT NULL,
  `out_temp` decimal(6,2) DEFAULT NULL,
  `room_temp` decimal(6,2) DEFAULT NULL,
  `repair_type` tinyint NOT NULL,
  `repair_time` datetime NOT NULL,
  `repair_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `repair_status` tinyint DEFAULT '0',
  `repair_result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `org_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_delete` tinyint DEFAULT '0',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `in_maintenance` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `dispatch_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `dispatch_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `dispatch_time` datetime DEFAULT NULL,
  `repair_no` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `fix_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `fix_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `fix_time` datetime DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `appoint_time` datetime DEFAULT NULL,
  `urgent_type` tinyint DEFAULT NULL,
  `service_type` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `meter_id` (`meter_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_repair`
--

LOCK TABLES `ht_repair` WRITE;
/*!40000 ALTER TABLE `ht_repair` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_repair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_scope`
--

DROP TABLE IF EXISTS `ht_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_scope` (
  `id` bigint NOT NULL COMMENT '主键',
  `tasks_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `building_id` bigint DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `house_id` bigint DEFAULT NULL,
  `meter_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `meter_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `concentrator_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `imei` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `is_special` tinyint(1) DEFAULT '0',
  `dtu_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='控制范围表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_scope`
--

LOCK TABLES `ht_scope` WRITE;
/*!40000 ALTER TABLE `ht_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_scope_dtu`
--

DROP TABLE IF EXISTS `ht_scope_dtu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_scope_dtu` (
  `id` bigint NOT NULL COMMENT '主键',
  `tasks_id` bigint DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `meter_arc_code` varchar(64) DEFAULT NULL COMMENT '档案编号',
  `dtu_num` varchar(64) DEFAULT NULL COMMENT 'DTU编号',
  `chan_nums` varchar(255) DEFAULT NULL COMMENT '通道号集合',
  `concentrator_code` varchar(64) DEFAULT NULL COMMENT '集中器编号',
  `status` int DEFAULT '0' COMMENT '执行状态',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='DTU控制范围表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_scope_dtu`
--

LOCK TABLES `ht_scope_dtu` WRITE;
/*!40000 ALTER TABLE `ht_scope_dtu` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_scope_dtu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_strategy`
--

DROP TABLE IF EXISTS `ht_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_strategy` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '策略名称',
  `type` tinyint DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `adjust_basis` int DEFAULT NULL,
  `stride` int DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `intervall` int DEFAULT NULL,
  `number` int DEFAULT NULL,
  `valve_min` int DEFAULT NULL,
  `valve_max` int DEFAULT NULL,
  `in_temp` decimal(12,4) DEFAULT NULL,
  `in_temp_deviation` int DEFAULT NULL,
  `out_temp` decimal(12,4) DEFAULT NULL,
  `out_temp_deviation` int DEFAULT NULL,
  `cur_flow` decimal(12,4) DEFAULT NULL,
  `cur_flow_deviation` int DEFAULT NULL,
  `is_in_temp_alert_min` decimal(12,4) DEFAULT NULL,
  `is_in_temp_alert_max` decimal(12,4) DEFAULT NULL,
  `room_temp` decimal(12,4) DEFAULT NULL,
  `room_temp_deviation` int DEFAULT NULL,
  `scope_type` int DEFAULT NULL,
  `is_report_police` int DEFAULT NULL,
  `report_police_number` int DEFAULT NULL,
  `is_manage_police` int DEFAULT NULL,
  `manage_police_number` int DEFAULT NULL,
  `coefficient` decimal(12,4) DEFAULT NULL,
  `bianhxs` decimal(12,4) DEFAULT NULL,
  `dinghxs` decimal(12,4) DEFAULT NULL,
  `dihxs` decimal(12,4) DEFAULT NULL,
  `zhonghxs` decimal(12,4) DEFAULT NULL,
  `bulixs` decimal(12,4) DEFAULT NULL,
  `biandinghxs` decimal(12,4) DEFAULT NULL,
  `biandihxs` decimal(12,4) DEFAULT NULL,
  `gdhxs` decimal(12,4) DEFAULT NULL,
  `sbgnhxs` decimal(12,4) DEFAULT NULL,
  `xbghxs` decimal(12,4) DEFAULT NULL,
  `zchxs` decimal(12,4) DEFAULT NULL,
  `is_xishu` int DEFAULT NULL,
  `is_fzxishu` int DEFAULT NULL,
  `heat_supply_index` int DEFAULT NULL,
  `temperature_difference` int DEFAULT NULL,
  `heat_difference` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='控制策略主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_strategy`
--

LOCK TABLES `ht_strategy` WRITE;
/*!40000 ALTER TABLE `ht_strategy` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_strategy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_strategy_perform`
--

DROP TABLE IF EXISTS `ht_strategy_perform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_strategy_perform` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tasks_id` bigint DEFAULT NULL COMMENT '任务ID',
  `command_index` int DEFAULT NULL COMMENT '指令顺序',
  `strategy_id` bigint DEFAULT NULL COMMENT '策略主表ID',
  `strategy_sub_id` bigint DEFAULT NULL COMMENT '策略子表ID（指令数据ID）',
  `instruction_id` bigint DEFAULT NULL COMMENT '指令ID',
  `instruction` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令内容',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令名称',
  `type` int DEFAULT NULL COMMENT '指令类型：1=控制命令，2=采集命令',
  `intervall` int DEFAULT NULL COMMENT '执行间隔',
  `unit` int DEFAULT NULL COMMENT '间隔单位：1=分钟，2=小时，3=天',
  `duration` int DEFAULT NULL COMMENT '作用时长（分钟）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `orderr` int DEFAULT NULL COMMENT '指令执行顺序',
  `xunhuan` int DEFAULT NULL COMMENT '是否循环执行：1=是，0=否',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志：0=存在，2=删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tasks_id` (`tasks_id`) USING BTREE,
  KEY `idx_strategy_id` (`strategy_id`) USING BTREE,
  KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='策略执行明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_strategy_perform`
--

LOCK TABLES `ht_strategy_perform` WRITE;
/*!40000 ALTER TABLE `ht_strategy_perform` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_strategy_perform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_strategy_sub`
--

DROP TABLE IF EXISTS `ht_strategy_sub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_strategy_sub` (
  `id` bigint NOT NULL COMMENT '主键',
  `strategy_id` bigint DEFAULT NULL,
  `instruction_id` bigint DEFAULT NULL,
  `sort` int DEFAULT NULL,
  `valve_angle` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='控制策略子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_strategy_sub`
--

LOCK TABLES `ht_strategy_sub` WRITE;
/*!40000 ALTER TABLE `ht_strategy_sub` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_strategy_sub` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_task_setting_log`
--

DROP TABLE IF EXISTS `ht_task_setting_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_task_setting_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `task_id` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `scope_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务执行设定历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_task_setting_log`
--

LOCK TABLES `ht_task_setting_log` WRITE;
/*!40000 ALTER TABLE `ht_task_setting_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_task_setting_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_task_setting_log_item`
--

DROP TABLE IF EXISTS `ht_task_setting_log_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_task_setting_log_item` (
  `id` bigint NOT NULL COMMENT '主键',
  `main_id` bigint DEFAULT NULL,
  `scope_id` bigint DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `old_angle` int DEFAULT NULL,
  `new_angle` int DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务执行设定历史子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_task_setting_log_item`
--

LOCK TABLES `ht_task_setting_log_item` WRITE;
/*!40000 ALTER TABLE `ht_task_setting_log_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_task_setting_log_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_tasks`
--

DROP TABLE IF EXISTS `ht_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_tasks` (
  `id` bigint NOT NULL COMMENT '主键',
  `cu_group_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `type` tinyint NOT NULL COMMENT '执行方式',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间表达式',
  `strategy_id` bigint DEFAULT NULL,
  `priority` tinyint DEFAULT NULL,
  `status` tinyint NOT NULL COMMENT '0停止 1启动',
  `number` tinyint NOT NULL DEFAULT '0',
  `last_time` datetime DEFAULT NULL,
  `total` tinyint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `adjust_basis` tinyint NOT NULL,
  `scope_type` tinyint NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `bean_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `days` tinyint DEFAULT NULL,
  `nums` tinyint DEFAULT NULL,
  `standard` tinyint DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `execution_time` int DEFAULT '0',
  `out_temp_pj` decimal(6,2) DEFAULT NULL,
  `is_use_report_rate` tinyint(1) NOT NULL DEFAULT '0',
  `report_rate` int NOT NULL DEFAULT '0',
  `is_use_first_control` tinyint(1) NOT NULL DEFAULT '0',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='控制任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_tasks`
--

LOCK TABLES `ht_tasks` WRITE;
/*!40000 ALTER TABLE `ht_tasks` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_tasks_perform`
--

DROP TABLE IF EXISTS `ht_tasks_perform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_tasks_perform` (
  `id` bigint NOT NULL COMMENT '主键',
  `instruction_id` bigint DEFAULT NULL,
  `group_id` bigint DEFAULT NULL,
  `strategy_id` bigint DEFAULT NULL,
  `command_index` int DEFAULT NULL,
  `orderr` int DEFAULT NULL,
  `instruction_type` int DEFAULT NULL,
  `instruction` int DEFAULT NULL,
  `number` int DEFAULT NULL,
  `intervall` int DEFAULT NULL,
  `fore_start` int DEFAULT NULL,
  `unit` int DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `status` int DEFAULT NULL,
  `instruction_status` int DEFAULT NULL,
  `send_time` datetime DEFAULT NULL,
  `tasks_id` bigint DEFAULT NULL,
  `in_temp` decimal(6,2) DEFAULT NULL,
  `out_temp` decimal(6,2) DEFAULT NULL,
  `room_temp` decimal(6,2) DEFAULT NULL,
  `valve_open` int DEFAULT NULL,
  `imei` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `dtu_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `out_temp_pj` decimal(6,2) DEFAULT NULL,
  `ref_heat` decimal(10,2) DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_meter_id` (`meter_id`),
  KEY `idx_tasks_id` (`tasks_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调控执行记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_tasks_perform`
--

LOCK TABLES `ht_tasks_perform` WRITE;
/*!40000 ALTER TABLE `ht_tasks_perform` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_tasks_perform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_tasks_perform_last`
--

DROP TABLE IF EXISTS `ht_tasks_perform_last`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_tasks_perform_last` (
  `id` bigint NOT NULL COMMENT '主键',
  `tasks_id` bigint DEFAULT NULL,
  `instruction_id` bigint DEFAULT NULL,
  `orderr` int DEFAULT NULL COMMENT '指令顺序',
  `instruction_type` int DEFAULT NULL COMMENT '指令类型',
  `instruction` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令内容',
  `number` int DEFAULT NULL COMMENT '指令执行次数',
  `intervall` int DEFAULT NULL COMMENT '间隔',
  `unit` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '单位',
  `duration` tinyint(1) DEFAULT NULL COMMENT '作用时长',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `concentrator_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编号',
  `tele_product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电信产品ID',
  `tele_api_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电信平台Master-APIkey',
  `tele_app_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电信平台AppKey',
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表号',
  `meter_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '档案编号',
  `status` int DEFAULT NULL COMMENT '执行状态',
  `instruction_status` int DEFAULT NULL COMMENT '执行结果',
  `send_time` datetime DEFAULT NULL COMMENT '指令发送时间',
  `fore_start` int DEFAULT NULL COMMENT '是否开始新的循环',
  `in_temp` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temp` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
  `room_temp` decimal(10,2) DEFAULT NULL COMMENT '室温',
  `valve_open` int DEFAULT NULL COMMENT '当前开度',
  `imei` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备IMEI',
  `dtu_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU编号',
  `chan_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `out_temp_pj` decimal(10,2) DEFAULT NULL COMMENT '平均回水温度',
  `cur_flow_compute` decimal(10,2) DEFAULT NULL COMMENT '计算流量',
  `ref_heat` decimal(10,2) DEFAULT NULL COMMENT '参考热量',
  `group_id` bigint DEFAULT NULL,
  `strategy_id` bigint DEFAULT NULL,
  `command_index` int DEFAULT NULL COMMENT '任务指令顺序号',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_tasks_id` (`tasks_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调控任务上次执行记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_tasks_perform_last`
--

LOCK TABLES `ht_tasks_perform_last` WRITE;
/*!40000 ALTER TABLE `ht_tasks_perform_last` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_tasks_perform_last` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ht_tasks_perform_ls`
--

DROP TABLE IF EXISTS `ht_tasks_perform_ls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ht_tasks_perform_ls` (
  `id` bigint NOT NULL COMMENT '主键',
  `tasks_id` bigint DEFAULT NULL,
  `instruction_id` bigint DEFAULT NULL,
  `orderr` int DEFAULT NULL COMMENT '指令顺序',
  `instruction_type` int DEFAULT NULL COMMENT '指令类型',
  `instruction` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令内容',
  `number` int DEFAULT NULL COMMENT '指令执行次数',
  `intervall` int DEFAULT NULL COMMENT '间隔(上报周期需要)',
  `unit` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '单位 01分钟 02小时 03天',
  `duration` tinyint(1) DEFAULT NULL COMMENT '作用时长',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `concentrator_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编号',
  `tele_product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电信产品ID',
  `tele_api_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电信平台Master-APIkey',
  `tele_app_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电信平台AppKey',
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表号',
  `meter_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '档案编号',
  `status` int DEFAULT NULL COMMENT '执行状态',
  `in_temp` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temp` decimal(10,2) DEFAULT NULL COMMENT '当前回水温度',
  `room_temp` decimal(10,2) DEFAULT NULL COMMENT '当前室温',
  `valve_open` int DEFAULT NULL COMMENT '当前开度',
  `instruction_status` int DEFAULT NULL COMMENT '执行结果',
  `send_time` datetime DEFAULT NULL COMMENT '指令发送时间',
  `is_type` int DEFAULT NULL COMMENT '类型标识',
  `alert_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '报警类型',
  `imei` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备IMEI号码',
  `dtu_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU编号',
  `chan_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `fore_start` int DEFAULT NULL COMMENT '是否开始新的循环 1是 0否',
  `out_temp_pj` decimal(10,2) DEFAULT NULL COMMENT '平均回水温度',
  `cur_flow_compute` decimal(10,2) DEFAULT NULL COMMENT '计算流量',
  `ref_heat` decimal(10,2) DEFAULT NULL COMMENT '参考热量',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_tasks_id` (`tasks_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调控任务执行历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ht_tasks_perform_ls`
--

LOCK TABLES `ht_tasks_perform_ls` WRITE;
/*!40000 ALTER TABLE `ht_tasks_perform_ls` DISABLE KEYS */;
/*!40000 ALTER TABLE `ht_tasks_perform_ls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_centrator_archive`
--

DROP TABLE IF EXISTS `mt_centrator_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_centrator_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_action` tinyint(1) DEFAULT NULL,
  `install_site` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_enabled` int DEFAULT '1',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='集中器档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_centrator_archive`
--

LOCK TABLES `mt_centrator_archive` WRITE;
/*!40000 ALTER TABLE `mt_centrator_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_centrator_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_electric_archive`
--

DROP TABLE IF EXISTS `mt_electric_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_electric_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `msg_type` tinyint DEFAULT NULL COMMENT '通讯方式 1=卡式 2=远传 3=手工抄表',
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `rated_voltage` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `rated_current` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `voltage_ratio` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `current_ratio` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `load_limit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `alarm_value` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `display_value` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `constant` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_enabled` int DEFAULT NULL,
  `seq` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_num_required` tinyint(1) DEFAULT NULL,
  `max_amount` decimal(18,2) DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电表仪表档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_electric_archive`
--

LOCK TABLES `mt_electric_archive` WRITE;
/*!40000 ALTER TABLE `mt_electric_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_electric_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_formula_file`
--

DROP TABLE IF EXISTS `mt_formula_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_formula_file` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公式名称',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公式类型',
  `cformula` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '中文公式',
  `eformula` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '英文公式',
  `seq` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '排序',
  `is_enabled` tinyint DEFAULT NULL COMMENT '是否启用',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公式档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_formula_file`
--

LOCK TABLES `mt_formula_file` WRITE;
/*!40000 ALTER TABLE `mt_formula_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_formula_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_gas_archive`
--

DROP TABLE IF EXISTS `mt_gas_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_gas_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_enabled` int DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='燃气表档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_gas_archive`
--

LOCK TABLES `mt_gas_archive` WRITE;
/*!40000 ALTER TABLE `mt_gas_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_gas_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_heat_archive`
--

DROP TABLE IF EXISTS `mt_heat_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_heat_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_action` tinyint(1) DEFAULT NULL,
  `install_site` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_enabled` int DEFAULT '1',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='热力表仪表档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_heat_archive`
--

LOCK TABLES `mt_heat_archive` WRITE;
/*!40000 ALTER TABLE `mt_heat_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_heat_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_meter_match`
--

DROP TABLE IF EXISTS `mt_meter_match`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_meter_match` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='仪表分配关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_meter_match`
--

LOCK TABLES `mt_meter_match` WRITE;
/*!40000 ALTER TABLE `mt_meter_match` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_meter_match` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_meter_sort`
--

DROP TABLE IF EXISTS `mt_meter_sort`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_meter_sort` (
  `id` bigint NOT NULL COMMENT '主键',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `vendor_id` bigint DEFAULT NULL,
  `is_onecard` tinyint DEFAULT '0',
  `measure_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='仪表分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_meter_sort`
--

LOCK TABLES `mt_meter_sort` WRITE;
/*!40000 ALTER TABLE `mt_meter_sort` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_meter_sort` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_meter_vendor`
--

DROP TABLE IF EXISTS `mt_meter_vendor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_meter_vendor` (
  `id` bigint NOT NULL COMMENT '主键',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '厂商编码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '厂商名称',
  `contacts` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '厂商联系人',
  `tele` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系人电话',
  `address` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '厂商地址',
  `seq` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '排序',
  `is_enabled` int DEFAULT '1' COMMENT '是否启用',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='仪表厂商表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_meter_vendor`
--

LOCK TABLES `mt_meter_vendor` WRITE;
/*!40000 ALTER TABLE `mt_meter_vendor` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_meter_vendor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_tc_archive`
--

DROP TABLE IF EXISTS `mt_tc_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_tc_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_action` tinyint(1) DEFAULT NULL,
  `install_site` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_enabled` int DEFAULT '1',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='温控器档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_tc_archive`
--

LOCK TABLES `mt_tc_archive` WRITE;
/*!40000 ALTER TABLE `mt_tc_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_tc_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_tc_valve`
--

DROP TABLE IF EXISTS `mt_tc_valve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_tc_valve` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_action` tinyint(1) DEFAULT NULL,
  `install_site` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_enabled` int DEFAULT '1',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='阀门档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_tc_valve`
--

LOCK TABLES `mt_tc_valve` WRITE;
/*!40000 ALTER TABLE `mt_tc_valve` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_tc_valve` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mt_water_archive`
--

DROP TABLE IF EXISTS `mt_water_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mt_water_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `sort_id` bigint DEFAULT NULL,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `msg_type` tinyint(1) DEFAULT NULL,
  `specification` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `constant` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `close_val` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `alarm_val` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `load_limit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `meter_num_required` tinyint(1) DEFAULT NULL,
  `is_enabled` int DEFAULT '0',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='水表仪表档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mt_water_archive`
--

LOCK TABLES `mt_water_archive` WRITE;
/*!40000 ALTER TABLE `mt_water_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `mt_water_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pm_parking_space`
--

DROP TABLE IF EXISTS `pm_parking_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pm_parking_space` (
  `id` bigint NOT NULL COMMENT '主键',
  `parking_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `parkinglot_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `area` decimal(18,4) DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
  `standard_price` decimal(12,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车位';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pm_parking_space`
--

LOCK TABLES `pm_parking_space` WRITE;
/*!40000 ALTER TABLE `pm_parking_space` DISABLE KEYS */;
/*!40000 ALTER TABLE `pm_parking_space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_abnormal_record`
--

DROP TABLE IF EXISTS `pr_abnormal_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_abnormal_record` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `meter_id` bigint DEFAULT NULL,
  `abnormal_type` varchar(32) DEFAULT NULL COMMENT '异常类型',
  `description` text COMMENT '异常描述',
  `handle_status` varchar(10) DEFAULT '0' COMMENT '处理状态(0待处理/1已处理)',
  `handle_result` varchar(255) DEFAULT NULL COMMENT '处理结果',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_id` (`meter_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='异常记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_abnormal_record`
--

LOCK TABLES `pr_abnormal_record` WRITE;
/*!40000 ALTER TABLE `pr_abnormal_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_abnormal_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_account_balance`
--

DROP TABLE IF EXISTS `pr_account_balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_account_balance` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `balance` decimal(18,4) DEFAULT '0.0000',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_house` (`user_id`,`house_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='个人账户余额';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_account_balance`
--

LOCK TABLES `pr_account_balance` WRITE;
/*!40000 ALTER TABLE `pr_account_balance` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_account_balance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_approval`
--

DROP TABLE IF EXISTS `pr_approval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_approval` (
  `id` bigint NOT NULL COMMENT '主键',
  `no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '流程编号',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '申请类型',
  `approval_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '申请人',
  `approval_time` datetime DEFAULT NULL COMMENT '申请时间',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '任务名称',
  `preferential_type` int DEFAULT NULL COMMENT '减免类型',
  `preferential` decimal(10,2) DEFAULT NULL COMMENT '减免金额',
  `preferential_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '减免原因',
  `approval_link` int DEFAULT NULL COMMENT '审批环节',
  `approval_type` int DEFAULT NULL COMMENT '审批类型',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `approval_users` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '审批人(当前环节)',
  `approval_users_all` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '所有审批人',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_approval_user` (`approval_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_approval`
--

LOCK TABLES `pr_approval` WRITE;
/*!40000 ALTER TABLE `pr_approval` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_approval` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_approval_opinion`
--

DROP TABLE IF EXISTS `pr_approval_opinion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_approval_opinion` (
  `id` bigint NOT NULL COMMENT '主键',
  `approval_id` bigint DEFAULT NULL,
  `approval_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '审批人',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `opinions` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '意见',
  `approval_status` int DEFAULT NULL COMMENT '审批状态 0待审批 1通过 2驳回',
  `approval_link` int DEFAULT NULL COMMENT '审批环节',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_approval_id` (`approval_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批意见表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_approval_opinion`
--

LOCK TABLES `pr_approval_opinion` WRITE;
/*!40000 ALTER TABLE `pr_approval_opinion` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_approval_opinion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_approval_sub`
--

DROP TABLE IF EXISTS `pr_approval_sub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_approval_sub` (
  `id` bigint NOT NULL COMMENT '主键',
  `approval_id` bigint DEFAULT NULL,
  `expense_id` bigint DEFAULT NULL,
  `org_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `building_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `room_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
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
  `warehouse_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `warehouse_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `material_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `material_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `material_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `material_use` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_approval_id` (`approval_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_approval_sub`
--

LOCK TABLES `pr_approval_sub` WRITE;
/*!40000 ALTER TABLE `pr_approval_sub` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_approval_sub` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_billing_notes`
--

DROP TABLE IF EXISTS `pr_billing_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_billing_notes` (
  `id` bigint NOT NULL COMMENT '主键',
  `serial_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serial_num` (`serial_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='票据备注';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_billing_notes`
--

LOCK TABLES `pr_billing_notes` WRITE;
/*!40000 ALTER TABLE `pr_billing_notes` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_billing_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_building`
--

DROP TABLE IF EXISTS `pr_building`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_building` (
  `id` bigint NOT NULL COMMENT '主键',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '楼宇编码',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '楼宇名称',
  `on_floor` int DEFAULT NULL COMMENT '地上楼层',
  `up_floor` int DEFAULT NULL COMMENT '地下楼层',
  `floor` int DEFAULT NULL COMMENT '总楼层',
  `unit_nums` int DEFAULT NULL COMMENT '总单元数',
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '排序',
  `used` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用途',
  `delivery_time` datetime DEFAULT NULL COMMENT '交付时间',
  `station_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='楼宇信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_building`
--

LOCK TABLES `pr_building` WRITE;
/*!40000 ALTER TABLE `pr_building` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_building` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_data_grant`
--

DROP TABLE IF EXISTS `pr_data_grant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_data_grant` (
  `id` bigint NOT NULL COMMENT '主键（雪花ID）',
  `user_id` bigint NOT NULL COMMENT '用户ID（对应主库 sys_user.user_id）',
  `company_id` varchar(32) NOT NULL COMMENT '公司ID',
  `org_id` varchar(32) NOT NULL COMMENT '小区ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改者',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(125) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_data_grant`
--

LOCK TABLES `pr_data_grant` WRITE;
/*!40000 ALTER TABLE `pr_data_grant` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_data_grant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_expense`
--

DROP TABLE IF EXISTS `pr_expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_expense` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `expire_date` datetime DEFAULT NULL,
  `last_date` datetime DEFAULT NULL,
  `last_reading` decimal(18,4) DEFAULT NULL,
  `this_reading` decimal(18,4) DEFAULT NULL,
  `qty` int DEFAULT NULL,
  `money` decimal(18,4) DEFAULT '0.0000',
  `standard_price` decimal(18,4) DEFAULT NULL,
  `max_price` decimal(18,4) DEFAULT NULL,
  `price_formula` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `trade_times` int DEFAULT NULL,
  `max_money` decimal(18,4) DEFAULT NULL,
  `money_formula` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_free` tinyint DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `preferential` decimal(18,4) DEFAULT '0.0000',
  `deduction` decimal(18,4) DEFAULT '0.0000',
  `latefee` decimal(18,4) DEFAULT '0.0000',
  `receivable` decimal(18,4) DEFAULT '0.0000',
  `paid_in` decimal(18,4) DEFAULT '0.0000',
  `final_money` decimal(18,4) DEFAULT '0.0000',
  `overdue_day` int DEFAULT '0',
  `is_charged` tinyint DEFAULT NULL,
  `charged_time` datetime DEFAULT NULL,
  `record_id` bigint DEFAULT NULL,
  `delay_date` datetime DEFAULT NULL,
  `heat_usage` tinyint DEFAULT NULL,
  `is_calc` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_closed` tinyint DEFAULT NULL,
  `year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `parking_space_id` bigint DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_is_charged` (`is_charged`),
  KEY `idx_standard_id` (`standard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='费用明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_expense`
--

LOCK TABLES `pr_expense` WRITE;
/*!40000 ALTER TABLE `pr_expense` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_expense_item`
--

DROP TABLE IF EXISTS `pr_expense_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_expense_item` (
  `id` bigint NOT NULL COMMENT '主键',
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_show` tinyint DEFAULT NULL,
  `is_printmonth` tinyint DEFAULT NULL,
  `price_precision` int DEFAULT NULL,
  `qty_precision` int DEFAULT NULL,
  `money_precision` int DEFAULT NULL,
  `is_integer` tinyint DEFAULT NULL,
  `precision_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `start_pos` int DEFAULT NULL,
  `sum_precision` int DEFAULT NULL,
  `change_cycle` int DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `num` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_item_code` (`item_code`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='费目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_expense_item`
--

LOCK TABLES `pr_expense_item` WRITE;
/*!40000 ALTER TABLE `pr_expense_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_expense_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_expense_log`
--

DROP TABLE IF EXISTS `pr_expense_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_expense_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `log_type` varchar(32) DEFAULT NULL COMMENT '日志类型',
  `content` text COMMENT '日志内容',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='费用操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_expense_log`
--

LOCK TABLES `pr_expense_log` WRITE;
/*!40000 ALTER TABLE `pr_expense_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_expense_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_family`
--

DROP TABLE IF EXISTS `pr_family`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_family` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '客户证件号',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '家庭成员姓名',
  `sex` int DEFAULT NULL COMMENT '性别',
  `contact_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系地址',
  `employer` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '工作单位',
  `family_id_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '家庭成员证件号',
  `relation_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '与户主关系',
  `house_id` bigint DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_user_id_no` (`user_id_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='家庭成员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_family`
--

LOCK TABLES `pr_family` WRITE;
/*!40000 ALTER TABLE `pr_family` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_archive`
--

DROP TABLE IF EXISTS `pr_heat_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `org_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区名称',
  `building_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '楼栋名称',
  `house_id` bigint DEFAULT NULL,
  `room_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房号',
  `archive_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `imei` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `line_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '线路号',
  `specification` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '规格',
  `model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '型号',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `standard_id` bigint DEFAULT NULL,
  `standard_price` decimal(10,2) DEFAULT '0.00' COMMENT '标准单价',
  `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
  `diff_temperature` decimal(10,2) DEFAULT NULL COMMENT '温差',
  `setting_temperature` decimal(10,2) DEFAULT '0.00' COMMENT '设定温度',
  `setting_status` int DEFAULT NULL COMMENT '设定状态',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `is_opened` int DEFAULT '0' COMMENT '是否开户 0否1是',
  `opened_time` datetime DEFAULT NULL COMMENT '开户时间',
  `his_money` decimal(10,2) DEFAULT '0.00' COMMENT '历史金额',
  `total_used` decimal(10,2) DEFAULT '0.00' COMMENT '累计用量',
  `current_reading` decimal(10,2) DEFAULT '0.00' COMMENT '当前读数',
  `total_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计金额',
  `total_recharge` decimal(10,2) DEFAULT '0.00' COMMENT '累计充值',
  `current_balance` decimal(10,2) DEFAULT '0.00' COMMENT '当前余额',
  `pay_degrees` decimal(10,2) DEFAULT '0.00' COMMENT '已购量',
  `start_reading` int DEFAULT NULL COMMENT '起始读数',
  `total_heat` decimal(10,2) DEFAULT NULL COMMENT '累计热量',
  `total_flow` decimal(10,2) DEFAULT NULL COMMENT '累计流量',
  `total_worktime` decimal(10,2) DEFAULT NULL COMMENT '累计工作时间',
  `trade_times` int DEFAULT '0' COMMENT '购买倍数',
  `hoard_limit` decimal(10,2) DEFAULT NULL COMMENT '囤积限值',
  `alarm_value` decimal(10,2) DEFAULT NULL COMMENT '报警值',
  `close_value` decimal(10,2) DEFAULT NULL COMMENT '关阀值',
  `is_steps` int DEFAULT '0' COMMENT '是否阶梯 0否1是',
  `measurement` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '计量方式',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '类型',
  `command` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令',
  `valve_opening` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门开度',
  `command_time` datetime DEFAULT NULL COMMENT '指令下发时间',
  `return_time` datetime DEFAULT NULL COMMENT '指令返回时间',
  `command_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '指令状态',
  `is_expense` int DEFAULT '0' COMMENT '是否产生费用 0否1是',
  `is_notify` int DEFAULT '0' COMMENT '是否通知 0否1是',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `is_print` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否打印',
  `print_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '打印类型',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房屋热表配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_archive`
--

LOCK TABLES `pr_heat_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_command_unit_valve_archive`
--

DROP TABLE IF EXISTS `pr_heat_command_unit_valve_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_command_unit_valve_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `unit_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `setting_status` int DEFAULT NULL COMMENT '设定状态',
  `actual_status` int DEFAULT NULL COMMENT '实际状态',
  `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
  `voltage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电压',
  `valve_time` datetime DEFAULT NULL COMMENT '阀门时间',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `reporting_interval` int DEFAULT NULL COMMENT '上报间隔',
  `total_degree` int DEFAULT NULL COMMENT '总度数',
  `residue_degree` int DEFAULT NULL COMMENT '剩余度数',
  `interval_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '间隔单位',
  `valid_time` int DEFAULT NULL COMMENT '有效时间',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `dtu_num_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号状态',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `dtu_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU状态',
  `caliber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '口径',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_unit_id` (`unit_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='单元控制阀门配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_command_unit_valve_archive`
--

LOCK TABLES `pr_heat_command_unit_valve_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_command_unit_valve_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_command_unit_valve_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_command_valve_archive`
--

DROP TABLE IF EXISTS `pr_heat_command_valve_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_command_valve_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `house_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `setting_status` int DEFAULT NULL COMMENT '设定状态',
  `actual_status` int DEFAULT NULL COMMENT '实际状态',
  `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
  `voltage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电压',
  `valve_time` datetime DEFAULT NULL COMMENT '阀门时间',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `reporting_interval` int DEFAULT NULL COMMENT '上报间隔',
  `total_degree` int DEFAULT NULL COMMENT '总度数',
  `residue_degree` int DEFAULT NULL COMMENT '剩余度数',
  `interval_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '间隔单位',
  `valid_time` int DEFAULT NULL COMMENT '有效时间',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `dtu_num_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号状态',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `dtu_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU状态',
  `trade_times` int DEFAULT '0' COMMENT '购买倍数',
  `is_open` int DEFAULT '0' COMMENT '是否开户 0否1是',
  `caliber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '口径',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='户间控制阀门配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_command_valve_archive`
--

LOCK TABLES `pr_heat_command_valve_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_command_valve_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_command_valve_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_daily`
--

DROP TABLE IF EXISTS `pr_heat_daily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_daily` (
  `id` bigint NOT NULL COMMENT '主键',
  `meter_id` bigint DEFAULT NULL,
  `meter_num` varchar(36) DEFAULT NULL COMMENT '表号',
  `card_num` varchar(36) DEFAULT NULL COMMENT '卡号',
  `meter_arc_code` varchar(36) DEFAULT NULL COMMENT '热表档案编号',
  `start_time` datetime DEFAULT NULL COMMENT '上次抄表时间',
  `start_reading` decimal(12,2) DEFAULT '0.00' COMMENT '上次读数',
  `read_time` datetime DEFAULT NULL COMMENT '本次抄表时间',
  `current_reading` decimal(12,2) DEFAULT '0.00' COMMENT '当前读数',
  `qty` decimal(12,2) DEFAULT '0.00' COMMENT '日用量',
  `in_temperature` decimal(8,2) DEFAULT '0.00' COMMENT '进水温度',
  `out_temperature` decimal(8,2) DEFAULT '0.00' COMMENT '回水温度',
  `diff_temperature` decimal(8,2) DEFAULT '0.00' COMMENT '供回水温差',
  `setting_status` varchar(20) DEFAULT NULL COMMENT '阀门设定状态',
  `valve_status` varchar(20) DEFAULT NULL COMMENT '阀门当前状态',
  `voltage` varchar(20) DEFAULT NULL COMMENT '电压',
  `standard_price` decimal(12,6) DEFAULT '0.000000' COMMENT '基本单价',
  `total_money` decimal(12,2) DEFAULT '0.00' COMMENT '总金额',
  `daily_date` datetime DEFAULT NULL COMMENT '日表所属日期',
  `is_calc` int DEFAULT NULL COMMENT '是否计算用量',
  `calc_date` datetime DEFAULT NULL COMMENT '费用结算时间',
  `house_id` bigint DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='热表日记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_daily`
--

LOCK TABLES `pr_heat_daily` WRITE;
/*!40000 ALTER TABLE `pr_heat_daily` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_daily` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_dtu_archive`
--

DROP TABLE IF EXISTS `pr_heat_dtu_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_dtu_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `install_site` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '状态',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IP地址',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `channel_num` int DEFAULT NULL COMMENT '通道数量',
  `channel_num_time` datetime DEFAULT NULL COMMENT '通道数量更新时间',
  `latest_time` datetime DEFAULT NULL COMMENT '最新数据时间',
  `last_time` datetime DEFAULT NULL COMMENT '最后在线时间',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_dtu_num` (`dtu_num`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='DTU采集器配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_dtu_archive`
--

LOCK TABLES `pr_heat_dtu_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_dtu_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_dtu_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_hot_archive`
--

DROP TABLE IF EXISTS `pr_heat_hot_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_hot_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `hoard_limit` decimal(10,2) DEFAULT NULL COMMENT '囤积限值',
  `alarm_value` decimal(10,2) DEFAULT NULL COMMENT '报警值',
  `close_value` bigint DEFAULT NULL COMMENT '关阀值',
  `measurement` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '计量方式',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `standard_id` bigint DEFAULT NULL,
  `standard_price` decimal(10,2) DEFAULT '0.00' COMMENT '标准单价',
  `is_steps` int DEFAULT '0' COMMENT '是否阶梯 0否1是',
  `start_reading` decimal(10,2) DEFAULT NULL COMMENT '起始读数',
  `current_reading` decimal(10,2) DEFAULT '0.00' COMMENT '当前读数',
  `total_used` decimal(10,2) DEFAULT '0.00' COMMENT '累计用量',
  `trade_times` int DEFAULT '0' COMMENT '购买倍数',
  `his_money` decimal(10,2) DEFAULT '0.00' COMMENT '历史金额',
  `total_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计金额',
  `total_recharge` decimal(10,2) DEFAULT '0.00' COMMENT '累计充值',
  `current_balance` decimal(10,2) DEFAULT '0.00' COMMENT '当前余额',
  `pay_degrees` decimal(10,2) DEFAULT '0.00' COMMENT '已购量',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `total_flow` decimal(10,2) DEFAULT '0.00' COMMENT '累计流量',
  `cur_flow` decimal(10,2) DEFAULT '0.00' COMMENT '当前流量',
  `total_worktime` decimal(10,2) DEFAULT '0.00' COMMENT '累计工作时间',
  `valve_time` datetime DEFAULT NULL COMMENT '阀门时间',
  `status1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '状态1',
  `status2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '状态2',
  `thermal_power` decimal(10,2) DEFAULT '0.00' COMMENT '热功率',
  `in_temperature` decimal(10,2) DEFAULT '0.00' COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT '0.00' COMMENT '回水温度',
  `voltage` decimal(10,2) DEFAULT '0.00' COMMENT '电压',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `cell_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电池状态',
  `is_opened` int DEFAULT '0' COMMENT '是否开户 0否1是',
  `opened_time` datetime DEFAULT NULL COMMENT '开户时间',
  `is_expense` int DEFAULT '0' COMMENT '是否产生费用 0否1是',
  `is_notify` int DEFAULT '0' COMMENT '是否通知 0否1是',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `house_id` bigint DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `dtu_num_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号状态',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `dtu_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU状态',
  `install_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装方式',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房屋热量表配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_hot_archive`
--

LOCK TABLES `pr_heat_hot_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_hot_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_hot_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_month`
--

DROP TABLE IF EXISTS `pr_heat_month`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_month` (
  `id` bigint NOT NULL COMMENT '主键',
  `meter_num` varchar(36) DEFAULT NULL COMMENT '表号',
  `card_num` varchar(36) DEFAULT NULL COMMENT '卡号',
  `meter_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(36) DEFAULT NULL COMMENT '热表档案编号',
  `start_time` datetime DEFAULT NULL COMMENT '上次抄表时间',
  `read_time` datetime DEFAULT NULL COMMENT '本次抄表时间',
  `start_reading` decimal(12,2) DEFAULT '0.00' COMMENT '上次读数',
  `current_reading` decimal(12,2) DEFAULT '0.00' COMMENT '本次读数',
  `qty` decimal(12,2) DEFAULT '0.00' COMMENT '当月用量',
  `record_ym` varchar(10) DEFAULT NULL COMMENT '结算月份/年份(如202008)',
  `statistics_type` varchar(20) DEFAULT NULL COMMENT '统计方式',
  `standard_id` bigint DEFAULT NULL,
  `total_money` decimal(12,2) DEFAULT '0.00' COMMENT '总金额',
  `is_audit` int DEFAULT NULL COMMENT '是否审批',
  `is_hiscalc` int DEFAULT NULL COMMENT '是否参与历史累计',
  `current_balance` decimal(12,2) DEFAULT '0.00' COMMENT '当前余额',
  `recharge_money` decimal(12,2) DEFAULT '0.00' COMMENT '当月充值金额',
  `pay_degrees` decimal(12,2) DEFAULT '0.00' COMMENT '缴至读数',
  `current_arrearage` decimal(12,2) DEFAULT '0.00' COMMENT '本月欠费',
  `add_arrearage` decimal(12,2) DEFAULT '0.00' COMMENT '累计欠费',
  `add_advances` decimal(12,2) DEFAULT '0.00' COMMENT '累计预收',
  `house_id` bigint DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='热表月记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_month`
--

LOCK TABLES `pr_heat_month` WRITE;
/*!40000 ALTER TABLE `pr_heat_month` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_month` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_reading`
--

DROP TABLE IF EXISTS `pr_heat_reading`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_reading` (
  `id` bigint NOT NULL COMMENT '主键',
  `manu_id` varchar(36) DEFAULT NULL COMMENT '厂家反馈的产品代码',
  `meter_arc_code` varchar(36) DEFAULT NULL COMMENT '热表档案编号',
  `meter_num` varchar(36) DEFAULT NULL COMMENT '表号',
  `card_num` varchar(36) DEFAULT NULL COMMENT '卡号',
  `device_id` varchar(36) DEFAULT NULL COMMENT '平台给终端分配的设备ID',
  `in_temperature` decimal(8,2) DEFAULT '0.00' COMMENT '进水温度',
  `out_temperature` decimal(8,2) DEFAULT '0.00' COMMENT '回水温度',
  `diff_temperature` decimal(8,2) DEFAULT '0.00' COMMENT '供回水温差',
  `total_heat` decimal(12,2) DEFAULT '0.00' COMMENT '累积热量',
  `total_flow` decimal(12,2) DEFAULT '0.00' COMMENT '累积流量',
  `flow_rate` decimal(12,2) DEFAULT '0.00' COMMENT '瞬时流量',
  `total_worktime` decimal(12,2) DEFAULT '0.00' COMMENT '累积工作时间',
  `water_press` decimal(8,2) DEFAULT '0.00' COMMENT '水压',
  `reverse_flow` decimal(12,2) DEFAULT '0.00' COMMENT '反向流量',
  `setting_status` varchar(20) DEFAULT NULL COMMENT '阀门设定状态',
  `valve_status` varchar(20) DEFAULT NULL COMMENT '阀门当前状态',
  `power_state` varchar(20) DEFAULT NULL COMMENT '电源状态',
  `attack_status` varchar(20) DEFAULT NULL COMMENT '异常状态',
  `read_time` datetime DEFAULT NULL COMMENT '抄表时间',
  `st` varchar(50) DEFAULT NULL COMMENT '状态字',
  `is_used` int DEFAULT NULL COMMENT '是否使用',
  `is_valid` int DEFAULT NULL COMMENT '是否有效(是否系统内表号)',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `csq` varchar(20) DEFAULT NULL COMMENT '信号',
  `voltage` varchar(20) DEFAULT NULL COMMENT '电压',
  `humi` varchar(20) DEFAULT NULL COMMENT '湿度',
  `temperature` decimal(8,2) DEFAULT '0.00' COMMENT '温度',
  `heat_power` decimal(12,2) DEFAULT '0.00' COMMENT '热功率',
  `status1` varchar(50) DEFAULT NULL COMMENT '热表状态1',
  `status2` varchar(50) DEFAULT NULL COMMENT '热表状态2',
  `meter_serial` int DEFAULT NULL COMMENT '子表序号',
  `user_set_temp` decimal(8,2) DEFAULT NULL COMMENT '用户设定温度',
  `room_temp` decimal(8,2) DEFAULT NULL COMMENT '室内温度',
  `avg_temp` decimal(8,2) DEFAULT NULL COMMENT '平均温度',
  `valve_model` varchar(50) DEFAULT NULL COMMENT '阀门型号',
  `cold_flg` int DEFAULT NULL COMMENT '冷水标志',
  `wkq_lock` int DEFAULT NULL COMMENT '温控器锁定',
  `temp_low` int DEFAULT NULL COMMENT '温度下限',
  `temp_high` int DEFAULT NULL COMMENT '温度上限',
  `work_time` int DEFAULT NULL COMMENT '工作时间',
  `total_open_time` int DEFAULT NULL COMMENT '总开启时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_meter_num` (`meter_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='热表抄表记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_reading`
--

LOCK TABLES `pr_heat_reading` WRITE;
/*!40000 ALTER TABLE `pr_heat_reading` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_reading` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_real_data`
--

DROP TABLE IF EXISTS `pr_heat_real_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_real_data` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `building_id` bigint DEFAULT NULL,
  `building_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '楼栋名称',
  `org_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区名称',
  `room_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房间号',
  `unit_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '单元编号',
  `floor` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '楼层',
  `station_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '换热站名称',
  `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度(阀门)',
  `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度(阀门)',
  `setting_status` int DEFAULT NULL COMMENT '设定开度',
  `valve_status` int DEFAULT NULL COMMENT '实际开度',
  `create_time` datetime DEFAULT NULL COMMENT '阀门更新时间',
  `rb_create_time` datetime DEFAULT NULL COMMENT '热表更新时间',
  `total_heat` decimal(12,2) DEFAULT NULL COMMENT '累计热量',
  `total_flow` decimal(12,2) DEFAULT NULL COMMENT '累计流量',
  `total_worktime` decimal(12,2) DEFAULT NULL COMMENT '累计时长',
  `attack_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门设备状态',
  `meter_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门编号',
  `rb_status1` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '热表状态1',
  `rb_status2` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '热表状态2',
  `rb_attack_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '热表设备状态',
  `rb_meter_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '热表编号',
  `rb_voltage` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '热表电量',
  `voltage` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门电量',
  `rb_in` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '进水温度(热表)',
  `rb_out` decimal(10,2) DEFAULT NULL COMMENT '回水温度(热表)',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `rbin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `rbout` decimal(12,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='热力实时数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_real_data`
--

LOCK TABLES `pr_heat_real_data` WRITE;
/*!40000 ALTER TABLE `pr_heat_real_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_real_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_station`
--

DROP TABLE IF EXISTS `pr_heat_station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_station` (
  `id` bigint NOT NULL COMMENT '主键',
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
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='换热站表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_station`
--

LOCK TABLES `pr_heat_station` WRITE;
/*!40000 ALTER TABLE `pr_heat_station` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_station` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_station_org`
--

DROP TABLE IF EXISTS `pr_heat_station_org`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_station_org` (
  `station_id` bigint DEFAULT NULL,
  `org_id` varchar(32) NOT NULL COMMENT '所属小区ID',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  KEY `idx_station_org_station` (`station_id`,`org_id`),
  KEY `idx_station_org_org` (`org_id`,`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='换热站所属小区表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_station_org`
--

LOCK TABLES `pr_heat_station_org` WRITE;
/*!40000 ALTER TABLE `pr_heat_station_org` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_station_org` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_station_partition`
--

DROP TABLE IF EXISTS `pr_heat_station_partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_station_partition` (
  `id` bigint NOT NULL COMMENT '主键',
  `station_id` bigint DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL COMMENT '分区名称',
  `tel` varchar(32) DEFAULT NULL COMMENT '联系方式',
  `principal` varchar(64) DEFAULT NULL COMMENT '负责人',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `seq` varchar(32) DEFAULT NULL COMMENT '排序',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_station_id` (`station_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='换热站分区表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_station_partition`
--

LOCK TABLES `pr_heat_station_partition` WRITE;
/*!40000 ALTER TABLE `pr_heat_station_partition` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_station_partition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_temp_archive`
--

DROP TABLE IF EXISTS `pr_heat_temp_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_temp_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `temper` decimal(10,2) DEFAULT NULL COMMENT '温度',
  `humidity` decimal(10,2) DEFAULT NULL COMMENT '湿度',
  `voltage` decimal(10,2) DEFAULT NULL COMMENT '电压',
  `signal_strength` decimal(10,2) DEFAULT NULL COMMENT '信号强度',
  `collect_time` datetime DEFAULT NULL COMMENT '采集时间',
  `reporting_interval` int DEFAULT NULL COMMENT '上报间隔',
  `interval_unit` int DEFAULT NULL COMMENT '间隔单位',
  `valid_time` int DEFAULT NULL COMMENT '有效时间',
  `collect_interval` int DEFAULT NULL COMMENT '采集间隔',
  `collect_unit` int DEFAULT NULL COMMENT '采集单位',
  `collect_num` int DEFAULT NULL COMMENT '采集次数',
  `mov_place` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '移动位置',
  `report_number` int DEFAULT NULL COMMENT '上报总数',
  `report_succ_num` int DEFAULT NULL COMMENT '上报成功数',
  `report_time` datetime DEFAULT NULL COMMENT '上报时间',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `house_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='温采器配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_temp_archive`
--

LOCK TABLES `pr_heat_temp_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_temp_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_temp_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_unit_hot_archive`
--

DROP TABLE IF EXISTS `pr_heat_unit_hot_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_unit_hot_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `hoard_limit` decimal(10,2) DEFAULT NULL COMMENT '囤积限值',
  `alarm_value` decimal(10,2) DEFAULT NULL COMMENT '报警值',
  `close_value` bigint DEFAULT NULL COMMENT '关阀值',
  `measurement` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '计量方式',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `standard_id` bigint DEFAULT NULL,
  `standard_price` decimal(10,2) DEFAULT '0.00' COMMENT '标准单价',
  `is_steps` int DEFAULT '0' COMMENT '是否阶梯 0否1是',
  `start_reading` decimal(10,2) DEFAULT NULL COMMENT '起始读数',
  `current_reading` decimal(10,2) DEFAULT '0.00' COMMENT '当前读数',
  `total_used` decimal(10,2) DEFAULT '0.00' COMMENT '累计用量',
  `trade_times` int DEFAULT '0' COMMENT '购买倍数',
  `his_money` decimal(10,2) DEFAULT '0.00' COMMENT '历史金额',
  `total_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计金额',
  `total_recharge` decimal(10,2) DEFAULT '0.00' COMMENT '累计充值',
  `current_balance` decimal(10,2) DEFAULT '0.00' COMMENT '当前余额',
  `pay_degrees` decimal(10,2) DEFAULT '0.00' COMMENT '已购量',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `total_flow` decimal(10,2) DEFAULT '0.00' COMMENT '累计流量',
  `cur_flow` decimal(10,2) DEFAULT '0.00' COMMENT '当前流量',
  `total_worktime` decimal(10,2) DEFAULT '0.00' COMMENT '累计工作时间',
  `valve_time` datetime DEFAULT NULL COMMENT '阀门时间',
  `status1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '状态1',
  `status2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '状态2',
  `thermal_power` decimal(10,2) DEFAULT '0.00' COMMENT '热功率',
  `in_temperature` decimal(10,2) DEFAULT '0.00' COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT '0.00' COMMENT '回水温度',
  `voltage` decimal(10,2) DEFAULT '0.00' COMMENT '电压',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `cell_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电池状态',
  `is_opened` int DEFAULT '0' COMMENT '是否开户 0否1是',
  `opened_time` datetime DEFAULT NULL COMMENT '开户时间',
  `is_expense` int DEFAULT '0' COMMENT '是否产生费用 0否1是',
  `is_notify` int DEFAULT '0' COMMENT '是否通知 0否1是',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `unit_id` bigint DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `dtu_num_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号状态',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `dtu_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU状态',
  `install_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装方式',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_unit_id` (`unit_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='单元热表配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_unit_hot_archive`
--

LOCK TABLES `pr_heat_unit_hot_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_unit_hot_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_unit_hot_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_unit_valve_archive`
--

DROP TABLE IF EXISTS `pr_heat_unit_valve_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_unit_valve_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `unit_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `setting_status` int DEFAULT NULL COMMENT '设定状态',
  `actual_status` int DEFAULT NULL COMMENT '实际状态',
  `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
  `voltage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电压',
  `valve_time` datetime DEFAULT NULL COMMENT '阀门时间',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `reporting_interval` int DEFAULT NULL COMMENT '上报间隔',
  `total_degree` int DEFAULT NULL COMMENT '总度数',
  `residue_degree` int DEFAULT NULL COMMENT '剩余度数',
  `interval_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '间隔单位',
  `valid_time` int DEFAULT NULL COMMENT '有效时间',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `dtu_num_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号状态',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `dtu_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU状态',
  `caliber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '口径',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_unit_id` (`unit_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='单元阀门配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_unit_valve_archive`
--

LOCK TABLES `pr_heat_unit_valve_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_unit_valve_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_unit_valve_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_heat_valve_archive`
--

DROP TABLE IF EXISTS `pr_heat_valve_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_heat_valve_archive` (
  `id` bigint NOT NULL COMMENT '主键',
  `archive_id` bigint DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表号',
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '卡号',
  `meter_arc_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案编码',
  `meter_arc_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '仪表档案名称',
  `concentrator_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '集中器编码',
  `imei_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IMEI号',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产品ID',
  `device_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备ID',
  `meter_serial` int DEFAULT NULL COMMENT '仪表序号',
  `house_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `valve_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门状态',
  `setting_status` int DEFAULT NULL COMMENT '设定状态',
  `actual_status` int DEFAULT NULL COMMENT '实际状态',
  `in_temperature` decimal(10,2) DEFAULT NULL COMMENT '进水温度',
  `out_temperature` decimal(10,2) DEFAULT NULL COMMENT '回水温度',
  `voltage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电压',
  `valve_time` datetime DEFAULT NULL COMMENT '阀门时间',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `reporting_interval` int DEFAULT NULL COMMENT '上报间隔',
  `total_degree` int DEFAULT NULL COMMENT '总度数',
  `residue_degree` int DEFAULT NULL COMMENT '剩余度数',
  `interval_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '间隔单位',
  `valid_time` int DEFAULT NULL COMMENT '有效时间',
  `is_changed` int DEFAULT '0' COMMENT '是否换表 0否1是',
  `is_stop` int DEFAULT '0' COMMENT '是否停用 0否1是',
  `chan_num_update_time` datetime DEFAULT NULL COMMENT '通道号更新时间',
  `chan_num_sync_time` datetime DEFAULT NULL COMMENT '通道号同步时间',
  `last_perform_id` bigint DEFAULT NULL,
  `dtu_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号',
  `dtu_num_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU号状态',
  `chan_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通道号',
  `install_site` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装位置',
  `dtu_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'DTU状态',
  `trade_times` int DEFAULT NULL COMMENT '购买倍数',
  `is_open` int DEFAULT '0' COMMENT '是否开户 0否1是',
  `caliber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '口径',
  `install_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '安装方式',
  `group_num25` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '分组号25',
  `user_set_temp` decimal(10,2) DEFAULT NULL COMMENT '用户设定温度',
  `room_temp` decimal(10,2) DEFAULT NULL COMMENT '室温',
  `avg_temp` decimal(10,2) DEFAULT NULL COMMENT '平均温度',
  `valve_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '阀门型号',
  `cold_flg` int DEFAULT '0' COMMENT '冷阀标志 0否1是',
  `wkq_lock` int DEFAULT '0' COMMENT '温控器锁定 0否1是',
  `temp_low` int DEFAULT NULL COMMENT '温度下限',
  `temp_high` int DEFAULT NULL COMMENT '温度上限',
  `work_time` int DEFAULT NULL COMMENT '工作时间',
  `total_open_time` int DEFAULT NULL COMMENT '累计开启时间',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `ins_flow` decimal(10,2) DEFAULT NULL COMMENT '瞬时流量',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_meter_num` (`meter_num`),
  KEY `idx_archive_id` (`archive_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='户间阀门配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_heat_valve_archive`
--

LOCK TABLES `pr_heat_valve_archive` WRITE;
/*!40000 ALTER TABLE `pr_heat_valve_archive` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_heat_valve_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_house`
--

DROP TABLE IF EXISTS `pr_house`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_house` (
  `id` bigint NOT NULL COMMENT '主键',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房屋编码',
  `room_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `building_id` bigint DEFAULT NULL,
  `building_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '楼宇名称',
  `unit_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `floor` int DEFAULT NULL COMMENT '楼层',
  `nfloor_area` decimal(18,4) DEFAULT NULL COMMENT '使用面积',
  `gfloor_area` decimal(18,4) DEFAULT NULL COMMENT '建筑面积',
  `heating_area` decimal(18,4) DEFAULT NULL COMMENT '供热面积',
  `frist_insidearea` decimal(18,4) DEFAULT NULL COMMENT '一楼内面积',
  `second_insidearea` decimal(18,4) DEFAULT NULL COMMENT '二楼内面积',
  `third_insidearea` decimal(18,4) DEFAULT NULL COMMENT '三楼内面积',
  `nature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房屋性质',
  `structure` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房屋结构',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房屋类型',
  `towards` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '朝向',
  `unit_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '单元类型',
  `unit_price` decimal(18,4) DEFAULT NULL COMMENT '单价',
  `property_term` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '产权年限',
  `delivery_time` datetime DEFAULT NULL COMMENT '工程交付时间',
  `accept_time` datetime DEFAULT NULL COMMENT '物业验收时间',
  `occupancy_time` datetime DEFAULT NULL COMMENT '入住时间',
  `establish_time` datetime DEFAULT NULL COMMENT '立户时间',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮寄地址',
  `decoration_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '装修状态',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '房屋状态',
  `pay_status` tinyint DEFAULT NULL COMMENT '缴费状态:0-欠费,1-已缴,2-停供,3-空置',
  `is_special` tinyint unsigned DEFAULT '0' COMMENT '是否特殊户',
  `rental_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '出租状态',
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '排序',
  `site_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '位置属性',
  `site_type_old` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '历史位置',
  `station_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '供热区域属性',
  `preset_angle` decimal(18,4) DEFAULT NULL COMMENT '预设角度',
  `preset_flow_rate` decimal(18,4) DEFAULT NULL COMMENT '预设流量',
  `in_temp` decimal(18,4) DEFAULT NULL COMMENT '进水温度',
  `out_temp` decimal(18,4) DEFAULT NULL COMMENT '出水温度',
  `room_temp` decimal(18,4) DEFAULT NULL COMMENT '室温',
  `valve_open` int DEFAULT NULL COMMENT '阀门开度百分比',
  `cur_flow` decimal(18,4) DEFAULT NULL COMMENT '当前流量',
  `other_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '外部缴费编码',
  `area` decimal(18,4) DEFAULT NULL,
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_charged` tinyint DEFAULT NULL,
  `is_calc` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_building_unit` (`building_id`,`unit_code`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房屋';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_house`
--

LOCK TABLES `pr_house` WRITE;
/*!40000 ALTER TABLE `pr_house` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_house` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_house_expense`
--

DROP TABLE IF EXISTS `pr_house_expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_house_expense` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房屋费用绑定';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_house_expense`
--

LOCK TABLES `pr_house_expense` WRITE;
/*!40000 ALTER TABLE `pr_house_expense` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_house_expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_house_log`
--

DROP TABLE IF EXISTS `pr_house_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_house_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `change_type` varchar(32) DEFAULT NULL COMMENT '变更类型',
  `change_val` int DEFAULT NULL COMMENT '变更值',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房屋变更日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_house_log`
--

LOCK TABLES `pr_house_log` WRITE;
/*!40000 ALTER TABLE `pr_house_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_house_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_account`
--

DROP TABLE IF EXISTS `pr_import_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_account` (
  `id` bigint NOT NULL COMMENT '主键',
  `type` tinyint DEFAULT NULL COMMENT '导入类型(0账户/1交易)',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `house_id` bigint DEFAULT NULL,
  `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
  `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
  `amount` decimal(18,4) DEFAULT NULL COMMENT '金额',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='导入暂存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_account`
--

LOCK TABLES `pr_import_account` WRITE;
/*!40000 ALTER TABLE `pr_import_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_basic_data`
--

DROP TABLE IF EXISTS `pr_import_basic_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_basic_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `org_name` varchar(255) DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `building_name` varchar(255) DEFAULT NULL,
  `building_id` bigint DEFAULT NULL,
  `building_code` varchar(128) DEFAULT NULL,
  `station_id` varchar(64) DEFAULT NULL,
  `room_num` varchar(255) DEFAULT NULL,
  `room_code` varchar(128) DEFAULT NULL,
  `station_name` varchar(255) DEFAULT NULL,
  `substation_name` varchar(255) DEFAULT NULL,
  `substation_id` varchar(64) DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  `unit_code` varchar(64) DEFAULT NULL,
  `unit_site` varchar(64) DEFAULT NULL,
  `floor` int DEFAULT NULL,
  `nfloor_area` decimal(18,4) DEFAULT NULL,
  `gfloor_area` decimal(18,4) DEFAULT NULL,
  `heating_area` decimal(18,4) DEFAULT NULL,
  `establish_time` datetime DEFAULT NULL,
  `nature` varchar(64) DEFAULT NULL,
  `site_type` varchar(64) DEFAULT NULL,
  `station_type` varchar(64) DEFAULT NULL,
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID（分库 pr_user.id）',
  `exist_user_id` varchar(64) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `phone` varchar(64) DEFAULT NULL,
  `id_no` varchar(64) DEFAULT NULL,
  `account` decimal(18,4) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `item_group` varchar(64) DEFAULT NULL,
  `item_code` varchar(64) DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
  `standard_name` varchar(255) DEFAULT NULL,
  `standard_price` decimal(18,4) DEFAULT NULL,
  `pay_status` varchar(64) DEFAULT NULL,
  `other_code` varchar(255) DEFAULT NULL,
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_basic_data`
--

LOCK TABLES `pr_import_basic_data` WRITE;
/*!40000 ALTER TABLE `pr_import_basic_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_basic_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_heat`
--

DROP TABLE IF EXISTS `pr_import_heat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_heat` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `archive_id` bigint DEFAULT NULL,
  `meter_serial` int DEFAULT NULL COMMENT '对应序号',
  `meter_num` varchar(255) DEFAULT NULL COMMENT '表号',
  `card_num` varchar(255) DEFAULT NULL COMMENT '卡号/IMSI',
  `imei` varchar(128) DEFAULT NULL COMMENT 'IMEI',
  `device_id` varchar(128) DEFAULT NULL COMMENT '设备ID',
  `dtu_num` varchar(128) DEFAULT NULL COMMENT 'DTU编号',
  `dtu_type` int DEFAULT NULL COMMENT 'DTU类型',
  `concentrator_code` varchar(128) DEFAULT NULL COMMENT '集中器编号',
  `chan_num` varchar(64) DEFAULT NULL COMMENT '通道号/组号',
  `install_site` varchar(255) DEFAULT NULL COMMENT '安装位置',
  `start_reading` decimal(18,4) DEFAULT NULL COMMENT '起始读数',
  `current_reading` decimal(18,4) DEFAULT NULL COMMENT '当前读数',
  `total_used` decimal(18,4) DEFAULT NULL COMMENT '累积用量',
  `trade_times` int DEFAULT NULL COMMENT '交易次数',
  `total_money` decimal(18,4) DEFAULT NULL COMMENT '累积已用金额',
  `total_recharge` varchar(255) DEFAULT NULL COMMENT '累积充值金额',
  `current_balance` decimal(18,4) DEFAULT NULL COMMENT '当前余额',
  `standard_id` bigint DEFAULT NULL,
  `standard_name` varchar(255) DEFAULT NULL COMMENT '单价名称',
  `standard_price` decimal(18,4) DEFAULT NULL COMMENT '单价金额',
  `install_type` varchar(64) DEFAULT NULL COMMENT '安装类型',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL COMMENT '状态',
  `meter_arc_code` varchar(128) DEFAULT NULL COMMENT '热表档案编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='热表导入临时表(房屋级)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_heat`
--

LOCK TABLES `pr_import_heat` WRITE;
/*!40000 ALTER TABLE `pr_import_heat` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_heat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_heat_temp`
--

DROP TABLE IF EXISTS `pr_import_heat_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_heat_temp` (
  `id` bigint NOT NULL COMMENT '主键',
  `org_name` varchar(255) DEFAULT NULL,
  `building_name` varchar(255) DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `room_num` varchar(255) DEFAULT NULL,
  `archive_id` bigint DEFAULT NULL,
  `meter_name` varchar(255) DEFAULT NULL,
  `specification` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `meter_serial` int DEFAULT NULL,
  `meter_num` varchar(255) DEFAULT NULL,
  `card_num` varchar(255) DEFAULT NULL,
  `imei` varchar(128) DEFAULT NULL,
  `dtu_num` varchar(128) DEFAULT NULL,
  `dtu_type` int DEFAULT NULL,
  `concentrator_code` varchar(128) DEFAULT NULL,
  `chan_num` varchar(64) DEFAULT NULL,
  `device_id` varchar(128) DEFAULT NULL,
  `caliber` varchar(64) DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  `meter_arc_code` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_heat_temp`
--

LOCK TABLES `pr_import_heat_temp` WRITE;
/*!40000 ALTER TABLE `pr_import_heat_temp` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_heat_temp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_history`
--

DROP TABLE IF EXISTS `pr_import_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_history` (
  `id` bigint NOT NULL COMMENT '主键',
  `org_name` varchar(255) DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `building_name` varchar(255) DEFAULT NULL,
  `room_num` varchar(255) DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `item_group` varchar(64) DEFAULT NULL,
  `item_code` varchar(64) DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
  `standard_name` varchar(255) DEFAULT NULL,
  `standard_price` decimal(18,4) DEFAULT NULL,
  `qty` decimal(18,4) DEFAULT NULL,
  `receivable` decimal(18,4) DEFAULT NULL,
  `deduction` decimal(18,4) DEFAULT NULL,
  `paid_in` decimal(18,4) DEFAULT NULL,
  `payment_balance` decimal(18,4) DEFAULT NULL,
  `status` varchar(64) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID（分库 pr_user.id）',
  `pay_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_history`
--

LOCK TABLES `pr_import_history` WRITE;
/*!40000 ALTER TABLE `pr_import_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_record`
--

DROP TABLE IF EXISTS `pr_import_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `house_id` bigint DEFAULT NULL,
  `org_name` varchar(255) DEFAULT NULL,
  `room_num` varchar(255) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `standard_price` decimal(18,4) DEFAULT NULL,
  `card_num` varchar(255) DEFAULT NULL,
  `trade_times` int DEFAULT NULL,
  `qty` int DEFAULT NULL,
  `receivable` decimal(18,4) DEFAULT NULL,
  `payment_balance` decimal(18,4) DEFAULT NULL,
  `paid_in` decimal(18,4) DEFAULT NULL,
  `trade_time` datetime DEFAULT NULL,
  `allow_amount` decimal(18,4) DEFAULT NULL,
  `deduction` decimal(18,4) DEFAULT NULL,
  `item_id` varchar(64) DEFAULT NULL,
  `archive_id` bigint DEFAULT NULL,
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID（分库 pr_user.id）',
  `meter_num` varchar(255) DEFAULT NULL,
  `meter_arc_code` varchar(128) DEFAULT NULL,
  `meter_serial` int DEFAULT NULL,
  `total_used` decimal(18,4) DEFAULT NULL,
  `total_money` decimal(18,4) DEFAULT NULL,
  `total_recharge` decimal(18,4) DEFAULT NULL,
  `current_balance` decimal(18,4) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_record`
--

LOCK TABLES `pr_import_record` WRITE;
/*!40000 ALTER TABLE `pr_import_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_unit_heat`
--

DROP TABLE IF EXISTS `pr_import_unit_heat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_unit_heat` (
  `id` bigint NOT NULL COMMENT '主键',
  `building_id` bigint DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  `archive_id` bigint DEFAULT NULL,
  `meter_serial` int DEFAULT NULL,
  `meter_num` varchar(255) DEFAULT NULL,
  `card_num` varchar(255) DEFAULT NULL,
  `imei` varchar(128) DEFAULT NULL,
  `device_id` varchar(128) DEFAULT NULL,
  `dtu_num` varchar(128) DEFAULT NULL,
  `dtu_type` int DEFAULT NULL,
  `concentrator_code` varchar(128) DEFAULT NULL,
  `chan_num` varchar(64) DEFAULT NULL,
  `install_site` varchar(255) DEFAULT NULL,
  `start_reading` decimal(18,4) DEFAULT NULL,
  `current_reading` decimal(18,4) DEFAULT NULL,
  `total_used` decimal(18,4) DEFAULT NULL,
  `trade_times` int DEFAULT NULL,
  `total_money` decimal(18,4) DEFAULT NULL,
  `total_recharge` varchar(255) DEFAULT NULL,
  `current_balance` decimal(18,4) DEFAULT NULL,
  `standard_id` bigint DEFAULT NULL,
  `standard_name` varchar(255) DEFAULT NULL,
  `standard_price` decimal(18,4) DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  `meter_arc_code` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_unit_heat`
--

LOCK TABLES `pr_import_unit_heat` WRITE;
/*!40000 ALTER TABLE `pr_import_unit_heat` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_unit_heat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_unit_valve`
--

DROP TABLE IF EXISTS `pr_import_unit_valve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_unit_valve` (
  `id` bigint NOT NULL COMMENT '主键',
  `building_id` bigint DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  `archive_id` bigint DEFAULT NULL,
  `meter_serial` int DEFAULT NULL,
  `meter_num` varchar(255) DEFAULT NULL,
  `card_num` varchar(255) DEFAULT NULL,
  `imei` varchar(128) DEFAULT NULL,
  `device_id` varchar(128) DEFAULT NULL,
  `dtu_num` varchar(128) DEFAULT NULL,
  `dtu_type` int DEFAULT NULL,
  `concentrator_code` varchar(128) DEFAULT NULL,
  `chan_num` varchar(64) DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  `meter_arc_code` varchar(128) DEFAULT NULL,
  `command_archive_id` varchar(64) DEFAULT NULL,
  `command_meter_arc_code` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_unit_valve`
--

LOCK TABLES `pr_import_unit_valve` WRITE;
/*!40000 ALTER TABLE `pr_import_unit_valve` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_unit_valve` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_import_valve`
--

DROP TABLE IF EXISTS `pr_import_valve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_import_valve` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `archive_id` bigint DEFAULT NULL,
  `meter_serial` int DEFAULT NULL,
  `meter_num` varchar(255) DEFAULT NULL,
  `card_num` varchar(255) DEFAULT NULL,
  `imei` varchar(128) DEFAULT NULL,
  `device_id` varchar(128) DEFAULT NULL,
  `dtu_num` varchar(128) DEFAULT NULL,
  `dtu_type` int DEFAULT NULL,
  `concentrator_code` varchar(128) DEFAULT NULL,
  `chan_num` varchar(64) DEFAULT NULL,
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `type` int DEFAULT NULL,
  `meter_arc_code` varchar(128) DEFAULT NULL,
  `command_archive_id` varchar(64) DEFAULT NULL,
  `command_meter_arc_code` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_import_valve`
--

LOCK TABLES `pr_import_valve` WRITE;
/*!40000 ALTER TABLE `pr_import_valve` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_import_valve` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_inspection_equipment`
--

DROP TABLE IF EXISTS `pr_inspection_equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_inspection_equipment` (
  `id` bigint NOT NULL COMMENT '主键',
  `equipment_name` varchar(128) DEFAULT NULL COMMENT '设备名称',
  `equipment_code` varchar(64) DEFAULT NULL COMMENT '设备编码',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='巡检设备';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_inspection_equipment`
--

LOCK TABLES `pr_inspection_equipment` WRITE;
/*!40000 ALTER TABLE `pr_inspection_equipment` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_inspection_equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_inspection_person`
--

DROP TABLE IF EXISTS `pr_inspection_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_inspection_person` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '巡检人姓名',
  `phone` varchar(32) DEFAULT NULL COMMENT '巡检人手机号',
  `type` varchar(32) DEFAULT NULL COMMENT '工种',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='巡检人员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_inspection_person`
--

LOCK TABLES `pr_inspection_person` WRITE;
/*!40000 ALTER TABLE `pr_inspection_person` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_inspection_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_inspection_plan`
--

DROP TABLE IF EXISTS `pr_inspection_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_inspection_plan` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(128) DEFAULT NULL COMMENT '计划名称',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `equipment_id` text COMMENT '设备ID集合',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='巡检计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_inspection_plan`
--

LOCK TABLES `pr_inspection_plan` WRITE;
/*!40000 ALTER TABLE `pr_inspection_plan` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_inspection_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_inspection_record`
--

DROP TABLE IF EXISTS `pr_inspection_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_inspection_record` (
  `id` bigint NOT NULL COMMENT '主键',
  `plan_id` bigint DEFAULT NULL,
  `person_id` bigint DEFAULT NULL,
  `person_name` varchar(64) DEFAULT NULL COMMENT '巡检人员姓名',
  `equipment_id` bigint DEFAULT NULL,
  `equipment_name` varchar(128) DEFAULT NULL COMMENT '设备名称',
  `result` varchar(32) DEFAULT NULL COMMENT '巡检结果',
  `content` text COMMENT '巡检内容',
  `images` text COMMENT '巡检图片',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='巡检记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_inspection_record`
--

LOCK TABLES `pr_inspection_record` WRITE;
/*!40000 ALTER TABLE `pr_inspection_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_inspection_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_notice`
--

DROP TABLE IF EXISTS `pr_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_notice` (
  `id` bigint NOT NULL COMMENT '主键',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_notice`
--

LOCK TABLES `pr_notice` WRITE;
/*!40000 ALTER TABLE `pr_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_operate_card_log`
--

DROP TABLE IF EXISTS `pr_operate_card_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_operate_card_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `meter_id` bigint DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `card_type` varchar(32) DEFAULT NULL COMMENT '卡类型',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_meter_id` (`meter_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='写卡操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_operate_card_log`
--

LOCK TABLES `pr_operate_card_log` WRITE;
/*!40000 ALTER TABLE `pr_operate_card_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_operate_card_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_options`
--

DROP TABLE IF EXISTS `pr_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_options` (
  `id` bigint NOT NULL COMMENT '主键',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `forbidden_buy_electric` tinyint DEFAULT NULL,
  `forbidden_buy_water` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物业选项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_options`
--

LOCK TABLES `pr_options` WRITE;
/*!40000 ALTER TABLE `pr_options` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_options_heat`
--

DROP TABLE IF EXISTS `pr_options_heat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_options_heat` (
  `id` bigint NOT NULL COMMENT '主键',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `option_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `money_charge` int DEFAULT NULL,
  `auto_close` int DEFAULT NULL,
  `auto_sms` int DEFAULT NULL,
  `open_time` datetime DEFAULT NULL,
  `close_time` datetime DEFAULT NULL,
  `open_early_days` int DEFAULT NULL,
  `close_later_days` int DEFAULT NULL,
  `scale` int DEFAULT NULL,
  `is_enable` int DEFAULT NULL,
  `quittance_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `start_serial` bigint DEFAULT NULL,
  `serial_length` int DEFAULT NULL,
  `letter_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `serial_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `round_mode` int DEFAULT NULL,
  `define1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `define2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `control_min` int DEFAULT NULL,
  `control_max` int DEFAULT NULL,
  `regulation` int DEFAULT NULL,
  `regulation_num` int DEFAULT NULL,
  `command_num` int DEFAULT NULL,
  `interval_time` int DEFAULT NULL,
  `command_time` datetime DEFAULT NULL,
  `tele_api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `tele_app_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `tele_app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `tele_product_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `service` int DEFAULT NULL,
  `dlsd_unit_code` int DEFAULT NULL,
  `stride` int DEFAULT NULL,
  `wdbjx` decimal(12,4) DEFAULT NULL,
  `wdbjd` decimal(12,4) DEFAULT NULL,
  `swbjx` decimal(12,4) DEFAULT NULL,
  `swbjd` decimal(12,4) DEFAULT NULL,
  `kzwclcs` int DEFAULT NULL,
  `kzsbcs` int DEFAULT NULL,
  `house_min` decimal(12,4) DEFAULT NULL,
  `house_max` decimal(12,4) DEFAULT NULL,
  `house_small_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `house_medium_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `house_big_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `back_water_min` decimal(12,4) DEFAULT NULL,
  `back_water_max` decimal(12,4) DEFAULT NULL,
  `back_water_small_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `back_water_medium_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `back_water_big_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `floor_view_complete_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `floor_view_no_complete_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `floor_view_abnormal_color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `bwbh` decimal(12,4) DEFAULT NULL,
  `bwbsh` decimal(12,4) DEFAULT NULL,
  `bwsh` decimal(12,4) DEFAULT NULL,
  `bwxh` decimal(12,4) DEFAULT NULL,
  `bwzjh` decimal(12,4) DEFAULT NULL,
  `bwbxh` decimal(12,4) DEFAULT NULL,
  `bwblyhxh` decimal(12,4) DEFAULT NULL,
  `hswdpcz` decimal(12,4) DEFAULT NULL,
  `is_abnormal_enable` int DEFAULT NULL,
  `wjfhswd` decimal(12,4) DEFAULT NULL,
  `heat_start_date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `heat_end_date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `charge_standard_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `penalty_rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `invoice_notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `payment_reminder` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `mrphwd` decimal(12,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='供热选项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_options_heat`
--

LOCK TABLES `pr_options_heat` WRITE;
/*!40000 ALTER TABLE `pr_options_heat` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_options_heat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_pet`
--

DROP TABLE IF EXISTS `pr_pet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_pet` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
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
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='宠物管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_pet`
--

LOCK TABLES `pr_pet` WRITE;
/*!40000 ALTER TABLE `pr_pet` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_pet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_print_template`
--

DROP TABLE IF EXISTS `pr_print_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_print_template` (
  `id` bigint NOT NULL COMMENT '主键',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `template_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `serial_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='打印模板';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_print_template`
--

LOCK TABLES `pr_print_template` WRITE;
/*!40000 ALTER TABLE `pr_print_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_print_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_reconciliation_diff`
--

DROP TABLE IF EXISTS `pr_reconciliation_diff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_reconciliation_diff` (
  `id` bigint NOT NULL COMMENT '主键',
  `bill_id` bigint DEFAULT NULL,
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
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_out_trade_no` (`out_trade_no`),
  KEY `idx_diff_type` (`diff_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信对账差异记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_reconciliation_diff`
--

LOCK TABLES `pr_reconciliation_diff` WRITE;
/*!40000 ALTER TABLE `pr_reconciliation_diff` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_reconciliation_diff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_repair_person`
--

DROP TABLE IF EXISTS `pr_repair_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_repair_person` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '维修人姓名',
  `phone` varchar(32) DEFAULT NULL COMMENT '维修人手机号',
  `type` varchar(32) DEFAULT NULL COMMENT '工种',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='维修人员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_repair_person`
--

LOCK TABLES `pr_repair_person` WRITE;
/*!40000 ALTER TABLE `pr_repair_person` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_repair_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_repair_record`
--

DROP TABLE IF EXISTS `pr_repair_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_repair_record` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` varchar(32) DEFAULT NULL COMMENT '业主ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '业主姓名',
  `phone` varchar(32) DEFAULT NULL COMMENT '报修人电话',
  `house_id` bigint DEFAULT NULL,
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
  `repair_content` text COMMENT '报修内容',
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
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dispatch_id` (`dispatch_id`),
  KEY `idx_repair_no` (`repair_no`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_repair_record`
--

LOCK TABLES `pr_repair_record` WRITE;
/*!40000 ALTER TABLE `pr_repair_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_repair_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_scheduling`
--

DROP TABLE IF EXISTS `pr_scheduling`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_scheduling` (
  `id` bigint NOT NULL COMMENT '主键',
  `person_id` bigint DEFAULT NULL,
  `person_name` varchar(64) DEFAULT NULL COMMENT '人员姓名',
  `work_date` datetime DEFAULT NULL COMMENT '工作日期',
  `shift` varchar(32) DEFAULT NULL COMMENT '班次',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='排班管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_scheduling`
--

LOCK TABLES `pr_scheduling` WRITE;
/*!40000 ALTER TABLE `pr_scheduling` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_scheduling` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_standard`
--

DROP TABLE IF EXISTS `pr_standard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_standard` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `cycles` int DEFAULT NULL,
  `generate_rule` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `step_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `statistics_type` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `step_maxgrade` int DEFAULT NULL,
  `is_step2` tinyint DEFAULT NULL,
  `step2_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `step2_maxgrade` int DEFAULT NULL,
  `is_latefee` tinyint DEFAULT NULL,
  `latefee_startdate` datetime DEFAULT NULL,
  `latefee_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `latefee_startdays` int DEFAULT NULL,
  `latefee_formula` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_limited` tinyint DEFAULT NULL,
  `limited_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `limited_cond` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `limited_times` int DEFAULT NULL,
  `limited_money` decimal(18,4) DEFAULT NULL,
  `limited_single_money` decimal(18,4) DEFAULT NULL,
  `standard_price` decimal(18,4) DEFAULT NULL,
  `money_formula` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `max_money` decimal(18,4) DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `step2type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `step2maxgrade` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_item_code` (`item_code`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收费标准';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_standard`
--

LOCK TABLES `pr_standard` WRITE;
/*!40000 ALTER TABLE `pr_standard` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_standard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_standard_price`
--

DROP TABLE IF EXISTS `pr_standard_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_standard_price` (
  `id` bigint NOT NULL COMMENT '主键',
  `standard_id` bigint DEFAULT NULL,
  `level` int DEFAULT NULL,
  `min_qty` decimal(18,4) DEFAULT NULL,
  `max_qty` decimal(18,4) DEFAULT NULL,
  `price` decimal(18,4) DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `step12` int DEFAULT NULL,
  `grade` int DEFAULT NULL,
  `minimum` int DEFAULT NULL,
  `maximum` int DEFAULT NULL,
  `standard_price` decimal(12,4) DEFAULT NULL,
  `price_formula` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `other1price` decimal(12,4) DEFAULT NULL,
  `other2price` decimal(12,4) DEFAULT NULL,
  `other3price` decimal(12,4) DEFAULT NULL,
  `max_price` decimal(12,4) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `line_mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `line_amount` decimal(12,4) DEFAULT NULL,
  `money_formula` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_standard_id` (`standard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收费标准价格阶梯';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_standard_price`
--

LOCK TABLES `pr_standard_price` WRITE;
/*!40000 ALTER TABLE `pr_standard_price` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_standard_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_strategy`
--

DROP TABLE IF EXISTS `pr_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_strategy` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(128) DEFAULT NULL COMMENT '策略名称',
  `type` varchar(32) DEFAULT NULL COMMENT '策略类型',
  `content` text COMMENT '策略内容(JSON)',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物业策略表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_strategy`
--

LOCK TABLES `pr_strategy` WRITE;
/*!40000 ALTER TABLE `pr_strategy` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_strategy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_transaction_detail`
--

DROP TABLE IF EXISTS `pr_transaction_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_transaction_detail` (
  `id` bigint NOT NULL COMMENT '主键',
  `meter_id` bigint DEFAULT NULL,
  `house_id` bigint DEFAULT NULL,
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `transaction_type` varchar(32) DEFAULT NULL COMMENT '交易类型',
  `receivable` decimal(18,4) DEFAULT '0.0000' COMMENT '应收金额',
  `paid_in` decimal(18,4) DEFAULT '0.0000' COMMENT '实收金额',
  `qty` decimal(18,4) DEFAULT '0.0000' COMMENT '用量',
  `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
  `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `record_time` datetime DEFAULT NULL COMMENT '交易时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_meter_id` (`meter_id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_transaction_detail`
--

LOCK TABLES `pr_transaction_detail` WRITE;
/*!40000 ALTER TABLE `pr_transaction_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_transaction_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_transaction_record`
--

DROP TABLE IF EXISTS `pr_transaction_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_transaction_record` (
  `id` bigint NOT NULL COMMENT '主键',
  `serial_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `transaction_type` tinyint DEFAULT NULL,
  `payment_type` tinyint DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT '0.0000',
  `paid_amount` decimal(18,4) DEFAULT '0.0000',
  `status` tinyint DEFAULT '0',
  `house_id` bigint DEFAULT NULL,
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `transaction_time` datetime DEFAULT NULL,
  `operator_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `original_record_id` bigint DEFAULT NULL,
  `invoice_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_serial_num` (`serial_num`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_transaction_time` (`transaction_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易记录主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_transaction_record`
--

LOCK TABLES `pr_transaction_record` WRITE;
/*!40000 ALTER TABLE `pr_transaction_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_transaction_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_transaction_record_sub`
--

DROP TABLE IF EXISTS `pr_transaction_record_sub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_transaction_record_sub` (
  `id` bigint NOT NULL COMMENT '主键',
  `main_id` bigint DEFAULT NULL,
  `expense_id` bigint DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT '0.0000',
  `balance_before` decimal(18,4) DEFAULT '0.0000',
  `balance_after` decimal(18,4) DEFAULT '0.0000',
  `item_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_group` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '费项分组',
  `item_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '费项编码',
  `house_id` bigint DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_main_id` (`main_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易记录子表明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_transaction_record_sub`
--

LOCK TABLES `pr_transaction_record_sub` WRITE;
/*!40000 ALTER TABLE `pr_transaction_record_sub` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_transaction_record_sub` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_unit`
--

DROP TABLE IF EXISTS `pr_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_unit` (
  `id` bigint NOT NULL COMMENT '主键',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '单元编码',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '单元名称',
  `building_id` bigint DEFAULT NULL,
  `on_floor` int DEFAULT NULL COMMENT '地上楼层',
  `heating_area` decimal(18,4) DEFAULT NULL COMMENT '供热面积',
  `up_floor` int DEFAULT NULL COMMENT '地下楼层',
  `floor` int DEFAULT NULL COMMENT '总楼层',
  `site` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '位置',
  `seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '排序',
  `station_id` bigint DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='单元信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_unit`
--

LOCK TABLES `pr_unit` WRITE;
/*!40000 ALTER TABLE `pr_unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_use_card_log`
--

DROP TABLE IF EXISTS `pr_use_card_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_use_card_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `meter_id` bigint DEFAULT NULL,
  `meter_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `card_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `valve_status` int DEFAULT NULL,
  `operation_time` datetime DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `operator_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_meter_id` (`meter_id`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='写卡日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_use_card_log`
--

LOCK TABLES `pr_use_card_log` WRITE;
/*!40000 ALTER TABLE `pr_use_card_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_use_card_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_user`
--

DROP TABLE IF EXISTS `pr_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_user` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_type` int DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `is_id_auth` int DEFAULT NULL,
  `nation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `id_startdate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_enddate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `employer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `wx_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `qq_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `occupation` int DEFAULT NULL,
  `education` int DEFAULT NULL,
  `hobby` int DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `emer_contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `emer_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seq` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `head_photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `front_photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `back_photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_company_org` (`company_id`,`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_user`
--

LOCK TABLES `pr_user` WRITE;
/*!40000 ALTER TABLE `pr_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_user_house`
--

DROP TABLE IF EXISTS `pr_user_house`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_user_house` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `house_id` bigint DEFAULT NULL,
  `relation_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '关系类型',
  `company_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `org_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小区ID',
  `record_source` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '记录来源',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-房屋关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_user_house`
--

LOCK TABLES `pr_user_house` WRITE;
/*!40000 ALTER TABLE `pr_user_house` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_user_house` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_wechat_bill`
--

DROP TABLE IF EXISTS `pr_wechat_bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_wechat_bill` (
  `id` bigint NOT NULL COMMENT '主键',
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
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_bill_date` (`bill_date`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信对账单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_wechat_bill`
--

LOCK TABLES `pr_wechat_bill` WRITE;
/*!40000 ALTER TABLE `pr_wechat_bill` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_wechat_bill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_wechat_bind_record`
--

DROP TABLE IF EXISTS `pr_wechat_bind_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_wechat_bind_record` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
  `heat_pay_code` varchar(64) DEFAULT NULL COMMENT '供热缴费编码',
  `wx_open_id` varchar(64) DEFAULT NULL COMMENT '微信openId',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_wx_open_id` (`wx_open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信绑定记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_wechat_bind_record`
--

LOCK TABLES `pr_wechat_bind_record` WRITE;
/*!40000 ALTER TABLE `pr_wechat_bind_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_wechat_bind_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_wechat_order`
--

DROP TABLE IF EXISTS `pr_wechat_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_wechat_order` (
  `id` bigint NOT NULL COMMENT '主键',
  `house_id` bigint DEFAULT NULL,
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
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `out_trade_no` varchar(255) DEFAULT NULL,
  `open_id` varchar(255) DEFAULT NULL,
  `other_code` varchar(255) DEFAULT NULL,
  `house_address` varchar(255) DEFAULT NULL,
  `total_fee` decimal(12,4) DEFAULT NULL,
  `body` varchar(255) DEFAULT NULL,
  `sp_bill_create_ip` varchar(255) DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `notify_url` varchar(255) DEFAULT NULL,
  `return_url` varchar(255) DEFAULT NULL,
  `trade_type` varchar(255) DEFAULT NULL,
  `bank_type` varchar(255) DEFAULT NULL,
  `attach` varchar(255) DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_company_org` (`company_id`,`org_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_wechat_order`
--

LOCK TABLES `pr_wechat_order` WRITE;
/*!40000 ALTER TABLE `pr_wechat_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_wechat_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_wechat_refund`
--

DROP TABLE IF EXISTS `pr_wechat_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_wechat_refund` (
  `id` bigint NOT NULL COMMENT '主键',
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
  `house_id` bigint DEFAULT NULL,
  `operator` varchar(40) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_out_trade_no` (`out_trade_no`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信退款记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_wechat_refund`
--

LOCK TABLES `pr_wechat_refund` WRITE;
/*!40000 ALTER TABLE `pr_wechat_refund` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_wechat_refund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pr_wechat_user`
--

DROP TABLE IF EXISTS `pr_wechat_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pr_wechat_user` (
  `id` bigint NOT NULL COMMENT '主键',
  `open_id` varchar(64) DEFAULT NULL COMMENT '微信openId',
  `other_code` varchar(64) DEFAULT NULL COMMENT '其他编码',
  `house_id` bigint DEFAULT NULL,
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
  `bind_status` tinyint DEFAULT NULL COMMENT '绑定状态: 0=未绑定 1=已绑定',
  `session_key` varchar(255) DEFAULT NULL COMMENT '会话密钥',
  `union_id` varchar(64) DEFAULT NULL COMMENT '微信unionId',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_open_id` (`open_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pr_wechat_user`
--

LOCK TABLES `pr_wechat_user` WRITE;
/*!40000 ALTER TABLE `pr_wechat_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `pr_wechat_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_area`
--

DROP TABLE IF EXISTS `sys_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_area` (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '父级ID',
  `parent_ids` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '所有父级ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '名称',
  `sort` int DEFAULT NULL COMMENT '排序',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '区划代码',
  `code2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用代码',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '类型：1=省，2=市，3=区县',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志：0=存在，2=删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='省市区表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_area`
--

LOCK TABLES `sys_area` WRITE;
/*!40000 ALTER TABLE `sys_area` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_organization`
--

DROP TABLE IF EXISTS `sys_organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_organization` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `parent_id` varchar(32) DEFAULT '0' COMMENT '父级ID',
  `level` varchar(10) NOT NULL DEFAULT '' COMMENT '组织层级',
  `name` varchar(128) DEFAULT NULL COMMENT '组织名称',
  `code` varchar(64) DEFAULT NULL COMMENT '组织编码',
  `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID（兼容字段）',
  `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织架构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_organization`
--

LOCK TABLES `sys_organization` WRITE;
/*!40000 ALTER TABLE `sys_organization` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'tenant_000000'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-01  6:44:57
