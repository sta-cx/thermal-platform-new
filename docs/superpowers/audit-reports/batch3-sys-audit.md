# Sys 系统管理模块迁移审核报告

## 审核概览
- **审核日期**: 2026-04-26
- **旧系统路径**: D:\chonggou\thermal-balance-backend
- **新系统路径**: D:\chonggou\thermal-platform-new
- **旧系统 Controller 数**: 13
- **新系统对应 Controller 数**: 11
- **完全匹配**: 8
- **部分匹配**: 3
- **缺失**: 2
- **完全重构**: 2

## 审核范围

本次审核涵盖以下 Controller：
1. SysCompany - 公司/租户管理
2. SysDict - 字典管理
3. SysHome - 首页统计
4. Role - 角色管理
5. Menu - 菜单管理
6. PropertyMenu - 物业菜单管理
7. User - 用户管理
8. Area - 地区管理
9. OssManager - OSS 文件管理
10. AccessCode - 仪表编码管理
11. SaOAuth2Server - OAuth2 认证服务
12. AgAutoVersion - 自助机版本管理
13. AgReaderParam - 读卡器参数管理

---

## 逐 Controller 对比

### 1. SysCompanyController - 公司/租户管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /company/pageList | POST | /system/tenant/list | GET | MATCH | 新系统使用租户模式 |
| /company/checkTele | POST | - | - | MISSING | **缺失**：手机号校验 |
| /company/save | POST | /system/tenant | POST | MATCH | |
| /company/updateData | POST | /system/tenant | PUT | MATCH | |
| /company/verifyDelete | POST | - | - | MISSING | **缺失**：删除前校验 |
| /company/deleteData | POST | /system/tenant/{ids} | DELETE | MATCH | |
| /company/editDetails | POST | - | - | MISSING | **缺失**：编辑详情 |
| /company/selectDetails | POST | - | - | MISSING | **缺失**：查看详情 |

#### 业务逻辑差异
- **架构变更**：新系统采用多租户架构，公司概念改为租户（Tenant）
- 新系统增加了租户套餐管理功能
- 新系统增加了租户同步功能（syncTenantPackage, syncTenantDict, syncTenantConfig）
- 新系统增加了动态租户切换功能
- 旧系统创建公司时自动创建用户和角色关联，新系统需确认是否保留此逻辑

#### 代码质量问题
- **功能简化**：新系统缺少详情查看和编辑详情功能
- **安全改进**：新系统增加了 @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY) 权限控制

---

### 2. SysDictController - 字典管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /sysDict/dictTpyeList | POST | /system/dict/type/list | GET | MATCH | 字典类型列表 |
| /sysDict/dictList | POST | /system/dict/data/list | GET | MATCH | 字典数据列表 |
| /sysDict/dictTpyeFrom | POST | /system/dict/type/{dictId} | GET | MATCH | 字典类型详情 |
| /sysDict/isDictTypeRepeat | POST | - | - | MISSING | **缺失**：字典类型重复校验 |
| /sysDict/isDictRepeat | POST | - | - | MISSING | **缺失**：字典数据重复校验 |
| /sysDict/insertData | POST | /system/dict/type + /system/dict/data | POST | MATCH | 拆分为两个接口 |
| /sysDict/deleteData | POST | /system/dict/type/{dictIds} | DELETE | MATCH | |
| /sysDict/getDataByType | POST | /system/dict/data/type/{dictType} | GET | MATCH | |

#### 业务逻辑差异
- **接口拆分**：新系统将字典类型和字典数据拆分为两个独立的 Controller
- 新系统增加了字典缓存刷新功能（/refreshCache）
- 新系统增加了导出功能（/export）
- 新系统移除了单独的重复校验端点（改为在新增/更新时自动校验）

#### 代码质量问题
- **架构改进**：拆分 Controller 使职责更清晰
- **功能改进**：增加了缓存管理和导出功能
- **兼容性风险**：接口拆分可能导致前端需要大量修改

---

### 3. SysHomeController - 首页统计

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /home/querHomeData | POST | /thermal/home/statistics | GET | PARTIAL | **骨架实现** |

