# Batch 2: Agent 代理管理模块迁移审核报告

**审核日期**: 2026-04-26
**审核范围**: 代理商管理相关的 6 个 Controller
**审核人**: Claude Code (Agent Team)

---

## 执行摘要

| 模块 | 迁移状态 | 覆盖度 | 严重性 |
|------|----------|--------|--------|
| AgentCompanyController | ❌ 未迁移 | 0% | **高** |
| AgentMeterController | ❌ 未迁移 | 0% | **高** |
| AgentPropertyController | ❌ 未迁移 | 0% | **高** |
| AgentPropertyMenuController | ❌ 未迁移 | 0% | **高** |
| AgentRoleController | ❌ 未迁移 | 0% | **高** |
| AgentUserController | ❌ 未迁移 | 0% | **高** |
| **辅助模块** |  |  |  |
| AccessCodeController | ✅ 已迁移 | 90% | 低 |
| AgReaderParamController | ✅ 已迁移 | 100% | 无 |
| AgAutoVersionController | ✅ 已迁移 | 100% | 无 |
| PrAutoMachineController | 🟡 骨架迁移 | 30% | 中 |

**总体评估**: 代理商核心管理模块完全未迁移，仅自助机辅助功能部分迁移。

---

## 详细对比

### 1. AgentCompanyController（代理商公司管理）

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentCompanyController.java`

#### API 端点对比

| 旧端点 | HTTP 方法 | 功能 | 新系统对应 | 状态 |
|--------|-----------|------|------------|------|
| `/agent/company/pageList` | POST | 左侧公司树查询 | ❌ 不存在 | MISSING |
| `/agent/company/getDataById` | POST | 根据ID查询公司 | ❌ 不存在 | MISSING |
| `/agent/company/deleteData` | POST | 删除代理商 | ❌ 不存在 | MISSING |
| `/agent/company/verifyTele` | POST | 验证手机号是否重复 | ❌ 不存在 | MISSING |
| `/agent/company/verifyCode` | POST | 验证编码是否重复 | ❌ 不存在 | MISSING |
| `/agent/company/verifyName` | POST | 验证名称是否重复 | ❌ 不存在 | MISSING |
| `/agent/company/insertData` | POST | 新增代理商 | ❌ 不存在 | MISSING |
| `/agent/company/startUsing` | POST | 启用代理商 | ❌ 不存在 | MISSING |
| `/agent/company/endUsing` | POST | 禁用代理商 | ❌ 不存在 | MISSING |
| `/agent/company/updateData` | POST | 更新代理商 | ❌ 不存在 | MISSING |

#### 业务逻辑分析

旧系统 `AgentCompanyServiceImpl` 核心功能：
- 创建代理商时同步创建超管用户
- 创建代理商角色（ROLE_SUPER_AGENT）
- 自动分配全部菜单权限给超管角色
- 启用/禁用时同步处理关联用户状态

**迁移建议**: 该功能可能已被新系统的多租户架构（SysTenant）替代，需确认业务需求。

---

### 2. AgentMeterController（代理商仪表分配）

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentMeterController.java`

#### API 端点对比

| 旧端点 | HTTP 方法 | 功能 | 新系统对应 | 状态 |
|--------|-----------|------|------------|------|
| `/agent/meter/meterList` | POST | 查询已分配仪表（分页） | ❌ 不存在 | MISSING |
| `/agent/meter/getList` | POST | 查询所有可分配仪表 | ❌ 不存在 | MISSING |
| `/agent/meter/insertData` | POST | 保存仪表分配 | ❌ 不存在 | MISSING |

#### 业务逻辑分析

支持仪表类型：
- `01`: 电表 (mt_electric_archive)
- `02`: 水表 (mt_water_archive)
- `03`: 热力表 (mt_heat_archive)
- `04`: 燃气表 (mt_gas_archive)

核心逻辑：先删除已分配，再批量插入新分配。

---

