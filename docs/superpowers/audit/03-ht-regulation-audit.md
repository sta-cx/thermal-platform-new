# Ht 热力调控模块迁移审核报告

## 审核概述

**审核日期**: 2026-04-25
**审核范围**: Ht 热力调控模块（调控策略、指令下发、任务调度、报警、报修、范围管理）
**旧系统路径**: D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/
**新系统路径**: D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/

---

## 一、迁移状态总表

| 模块/功能 | 旧系统 Controller | 新系统 Controller | 状态 | 备注 |
|----------|------------------|------------------|------|------|
| **策略管理** | HtStrategyController | HtStrategyController | 已迁移 | 含子表 HtStrategySub |
| **指令管理** | HtInstructionController | HtInstructionController | 已迁移 | 完整 CRUD |
| **任务管理** | HtTasksController | HtTasksController | 部分迁移 | 缺少部分复杂查询功能 |
| **任务执行记录** | HtTasksPerformController | HtTasksPerformController | 部分迁移 | 功能简化 |
| **报警管理** | HtAlertController | HtAlertController | 已迁移 | 完整功能 |
| **报修管理** | HtRepairController | HtRepairController | 已迁移 | 完整功能 |
| **范围管理** | HtScopeController | HtScopeController | 已迁移 | 基础功能 |
| **房屋策略绑定** | HtHouseStrategyController | HtHouseStrategyController | 已迁移 | 完整功能 |
| **策略执行顺序** | HtStrategyPerformController | - | 未迁移 | 表不存在 |
| **DTU 范围管理** | HtScopeDtuController | - | 未迁移 | 表不存在 |
| **任务执行临时表** | HtTasksPerformLsController | - | 未迁移 | 表不存在 |

---

## 二、已迁移功能审核

### 2.1 HtStrategyController（控制策略管理）

**API 兼容性**: 良好
- 旧端点: `/htStrategy/*` → 新端点: `/thermal/ht/strategy/*`
- HTTP 方法规范化: POST → RESTful（GET/POST/PUT/DELETE）
- 参数校验: 新增 `@Validated` + Bo 对象

**业务逻辑保真**:
- ✅ 主子表联合保存逻辑一致
- ✅ 删除前引用检查逻辑保留
- ✅ 子表级联删除逻辑正确

**代码质量**:
- ✅ 使用 `@Transactional` 保证事务完整性
- ✅ 使用 Sa-Token 进行权限控制
- ✅ 使用统一的 `TableDataInfo` 分页响应

### 2.2 HtInstructionController（控制指令管理）

**API 兼容性**: 良好
- 旧端点: `/htInstruction/*` → 新端点: `/thermal/ht/instruction/*`
- RESTful 规范化

**业务逻辑保真**:
- ✅ 删除前校验策略子表引用关系
- ✅ 查询所有指令接口保留

**代码质量**:
- ✅ 统一异常处理
- ✅ 权限注解完整

### 2.3 HtTasksController（调控任务管理）

**API 兼容性**: 中等
- 旧端点: `/property/htTasks/*` → 新端点: `/thermal/ht/tasks/*`
- 基础 CRUD 完整，但部分复杂查询端点未迁移

**业务逻辑保真**:
- ✅ 任务状态校验（运行中不允许修改/删除）
- ✅ Quartz 调度集成（`ThermalJobManager`）
- ✅ 特殊户/特殊单元标记功能
- ✅ 缴费状态设置
- ✅ 平衡率刷新
- ✅ 设定开度保存
- ⚠️ 缺少以下功能：
  - `pageDeviceList` 设备类型分页查询
  - `querySummary` 汇总统计（部分实现）
  - `getHouseOtherCode` / `updateOtherCode` 其他代码管理
  - `updateSdkdLog` 重新设定开度日志

**代码质量**:
- ✅ 事务管理完整
- ✅ 调度器集成良好
- ⚠️ 部分复杂业务逻辑待补充

### 2.4 HtTasksPerformController（调控执行记录）

**API 兼容性**: 中等
- 旧端点: `/htTasksPerform/*` → 新端点: `/thermal/ht/tasksPerform/*`
- 功能简化，仅保留核心查询

**业务逻辑保真**:
- ✅ 按仪表ID查询执行记录
- ✅ 更新指令发送状态
- ⚠️ 缺少大量旧系统功能：
  - 复杂的档案更新逻辑（`updateValveArchive`、`updateUnitValveArchive` 等）
  - DTU 状态更新
  - 报警插入
  - 回读数据处理
  - 批量更新操作

