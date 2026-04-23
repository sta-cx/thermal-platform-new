-- Phase 2 数据迁移脚本
-- 将旧系统 rltk_pro.sys_* 表数据迁移到新系统 ry-vue 对应表
-- 执行前请备份 ry-vue 数据库
--
-- 使用方法: mysql -u root -proot ry-vue < phase2_data_migration.sql

-- ============================================
-- 1. sys_dict → sys_dict_type + sys_dict_data
-- 旧系统: sys_dict(id, name, value, type, sort)
-- 新系统: sys_dict_type(id, dict_name, dict_type) + sys_dict_data(dict_code, dict_type, dict_label, dict_value, sort)
-- ============================================

-- 提取字典类型（去重）
INSERT IGNORE INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
SELECT
    MIN(id) as dict_id,
    type as dict_name,
    type as dict_type,
    '0' as status,
    1 as create_by,
    NOW() as create_time,
    '从旧系统迁移' as remark
FROM rltk_pro.sys_dict
GROUP BY type;

-- 提取字典数据
INSERT IGNORE INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT
    d.id as dict_code,
    d.sort as dict_sort,
    d.name as dict_label,
    d.value as dict_value,
    d.type as dict_type,
    '0' as status,
    1 as create_by,
    NOW() as create_time
FROM rltk_pro.sys_dict d;

-- ============================================
-- 2. sys_log → sys_oper_log
-- 旧系统: sys_log(id, user_id, operation, method, params, ip, create_time)
-- 新系统: sys_oper_log(oper_id, title, business_type, method, request_method, oper_name, oper_url, oper_ip, oper_param, json_result, status, oper_time)
-- ============================================

INSERT IGNORE INTO sys_oper_log (
    oper_id, title, business_type, method, request_method,
    oper_name, oper_url, oper_ip, oper_param, json_result,
    status, oper_time
)
SELECT
    l.id as oper_id,
    '旧系统操作日志' as title,
    0 as business_type,
    l.method,
    'POST' as request_method,
    (SELECT user_name FROM rltk_pro.sys_user WHERE id = l.user_id LIMIT 1) as oper_name,
    l.method as oper_url,
    l.ip as oper_ip,
    l.params as oper_param,
    NULL as json_result,
    '0' as status,
    l.create_time as oper_time
FROM rltk_pro.sys_log l
LIMIT 10000;

-- ============================================
-- 3. oauth_client_details → sys_client
-- 旧系统 OAuth2 客户端迁移到 RuoYi 客户端表
-- ============================================

INSERT IGNORE INTO sys_client (
    id, client_id, client_key, client_secret, grant_type,
    scope, status, create_by, create_time
)
SELECT
    UUID_SHORT() as id,
    client_id,
    client_id as client_key,
    client_secret,
    'password,authorization_code,refresh_token' as grant_type,
    COALESCE(scope, 'all') as scope,
    '0' as status,
    1 as create_by,
    NOW() as create_time
FROM rltk_pro.oauth_client_details;

-- ============================================
-- 4. sys_company → sys_dept (公司→部门)
-- ============================================

INSERT IGNORE INTO sys_dept (
    dept_id, parent_id, ancestors, dept_name, order_num, leader, status, create_by, create_time
)
SELECT
    c.id as dept_id,
    100 as parent_id,
    '0,100' as ancestors,
    c.name as dept_name,
    0 as order_num,
    NULL as leader,
    '0' as status,
    1 as create_by,
    NOW() as create_time
FROM rltk_pro.sys_company c;

-- ============================================
-- 注意事项
-- ============================================
-- 1. sys_user: 旧系统使用 BCrypt + AES 混合加密，新系统使用 BCrypt。
--    存量用户密码需要在登录时逐步迁移，或强制用户重置密码。
-- 2. sys_menu: RuoYi 菜单表结构差异较大（增加了 component, perms 等字段），
--    建议在 Phase 6 前端迁移时通过手动重建菜单，而非自动迁移。
-- 3. sys_tenant: RuoYi 已原生支持多租户，旧 sys_tenant 数据可迁移到
--    sys_tenant 表，但 sys_tenant_user 的映射关系需重新建立。
