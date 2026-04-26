# 第三轮全量审核 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 对新老系统全部 ~534 个端点进行全量深度审核，产出 API 覆盖度清单 + Service/DB 层对比报告 + 最终汇总

**Architecture:** 两轮串行。第一轮 4 并行 Agent 扫描 API 端点覆盖度；主进程汇总后，第二轮 4 并行 Agent 深入 Service/DB 层逐端点对比业务逻辑。

**Tech Stack:** 文件读取 + Markdown 报告生成，无需运行代码

---

## File Structure

### 需要读取的旧系统文件

旧系统根目录: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/`

| Agent | 需读取的 Controller 文件 |
|-------|------------------------|
| Agent 1 | `controller/PrBuildingController.java`, `controller/PrUnitController.java`, `controller/PrHouseController.java`, `controller/PrFamilyController.java`, `controller/PrHeatStationController.java`, `controller/PrHeatStationPartitionController.java`, `controller/PrRegionalController.java`, `controller/PrStandardController.java`, `controller/PrOptionsController.java`, `controller/PrOptionsHeatController.java`, `controller/PrExpenseItemController.java`, `controller/PrUserController.java`, `controller/PrPetController.java`, `controller/PrHouseChangeController.java`, `controller/PrHouseExpenseController.java`, `controller/PrUseCardLogController.java`, `controller/PrHeatArchiveController.java`, `controller/PrHeatTempArchiveController.java`, `controller/PrHeatHotArchiveController.java`, `controller/PrHeatUnitHotArchiveController.java`, `controller/PrHeatValveArchiveController.java` (如存在), `controller/PrHeatCommandValveArchiveController.java`, `controller/PrHeatUnitValveArchiveController.java`, `controller/PrHeatCommandUnitValveArchiveController.java`, `controller/PrHeatDtuArchiveController.java`, `controller/PrHeatDailyController.java`, `controller/PrHeatReadingCopy1Controller.java` (注意命名), `controller/PrHeatMonthController.java`, `controller/PrHeatControlController.java`, `controller/PrAbnormalRecordController.java`, `controller/PrAutoMachineController.java` |
| Agent 2 | `controller/PrExpenseController.java`, `controller/PrAccountController.java`, `controller/PrTransactionRecordController.java`, `controller/PrBillingNotesController.java`, `controller/PrPrintTemplateController.java`, `controller/SingleChargeController.java` (如存在), `controller/ChargeDetailStateNameController.java`, `controller/ReconciliationController.java`, `controller/PrImportRecordController.java`, `controller/PrImportBasicDataController.java`, `controller/PrImportHeatController.java`, `controller/PrImportHeatTempController.java`, `controller/PrImportHistoryController.java`, `controller/PrImportUnitHeatController.java`, `controller/PrImportUnitValveController.java`, `controller/PrImportValveController.java`, `controller/PrImportAuthorizationCodeController.java`, `controller/PrWechatBindRecordController.java`, `controller/PrStrategyController.java`, `controller/PrNoticeController.java`, `controller/PrRepairRecordController.java`, `controller/PrRepairPersonController.java`, `controller/PrInspectionPersonController.java`, `controller/PrInspectionPlanController.java`, `controller/PrInspectionRecordController.java`, `controller/PrSchedulingController.java`, `controller/AgentCompanyController.java`, `controller/AgentPropertyController.java`, `controller/AgentPropertyMenuController.java`, `controller/AgentRoleController.java`, `controller/AgentUserController.java`, `controller/AgentMeterController.java` |
| Agent 3 | `controller/HtStrategyController.java`, `controller/HtHouseStrategyController.java`, `controller/HtInstructionController.java`, `controller/HtAlertController.java`, `controller/HtScopeController.java`, `controller/HtScopeDtuController.java`, `controller/HtTasksController.java`, `controller/HtTasksPerformController.java`, `controller/HtTasksPerformLsController.java`, `controller/HtStrategyPerformController.java`, `controller/HtRepairController.java`, `controller/MtElectricArchiveController.java`, `controller/MtWaterArchiveControlller.java` (注意旧系统拼写错误), `controller/MtGasArchiveController.java`, `controller/MtCentratorArchiveController.java`, `controller/MtTcArchiveController.java`, `controller/MtTcValveController.java`, `controller/MtFormulaFileController.java`, `controller/MtMeterVendorController.java`, `controller/MtMeterSortController.java`, `controller/MtHeatArchiveController.java` |
| Agent 4 | `controller/WechatAuthController.java`, `controller/WechatPayController.java`, `controller/WxPortalController.java`, `controller/WxMaUserController.java`, `controller/WxMaMediaController.java`, `controller/RepairController.java`, `controller/SysUserController.java`, `controller/SysCompanyController.java`, `controller/SysDictController.java`, `controller/SysColumnController.java`, `controller/SysHomeController.java`, `controller/MenuController.java`, `controller/RoleController.java`, `controller/PropertyMenuController.java`, `controller/UserController.java`, `controller/OssManagerController.java`, `controller/ToolsController.java`, `controller/PushController.java`, `controller/TaskController.java`, `controller/AreaController.java` |

### 需要读取的新系统文件

新系统根目录: `D:/chonggou/thermal-platform-new/`

**sdkj-thermal 模块 Controller:**
`sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/`

**sdkj-meter 模块 Controller:**
`sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/`

**sdkj-system 模块 Controller:**
`sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/controller/`

### 产出文件

| 文件 | 说明 |
|------|------|
| `docs/superpowers/audit-r3/round1-agent1-pr-basic-device.md` | Agent 1 第一轮报告 |
| `docs/superpowers/audit-r3/round1-agent2-pr-charge-agent.md` | Agent 2 第一轮报告 |
| `docs/superpowers/audit-r3/round1-agent3-ht-mt.md` | Agent 3 第一轮报告 |
| `docs/superpowers/audit-r3/round1-agent4-wechat-sys.md` | Agent 4 第一轮报告 |
| `docs/superpowers/audit-r3/round1-summary.md` | 第一轮汇总 |
| `docs/superpowers/audit-r3/round2-agent1-pr-basic-device.md` | Agent 1 第二轮报告 |
| `docs/superpowers/audit-r3/round2-agent2-pr-charge-agent.md` | Agent 2 第二轮报告 |
| `docs/superpowers/audit-r3/round2-agent3-ht-mt.md` | Agent 3 第二轮报告 |
| `docs/superpowers/audit-r3/round2-agent4-wechat-sys.md` | Agent 4 第二轮报告 |
| `docs/superpowers/audit-r3/final-summary.md` | 最终汇总报告 |

---

## 第一轮：API 覆盖度扫描（4 并行 Agent）

### Task 1: Round 1 — Agent 1 (Pr 基础数据 + Pr 设备档案)

**Files:**
- Read: 旧系统 31 个 Controller（见上方 Agent 1 列表）
- Read: 新系统对应 Controller（`sdkj-thermal/controller/` 下 Pr* 相关）
- Write: `docs/superpowers/audit-r3/round1-agent1-pr-basic-device.md`

- [ ] **Step 1: 提取旧系统端点清单**

读取旧系统 Pr 基础数据模块的 16 个 Controller:
- `PrBuildingController`, `PrUnitController`, `PrHouseController`, `PrFamilyController`
- `PrHeatStationController`, `PrHeatStationPartitionController`, `PrRegionalController`
- `PrStandardController`, `PrOptionsController`, `PrOptionsHeatController`
- `PrExpenseItemController`, `PrUserController`, `PrPetController`
- `PrHouseChangeController`, `PrHouseExpenseController`, `PrUseCardLogController`

以及 Pr 设备档案模块的 15 个 Controller:
- `PrHeatArchiveController`, `PrHeatTempArchiveController`, `PrHeatHotArchiveController`
- `PrHeatUnitHotArchiveController`, `PrHeatCommandValveArchiveController`
- `PrHeatUnitValveArchiveController`, `PrHeatCommandUnitValveArchiveController`
- `PrHeatDtuArchiveController`, `PrHeatDailyController`
- `PrHeatReadingCopy1Controller`, `PrHeatMonthController`
- `PrHeatControlController`, `PrAbnormalRecordController`, `PrAutoMachineController`
- `PrHeatValveArchiveController`（如存在）

对每个 Controller，提取所有 `@RequestMapping`/`@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping` 注解的路径和方法名。

- [ ] **Step 2: 提取新系统端点清单**

读取新系统 `sdkj-thermal/controller/` 下所有 Pr 前缀的 Controller:
- `PrBuildingController`, `PrUnitController`, `PrHouseController`, `PrFamilyController`
- `PrHeatStationController`, `PrHeatStationPartitionController`, `PrRegionalController`
- `PrStandardController`, `PrOptionsController`, `PrOptionsHeatController`
- `PrExpenseItemController`, `PrUserController`, `PrPetController`
- `PrHouseChangeController`, `PrHouseExpenseController`, `PrUseCardLogController`
- `PrHeatArchiveController`, `PrHeatTempArchiveController`, `PrHeatHotArchiveController`
- `PrHeatUnitHotArchiveController`, `PrHeatCommandValveArchiveController`
- `PrHeatUnitValveArchiveController`, `PrHeatCommandUnitValveArchiveController`
- `PrHeatDtuArchiveController`, `PrHeatDailyController`, `PrHeatReadingController`
- `PrHeatMonthController`, `PrHeatControlController`
- `PrAbnormalRecordController`, `PrAutoMachineController`
- `PrHeatValveArchiveController`

提取所有端点。

- [ ] **Step 3: 逐端点对比**

对旧系统每个端点，在新系统中查找语义等价的端点。分类为：
- **完全匹配**: 语义一致，参数完整
- **部分匹配**: 端点存在但参数简化/行为变更
- **骨架**: Controller 存在但 Service 方法体为空/返回占位数据
- **缺失**: 新系统中无对应端点
- **新增**: 新系统新增端点

- [ ] **Step 4: 写入报告**

产出 `docs/superpowers/audit-r3/round1-agent1-pr-basic-device.md`，格式：

```markdown
# Round 1 — Agent 1: Pr 基础数据 + Pr 设备档案

