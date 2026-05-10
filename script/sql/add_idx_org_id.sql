-- Add idx_org_id indexes to replace removed idx_company_org
-- Generated: 2026-05-10

ALTER TABLE `pm_parking_space` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_abnormal_record` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_account_balance` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_approval` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_expense` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_expense_item` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_expense_log` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_heat_daily` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_heat_month` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_heat_reading` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_house_expense` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_house_log` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_import_account` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_inspection_plan` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_inspection_record` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_notice` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_operate_card_log` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_options` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_options_heat` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_pet` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_print_template` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_repair_record` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_scheduling` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_standard` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_strategy` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_transaction_detail` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_transaction_record` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_use_card_log` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_user` ADD INDEX `idx_org_id` (`org_id`);
ALTER TABLE `pr_wechat_order` ADD INDEX `idx_org_id` (`org_id`);