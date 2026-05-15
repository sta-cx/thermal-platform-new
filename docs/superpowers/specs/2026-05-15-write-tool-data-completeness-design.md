# Write Tool 数据完整性范式

**日期**: 2026-05-15
**状态**: 已批准
**性质**: 长期开发范式（非单次改造）
**适用范围**: 所有涉及 WriteTool 的新增、修改、重构工作

## 问题

AI 通过 Tool Calling 创建/修改业务数据时，缺少手动操作流程中的数据完整性保障：

- 传统 UI 通过必填项校验、下拉框枚举、前端联动查询保证数据完整
- AI Tool 只有 `@ToolParam` 文本描述，LLM 未必收集所有必要字段
- 导致 AI 产出的记录字段缺失（缺楼宇/房号/编号/默认值等），数据质量随使用退化

**目标**: AI 产出的每条业务记录与手动创建完全等价（业务级完整性）。本范式作为所有 WriteTool 开发的统一准则。

---

## 范式核心：WriteTool 分类与职责分层

### WriteTool 分为两类

#### 类型 A：CRUD 类（创建/修改业务记录）

创建或修改业务实体的操作。例：创建报修工单、标记缴费、新增热户。

**必须**遵循三层职责分离：

```
┌─ Tool 层（薄层）─────────────────────────────────────┐
│  职责：                                               │
│  - 定义 LLM 可见的参数（@ToolParam + description）     │
│  - 调用 Service 的 xxxFromAi() 方法                    │
│  - 将结果转为 record 返回给 LLM                        │
│  禁止：                                               │
│  - 不注入 Mapper                                      │
│  - 不查数据库                                         │
│  - 不做字段补全、默认值设置、编号生成                   │
└──────────────────────────────────────────────────────┘
        │
        ▼
┌─ Service 层（补全层）─────────────────────────────────┐
│  职责：                                               │
│  - 查关联表补全字段                                    │
│  - 生成业务编号                                        │
│  - 设置业务默认值                                      │
│  - 校验关联数据存在性                                   │
│  - 保存                                               │
│  命名约定：createFromAi / updateFromAi / markXxxFromAi │
└──────────────────────────────────────────────────────┘
        │
        ▼
   Mapper / 数据库 — INSERT / UPDATE
```

#### 类型 B：Command 类（下发指令/执行动作）

不创建业务记录，而是触发一个动作。例：下发阀门控制指令、触发同步任务。

**遵循双职责分离**：

```
Tool 层：参数收集 + 委托 + 返回结果
Command Service 层：参数校验 + 动作执行 + 结果构建
```

Command 类不需要"数据补全"步骤，但仍需要：
- 关联数据存在性校验（如阀门是否存在）
- 参数合法性校验（如开度 0-100）
- 清晰的错误消息

### 为什么这样分

- CRUD 类的核心风险是**字段不完整**，需要 Service 层做关联查询和默认值补全
- Command 类的核心风险是**操作不合法**，需要 Service 层做校验和权限检查
- 混在一起会导致 CRUD 类的补全逻辑被忽略，或 Command 类被强行套入不合适的模式

---

## 信息完整性保障：四层防线

适用于所有 WriteTool（CRUD 类和 Command 类）。

### 第1层：Tool 描述引导

`@ToolParam` 的 `required` 和 `description` 引导 LLM 收集参数。description 中必须写明：
- 参数的含义和格式
- 不知道该参数时应该怎么办（引用读 Tool 或追问用户）

```java
@ToolParam(description = "房屋 ID,必填。如果用户不知道 ID,请先调用 queryHouseByAddress 查询")
Long houseId
```

**开发规范**：每个 `@ToolParam(required=false)` 都必须有正当理由，并在 description 中说明为什么可选、缺少时如何处理。

### 第2层：Read Tool 辅助定位

LLM 在调写 Tool 前，先用读 Tool 查关联数据，拿到准确 ID。

示例流程：用户说"3号楼201" → LLM 调 `queryHouseByRoom("3","201")` → 拿到 houseId → 再调 `createRepair(houseId, ...)`

现有只读 Tool 已注册在 ToolRegistry，不需要新增基础设施。新增 WriteTool 时确认对应的读 Tool 已存在；不存在时一并补充。

### 第3层：Service 层业务校验

`xxxFromAi()` 方法内部必须校验：
1. **关联数据存在性**：所有通过 ID 引用的实体必须查库确认存在
2. **字段合法性**：枚举值范围内、数值区间合法
3. **业务状态校验**：如已缴费不可重复标记、已关闭工单不可再派单

校验失败抛 `IllegalArgumentException`，消息必须是人类可读的。ToolExecutor 会捕获异常返回给 LLM，LLM 转述给用户。

```java
PrHouse house = houseMapper.selectById(houseId);
if (house == null) {
    throw new IllegalArgumentException("房屋ID " + houseId + " 不存在，请核实后重试");
}
```

**关键注意**：查关联表时要注意 `@TableField(exist = false)` 字段。例如 `PrHouse.orgName` 是非数据库字段，需要额外查组织表获取，不能直接 `house.getOrgName()`。

### 第4层：降级引导

system prompt 中约定：无法确认关键信息时，建议用户到业务页面手动操作。