## 统计

| 模块 | 旧端点数 | 完全匹配 | 部分匹配 | 骨架 | 缺失 | 新增 |
|------|---------|---------|---------|------|------|------|
| Pr 基础数据 | X | X | X | X | X | X |
| Pr 设备档案 | X | X | X | X | X | X |

## 端点明细

### PrBuildingController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| ... | ... | ... | ... | ... | ... |

### PrUnitController
...

### Pr 设备档案模块

### PrHeatArchiveController
...
```

---

### Task 2: Round 1 — Agent 2 (Pr 收费运维 + Agent 代理管理)

**Files:**
- Read: 旧系统 32 个 Controller（见上方 Agent 2 列表）
- Read: 新系统对应 Controller
- Write: `docs/superpowers/audit-r3/round1-agent2-pr-charge-agent.md`

- [ ] **Step 1: 提取旧系统端点清单**

读取旧系统 Pr 收费运维模块的 Controller:
- `PrExpenseController`, `PrAccountController`, `PrTransactionRecordController`
- `PrBillingNotesController`, `PrPrintTemplateController`, `ChargeDetailStateNameController`
- `ReconciliationController`, `PrImportRecordController`, `PrImportBasicDataController`
- `PrImportHeatController`, `PrImportHeatTempController`, `PrImportHistoryController`
- `PrImportUnitHeatController`, `PrImportUnitValveController`, `PrImportValveController`
- `PrImportAuthorizationCodeController`, `PrWechatBindRecordController`
- `PrStrategyController`, `PrNoticeController`
- `PrRepairRecordController`, `PrRepairPersonController`
- `PrInspectionPersonController`, `PrInspectionPlanController`, `PrInspectionRecordController`
- `PrSchedulingController`

以及 Agent 代理管理模块:
- `AgentCompanyController`, `AgentPropertyController`, `AgentPropertyMenuController`
- `AgentRoleController`, `AgentUserController`, `AgentMeterController`

- [ ] **Step 2: 提取新系统端点清单**

读取新系统 `sdkj-thermal/controller/` 下对应 Controller:
- 收费运维: `PrExpenseController`, `PrAccountController`, `PrTransactionRecordController`, `PrBillingNotesController`, `PrPrintTemplateController`, `SingleChargeController`, `ReconciliationController`
- 导入: `PrImportRecordController`, `PrImportBasicDataController`, `PrImportHeatController`, `PrImportHeatTempController`, `PrImportHistoryController`, `PrImportUnitHeatController`, `PrImportUnitValveController`, `PrImportValveController`, `PrImportAuthorizationCodeController`
- 运维: `PrWechatBindRecordController`, `PrStrategyController`, `PrNoticeController`, `PrRepairRecordController`, `PrRepairPersonController`, `PrInspectionPersonController`, `PrInspectionPlanController`, `PrInspectionRecordController`, `PrSchedulingController`
- Agent: `AgCompanyController`, `AgPropertyController`, `AgPropertyMenuController`, `AgRoleController`, `AgUserController`, `AgentMeterController`(位于 sdkj-meter)

- [ ] **Step 3: 逐端点对比**

同 Task 1 Step 3 的分类标准。

- [ ] **Step 4: 写入报告**

产出 `docs/superpowers/audit-r3/round1-agent2-pr-charge-agent.md`，同 Task 1 的报告格式，包含统计表 + 端点明细表。

---

### Task 3: Round 1 — Agent 3 (Ht 热力调控 + Mt 仪表设备)

**Files:**
- Read: 旧系统 21 个 Controller（见上方 Agent 3 列表）
- Read: 新系统对应 Controller
- Write: `docs/superpowers/audit-r3/round1-agent3-ht-mt.md`

- [ ] **Step 1: 提取旧系统端点清单**

读取旧系统 Ht 热力调控模块:
- `HtStrategyController`, `HtHouseStrategyController`, `HtInstructionController`
- `HtAlertController`, `HtScopeController`, `HtScopeDtuController`
- `HtTasksController`, `HtTasksPerformController`, `HtTasksPerformLsController`
- `HtStrategyPerformController`, `HtRepairController`

以及 Mt 仪表设备模块:
- `MtElectricArchiveController`, `MtWaterArchiveControlller` (注意旧系统拼写)
- `MtGasArchiveController`, `MtCentratorArchiveController`
- `MtTcArchiveController`, `MtTcValveController`, `MtFormulaFileController`
- `MtMeterVendorController`, `MtMeterSortController`, `MtHeatArchiveController`

- [ ] **Step 2: 提取新系统端点清单**

读取新系统 `sdkj-thermal/controller/` 下 Ht 前缀的 Controller:
- `HtStrategyController`, `HtHouseStrategyController`, `HtInstructionController`
- `HtAlertController`, `HtScopeController`, `HtTasksController`
- `HtTasksPerformController`, `HtStrategyPerformController`, `HtRepairController`

以及 `sdkj-meter/controller/` 下 Mt 前缀的 Controller:
- `MtElectricArchiveController`, `MtWaterArchiveController`
- `MtGasArchiveController`, `MtCentratorArchiveController`
- `MtTcArchiveController`, `MtTcValveController`, `MtFormulaFileController`
- `MtMeterVendorController`, `MtMeterSortController`, `MtHeatArchiveController`
- `AgentMeterController`

- [ ] **Step 3: 逐端点对比**

同 Task 1 Step 3。特别检查 `HtScopeDtuController` 和 `HtTasksPerformLsController` 是否在新系统存在。

- [ ] **Step 4: 写入报告**

产出 `docs/superpowers/audit-r3/round1-agent3-ht-mt.md`，同 Task 1 的报告格式。

---

### Task 4: Round 1 — Agent 4 (微信支付 + 系统管理)

**Files:**
- Read: 旧系统 20 个 Controller（见上方 Agent 4 列表）
- Read: 新系统对应 Controller
- Write: `docs/superpowers/audit-r3/round1-agent4-wechat-sys.md`

- [ ] **Step 1: 提取旧系统端点清单**

读取旧系统微信支付模块:
- `WechatAuthController`, `WechatPayController`, `WxPortalController`
- `WxMaUserController`, `WxMaMediaController`, `RepairController`

以及系统管理模块:
- `SysUserController`, `SysCompanyController`, `SysDictController`
- `SysColumnController`, `SysHomeController`, `MenuController`
- `RoleController`, `PropertyMenuController`, `UserController`
- `OssManagerController`, `ToolsController`, `PushController`
- `TaskController`, `AreaController`

- [ ] **Step 2: 提取新系统端点清单**

读取新系统 `sdkj-thermal/controller/` 下微信相关:
- `WechatAuthController`, `WechatPayController`, `WxPortalController`
- `WxMaUserController`, `WxMaMediaController`, `WechatRepairController`
- `AccessCodeController`, `IoTCallbackController`

以及 `sdkj-system/controller/` 和 `sdkj-thermal/controller/` 下系统相关:
- 新系统的系统管理 Controller 在 `sdkj-system` 模块中，需搜索确认
- `AreaController`

- [ ] **Step 3: 逐端点对比**

同 Task 1 Step 3。特别注意系统管理端点可能从 `sdkj-thermal` 迁移到了 `sdkj-system` 模块。

- [ ] **Step 4: 写入报告**

产出 `docs/superpowers/audit-r3/round1-agent4-wechat-sys.md`，同 Task 1 的报告格式。

---

### Task 5: Round 1 汇总

**Files:**
- Read: `docs/superpowers/audit-r3/round1-agent1-pr-basic-device.md`
- Read: `docs/superpowers/audit-r3/round1-agent2-pr-charge-agent.md`
- Read: `docs/superpowers/audit-r3/round1-agent3-ht-mt.md`
- Read: `docs/superpowers/audit-r3/round1-agent4-wechat-sys.md`
- Write: `docs/superpowers/audit-r3/round1-summary.md`

- [ ] **Step 1: 合并统计**

汇总 4 份报告的统计数据，生成按模块的完成度百分比。

- [ ] **Step 2: 生成热力图**

为 8 个模块生成文本热力图（类似前两轮格式）。

- [ ] **Step 3: 标注第二轮重点**

标注"部分匹配"和"缺失"端点数量最多的模块，指导第二轮深入方向。

- [ ] **Step 4: 写入汇总报告**

产出 `docs/superpowers/audit-r3/round1-summary.md`。

---

## 第二轮：深度 Service/DB 验证（4 并行 Agent）

### Task 6: Round 2 — Agent 1 (Pr 基础数据 + Pr 设备档案)

**Files:**
- Read: `docs/superpowers/audit-r3/round1-agent1-pr-basic-device.md`（第一轮结果）
- Read: 旧系统 `service/impl/` 下对应的 Service 实现
- Read: 旧系统 `entity/` 下对应的 Entity
- Read: 旧系统 `mapper/` 下对应的 Mapper XML
- Read: 新系统 `service/impl/` 下对应的 Service 实现
- Read: 新系统 `domain/` 下对应的 Entity
- Read: 新系统 `mapper/` XML
- Write: `docs/superpowers/audit-r3/round2-agent1-pr-basic-device.md`

- [ ] **Step 1: 读取第一轮结果**

读取 Round 1 报告，识别所有"部分匹配"和"骨架"端点，这些是深入审查的重点。"完全匹配"端点做抽样检查。

- [ ] **Step 2: Service 层对比**

对每个关键端点：
1. 读取旧系统 Service 实现，提取核心逻辑（条件分支、计算公式、数据转换）
2. 读取新系统 Service 实现
3. 标注逻辑差异，特别关注:
   - PrHouse 的导入导出逻辑
   - PrHeatArchive 的 manualControl 指令处理
   - PrHeatValveArchive 的批量操作
   - PrHeatDaily/PrHeatMonth 的统计计算

- [ ] **Step 3: DB 层对比**

对每个模块：
1. 对比旧 Entity 与新 Domain 的字段列表，标注遗漏/新增字段
2. 对比 Mapper XML 中的关键查询（特别是列表查询、统计查询）
3. 检查动态 SQL 条件是否等价

- [ ] **Step 4: 风险标注**

标注以下风险:
- 事务安全: 收费/退费操作是否有 `@Transactional`
- 参数校验: 边界条件是否处理
- 数据一致性: 多表操作是否有事务保护
- 安全: 是否有 SQL 拼接或未鉴权端点

- [ ] **Step 5: 写入报告**

产出 `docs/superpowers/audit-r3/round2-agent1-pr-basic-device.md`，格式：

```markdown
# Round 2 — Agent 1: Pr 基础数据 + Pr 设备档案（深度审核）

