# Write Tool 数据完整性保障设计

**日期**: 2026-05-15
**状态**: 已批准
**模块**: sdkj-ai + sdkj-thermal

## 问题

AI 通过 Tool Calling 创建业务数据时，缺少手动创建流程中的数据补全机制：
- 传统 UI 通过必填项、下拉框、前端联动保证数据完整
- AI Tool 只有 `@ToolParam` 文本描述，LLM 未必收集所有必要字段
- 导致 AI 创建的记录字段不完整（缺楼宇/房号/编号/默认值等），数据质量退化

**目标**: AI 创建的记录与手动创建完全等价（业务级完整性）。

## 架构决策：方案 C — Service 层补全

WriteTool 分三层职责：

```
WriteTool（薄层）
  - 定义 LLM 可见的参数（@ToolParam）
  - 调用 Service 的 createFromAi 方法
  - 将结果转为 record 返回给 LLM
  - 不注入 Mapper，不查数据库，不做字段补全

Service.createFromXxx（补全层）
  - 查关联表补全字段（房屋→楼宇/单元/房号/小区）
  - 生成业务编号（repairNo 等）
  - 设置业务默认值（repairType/urgentType 等）
  - 校验关联数据存在性
  - 保存

Mapper / 数据库
  - INSERT
```

**为什么不选其他方案**：
- A（Tool 自己补全）：补全逻辑和手动路径独立，时间久必然漂移不一致
- B（复用 Controller）：现有 Controller 没做数据补全（那是前端职责），且 Controller 有权限/日志拦截器冲突

## 四层信息完整性防线

### 第1层：Tool 描述引导

`@ToolParam` 的 `required` 和 `description` 引导 LLM 收集参数。在 description 中写明"不知道时怎么办"。

```java
@ToolParam(description = "房屋 ID,必填。如果用户不知道 ID,请先调用 queryHouseByAddress 查询")
Long houseId
```

### 第2层：Read Tool 辅助定位

LLM 在调写 Tool 前，先用读 Tool 查关联数据，拿到准确 ID。

示例流程：用户说"3号楼201" → LLM 调 `queryHouseByRoom("3","201")` → 拿到 houseId → 再调 `createRepair(houseId, ...)`

现有只读 Tool 已注册在 ToolRegistry，不需要新增基础设施。

### 第3层：Service 层业务校验

`createFromAi()` 内部校验关联数据存在性和字段合法性。校验失败抛清晰异常，由 ToolExecutor 捕获后返回给 LLM，LLM 转述给用户。

```java
PrHouse house = houseMapper.selectById(houseId);
if (house == null) throw new IllegalArgumentException("房屋ID " + houseId + " 不存在，请核实后重试");
```

### 第4层：降级引导

system prompt 中约定：无法确认关键信息时，建议用户到业务页面手动操作。

## 现有 WriteTool 改造计划

### CreateRepairTool

| 项目 | 改动 |
|------|------|
| Tool 层 | 移除 PrHouseMapper 注入；只调 `repairService.createFromAi(houseId, repairInfo, userName, userPhone)` |
| Service 层 | `IHtRepairService.createFromAi()` 新增：查 pr_house 带出 buildingId/buildingName/unitCode/roomNum/orgId/orgName；调 `generateRepairNo()` 生成编号；设 repairType=0、urgentType=0、serviceType=0、repairTime=now、repairStatus=0 |

### MarkPaidTool

| 项目 | 改动 |
|------|------|
| Tool 层 | 移除 PrExpenseMapper 注入；只调 `expenseService.markPaidFromAi(expenseId, note)` |
| Service 层 | 校验费用条目存在性 + 状态检查（已缴费不可重复标记） |

### DispatchValveCommandTool

| 项目 | 改动 |
|------|------|
| Tool 层 | 移除 PrHeatValveArchiveMapper 注入；只调 valve Service 的 dispatchFromAi |
| Service 层 | 校验阀门存在；dryRun 判断集中管理 |

## 开发者规范

未来新增写操作 Tool 时遵循：

1. **Service 层**：新增 `createFromAi` / `updateFromAi` 方法，签名只接受 LLM 可收集的参数；内部做查关联→补全→编号→默认值→校验→保存
2. **Tool 层**：不注入 Mapper；只做参数收集 + 调 Service + 返回 record；`@ToolParam` description 写明"不知道时怎么办"
3. **Tool description**：引用读 Tool 辅助定位

## 涉及文件

| 文件 | 改动 |
|------|------|
| `IHtRepairService` / `HtRepairServiceImpl` | 新增 `createFromAi()` |
| `IPrExpenseService` / `PrExpenseServiceImpl` | 新增 `markPaidFromAi()` |
| 阀门相关 Service | 新增 `dispatchFromAi()` |
| `CreateRepairTool` | 精简为薄层 |
| `MarkPaidTool` | 精简为薄层 |
| `DispatchValveCommandTool` | 精简为薄层 |
| `SdkjAiAutoConfiguration` 中 assistantChatClient defaultSystem | 增加降级引导规则 |