当前 `assistantChatClient` 的 `defaultSystem`（`SdkjAiAutoConfiguration.java:86`）需要更新：
- 第3条从"只读"改为包含写操作说明
- 新增降级引导规则

---

## 现有 WriteTool 现状问题与改造

### CreateRepairTool（CRUD 类）

**现状问题**：
1. 缺少 `repairNo` 生成（Controller 路径调了 `generateRepairNo()`，Tool 路径漏了）
2. 缺少 buildingId/buildingName/unitCode/roomNum（只取了 orgId/orgName）
3. `PrHouse.orgName` 是 `@TableField(exist = false)`，当前 `house.getOrgName()` 返回 null
4. house 为 null 时 `orgId = "0"` 静默降级，不报错

**改造方案**：

| 层 | 改动 |
|------|------|
| Tool 层 | 移除 `PrHouseMapper` 注入；只调 `repairService.createFromAi(houseId, repairInfo, userName, userPhone)` |
| Service 层 `createFromAi()` | 1. 查 PrHouse 确认存在，不存在则抛异常<br>2. 从 PrHouse 带出 buildingId/buildingName/unitCode/roomNum/orgId<br>3. 查组织表获取 orgName（因 PrHouse.orgName 是非数据库字段）<br>4. 调 `generateRepairNo()` 生成编号<br>5. 设 repairType=0、urgentType=0、serviceType=0、repairTime=now、repairStatus=0<br>6. 保存 |

**涉及文件**：
- `CreateRepairTool.java` — 精简为薄层
- `IHtRepairService.java` — 新增 `createFromAi()` 签名
- `HtRepairServiceImpl.java` — 实现 `createFromAi()`

### MarkPaidTool（CRUD 类 — 状态变更）

**现状问题**：
1. 直接操作 Mapper，无状态校验（已缴费条目可被重复标记）
2. 补全逻辑在 Tool 内，不符合范式

**改造方案**：

| 层 | 改动 |
|------|------|
| Tool 层 | 移除 `PrExpenseMapper` 注入；只调 `expenseService.markPaidFromAi(expenseId, note)` |
| Service 层 `markPaidFromAi()` | 1. 查费用条目确认存在<br>2. 检查 isCharged 状态，已缴费则抛异常<br>3. 更新 isCharged=1 |

**涉及文件**：
- `MarkPaidTool.java` — 精简为薄层
- `IPrExpenseService.java` — 新增 `markPaidFromAi()` 签名
- `PrExpenseServiceImpl.java` — 实现 `markPaidFromAi()`

### DispatchValveCommandTool（Command 类）

**现状问题**：
1. 指令下发逻辑在 Tool 内，dryRun 判断和 IoT 占位实现混杂

**改造方案**：

此 Tool 属于 Command 类，不创建业务记录，不套用 `createFromAi` 模式。但仍需：
- 移除 `PrHeatValveArchiveMapper` 注入
- 在 `IPrHeatValveArchiveService` 中新增 `dispatchFromAi()` 方法
- Service 内校验阀门存在 + 参数合法性 + dryRun 判断

| 层 | 改动 |
|------|------|
| Tool 层 | 移除 `PrHeatValveArchiveMapper` 注入；只调 `valveArchiveService.dispatchFromAi(houseId, action, openness, dryRun)` |
| Service 层 `dispatchFromAi()` | 1. 查阀门确认存在<br>2. 校验 action 枚举合法（OPEN/CLOSE/SET_OPENNESS）<br>3. SET_OPENNESS 时校验 openness 在 0-100<br>4. dryRun 判断<br>5. 返回 ValveCommandResult |

**涉及文件**：
- `DispatchValveCommandTool.java` — 精简为薄层
- `IPrHeatValveArchiveService.java` — 新增 `dispatchFromAi()` 签名
- `PrHeatValveArchiveServiceImpl.java` — 实现 `dispatchFromAi()`

### assistantChatClient system prompt

**涉及文件**：`SdkjAiAutoConfiguration.java` 的 `assistantChatClient` 方法

**改动**：更新 `defaultSystem`，加入写操作引导和降级规则。

---

## 开发者清单

**每次新增或修改 WriteTool 时，逐项确认：**

- [ ] **分类**：确定是 CRUD 类还是 Command 类
- [ ] **Tool 层**：不注入 Mapper；只做参数收集 + 委托 Service + 返回 record
- [ ] **Service 层**：新增 `xxxFromAi()` 方法，签名只接受 LLM 可收集的参数
- [ ] **关联查询**：所有 ID 引用的实体都查库确认存在；注意 `@TableField(exist=false)` 字段需额外查表
- [ ] **默认值**：与手动创建路径的默认值保持一致
- [ ] **编号生成**：如果手动路径有编号生成（如 repairNo），FromAi 方法中也必须生成
- [ ] **业务校验**：状态前置检查、重复操作防护、枚举值范围校验
- [ ] **错误消息**：校验失败抛 `IllegalArgumentException`，消息人类可读
- [ ] **ToolParam description**：每个 required=true 参数说明格式；每个 required=false 参数说明可选原因和缺失时处理方式
- [ ] **读 Tool 对应**：确认 LLM 能通过读 Tool 定位到所需 ID，缺少时一并补充
