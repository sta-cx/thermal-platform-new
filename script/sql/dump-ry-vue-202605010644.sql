-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: ry-vue
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
INSERT INTO `sys_area` VALUES ('10','1','0,1,','上海市',9,'310000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('11','1','0,1,','江苏省',10,'320000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('12','1','0,1,','浙江省',11,'330000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('13','1','0,1,','安徽省',12,'340000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('14','1','0,1,','福建省',13,'350000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('15','1','0,1,','江西省',14,'360000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('16','1','0,1,','山东省',15,'370000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('17','1','0,1,','河南省',16,'410000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('18','1','0,1,','湖北省',17,'420000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('19','1','0,1,','湖南省',18,'430000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('2','1','0,1,','北京市',1,'110000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('20','1','0,1,','广东省',19,'440000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('21','1','0,1,','广西壮族自治区',20,'450000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('22','1','0,1,','海南省',21,'460000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('23','1','0,1,','重庆市',22,'500000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('24','1','0,1,','四川省',23,'510000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('25','1','0,1,','贵州省',24,'520000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('26','1','0,1,','云南省',25,'530000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('27','1','0,1,','西藏自治区',26,'540000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('28','1','0,1,','陕西省',27,'610000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('29','1','0,1,','甘肃省',28,'620000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('3','1','0,1,','天津市',2,'120000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('30','1','0,1,','青海省',29,'630000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('31','1','0,1,','宁夏回族自治区',30,'640000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('32','1','0,1,','新疆维吾尔自治区',31,'650000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('33','1','0,1,','台湾省',32,'710000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('34','1','0,1,','香港特别行政区',33,'810000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('35','1','0,1,','澳门特别行政区',34,'820000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('4','1','0,1,','河北省',3,'130000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('5','1','0,1,','山西省',4,'140000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('6','1','0,1,','内蒙古自治区',5,'150000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('7','1','0,1,','辽宁省',6,'210000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('8','1','0,1,','吉林省',7,'220000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0'),('9','1','0,1,','黑龙江省',8,'230000',NULL,'1',NULL,'2026-04-27 21:34:43',NULL,NULL,NULL,'0');
/*!40000 ALTER TABLE `sys_area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_client`
--

DROP TABLE IF EXISTS `sys_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_client` (
  `id` bigint NOT NULL COMMENT 'id',
  `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '客户端id',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '客户端key',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '客户端秘钥',
  `grant_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '授权类型',
  `device_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备类型',
  `active_timeout` int DEFAULT '1800' COMMENT 'token活跃超时时间',
  `timeout` int DEFAULT '604800' COMMENT 'token固定超时',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统授权表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_client`
--

LOCK TABLES `sys_client` WRITE;
/*!40000 ALTER TABLE `sys_client` DISABLE KEYS */;
INSERT INTO `sys_client` VALUES (1,'e5cd7e4891bf95d1d19206ce24a7b32e','pc','pc123','password,social','pc',1800,604800,'0','0',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12'),(2,'428a8310cd442757ae699df5d894f051','app','app123','password,sms,social','android',1800,604800,'0','0',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12');
/*!40000 ALTER TABLE `sys_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_column`
--

DROP TABLE IF EXISTS `sys_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_column` (
  `id` bigint NOT NULL,
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `page_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面/表格名称',
  `column_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '自定义列名（逗号分隔）',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_page` (`user_id`,`page_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户自定义表格列';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_column`
--

LOCK TABLES `sys_column` WRITE;
/*!40000 ALTER TABLE `sys_column` DISABLE KEYS */;
INSERT INTO `sys_column` VALUES (2047275887788113922,'000000',1,'testTable','name,age,email',103,1,'2026-04-23 19:26:23',1,'2026-04-23 19:31:27');
/*!40000 ALTER TABLE `sys_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_company`
--

DROP TABLE IF EXISTS `sys_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_company` (
  `id` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(128) DEFAULT NULL,
  `province` varchar(64) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `county` varchar(64) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `post_code` varchar(16) DEFAULT NULL,
  `principal` varchar(64) DEFAULT NULL,
  `tele` varchar(32) DEFAULT NULL,
  `fax` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `is_enabled` tinyint DEFAULT '1',
  `is_audited` tinyint DEFAULT '0',
  `nature` int DEFAULT NULL,
  `business_license` varchar(128) DEFAULT NULL,
  `business_scope` varchar(500) DEFAULT NULL,
  `founded_date` date DEFAULT NULL,
  `business_term` date DEFAULT NULL,
  `economy_character` varchar(64) DEFAULT NULL,
  `legal_person` varchar(64) DEFAULT NULL,
  `registered_capital` varchar(32) DEFAULT NULL,
  `business_license_img` varchar(500) DEFAULT NULL,
  `legal_representative_idcard_img1` varchar(500) DEFAULT NULL,
  `legal_representative_idcard_img2` varchar(500) DEFAULT NULL,
  `bank_name` varchar(128) DEFAULT NULL,
  `bank_address` varchar(255) DEFAULT NULL,
  `account_name` varchar(128) DEFAULT NULL,
  `corpotate_account` varchar(64) DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `settle_time` datetime DEFAULT NULL,
  `limited_houses` int DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `longitude` varchar(32) DEFAULT NULL,
  `latitude` varchar(32) DEFAULT NULL,
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公司信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_company`
--

LOCK TABLES `sys_company` WRITE;
/*!40000 ALTER TABLE `sys_company` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` bigint NOT NULL COMMENT '参数主键',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'000000','主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'000000','用户管理-账号初始密码','sys.user.initPassword','123456','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'初始化密码 123456'),(3,'000000','主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'深色主题theme-dark，浅色主题theme-light'),(5,'000000','账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'是否开启注册用户功能（true开启，false关闭）'),(11,'000000','OSS预览列表资源开关','sys.oss.previewListResource','true','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'true:开启, false:关闭');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL COMMENT '部门id',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '部门名称',
  `dept_category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '部门类别编码',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` bigint DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,'000000',0,'0','总公司',NULL,0,NULL,'15888888888','xxx@qq.com','0','0',103,1,'2026-04-23 15:56:09',NULL,NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL COMMENT '字典编码',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,'000000',1,'男','0','sys_user_sex','','','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'性别男'),(2,'000000',2,'女','1','sys_user_sex','','','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'性别女'),(3,'000000',3,'未知','2','sys_user_sex','','','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'性别未知'),(4,'000000',1,'显示','0','sys_show_hide','','primary','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'显示菜单'),(5,'000000',2,'隐藏','1','sys_show_hide','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'隐藏菜单'),(6,'000000',1,'正常','0','sys_normal_disable','','primary','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'正常状态'),(7,'000000',2,'停用','1','sys_normal_disable','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'停用状态'),(12,'000000',1,'是','Y','sys_yes_no','','primary','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'系统默认是'),(13,'000000',2,'否','N','sys_yes_no','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'系统默认否'),(14,'000000',1,'通知','1','sys_notice_type','','warning','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'通知'),(15,'000000',2,'公告','2','sys_notice_type','','success','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'公告'),(16,'000000',1,'正常','0','sys_notice_status','','primary','Y',103,1,'2026-04-23 15:56:11',NULL,NULL,'正常状态'),(17,'000000',2,'关闭','1','sys_notice_status','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'关闭状态'),(18,'000000',1,'新增','1','sys_oper_type','','info','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'新增操作'),(19,'000000',2,'修改','2','sys_oper_type','','info','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'修改操作'),(20,'000000',3,'删除','3','sys_oper_type','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'删除操作'),(21,'000000',4,'授权','4','sys_oper_type','','primary','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'授权操作'),(22,'000000',5,'导出','5','sys_oper_type','','warning','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'导出操作'),(23,'000000',6,'导入','6','sys_oper_type','','warning','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'导入操作'),(24,'000000',7,'强退','7','sys_oper_type','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'强退操作'),(25,'000000',8,'生成代码','8','sys_oper_type','','warning','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'生成操作'),(26,'000000',9,'清空数据','9','sys_oper_type','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'清空操作'),(27,'000000',1,'成功','0','sys_common_status','','primary','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'正常状态'),(28,'000000',2,'失败','1','sys_common_status','','danger','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'停用状态'),(29,'000000',99,'其他','0','sys_oper_type','','info','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'其他操作'),(30,'000000',0,'密码认证','password','sys_grant_type','el-check-tag','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'密码认证'),(31,'000000',0,'短信认证','sms','sys_grant_type','el-check-tag','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'短信认证'),(32,'000000',0,'邮件认证','email','sys_grant_type','el-check-tag','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'邮件认证'),(33,'000000',0,'小程序认证','xcx','sys_grant_type','el-check-tag','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'小程序认证'),(34,'000000',0,'三方登录认证','social','sys_grant_type','el-check-tag','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'三方登录认证'),(35,'000000',0,'PC','pc','sys_device_type','','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'PC'),(36,'000000',0,'安卓','android','sys_device_type','','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'安卓'),(37,'000000',0,'iOS','ios','sys_device_type','','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'iOS'),(38,'000000',0,'小程序','xcx','sys_device_type','','default','N',103,1,'2026-04-23 15:56:11',NULL,NULL,'小程序');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL COMMENT '字典主键',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典类型',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `tenant_id` (`tenant_id`,`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'000000','用户性别','sys_user_sex',103,1,'2026-04-23 15:56:11',NULL,NULL,'用户性别列表'),(2,'000000','菜单状态','sys_show_hide',103,1,'2026-04-23 15:56:11',NULL,NULL,'菜单状态列表'),(3,'000000','系统开关','sys_normal_disable',103,1,'2026-04-23 15:56:11',NULL,NULL,'系统开关列表'),(6,'000000','系统是否','sys_yes_no',103,1,'2026-04-23 15:56:11',NULL,NULL,'系统是否列表'),(7,'000000','通知类型','sys_notice_type',103,1,'2026-04-23 15:56:11',NULL,NULL,'通知类型列表'),(8,'000000','通知状态','sys_notice_status',103,1,'2026-04-23 15:56:11',NULL,NULL,'通知状态列表'),(9,'000000','操作类型','sys_oper_type',103,1,'2026-04-23 15:56:11',NULL,NULL,'操作类型列表'),(10,'000000','系统状态','sys_common_status',103,1,'2026-04-23 15:56:11',NULL,NULL,'登录状态列表'),(11,'000000','授权类型','sys_grant_type',103,1,'2026-04-23 15:56:11',NULL,NULL,'认证授权类型'),(12,'000000','设备类型','sys_device_type',103,1,'2026-04-23 15:56:11',NULL,NULL,'客户端设备类型');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL COMMENT '访问ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户账号',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '客户端',
  `device_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '设备类型',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (2049009450766118914,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 14:14:56'),(2049014976337989634,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-28 14:36:54'),(2049015098509676546,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 14:37:23'),(2049020618490564610,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-28 14:59:19'),(2049020631782313986,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 14:59:22'),(2049020667366789122,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 14:59:31'),(2049027969134182402,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 15:28:32'),(2049029477334597634,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-28 15:34:31'),(2049029485421215746,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 15:34:33'),(2049029874543575041,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 15:36:06'),(2049030029036568577,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 15:36:43'),(2049030259664568322,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 15:37:38'),(2049031086810673154,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 15:40:55'),(2049035913590542338,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 16:00:06'),(2049075436536737794,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 18:37:09'),(2049097808683053058,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 20:06:03'),(2049098145238200322,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-28 20:07:23'),(2049280347825553409,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 08:11:23'),(2049294471288684546,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 09:07:31'),(2049315704373813250,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 10:31:53'),(2049368535541837826,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 14:01:49'),(2049369525108174850,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 14:05:45'),(2049369804645953537,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 14:06:52'),(2049369843296464897,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 14:07:01'),(2049380137322246145,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 14:47:55'),(2049380141663350785,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 14:47:55'),(2049380147313078274,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 14:47:57'),(2049388095907844097,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 15:19:33'),(2049388823061745665,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 15:22:26'),(2049399905067225089,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','1','密码输入错误1次','2026-04-29 16:06:28'),(2049400026915950594,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','1','密码输入错误2次','2026-04-29 16:06:57'),(2049400052660588546,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 16:07:03'),(2049428926207381505,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 18:01:47'),(2049469543713087489,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:43:11'),(2049469562398711809,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:43:16'),(2049469584477528065,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:43:21'),(2049469642157596673,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 20:43:35'),(2049469648696516609,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:43:36'),(2049469706322059266,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 20:43:50'),(2049470052117286913,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:45:12'),(2049471529910390785,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:51:05'),(2049471550424731649,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:51:10'),(2049471553696288770,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:51:10'),(2049471557445996545,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:51:11'),(2049471565507448833,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:51:13'),(2049471568783200258,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:51:14'),(2049473593667346434,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 20:59:17'),(2049476346338729985,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 21:10:13'),(2049476355004162049,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 21:10:15'),(2049476379305959425,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 21:10:21'),(2049476386528550913,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 21:10:23'),(2049476833083514882,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 21:12:09'),(2049487739335073793,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 21:55:29'),(2049488648723095553,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 21:59:06'),(2049488689923743745,'000000','test','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 21:59:16'),(2049488759767293954,'000000','test','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 21:59:33'),(2049488770601181186,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 21:59:35'),(2049494246671900674,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 22:21:21'),(2049494280339578881,'000000','test','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 22:21:29'),(2049494317509500929,'000000','test','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 22:21:38'),(2049494327932346369,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 22:21:40'),(2049495629848502274,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 22:26:51'),(2049495661846847489,'000000','test','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 22:26:58'),(2049495856525467650,'000000','test','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 22:27:45'),(2049495862061948929,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 22:27:46'),(2049496037660680193,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-29 22:28:28'),(2049496043138441218,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 22:28:29'),(2049507576593575937,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-29 23:14:19'),(2049862159341785090,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-30 22:43:18'),(2049862269232549889,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-30 22:43:44'),(2049862453928726530,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-04-30 22:44:28'),(2049862462518661122,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-04-30 22:44:30'),(2049969575962157058,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-05-01 05:50:08'),(2049976603560452098,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','退出成功','2026-05-01 06:18:04'),(2049976641980276737,'000000','admin','pc','pc','0:0:0:0:0:0:0:1','内网IP','Chrome','Windows 10 or Windows Server 2016','0','登录成功','2026-05-01 06:18:13');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '组件路径',
  `query_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '路由参数',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '显示状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '#' COMMENT '菜单图标',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'system',NULL,'',1,0,'M','0','0','','lucide:settings',103,1,'2026-04-23 15:56:10',NULL,NULL,'系统管理目录'),(2,'系统监控',0,3,'monitor',NULL,'',1,0,'M','0','0','','lucide:monitor',103,1,'2026-04-23 15:56:10',NULL,NULL,'系统监控目录'),(6,'租户管理',0,2,'tenant',NULL,'',1,0,'M','0','0','','lucide:building-2',103,1,'2026-04-23 15:56:10',NULL,NULL,'租户管理目录'),(100,'用户管理',1,1,'user','system/user/index','',1,0,'C','0','0','system:user:list','lucide:user',103,1,'2026-04-23 15:56:10',NULL,NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','',1,0,'C','0','0','system:role:list','lucide:users',103,1,'2026-04-23 15:56:10',NULL,NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','',1,0,'C','0','0','system:menu:list','lucide:menu',103,1,'2026-04-23 15:56:10',NULL,NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','',1,0,'C','0','0','system:dept:list','lucide:git-branch',103,1,'2026-04-23 15:56:10',NULL,NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','',1,0,'C','0','0','system:post:list','lucide:id-card',103,1,'2026-04-23 15:56:10',NULL,NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','',1,0,'C','0','0','system:dict:list','lucide:book-open',103,1,'2026-04-23 15:56:10',NULL,NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','',1,0,'C','0','0','system:config:list','lucide:sliders',103,1,'2026-04-23 15:56:10',NULL,NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','',1,0,'C','0','0','system:notice:list','lucide:megaphone',103,1,'2026-04-23 15:56:10',NULL,NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','',1,0,'M','0','0','','lucide:scroll-text',103,1,'2026-04-23 15:56:10',NULL,NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','',1,0,'C','0','0','monitor:online:list','lucide:wifi',103,1,'2026-04-23 15:56:10',NULL,NULL,'在线用户菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','',1,0,'C','0','0','monitor:cache:list','lucide:database',103,1,'2026-04-23 15:56:10',NULL,NULL,'缓存监控菜单'),(118,'文件管理',1,10,'oss','system/oss/index','',1,0,'C','0','0','system:oss:list','lucide:upload',103,1,'2026-04-23 15:56:10',NULL,NULL,'文件管理菜单'),(121,'租户管理',6,1,'tenant','system/tenant/index','',1,0,'C','0','0','system:tenant:list','lucide:list',103,1,'2026-04-23 15:56:10',NULL,NULL,'租户管理菜单'),(122,'租户权限管理',6,2,'tenantPackage','system/tenantPackage/index','',1,0,'C','0','0','system:tenantPackage:list','lucide:package',103,1,'2026-04-23 15:56:10',NULL,NULL,'租户权限管理菜单'),(123,'客户端管理',1,11,'client','system/client/index','',1,0,'C','0','0','system:client:list','lucide:globe',103,1,'2026-04-23 15:56:10',NULL,NULL,'客户端管理菜单'),(130,'分配用户',1,2,'role-auth/user/:roleId','system/role/authUser','',1,1,'C','1','0','system:role:edit','lucide:user-plus',103,1,'2026-04-23 15:56:10',NULL,NULL,'/system/role'),(131,'分配角色',1,1,'user-auth/role/:userId','system/user/authRole','',1,1,'C','1','0','system:user:edit','lucide:user-check',103,1,'2026-04-23 15:56:10',NULL,NULL,'/system/user'),(132,'字典数据',1,6,'dict-data/index/:dictId','system/dict/data','',1,1,'C','1','0','system:dict:list','lucide:database',103,1,'2026-04-23 15:56:10',NULL,NULL,'/system/dict'),(133,'文件配置管理',1,10,'oss-config/index','system/oss/config','',1,1,'C','1','0','system:ossConfig:list','lucide:settings-2',103,1,'2026-04-23 15:56:10',NULL,NULL,'/system/oss'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','',1,0,'C','0','0','monitor:operlog:list','lucide:clipboard-list',103,1,'2026-04-23 15:56:10',NULL,NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','',1,0,'C','0','0','monitor:logininfor:list','lucide:log-in',103,1,'2026-04-23 15:56:10',NULL,NULL,'登录日志菜单'),(1001,'用户查询',100,1,'','','',1,0,'F','0','0','system:user:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1002,'用户新增',100,2,'','','',1,0,'F','0','0','system:user:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1003,'用户修改',100,3,'','','',1,0,'F','0','0','system:user:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1004,'用户删除',100,4,'','','',1,0,'F','0','0','system:user:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1005,'用户导出',100,5,'','','',1,0,'F','0','0','system:user:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1006,'用户导入',100,6,'','','',1,0,'F','0','0','system:user:import','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1007,'重置密码',100,7,'','','',1,0,'F','0','0','system:user:resetPwd','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1008,'角色查询',101,1,'','','',1,0,'F','0','0','system:role:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1009,'角色新增',101,2,'','','',1,0,'F','0','0','system:role:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1010,'角色修改',101,3,'','','',1,0,'F','0','0','system:role:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1011,'角色删除',101,4,'','','',1,0,'F','0','0','system:role:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1012,'角色导出',101,5,'','','',1,0,'F','0','0','system:role:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1013,'菜单查询',102,1,'','','',1,0,'F','0','0','system:menu:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1014,'菜单新增',102,2,'','','',1,0,'F','0','0','system:menu:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1015,'菜单修改',102,3,'','','',1,0,'F','0','0','system:menu:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1016,'菜单删除',102,4,'','','',1,0,'F','0','0','system:menu:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1017,'部门查询',103,1,'','','',1,0,'F','0','0','system:dept:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1018,'部门新增',103,2,'','','',1,0,'F','0','0','system:dept:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1019,'部门修改',103,3,'','','',1,0,'F','0','0','system:dept:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1020,'部门删除',103,4,'','','',1,0,'F','0','0','system:dept:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1021,'岗位查询',104,1,'','','',1,0,'F','0','0','system:post:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1022,'岗位新增',104,2,'','','',1,0,'F','0','0','system:post:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1023,'岗位修改',104,3,'','','',1,0,'F','0','0','system:post:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1024,'岗位删除',104,4,'','','',1,0,'F','0','0','system:post:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1025,'岗位导出',104,5,'','','',1,0,'F','0','0','system:post:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1026,'字典查询',105,1,'#','','',1,0,'F','0','0','system:dict:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1027,'字典新增',105,2,'#','','',1,0,'F','0','0','system:dict:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1028,'字典修改',105,3,'#','','',1,0,'F','0','0','system:dict:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1029,'字典删除',105,4,'#','','',1,0,'F','0','0','system:dict:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1030,'字典导出',105,5,'#','','',1,0,'F','0','0','system:dict:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1031,'参数查询',106,1,'#','','',1,0,'F','0','0','system:config:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1032,'参数新增',106,2,'#','','',1,0,'F','0','0','system:config:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1033,'参数修改',106,3,'#','','',1,0,'F','0','0','system:config:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1034,'参数删除',106,4,'#','','',1,0,'F','0','0','system:config:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1035,'参数导出',106,5,'#','','',1,0,'F','0','0','system:config:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1036,'公告查询',107,1,'#','','',1,0,'F','0','0','system:notice:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1037,'公告新增',107,2,'#','','',1,0,'F','0','0','system:notice:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1038,'公告修改',107,3,'#','','',1,0,'F','0','0','system:notice:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1039,'公告删除',107,4,'#','','',1,0,'F','0','0','system:notice:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1040,'操作查询',500,1,'#','','',1,0,'F','0','0','monitor:operlog:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1041,'操作删除',500,2,'#','','',1,0,'F','0','0','monitor:operlog:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1042,'日志导出',500,4,'#','','',1,0,'F','0','0','monitor:operlog:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1043,'登录查询',501,1,'#','','',1,0,'F','0','0','monitor:logininfor:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1044,'登录删除',501,2,'#','','',1,0,'F','0','0','monitor:logininfor:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1045,'日志导出',501,3,'#','','',1,0,'F','0','0','monitor:logininfor:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1046,'在线查询',109,1,'#','','',1,0,'F','0','0','monitor:online:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1047,'批量强退',109,2,'#','','',1,0,'F','0','0','monitor:online:batchLogout','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1048,'单条强退',109,3,'#','','',1,0,'F','0','0','monitor:online:forceLogout','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1050,'账户解锁',501,4,'#','','',1,0,'F','0','0','monitor:logininfor:unlock','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1061,'客户端管理查询',123,1,'#','','',1,0,'F','0','0','system:client:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1062,'客户端管理新增',123,2,'#','','',1,0,'F','0','0','system:client:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1063,'客户端管理修改',123,3,'#','','',1,0,'F','0','0','system:client:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1064,'客户端管理删除',123,4,'#','','',1,0,'F','0','0','system:client:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1065,'客户端管理导出',123,5,'#','','',1,0,'F','0','0','system:client:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1600,'文件查询',118,1,'#','','',1,0,'F','0','0','system:oss:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1601,'文件上传',118,2,'#','','',1,0,'F','0','0','system:oss:upload','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1602,'文件下载',118,3,'#','','',1,0,'F','0','0','system:oss:download','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1603,'文件删除',118,4,'#','','',1,0,'F','0','0','system:oss:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1606,'租户查询',121,1,'#','','',1,0,'F','0','0','system:tenant:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1607,'租户新增',121,2,'#','','',1,0,'F','0','0','system:tenant:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1608,'租户修改',121,3,'#','','',1,0,'F','0','0','system:tenant:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1609,'租户删除',121,4,'#','','',1,0,'F','0','0','system:tenant:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1610,'租户导出',121,5,'#','','',1,0,'F','0','0','system:tenant:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1611,'租户套餐查询',122,1,'#','','',1,0,'F','0','0','system:tenantPackage:query','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1612,'租户套餐新增',122,2,'#','','',1,0,'F','0','0','system:tenantPackage:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1613,'租户套餐修改',122,3,'#','','',1,0,'F','0','0','system:tenantPackage:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1614,'租户套餐删除',122,4,'#','','',1,0,'F','0','0','system:tenantPackage:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1615,'租户套餐导出',122,5,'#','','',1,0,'F','0','0','system:tenantPackage:export','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1620,'配置列表',118,5,'#','','',1,0,'F','0','0','system:ossConfig:list','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1621,'配置添加',118,6,'#','','',1,0,'F','0','0','system:ossConfig:add','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1622,'配置编辑',118,6,'#','','',1,0,'F','0','0','system:ossConfig:edit','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(1623,'配置删除',118,6,'#','','',1,0,'F','0','0','system:ossConfig:remove','#',103,1,'2026-04-23 15:56:10',NULL,NULL,''),(2000,'供热平衡管理',0,2,'thermal',NULL,NULL,1,0,'M','0','0',NULL,'lucide:flame',NULL,NULL,NULL,NULL,NULL,'热力平台顶级菜单'),(2001,'仪表管理',2000,1,'meter',NULL,NULL,1,0,'M','0','0',NULL,'lucide:gauge',NULL,NULL,NULL,NULL,NULL,NULL),(2002,'热力调控',2000,2,'ht',NULL,NULL,1,0,'M','0','0',NULL,'lucide:thermometer',NULL,NULL,NULL,NULL,NULL,NULL),(2003,'物业收费',2000,3,'property',NULL,NULL,1,0,'M','0','0',NULL,'lucide:building-2',NULL,NULL,NULL,NULL,NULL,NULL),(2004,'代理商管理',2000,4,'agent',NULL,NULL,1,0,'M','0','0',NULL,'lucide:users',NULL,NULL,NULL,NULL,NULL,NULL),(2005,'微信管理',2000,5,'wechat',NULL,NULL,1,0,'M','0','0',NULL,'lucide:smartphone',NULL,NULL,NULL,NULL,NULL,NULL),(2100,'热力表档案',2001,1,'heat','thermal/meter/heat/index',NULL,1,0,'C','0','0','thermal:meter:heat:list','lucide:flame',NULL,NULL,NULL,NULL,NULL,NULL),(2101,'水表档案',2001,2,'water','thermal/meter/water/index',NULL,1,0,'C','0','0','thermal:meter:water:list','lucide:droplets',NULL,NULL,NULL,NULL,NULL,NULL),(2102,'电表档案',2001,3,'electric','thermal/meter/electric/index',NULL,1,0,'C','0','0','thermal:meter:electric:list','lucide:zap',NULL,NULL,NULL,NULL,NULL,NULL),(2103,'燃气表档案',2001,4,'gas','thermal/meter/gas/index',NULL,1,0,'C','0','0','thermal:meter:gas:list','lucide:flame-kindling',NULL,NULL,NULL,NULL,NULL,NULL),(2104,'集中器档案',2001,5,'centrator','thermal/meter/centrator/index',NULL,1,0,'C','0','0','thermal:meter:centrator:list','lucide:server',NULL,NULL,NULL,NULL,NULL,NULL),(2105,'温控器档案',2001,6,'tc','thermal/meter/tc/index',NULL,1,0,'C','0','0','thermal:meter:tc:list','lucide:thermometer',NULL,NULL,NULL,NULL,NULL,NULL),(2106,'阀门档案',2001,7,'valve','thermal/meter/valve/index',NULL,1,0,'C','0','0','thermal:meter:valve:list','lucide:shower-head',NULL,NULL,NULL,NULL,NULL,NULL),(2107,'仪表分类',2001,8,'sort','thermal/meter/sort/index',NULL,1,0,'C','0','0','thermal:meter:sort:list','lucide:list-tree',NULL,NULL,NULL,NULL,NULL,NULL),(2108,'仪表厂商',2001,9,'vendor','thermal/meter/vendor/index',NULL,1,0,'C','0','0','thermal:meter:vendor:list','lucide:factory',NULL,NULL,NULL,NULL,NULL,NULL),(2109,'公式档案',2001,10,'formula','thermal/meter/formula/index',NULL,1,0,'C','0','0','thermal:meter:formula:list','lucide:function-square',NULL,NULL,NULL,NULL,NULL,NULL),(2110,'代理商仪表',2001,11,'agent-meter','thermal/meter/agent-meter/index',NULL,1,0,'C','0','0','thermal:meter:agent-meter:list','lucide:hard-drive',NULL,NULL,NULL,NULL,NULL,NULL),(2200,'热力站管理',2002,1,'station','thermal/ht/station/index',NULL,1,0,'C','0','0','thermal:ht:station:list','lucide:building-2',NULL,NULL,NULL,NULL,NULL,NULL),(2201,'热力站分区',2002,2,'station-partition','thermal/ht/station-partition/index',NULL,1,0,'C','0','0','thermal:ht:station-partition:list','lucide:layers',NULL,NULL,NULL,NULL,NULL,NULL),(2202,'控制策略',2002,3,'strategy','thermal/ht/strategy/index',NULL,1,0,'C','0','0','thermal:ht:strategy:list','lucide:git-branch',NULL,NULL,NULL,NULL,NULL,NULL),(2203,'控制指令',2002,4,'instruction','thermal/ht/instruction/index',NULL,1,0,'C','0','0','thermal:ht:instruction:list','lucide:terminal',NULL,NULL,NULL,NULL,NULL,NULL),(2204,'控制范围',2002,5,'scope','thermal/ht/scope/index',NULL,1,0,'C','0','0','thermal:ht:scope:list','lucide:target',NULL,NULL,NULL,NULL,NULL,NULL),(2205,'DTU控制范围',2002,6,'scope-dtu','thermal/ht/scope-dtu/index',NULL,1,0,'C','0','0','thermal:ht:scope-dtu:list','lucide:wifi',NULL,NULL,NULL,NULL,NULL,NULL),(2206,'策略执行记录',2002,7,'strategy-perform','thermal/ht/strategy-perform/index',NULL,1,0,'C','0','0','thermal:ht:strategy-perform:list','lucide:history',NULL,NULL,NULL,NULL,NULL,NULL),(2207,'调控任务',2002,8,'tasks','thermal/ht/tasks/index',NULL,1,0,'C','0','0','thermal:ht:tasks:list','lucide:clock',NULL,NULL,NULL,NULL,NULL,NULL),(2208,'调控执行记录',2002,9,'tasks-perform','thermal/ht/tasks-perform/index',NULL,1,0,'C','0','0','thermal:ht:tasks-perform:list','lucide:list-checks',NULL,NULL,NULL,NULL,NULL,NULL),(2209,'房屋策略',2002,10,'house-strategy','thermal/ht/house-strategy/index',NULL,1,0,'C','0','0','thermal:ht:house-strategy:list','lucide:home',NULL,NULL,NULL,NULL,NULL,NULL),(2210,'报警管理',2002,11,'alert','thermal/ht/alert/index',NULL,1,0,'C','0','0','thermal:ht:alert:list','lucide:bell',NULL,NULL,NULL,NULL,NULL,NULL),(2211,'报修管理',2002,12,'repair','thermal/ht/repair/index',NULL,1,0,'C','0','0','thermal:ht:repair:list','lucide:wrench',NULL,NULL,NULL,NULL,NULL,NULL),(2212,'阀门控制',2002,13,'control','thermal/ht/control/index',NULL,1,0,'C','0','0','thermal:ht:control:list','lucide:settings-2',NULL,NULL,NULL,NULL,NULL,NULL),(2213,'房屋热表配表',2002,14,'heat-archive','thermal/ht/heat-archive/index',NULL,1,0,'C','0','0','thermal:ht:heat-archive:list','lucide:database',NULL,NULL,NULL,NULL,NULL,NULL),(2214,'阀门配表',2002,15,'valve-archive','thermal/ht/valve-archive/index',NULL,1,0,'C','0','0','thermal:ht:valve-archive:list','lucide:shuffle',NULL,NULL,NULL,NULL,NULL,NULL),(2215,'单元热表',2002,16,'unit-hot-archive','thermal/ht/unit-hot-archive/index',NULL,1,0,'C','0','0','thermal:ht:unit-hot-archive:list','lucide:thermometer',NULL,NULL,NULL,NULL,NULL,NULL),(2216,'单元阀门配表',2002,17,'unit-valve-archive','thermal/ht/unit-valve-archive/index',NULL,1,0,'C','0','0','thermal:ht:unit-valve-archive:list','lucide:pipeline',NULL,NULL,NULL,NULL,NULL,NULL),(2217,'DTU配表',2002,18,'dtu-archive','thermal/ht/dtu-archive/index',NULL,1,0,'C','0','0','thermal:ht:dtu-archive:list','lucide:hard-drive',NULL,NULL,NULL,NULL,NULL,NULL),(2218,'温度配表',2002,19,'temp-archive','thermal/ht/temp-archive/index',NULL,1,0,'C','0','0','thermal:ht:temp-archive:list','lucide:thermometer-sun',NULL,NULL,NULL,NULL,NULL,NULL),(2219,'日用量/月用量',2002,20,'heat-usage','thermal/ht/heat-usage/index',NULL,1,0,'C','0','0','thermal:ht:heat-usage:list','lucide:bar-chart-3',NULL,NULL,NULL,NULL,NULL,NULL),(2220,'抄表记录',2002,21,'heat-reading','thermal/ht/heat-reading/index',NULL,1,0,'C','0','0','thermal:ht:heat-reading:list','lucide:clipboard-list',NULL,NULL,NULL,NULL,NULL,NULL),(2300,'区域管理',2003,1,'regional','thermal/property/regional/index',NULL,1,0,'C','0','0','thermal:property:regional:list','lucide:map-pin',NULL,NULL,NULL,NULL,NULL,NULL),(2301,'公司信息',2003,2,'company','thermal/property/company/index',NULL,1,0,'C','0','0','thermal:property:company:list','lucide:building',NULL,NULL,NULL,NULL,NULL,NULL),(2302,'楼宇管理',2003,3,'building','thermal/property/building/index',NULL,1,0,'C','0','0','thermal:property:building:list','lucide:building-2',NULL,NULL,NULL,NULL,NULL,NULL),(2303,'单元管理',2003,4,'unit','thermal/property/unit/index',NULL,1,0,'C','0','0','thermal:property:unit:list','lucide:layers',NULL,NULL,NULL,NULL,NULL,NULL),(2304,'房屋管理',2003,5,'house','thermal/property/house/index',NULL,1,0,'C','0','0','thermal:property:house:list','lucide:home',NULL,NULL,NULL,NULL,NULL,NULL),(2305,'热户管理',2003,6,'family','thermal/property/family/index',NULL,1,0,'C','0','0','thermal:property:family:list','lucide:users',NULL,NULL,NULL,NULL,NULL,NULL),(2306,'客户管理',2003,7,'user','thermal/property/user/index',NULL,1,0,'C','0','0','thermal:property:user:list','lucide:contact',NULL,NULL,NULL,NULL,NULL,NULL),(2307,'费用项目',2003,8,'expense-item','thermal/property/expense-item/index',NULL,1,0,'C','0','0','thermal:property:expense-item:list','lucide:receipt',NULL,NULL,NULL,NULL,NULL,NULL),(2308,'费用标准',2003,9,'standard','thermal/property/standard/index',NULL,1,0,'C','0','0','thermal:property:standard:list','lucide:calculator',NULL,NULL,NULL,NULL,NULL,NULL),(2309,'收费策略',2003,10,'strategy','thermal/property/strategy/index',NULL,1,0,'C','0','0','thermal:property:strategy:list','lucide:brain-circuit',NULL,NULL,NULL,NULL,NULL,NULL),(2310,'物业角色',2003,11,'role','thermal/property/role/index',NULL,1,0,'C','0','0','thermal:property:role:list','lucide:badge-check',NULL,NULL,NULL,NULL,NULL,NULL),(2311,'物业菜单',2003,12,'menu','thermal/property/menu/index',NULL,1,0,'C','0','0','thermal:property:menu:list','lucide:menu',NULL,NULL,NULL,NULL,NULL,NULL),(2312,'宠物管理',2003,13,'pet','thermal/property/pet/index',NULL,1,0,'C','0','0','thermal:property:pet:list','lucide:paw-print',NULL,NULL,NULL,NULL,NULL,NULL),(2313,'房屋变更',2003,14,'house-change','thermal/property/house-change/index',NULL,1,0,'C','0','0','thermal:property:house-change:list','lucide:move',NULL,NULL,NULL,NULL,NULL,NULL),(2314,'收费通知',2003,15,'notice','thermal/property/notice/index',NULL,1,0,'C','0','0','thermal:property:notice:list','lucide:megaphone',NULL,NULL,NULL,NULL,NULL,NULL),(2315,'账单票据',2003,16,'billing-notes','thermal/property/billing-notes/index',NULL,1,0,'C','0','0','thermal:property:billing-notes:list','lucide:file-text',NULL,NULL,NULL,NULL,NULL,NULL),(2316,'打印模板',2003,17,'print-template','thermal/property/print-template/index',NULL,1,0,'C','0','0','thermal:property:print-template:list','lucide:printer',NULL,NULL,NULL,NULL,NULL,NULL),(2317,'写卡日志',2003,18,'use-card-log','thermal/property/use-card-log/index',NULL,1,0,'C','0','0','thermal:property:use-card-log:list','lucide:credit-card',NULL,NULL,NULL,NULL,NULL,NULL),(2318,'异常记录',2003,19,'abnormal','thermal/property/abnormal/index',NULL,1,0,'C','0','0','thermal:property:abnormal:list','lucide:alert-triangle',NULL,NULL,NULL,NULL,NULL,NULL),(2319,'排班管理',2003,20,'scheduling','thermal/property/scheduling/index',NULL,1,0,'C','0','0','thermal:property:scheduling:list','lucide:calendar',NULL,NULL,NULL,NULL,NULL,NULL),(2320,'维修人员',2003,21,'repair-person','thermal/property/repair-person/index',NULL,1,0,'C','0','0','thermal:property:repair-person:list','lucide:users',NULL,NULL,NULL,NULL,NULL,NULL),(2321,'巡检人员',2003,22,'inspection-person','thermal/property/inspection-person/index',NULL,1,0,'C','0','0','thermal:property:inspection-person:list','lucide:user-check',NULL,NULL,NULL,NULL,NULL,NULL),(2322,'巡检计划',2003,23,'inspection-plan','thermal/property/inspection-plan/index',NULL,1,0,'C','0','0','thermal:property:inspection-plan:list','lucide:clipboard-check',NULL,NULL,NULL,NULL,NULL,NULL),(2323,'巡检记录',2003,24,'inspection-record','thermal/property/inspection-record/index',NULL,1,0,'C','0','0','thermal:property:inspection-record:list','lucide:list',NULL,NULL,NULL,NULL,NULL,NULL),(2324,'微信绑定',2003,25,'wechat-bind','thermal/property/wechat-bind/index',NULL,1,0,'C','0','0','thermal:property:wechat-bind:list','lucide:smartphone',NULL,NULL,NULL,NULL,NULL,NULL),(2325,'房屋费用',2003,26,'house-expense','thermal/property/house-expense/index',NULL,1,0,'C','0','0','thermal:property:house-expense:list','lucide:receipt',NULL,NULL,NULL,NULL,NULL,NULL),(2326,'交易记录',2003,27,'transaction','thermal/property/transaction/index',NULL,1,0,'C','0','0','thermal:property:transaction:list','lucide:file-text',NULL,NULL,NULL,NULL,NULL,NULL),(2327,'个人账户',2003,28,'account','thermal/property/account/index',NULL,1,0,'C','0','0','thermal:property:account:list','lucide:wallet',NULL,NULL,NULL,NULL,NULL,NULL),(2328,'自动抄表',2003,29,'auto-machine','thermal/property/auto-machine/index',NULL,1,0,'C','0','0','thermal:property:auto-machine:list','lucide:rotate-ccw',NULL,NULL,NULL,NULL,NULL,NULL),(2329,'维修记录',2003,30,'repair-record','thermal/property/repair-record/index',NULL,1,0,'C','0','0','thermal:property:repair-record:list','lucide:wrench',NULL,NULL,NULL,NULL,NULL,NULL),(2330,'费用管理',2003,31,'expense','thermal/property/expense/index',NULL,1,0,'C','0','0','thermal:property:expense:list','lucide:dollar-sign',NULL,NULL,NULL,NULL,NULL,NULL),(2331,'单笔收费',2003,32,'single-charge','thermal/property/single-charge/index',NULL,1,0,'C','0','0','thermal:property:single-charge:list','lucide:cash-register',NULL,NULL,NULL,NULL,NULL,NULL),(2332,'批量导入',2003,33,'import','thermal/property/import/index',NULL,1,0,'C','0','0','thermal:property:import:list','lucide:upload',NULL,NULL,NULL,NULL,NULL,NULL),(2400,'代理商用户',2004,1,'user','thermal/agent/user/index',NULL,1,0,'C','0','0','thermal:agent:user:list','lucide:user-circle',NULL,NULL,NULL,NULL,NULL,NULL),(2401,'代理商角色',2004,2,'role','thermal/agent/role/index',NULL,1,0,'C','0','0','thermal:agent:role:list','lucide:shield',NULL,NULL,NULL,NULL,NULL,NULL),(2402,'代理商公司',2004,3,'company','thermal/agent/company/index',NULL,1,0,'C','0','0','thermal:agent:company:list','lucide:building',NULL,NULL,NULL,NULL,NULL,NULL),(2403,'代理商物业',2004,4,'property','thermal/agent/property/index',NULL,1,0,'C','0','0','thermal:agent:property:list','lucide:building-2',NULL,NULL,NULL,NULL,NULL,NULL),(2404,'代理商菜单',2004,5,'menu','thermal/agent/menu/index',NULL,1,0,'C','0','0','thermal:agent:menu:list','lucide:menu',NULL,NULL,NULL,NULL,NULL,NULL),(2405,'代理商仪表',2004,6,'meter','thermal/agent/meter/index',NULL,1,0,'C','0','0','thermal:agent:meter:list','lucide:gauge',NULL,NULL,NULL,NULL,NULL,NULL),(2406,'抄表参数',2004,7,'reader-param','thermal/agent/reader-param/index',NULL,1,0,'C','0','0','thermal:agent:reader-param:list','lucide:settings',NULL,NULL,NULL,NULL,NULL,NULL),(2407,'版本管理',2004,8,'auto-version','thermal/agent/auto-version/index',NULL,1,0,'C','0','0','thermal:agent:auto-version:list','lucide:rotate-ccw',NULL,NULL,NULL,NULL,NULL,NULL),(2408,'授权码管理',2004,9,'access-code','thermal/agent/access-code/index',NULL,1,0,'C','0','0','thermal:agent:access-code:list','lucide:key',NULL,NULL,NULL,NULL,NULL,NULL),(2500,'微信支付',2005,1,'pay','thermal/wechat/pay/index',NULL,1,0,'C','0','0','thermal:wechat:pay:list','lucide:wallet',NULL,NULL,NULL,NULL,NULL,NULL),(2501,'微信授权',2005,2,'auth','thermal/wechat/auth/index',NULL,1,0,'C','0','0','thermal:wechat:auth:list','lucide:lock',NULL,NULL,NULL,NULL,NULL,NULL),(2502,'微信报修',2005,3,'repair','thermal/wechat/repair/index',NULL,1,0,'C','0','0','thermal:wechat:repair:list','lucide:wrench',NULL,NULL,NULL,NULL,NULL,NULL),(2503,'对账管理',2005,4,'reconciliation','thermal/wechat/reconciliation/index',NULL,1,0,'C','0','0','thermal:wechat:reconciliation:list','lucide:file-spreadsheet',NULL,NULL,NULL,NULL,NULL,NULL),(2504,'小程序用户',2005,5,'wx-user','thermal/wechat/wx-user/index',NULL,1,0,'C','0','0','thermal:wechat:wx-user:list','lucide:smartphone',NULL,NULL,NULL,NULL,NULL,NULL),(2505,'IoT回调日志',2005,6,'iot','thermal/wechat/iot/index',NULL,1,0,'C','0','0','thermal:wechat:iot:list','lucide:radio',NULL,NULL,NULL,NULL,NULL,NULL),(2600,'统计概览',2610,1,'analytics','dashboard/analytics/index',NULL,1,0,'C','0','0','dashboard:analytics:list','lucide:bar-chart-3',NULL,NULL,NULL,NULL,NULL,NULL),(2601,'实时监控',2610,2,'realtime','dashboard/realtime/index',NULL,1,0,'C','0','0','dashboard:realtime:list','lucide:activity',NULL,NULL,NULL,NULL,NULL,NULL),(2602,'能耗分析',2610,3,'energy','dashboard/energy/index',NULL,1,0,'C','0','0','dashboard:energy:list','lucide:trending-up',NULL,NULL,NULL,NULL,NULL,NULL),(2603,'热量平衡',2610,4,'balance','dashboard/balance/index',NULL,1,0,'C','0','0','dashboard:balance:list','lucide:scale',NULL,NULL,NULL,NULL,NULL,NULL),(2610,'仪表盘',0,0,'dashboard',NULL,NULL,1,0,'M','0','0','','ep:data-analysis',NULL,1,'2026-04-28 15:56:59',NULL,NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` bigint NOT NULL COMMENT '公告ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL COMMENT '日志主键',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (2049319924451880962,'000000','岗位管理',2,'org.sdkj.system.controller.system.SysPostController.edit()','PUT',1,'admin','总公司','/system/post','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"postId\":1,\"deptId\":100,\"belongDeptId\":null,\"postCode\":\"default\",\"postName\":\"默认岗位\",\"postCategory\":null,\"postSort\":1,\"status\":\"0\",\"remark\":\"\"}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 10:48:39',35),(2049396343973105666,'000000','租户套餐',1,'org.sdkj.system.controller.system.SysTenantPackageController.add()','POST',1,'admin','总公司','/system/tenant/package','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"packageId\":\"2049396343356542977\",\"packageName\":\"test\",\"menuIds\":[1,100,101,102,103,104,105,106,107,108,109,118,123,2,2000,2001,2002,2003,2004,2005,2610,500,501,1001,1002,1003,1004,1005,1006,1007,131,1008,1009,1010,1011,1012,130,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,132,1031,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1050,1600,1601,1602,1603,1620,1621,1622,1623,133,1061,1062,1063,1064,1065,1046,1047,1048,113,2100,2101,2102,2103,2104,2105,2106,2107,2108,2109,2110,2200,2201,2202,2203,2204,2205,2206,2207,2208,2209,2210,2211,2212,2213,2214,2215,2216,2217,2218,2219,2220,2300,2301,2302,2303,2304,2305,2306,2307,2308,2309,2310,2311,2312,2313,2314,2315,2316,2317,2318,2319,2320,2321,2322,2323,2324,2325,2326,2327,2328,2329,2330,2331,2332,2400,2401,2402,2403,2404,2405,2406,2407,2408,2500,2501,2502,2503,2504,2505,2600,2601,2602,2603],\"remark\":null,\"menuCheckStrictly\":null,\"status\":null}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 15:52:19',204),(2049398001360719873,'000000','用户管理',1,'org.sdkj.system.controller.system.SysUserController.add()','POST',1,'admin','总公司','/system/user','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"userId\":\"2049398001306193922\",\"deptId\":100,\"userName\":\"test\",\"nickName\":\"测试用户\",\"userType\":null,\"email\":null,\"phonenumber\":null,\"sex\":\"0\",\"status\":\"0\",\"remark\":null,\"roleIds\":null,\"postIds\":[],\"roleId\":null,\"userIds\":null,\"excludeUserIds\":null,\"superAdmin\":false}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 15:58:54',140),(2049398634386051073,'000000','租户套餐',2,'org.sdkj.system.controller.system.SysTenantPackageController.edit()','PUT',1,'admin','总公司','/system/tenant/package','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"packageId\":\"2049396343356542977\",\"packageName\":\"test1\",\"menuIds\":[1,100,101,102,103,104,105,106,107,108,109,118,123,2,2000,2001,2002,2003,2004,2005,2610,500,501,1001,1002,1003,1004,1005,1006,1007,131,1008,1009,1010,1011,1012,130,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,132,1031,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1050,1600,1601,1602,1603,1620,1621,1622,1623,133,1061,1062,1063,1064,1065,1046,1047,1048,113,2100,2101,2102,2103,2104,2105,2106,2107,2108,2109,2110,2200,2201,2202,2203,2204,2205,2206,2207,2208,2209,2210,2211,2212,2213,2214,2215,2216,2217,2218,2219,2220,2300,2301,2302,2303,2304,2305,2306,2307,2308,2309,2310,2311,2312,2313,2314,2315,2316,2317,2318,2319,2320,2321,2322,2323,2324,2325,2326,2327,2328,2329,2330,2331,2332,2400,2401,2402,2403,2404,2405,2406,2407,2408,2500,2501,2502,2503,2504,2505,2600,2601,2602,2603],\"remark\":null,\"menuCheckStrictly\":true,\"status\":null}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 16:01:25',16),(2049398760655572993,'000000','租户套餐',1,'org.sdkj.system.controller.system.SysTenantPackageController.add()','POST',1,'admin','总公司','/system/tenant/package','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"packageId\":\"2049398760622018562\",\"packageName\":\"test2\",\"menuIds\":[1,100,101,102,103,104,105,106,107,108,118,123,500,501,1001,1002,1003,1004,1005,1006,1007,131,1008,1009,1010,1011,1012,130,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,132,1031,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1050,1600,1601,1602,1603,1620,1621,1622,1623,133,1061,1062,1063,1064,1065],\"remark\":null,\"menuCheckStrictly\":null,\"status\":null}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 16:01:55',11),(2049399039027335170,'000000','租户管理',1,'org.sdkj.system.controller.system.SysTenantController.add()','POST',1,'admin','总公司','/system/tenant','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"id\":\"2049399038679207938\",\"tenantId\":null,\"contactUserName\":\"123\",\"contactPhone\":\"15866541233\",\"companyName\":\"测试企业1\",\"adminUserId\":null,\"licenseNumber\":null,\"address\":null,\"nature\":null,\"businessLicense\":null,\"legalPerson\":null,\"bankName\":null,\"bankAddress\":null,\"accountName\":null,\"corporateAccount\":null,\"province\":null,\"city\":null,\"county\":null,\"domain\":null,\"dbUrl\":null,\"dbUsername\":\"root\",\"dbPassword\":\"root\",\"dbDriver\":null,\"dbHost\":\"localhost\",\"dbPort\":3306,\"dbName\":\"tenant_000000\",\"intro\":null,\"longitude\":null,\"latitude\":null,\"remark\":null,\"packageId\":\"2049396343356542977\",\"expireTime\":null,\"accountCount\":-1,\"status\":null}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 16:03:02',160),(2049493736166383618,'000000','租户管理',3,'org.sdkj.system.controller.system.SysTenantController.remove()','DELETE',1,'admin','总公司','/system/tenant/2049399038679207938','0:0:0:0:0:0:0:1','内网IP','[\"2049399038679207938\"]','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:19:19',81),(2049494139893309443,'000000','租户管理',1,'org.sdkj.system.controller.system.SysTenantController.add()','POST',1,'admin','总公司','/system/tenant','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"id\":\"2049494139826200578\",\"tenantId\":null,\"contactUserName\":\"测试企业\",\"contactPhone\":\"15998652321\",\"companyName\":\"测试企业\",\"adminUserId\":\"2049398001306193922\",\"licenseNumber\":null,\"address\":null,\"nature\":null,\"businessLicense\":null,\"legalPerson\":null,\"bankName\":null,\"bankAddress\":null,\"accountName\":null,\"corporateAccount\":null,\"province\":null,\"city\":null,\"county\":null,\"domain\":null,\"dbUrl\":null,\"dbUsername\":\"root\",\"dbPassword\":\"root\",\"dbDriver\":null,\"dbHost\":\"localhost\",\"dbPort\":3306,\"dbName\":\"tenant_test\",\"intro\":null,\"longitude\":null,\"latitude\":null,\"remark\":null,\"packageId\":\"2049396343356542977\",\"expireTime\":null,\"accountCount\":-1,\"status\":null}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:20:55',28),(2049494210273730562,'000000','租户套餐',2,'org.sdkj.system.controller.system.SysTenantPackageController.edit()','PUT',1,'admin','总公司','/system/tenant/package','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"packageId\":\"2049396343356542977\",\"packageName\":\"test1\",\"menuIds\":[109,2,2000,2001,2002,2003,2004,2005,2610,1046,1047,1048,113,2100,2101,2102,2103,2104,2105,2106,2107,2108,2109,2110,2200,2201,2202,2203,2204,2205,2206,2207,2208,2209,2210,2211,2212,2213,2214,2215,2216,2217,2218,2219,2220,2300,2301,2302,2303,2304,2305,2306,2307,2308,2309,2310,2311,2312,2313,2314,2315,2316,2317,2318,2319,2320,2321,2322,2323,2324,2325,2326,2327,2328,2329,2330,2331,2332,2400,2401,2402,2403,2404,2405,2406,2407,2408,2500,2501,2502,2503,2504,2505,2600,2601,2602,2603],\"remark\":null,\"menuCheckStrictly\":true,\"status\":null}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:21:12',24),(2049494228909023234,'000000','租户管理',2,'org.sdkj.system.controller.system.SysTenantController.syncTenantPackage()','GET',1,'admin','总公司','/system/tenant/syncTenantPackage','0:0:0:0:0:0:0:1','内网IP','{\"packageId\":\"2049396343356542977\",\"tenantId\":\"376041\"}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:21:17',19),(2049495531416576002,'000000','角色管理',1,'org.sdkj.system.controller.system.SysRoleController.add()','POST',1,'admin','总公司','/system/role','0:0:0:0:0:0:0:1','内网IP','{\"createDept\":null,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"roleId\":\"2049495531223638017\",\"roleName\":\"测试角色\",\"roleKey\":\"testRole\",\"roleSort\":1,\"dataScope\":null,\"menuCheckStrictly\":null,\"deptCheckStrictly\":null,\"status\":\"0\",\"remark\":\"\",\"menuIds\":[1,100,101,102,103,104,105,106,107,108,109,118,121,122,123,2,2000,2001,2002,2003,2004,2005,2610,500,501,6,1001,1002,1003,1004,1005,1006,1007,131,1008,1009,1010,1011,1012,130,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,132,1031,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1050,1600,1601,1602,1603,1620,1621,1622,1623,133,1061,1062,1063,1064,1065,1046,1047,1048,113,1606,1607,1608,1609,1610,1611,1612,1613,1614,1615,2100,2101,2102,2103,2104,2105,2106,2107,2108,2109,2110,2200,2201,2202,2203,2204,2205,2206,2207,2208,2209,2210,2211,2212,2213,2214,2215,2216,2217,2218,2219,2220,2300,2301,2302,2303,2304,2305,2306,2307,2308,2309,2310,2311,2312,2313,2314,2315,2316,2317,2318,2319,2320,2321,2322,2323,2324,2325,2326,2327,2328,2329,2330,2331,2332,2400,2401,2402,2403,2404,2405,2406,2407,2408,2500,2501,2502,2503,2504,2505,2600,2601,2602,2603],\"deptIds\":null,\"superAdmin\":false}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:26:27',59),(2049495611448090625,'000000','角色管理',4,'org.sdkj.system.controller.system.SysRoleController.selectAuthUserAll()','PUT',1,'admin','总公司','/system/role/authUser/selectAll','0:0:0:0:0:0:0:1','内网IP','{\"roleId\":\"2049495531223638017\",\"userIds\":\"2049398001306193922\"}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:26:46',15),(2049496019608395778,'000000','租户管理',2,'org.sdkj.system.controller.system.SysTenantController.syncTenantPackage()','GET',1,'admin','总公司','/system/tenant/syncTenantPackage','0:0:0:0:0:0:0:1','内网IP','{\"packageId\":\"2049396343356542977\",\"tenantId\":\"376041\"}','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 22:28:24',9),(2049507623569780738,'000000','租户管理',3,'org.sdkj.system.controller.system.SysTenantController.remove()','DELETE',1,'admin','总公司','/system/tenant/2049494139826200578','0:0:0:0:0:0:0:1','内网IP','[\"2049494139826200578\"]','{\"code\":200,\"msg\":\"操作成功\",\"data\":null}',0,'','2026-04-29 23:14:30',52);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oss`
--

DROP TABLE IF EXISTS `sys_oss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oss` (
  `oss_id` bigint NOT NULL COMMENT '对象存储主键',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原名',
  `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件后缀名',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'URL地址',
  `ext1` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '扩展字段',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '上传人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `service` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'minio' COMMENT '服务商',
  PRIMARY KEY (`oss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OSS对象存储表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oss`
--

LOCK TABLES `sys_oss` WRITE;
/*!40000 ALTER TABLE `sys_oss` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_oss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oss_config`
--

DROP TABLE IF EXISTS `sys_oss_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oss_config` (
  `oss_config_id` bigint NOT NULL COMMENT '主键',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `config_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '配置key',
  `access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT 'accessKey',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '秘钥',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '桶名称',
  `prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '前缀',
  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '访问站点',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '自定义域名',
  `is_https` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'N' COMMENT '是否https（Y=是,N=否）',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '域',
  `access_policy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '是否默认（0=是,1=否）',
  `ext1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '扩展字段',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`oss_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对象存储配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oss_config`
--

LOCK TABLES `sys_oss_config` WRITE;
/*!40000 ALTER TABLE `sys_oss_config` DISABLE KEYS */;
INSERT INTO `sys_oss_config` VALUES (1,'000000','minio','ruoyi','ruoyi123','ruoyi','','127.0.0.1:9000','','N','','1','1','',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12',NULL),(2,'000000','qiniu','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','ruoyi','','s3-cn-north-1.qiniucs.com','','N','','1','1','',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12',NULL),(3,'000000','aliyun','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','ruoyi','','oss-cn-beijing.aliyuncs.com','','N','','1','1','',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12',NULL),(4,'000000','qcloud','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','ruoyi-1240000000','','cos.ap-beijing.myqcloud.com','','N','ap-beijing','1','1','',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12',NULL),(5,'000000','image','ruoyi','ruoyi123','ruoyi','image','127.0.0.1:9000','','N','','1','1','',103,1,'2026-04-23 15:56:12',1,'2026-04-23 15:56:12',NULL);
/*!40000 ALTER TABLE `sys_oss_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `dept_id` bigint NOT NULL COMMENT '部门id',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位编码',
  `post_category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '岗位类别编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'000000',100,'default',NULL,'默认岗位',1,'0',103,1,'2026-04-23 15:56:09',1,'2026-04-29 10:48:39','');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'000000','超级管理员','superadmin',1,'1',1,1,'0','0',103,1,'2026-04-23 15:56:09',NULL,NULL,'超级管理员'),(2049495531223638017,'000000','测试角色','testRole',1,'1',1,1,'0','0',100,1,'2026-04-29 22:26:27',1,'2026-04-29 22:26:27','');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2049495531223638017,1),(2049495531223638017,2),(2049495531223638017,6),(2049495531223638017,100),(2049495531223638017,101),(2049495531223638017,102),(2049495531223638017,103),(2049495531223638017,104),(2049495531223638017,105),(2049495531223638017,106),(2049495531223638017,107),(2049495531223638017,108),(2049495531223638017,109),(2049495531223638017,113),(2049495531223638017,118),(2049495531223638017,121),(2049495531223638017,122),(2049495531223638017,123),(2049495531223638017,130),(2049495531223638017,131),(2049495531223638017,132),(2049495531223638017,133),(2049495531223638017,500),(2049495531223638017,501),(2049495531223638017,1001),(2049495531223638017,1002),(2049495531223638017,1003),(2049495531223638017,1004),(2049495531223638017,1005),(2049495531223638017,1006),(2049495531223638017,1007),(2049495531223638017,1008),(2049495531223638017,1009),(2049495531223638017,1010),(2049495531223638017,1011),(2049495531223638017,1012),(2049495531223638017,1013),(2049495531223638017,1014),(2049495531223638017,1015),(2049495531223638017,1016),(2049495531223638017,1017),(2049495531223638017,1018),(2049495531223638017,1019),(2049495531223638017,1020),(2049495531223638017,1021),(2049495531223638017,1022),(2049495531223638017,1023),(2049495531223638017,1024),(2049495531223638017,1025),(2049495531223638017,1026),(2049495531223638017,1027),(2049495531223638017,1028),(2049495531223638017,1029),(2049495531223638017,1030),(2049495531223638017,1031),(2049495531223638017,1032),(2049495531223638017,1033),(2049495531223638017,1034),(2049495531223638017,1035),(2049495531223638017,1036),(2049495531223638017,1037),(2049495531223638017,1038),(2049495531223638017,1039),(2049495531223638017,1040),(2049495531223638017,1041),(2049495531223638017,1042),(2049495531223638017,1043),(2049495531223638017,1044),(2049495531223638017,1045),(2049495531223638017,1046),(2049495531223638017,1047),(2049495531223638017,1048),(2049495531223638017,1050),(2049495531223638017,1061),(2049495531223638017,1062),(2049495531223638017,1063),(2049495531223638017,1064),(2049495531223638017,1065),(2049495531223638017,1600),(2049495531223638017,1601),(2049495531223638017,1602),(2049495531223638017,1603),(2049495531223638017,1606),(2049495531223638017,1607),(2049495531223638017,1608),(2049495531223638017,1609),(2049495531223638017,1610),(2049495531223638017,1611),(2049495531223638017,1612),(2049495531223638017,1613),(2049495531223638017,1614),(2049495531223638017,1615),(2049495531223638017,1620),(2049495531223638017,1621),(2049495531223638017,1622),(2049495531223638017,1623),(2049495531223638017,2000),(2049495531223638017,2001),(2049495531223638017,2002),(2049495531223638017,2003),(2049495531223638017,2004),(2049495531223638017,2005),(2049495531223638017,2100),(2049495531223638017,2101),(2049495531223638017,2102),(2049495531223638017,2103),(2049495531223638017,2104),(2049495531223638017,2105),(2049495531223638017,2106),(2049495531223638017,2107),(2049495531223638017,2108),(2049495531223638017,2109),(2049495531223638017,2110),(2049495531223638017,2200),(2049495531223638017,2201),(2049495531223638017,2202),(2049495531223638017,2203),(2049495531223638017,2204),(2049495531223638017,2205),(2049495531223638017,2206),(2049495531223638017,2207),(2049495531223638017,2208),(2049495531223638017,2209),(2049495531223638017,2210),(2049495531223638017,2211),(2049495531223638017,2212),(2049495531223638017,2213),(2049495531223638017,2214),(2049495531223638017,2215),(2049495531223638017,2216),(2049495531223638017,2217),(2049495531223638017,2218),(2049495531223638017,2219),(2049495531223638017,2220),(2049495531223638017,2300),(2049495531223638017,2301),(2049495531223638017,2302),(2049495531223638017,2303),(2049495531223638017,2304),(2049495531223638017,2305),(2049495531223638017,2306),(2049495531223638017,2307),(2049495531223638017,2308),(2049495531223638017,2309),(2049495531223638017,2310),(2049495531223638017,2311),(2049495531223638017,2312),(2049495531223638017,2313),(2049495531223638017,2314),(2049495531223638017,2315),(2049495531223638017,2316),(2049495531223638017,2317),(2049495531223638017,2318),(2049495531223638017,2319),(2049495531223638017,2320),(2049495531223638017,2321),(2049495531223638017,2322),(2049495531223638017,2323),(2049495531223638017,2324),(2049495531223638017,2325),(2049495531223638017,2326),(2049495531223638017,2327),(2049495531223638017,2328),(2049495531223638017,2329),(2049495531223638017,2330),(2049495531223638017,2331),(2049495531223638017,2332),(2049495531223638017,2400),(2049495531223638017,2401),(2049495531223638017,2402),(2049495531223638017,2403),(2049495531223638017,2404),(2049495531223638017,2405),(2049495531223638017,2406),(2049495531223638017,2407),(2049495531223638017,2408),(2049495531223638017,2500),(2049495531223638017,2501),(2049495531223638017,2502),(2049495531223638017,2503),(2049495531223638017,2504),(2049495531223638017,2505),(2049495531223638017,2600),(2049495531223638017,2601),(2049495531223638017,2602),(2049495531223638017,2603),(2049495531223638017,2610);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_social`
--

DROP TABLE IF EXISTS `sys_social`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_social` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户id',
  `auth_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '平台+平台唯一id',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户来源',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '平台编号唯一id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户昵称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户邮箱',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '头像地址',
  `access_token` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户的授权令牌',
  `expire_in` int DEFAULT NULL COMMENT '用户的授权令牌的有效期，部分平台可能没有',
  `refresh_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '刷新令牌，部分平台可能没有',
  `access_code` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '平台的授权信息，部分平台可能没有',
  `union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户的 unionid',
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '授予的权限，部分平台可能没有',
  `token_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '个别平台的授权信息，部分平台可能没有',
  `id_token` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'id token，部分平台可能没有',
  `mac_algorithm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小米平台用户的附带属性，部分平台可能没有',
  `mac_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小米平台用户的附带属性，部分平台可能没有',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户的授权code，部分平台可能没有',
  `oauth_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'Twitter平台用户的附带属性，部分平台可能没有',
  `oauth_token_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'Twitter平台用户的附带属性，部分平台可能没有',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社会化关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_social`
--

LOCK TABLES `sys_social` WRITE;
/*!40000 ALTER TABLE `sys_social` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_social` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_tenant`
--

DROP TABLE IF EXISTS `sys_tenant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_tenant` (
  `id` bigint NOT NULL COMMENT 'id',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户编号',
  `contact_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `company_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '企业名称',
  `license_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '统一社会信用代码',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '地址',
  `nature` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '企业性质',
  `business_license` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '营业执照号',
  `legal_person` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '法人代表',
  `bank_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '开户银行',
  `bank_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '银行地址',
  `account_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '账户名称',
  `corporate_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '对公账号',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '城市',
  `county` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '区县',
  `intro` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '企业简介',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `domain` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '域名',
  `db_url` varchar(500) DEFAULT NULL COMMENT '租户数据库连接URL',
  `db_username` varchar(100) DEFAULT NULL COMMENT '租户数据库用户名',
  `db_password` varchar(200) DEFAULT NULL COMMENT '租户数据库密码',
  `db_driver` varchar(200) DEFAULT 'com.mysql.cj.jdbc.Driver' COMMENT '租户数据库驱动',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `package_id` bigint DEFAULT NULL COMMENT '租户套餐编号',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `account_count` int DEFAULT '-1' COMMENT '用户数量（-1不限制）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '租户状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_tenant`
--

LOCK TABLES `sys_tenant` WRITE;
/*!40000 ALTER TABLE `sys_tenant` DISABLE KEYS */;
INSERT INTO `sys_tenant` VALUES (1,'000000','管理组','15888888888','XXX有限公司',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'多租户通用后台管理管理系统',NULL,NULL,NULL,'jdbc:mysql://localhost:3306/tenant_000000?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8','root','root','com.mysql.cj.jdbc.Driver',NULL,NULL,NULL,-1,'0','0',103,1,'2026-04-23 15:56:09',NULL,NULL),(2049399038679207938,'107329','123','15866541233','测试企业1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'jdbc:mysql://localhost:3306/tenant_000000?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8','root','root','com.mysql.cj.jdbc.Driver',NULL,2049396343356542977,NULL,-1,'0','1',100,1,'2026-04-29 16:03:01',1,'2026-04-29 22:19:19'),(2049494139826200578,'376041','测试企业','15998652321','测试企业',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'jdbc:mysql://localhost:3306/tenant_test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8','root','root','com.mysql.cj.jdbc.Driver',NULL,2049396343356542977,NULL,-1,'0','1',100,1,'2026-04-29 22:20:55',1,'2026-04-29 23:14:30');
/*!40000 ALTER TABLE `sys_tenant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_tenant_package`
--

DROP TABLE IF EXISTS `sys_tenant_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_tenant_package` (
  `package_id` bigint NOT NULL COMMENT '租户套餐id',
  `package_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '套餐名称',
  `menu_ids` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '关联菜单id',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户套餐表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_tenant_package`
--

LOCK TABLES `sys_tenant_package` WRITE;
/*!40000 ALTER TABLE `sys_tenant_package` DISABLE KEYS */;
INSERT INTO `sys_tenant_package` VALUES (2049396343356542977,'test1','109,2,2000,2001,2002,2003,2004,2005,2610,1046,1047,1048,113,2100,2101,2102,2103,2104,2105,2106,2107,2108,2109,2110,2200,2201,2202,2203,2204,2205,2206,2207,2208,2209,2210,2211,2212,2213,2214,2215,2216,2217,2218,2219,2220,2300,2301,2302,2303,2304,2305,2306,2307,2308,2309,2310,2311,2312,2313,2314,2315,2316,2317,2318,2319,2320,2321,2322,2323,2324,2325,2326,2327,2328,2329,2330,2331,2332,2400,2401,2402,2403,2404,2405,2406,2407,2408,2500,2501,2502,2503,2504,2505,2600,2601,2602,2603',NULL,1,'0','0',100,1,'2026-04-29 15:52:19',1,'2026-04-29 22:21:12'),(2049398760622018562,'test2','1,100,101,102,103,104,105,106,107,108,118,123,500,501,1001,1002,1003,1004,1005,1006,1007,131,1008,1009,1010,1011,1012,130,1013,1014,1015,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,132,1031,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1050,1600,1601,1602,1603,1620,1621,1622,1623,133,1061,1062,1063,1064,1065',NULL,1,'0','0',100,1,'2026-04-29 16:01:55',1,'2026-04-29 16:01:55');
/*!40000 ALTER TABLE `sys_tenant_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_tenant_user`
--

DROP TABLE IF EXISTS `sys_tenant_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_tenant_user` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '系统用户ID (sys_user.id)',
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID (sys_tenant.tenant_id)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tenant` (`user_id`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-租户关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_tenant_user`
--

LOCK TABLES `sys_tenant_user` WRITE;
/*!40000 ALTER TABLE `sys_tenant_user` DISABLE KEYS */;
INSERT INTO `sys_tenant_user` VALUES (1,1,'000000','2026-04-28 21:58:17');
/*!40000 ALTER TABLE `sys_tenant_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000' COMMENT '租户编号',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'sys_user' COMMENT '用户类型（sys_user系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` bigint DEFAULT NULL COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'000000',100,'admin','系统管理员','sys_user','crazyLionLi@163.com','15888888888','1',NULL,'$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','0:0:0:0:0:0:0:1','2026-05-01 06:18:13',103,1,'2026-04-23 15:56:09',-1,'2026-05-01 06:18:13','管理员'),(2049398001306193922,'000000',100,'test','测试用户','sys_user','','','0',NULL,'$2a$10$DW4mldbEXNZkL3MTAx6RleqW40VqBkhMR6N60QEd2cgF6jOEYbRsi','0','0','0:0:0:0:0:0:0:1','2026-04-29 22:26:58',100,1,'2026-04-29 15:58:54',-1,'2026-04-29 22:26:58',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_ext`
--

DROP TABLE IF EXISTS `sys_user_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_ext` (
  `id` varchar(64) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `company_id` varchar(64) DEFAULT NULL,
  `wx_openid` varchar(128) DEFAULT NULL,
  `wx_number` varchar(128) DEFAULT NULL,
  `qq_number` varchar(64) DEFAULT NULL,
  `nation` varchar(32) DEFAULT NULL,
  `native_place` varchar(128) DEFAULT NULL,
  `nationality` varchar(32) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `is_realname` tinyint DEFAULT '0',
  `idcard` varchar(32) DEFAULT NULL,
  `real_name` varchar(64) DEFAULT NULL,
  `id_startdate` date DEFAULT NULL,
  `id_enddate` date DEFAULT NULL,
  `id_department` varchar(128) DEFAULT NULL,
  `is_super` tinyint DEFAULT '0',
  `create_dept` bigint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户扩展表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_ext`
--

LOCK TABLES `sys_user_ext` WRITE;
/*!40000 ALTER TABLE `sys_user_ext` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_ext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2049398001306193922,2049495531223638017);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'ry-vue'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-01  6:44:33