### 3. AgentPropertyController（代理商-物业关联管理）

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentPropertyController.java`

#### API 端点对比

| 旧端点 | HTTP 方法 | 功能 | 新系统对应 | 状态 |
|--------|-----------|------|------------|------|
| `/agent/propery/pageList` | POST | 代理商所属物业公司列表 | ❌ 不存在 | MISSING |
| `/agent/propery/queryAgMenuList` | POST | 查询代理商未分配菜单 | ❌ 不存在 | MISSING |
| `/agent/propery/queryPrMenuList` | POST | 查询物业公司已分配菜单 | ❌ 不存在 | MISSING |
| `/agent/propery/menuInsertData` | POST | 保存菜单分配 | ❌ 不存在 | MISSING |
| `/agent/propery/updatePrAudited` | POST | 保存审核状态 | ❌ 不存在 | MISSING |
| `/agent/propery/updatePrEnabled` | POST | 保存启用状态 | ❌ 不存在 | MISSING |
| `/agent/propery/queryPrCompany` | POST | 查询物业公司初始设置 | ❌ 不存在 | MISSING |
| `/agent/propery/updataPrCompany` | POST | 更新物业公司信息 | ❌ 不存在 | MISSING |
| `/agent/propery/queryAutoMachine` | POST | 查询自助机档案 | ❌ 不存在 | MISSING |
| `/agent/propery/updateAutoMachine` | POST | 保存自助机档案 | ❌ 不存在 | MISSING |
| `/agent/propery/queryMeter` | POST | 查询可分配仪表信息 | ❌ 不存在 | MISSING |
| `/agent/propery/updataMeter` | POST | 保存仪表分配 | ❌ 不存在 | MISSING |

#### 业务逻辑分析

支持的仪表类型：
- `03`: 热表 (mt_heat_archive)
- `11`: 采集器 (mt_water_archive)
- `21`: 温采器 (mt_tc_archive)
- `31`: 阀门 (mt_tc_valve)

---

### 4. AgentPropertyMenuController（物业公司菜单权限）

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentPropertyMenuController.java`

#### API 端点对比

| 旧端点 | HTTP 方法 | 功能 | 新系统对应 | 状态 |
|--------|-----------|------|------------|------|
| `/agent/propertyMenu/findYes` | POST | 查询已分配菜单 | ❌ 不存在 | MISSING |
| `/agent/propertyMenu/findNo` | POST | 查询未分配菜单 | ❌ 不存在 | MISSING |
| `/agent/propertyMenu/permissionUpd` | POST | 更新菜单权限 | ❌ 不存在 | MISSING |

---

### 5. AgentRoleController（代理商角色管理）

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentRoleController.java`

#### API 端点对比

| 旧端点 | HTTP 方法 | 功能 | 新系统对应 | 状态 |
|--------|-----------|------|------------|------|
| `/agent/role/pageList` | POST | 角色列表 | ❌ 不存在 | MISSING |
| `/agent/role/allRole` | POST | 用户未分配角色 | ❌ 不存在 | MISSING |
| `/agent/role/allReadlyRole` | POST | 用户已有角色 | ❌ 不存在 | MISSING |
| `/agent/role/insertData` | POST | 新增角色 | ❌ 不存在 | MISSING |
| `/agent/role/updateData` | POST | 修改角色 | ❌ 不存在 | MISSING |
| `/agent/role/deleteData` | POST | 删除角色 | ❌ 不存在 | MISSING |
| `/agent/role/verifyIdent` | POST | 验证标识是否重复 | ❌ 不存在 | MISSING |
| `/agent/role/verifyName` | POST | 验证名称是否重复 | ❌ 不存在 | MISSING |
| `/agent/role/findYes` | POST | 查询角色已分配菜单 | ❌ 不存在 | MISSING |
| `/agent/role/findNo` | POST | 查询角色未分配菜单 | ❌ 不存在 | MISSING |
| `/agent/role/permissionUpd` | POST | 保存菜单权限 | ❌ 不存在 | MISSING |

#### 业务逻辑分析

角色标识格式：`ROLE_{角色名称}`

---

### 6. AgentUserController（代理商用户管理）

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentUserController.java`

#### API 端点对比

