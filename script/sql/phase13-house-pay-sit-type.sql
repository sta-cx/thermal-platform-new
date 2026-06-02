-- phase13: pr_house 增加「缴费位置属性」列 pay_sit_type
--
-- 用途: 监控模块筛选栏"分组"(孤岛户/上不供户/下不供户/正常户)的底层字段。
-- 背景: 老系统 pr_house.pay_sit_type(tinyint, 1孤岛/2上不供/3下不供/4正常) 在新系统迁移时遗漏。
--       此前 MonitorBo.houseType 被误 eq 到 station_type，但 station_type 实为「分区id」
--       (= pr_heat_station_partition.id)，不能复用。故新增专属列。
-- 关联: 分区筛选用 station_type；本列只服务 houseType(分组) 筛选与列显示。
--
-- 在每个租户业务库执行（新建租户库由 tenant_db_schema.sql 自动包含此列）。

ALTER TABLE `pr_house`
  ADD COLUMN `pay_sit_type` tinyint DEFAULT NULL
  COMMENT '缴费位置属性(1孤岛/2上不供/3下不供/4正常)' AFTER `is_special`;