#### 业务逻辑差异
- **重大变更**：新系统仅返回骨架数据，真实业务逻辑标记为 TODO
- 旧系统通过 6 个异步 Future 并发查询：
  - querHomeData1: 设备统计（热力表、电表、水表、燃气表数量）
  - querHomeData2: 收费统计（应收、实收、欠费）
  - querHomeData3: 告警统计（各类告警数量）
  - querHomeData4: 策略统计（控制策略数量、执行状态）
  - querHomeData6: 任务统计（定时任务、执行记录）
  - querHomeData7: 用户统计（业主数量、绑定率）
- 新系统目前只返回 0 值占位符

#### 代码质量问题
- **严重问题**：核心业务逻辑完全未实现
- **迁移计划**：注释中说明 Phase 3-5 业务模块迁移后补充真实查询
- **性能考虑**：旧系统使用并发查询，新系统实现时应保留此优化

---

### 4. RoleController - 角色管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /role/{id} | GET | /system/role/{roleId} | GET | MATCH | |
| /role | POST | /system/role | POST | MATCH | |
| /role | PUT | /system/role | PUT | MATCH | |
| /role/{id} | DELETE | /system/role/{roleIds} | DELETE | MATCH | |
| /role/list | GET | /system/role/list | GET | MATCH | |
| /role/page | GET | /system/role/list | GET | PARTIAL | 分页功能合并 |
| /role/menu | PUT | - | - | MISSING | **缺失**：更新角色菜单 |
| - | - | /system/role/dataScope | PUT | EXTRA | 新增数据权限 |
| - | - | /system/role/changeStatus | PUT | EXTRA | 新增状态修改 |
| - | - | /system/role/authUser/* | * | EXTRA | 新增用户授权管理 |

#### 业务逻辑差异
- **功能增强**：新系统增加了数据权限管理功能
- **功能增强**：新系统增加了用户授权管理（分配/取消用户角色）
- **功能缺失**：新系统缺少 /role/menu 端点（角色菜单分配）
- 新系统移除了角色菜单缓存清理逻辑（旧系统在更新/删除时清理 Sa-Token 缓存）

#### 代码质量问题
- **关键缺失**：/role/menu 端点缺失可能导致无法为角色分配菜单权限
- **功能改进**：新增的数据权限和用户授权管理是重要的功能增强
- **权限细化**：新系统使用更细粒度的权限注解

---

### 5. MenuController - 菜单管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /menu | GET | /system/menu/getRouters | GET | MATCH | 获取用户路由 |
| /menu/tree1 | GET | - | - | MISSING | **缺失**：未分配菜单树 |
| /menu/tree/{roleId} | GET | /system/menu/roleMenuTreeselect/{roleId} | GET | MATCH | |
| /menu/{id} | GET | /system/menu/{menuId} | GET | MATCH | |
| /menu | POST | /system/menu | POST | MATCH | |
| /menu/{id} | DELETE | /system/menu/{menuId} | DELETE | MATCH | |
| /menu | PUT | /system/menu | PUT | MATCH | |
| - | - | /system/menu/treeselect | GET | EXTRA | 新增菜单树下拉 |
| - | - | /system/menu/tenantPackageMenuTreeselect/{packageId} | GET | EXTRA | 新增租户套餐菜单 |
| - | - | /system/menu/cascade/{menuIds} | DELETE | EXTRA | 新增级联删除 |

#### 业务逻辑差异
- **功能增强**：新系统增加了租户套餐菜单管理
- **功能增强**：新系统增加了级联删除功能
- **功能缺失**：新系统缺少 /menu/tree1 端点（未分配菜单查询）
- 新系统移除了自定义的树构建逻辑，使用 Hutool 的 Tree 工具

#### 代码质量问题
- **架构改进**：使用标准化的树结构工具
- **功能增强**：租户套餐菜单管理是多租户架构的重要功能
- **缺失影响**：缺少未分配菜单查询可能影响菜单分配操作

---

### 6. PropertyMenuController - 物业菜单管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /propertyMenu/tree | GET | - | - | MISSING | **未迁移** |
| /propertyMenu | POST | - | - | MISSING | **未迁移** |
| /propertyMenu/{id} | GET | - | - | MISSING | **未迁移** |
| /propertyMenu/{id} | DELETE | - | - | MISSING | **未迁移** |
| /propertyMenu | PUT | - | - | MISSING | **未迁移** |

#### 业务逻辑差异
- **完全未迁移**：新系统中没有找到 PropertyMenuController
- 旧系统中物业菜单是独立的菜单系统（type=2），与系统菜单（type=0/1）分开管理
- 新系统可能将物业菜单合并到系统菜单中

#### 代码质量问题
- **迁移缺失**：需要确认物业菜单管理功能是否需要保留
- 如果需要保留，建议：
  - 在 SysMenu 中增加 type 字段区分
  - 或创建独立的 PrMenuController

---

### 7. UserController - 用户管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /user/info | GET | /system/user/getInfo | GET | MATCH | |
| /user/logout | POST | /auth/logout | POST | MATCH | 迁移到 AuthController |
| - | - | /system/user/list | GET | EXTRA | 新增用户列表 |
| - | - | /system/user/{userId} | GET | EXTRA | 新增用户详情 |
| - | - | /system/user | POST | EXTRA | 新增用户 |
| - | - | /system/user | PUT | EXTRA | 新增用户更新 |
| - | - | /system/user/{userIds} | DELETE | EXTRA | 新增用户删除 |
| - | - | /system/user/resetPwd | PUT | EXTRA | 新增重置密码 |
| - | - | /system/user/changeStatus | PUT | EXTRA | 新增状态修改 |
| - | - | /system/user/authRole/{userId} | GET | EXTRA | 新增角色授权 |
| - | - | /system/user/authRole | PUT | EXTRA | 新增保存角色授权 |

#### 业务逻辑差异
- **重大变更**：旧系统 UserController 仅包含登录相关的 info/logout 端点
- 新系统增加了完整的用户 CRUD 功能
- 新系统将用户管理功能集中到 SysUserController
- 旧系统的用户认证逻辑（公司审核、到期检查等）未在新系统中找到

#### 代码质量问题
- **功能增强**：新系统提供了完整的用户管理功能
- **业务逻辑缺失**：旧系统中的公司审核、到期检查等逻辑需要确认是否迁移
- **架构改进**：职责分离更清晰（认证 vs 用户管理）

---

### 8. AreaController - 地区管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /area/province | GET | /thermal/area/provinces | GET | MATCH | |
| /area/city | GET | /thermal/area/cities/{provinceId} | GET | MATCH | |
| /area/county | GET | /thermal/area/districts/{cityId} | GET | MATCH | |

#### 业务逻辑差异
- **路径变更**：从 /area 改为 /thermal/area
- **参数变更**：新系统使用路径参数替代查询参数
- 新系统增加了 @SaCheckLogin 权限控制

#### 代码质量问题
- 无明显问题
- RESTful 风格改进

---

### 9. OssManagerController - OSS 文件管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /stsOss/read | GET | - | - | MISSING | **缺失**：读权限Token |
| /stsOss/write | GET | - | - | MISSING | **缺失**：写权限Token |
| /stsOss/manager | GET | - | - | MISSING | **缺失**：管理权限Token |
| /stsOss/getCode | GET | - | - | MISSING | **缺失**：发送验证码 |
| /stsOss/uploadFile | POST | /resource/oss/upload | POST | MATCH | |
| /stsOss/getUrl | POST | - | - | MISSING | **缺失**：获取文件URL |
| /stsOss/hasFileInOss | POST | - | - | MISSING | **缺失**：检查文件存在 |
| /stsOss/validateTele | GET | - | - | MISSING | **缺失**：验证手机号 |
| /stsOss/validateAgentCode | GET | - | - | MISSING | **缺失**：验证代理商码 |
| /stsOss/registerCompany | POST | - | - | MISSING | **缺失**：注册公司 |

#### 业务逻辑差异
- **功能大幅简化**：新系统仅保留基础的上传功能
- **功能缺失**：缺少 STS Token 管理、验证码发送、公司注册等功能
- 新系统增加了文件列表、下载、删除等管理功能

#### 代码质量问题
- **严重功能缺失**：STS Token 管理功能缺失可能影响前端直传 OSS
- **功能迁移**：验证码、公司注册等功能可能迁移到其他模块
- **权限控制**：新系统增加了细粒度的权限控制

---

### 10. AccessCodeController - 仪表编码管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /accessCode/accessMtVendorCode | GET | /thermal/agent/access-code/vendorCode | GET | MATCH | |
| /accessCode/accessMtSortCode | GET | /thermal/agent/access-code/sortCode | GET | MATCH | |
| /accessCode/accessMeterCode | GET | /thermal/agent/access-code/meterCode | GET | MATCH | |

#### 业务逻辑差异
- **路径变更**：从 /accessCode 改为 /thermal/agent/access-code
- **实现变更**：新系统使用 JdbcTemplate 直接查询，旧系统通过 Service
- 新系统增加了 @SaCheckLogin 和 @SaCheckPermission 权限控制

#### 代码质量问题
- **代码质量改进**：新系统使用更简洁的实现
- **安全改进**：增加了权限控制
- 无明显功能缺失

---

### 11. SaOAuth2ServerController - OAuth2 认证服务

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /oauth/authorize | GET | - | - | MISSING | **未迁移** |
| /oauth/doLogin | GET | - | - | MISSING | **未迁移** |
| /oauth/doConfirm | GET | - | - | MISSING | **未迁移** |
| /oauth/token | GET | /auth/login | POST | PARTIAL | **重构** |
| /oauth/refresh | GET | - | - | MISSING | **未迁移** |
| /oauth/revoke | GET | - | - | MISSING | **未迁移** |
| /oauth/client_token | GET | - | - | MISSING | **未迁移** |

#### 业务逻辑差异
- **完全重构**：新系统使用 AuthController 替代 SaOAuth2ServerController
- 新系统支持多种认证方式（密码、社交登录、短信等）
- 新系统移除了 OAuth2 标准流程，改为自定义的认证流程
- 新系统增加了租户登录支持

#### 代码质量问题
- **架构变更**：从标准 OAuth2 改为自定义认证流程
- **功能变更**：移除了 OAuth2 标准的授权码、刷新令牌等流程
- **兼容性风险**：如果前端依赖标准 OAuth2 流程，需要大量修改
- **功能增强**：新系统支持更多认证方式

---

### 12. AgAutoVersionController - 自助机版本管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /agAutoVersion/pageList | GET | /thermal/agent/auto-version/list | GET | MATCH | |
| /agAutoVersion/updateData | GET | /thermal/agent/auto-version | PUT | MATCH | |

#### 业务逻辑差异
- **路径变更**：从 /agAutoVersion 改为 /thermal/agent/auto-version
- **方法变更**：updateData 从 GET 改为 PUT，符合 RESTful 规范
- 新系统增加了 @SaCheckLogin 和 @SaCheckPermission 权限控制

#### 代码质量问题
- **代码质量改进**：使用标准的 RESTful 方法
- **安全改进**：增加了权限控制
- 无明显功能缺失

---

### 13. AgReaderParamController - 读卡器参数管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /common/AgReaderParam/getDataByCode | POST | /thermal/agent/reader-param | GET | MATCH | |

#### 业务逻辑差异
- **路径变更**：从 /common/AgReaderParam 改为 /thermal/agent/reader-param
- **方法变更**：从 POST 改为 GET
- **参数变更**：从请求体改为查询参数
- 新系统增加了 @SaCheckLogin 和 @SaCheckPermission 权限控制

#### 代码质量问题
- **代码质量改进**：使用 GET 方法更符合查询语义
- **安全改进**：增加了权限控制
- 无明显功能缺失

---

## 总结与建议

### 关键发现

#### 1. 完全未迁移的 Controller
- **PropertyMenuController**: 物业菜单管理（5个端点完全缺失）
- **SaOAuth2ServerController**: OAuth2 认证服务（7个端点，部分功能迁移到 AuthController）

#### 2. 严重功能缺失的 Controller

**SysCompanyController** 缺失以下关键功能：
- checkTele（手机号校验）
- verifyDelete（删除前校验）
- editDetails（编辑详情）
- selectDetails（查看详情）

**OssManagerController** 缺失以下关键功能：
- STS Token 管理（read/write/manager）
- 验证码发送（getCode）
- 文件 URL 获取（getUrl）
- 文件存在检查（hasFileInOss）
- 手机号/代理商码验证
- 公司注册功能

**RoleController** 缺失：
- /role/menu（更新角色菜单）

**MenuController** 缺失：
- /menu/tree1（未分配菜单树）

**SysHomeController** 缺失：
- 首页统计业务逻辑完全未实现（仅返回骨架数据）

#### 3. 架构重构的 Controller

**UserController**:
- 旧系统仅包含登录相关的 info/logout
- 新系统增加了完整的用户 CRUD 功能
- 职责分离更清晰

**SaOAuth2ServerController → AuthController**:
- 从标准 OAuth2 流程改为自定义认证流程
- 支持更多认证方式
- 增加了租户登录支持

#### 4. 功能增强的 Controller

**RoleController**:
- 新增数据权限管理
- 新增用户授权管理
- 新增状态修改

**MenuController**:
- 新增租户套餐菜单管理
- 新增级联删除

**SysUserController**:
- 新增完整的用户 CRUD
- 新增角色授权
- 新增密码重置

### 缺失功能清单

#### 高优先级缺失
1. **首页统计数据**（SysHomeController）
   - 设备统计
   - 收费统计
   - 告警统计
   - 策略统计
   - 任务统计
   - 用户统计

2. **角色菜单分配**（RoleController）
   - /role/menu 端点

3. **STS Token 管理**（OssManagerController）
   - /stsOss/read
   - /stsOss/write
   - /stsOss/manager

4. **物业菜单管理**（PropertyMenuController）
   - 完整的 CRUD 功能

#### 中优先级缺失
5. **公司详情管理**（SysCompanyController）
   - editDetails
   - selectDetails

6. **OSS 文件管理**（OssManagerController）
   - getUrl（获取文件 URL）
   - hasFileInOss（检查文件存在）

7. **未分配菜单查询**（MenuController）
   - /menu/tree1

#### 低优先级缺失
8. **公司注册相关**（OssManagerController）
   - 验证码发送
   - 手机号/代理商码验证
   - 公司注册

9. **公司删除校验**（SysCompanyController）
   - verifyDelete
   - checkTele

### 代码质量风险

#### 安全性改进
- ✅ 新系统全面使用 Sa-Token 权限注解
- ✅ 新系统增加了细粒度的权限控制
- ✅ 新系统增加了操作日志（@Log 注解）
- ✅ 新系统增加了参数校验（@Validated 注解）

#### 架构改进
- ✅ 新系统使用 RESTful 风格的 API 设计
- ✅ 新系统使用 BO/VO 模式进行数据转换
- ✅ 新系统职责分离更清晰（认证 vs 用户管理）
- ✅ 新系统支持多租户架构
- ✅ 新系统增加了数据权限管理

#### 潜在问题
1. **功能完整性风险**：多个核心功能未迁移
2. **向后兼容性**：API 路径和方法的变更需要前端同步修改
3. **OAuth2 标准**：移除标准 OAuth2 流程可能影响集成
4. **首页统计**：业务逻辑完全未实现，影响用户体验

### 建议行动项

#### 立即处理
1. **实现首页统计数据**（SysHomeController）
   - 这是用户登录后看到的第一个页面
   - 需要实现 6 个统计查询的业务逻辑
   - 建议使用并发查询优化性能

2. **补充角色菜单分配功能**（RoleController）
   - 这是权限管理的核心功能
   - 缺失会导致无法为角色分配菜单

3. **确认物业菜单管理方案**（PropertyMenuController）
   - 决定是否保留独立的物业菜单
   - 或合并到系统菜单中

#### 短期处理
4. **评估 STS Token 管理的迁移优先级**
   - 如果前端使用直传 OSS，必须实现
   - 否则可以继续使用服务端上传

5. **补充公司详情管理功能**（SysCompanyController）
   - editDetails
   - selectDetails

6. **验证 OAuth2 流程变更的影响**
   - 确认所有客户端是否已适配新的认证流程

#### 长期优化
7. **建立完善的 API 文档**
8. **增加集成测试覆盖所有 API 端点**
9. **考虑实现 OAuth2 标准流程**（如果需要标准兼容）

---

**审核完成日期**: 2026-04-26
**审核人员**: Claude Code Agent
**下次审核建议**: 完成缺失功能迁移后重新审核
