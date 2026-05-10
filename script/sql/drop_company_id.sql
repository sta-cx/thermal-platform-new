-- DDL Migration: Drop company_id from business tables
-- Generated: 2026-05-10
-- Target: tenant databases

ALTER TABLE `pr_expense`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_expense_item`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_expense_log`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_command_unit_valve_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_command_valve_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_daily`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_dtu_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_hot_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_month`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_reading`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_real_data`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_station`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_station_partition`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_temp_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_unit_hot_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_unit_valve_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_heat_valve_archive`
  DROP INDEX `idx_company_id`,
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_house_expense`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_house_log`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_account`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_basic_data`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_heat`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_heat_temp`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_history`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_record`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_unit_heat`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_unit_valve`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_import_valve`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_inspection_equipment`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_inspection_person`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_inspection_plan`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_inspection_record`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_notice`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_operate_card_log`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_options`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_options_heat`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_pet`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_print_template`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_reconciliation_diff`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_repair_person`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_repair_record`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_scheduling`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_standard`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_standard_price`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_strategy`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_transaction_detail`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_transaction_record`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_use_card_log`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_user`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_user_house`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_wechat_bill`
  DROP INDEX `idx_company_id`,
  DROP COLUMN `company_id`;

ALTER TABLE `pr_wechat_bind_record`
  DROP COLUMN `company_id`;

ALTER TABLE `pr_wechat_order`
  DROP INDEX `idx_company_org`,
  DROP COLUMN `company_id`;
