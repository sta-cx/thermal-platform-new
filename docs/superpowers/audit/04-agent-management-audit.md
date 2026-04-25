# Agent 代理商管理模块迁移审核报告

**审核日期**: 2026-04-25
**审核范围**: Agent 代理商管理模块从旧系统到新系统的迁移质量
**旧系统路径**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/`
**新系统路径**: `D:/chonggou/thermal-platform-new/sdkj-modules/`

---

## 一、迁移状态总表

| 旧系统 Controller | 功能说明 | 新系统状态 | 备注 |
|------------------|---------|-----------|------|
| AgentCompanyController | 代理商公司管理（树形结构） | **未迁移** | 使用 `sys_company.nature='1'` 区分代理商 |
| AgentUserController | 代理商员工管理 | **未迁移** | 使用 `sys_user` 表，通过 `company_id` 关联 |
| AgentRoleController | 代理商角色管理 | **未迁移** | 使用 `sys_role.nature='1'` 区分代理商角色 |
| AgentPropertyController | 物业公司管理（代理商旗下） | **未迁移** | 使用 `sys_company.nature='2'` 区分物业公司 |
| AgentPropertyMenuController | 物业公司菜单分配 | **未迁移** | 通过 `sys_role_menu` 关联物业公司超管角色 |
| AgentMeterController | 仪表分配管理 | **已迁移** | 迁移至 `sdkj-meter` 模块，见下方详细审核 |
| AccessCodeController | 仪表编码获取服务 | **未迁移** | 编码生成逻辑可能已整合至仪表模块 |
| AgAutoVersionController | 自助机版本管理 | **未迁移** | 使用 `ag_auto_version` 表 |
| AgReaderParamController | 自助机读卡器参数 | **未迁移** | 使用 `ag_reader_param` 表 |

**迁移状态汇总**:
- **完全未迁移**: 8/9 个 Controller
- **已迁移**: 1/9 个 Controller (AgentMeterController)

---

## 二、已迁移功能审核

### AgentMeterController（仪表分配管理）

**旧系统路径**: `com.thermal.controller.AgentMeterController`
**新系统路径**: `org.sdkj.meter.controller.AgentMeterController`

#### 2.1 API 兼容性审核

| 旧系统 API | 新系统 API | 兼容性 | 说明 |
|-----------|-----------|-------|------|
| `/agent/meter/meterList` | `GET /thermal/meter/agent/allocated` | 不兼容 | 路由完全重写，参数结构变化 |
| `/agent/meter/getList` | `GET /thermal/meter/agent/all` | 不兼容 | 路由完全重写 |
| `/agent/meter/insertData` | `POST /thermal/meter/agent/allocate` | 不兼容 | 路由和参数结构变化 |

**问题**: API 路由完全不兼容，前端需要全面改造。

#### 2.2 业务逻辑保真度

**核心功能对比**:

| 功能 | 旧系统实现 | 新系统实现 | 保真度 |
|------|----------|----------|-------|
| 已分配仪表分页查询 | `meterList()`，支持 01/02/03/04 类型 | `allocated()`，使用 switch 表达式 | 100% |
| 所有仪表列表查询 | `getList()`，按公司过滤 | `all()`，移除公司过滤（待确认） | 90% |
| 批量分配仪表 | `insertData()`，逐条插入 | `allocate()`，使用事务批量操作 | 增强 |

#### 2.3 代码质量

**改进点**:
1. 使用 `switch` 表达式替代 `if-else`，代码更简洁
2. 添加 `@SaCheckPermission` 权限校验注解
3. 使用 `@RequiredArgsConstructor` 替代 `@Autowired`
4. 返回统一的 `R` 和 `TableDataInfo` 响应格式
5. 添加 `@Log` 操作日志注解

**问题点**:
1. **缺少 search 参数的 trim 处理**（虽然 `archiveSearch` 方法内有处理）
2. **缺少公司 ID 过滤逻辑** - 新系统 `all()` 方法没有按 `companyId` 过滤，与旧系统逻辑不一致

#### 2.4 数据库表依赖

旧系统使用 `mt_meter_match` 表存储分配关系。新系统是否使用同一表需要确认。

---

## 三、未迁移功能清单

### 3.1 代理商公司管理（AgentCompanyController）

**功能点**:
- 左侧公司树查询（`pageList`）
- 公司详情查询（`getDataById`）
- 公司删除（`deleteData`）
- 手机号/编码/名称唯一性校验
- 代理商新增/修改（`insertData`/`updateData`）
- 启用/禁用（`startUsing`/`endUsing`）

**数据库表**: `sys_company`（通过 `nature='1'` 区分代理商）

**迁移建议**: 使用 RuoYi-Vue-Plus 的多租户功能替代，或创建专门的代理商管理模块。

### 3.2 代理商员工管理（AgentUserController）

**功能点**:
- 员工分页列表（`pageList`）
- 手机号唯一性校验（`checkTele`）
- 员工新增/修改/删除
- 启用/禁用
- 角色分配（`saveUserRole`）

**数据库表**: `sys_user`（通过 `company_id` 关联代理商）

**迁移建议**: 新系统的 `sdkj-system` 模块已有用户管理，需扩展代理商公司维度。

### 3.3 代理商角色管理（AgentRoleController）

**功能点**:
- 角色分页列表（`pageList`）
- 角色新增/修改/删除
- 角色权限分配（`permissionUpd`）
- 标识/名称唯一性校验
- 用户已分配/未分配角色查询

**数据库表**: `sys_role`（通过 `nature='1'` 区分代理商角色）

**迁移建议**: 新系统 `sdkj-system` 模块已有角色管理，需扩展代理商维度和菜单权限的按公司隔离。

### 3.4 物业公司管理（AgentPropertyController）

**功能点**:
- 物业公司列表（`pageList`）- 查询代理商旗下的物业公司
- 物业公司菜单分配（`queryAgMenuList`/`queryPrMenuList`/`menuInsertData`）
- 审核状态管理（`updatePrAudited`）
- 启用状态管理（`updatePrEnabled`）
- 物业公司初始设置（`queryPrCompany`/`updataPrCompany`）
- 自助机档案分配（`queryAutoMachine`/`updateAutoMachine`）
- 仪表分配（`queryMeter`/`updataMeter`）

**数据库表**: `sys_company`（通过 `nature='2'` 区分物业公司）

**迁移建议**: 这是代理商系统的核心功能，需要完整迁移。

### 3.5 物业公司菜单分配（AgentPropertyMenuController）

**功能点**:
- 查询物业公司已分配菜单（`findYes`）
- 查询物业公司未分配菜单（`findNo`）
- 保存菜单分配（`permissionUpd`）

**数据库表**: `sys_role_menu`（通过物业公司超管角色关联）

### 3.6 仪表编码获取（AccessCodeController）

**功能点**:
- 获取仪表厂商最新编码（`accessMtVendorCode`）
- 获取仪表分类最新编码（`accessMtSortCode`）
- 获取仪表档案编码（`accessMeterCode`）- 支持 01/02/03/04/11/21/31 类型

**迁移建议**: 编码生成逻辑应整合到 `sdkj-meter` 模块的服务层。

### 3.7 自助机版本管理（AgAutoVersionController）

**功能点**:
- 版本列表查询（`pageList`）
- 版本更新（`updateData`）

**数据库表**: `ag_auto_version`

### 3.8 自助机读卡器参数（AgReaderParamController）

**功能点**:
- 根据 CODE 获取读卡器参数（`getDataByCode`）

**数据库表**: `ag_reader_param`

---

## 四、问题汇总

### 4.1 架构设计问题

1. **多租户策略不一致**: 旧系统通过 `sys_company.nature` 字段区分代理商（nature='1'）和物业公司（nature='2'），新系统采用 RuoYi-Vue-Plus 的 `tenant_id` 多租户模式。两种策略不兼容，需要数据迁移方案。

2. **菜单权限隔离缺失**: 旧系统的代理商角色菜单（`nature='1'`）和物业公司菜单（`nature='2'`）是完全隔离的，新系统没有对应的机制。

3. **公司层级关系缺失**: 旧系统支持代理商 → 物业公司的层级关系（通过 `parent_id`），新系统的租户模式是平级的。

### 4.2 数据库表缺失

以下表在新系统中可能缺失或结构不兼容：
- `sys_company` - 新系统使用 `sys_tenant` 替代，但字段结构不同
- `ag_auto_version` - 自助机版本表
- `ag_reader_param` - 自助机读卡器参数表
- `ag_auto_machine` - 自助机档案表
- `mt_meter_match` - 仪表分配关系表（需确认）

### 4.3 代码质量建议

1. **AgentMeterController.all() 方法** - 缺少 `companyId` 参数过滤，与旧系统逻辑不一致
2. **缺少统一的权限标识符** - 旧系统使用 `ROLE_` 前缀，新系统未明确
3. **编码生成逻辑** - `AccessCodeController` 的编码生成逻辑需要迁移到服务层

### 4.4 遗留功能

以下功能需要完整实现：
1. 代理商公司管理（CRUD + 树形结构）
2. 代理商员工管理（含角色分配）
3. 代理商角色管理（含菜单权限）
4. 物业公司管理（代理商旗下物业公司）
5. 物业公司菜单分配
6. 自助机版本管理
7. 自助机读卡器参数管理
8. 仪表编码生成服务

---

## 五、迁移建议

### 5.1 短期建议

1. **创建 `sdkj-agent` 模块** - 专门管理代理商相关功能
2. **保留 `sys_company` 表** - 与 RuoYi 的 `sys_tenant` 并存，通过 `nature` 字段区分
3. **迁移核心 Controller** - 按优先级迁移：
   - P0: AgentCompanyController、AgentUserController
   - P1: AgentRoleController、AgentPropertyController
   - P2: AccessCodeController、AgAutoVersionController

### 5.2 长期建议

1. **评估多租户方案** - 决定使用旧系统的 `sys_company` 模式还是 RuoYi 的 `sys_tenant` 模式
2. **统一权限体系** - 将代理商权限、物业权限整合到 Sa-Token + 菜单权限体系
3. **API 版本控制** - 考虑使用 `/api/v1/` 前缀，便于未来升级

---

## 六、审核结论

**Agent 代理商管理模块整体迁移状态**: **严重不足**

- **已迁移**: 11%（1/9 个 Controller）
- **核心功能**: 完全缺失（公司管理、用户管理、角色管理、物业管理）
- **风险评估**: 高 - 代理商系统是供热平台的核心功能，缺失会影响多客户 SaaS 模式

**建议**: 在正式上线前，必须完成代理商核心模块的迁移工作。
