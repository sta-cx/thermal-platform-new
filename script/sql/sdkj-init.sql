-- ----------------------------
-- 第三方平台授权表
-- ----------------------------
create table sys_social
(
    id                 bigint           not null        comment '主键',
    user_id            bigint           not null        comment '用户ID',
    tenant_id          varchar(20)      default '000000' comment '租户id',
    auth_id            varchar(255)     not null        comment '平台+平台唯一id',
    source             varchar(255)     not null        comment '用户来源',
    open_id            varchar(255)     default null    comment '平台编号唯一id',
    user_name          varchar(30)      not null        comment '登录账号',
    nick_name          varchar(30)      default ''      comment '用户昵称',
    email              varchar(255)     default ''      comment '用户邮箱',
    avatar             varchar(500)     default ''      comment '头像地址',
    access_token       varchar(2000)     not null       comment '用户的授权令牌',
    expire_in          int              default null    comment '用户的授权令牌的有效期，部分平台可能没有',
    refresh_token      varchar(255)     default null    comment '刷新令牌，部分平台可能没有',
    access_code        varchar(2000)     default null   comment '平台的授权信息，部分平台可能没有',
    union_id           varchar(255)     default null    comment '用户的 unionid',
    scope              varchar(255)     default null    comment '授予的权限，部分平台可能没有',
    token_type         varchar(255)     default null    comment '个别平台的授权信息，部分平台可能没有',
    id_token           varchar(2000)    default null    comment 'id token，部分平台可能没有',
    mac_algorithm      varchar(255)     default null    comment '小米平台用户的附带属性，部分平台可能没有',
    mac_key            varchar(255)     default null    comment '小米平台用户的附带属性，部分平台可能没有',
    code               varchar(255)     default null    comment '用户的授权code，部分平台可能没有',
    oauth_token        varchar(255)     default null    comment 'Twitter平台用户的附带属性，部分平台可能没有',
    oauth_token_secret varchar(255)     default null    comment 'Twitter平台用户的附带属性，部分平台可能没有',
    create_dept        bigint(20)                       comment '创建部门',
    create_by          bigint(20)                       comment '创建者',
    create_time        datetime                         comment '创建时间',
    update_by          bigint(20)                       comment '更新者',
    update_time        datetime                         comment '更新时间',
    del_flag           char(1)          default '0'     comment '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id)
) engine=innodb comment = '社会化关系表';


-- ----------------------------
-- 租户表
-- ----------------------------
create table sys_tenant
(
    id                bigint(20)    not null        comment 'id',
    tenant_id         varchar(20)   not null        comment '租户编号',
    contact_user_name varchar(20)                   comment '联系人',
    contact_phone     varchar(20)                   comment '联系电话',
    company_name      varchar(30)                   comment '企业名称',
    license_number    varchar(30)                   comment '统一社会信用代码',
    address           varchar(200)                  comment '地址',
    intro             varchar(200)                  comment '企业简介',
    domain            varchar(200)                  comment '域名',
    remark            varchar(200)                  comment '备注',
    package_id        bigint(20)                    comment '租户套餐编号',
    expire_time       datetime                      comment '过期时间',
    account_count     int           default -1      comment '用户数量（-1不限制）',
    status            char(1)       default '0'     comment '租户状态（0正常 1停用）',
    del_flag          char(1)       default '0'     comment '删除标志（0代表存在 1代表删除）',
    create_dept       bigint(20)                    comment '创建部门',
    create_by         bigint(20)                    comment '创建者',
    create_time       datetime                      comment '创建时间',
    update_by         bigint(20)                    comment '更新者',
    update_time       datetime                      comment '更新时间',
    primary key (id)
) engine=innodb comment = '租户表';


-- ----------------------------
-- 初始化-租户表数据
-- ----------------------------

insert into sys_tenant values(1, '000000', '管理组', '15888888888', 'XXX有限公司', null, null, '多租户通用后台管理管理系统', null, null, null, null, -1, '0', '0', 103, 1, sysdate(), null, null);


-- ----------------------------
-- 租户套餐表
-- ----------------------------
create table sys_tenant_package (
    package_id              bigint(20)     not null    comment '租户套餐id',
    package_name            varchar(20)                comment '套餐名称',
    menu_ids                varchar(3000)              comment '关联菜单id',
    remark                  varchar(200)               comment '备注',
    menu_check_strictly     tinyint(1)     default 1   comment '菜单树选择项是否关联显示',
    status                  char(1)        default '0' comment '状态（0正常 1停用）',
    del_flag                char(1)        default '0' comment '删除标志（0代表存在 1代表删除）',
    create_dept             bigint(20)                 comment '创建部门',
    create_by               bigint(20)                 comment '创建者',
    create_time             datetime                   comment '创建时间',
    update_by               bigint(20)                 comment '更新者',
    update_time             datetime                   comment '更新时间',
    primary key (package_id)
) engine=innodb comment = '租户套餐表';


-- ----------------------------
-- 1、部门表
-- ----------------------------
create table sys_dept (
    dept_id           bigint(20)      not null                   comment '部门id',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    parent_id         bigint(20)      default 0                  comment '父部门id',
    ancestors         varchar(500)    default ''                 comment '祖级列表',
    dept_name         varchar(30)     default ''                 comment '部门名称',
    dept_category     varchar(100)    default null               comment '部门类别编码',
    order_num         int(4)          default 0                  comment '显示顺序',
    leader            bigint(20)      default null               comment '负责人',
    phone             varchar(11)     default null               comment '联系电话',
    email             varchar(50)     default null               comment '邮箱',
    status            char(1)         default '0'                comment '部门状态（0正常 1停用）',
    del_flag          char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    primary key (dept_id)
) engine=innodb comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------


