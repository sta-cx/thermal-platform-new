-- ============================================================
-- 多租户独立库架构 - master 库 schema 变更
-- 执行目标: ry-vue (master 库)
-- ============================================================

-- Step 1: 扩展 sys_tenant 表，添加租户数据库连接信息
ALTER TABLE sys_tenant
  ADD COLUMN db_url VARCHAR(500) NULL COMMENT '租户数据库连接URL' AFTER domain,
  ADD COLUMN db_username VARCHAR(100) NULL COMMENT '租户数据库用户名' AFTER db_url,
  ADD COLUMN db_password VARCHAR(200) NULL COMMENT '租户数据库密码' AFTER db_username,
  ADD COLUMN db_driver VARCHAR(200) NULL DEFAULT 'com.mysql.cj.jdbc.Driver' COMMENT '租户数据库驱动' AFTER db_password;

-- Step 2: 创建 sys_tenant_user 用户-租户关联表
CREATE TABLE IF NOT EXISTS sys_tenant_user (
  id BIGINT NOT NULL PRIMARY KEY COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '系统用户ID (sys_user.id)',
  tenant_id VARCHAR(20) NOT NULL COMMENT '租户ID (sys_tenant.tenant_id)',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  UNIQUE KEY uk_user_tenant (user_id, tenant_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-租户关联表';

-- Step 3: 为现有用户建立默认租户映射 (tenant_id = '000000')
INSERT INTO sys_tenant_user (id, user_id, tenant_id, create_time)
SELECT user_id, user_id, '000000', NOW()
FROM sys_user
WHERE del_flag = '0' AND status = '0';

-- Step 4: 更新默认租户的数据库连接（临时指向当前 ry-vue 库）
UPDATE sys_tenant
SET db_url = 'jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8',
    db_username = 'root',
    db_password = 'root'
WHERE tenant_id = '000000';