**代码质量**:
- ⚠️ 功能简化较多，可能影响生产环境完整运行

### 2.5 HtAlertController（报警管理）

**API 兼容性**: 良好
- 旧端点: `/htAlert/*` → 新端点: `/thermal/ht/alert/*`

**业务逻辑保真**:
- ✅ 分页查询完整
- ✅ 异常报警列表查询
- ✅ 类型统计
- ✅ 批量插入报警记录

**代码质量**: 良好

### 2.6 HtRepairController（报修管理）

**API 兼容性**: 良好
- 旧端点: `/htRepair/*` → 新端点: `/thermal/ht/repair/*`

**业务逻辑保真**:
- ✅ 报修编号生成（改为 UUID 格式）
- ✅ 派单功能
- ✅ 状态更新
- ✅ 逻辑删除
- ✅ 类型统计
- ✅ 房间查询

**代码质量**: 良好

### 2.7 HtScopeController（范围管理）

**API 兼容性**: 基础功能保留
- 旧端点: `/htScope/*` → 新端点: `/thermal/ht/scope/*`

**业务逻辑保真**:
- ✅ 按任务ID获取房屋列表

**代码质量**: 良好

### 2.8 HtHouseStrategyController（房屋策略绑定）

**API 兼容性**: 良好
- 旧端点: `/property/houseStrategy/*` → 新端点: `/thermal/ht/houseStrategy/*`

**业务逻辑保真**:
- ✅ 批量保存
- ✅ 批量修改
- ✅ 批量删除
- ✅ 分页查询

**代码质量**: 良好

---

## 三、未迁移功能清单

### 3.1 完全未迁移的模块

#### HtStrategyPerformController（策略执行顺序表）
- **表名**: `ht_strategy_perform`
- **用途**: 存储控制任务的命令执行顺序，支持策略的循环执行、时间间隔控制
- **字段**: `command_index`, `strategy_id`, `instruction_id`, `intervall`, `xunhuan` 等
- **影响**: 无法实现复杂的多步骤策略调度
- **建议**: 补充表结构和相关逻辑

#### HtScopeDtuController（DTU 控制范围表）
- **表名**: `ht_scope_dtu`
- **用途**: 管理基于 DTU（集中器）的控制范围，支持通道号集合控制
- **字段**: `dtu_num`, `chan_nums`, `concentrator_code`, `status` 等
- **影响**: 无法基于 DTU 进行批量设备控制
- **建议**: 根据业务需求确认是否需要迁移

#### HtTasksPerformLsController（任务执行临时表）
- **表名**: `ht_tasks_perform_ls`
- **用途**: 临时存储任务执行数据，用于生成正式执行任务
- **影响**: 可能影响任务生成的中间过程
- **建议**: 确认是否被其他逻辑替代

### 3.2 HtTasksPerformMapper 缺失的功能

旧系统中 `HtTasksPerformMapper` 包含大量设备档案更新和回读数据处理方法，新系统未迁移：

**档案更新方法**:
- `updateValveArchive` / `updateValveArchiveBatch` - 阀门档案更新
- `updateUnitValveArchive` - 单元阀门档案更新
- `updateTempArchive` - 温度档案更新
- `updateHotArchive` / `updateUnitHotArchive` - 热力档案更新
- `updateDtu` / `updateDtuHot` - DTU 状态更新

**回读数据处理**:
- `insertReading` - 插入回读数据
- `insertYTReading` - 插入云台回读数据
- `insertTempReading` - 插入温度回读数据
- `insertHotReading` - 插入热力回读数据

**房屋/单元更新**:
- `updateValveHouse` / `updateValveUnit` - 更新房屋/单元阀门状态
- `updateTempHouse` / `updateHotHouse` - 更新房屋温度/热力数据
- `updateDtuArchive` - 更新 DTU 档案

**影响**: 这些是核心的设备状态同步功能，缺失会导致设备状态无法正确更新。

### 3.3 HtTasksController 缺失的功能

- `pageDeviceList` - 按设备类型分页查询阀门配表列表
- `querySummary` - 汇总统计（部分实现）
- `getHouseOtherCode` / `updateOtherCode` - 其他代码管理
- `updateSdkdLog` - 重新设定开度日志

---