| 旧端点 | HTTP 方法 | 功能 | 新系统对应 | 状态 |
|--------|-----------|------|------------|------|
| `/agent/user/pageList` | POST | 用户列表 | ❌ 不存在 | MISSING |
| `/agent/user/checkTele` | POST | 校验手机号是否已注册 | ❌ 不存在 | MISSING |
| `/agent/user/insertData` | POST | 新增用户 | ❌ 不存在 | MISSING |
| `/agent/user/updateData` | POST | 修改用户 | ❌ 不存在 | MISSING |
| `/agent/user/deleteData` | POST | 删除用户 | ❌ 不存在 | MISSING |
| `/agent/user/startUsing` | POST | 启用用户 | ❌ 不存在 | MISSING |
| `/agent/user/endUsing` | POST | 禁用用户 | ❌ 不存在 | MISSING |
| `/agent/user/saveUserRole` | POST | 保存用户角色关联 | ❌ 不存在 | MISSING |

#### 业务逻辑分析

密码加密：BCryptPasswordEncoder
用户默认非超管（isSuper = 0）

---

### 7. AccessCodeController（仪表编码获取）✅

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AccessCodeController.java`
**新系统路径**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\AccessCodeController.java`

#### API 端点对比

| 旧端点 | 新端点 | HTTP 方法 | 状态 | 备注 |
|--------|--------|-----------|------|------|
| `/accessCode/accessMtVendorCode` | `/thermal/agent/access-code/vendorCode` | GET | ✅ MATCH | 已迁移 |
| `/accessCode/accessMtSortCode` | `/thermal/agent/access-code/sortCode` | GET | ✅ MATCH | 已迁移 |
| `/accessCode/accessMeterCode` | `/thermal/agent/access-code/meterCode` | GET | ✅ MATCH | 已迁移，支持更多仪表类型 |

**改进点**：
- 新增权限注解 `@SaCheckPermission`
- 新增登录校验 `@SaCheckLogin`
- 参数校验更规范
- 代码结构更清晰，使用 switch 表达式

---

### 8. AgReaderParamController（自助机读卡器参数）✅

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\PrAutoMachineController.java` (getReaderParam方法)
**新系统路径**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\AgReaderParamController.java`

#### API 端点对比

| 旧端点 | 新端点 | HTTP 方法 | 状态 | 备注 |
|--------|--------|-----------|------|------|
| `/property/autoMachine/getReaderParam` | `/thermal/agent/reader-param` | GET | ✅ MATCH | 已独立为Controller |

**改进点**：
- 从 PrAutoMachineController 中独立出来
- 使用 MyBatis-Plus Lambda 查询
- 权限控制完善

---

### 9. AgAutoVersionController（自助机版本管理）✅

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\PrAutoMachineController.java` (getClientVersion方法)
**新系统路径**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\AgAutoVersionController.java`

#### API 端点对比

| 旧端点 | 新端点 | HTTP 方法 | 状态 | 备注 |
|--------|--------|-----------|------|------|
| `/property/autoMachine/getClientVersion` | `/thermal/agent/auto-version/list` | GET | ✅ MATCH | 已迁移 |
| - | `/thermal/agent/auto-version` (PUT) | PUT | ✅ EXTRA | 新增更新接口 |

**改进点**：
- RESTful 风格 API
- 使用标准 HTTP 方法
- 权限注解完善

---

### 10. PrAutoMachineController（自助机管理）🟡

**旧系统路径**: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\PrAutoMachineController.java`
**新系统路径**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\PrAutoMachineController.java`

#### API 端点对比