## 一、Service 层对比

### PrHouseController
| 旧端点 | Service 差异 | 严重度 | 说明 |
|--------|-------------|--------|------|
| ... | ... | P0/P1/P2 | ... |

### PrHeatArchiveController
...

## 二、DB 层对比

### PrHouse（旧 Entity vs 新 Domain）
| 旧字段 | 新字段 | 状态 | 备注 |
|--------|--------|------|------|
| ... | ... | 匹配/遗漏/类型变更 | ... |

### 关键 Mapper 查询等价性
...

## 三、风险项
...

## 四、行动建议（按优先级）
...
```

---

### Task 7: Round 2 — Agent 2 (Pr 收费运维 + Agent 代理管理)

**Files:**
- Read: 第一轮结果 + 旧系统和新系统 Service/Entity/Mapper
- Write: `docs/superpowers/audit-r3/round2-agent2-pr-charge-agent.md`

- [ ] **Step 1: 读取第一轮结果**

读取 Round 1 报告，识别重点审查端点。

- [ ] **Step 2: Service 层对比**

重点模块:
- PrExpense / SingleCharge / Reconciliation: 收费计算逻辑、退费流程
- PrAccount: 账户余额操作
- PrTransactionRecord: 交易记录创建逻辑
- Agent 模块: 代理权限、角色菜单分配
- PrImport 系列: 导入解析逻辑

- [ ] **Step 3: DB 层对比**

- [ ] **Step 4: 风险标注**

特别关注收费/退费的事务安全。

- [ ] **Step 5: 写入报告**

产出 `docs/superpowers/audit-r3/round2-agent2-pr-charge-agent.md`，同 Task 6 报告格式。

---

### Task 8: Round 2 — Agent 3 (Ht 热力调控 + Mt 仪表设备)

**Files:**
- Read: 第一轮结果 + 旧系统和新系统 Service/Entity/Mapper
- Write: `docs/superpowers/audit-r3/round2-agent3-ht-mt.md`

- [ ] **Step 1: 读取第一轮结果**

- [ ] **Step 2: Service 层对比**

重点模块:
- HtStrategy: 策略生成/下发逻辑
- HtInstruction: 指令下发协议
- HtTasks: 任务调度逻辑
- Mt 各仪表: 数据采集/解析逻辑

- [ ] **Step 3: DB 层对比**

- [ ] **Step 4: 风险标注**

- [ ] **Step 5: 写入报告**

产出 `docs/superpowers/audit-r3/round2-agent3-ht-mt.md`，同 Task 6 报告格式。

---

### Task 9: Round 2 — Agent 4 (微信支付 + 系统管理)

**Files:**
- Read: 第一轮结果 + 旧系统和新系统 Service/Entity/Mapper
- Write: `docs/superpowers/audit-r3/round2-agent4-wechat-sys.md`

- [ ] **Step 1: 读取第一轮结果**

- [ ] **Step 2: Service 层对比**

重点模块:
- WechatPay: 支付创建/回调/退款逻辑
- WechatAuth: 微信授权流程
- SysUser/SysCompany: 用户/公司管理
- 系统管理模块是否由 sdkj-system 基座功能替代

- [ ] **Step 3: DB 层对比**

- [ ] **Step 4: 风险标注**

特别关注支付签名验证、回调幂等性。

- [ ] **Step 5: 写入报告**

产出 `docs/superpowers/audit-r3/round2-agent4-wechat-sys.md`，同 Task 6 报告格式。

---

### Task 10: 最终汇总报告

**Files:**
- Read: 所有 8 份 round2 报告 + round1-summary
- Write: `docs/superpowers/audit-r3/final-summary.md`

- [ ] **Step 1: 合并第二轮统计**

汇总 4 份第二轮报告的统计和发现。

- [ ] **Step 2: 生成迁移完成度热力图**

按模块统计最终完成度（结合 API 覆盖度 + Service 逻辑等价度 + DB 字段完整度）。

- [ ] **Step 3: 按优先级排列行动项**

将所有发现按 P0（阻塞上线）/ P1（功能完整）/ P2（中期优化）分类。

- [ ] **Step 4: 代码质量总评**

对比新老系统架构改进点和残留风险。

- [ ] **Step 5: 写入最终报告**

产出 `docs/superpowers/audit-r3/final-summary.md`，格式：

```markdown
# 第三轮全量审核 — 最终汇总报告

**审核日期**: 2026-04-26
**审核范围**: 全量 ~534 端点，深度 API + Service + DB 三级审核

## 一、整体迁移状态

| 模块 | 旧端点数 | 完全匹配 | 部分匹配 | 骨架 | 缺失 | 新增 |
|------|---------|---------|---------|------|------|------|
| ... | ... | ... | ... | ... | ... | ... |

## 二、Service 层逻辑等价度
...

## 三、DB 层字段完整度
...

## 四、迁移完成度热力图
...

## 五、按优先级排列的行动项

### P0 — 阻塞上线
### P1 — 功能完整性
### P2 — 中期优化

## 六、代码质量总评
...

## 七、详细报告索引
...
```