insert into sys_dept values(100, '000000', 0,   '0',          'XXX科技',   null,0, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(101, '000000', 100, '0,100',      '深圳总公司', null,1, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(102, '000000', 100, '0,100',      '长沙分公司', null,2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(103, '000000', 101, '0,100,101',  '研发部门',   null,1, 1, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(104, '000000', 101, '0,100,101',  '市场部门',   null,2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(105, '000000', 101, '0,100,101',  '测试部门',   null,3, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(106, '000000', 101, '0,100,101',  '财务部门',   null,4, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(107, '000000', 101, '0,100,101',  '运维部门',   null,5, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(108, '000000', 102, '0,100,102',  '市场部门',   null,1, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(109, '000000', 102, '0,100,102',  '财务部门',   null,2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);


-- ----------------------------
-- 2、用户信息表
-- ----------------------------
create table sys_user (
    user_id           bigint(20)      not null                   comment '用户ID',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    dept_id           bigint(20)      default null               comment '部门ID',
    user_name         varchar(30)     not null                   comment '用户账号',
    nick_name         varchar(30)     not null                   comment '用户昵称',
    user_type         varchar(10)     default 'sys_user'         comment '用户类型（sys_user系统用户）',
    email             varchar(50)     default ''                 comment '用户邮箱',
    phonenumber       varchar(11)     default ''                 comment '手机号码',
    sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
    avatar            bigint(20)                                 comment '头像地址',
    password          varchar(100)    default ''                 comment '密码',
    status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
    del_flag          char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
    login_ip          varchar(128)    default ''                 comment '最后登录IP',
    login_date        datetime                                   comment '最后登录时间',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (user_id)
) engine=innodb comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1, '000000', 103, 'admin', '疯狂的狮子Li', 'sys_user', 'crazyLionLi@163.com', '15888888888', '1', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '管理员');
insert into sys_user values(3, '000000', 108, 'test', '本部门及以下 密码666666', 'sys_user', '', '', '0', null, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), 3, sysdate(), null);
insert into sys_user values(4, '000000', 102, 'test1', '仅本人 密码666666', 'sys_user', '', '', '0', null, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), 4, sysdate(), null);

-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
create table sys_post
(
    post_id       bigint(20)      not null                   comment '岗位ID',
    tenant_id     varchar(20)     default '000000'           comment '租户编号',
    dept_id       bigint(20)      not null                   comment '部门id',
    post_code     varchar(64)     not null                   comment '岗位编码',
    post_category varchar(100)    default null               comment '岗位类别编码',
    post_name     varchar(50)     not null                   comment '岗位名称',
    post_sort     int(4)          not null                   comment '显示顺序',
    status        char(1)         not null                   comment '状态（0正常 1停用）',
    create_dept   bigint(20)      default null               comment '创建部门',
    create_by     bigint(20)      default null               comment '创建者',
    create_time   datetime                                   comment '创建时间',
    update_by     bigint(20)      default null               comment '更新者',
    update_time   datetime                                   comment '更新时间',
    remark        varchar(500)    default null               comment '备注',
    primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, '000000', 103, 'ceo',  null, '董事长',    1, '0', 103, 1, sysdate(), null, null, '');
insert into sys_post values(2, '000000', 100, 'se',   null, '项目经理',  2, '0', 103, 1, sysdate(), null, null, '');
insert into sys_post values(3, '000000', 100, 'hr',   null, '人力资源',  3, '0', 103, 1, sysdate(), null, null, '');
insert into sys_post values(4, '000000', 100, 'user', null, '普通员工',  4, '0', 103, 1, sysdate(), null, null, '');


-- ----------------------------
-- 4、角色信息表
-- ----------------------------
create table sys_role (
    role_id              bigint(20)      not null                   comment '角色ID',
    tenant_id            varchar(20)     default '000000'           comment '租户编号',
    role_name            varchar(30)     not null                   comment '角色名称',
    role_key             varchar(100)    not null                   comment '角色权限字符串',
    role_sort            int(4)          not null                   comment '显示顺序',
    data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）',
    menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
    dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
    status               char(1)         not null                   comment '角色状态（0正常 1停用）',
    del_flag             char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
    create_dept          bigint(20)      default null               comment '创建部门',
    create_by            bigint(20)      default null               comment '创建者',
    create_time          datetime                                   comment '创建时间',
    update_by            bigint(20)      default null               comment '更新者',
    update_time          datetime                                   comment '更新时间',
    remark               varchar(500)    default null               comment '备注',
    primary key (role_id)
) engine=innodb comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values(1, '000000', '超级管理员',  'superadmin',  1, 1, 1, 1, '0', '0', 103, 1, sysdate(), null, null, '超级管理员');
insert into sys_role values(3, '000000', '本部门及以下', 'test1', 3, 4, 1, 1, '0', '0', 103, 1, sysdate(), null, null, '');
insert into sys_role values(4, '000000', '仅本人',      'test2', 4, 5, 1, 1, '0', '0', 103, 1, sysdate(), null, null, '');

-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
create table sys_menu (
    menu_id           bigint(20)      not null                   comment '菜单ID',
    menu_name         varchar(50)     not null                   comment '菜单名称',
    parent_id         bigint(20)      default 0                  comment '父菜单ID',
    order_num         int(4)          default 0                  comment '显示顺序',
    path              varchar(200)    default ''                 comment '路由地址',
    component         varchar(255)    default null               comment '组件路径',
    query_param       varchar(255)    default null               comment '路由参数',
    is_frame          int(1)          default 1                  comment '是否为外链（0是 1否）',
    is_cache          int(1)          default 0                  comment '是否缓存（0缓存 1不缓存）',
    menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
    visible           char(1)         default 0                  comment '显示状态（0显示 1隐藏）',
    status            char(1)         default 0                  comment '菜单状态（0正常 1停用）',
    perms             varchar(100)    default null               comment '权限标识',
    icon              varchar(100)    default '#'                comment '菜单图标',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default ''                 comment '备注',
    primary key (menu_id)
) engine=innodb comment = '菜单权限表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '1', 'system',           null, '', 1, 0, 'M', '0', '0', '', 'system',   103, 1, sysdate(), null, null, '系统管理目录');
insert into sys_menu values('6', '租户管理', '0', '2', 'tenant',           null, '', 1, 0, 'M', '0', '0', '', 'chart',    103, 1, sysdate(), null, null, '租户管理目录');
insert into sys_menu values('2', '系统监控', '0', '3', 'monitor',          null, '', 1, 0, 'M', '0', '0', '', 'monitor',  103, 1, sysdate(), null, null, '系统监控目录');
insert into sys_menu values('3', '系统工具', '0', '4', 'tool',             null, '', 1, 0, 'M', '0', '0', '', 'tool',     103, 1, sysdate(), null, null, '系统工具目录');
insert into sys_menu values('4', 'PLUS官网', '0', '5', 'https://gitee.com/dromara/RuoYi-Vue-Plus', null, '', 0, 0, 'M', '0', '0', '', 'guide',    103, 1, sysdate(), null, null, 'RuoYi-Vue-Plus官网地址');
insert into sys_menu values('5', '测试菜单', '0', '5', 'demo',             null, '', 1, 0, 'M', '0', '0', '', 'star',     103, 1, sysdate(), null, null, '测试菜单');
-- 二级菜单
insert into sys_menu values('100',  '用户管理',     '1',   '1', 'user',             'system/user/index',            '', 1, 0, 'C', '0', '0', 'system:user:list',            'user',          103, 1, sysdate(), null, null, '用户管理菜单');
insert into sys_menu values('101',  '角色管理',     '1',   '2', 'role',             'system/role/index',            '', 1, 0, 'C', '0', '0', 'system:role:list',            'peoples',       103, 1, sysdate(), null, null, '角色管理菜单');
insert into sys_menu values('102',  '菜单管理',     '1',   '3', 'menu',             'system/menu/index',            '', 1, 0, 'C', '0', '0', 'system:menu:list',            'tree-table',    103, 1, sysdate(), null, null, '菜单管理菜单');
insert into sys_menu values('103',  '部门管理',     '1',   '4', 'dept',             'system/dept/index',            '', 1, 0, 'C', '0', '0', 'system:dept:list',            'tree',          103, 1, sysdate(), null, null, '部门管理菜单');
insert into sys_menu values('104',  '岗位管理',     '1',   '5', 'post',             'system/post/index',            '', 1, 0, 'C', '0', '0', 'system:post:list',            'post',          103, 1, sysdate(), null, null, '岗位管理菜单');
insert into sys_menu values('105',  '字典管理',     '1',   '6', 'dict',             'system/dict/index',            '', 1, 0, 'C', '0', '0', 'system:dict:list',            'dict',          103, 1, sysdate(), null, null, '字典管理菜单');
insert into sys_menu values('106',  '参数设置',     '1',   '7', 'config',           'system/config/index',          '', 1, 0, 'C', '0', '0', 'system:config:list',          'edit',          103, 1, sysdate(), null, null, '参数设置菜单');
insert into sys_menu values('107',  '通知公告',     '1',   '8', 'notice',           'system/notice/index',          '', 1, 0, 'C', '0', '0', 'system:notice:list',          'message',       103, 1, sysdate(), null, null, '通知公告菜单');
insert into sys_menu values('108',  '日志管理',     '1',   '9', 'log',              '',                             '', 1, 0, 'M', '0', '0', '',                            'log',           103, 1, sysdate(), null, null, '日志管理菜单');
insert into sys_menu values('109',  '在线用户',     '2',   '1', 'online',           'monitor/online/index',         '', 1, 0, 'C', '0', '0', 'monitor:online:list',         'online',        103, 1, sysdate(), null, null, '在线用户菜单');
insert into sys_menu values('113',  '缓存监控',     '2',   '5', 'cache',            'monitor/cache/index',          '', 1, 0, 'C', '0', '0', 'monitor:cache:list',          'redis',         103, 1, sysdate(), null, null, '缓存监控菜单');
insert into sys_menu values('115',  '代码生成',     '3',   '2', 'gen',              'tool/gen/index',               '', 1, 0, 'C', '0', '0', 'tool:gen:list',               'code',          103, 1, sysdate(), null, null, '代码生成菜单');
insert into sys_menu values('121',  '租户管理',     '6',   '1', 'tenant',           'system/tenant/index',          '', 1, 0, 'C', '0', '0', 'system:tenant:list',          'list',          103, 1, sysdate(), null, null, '租户管理菜单');
insert into sys_menu values('122',  '租户套餐管理',  '6',   '2', 'tenantPackage',    'system/tenantPackage/index',   '', 1, 0, 'C', '0', '0', 'system:tenantPackage:list',   'form',          103, 1, sysdate(), null, null, '租户套餐管理菜单');
insert into sys_menu values('123',  '客户端管理',   '1',   '11', 'client',           'system/client/index',          '', 1, 0, 'C', '0', '0', 'system:client:list',          'international', 103, 1, sysdate(), null, null, '客户端管理菜单');
insert into sys_menu values('116', '修改生成配置',  '3',   '2', 'gen-edit/index/:tableId', 'tool/gen/editTable', '', 1, 1, 'C', '1', '0', 'tool:gen:edit',           '#',               103, 1, sysdate(), null, null, '/tool/gen');
insert into sys_menu values('130', '分配用户',     '1',   '2', 'role-auth/user/:roleId', 'system/role/authUser', '', 1, 1, 'C', '1', '0', 'system:role:edit',      '#',               103, 1, sysdate(), null, null, '/system/role');
insert into sys_menu values('131', '分配角色',     '1',   '1', 'user-auth/role/:userId', 'system/user/authRole', '', 1, 1, 'C', '1', '0', 'system:user:edit',      '#',               103, 1, sysdate(), null, null, '/system/user');
insert into sys_menu values('132', '字典数据',     '1',   '6', 'dict-data/index/:dictId', 'system/dict/data', '', 1, 1, 'C', '1', '0', 'system:dict:list',         '#',               103, 1, sysdate(), null, null, '/system/dict');
insert into sys_menu values('133', '文件配置管理',  '1',   '10', 'oss-config/index',              'system/oss/config', '', 1, 1, 'C', '1', '0', 'system:ossConfig:list',  '#',                103, 1, sysdate(), null, null, '/system/oss');

-- springboot-admin监控
insert into sys_menu values('117',  'Admin监控',   '2',   '5',  'Admin',            'monitor/admin/index',         '', 1, 0, 'C', '0', '0', 'monitor:admin:list',           'dashboard',     103, 1, sysdate(), null, null, 'Admin监控菜单');
-- oss菜单
insert into sys_menu values('118',  '文件管理',     '1',   '10', 'oss',              'system/oss/index',            '', 1, 0, 'C', '0', '0', 'system:oss:list',              'upload',        103, 1, sysdate(), null, null, '文件管理菜单');
-- snail-job server控制台
insert into sys_menu values('120',  '任务调度中心',  '2',   '6',  'snailjob',     'monitor/snailjob/index',    '', 1, 0, 'C', '0', '0', 'monitor:snailjob:list',          'job',           103, 1, sysdate(), null, null, 'SnailJob控制台菜单');

-- 三级菜单
insert into sys_menu values('500',  '操作日志', '108', '1', 'operlog',    'monitor/operlog/index',    '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'form',          103, 1, sysdate(), null, null, '操作日志菜单');
insert into sys_menu values('501',  '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor',    103, 1, sysdate(), null, null, '登录日志菜单');
-- 用户管理按钮
insert into sys_menu values('1001', '用户查询', '100', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1002', '用户新增', '100', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1003', '用户修改', '100', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1004', '用户删除', '100', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1005', '用户导出', '100', '5',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1006', '用户导入', '100', '6',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1007', '重置密码', '100', '7',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',       '#', 103, 1, sysdate(), null, null, '');
-- 角色管理按钮
insert into sys_menu values('1008', '角色查询', '101', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1009', '角色新增', '101', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1010', '角色修改', '101', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1011', '角色删除', '101', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1012', '角色导出', '101', '5',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',         '#', 103, 1, sysdate(), null, null, '');
-- 菜单管理按钮
insert into sys_menu values('1013', '菜单查询', '102', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1014', '菜单新增', '102', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1015', '菜单修改', '102', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1016', '菜单删除', '102', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove',         '#', 103, 1, sysdate(), null, null, '');
-- 部门管理按钮
insert into sys_menu values('1017', '部门查询', '103', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1018', '部门新增', '103', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1019', '部门修改', '103', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1020', '部门删除', '103', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove',         '#', 103, 1, sysdate(), null, null, '');
-- 岗位管理按钮
insert into sys_menu values('1021', '岗位查询', '104', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1022', '岗位新增', '104', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1023', '岗位修改', '104', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1024', '岗位删除', '104', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1025', '岗位导出', '104', '5',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:export',         '#', 103, 1, sysdate(), null, null, '');
-- 字典管理按钮
insert into sys_menu values('1026', '字典查询', '105', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1027', '字典新增', '105', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1028', '字典修改', '105', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1029', '字典删除', '105', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1030', '字典导出', '105', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export',         '#', 103, 1, sysdate(), null, null, '');
-- 参数设置按钮
insert into sys_menu values('1031', '参数查询', '106', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1032', '参数新增', '106', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1033', '参数修改', '106', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1034', '参数删除', '106', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1035', '参数导出', '106', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export',       '#', 103, 1, sysdate(), null, null, '');
-- 通知公告按钮
insert into sys_menu values('1036', '公告查询', '107', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1037', '公告新增', '107', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1038', '公告修改', '107', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1039', '公告删除', '107', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove',       '#', 103, 1, sysdate(), null, null, '');
-- 操作日志按钮
insert into sys_menu values('1040', '操作查询', '500', '1', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query',      '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1041', '操作删除', '500', '2', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1042', '日志导出', '500', '4', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export',     '#', 103, 1, sysdate(), null, null, '');
-- 登录日志按钮
insert into sys_menu values('1043', '登录查询', '501', '1', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1044', '登录删除', '501', '2', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1045', '日志导出', '501', '3', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1050', '账户解锁', '501', '4', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock',  '#', 103, 1, sysdate(), null, null, '');
-- 在线用户按钮
insert into sys_menu values('1046', '在线查询', '109', '1', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1047', '批量强退', '109', '2', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1048', '单条强退', '109', '3', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 103, 1, sysdate(), null, null, '');
-- 代码生成按钮
insert into sys_menu values('1055', '生成查询', '115', '1', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',             '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1056', '生成修改', '115', '2', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',              '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1057', '生成删除', '115', '3', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1058', '导入代码', '115', '2', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1059', '预览代码', '115', '4', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1060', '生成代码', '115', '5', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',              '#', 103, 1, sysdate(), null, null, '');
-- oss相关按钮
insert into sys_menu values('1600', '文件查询', '118', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1601', '文件上传', '118', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:upload',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1602', '文件下载', '118', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:download',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1603', '文件删除', '118', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:remove',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1620', '配置列表', '118', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:list',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1621', '配置添加', '118', '6', '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:add',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1622', '配置编辑', '118', '6', '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:edit',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1623', '配置删除', '118', '6', '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:remove',      '#', 103, 1, sysdate(), null, null, '');

-- 租户管理相关按钮
insert into sys_menu values ('1606', '租户查询', '121', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1607', '租户新增', '121', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:add',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1608', '租户修改', '121', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:edit',    '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1609', '租户删除', '121', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1610', '租户导出', '121', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:export',  '#', 103, 1, sysdate(), null, null, '');
-- 租户套餐管理相关按钮
insert into sys_menu values ('1611', '租户套餐查询', '122', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1612', '租户套餐新增', '122', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:add',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1613', '租户套餐修改', '122', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:edit',    '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1614', '租户套餐删除', '122', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1615', '租户套餐导出', '122', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:export',  '#', 103, 1, sysdate(), null, null, '');
-- 客户端管理按钮
insert into sys_menu values('1061', '客户端管理查询', '123', '1',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1062', '客户端管理新增', '123', '2',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1063', '客户端管理修改', '123', '3',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:edit',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1064', '客户端管理删除', '123', '4',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:remove',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1065', '客户端管理导出', '123', '5',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:export',       '#', 103, 1, sysdate(), null, null, '');

-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
create table sys_user_role (
    user_id   bigint(20) not null comment '用户ID',
    role_id   bigint(20) not null comment '角色ID',
    primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('3', '3');
insert into sys_user_role values ('4', '4');

-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
create table sys_role_menu (
    role_id   bigint(20) not null comment '角色ID',
    menu_id   bigint(20) not null comment '菜单ID',
    primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('3', '1');
insert into sys_role_menu values ('3', '5');
insert into sys_role_menu values ('3', '100');
insert into sys_role_menu values ('3', '101');
insert into sys_role_menu values ('3', '102');
insert into sys_role_menu values ('3', '103');
insert into sys_role_menu values ('3', '104');
insert into sys_role_menu values ('3', '105');
insert into sys_role_menu values ('3', '106');
insert into sys_role_menu values ('3', '107');
insert into sys_role_menu values ('3', '108');
insert into sys_role_menu values ('3', '118');
insert into sys_role_menu values ('3', '123');
insert into sys_role_menu values ('3', '130');
insert into sys_role_menu values ('3', '131');
insert into sys_role_menu values ('3', '132');
insert into sys_role_menu values ('3', '133');
insert into sys_role_menu values ('3', '500');
insert into sys_role_menu values ('3', '501');
insert into sys_role_menu values ('3', '1001');
insert into sys_role_menu values ('3', '1002');
insert into sys_role_menu values ('3', '1003');
insert into sys_role_menu values ('3', '1004');
insert into sys_role_menu values ('3', '1005');
insert into sys_role_menu values ('3', '1006');
insert into sys_role_menu values ('3', '1007');
insert into sys_role_menu values ('3', '1008');
insert into sys_role_menu values ('3', '1009');
insert into sys_role_menu values ('3', '1010');
insert into sys_role_menu values ('3', '1011');
insert into sys_role_menu values ('3', '1012');
insert into sys_role_menu values ('3', '1013');
insert into sys_role_menu values ('3', '1014');
insert into sys_role_menu values ('3', '1015');
insert into sys_role_menu values ('3', '1016');
insert into sys_role_menu values ('3', '1017');
insert into sys_role_menu values ('3', '1018');
insert into sys_role_menu values ('3', '1019');
insert into sys_role_menu values ('3', '1020');
insert into sys_role_menu values ('3', '1021');
insert into sys_role_menu values ('3', '1022');
insert into sys_role_menu values ('3', '1023');
insert into sys_role_menu values ('3', '1024');
insert into sys_role_menu values ('3', '1025');
insert into sys_role_menu values ('3', '1026');
insert into sys_role_menu values ('3', '1027');
insert into sys_role_menu values ('3', '1028');
insert into sys_role_menu values ('3', '1029');
insert into sys_role_menu values ('3', '1030');
insert into sys_role_menu values ('3', '1031');
insert into sys_role_menu values ('3', '1032');
insert into sys_role_menu values ('3', '1033');
insert into sys_role_menu values ('3', '1034');
insert into sys_role_menu values ('3', '1035');
insert into sys_role_menu values ('3', '1036');
insert into sys_role_menu values ('3', '1037');
insert into sys_role_menu values ('3', '1038');
insert into sys_role_menu values ('3', '1039');
insert into sys_role_menu values ('3', '1040');
insert into sys_role_menu values ('3', '1041');
insert into sys_role_menu values ('3', '1042');
insert into sys_role_menu values ('3', '1043');
insert into sys_role_menu values ('3', '1044');
insert into sys_role_menu values ('3', '1045');
insert into sys_role_menu values ('3', '1050');
insert into sys_role_menu values ('3', '1061');
insert into sys_role_menu values ('3', '1062');
insert into sys_role_menu values ('3', '1063');
insert into sys_role_menu values ('3', '1064');
insert into sys_role_menu values ('3', '1065');
insert into sys_role_menu values ('3', '1500');
insert into sys_role_menu values ('3', '1501');
insert into sys_role_menu values ('3', '1502');
insert into sys_role_menu values ('3', '1503');
insert into sys_role_menu values ('3', '1504');
insert into sys_role_menu values ('3', '1505');
insert into sys_role_menu values ('3', '1506');
insert into sys_role_menu values ('3', '1507');
insert into sys_role_menu values ('3', '1508');
insert into sys_role_menu values ('3', '1509');
insert into sys_role_menu values ('3', '1510');
insert into sys_role_menu values ('3', '1511');
insert into sys_role_menu values ('3', '1600');
insert into sys_role_menu values ('3', '1601');
insert into sys_role_menu values ('3', '1602');
insert into sys_role_menu values ('3', '1603');
insert into sys_role_menu values ('3', '1620');
insert into sys_role_menu values ('3', '1621');
insert into sys_role_menu values ('3', '1622');
insert into sys_role_menu values ('3', '1623');
insert into sys_role_menu values ('3', '11616');
insert into sys_role_menu values ('3', '11618');
insert into sys_role_menu values ('3', '11619');
insert into sys_role_menu values ('3', '11622');
insert into sys_role_menu values ('3', '11623');
insert into sys_role_menu values ('3', '11629');
insert into sys_role_menu values ('3', '11632');
insert into sys_role_menu values ('3', '11633');
insert into sys_role_menu values ('3', '11638');
insert into sys_role_menu values ('3', '11639');
insert into sys_role_menu values ('3', '11640');
insert into sys_role_menu values ('3', '11641');
insert into sys_role_menu values ('3', '11642');
insert into sys_role_menu values ('3', '11643');
insert into sys_role_menu values ('3', '11701');
insert into sys_role_menu values ('4', '5');
insert into sys_role_menu values ('4', '1500');
insert into sys_role_menu values ('4', '1501');
insert into sys_role_menu values ('4', '1502');
insert into sys_role_menu values ('4', '1503');
insert into sys_role_menu values ('4', '1504');
insert into sys_role_menu values ('4', '1505');
insert into sys_role_menu values ('4', '1506');
insert into sys_role_menu values ('4', '1507');
insert into sys_role_menu values ('4', '1508');
insert into sys_role_menu values ('4', '1509');
insert into sys_role_menu values ('4', '1510');
insert into sys_role_menu values ('4', '1511');

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
create table sys_role_dept (
    role_id   bigint(20) not null comment '角色ID',
    dept_id   bigint(20) not null comment '部门ID',
    primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
create table sys_user_post
(
    user_id   bigint(20) not null comment '用户ID',
    post_id   bigint(20) not null comment '岗位ID',
    primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');

-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
create table sys_oper_log (
    oper_id           bigint(20)      not null                   comment '日志主键',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    title             varchar(50)     default ''                 comment '模块标题',
    business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
    method            varchar(100)    default ''                 comment '方法名称',
    request_method    varchar(10)     default ''                 comment '请求方式',
    operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name         varchar(50)     default ''                 comment '操作人员',
    dept_name         varchar(50)     default ''                 comment '部门名称',
    oper_url          varchar(255)    default ''                 comment '请求URL',
    oper_ip           varchar(128)    default ''                 comment '主机地址',
    oper_location     varchar(255)    default ''                 comment '操作地点',
    oper_param        varchar(4000)   default ''                 comment '请求参数',
    json_result       varchar(4000)   default ''                 comment '返回参数',
    status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
    error_msg         varchar(4000)   default ''                 comment '错误消息',
    oper_time         datetime                                   comment '操作时间',
    cost_time         bigint(20)      default 0                  comment '消耗时间',
    primary key (oper_id),
    key idx_sys_oper_log_bt (business_type),
    key idx_sys_oper_log_s  (status),
    key idx_sys_oper_log_ot (oper_time)
) engine=innodb comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表
-- ----------------------------
create table sys_dict_type
(
    dict_id          bigint(20)      not null                   comment '字典主键',
    tenant_id        varchar(20)     default '000000'           comment '租户编号',
    dict_name        varchar(100)    default ''                 comment '字典名称',
    dict_type        varchar(100)    default ''                 comment '字典类型',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      datetime                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      datetime                                   comment '更新时间',
    remark           varchar(500)    default null               comment '备注',
    primary key (dict_id),
    unique (tenant_id, dict_type)
) engine=innodb comment = '字典类型表';

insert into sys_dict_type values(1, '000000', '用户性别', 'sys_user_sex',        103, 1, sysdate(), null, null, '用户性别列表');
insert into sys_dict_type values(2, '000000', '菜单状态', 'sys_show_hide',       103, 1, sysdate(), null, null, '菜单状态列表');
insert into sys_dict_type values(3, '000000', '系统开关', 'sys_normal_disable',  103, 1, sysdate(), null, null, '系统开关列表');
insert into sys_dict_type values(6, '000000', '系统是否', 'sys_yes_no',          103, 1, sysdate(), null, null, '系统是否列表');
insert into sys_dict_type values(7, '000000', '通知类型', 'sys_notice_type',     103, 1, sysdate(), null, null, '通知类型列表');
insert into sys_dict_type values(8, '000000', '通知状态', 'sys_notice_status',   103, 1, sysdate(), null, null, '通知状态列表');
insert into sys_dict_type values(9, '000000', '操作类型', 'sys_oper_type',       103, 1, sysdate(), null, null, '操作类型列表');
insert into sys_dict_type values(10, '000000', '系统状态', 'sys_common_status',  103, 1, sysdate(), null, null, '登录状态列表');
insert into sys_dict_type values(11, '000000', '授权类型', 'sys_grant_type',     103, 1, sysdate(), null, null, '认证授权类型');
insert into sys_dict_type values(12, '000000', '设备类型', 'sys_device_type',    103, 1, sysdate(), null, null, '客户端设备类型');


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
create table sys_dict_data
(
    dict_code        bigint(20)      not null                   comment '字典编码',
    tenant_id        varchar(20)     default '000000'           comment '租户编号',
    dict_sort        int(4)          default 0                  comment '字典排序',
    dict_label       varchar(100)    default ''                 comment '字典标签',
    dict_value       varchar(100)    default ''                 comment '字典键值',
    dict_type        varchar(100)    default ''                 comment '字典类型',
    css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
    list_class       varchar(100)    default null               comment '表格回显样式',
    is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      datetime                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      datetime                                   comment '更新时间',
    remark           varchar(500)    default null               comment '备注',
    primary key (dict_code)
) engine=innodb comment = '字典数据表';

insert into sys_dict_data values(1, '000000', 1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', 103, 1, sysdate(), null, null, '性别男');
insert into sys_dict_data values(2, '000000', 2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', 103, 1, sysdate(), null, null, '性别女');
insert into sys_dict_data values(3, '000000', 3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', 103, 1, sysdate(), null, null, '性别未知');
insert into sys_dict_data values(4, '000000', 1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', 103, 1, sysdate(), null, null, '显示菜单');
insert into sys_dict_data values(5, '000000', 2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '隐藏菜单');
insert into sys_dict_data values(6, '000000', 1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(7, '000000', 2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', 103, 1, sysdate(), null, null, '停用状态');
insert into sys_dict_data values(12, '000000', 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', 103, 1, sysdate(), null, null, '系统默认是');
insert into sys_dict_data values(13, '000000', 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', 103, 1, sysdate(), null, null, '系统默认否');
insert into sys_dict_data values(14, '000000', 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', 103, 1, sysdate(), null, null, '通知');
insert into sys_dict_data values(15, '000000', 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', 103, 1, sysdate(), null, null, '公告');
insert into sys_dict_data values(16, '000000', 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(17, '000000', 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', 103, 1, sysdate(), null, null, '关闭状态');
insert into sys_dict_data values(29, '000000', 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', 103, 1, sysdate(), null, null, '其他操作');
insert into sys_dict_data values(18, '000000', 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', 103, 1, sysdate(), null, null, '新增操作');
insert into sys_dict_data values(19, '000000', 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', 103, 1, sysdate(), null, null, '修改操作');
insert into sys_dict_data values(20, '000000', 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '删除操作');
insert into sys_dict_data values(21, '000000', 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', 103, 1, sysdate(), null, null, '授权操作');
insert into sys_dict_data values(22, '000000', 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', 103, 1, sysdate(), null, null, '导出操作');
insert into sys_dict_data values(23, '000000', 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', 103, 1, sysdate(), null, null, '导入操作');
insert into sys_dict_data values(24, '000000', 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '强退操作');
insert into sys_dict_data values(25, '000000', 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', 103, 1, sysdate(), null, null, '生成操作');
insert into sys_dict_data values(26, '000000', 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '清空操作');
insert into sys_dict_data values(27, '000000', 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(28, '000000', 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', 103, 1, sysdate(), null, null, '停用状态');
insert into sys_dict_data values(30, '000000', 0,  '密码认证', 'password',   'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '密码认证');
insert into sys_dict_data values(31, '000000', 0,  '短信认证', 'sms',        'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '短信认证');
insert into sys_dict_data values(32, '000000', 0,  '邮件认证', 'email',      'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '邮件认证');
insert into sys_dict_data values(33, '000000', 0,  '小程序认证', 'xcx',      'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '小程序认证');
insert into sys_dict_data values(34, '000000', 0,  '三方登录认证', 'social', 'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '三方登录认证');
insert into sys_dict_data values(35, '000000', 0,  'PC',    'pc',         'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, 'PC');
insert into sys_dict_data values(36, '000000', 0,  '安卓', 'android',     'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, '安卓');
insert into sys_dict_data values(37, '000000', 0,  'iOS', 'ios',          'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, 'iOS');
insert into sys_dict_data values(38, '000000', 0,  '小程序', 'xcx',       'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, '小程序');


-- ----------------------------
-- 13、参数配置表
-- ----------------------------
create table sys_config (
    config_id         bigint(20)      not null                   comment '参数主键',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    config_name       varchar(100)    default ''                 comment '参数名称',
    config_key        varchar(100)    default ''                 comment '参数键名',
    config_value      varchar(500)    default ''                 comment '参数键值',
    config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (config_id)
) engine=innodb comment = '参数配置表';

insert into sys_config values(1, '000000', '主框架页-默认皮肤样式名称',     'sys.index.skinName',            'skin-blue',     'Y', 103, 1, sysdate(), null, null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow' );
insert into sys_config values(2, '000000', '用户管理-账号初始密码',        'sys.user.initPassword',         '123456',        'Y', 103, 1, sysdate(), null, null, '初始化密码 123456' );
insert into sys_config values(3, '000000', '主框架页-侧边栏主题',          'sys.index.sideTheme',           'theme-dark',    'Y', 103, 1, sysdate(), null, null, '深色主题theme-dark，浅色主题theme-light' );
insert into sys_config values(5, '000000', '账号自助-是否开启用户注册功能',  'sys.account.registerUser',      'false',         'Y', 103, 1, sysdate(), null, null, '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(11, '000000', 'OSS预览列表资源开关',         'sys.oss.previewListResource',   'true',          'Y', 103, 1, sysdate(), null, null, 'true:开启, false:关闭');


-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
create table sys_logininfor (
    info_id        bigint(20)     not null                  comment '访问ID',
    tenant_id      varchar(20)    default '000000'          comment '租户编号',
    user_name      varchar(50)    default ''                comment '用户账号',
    client_key     varchar(32)    default ''                comment '客户端',
    device_type    varchar(32)    default ''                comment '设备类型',
    ipaddr         varchar(128)   default ''                comment '登录IP地址',
    login_location varchar(255)   default ''                comment '登录地点',
    browser        varchar(50)    default ''                comment '浏览器类型',
    os             varchar(50)    default ''                comment '操作系统',
    status         char(1)        default '0'               comment '登录状态（0成功 1失败）',
    msg            varchar(255)   default ''                comment '提示消息',
    login_time     datetime                                 comment '访问时间',
    primary key (info_id),
    key idx_sys_logininfor_s  (status),
    key idx_sys_logininfor_lt (login_time)
) engine=innodb comment = '系统访问记录';


-- ----------------------------
-- 17、通知公告表
-- ----------------------------
create table sys_notice (
    notice_id         bigint(20)      not null                   comment '公告ID',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    notice_title      varchar(50)     not null                   comment '公告标题',
    notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
    notice_content    longblob        default null               comment '公告内容',
    status            char(1)         default '0'                comment '公告状态（0正常 1关闭）',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(255)    default null               comment '备注',
    primary key (notice_id)
) engine=innodb comment = '通知公告表';

-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '000000', '温馨提醒：2018-07-01 新版本发布啦', '2', '新版本内容', '0', 103, 1, sysdate(), null, null, '管理员');
insert into sys_notice values('2', '000000', '维护通知：2018-07-01 系统凌晨维护', '1', '维护内容',   '0', 103, 1, sysdate(), null, null, '管理员');


-- ----------------------------
-- 18、代码生成业务表
-- ----------------------------
create table gen_table (
    table_id          bigint(20)      not null                   comment '编号',
    data_name         varchar(200)    default ''                 comment '数据源名称',
    table_name        varchar(200)    default ''                 comment '表名称',
    table_comment     varchar(500)    default ''                 comment '表描述',
    sub_table_name    varchar(64)     default null               comment '关联子表的表名',
    sub_table_fk_name varchar(64)     default null               comment '子表关联的外键名',
    class_name        varchar(100)    default ''                 comment '实体类名称',
    tpl_category      varchar(200)    default 'crud'             comment '使用的模板（crud单表操作 tree树表操作）',
    package_name      varchar(100)                               comment '生成包路径',
    module_name       varchar(30)                                comment '生成模块名',
    business_name     varchar(30)                                comment '生成业务名',
    function_name     varchar(50)                                comment '生成功能名',
    function_author   varchar(50)                                comment '生成功能作者',
    gen_type          char(1)         default '0'                comment '生成代码方式（0zip压缩包 1自定义路径）',
    gen_path          varchar(200)    default '/'                comment '生成路径（不填默认项目路径）',
    options           varchar(1000)                              comment '其它生成选项',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (table_id)
) engine=innodb comment = '代码生成业务表';


-- ----------------------------
-- 19、代码生成业务表字段
-- ----------------------------
create table gen_table_column (
    column_id         bigint(20)      not null                   comment '编号',
    table_id          bigint(20)                                 comment '归属表编号',
    column_name       varchar(200)                               comment '列名称',
    column_comment    varchar(500)                               comment '列描述',
    column_type       varchar(100)                               comment '列类型',
    java_type         varchar(500)                               comment 'JAVA类型',
    java_field        varchar(200)                               comment 'JAVA字段名',
    is_pk             char(1)                                    comment '是否主键（1是）',
    is_increment      char(1)                                    comment '是否自增（1是）',
    is_required       char(1)                                    comment '是否必填（1是）',
    is_insert         char(1)                                    comment '是否为插入字段（1是）',
    is_edit           char(1)                                    comment '是否编辑字段（1是）',
    is_list           char(1)                                    comment '是否列表字段（1是）',
    is_query          char(1)                                    comment '是否查询字段（1是）',
    query_type        varchar(200)    default 'EQ'               comment '查询方式（等于、不等于、大于、小于、范围）',
    html_type         varchar(200)                               comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    dict_type         varchar(200)    default ''                 comment '字典类型',
    sort              int                                        comment '排序',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    primary key (column_id)
) engine=innodb comment = '代码生成业务表字段';

-- ----------------------------
-- OSS对象存储表
-- ----------------------------
create table sys_oss (
    oss_id          bigint(20)   not null                   comment '对象存储主键',
    tenant_id       varchar(20)           default '000000'  comment '租户编号',
    file_name       varchar(255) not null default ''        comment '文件名',
    original_name   varchar(255) not null default ''        comment '原名',
    file_suffix     varchar(10)  not null default ''        comment '文件后缀名',
    url             varchar(500) not null                   comment 'URL地址',
    ext1            text                  default null      comment '扩展字段',
    create_dept     bigint(20)            default null      comment '创建部门',
    create_time     datetime              default null      comment '创建时间',
    create_by       bigint(20)            default null      comment '上传人',
    update_time     datetime              default null      comment '更新时间',
    update_by       bigint(20)            default null      comment '更新人',
    service         varchar(20)  not null default 'minio'   comment '服务商',
    primary key (oss_id)
) engine=innodb comment ='OSS对象存储表';

-- ----------------------------
-- OSS对象存储动态配置表
-- ----------------------------
create table sys_oss_config (
    oss_config_id   bigint(20)    not null                  comment '主键',
    tenant_id       varchar(20)             default '000000'comment '租户编号',
    config_key      varchar(20)   not null  default ''      comment '配置key',
    access_key      varchar(255)            default ''      comment 'accessKey',
    secret_key      varchar(255)            default ''      comment '秘钥',
    bucket_name     varchar(255)            default ''      comment '桶名称',
    prefix          varchar(255)            default ''      comment '前缀',
    endpoint        varchar(255)            default ''      comment '访问站点',
    domain          varchar(255)            default ''      comment '自定义域名',
    is_https        char(1)                 default 'N'     comment '是否https（Y=是,N=否）',
    region          varchar(255)            default ''      comment '域',
    access_policy   char(1)       not null  default '1'     comment '桶权限类型(0=private 1=public 2=custom)',
    status          char(1)                 default '1'     comment '是否默认（0=是,1=否）',
    ext1            varchar(255)            default ''      comment '扩展字段',
    create_dept     bigint(20)              default null    comment '创建部门',
    create_by       bigint(20)              default null    comment '创建者',
    create_time     datetime                default null    comment '创建时间',
    update_by       bigint(20)              default null    comment '更新者',
    update_time     datetime                default null    comment '更新时间',
    remark          varchar(500)            default null    comment '备注',
    primary key (oss_config_id)
) engine=innodb comment='对象存储配置表';

insert into sys_oss_config values (1, '000000', 'minio',  'ruoyi',            'ruoyi123',        'ruoyi',             '', '127.0.0.1:9000',                '','N', '',             '1' ,'0', '', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (2, '000000', 'qiniu',  'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'ruoyi',             '', 's3-cn-north-1.qiniucs.com',     '','N', '',             '1' ,'1', '', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (3, '000000', 'aliyun', 'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'ruoyi',             '', 'oss-cn-beijing.aliyuncs.com',   '','N', '',             '1' ,'1', '', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (4, '000000', 'qcloud', 'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'ruoyi-1240000000',  '', 'cos.ap-beijing.myqcloud.com',   '','N', 'ap-beijing',   '1' ,'1', '', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (5, '000000', 'image',  'ruoyi',            'ruoyi123',        'ruoyi',             'image', '127.0.0.1:9000',           '','N', '',             '1' ,'1', '', 103, 1, sysdate(), 1, sysdate(), null);

-- ----------------------------
-- 系统授权表
-- ----------------------------
create table sys_client (
    id                  bigint(20)    not null            comment 'id',
    client_id           varchar(64)   default null        comment '客户端id',
    client_key          varchar(32)   default null        comment '客户端key',
    client_secret       varchar(255)  default null        comment '客户端秘钥',
    grant_type          varchar(255)  default null        comment '授权类型',
    device_type         varchar(32)   default null        comment '设备类型',
    active_timeout      int(11)       default 1800        comment 'token活跃超时时间',
    timeout             int(11)       default 604800      comment 'token固定超时',
    status              char(1)       default '0'         comment '状态（0正常 1停用）',
    del_flag            char(1)       default '0'         comment '删除标志（0代表存在 1代表删除）',
    create_dept         bigint(20)    default null        comment '创建部门',
    create_by           bigint(20)    default null        comment '创建者',
    create_time         datetime      default null        comment '创建时间',
    update_by           bigint(20)    default null        comment '更新者',
    update_time         datetime      default null        comment '更新时间',
    primary key (id)
) engine=innodb comment='系统授权表';

insert into sys_client values (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, 0, 0, 103, 1, sysdate(), 1, sysdate());
insert into sys_client values (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms,social', 'android', 1800, 604800, 0, 0, 103, 1, sysdate(), 1, sysdate());


-- ========================================
-- Custom Phase 3: Meter Module Tables
-- ========================================

-- Phase 3: 仪表模块表迁移
-- 迁移自旧系统仪表模块

-- ========================================
-- 1. 仪表厂商表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_vendor` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(32) DEFAULT NULL COMMENT '厂商编码',
    `name` varchar(32) DEFAULT NULL COMMENT '厂商名称',
    `contacts` varchar(32) DEFAULT NULL COMMENT '厂商联系人',
    `tele` varchar(32) DEFAULT NULL COMMENT '联系人电话',
    `address` varchar(125) DEFAULT NULL COMMENT '厂商地址',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表厂商表';

-- ========================================
-- 2. 仪表分类表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_sort` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(32) DEFAULT NULL COMMENT '编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `model` varchar(32) DEFAULT NULL COMMENT '型号',
    `vendor_id` varchar(32) DEFAULT NULL COMMENT '厂商',
    `is_onecard` tinyint DEFAULT '0' COMMENT '是否一卡通',
    `measure_type` varchar(2) DEFAULT NULL COMMENT '计费模式 0按量 1按金额 2按时间',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_type` varchar(10) NOT NULL COMMENT '仪表类型',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表分类表';

-- ========================================
-- 3. 电表仪表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_electric_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '电表仪表分类ID',
    `msg_type` tinyint(1) DEFAULT NULL COMMENT '通讯方式 1卡式 2远传 3手工抄表',
    `code` varchar(32) DEFAULT NULL COMMENT '电表类型编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '表型号',
    `rated_voltage` varchar(32) DEFAULT NULL COMMENT '额定电压(V)',
    `rated_current` varchar(32) DEFAULT NULL COMMENT '额定电流(A)',
    `voltage_ratio` varchar(32) DEFAULT NULL COMMENT '电压变比',
    `current_ratio` varchar(32) DEFAULT NULL COMMENT '电流变比',
    `load_limit` varchar(32) DEFAULT NULL COMMENT '负荷限制(kw.h)',
    `alarm_value` varchar(32) DEFAULT NULL COMMENT '报警值(kw.h)',
    `display_value` varchar(32) DEFAULT NULL COMMENT '长显报警值(kw.h)',
    `constant` varchar(32) DEFAULT NULL COMMENT '常数(imp/kw.h)',
    `is_enabled` int DEFAULT NULL COMMENT '是否启用',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_num_required` tinyint(1) DEFAULT NULL COMMENT '表号是否必填',
    `max_amount` decimal(18,2) DEFAULT NULL COMMENT '最大购量',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电表仪表档案';

-- ========================================
-- 4. 水表仪表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_water_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '水表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '水表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `msg_type` tinyint(1) DEFAULT NULL COMMENT '通讯方式 1卡式 2远传 3手工抄表',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格(A)',
    `model` varchar(32) DEFAULT NULL COMMENT '表型号',
    `constant` varchar(10) DEFAULT NULL COMMENT '常数(脉冲)',
    `close_val` varchar(32) DEFAULT NULL COMMENT '关阀值',
    `alarm_val` varchar(32) DEFAULT NULL COMMENT '报警值',
    `load_limit` varchar(32) DEFAULT NULL COMMENT '囤积量',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_num_required` tinyint(1) DEFAULT NULL COMMENT '表号是否必填',
    `is_enabled` int DEFAULT '0' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='水表仪表档案';

-- ========================================
-- 5. 热力表仪表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_heat_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='热力表仪表档案';

-- ========================================
-- 6. 燃气表档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_gas_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) DEFAULT NULL COMMENT '燃气表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '燃气表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '燃气表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '燃气表型号',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT NULL COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='燃气表档案表';

-- ========================================
-- 7. 集中器档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_centrator_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    KEY `idx_centrator_name` (`name`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='集中器档案';

-- ========================================
-- 8. 温控器档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_tc_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='温控器档案';

-- ========================================
-- 9. 阀门表档案
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_tc_valve` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '热力表仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    KEY `idx_tc_valve_type` (`id`, `type`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='阀门表档案';

-- ========================================
-- 10. 仪表分配表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_match` (
    `id` varchar(32) NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `archive_id` varchar(32) NOT NULL COMMENT '仪表档案ID',
    `company_id` varchar(32) NOT NULL COMMENT '分配公司ID',
    `meter_type` varchar(10) NOT NULL COMMENT '仪表类型',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(32) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(32) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表分配表';

-- ========================================
-- 11. 公式档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_formula_file` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(64) DEFAULT NULL COMMENT '公式名称',
    `type` varchar(32) DEFAULT NULL COMMENT '公式类型',
    `cformula` varchar(500) DEFAULT NULL COMMENT '中文公式',
    `eformula` varchar(500) DEFAULT NULL COMMENT '英文公式',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` varchar(1) DEFAULT '1' COMMENT '是否启用 0=禁用 1=启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公式档案表';

-- ========================================
-- Custom Phase 4: Thermal Regulation Tables
-- ========================================

-- Phase 4: 热力调控模块表迁移
-- 迁移自旧系统调控模块

-- ========================================
-- 1. 控制指令表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_instruction` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `name` varchar(64) NOT NULL COMMENT '指令名称',
    `type` tinyint NOT NULL COMMENT '指令类型',
    `instruction` varchar(256) DEFAULT NULL COMMENT '指令内容',
    `remark` varchar(60) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制指令表';

-- ========================================
-- 2. 控制策略主表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_strategy` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `name` varchar(64) NOT NULL COMMENT '策略名称',
    `type` tinyint DEFAULT NULL,
    `company_id` varchar(32) DEFAULT NULL,
    `remark` varchar(255) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制策略主表';

-- ========================================
-- 3. 控制策略子表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_strategy_sub` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `strategy_id` varchar(32) DEFAULT NULL,
    `instruction_id` varchar(32) DEFAULT NULL,
    `sort` int DEFAULT NULL,
    `valve_angle` varchar(32) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制策略子表';

-- ========================================
-- 4. 报警表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_alert` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `building_id` varchar(32) DEFAULT NULL,
    `unit_id` varchar(32) DEFAULT NULL,
    `house_id` varchar(32) DEFAULT NULL,
    `meter_id` varchar(32) NOT NULL,
    `is_charged` tinyint DEFAULT NULL,
    `valve` tinyint DEFAULT NULL,
    `in_temp` decimal(6,2) DEFAULT NULL,
    `out_temp` decimal(6,2) DEFAULT NULL,
    `room_temp` decimal(6,2) DEFAULT NULL,
    `alert_type` tinyint NOT NULL,
    `alert_time` datetime NOT NULL,
    `alert_status` varchar(500) DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `company_id` varchar(32) NOT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `in_maintenance` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `meter_id` (`meter_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报警表';

-- ========================================
-- 5. 报修表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_repair` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `building_id` varchar(32) DEFAULT NULL,
    `building_name` varchar(255) DEFAULT NULL,
    `unit_code` varchar(255) DEFAULT NULL,
    `house_id` varchar(32) DEFAULT NULL,
    `room_num` varchar(255) DEFAULT NULL,
    `meter_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(20) DEFAULT NULL,
    `is_charged` tinyint DEFAULT '0',
    `valve_status` varchar(10) DEFAULT NULL,
    `valve` tinyint DEFAULT NULL,
    `in_temp` decimal(6,2) DEFAULT NULL,
    `out_temp` decimal(6,2) DEFAULT NULL,
    `room_temp` decimal(6,2) DEFAULT NULL,
    `repair_type` tinyint NOT NULL,
    `repair_time` datetime NOT NULL,
    `repair_info` varchar(500) DEFAULT NULL,
    `repair_status` tinyint DEFAULT '0',
    `repair_result` varchar(500) DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `org_name` varchar(255) DEFAULT NULL,
    `company_id` varchar(32) NOT NULL,
    `is_delete` tinyint DEFAULT '0',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_name` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `in_maintenance` varchar(255) DEFAULT NULL,
    `dispatch_id` varchar(50) DEFAULT NULL,
    `dispatch_name` varchar(50) DEFAULT NULL,
    `dispatch_time` datetime DEFAULT NULL,
    `repair_no` varchar(25) DEFAULT NULL,
    `fix_id` varchar(50) DEFAULT NULL,
    `fix_name` varchar(50) DEFAULT NULL,
    `fix_time` datetime DEFAULT NULL,
    `user_name` varchar(255) DEFAULT NULL,
    `user_phone` varchar(20) DEFAULT NULL,
    `appoint_time` datetime DEFAULT NULL,
    `urgent_type` tinyint DEFAULT NULL,
    `service_type` tinyint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `meter_id` (`meter_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报修表';

-- ========================================
-- 6. 控制任务表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_tasks` (
    `id` int NOT NULL AUTO_INCREMENT,
    `tenant_id` varchar(20) DEFAULT '000000',
    `cu_group_id` varchar(32) NOT NULL DEFAULT '',
    `name` varchar(64) NOT NULL COMMENT '任务名称',
    `type` tinyint NOT NULL COMMENT '执行方式',
    `cron_expression` varchar(255) NOT NULL COMMENT '时间表达式',
    `strategy_id` varchar(32) DEFAULT NULL,
    `priority` tinyint DEFAULT NULL,
    `status` tinyint NOT NULL COMMENT '0停止 1启动',
    `number` tinyint NOT NULL DEFAULT '0',
    `last_time` datetime DEFAULT NULL,
    `total` tinyint DEFAULT NULL,
    `org_id` varchar(32) NOT NULL,
    `company_id` varchar(32) NOT NULL,
    `adjust_basis` tinyint NOT NULL,
    `scope_type` tinyint NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `bean_class` varchar(255) NOT NULL,
    `job_group` varchar(32) NOT NULL,
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
    `create_by` varchar(32) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(32) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制任务表';

-- ========================================
-- 7. 调控执行记录表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_tasks_perform` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `instruction_id` varchar(32) DEFAULT NULL,
    `group_id` varchar(32) DEFAULT NULL,
    `strategy_id` varchar(32) DEFAULT NULL,
    `command_index` int DEFAULT NULL,
    `orderr` int DEFAULT NULL,
    `instruction_type` int DEFAULT NULL,
    `instruction` int DEFAULT NULL,
    `number` int DEFAULT NULL,
    `intervall` int DEFAULT NULL,
    `fore_start` int DEFAULT NULL,
    `unit` int DEFAULT NULL,
    `duration` int DEFAULT NULL,
    `org_id` varchar(32) DEFAULT NULL,
    `company_id` varchar(32) DEFAULT NULL,
    `concentrator_code` varchar(32) DEFAULT NULL,
    `device_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(32) DEFAULT NULL,
    `meter_id` varchar(32) DEFAULT NULL,
    `meter_arc_code` varchar(32) DEFAULT NULL,
    `status` int DEFAULT NULL,
    `instruction_status` int DEFAULT NULL,
    `send_time` datetime DEFAULT NULL,
    `tasks_id` varchar(32) DEFAULT NULL,
    `in_temp` decimal(6,2) DEFAULT NULL,
    `out_temp` decimal(6,2) DEFAULT NULL,
    `room_temp` decimal(6,2) DEFAULT NULL,
    `valve_open` int DEFAULT NULL,
    `imei` varchar(30) DEFAULT NULL,
    `dtu_num` varchar(20) DEFAULT NULL,
    `chan_num` varchar(20) DEFAULT NULL,
    `out_temp_pj` decimal(6,2) DEFAULT NULL,
    `ref_heat` decimal(10,2) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_tasks_id` (`tasks_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调控执行记录表';

-- ========================================
-- 8. 控制范围表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_scope` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `tasks_id` varchar(32) NOT NULL,
    `org_id` varchar(32) NOT NULL,
    `building_id` varchar(32) DEFAULT NULL,
    `unit_id` varchar(32) DEFAULT NULL,
    `company_id` varchar(32) NOT NULL,
    `house_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(20) NOT NULL,
    `meter_id` varchar(32) NOT NULL,
    `meter_arc_code` varchar(10) NOT NULL,
    `concentrator_code` varchar(10) DEFAULT NULL,
    `imei` varchar(30) DEFAULT NULL,
    `device_id` varchar(32) DEFAULT NULL,
    `status` tinyint DEFAULT NULL,
    `is_special` tinyint(1) DEFAULT '0',
    `dtu_num` varchar(20) DEFAULT NULL,
    `chan_num` varchar(20) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制范围表';

-- ========================================
-- 9. 单元房屋策略表
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_house_strategy` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `tasks_id` varchar(32) NOT NULL,
    `type` tinyint NOT NULL,
    `strategy_id` varchar(32) NOT NULL,
    `name` varchar(64) DEFAULT NULL,
    `remark` varchar(125) DEFAULT NULL,
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
    `org_id` varchar(32) NOT NULL,
    `company_id` varchar(32) NOT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `preset_angle` tinyint DEFAULT NULL,
    `preset_flow_rate` decimal(10,2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='单元房屋策略表';

-- ========================================
-- 10. 任务执行设定历史表(主表)
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_task_setting_log` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `task_id` varchar(5) NOT NULL,
    `scope_type` varchar(2) NOT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) NOT NULL,
    `create_time` datetime NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务执行设定历史表';

-- ========================================
-- 11. 任务执行设定历史表(子表)
-- ========================================
CREATE TABLE IF NOT EXISTS `ht_task_setting_log_item` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `main_id` varchar(32) NOT NULL,
    `scope_id` varchar(32) DEFAULT NULL,
    `meter_num` varchar(32) DEFAULT NULL,
    `old_angle` int DEFAULT NULL,
    `new_angle` int DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务执行设定历史子表';

-- ========================================
-- Custom Phase 5: Property/Charging Tables
-- ========================================

-- Phase 5: 物业收费模块表迁移 + 菜单系统迁移
-- 从旧库 rltk_pro 迁移到新库

-- ========================================
-- 1. 交易记录主表
-- ========================================
CREATE TABLE IF NOT EXISTS pr_transaction_record (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `serial_num` varchar(32) DEFAULT NULL COMMENT '流水号',
    `transaction_type` tinyint DEFAULT NULL COMMENT '交易类型: 1=收费 2=退费 3=转存 4=优惠',
    `payment_type` tinyint DEFAULT NULL COMMENT '缴费方式: 1=现金 2=微信 3=支付宝 4=刷卡',
    `amount` decimal(18,4) DEFAULT 0.0000 COMMENT '交易金额',
    `paid_amount` decimal(18,4) DEFAULT 0.0000 COMMENT '实收金额',
    `status` tinyint DEFAULT 0 COMMENT '交易状态: 0=正常 1=撤销 2=作废',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `transaction_time` datetime DEFAULT NULL COMMENT '交易时间',
    `operator_id` varchar(32) DEFAULT NULL COMMENT '操作人',
    `notes` varchar(255) DEFAULT NULL COMMENT '备注',
    `original_record_id` varchar(32) DEFAULT NULL COMMENT '原交易记录ID',
    `invoice_no` varchar(32) DEFAULT NULL COMMENT '发票号',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(40) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(40) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_serial_num` (`serial_num`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_transaction_time` (`transaction_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易记录主表';

-- ========================================
-- 2. 交易记录子表明细
-- ========================================
CREATE TABLE IF NOT EXISTS pr_transaction_record_sub (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `main_id` varchar(32) DEFAULT NULL COMMENT '主表ID',
    `expense_id` varchar(32) DEFAULT NULL COMMENT '费用明细ID',
    `amount` decimal(18,4) DEFAULT 0.0000 COMMENT '交易金额',
    `balance_before` decimal(18,4) DEFAULT 0.0000 COMMENT '交易前余额',
    `balance_after` decimal(18,4) DEFAULT 0.0000 COMMENT '交易后余额',
    `item_name` varchar(64) DEFAULT NULL COMMENT '费项名称',
    `notes` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_main_id` (`main_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易记录子表明细';

-- ========================================
-- 3. 个人账户余额
-- ========================================
CREATE TABLE IF NOT EXISTS pr_account_balance (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `balance` decimal(18,4) DEFAULT 0.0000 COMMENT '账户余额',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_house` (`user_id`, `house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='个人账户余额';

-- ========================================
-- 4. 票据备注
-- ========================================
CREATE TABLE IF NOT EXISTS pr_billing_notes (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `serial_num` varchar(32) NOT NULL COMMENT '流水号',
    `notes` varchar(255) DEFAULT NULL COMMENT '票据备注',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_serial_num` (`serial_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='票据备注';

-- ========================================
-- 5. 写卡日志
-- ========================================
CREATE TABLE IF NOT EXISTS pr_use_card_log (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `meter_id` varchar(32) DEFAULT NULL COMMENT '仪表ID',
    `meter_num` varchar(32) DEFAULT NULL COMMENT '仪表号',
    `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
    `card_num` varchar(32) DEFAULT NULL COMMENT '卡号',
    `valve_status` int DEFAULT NULL COMMENT '阀门状态',
    `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `operator_id` varchar(32) DEFAULT NULL COMMENT '操作人',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_meter_id` (`meter_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='写卡日志';

-- ========================================
-- 6. 供热选项
-- ========================================
CREATE TABLE IF NOT EXISTS pr_options_heat (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `option_key` varchar(64) DEFAULT NULL COMMENT '选项键',
    `option_value` varchar(255) DEFAULT NULL COMMENT '选项值',
    `option_type` varchar(32) DEFAULT NULL COMMENT '选项类型',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='供热选项';

-- ========================================
-- 7. 打印模板
-- ========================================
CREATE TABLE IF NOT EXISTS pr_print_template (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `name` varchar(64) DEFAULT NULL COMMENT '模板名称',
    `template_content` text DEFAULT NULL COMMENT '模板内容',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打印模板';

-- ========================================
-- 8. 物业选项
-- ========================================
CREATE TABLE IF NOT EXISTS pr_options (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `option_key` varchar(64) DEFAULT NULL COMMENT '选项键',
    `option_value` varchar(255) DEFAULT NULL COMMENT '选项值',
    `option_type` varchar(32) DEFAULT NULL COMMENT '选项类型',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物业选项';

-- ========================================
-- 9. 收费标准
-- ========================================
CREATE TABLE IF NOT EXISTS pr_standard (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `name` varchar(64) DEFAULT NULL COMMENT '标准名称',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `cycles` int DEFAULT NULL COMMENT '收费周期',
    `generate_rule` varchar(32) DEFAULT NULL COMMENT '生成规则',
    `step_type` varchar(32) DEFAULT NULL COMMENT '阶梯类型',
    `statistics_type` varchar(5) DEFAULT NULL COMMENT '统计方式',
    `step_maxgrade` int DEFAULT NULL COMMENT '阶梯最大级数',
    `is_step2` tinyint DEFAULT NULL COMMENT '是否启用第二阶梯',
    `step2_type` varchar(32) DEFAULT NULL COMMENT '阶梯二类型',
    `step2_maxgrade` int DEFAULT NULL COMMENT '阶梯二最大级数',
    `is_latefee` tinyint DEFAULT NULL COMMENT '是否启用滞纳金',
    `latefee_startdate` datetime DEFAULT NULL COMMENT '滞纳金开始日期',
    `latefee_type` varchar(32) DEFAULT NULL COMMENT '滞纳金类型',
    `latefee_startdays` int DEFAULT NULL COMMENT '滞纳金开始天数',
    `latefee_formula` varchar(255) DEFAULT NULL COMMENT '滞纳金公式',
    `is_limited` tinyint DEFAULT NULL COMMENT '是否限购',
    `limited_type` varchar(32) DEFAULT NULL COMMENT '限购方式',
    `limited_cond` varchar(32) DEFAULT NULL COMMENT '限购条件',
    `limited_times` int DEFAULT NULL COMMENT '限购次数',
    `limited_money` decimal(18,4) DEFAULT NULL COMMENT '限购金额',
    `limited_single_money` decimal(18,4) DEFAULT NULL COMMENT '单次购买最大金额',
    `standard_price` decimal(18,4) DEFAULT NULL COMMENT '基本单价',
    `money_formula` varchar(255) DEFAULT NULL COMMENT '金额公式',
    `max_money` decimal(18,4) DEFAULT NULL COMMENT '最大金额',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_item_code` (`item_code`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收费标准';

-- ========================================
-- 10. 收费标准价格阶梯
-- ========================================
CREATE TABLE IF NOT EXISTS pr_standard_price (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `standard_id` varchar(32) DEFAULT NULL COMMENT '收费标准ID',
    `level` int DEFAULT NULL COMMENT '阶梯级别',
    `min_qty` decimal(18,4) DEFAULT NULL COMMENT '最小数量',
    `max_qty` decimal(18,4) DEFAULT NULL COMMENT '最大数量',
    `price` decimal(18,4) DEFAULT NULL COMMENT '单价',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_standard_id` (`standard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收费标准价格阶梯';

-- ========================================
-- 11. 费目
-- ========================================
CREATE TABLE IF NOT EXISTS pr_expense_item (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `item_code` varchar(32) DEFAULT NULL COMMENT '项目编号',
    `item_name` varchar(64) DEFAULT NULL COMMENT '项目名称',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `is_show` tinyint DEFAULT NULL COMMENT '在票据是否长显',
    `is_printmonth` tinyint DEFAULT NULL COMMENT '打印是否显示月',
    `price_precision` int DEFAULT NULL COMMENT '单价精度',
    `qty_precision` int DEFAULT NULL COMMENT '数量精度',
    `money_precision` int DEFAULT NULL COMMENT '金额精度',
    `is_integer` tinyint DEFAULT NULL COMMENT '是否取整',
    `precision_type` varchar(32) DEFAULT NULL COMMENT '金额小数计算类型',
    `start_pos` int DEFAULT NULL COMMENT '开始位数',
    `sum_precision` int DEFAULT NULL COMMENT '费项合计精度',
    `change_cycle` int DEFAULT NULL COMMENT '起始周期改变',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_item_code` (`item_code`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费目';

-- ========================================
-- 12. 房屋费用绑定
-- ========================================
CREATE TABLE IF NOT EXISTS pr_house_expense (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `standard_id` varchar(32) DEFAULT NULL COMMENT '收费标准ID',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房屋费用绑定';

-- ========================================
-- 13. 费用明细
-- ========================================
CREATE TABLE IF NOT EXISTS pr_expense (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `house_id` varchar(32) DEFAULT NULL COMMENT '房屋ID',
    `item_group` varchar(32) DEFAULT NULL COMMENT '费项分组',
    `item_code` varchar(32) DEFAULT NULL COMMENT '费项编码',
    `item_name` varchar(64) DEFAULT NULL COMMENT '费项名称',
    `standard_id` varchar(32) DEFAULT NULL COMMENT '收费标准ID',
    `start_date` datetime DEFAULT NULL COMMENT '起收日期',
    `expire_date` datetime DEFAULT NULL COMMENT '截止日期',
    `last_date` datetime DEFAULT NULL COMMENT '最迟缴费日期',
    `last_reading` decimal(18,4) DEFAULT NULL COMMENT '上次读数',
    `this_reading` decimal(18,4) DEFAULT NULL COMMENT '本次读数',
    `qty` int DEFAULT NULL COMMENT '用量/周期数',
    `money` decimal(18,4) DEFAULT 0.0000 COMMENT '金额',
    `standard_price` decimal(18,4) DEFAULT NULL COMMENT '单价',
    `max_price` decimal(18,4) DEFAULT NULL COMMENT '最大单价',
    `price_formula` varchar(255) DEFAULT NULL COMMENT '单价计算公式',
    `trade_times` int DEFAULT NULL COMMENT '购买倍数',
    `max_money` decimal(18,4) DEFAULT NULL COMMENT '最大金额',
    `money_formula` varchar(255) DEFAULT NULL COMMENT '金额计算公式',
    `is_free` tinyint DEFAULT NULL COMMENT '是否免收',
    `reason` varchar(255) DEFAULT NULL COMMENT '原因',
    `preferential` decimal(18,4) DEFAULT 0.0000 COMMENT '优惠金额',
    `deduction` decimal(18,4) DEFAULT 0.0000 COMMENT '抵扣金额',
    `latefee` decimal(18,4) DEFAULT 0.0000 COMMENT '滞纳金',
    `receivable` decimal(18,4) DEFAULT 0.0000 COMMENT '应收金额',
    `paid_in` decimal(18,4) DEFAULT 0.0000 COMMENT '实收金额',
    `final_money` decimal(18,4) DEFAULT 0.0000 COMMENT '费项合并金额',
    `overdue_day` int DEFAULT 0 COMMENT '逾期天数',
    `is_charged` tinyint DEFAULT NULL COMMENT '是否已收费',
    `charged_time` datetime DEFAULT NULL COMMENT '收费时间',
    `record_id` varchar(32) DEFAULT NULL COMMENT '交易记录主表ID',
    `delay_date` datetime DEFAULT NULL COMMENT '延期日期',
    `heat_usage` tinyint DEFAULT NULL COMMENT '暖气使用情况',
    `is_calc` varchar(5) DEFAULT NULL COMMENT '是否计算',
    `is_closed` tinyint DEFAULT NULL COMMENT '是否轧账',
    `year` varchar(10) DEFAULT NULL COMMENT '年份',
    `month` varchar(10) DEFAULT NULL COMMENT '月份',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `parking_space_id` varchar(32) DEFAULT NULL COMMENT '车位ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_company_org` (`company_id`, `org_id`),
    KEY `idx_is_charged` (`is_charged`),
    KEY `idx_standard_id` (`standard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用明细';

-- ========================================
-- 14. 客户
-- ========================================
CREATE TABLE IF NOT EXISTS pr_user (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `id_no` varchar(32) DEFAULT NULL COMMENT '身份证号',
    `address` varchar(255) DEFAULT NULL COMMENT '地址',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户';

-- ========================================
-- 15. 房屋
-- ========================================
CREATE TABLE IF NOT EXISTS pr_house (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `room_num` varchar(32) DEFAULT NULL COMMENT '房号',
    `building_id` varchar(32) DEFAULT NULL COMMENT '楼宇ID',
    `unit_code` varchar(32) DEFAULT NULL COMMENT '单元编码',
    `area` decimal(18,4) DEFAULT NULL COMMENT '面积',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `is_charged` tinyint DEFAULT NULL COMMENT '是否已收费',
    `is_calc` varchar(5) DEFAULT NULL COMMENT '是否计算',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_building_unit` (`building_id`, `unit_code`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房屋';

-- ========================================
-- 16. 车位
-- ========================================
CREATE TABLE IF NOT EXISTS pm_parking_space (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `parking_code` varchar(32) DEFAULT NULL COMMENT '车位编号',
    `parkinglot_name` varchar(64) DEFAULT NULL COMMENT '停车场名称',
    `area` decimal(18,4) DEFAULT NULL COMMENT '面积',
    `org_id` varchar(32) DEFAULT NULL COMMENT '小区ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_company_org` (`company_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='车位';

-- ========================================
-- 菜单系统迁移
-- 从 rltk_pro.sys_menu 迁移到 sys_menu
-- ========================================


-- ========================================
-- Custom Thermal/Meter/Charging Menu Data
-- ========================================

-- 供热平衡管理 (一级菜单)
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2000, '供热平衡管理', 0, 5, 'thermal', 'Layout', 0, 0, 'M', '0', '0', NULL, 'thermometer', 103, 1, NOW(), '供热平衡管理模块')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 控制策略
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2001, '控制策略', 2000, 1, 'strategy', 'thermal/ht/strategy/index', 0, 0, 'C', '0', '0', 'thermal:ht:strategy:list', 'strategy', 103, 1, NOW(), '控制策略管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 控制指令
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2002, '控制指令', 2000, 2, 'instruction', 'thermal/ht/instruction/index', 0, 0, 'C', '0', '0', 'thermal:ht:instruction:list', 'edit', 103, 1, NOW(), '控制指令管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 报警记录
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2003, '报警记录', 2000, 3, 'alert', 'thermal/ht/alert/index', 0, 0, 'C', '0', '0', 'thermal:ht:alert:list', 'bug', 103, 1, NOW(), '报警记录管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 报修记录
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2004, '报修记录', 2000, 4, 'repair', 'thermal/ht/repair/index', 0, 0, 'C', '0', '0', 'thermal:ht:repair:list', 'tool', 103, 1, NOW(), '报修记录管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 调控任务
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2005, '调控任务', 2000, 5, 'tasks', 'thermal/ht/tasks/index', 0, 0, 'C', '0', '0', 'thermal:ht:tasks:list', 'tree-table', 103, 1, NOW(), '调控任务管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 房屋策略
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2006, '房屋策略', 2000, 6, 'houseStrategy', 'thermal/ht/houseStrategy/index', 0, 0, 'C', '0', '0', 'thermal:ht:houseStrategy:list', 'nested', 103, 1, NOW(), '房屋策略管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 仪表管理 (一级菜单)
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2100, '仪表管理', 0, 6, 'meter', 'Layout', 0, 0, 'M', '0', '0', NULL, 'component', 103, 1, NOW(), '仪表管理模块')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 仪表厂商
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2101, '仪表厂商', 2100, 1, 'vendor', 'thermal/meter/vendor/index', 0, 0, 'C', '0', '0', 'thermal:meter:vendor:list', 'peoples', 103, 1, NOW(), '仪表厂商管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 仪表分类
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2102, '仪表分类', 2100, 2, 'sort', 'thermal/meter/sort/index', 0, 0, 'C', '0', '0', 'thermal:meter:sort:list', 'tree', 103, 1, NOW(), '仪表分类管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 电表档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2103, '电表档案', 2100, 3, 'electric', 'thermal/meter/electric/index', 0, 0, 'C', '0', '0', 'thermal:meter:electric:list', 'form', 103, 1, NOW(), '电表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 水表档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2104, '水表档案', 2100, 4, 'water', 'thermal/meter/water/index', 0, 0, 'C', '0', '0', 'thermal:meter:water:list', 'form', 103, 1, NOW(), '水表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 热力表档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2105, '热力表档案', 2100, 5, 'heat', 'thermal/meter/heat/index', 0, 0, 'C', '0', '0', 'thermal:meter:heat:list', 'form', 103, 1, NOW(), '热力表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 燃气表档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2106, '燃气表档案', 2100, 6, 'gas', 'thermal/meter/gas/index', 0, 0, 'C', '0', '0', 'thermal:meter:gas:list', 'form', 103, 1, NOW(), '燃气表档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 集中器档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2107, '集中器档案', 2100, 7, 'centrator', 'thermal/meter/centrator/index', 0, 0, 'C', '0', '0', 'thermal:meter:centrator:list', 'form', 103, 1, NOW(), '集中器档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 温控器档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2108, '温控器档案', 2100, 8, 'tc', 'thermal/meter/tc/index', 0, 0, 'C', '0', '0', 'thermal:meter:tc:list', 'form', 103, 1, NOW(), '温控器档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 阀门档案
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2109, '阀门档案', 2100, 9, 'valve', 'thermal/meter/valve/index', 0, 0, 'C', '0', '0', 'thermal:meter:valve:list', 'form', 103, 1, NOW(), '阀门档案管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 收费管理 (一级菜单)
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2200, '收费管理', 0, 7, 'charge', 'Layout', 0, 0, 'M', '0', '0', NULL, 'money', 103, 1, NOW(), '收费管理模块')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 日常收费
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2201, '日常收费', 2200, 1, 'singleCharge', 'thermal/property/singleCharge/index', 0, 0, 'C', '0', '0', 'thermal:property:singleCharge:charge', 'guide', 103, 1, NOW(), '日常收费')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 收费标准
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2202, '收费标准', 2200, 2, 'standard', 'thermal/property/standard/index', 0, 0, 'C', '0', '0', 'thermal:property:standard:list', 'documentation', 103, 1, NOW(), '收费标准管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 费目管理
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2203, '费目管理', 2200, 3, 'expenseItem', 'thermal/property/expenseItem/index', 0, 0, 'C', '0', '0', 'thermal:property:expenseItem:list', 'edit', 103, 1, NOW(), '费目管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 费用明细
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2204, '费用明细', 2200, 4, 'expense', 'thermal/property/expense/index', 0, 0, 'C', '0', '0', 'thermal:property:expense:list', 'list', 103, 1, NOW(), '费用明细管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 房屋费用
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2205, '房屋费用', 2200, 5, 'houseExpense', 'thermal/property/houseExpense/index', 0, 0, 'C', '0', '0', 'thermal:property:houseExpense:list', 'nested', 103, 1, NOW(), '房屋费用绑定')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 客户管理
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2206, '客户管理', 2200, 6, 'user', 'thermal/property/user/index', 0, 0, 'C', '0', '0', 'thermal:property:user:list', 'user', 103, 1, NOW(), '客户管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 账户管理
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2207, '账户管理', 2200, 7, 'account', 'thermal/property/account/index', 0, 0, 'C', '0', '0', 'thermal:property:account:list', 'account', 103, 1, NOW(), '个人账户管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 交易记录
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2208, '交易记录', 2200, 8, 'transaction', 'thermal/property/transaction/index', 0, 0, 'C', '0', '0', 'thermal:property:transaction:list', 'clipboard', 103, 1, NOW(), '交易记录管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 票据备注
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2209, '票据备注', 2200, 9, 'billingNotes', 'thermal/property/billingNotes/index', 0, 0, 'C', '0', '0', 'thermal:property:billingNotes:list', 'notebook', 103, 1, NOW(), '票据备注管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 系统选项
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2210, '系统选项', 2200, 10, 'options', 'thermal/property/options/index', 0, 0, 'C', '0', '0', 'thermal:property:options:list', 'setting', 103, 1, NOW(), '系统选项管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 写卡记录
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2211, '写卡记录', 2200, 11, 'useCardLog', 'thermal/property/useCardLog/index', 0, 0, 'C', '0', '0', 'thermal:property:useCardLog:list', 'log', 103, 1, NOW(), '写卡日志管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 房屋变更
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2212, '房屋变更', 2200, 12, 'houseChange', 'thermal/property/houseChange/index', 0, 0, 'C', '0', '0', 'thermal:property:houseChange:list', 'input', 103, 1, NOW(), '房屋入住/迁出管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 打印模板
INSERT INTO `sys_menu` (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, remark)
VALUES (2213, '打印模板', 2200, 13, 'printTemplate', 'thermal/property/printTemplate/index', 0, 0, 'C', '0', '0', 'thermal:property:printTemplate:list', 'printer', 103, 1, NOW(), '打印模板管理')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);