| 旧端点 | 新端点 | HTTP 方法 | 状态 | 备注 |
|--------|--------|-----------|------|------|
| `/property/autoMachine/pageList` | `/thermal/property/auto-machine/list` | GET | 🟡 SKELETON | 骨架已实现，业务逻辑待完成 |
| `/property/autoMachine/getDataById` | `/thermal/property/auto-machine/{id}` | GET | 🟡 SKELETON | 骨架已实现 |
| `/property/autoMachine/updateData` | `/thermal/property/auto-machine` (PUT) | PUT | 🟡 SKELETON | 骨架已实现 |
| `/property/autoMachine/getSerialNum` | `/thermal/property/auto-machine/serial-num` | GET | 🟡 SKELETON | 骨架已实现 |
| `/property/autoMachine/getQrCode` | `/thermal/property/auto-machine/qr-heat` | POST | 🟡 TODO | 需支付集成 |
| `/property/autoMachine/getQrCodeWater` | `/thermal/property/auto-machine/qr-water` | POST | 🟡 TODO | 需支付集成 |
| `/property/autoMachine/getQrCodeEle` | `/thermal/property/auto-machine/qr-ele` | POST | 🟡 TODO | 需支付集成 |
| `/property/autoMachine/callback` | `/thermal/property/auto-machine/callback/wechat-heat` | POST | 🟡 TODO | 支付回调待实现 |
| `/property/autoMachine/aliCallBack` | `/thermal/property/auto-machine/callback/ali-heat` | POST | 🟡 TODO | 支付回调待实现 |
| `/property/autoMachine/queryPaymentSuccess` | `/thermal/property/auto-machine/payment-status` | GET | 🟡 SKELETON | 骨架已实现 |
| `/property/autoMachine/getRecordBySerialNum` | `/thermal/property/auto-machine/record` | GET | 🟡 SKELETON | 骨架已实现 |
| `/property/autoMachine/getIsReadCard` | `/thermal/property/auto-machine/read-card` | GET | 🟡 SKELETON | 骨架已实现 |
| `/property/autoMachine/getReaderParam` | `/thermal/agent/reader-param` | GET | ✅ MOVED | 已移至 AgReaderParamController |
| `/property/autoMachine/getClientVersion` | `/thermal/agent/auto-version/list` | GET | ✅ MOVED | 已移至 AgAutoVersionController |
| `/property/autoMachine/getClientDownload` | - | - | ❌ MISSING | OSS下载功能未迁移 |

**待完成功能**：
- 支付宝/微信支付集成
- 支付回调处理
- OSS 客户端下载功能
- 业务逻辑实现

---

## 数据库表结构

旧系统核心表（需确认新系统是否存在）：
- `sys_company`: 公司/代理商表
- `ag_auto_machine`: 自助机档案
- `ag_reader_param`: 读卡器参数
- `ag_auto_version`: 客户端版本

关联表：
- 代理商-物业关联
- 代理商-仪表分配
- 角色-菜单权限（代理商专用）

---

## 迁移建议

### 高优先级（必须迁移）

1. **确认代理商业务模型**
   - 确认新系统是否使用多租户（SysTenant）替代代理商（SysCompany）
   - 如果是，需评估旧系统代理商数据迁移方案
   - 如果否，需完整迁移 AgentCompanyController 及相关 Service

2. **自助机支付功能**
   - 完成 PrAutoMachineController 的支付端点实现
   - 集成微信支付/支付宝 SDK
   - 实现支付回调处理逻辑

### 中优先级（建议迁移）

3. **仪表分配功能**
   - 迁移 AgentMeterController 功能
   - 或整合到新的权限管理体系中

4. **OSS 客户端下载**
   - 迁移 getClientDownload 功能
   - 整合到新系统 OSS 模块

### 低优先级（可选）

5. **代码优化**
   - 统一使用 RESTful 风格
   - 添加完整的权限注解
   - 完善参数校验

---

## 风险评估

| 风险项 | 严重性 | 影响 | 缓解措施 |
|--------|--------|------|----------|
| 代理商核心功能缺失 | **高** | 无法管理代理商客户 | 确认业务模型，优先迁移或设计替代方案 |
| 支付功能未实现 | **高** | 自助机无法收费 | Phase 6 优先完成支付集成 |
| 数据表结构不明确 | **中** | 迁移可能遗漏数据 | 审核数据库表结构，确认映射关系 |

---

## 审核结论

**Agent 代理管理模块迁移状态：严重不完整**

- ✅ 已迁移：3 个辅助 Controller（AccessCode, AgReaderParam, AgAutoVersion）
- 🟡 骨架迁移：1 个 Controller（PrAutoMachine，支付功能待实现）
- ❌ 完全缺失：6 个核心 Controller（AgentCompany, AgentMeter, AgentProperty, AgentPropertyMenu, AgentRole, AgentUserController）

**建议行动**：
1. 立即与业务团队确认代理商管理在新系统中的定位
2. 如需保留代理商模式，启动核心 Controller 迁移
3. 优先完成自助机支付功能，确保自助机能正常使用
