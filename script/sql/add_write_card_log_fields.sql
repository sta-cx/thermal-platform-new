-- 写卡迁移（阶段6）：pr_use_card_log 加写卡日志三列，向后兼容阶段5 valve-operation-log
-- 各租户库分别执行
ALTER TABLE `pr_use_card_log`
  ADD COLUMN `type` varchar(8) DEFAULT NULL COMMENT '操作类型 1写卡 2开卡 3补卡',
  ADD COLUMN `card_type` varchar(8) DEFAULT NULL COMMENT '卡类型',
  ADD COLUMN `content` longtext COMMENT '写卡报文JSON';