## 四、问题汇总

### Critical（必须修复）

#### C-1: HtTasksPerformMapper 核心功能缺失
**位置**: `HtTasksPerformMapper.java`
**问题**: 新系统仅保留基础查询方法，缺失所有设备档案更新和回读数据处理方法
**影响**: 设备状态无法同步，调控指令无法正确执行
**建议**: 补充以下关键方法：
- `updateValveArchive` - 阀门状态更新
- `insertReading` - 回读数据插入
- `updateDtu` - DTU 状态更新
- `updateValveHouse` - 房屋阀门状态更新

#### C-2: ht_strategy_perform 表缺失
**位置**: 数据库表结构
**问题**: 策略执行顺序表不存在，无法支持复杂的多步骤策略调度
**影响**: 无法实现循环执行、时间间隔控制等高级策略功能
**建议**: 
1. 创建 `ht_strategy_perform` 表
2. 创建对应的 Entity、Mapper、Service
3. 实现策略执行顺序管理逻辑

#### C-3: ht_scope_dtu 表缺失
**位置**: 数据库表结构
**问题**: DTU 控制范围表不存在，无法基于 DTU 进行批量设备控制
**影响**: DTU 相关功能无法使用
**建议**: 根据业务需求确认是否需要迁移此表及相关逻辑

### Important（建议修复）

#### I-1: HtTasksController 功能不完整
**位置**: `HtTasksController.java`
**问题**: 缺少部分复杂查询和管理功能
**建议**: 补充以下接口：
- `GET /thermal/ht/tasks/deviceList` - 设备类型分页查询
- `GET /thermal/ht/tasks/summary` - 汇总统计
- `GET /thermal/ht/tasks/otherCode/{id}` - 其他代码查询
- `PUT /thermal/ht/tasks/otherCode` - 其他代码更新
- `PUT /thermal/ht/tasks/valveAngleLog` - 重新设定开度日志

#### I-2: 报修编号生成逻辑变更
**位置**: `HtRepairController.java:78`
**问题**: 旧系统使用 `GengerateRecordNoYMD()` 生成格式化编号，新系统使用 UUID
**建议**: 统一编号生成规则，确保兼容性

#### I-3: HtTasksPerformController 功能简化过多
**位置**: `HtTasksPerformController.java`
**问题**: 仅保留基础查询，缺少状态统计、批量操作等功能
**建议**: 根据业务需求补充必要的执行记录管理功能

### Minor（可选优化）

#### M-1: API 路径不一致
**位置**: 各 Controller
**问题**: 部分端点路径变化较大（如 `/property/htTasks` → `/thermal/ht/tasks`）
**建议**: 更新 API 文档，或考虑提供兼容层

#### M-2: 权限标识字符串
**位置**: 各 Controller `@SaCheckPermission` 注解
**问题**: 权限标识使用新格式（如 `thermal:ht:strategy:list`），需确保权限配置同步
**建议**: 核对权限配置，确保前端权限控制正确

#### M-3: 分页参数差异
**位置**: 各 Controller
**问题**: 旧系统使用 MyBatis-Plus `Page` 对象，新系统使用自定义 `PageQuery`
**建议**: 确保前端调用参数兼容

---

## 五、总体评估

### 迁移完成度: 约 65%

**已迁移**:
- ✅ 基础 CRUD 功能完整
- ✅ 策略管理、指令管理核心功能
- ✅ 报警、报修管理完整
- ✅ Quartz 调度基础集成

**未迁移/部分迁移**:
- ⚠️ 设备状态同步核心逻辑缺失（Critical）
- ⚠️ 复杂策略执行顺序管理缺失（Critical）
- ⚠️ DTU 控制功能缺失（Critical）
- ⚠️ 任务管理部分高级功能缺失（Important）

### 建议

1. **立即处理**: 补充 `HtTasksPerformMapper` 中的设备状态更新方法，否则系统无法正常运行
2. **高优先级**: 创建 `ht_strategy_perform` 表及相关逻辑，支持复杂策略调度
3. **中优先级**: 补充 `HtTasksController` 缺失的管理接口
4. **评估确认**: 确认 `ht_scope_dtu` 表是否需要迁移

### 风险提示

当前迁移状态**不建议直接用于生产环境**，至少需要补充 Critical 级别的问题才能确保基本功能可用。特别是设备状态同步逻辑的缺失，会导致调控指令无法正确执行。
