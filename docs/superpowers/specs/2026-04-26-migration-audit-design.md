# 新老系统迁移全面审核方案

## 背景

旧系统 thermal-balance-backend（Spring Boot 2.2, 单模块, ~103 Controller）已迁移至新系统 thermal-platform-new（Spring Boot 3.5, 多模块, ~107 Controller）。需要组建 Agent 团队对全部模块进行逐 Controller 精细对比审核。

## 审核范围

全部 6 大业务领域，共计 ~103 个旧系统 Controller 与 ~107 个新系统 Controller：

| 领域 | 前缀 | 旧系统路径 | 新系统路径 |
|------|------|-----------|-----------|
| 物业/供热管理 | Pr | controller/Pr* | sdkj-thermal/org/sdkj/thermal/controller/ |
| 供热控制策略 | Ht | controller/Ht* | sdkj-thermal/org/sdkj/thermal/controller/ |
| 仪表设备 | Mt | controller/Mt* | sdkj-meter/org/sdkj/meter/controller/ |
| 代理管理 | Agent | controller/Agent* | sdkj-thermal/org/sdkj/thermal/controller/ (Ag*) |
| 微信/支付 | Wechat | controller/Wechat*,Wx* | sdkj-thermal/org/sdkj/thermal/controller/ |
| 系统管理 | Sys | controller/Sys*,Role*,Menu*,User*,Area* | sdkj-system + sdkj-admin |

## 审核维度

每个 Controller 逐一检查以下三项：

### 1. API 覆盖度
- 旧系统每个 @RequestMapping/@GetMapping/@PostMapping/@PutMapping/@DeleteMapping 端点是否在新系统有对应实现
- HTTP 方法是否一致（GET/POST/PUT/DELETE）
- 请求路径是否对应（允许路径变更但需记录）
- 请求参数（@RequestParam/@PathVariable/@RequestBody）是否完整迁移

### 2. 业务逻辑一致性
- 旧系统 Service 层核心方法在新系统 ServiceImpl 中是否有对应实现
- 关键业务流程是否完整保留（如：收费计算、阀门控制、策略执行）
- 数据查询逻辑是否等价（SQL/JOIN/分页方式）
- 异常处理和边界情况是否覆盖

### 3. 代码质量
- 安全性：是否有 SQL 注入、XSS 等风险
- 事务管理：@Transactional 使用是否正确
- 异常处理：是否有吞异常、空 catch 等
- MyBatis-Plus 用法：BaseMapperPlus 模式是否正确
- Sa-Token 权限注解是否到位

## Agent 团队编排

### 约束
- 模型供应商最多 3 个并发 API 调用
- 每批最多 3 个 Agent 并行

### Batch 1：Pr 物业/供热管理（拆为 3 个子 Agent）

| Agent 名称 | 负责范围 | 旧系统 Controller 数 |
|------------|---------|---------------------|
| pr-basic | Pr-基础数据：PrBuilding, PrUnit, PrHouse, PrHouseChange, PrFamily, PrRegional, PrHeatStation, PrHeatStationPartition, Area, PrCompany, PrUser, PrRole, PrOptions, PrOptionsHeat, PrPet, PrHeatArchive, PrUserController, SysColumnController, PrStrategy, PrPrintTemplate | ~20 |
| pr-device | Pr-设备档案：PrHeatHotArchive, PrHeatTempArchive, PrHeatUnitHotArchive, PrHeatValveArchive, PrHeatUnitValveArchive, PrHeatCommandValveArchive, PrHeatCommandUnitValveArchive, PrHeatDtuArchive, PrHeatControl, PrHeatDaily, PrHeatReading, PrHeatMonth, PrAutoMachine, PrHeatArchive, PrAbnormalRecord | ~15 |
| pr-charge | Pr-收费运维：PrExpense, PrExpenseItem, PrHouseExpense, PrStandard, PrBillingNotes, PrTransactionRecord, PrAccount, PrUseCardLog, SingleCharge, Reconciliation, PrInspectionPerson, PrInspectionPlan, PrInspectionRecord, PrRepairPerson, PrRepairRecord, PrScheduling, PrNotice, PrImport*, PrWechatBindRecord, PushController, TaskController, ToolsController, ChargeDetailStateNameController | ~25 |

### Batch 2：Ht + Mt + Agent

| Agent 名称 | 负责范围 | 旧系统 Controller 数 |
|------------|---------|---------------------|
| ht-review | Ht*: HtStrategy, HtHouseStrategy, HtStrategyPerform, HtInstruction, HtAlert, HtScope, HtScopeDtu, HtTasks, HtTasksPerform, HtTasksPerformLs, HtRepair | ~11 |
| mt-review | Mt*: MtMeterVendor, MtMeterSort, MtElectricArchive, MtWaterArchive, MtGasArchive, MtHeatArchive, MtCentratorArchive, MtTcArchive, MtTcValve, MtFormulaFile | ~10 |
| agent-review | Agent*: AgentCompany, AgentMeter, AgentProperty, AgentPropertyMenu, AgentRole, AgentUser | ~6 |

### Batch 3：Wechat + Sys

| Agent 名称 | 负责范围 | 旧系统 Controller 数 |
|------------|---------|---------------------|
| wechat-review | WechatAuth, WechatPay, Reconciliation(微信侧), RepairController(微信端), WxPortal, WxMaUser, WxMaMedia | ~7 |
| sys-review | SysCompany, SysDict, SysHome, RoleController, MenuController, PropertyMenuController, UserController, AreaController, OssManager, AccessCode, SaOAuth2Server, AgAutoVersion, AgReaderParam | ~13 |

## 输出格式

每个 Agent 输出标准化的审核报告，包含：

```markdown
# [领域名称] 迁移审核报告

## 审核概览
- 旧系统 Controller 数：X
- 新系统对应 Controller 数：Y
- 完全匹配：Z
- 部分匹配：W
- 缺失：V

## 逐 Controller 对比

### [ControllerName]

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /xxx | GET /xxx | MATCH | - |
| POST /yyy | - | MISSING | 新系统未实现 |

#### 业务逻辑差异
- [差异描述]

#### 代码质量问题
- [问题描述]

## 总结与建议
- [关键发现]
- [缺失功能清单]
- [代码质量风险]
```

## 执行计划

1. Batch 1 启动（3 Agent 并行）→ 等待完成
2. Batch 2 启动（3 Agent 并行）→ 等待完成
3. Batch 3 启动（2 Agent 并行）→ 等待完成
4. 汇总所有 Agent 报告，生成整体迁移状态总览
