# Write Tool 操作完整性范式

**版本**: v2.0
**日期**: 2026-05-15
**状态**: 已批准
**性质**: 长期开发范式（**所有 LLM Tool Calling 写操作的统一准则**，非单次改造任务）
**适用范围**: `@Tool` + `@WriteTool` 标注的所有方法；未来通过 MCP / 其他 Agent 框架接入的写操作同样适用
**Owner**: sdkj-ai 模块维护者（待指定，提交记录见 git log）
**修订记录**:
- v1.0 2026-05-15 初稿
- v2.0 2026-05-15 审核后修订：补全事务、数据源、可重入性、审计字段、错误消息、二次确认预览、Command 类逻辑必填等硬约束；范式与首批改造拆分；强化开发者清单与评审清单

---

## 0. 目的与适用边界

### 0.1 目的

让 **AI 通过 Tool Calling 写入的每一条业务记录、下发的每一条指令** 与人在 UI 上手动操作完全等价：字段完整、状态合法、有审计可追、可二次确认、可回滚追责。

### 0.2 必须遵循本范式的场景

- 新增任何标注 `@Tool` 的方法（无论是否标 `@WriteTool`，都需先按本范式判定是否属于"写"）
- 修改现有 WriteTool 的参数、签名、行为
- 重构涉及 `IXxxService.xxxFromAi()` / `xxxFromAgent()` 类方法
- 新增 LLM 可调用的 MCP Tool、未来 Agent 接入入口

### 0.3 不适用本范式的场景

- 仅供用户通过页面操作的 Controller → Service 链路（保持现状）
- 内部定时任务、批量导入（除非这些任务会被 LLM 触发）
- 只读 Tool（`@WriteTool` 不标 或 risk=LOW 的纯查询）—— 只读 Tool 自有约定，本范式不涵盖

### 0.4 与既有规范的关系

- 服从 `D:\chonggou\thermal-platform-new\CLAUDE.md` 的分层架构（Bo/Entity/Vo + BaseMapperPlus）
- 服从 `D:\chonggou\CLAUDE.md` 的多租户独立库模式与组织权限规则
- 与 `docs/superpowers/specs/` 下其他 AI 相关 spec 互补；如有冲突以本文件为准并提 issue 修订其他文件

---

## 1. 问题背景

传统 UI 通过 **必填项校验、下拉框枚举、前端联动查询、表单默认值** 保障数据完整性。AI Tool 失去这层保障：

- `@ToolParam` 只有文本 description，LLM 收集参数依赖自然语言理解，不强制
- LLM 会"补脑"它不知道的字段（猜测、留空、传 null）
- 没有前端联动，LLM 不知道某个 houseId 对应的 buildingId/orgId
- 没有按钮约束，LLM 可能对已完成的业务重复操作

不解决这些问题的后果：**AI 产出的记录字段缺失、状态不一致、审计断链；随使用量增加，数据库被污染到无法回滚**。

---

## 2. 范式核心：WriteTool 分类与职责分层

### 2.1 两种 WriteTool

#### 类型 A：CRUD 类（创建/修改业务记录）

创建或修改业务实体的写操作。例：创建报修工单、标记缴费、新增热户、修改房屋面积。

**特征**：
- 操作后数据库新增或修改一条业务记录
- 关心"字段是否完整"、"状态是否合法"、"是否与手动路径等价"
- 必须有审计字段（createBy/updateBy/createTime/updateTime）

#### 类型 B：Command 类（下发指令/执行动作）

不创建业务记录而是触发一个动作。例：下发阀门控制指令、触发同步任务、推送通知。

**特征**：
- 操作目标是外部系统（IoT 设备、第三方 API、消息队列）
- 关心"动作是否合法"、"目标是否存在"、"参数是否有效"
- 失败需要可追溯（即使不写业务表，也需写指令记录表或日志）

#### 判定流程图

```
Tool 的核心副作用是什么?
  ├─ 在数据库 INSERT / UPDATE 业务表 → CRUD 类
  ├─ 调用外部系统（IoT/MQ/HTTP） → Command 类
  └─ 同时存在两者 → 拆分为两个 Tool；不允许复合
```

### 2.2 CRUD 类职责分层（三层）

```
┌─ Tool 层（薄层）─────────────────────────────────────┐
│  职责：                                              │
│  - 定义 LLM 可见的参数（@ToolParam + description）   │
│  - 调用 Service 的 xxxFromAi() 方法                  │
│  - 将结果转为 record 返回给 LLM                      │
│  禁止：                                              │
│  - 不注入 Mapper                                     │
│  - 不查数据库                                        │
│  - 不做字段补全、默认值设置、编号生成                │
│  - 不直接 new Entity 然后 setXxx()                   │
└──────────────────────────────────────────────────────┘
        │
        ▼
┌─ Service 层（补全 + 校验层）─────────────────────────┐
│  职责：                                              │
│  - 校验所有 ID 引用的关联实体存在                    │
│  - 查关联表补全字段（含 @TableField(exist=false)）   │
│  - 生成业务编号（带并发保护）                        │
│  - 设置业务默认值（与 Controller 路径一致）          │
│  - 业务状态前置校验                                  │
│  - 显式 setCreateBy/setUpdateBy（应对 reactive 线程  │
│    InjectionMetaObjectHandler 兜底为 -1 的问题）     │
│  - 保存（@Transactional）                            │
│  命名约定：createFromAi / updateFromAi / markXxxFromAi │
└──────────────────────────────────────────────────────┘
        │
        ▼
   Mapper / 数据库 — INSERT / UPDATE
```

### 2.3 Command 类职责分层（两层）

```
┌─ Tool 层（薄层）─────────────────────────────────────┐
│  职责：                                              │
│  - 定义参数、调用 Service 的 dispatchXxxFromAi()     │
│  - 透传 dryRun / 权限等运行时参数（由 ToolExecutor   │
│    覆盖；Tool 内不再判断）                           │
│  禁止：                                              │
│  - 不注入 Mapper                                     │
│  - 不做 dryRun 分支                                  │
│  - 不直接调 IoT/外部 API                             │
└──────────────────────────────────────────────────────┘
        │
        ▼
┌─ Command Service 层（校验 + 执行层）─────────────────┐
│  职责：                                              │
│  - 校验目标存在性（如阀门是否存在）                  │
│  - 校验参数合法性（如 action 枚举、openness 区间）   │
│  - 校验逻辑必填（条件必填字段）                      │
│  - 执行动作（IoT 下发 / API 调用）                   │
│  - 失败时写指令记录表（含错误堆栈）                  │
│  - 返回结构化结果                                    │
│  命名约定：dispatchFromAi / triggerFromAi /          │
│           sendFromAi                                 │
└──────────────────────────────────────────────────────┘
```

### 2.4 为什么这样分

| 关注点 | CRUD 类 | Command 类 |
|--------|---------|------------|
| 核心风险 | 字段不完整 | 操作不合法 |
| 主要防护手段 | 关联查询、默认值补全 | 参数校验、权限检查 |
| 数据落表 | 业务表 | 指令记录表 / 日志 |
| 幂等性 | 状态前置校验 | 重复指令防抖 |
| 失败处理 | 整事务回滚 | 失败需补偿或登记 |

强制分类避免：CRUD 类的补全逻辑被误省略；Command 类被套入不必要的 `createFromAi` 模式。

---

## 3. 信息完整性保障：四层防线

适用于所有 WriteTool（CRUD 与 Command）。

### 3.1 第 1 层：Tool 描述引导

`@Tool` 与 `@ToolParam` 的 description 决定 LLM 是否能正确收集参数。

**强制要求**：
- 每个 `@Tool` 必须说明"典型用途"（什么场景下调用）
- 每个 `@ToolParam(required=true)` description 必须包含：
  1. 参数含义与格式
  2. 不知道该参数时如何获取（**点名引用某个只读 Tool 或追问用户**）
- 每个 `@ToolParam(required=false)` description 必须包含：
  1. 为什么可选
  2. 缺失时的默认行为
  3. 何时**逻辑必填**（如"当 action=SET_OPENNESS 时本参数必填"）

**示例（合规）**：

```java
@Tool(description = """
    给指定热户创建一条报修工单。
    典型用途:客服在与用户对话时,用户口述报修原因后调用本 Tool 录入。
    如果用户只给了门牌号(如"3 号楼 201"),先调用 queryHouseByAddress 查到 houseId 再调用本 Tool。
    """)
public CreatedRepair create(
    @ToolParam(description = "房屋 ID,必填。如果用户只提供地址,请先调用 queryHouseByAddress 查询")
    Long houseId,

    @ToolParam(description = "报修描述,用户原话或概括")
    String repairInfo,

    @ToolParam(description = "联系人姓名,可选。不传时取房屋户主姓名;户主姓名也缺失时不填",
               required = false)
    String userName,

    @ToolParam(description = "联系电话,可选。格式 1[3-9]xxxxxxxxx;不传时取户主电话",
               required = false)
    String userPhone
) { ... }
```

**反例（违规）**：

```java
@ToolParam(description = "房屋 ID") Long houseId,           // 缺少"不知道时怎么办"
@ToolParam(description = "动作", required = false) action,  // 缺少格式、枚举值、可选原因
```

### 3.2 第 2 层：Read Tool 辅助定位

LLM 在调写 Tool 前必须能通过读 Tool 拿到 ID。

**规则**：
- 新增 WriteTool 前，必须确认所有 ID 类参数都有对应的 readonly Tool 能查
- 不存在时**先补 readonly Tool**，再写 WriteTool
- 读 Tool 命名 `queryXxxByYyy` / `getXxxByYyy`，挂在 `org.sdkj.thermal.ai.tools.readonly` 包下
- 在 system prompt 与 Tool description 中明确点名引用关系

**示例链路**：

```
用户："3 号楼 201 的阀门关一下"
LLM 步骤:
1. queryHouseByAddress(building="3", room="201") → houseId=12345
2. getValveStatus(houseId=12345) → 确认阀门存在、当前状态
3. dispatchValveCommand(houseId=12345, action="CLOSE") → 二次确认 → 执行
```

### 3.3 第 3 层：Service 层业务校验

`xxxFromAi()` 内部必须校验以下三类：

**3.3.1 关联数据存在性**

所有通过 ID 引用的实体必须查库确认。失败时抛 `IllegalArgumentException`，消息人类可读。

```java
PrHouse house = houseMapper.selectById(houseId);
if (house == null) {
    throw new IllegalArgumentException("房屋 ID " + houseId + " 不存在，请核实后重试");
}
```

**关键提醒**：`@TableField(exist = false)` 字段不会被 `selectById` 填充。例如 `PrHouse.orgName`、`PrHouse.userName` 都是查询投影字段，需要额外查 `sys_organization`、户主表等填充。**不要直接 `house.getOrgName()`，必为 null**。

**3.3.2 字段合法性**

- 枚举值必须在白名单内（不区分大小写需在 description 与校验中保持一致）
- 数值区间合法（如开度 0-100）
- 字符串长度、格式（电话 `1[3-9]\d{9}`、金额精度等）

**3.3.3 业务状态前置校验**

- 重复操作防护（已缴费、已关闭、已派单等）
- 时序依赖（未确认订单不能退费等）
- 跨实体一致性（房屋必须属于当前租户、当前组织权限范围内）

**重要**：业务状态校验的语义需要业务方明确。例如"已缴费的费用是否允许重新标记缴费"——可能因业务场景而异。**范式要求**：每次决定校验语义时，必须在 Service 方法的 javadoc 中说明判断依据。

### 3.4 第 4 层：System Prompt 降级引导

`assistantChatClient` 的 `defaultSystem`（`SdkjAiAutoConfiguration.java:78-102`）必须包含写操作降级规则。

**目标新 system prompt（最小可用版本，落地时按需调整）**：

```text
你是 SDKJ 智慧供热平台的助手。回答与执行操作时遵循以下规则:

【信息来源】
1. 优先使用"参考资料"中的事实;参考资料里没有的内容不要编造
2. 涉及数据库实际数据时,必须调用提供的 readonly Tool 查询,不要靠记忆回答

【写操作规则(创建/修改/下发指令)】
3. 调用写 Tool 前,先确保所有 ID 类参数都已通过 readonly Tool 确认;
   不要凭对话上下文猜测 ID
4. 若关键参数无法确认(如用户只说了"3 号楼"但拿不到 buildingId),
   告诉用户:"无法定位到具体房屋,请到[业务页面名]手动操作,
   或提供更精确的信息(如完整地址、热户编号)"
5. 写 Tool 返回错误时,把错误消息直接转述给用户,不要重试同样的参数

【行为约束】
6. 不回答与供热平台无关的话题
7. 一次回答中最多调用一个写 Tool;多个写操作分轮次执行,每次都让用户确认
8. 对敏感写操作(阀门控制、单笔退费),即使用户表达了"全部""所有""批量"等意图,
   也仅对**已明确指定**的目标执行一次,不要扩大范围
```

实施时把这段写入 `SdkjAiAutoConfiguration.assistantChatClient` 的 `defaultSystem`，覆盖现有第 3 条"只读"限制。

---

## 4. 实现约束（硬性规则，违反即拒绝合入）

以下规则不可妥协，无论 Tool 类型、Service 实现风格如何变化。

### 4.1 数据源与线程模型

WriteTool 当前有两条执行路径，数据源切换方式不同：

| 路径 | 触发场景 | 线程类型 | 数据源切换 |
|------|---------|----------|-----------|
| Dispatcher | LOW Tool（只读）即时执行 | reactive | 手动 `DynamicDataSourceContextHolder.push("tenant_" + tenantId)` |
| `/ai/tool-calls/{callId}/confirm` | MEDIUM/HIGH WriteTool 用户确认后 | HTTP 同步 | TenantFilter 自动切换（`/ai/*` 已纳入） |

**Service 层 `xxxFromAi()` 必须满足**：
- 假设数据源已切到 `tenant_{tenantId}`，**不在方法内手动 push 数据源**
- 不能携带数据源切换的副作用（避免影响调用方 ThreadLocal）
- 跨数据源场景（极少见，如需查 master 库系统表）必须显式 `@DS("master")` 标注 Mapper 方法

**新增 WriteTool 时必须确认**：调用路径是 `/ai/tool-calls/{callId}/confirm`，TenantFilter 已覆盖。如果未来出现绕过 TenantFilter 的调用入口（如内部消息驱动），必须在该入口手动切数据源后再进 Service。

### 4.2 事务边界

**强制规则**：
- `xxxFromAi()` 默认加 `@Transactional(rollbackFor = Exception.class)`
- 校验阶段抛出的 `IllegalArgumentException` 也必须触发回滚（默认 RuntimeException 已触发，但显式 rollbackFor 防御后期改动）
- 跨数据源操作（极少）必须明确事务策略，禁止默认依赖 `@Transactional` 跨 DS 生效

**编号生成并发安全**：
- `MAX(repairNo) + 1` 这类基于查询的编号生成在并发下会撞号
- 短期方案：编号生成必须在事务内、且加业务级互斥（Redis 锁 `LOCK:repairNo:{date}` 或数据库唯一索引兜底）
- 长期方案：用数据库序列、Snowflake 派生号、或 Redis INCR

### 4.3 Service 方法可重入性

`xxxFromAi()` 是 Service 公共方法，理论上能被任何 Bean 调用。**风险**：批量导入或定时任务直接调用，绕过 ToolExecutor 的权限校验和 Confirmation。

**强制规则（任选其一）**：

1. **包级访问 + 调用方校验**（推荐）：
   ```java
   public XxxResult createFromAi(...) {
       // 显式拒绝来自 HTTP Controller 的直接调用
       // 仅允许来自 ToolExecutor 或受信任的 Bean
       SecurityContext ctx = AiCallContext.current();
       if (ctx == null || !ctx.isFromTool()) {
           throw new IllegalStateException("createFromAi 只能由 Tool 层调用");
       }
       ...
   }
   ```

2. **抽出内部方法**：
   ```java
   public XxxResult createFromAi(...) { return doCreate(...); }
   protected XxxResult doCreate(...) { ... }  // 内部复用
   ```
   并约定 `xxxFromAi` 不被其他 Service 调用。

**简化方案**：项目初期，先在 javadoc 中明确"仅 Tool 层使用"，并在代码评审时检查。等出现第二个调用方时再升级为方案 1。

### 4.4 审计字段填充

`InjectionMetaObjectHandler` 在 `LoginHelper.getLoginUser()` 取不到上下文时，会兜底填 `-1`（`InjectionMetaObjectHandler.java:53-58`）。

- HTTP 同步线程（`/ai/tool-calls/confirm` 路径）：LoginHelper 有上下文 → 正常填用户 ID
- Reactive 线程（Dispatcher 路径）：LoginHelper 可能丢失 → 填 -1

**强制规则**：
- WriteTool 的 Service 方法**显式设置** createBy/updateBy（不依赖自动注入兜底）：
  ```java
  Long userId = AiCallContext.currentUserId();  // 从 PendingToolCall 透传
  if (userId == null) {
      throw new IllegalStateException("AI 调用上下文丢失 userId");
  }
  entity.setCreateBy(userId);
  entity.setUpdateBy(userId);
  ```
- 现阶段若未实现 `AiCallContext` 透传，至少在 Service 方法签名中接受 `userId` 参数，由 Tool 层从 Spring AI 的 ToolContext 或者 ToolExecutor 注入

### 4.5 错误消息规范

错误消息会被 LLM 转述给最终用户，需要兼顾"足够说明问题"与"不泄露敏感信息"。

**允许在错误消息中暴露**：
- 当前租户内的业务 ID（houseId、expenseId、repairId 等）
- 业务字段名（中文）
- 枚举合法值
- 操作建议（"请到 X 页面手动操作"）

**禁止在错误消息中暴露**：
- 跨租户的 ID（理论上租户隔离不会出现，但防御性禁止）
- 用户姓名、电话、身份证（即使是当前操作的用户）
- 数据库表名、字段名、SQL 错误
- 内部堆栈、异常类全限定名

**示例**：

```java
// 合规
throw new IllegalArgumentException("房屋 ID 12345 不存在，请确认后重试");
throw new IllegalArgumentException("费用条目 67890 已是缴费状态，无需重复标记");
throw new IllegalArgumentException("action 必须是 OPEN/CLOSE/SET_OPENNESS 之一，当前值: " + action);

// 违规
throw new IllegalArgumentException("查询 pr_house 表失败: " + e.getMessage());  // 暴露表名
throw new IllegalArgumentException("张三的房屋不存在");                            // 暴露姓名
```

### 4.6 二次确认预览

`PendingToolCall` 持久化时 `arguments` 是原始 JSON，对用户不友好（`houseId=12345` 看不懂）。

**强制规则**：
- 每个 WriteTool 必须提供 `previewSummary()` 方法（或 Tool record 中包含 `summary` 字段），生成人类可读的操作预览
- 预览必须包含：操作类型、目标对象（人话描述）、关键参数
- 预览生成失败时降级为 JSON，不阻断流程

**示例**：

```java
public record CreatedRepair(
    Long repairId,
    Long houseId,
    String summary,        // "为 3 号楼 1 单元 201 室创建报修工单：暖气不热"
    String status
) {}
```

二次确认弹窗优先展示 `summary`，原始 JSON 折叠在"详细参数"里。

---

## 5. 编码规范

### 5.1 命名约定

| 角色 | 命名 | 示例 |
|------|------|------|
| Tool 类 | `<动作><对象>Tool` | `CreateRepairTool` / `DispatchValveCommandTool` |
| Tool 方法 | 动词 | `create` / `markPaid` / `dispatch` |
| Service 方法 | `<动作>FromAi` | `createFromAi` / `markPaidFromAi` / `dispatchFromAi` |
| Tool 返回 record | `<过去分词><对象>` | `CreatedRepair` / `MarkedPayment` / `ValveCommandResult` |
| 包路径 | `<module>.ai.tools.<readonly\|write>` | `org.sdkj.thermal.ai.tools.write` |

### 5.2 `@ToolParam` description 模板

```
<参数含义>，<格式约束>。<必填/可选 + 缺失行为>。[如何获取]
```

例：
- ✅ `"房屋 ID,必填。如果用户只提供地址,请先调用 queryHouseByAddress 查询"`
- ✅ `"开度 0-100。仅 action=SET_OPENNESS 时必填,其他 action 忽略"`
- ❌ `"房屋 ID"`（缺格式约束、缺缺失行为）

### 5.3 Service 方法签名规范

```java
/**
 * 由 AI Tool 层调用创建报修工单。
 *
 * <p>校验规则：
 * <ul>
 *   <li>houseId 必须存在</li>
 *   <li>repairInfo 不能为空</li>
 * </ul>
 *
 * <p>补全字段：buildingId/buildingName/unitCode/roomNum/orgId/orgName/repairNo
 * 与 {@link HtRepairController#insert} 路径保持一致。
 *
 * @param houseId    房屋 ID
 * @param repairInfo 报修描述
 * @param userName   联系人姓名（可空，取户主）
 * @param userPhone  联系电话（可空，取户主）
 * @param operatorId 操作者用户 ID（由 Tool 层从 ToolContext 注入）
 * @return 已创建的报修工单标识
 * @throws IllegalArgumentException 校验失败
 */
@Transactional(rollbackFor = Exception.class)
public CreatedRepair createFromAi(
    Long houseId,
    String repairInfo,
    String userName,
    String userPhone,
    Long operatorId
);
```

### 5.4 异常类型选择

| 场景 | 异常类型 | LLM 行为 |
|------|---------|---------|
| 参数缺失/格式错 | `IllegalArgumentException` | LLM 看到后向用户追问 |
| 关联数据不存在 | `IllegalArgumentException` | LLM 转述给用户 |
| 业务状态不合法（重复缴费等） | `IllegalArgumentException` | LLM 转述给用户 |
| 权限不足 | `SecurityException` | ToolExecutor 已统一处理为"权限不足" |
| 系统错误（IoT 不可达、DB 超时） | 自定义 `ToolExecutionException` | ToolExecutor 包装为通用错误 |

**禁止**：抛 `RuntimeException` / `Exception` / `Throwable` 等通用异常，会丢失语义。

---

## 6. 开发者清单

**每次新增或修改 WriteTool 时，逐项确认。任何"否"必须在 PR 描述中说明理由。**

### 6.1 分类与结构

- [ ] **分类**：明确属于 CRUD 类还是 Command 类，写入 Tool javadoc
- [ ] **Tool 层是薄层**：不注入 Mapper、不查 DB、不做字段补全
- [ ] **Service 层方法命名**：`xxxFromAi`，签名只接受 LLM 可收集的参数 + 必要的 operatorId

### 6.2 参数与描述

- [ ] **每个 `@ToolParam(required=true)`** description 包含"如何获取"
- [ ] **每个 `@ToolParam(required=false)`** description 说明"为什么可选 + 缺失行为 + 是否逻辑必填"
- [ ] **Tool 描述**包含"典型用途"与读 Tool 引用关系
- [ ] **对应 readonly Tool 存在**：所有 ID 类参数都能被现有 readonly Tool 查到；不存在时一并补充

### 6.3 数据完整性（CRUD 类必查）

- [ ] **所有 ID 引用关联实体查库**：不存在则抛 `IllegalArgumentException`
- [ ] **注意 `@TableField(exist=false)` 字段**：不能直接取，需额外查表
- [ ] **默认值与 Controller 路径对照**：列出 Controller 路径的所有默认设置项，逐一对照
- [ ] **业务编号生成**：手动路径若有（如 `repairNo`），FromAi 必须生成；编号生成在事务内且并发安全

### 6.4 操作合法性（Command 类必查）

- [ ] **目标存在性**：所操作的设备/对象必须查库确认
- [ ] **参数枚举**：白名单校验，大小写敏感性与 description 一致
- [ ] **数值区间**：边界值（0/100/最大值）测试
- [ ] **逻辑必填**：条件必填字段在条件成立时校验
- [ ] **失败追溯**：失败时落记录表或日志，含错误堆栈

### 6.5 安全与一致性

- [ ] **`@Transactional(rollbackFor = Exception.class)`** 加在 `xxxFromAi`
- [ ] **审计字段显式 set**：createBy/updateBy 来自 operatorId 参数，不依赖自动注入兜底
- [ ] **数据源假设记录**：Service 假设数据源已切到 tenant_xxx，不在方法内 push
- [ ] **错误消息合规**：人类可读、不暴露 PII、不暴露 SQL/堆栈
- [ ] **可重入性策略**：选择"javadoc 约束 / AiCallContext 校验 / 内部 doXxx 隔离"之一并实施
- [ ] **二次确认预览**：Tool 返回 record 包含 `summary` 字段

### 6.6 验收

- [ ] **手工 LLM 测试**：构造一个 LLM 调用（缺参数、错枚举值、ID 不存在、重复操作各一次），观察 LLM 收到错误后的行为是否符合预期
- [ ] **数据库核对**：Tool 调用后查表，所有 NOT NULL 字段非空，无 `-1`/`"0"`/`null` 类静默降级
- [ ] **审计可追**：`ai_tool_invocation` 表能查到本次调用，含 callId、userId、参数、结果

---

## 7. 评审清单

代码评审人在合入 WriteTool 相关 PR 前必须对照本清单：

```
□ Tool 层是否注入了 Mapper?(应该没有)
□ Tool 内是否有 DB 查询、字段补全、默认值设置?(应该没有)
□ Service 方法是否命名为 xxxFromAi?
□ 是否有 @Transactional 注解?
□ 所有 ID 参数是否都查库校验存在?
□ @TableField(exist=false) 字段是否被错误地直接取值?
□ 编号生成是否有并发保护?
□ 错误消息是否人类可读、不含 PII/SQL?
□ Tool 返回 record 是否包含 summary?
□ 是否补全了对应的 readonly Tool?
□ system prompt 是否需要更新?(若有新业务场景)
□ 是否破坏了既有 Controller 路径的数据一致性?
```

---

## 8. 范式演进规则

- 修改本范式需提 PR，PR 中必须列出受影响的现有 WriteTool 与改造计划
- 范式发布后，新建 WriteTool 必须遵守；存量 WriteTool 在下次修改时遵守
- 出现范式覆盖不到的新场景（如 MCP Tool、多模态写操作）时，**先扩展范式再写代码**
- 每次范式修改更新本文件 v 号与"修订记录"

---

## 9. 首批落地对象：三个现有 WriteTool 改造

> 本节是范式发布后的第一批改造任务清单，作为范式应用示范。完成后归档到 `docs/superpowers/plans/2026-05-15-write-tool-refactor-plan.md`，本节保留作为范式示例。

### 9.1 CreateRepairTool（CRUD 类）

**现状代码**：`thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/CreateRepairTool.java:48-65`

**现状缺陷**：

| # | 缺陷 | 影响 |
|---|------|------|
| 1 | 缺 `repairNo` 生成（手动路径 `HtRepairController.insert` 调了 `generateRepairNo()`） | 数据库 repair_no 列空，无法按编号检索 |
| 2 | 缺 `buildingId / buildingName / unitCode / roomNum` 补全 | 工单列表过滤、统计、跨表关联失效 |
| 3 | `house.getOrgName()` 取 null（`PrHouse.orgName` 是 `@TableField(exist=false)`） | orgName 字段空 |
| 4 | `house == null` 时 `orgId = "0"` 静默降级 | NOT NULL 约束不报错，但数据无意义 |
| 5 | 默认值缺失 `urgentType`、`serviceType`（手动 Bo 通常会带） | 默认枚举值为 null |
| 6 | 不在事务内 | 并发场景下编号撞号、部分字段写入 |

**目标改造**：

| 层 | 改动 |
|------|------|
| Tool 层 | 移除 `PrHouseMapper` 注入；仅调用 `repairService.createFromAi(houseId, repairInfo, userName, userPhone, operatorId)`；返回的 record 加 `summary` |
| Service 层 `createFromAi()` | 1. `@Transactional(rollbackFor = Exception.class)`<br>2. 查 `PrHouse`，不存在抛异常<br>3. 从 PrHouse 带出 `buildingId/buildingName/unitCode/roomNum/orgId`<br>4. 查 `sys_organization`（或对应表）获取 `orgName`<br>5. 调 `generateRepairNo()`（并发保护见 §4.2）<br>6. 设默认值 `repairType=0`、`urgentType=0`、`serviceType=0`、`repairTime=now`、`repairStatus=0`<br>7. 显式 `setCreateBy/setUpdateBy(operatorId)`<br>8. 保存 |

**涉及文件**：
- `CreateRepairTool.java` — 精简为薄层
- `IHtRepairService.java` — 新增 `createFromAi` 签名
- `HtRepairServiceImpl.java` — 实现 `createFromAi`，注入 `PrHouseMapper` 与组织 Mapper

### 9.2 MarkPaidTool（CRUD 类 — 状态变更）

**现状代码**：`MarkPaidTool.java:42-48`

**现状缺陷**：

| # | 缺陷 | 影响 |
|---|------|------|
| 1 | 直接操作 Mapper，无状态校验 | 已缴费条目可被重复标记 |
| 2 | 缺 `chargedTime` 设置（实体字段 `PrExpense.chargedTime` 存在） | 缴费时间空，对账困难 |
| 3 | 不写 `paidIn`、`recordId`、`isClosed` 等状态联动字段 | 与手动收费路径数据不一致 |
| 4 | 补全逻辑在 Tool 内 | 违反职责分层 |
| 5 | 不在事务内 | 状态部分更新 |

**业务语义待确认**：
- "已缴费时再调用" 的处理策略：抛异常（阻止重复标记）vs 返回 `needConfirm`（让用户二次确认）vs 允许覆盖（视为补登）
- **建议默认抛异常**：错误消息引导客服核对前一次缴费记录；如有"补登"需求，新增独立 Tool `overrideMarkPaid` 且 risk=HIGH

**目标改造**：

| 层 | 改动 |
|------|------|
| Tool 层 | 移除 `PrExpenseMapper` 注入；仅调用 `expenseService.markPaidFromAi(expenseId, note, operatorId)`；返回 record 加 `summary` |
| Service 层 `markPaidFromAi()` | 1. `@Transactional(rollbackFor = Exception.class)`<br>2. 查费用条目，不存在抛异常<br>3. 校验 `isCharged != 1`，已缴费抛"已缴费,无需重复标记"<br>4. 设 `isCharged=1`、`chargedTime=now`、`paidIn=receivable`<br>5. 显式 `setUpdateBy(operatorId)`<br>6. 保存 |

**涉及文件**：
- `MarkPaidTool.java` — 精简为薄层
- `IPrExpenseService.java` — 新增 `markPaidFromAi` 签名
- `PrExpenseServiceImpl.java` — 实现 `markPaidFromAi`

### 9.3 DispatchValveCommandTool（Command 类）

**现状代码**：`DispatchValveCommandTool.java:46-69`

**现状缺陷**：

| # | 缺陷 | 影响 |
|---|------|------|
| 1 | `dryRun` 分支在 Tool 内 | 与 `ToolExecutor.applyDryRunPolicy` 重复逻辑 |
| 2 | IoT 下发占位实现在 Tool 内 | 真实接入时改 Tool 而不是 Service |
| 3 | 阀门不存在返回 result，未抛异常 | LLM 拿到的不是"失败"而是"已下发"，误导后续对话 |
| 4 | `action` 枚举无校验 | LLM 可能传 `"open"` / `"TURN_ON"` 等非法值 |
| 5 | `openness` 逻辑必填未校验（仅 `SET_OPENNESS` 时） | 缺值时静默下发 |
| 6 | 失败无落表 | 真实 IoT 下发失败无法追溯 |

**目标改造**：

| 层 | 改动 |
|------|------|
| Tool 层 | 移除 `PrHeatValveArchiveMapper` 注入；仅调用 `valveArchiveService.dispatchFromAi(houseId, action, openness, dryRun, operatorId)`；返回 record 加 `summary` |
| Service 层 `dispatchFromAi()` | 1. 查阀门确认存在，不存在抛异常<br>2. 校验 action ∈ {OPEN, CLOSE, SET_OPENNESS}（大写敏感）<br>3. `SET_OPENNESS` 时校验 openness ∈ [0,100]<br>4. `dryRun=true` → 仅返回预览，不下发<br>5. `dryRun=false` → 下发 IoT，失败写 `ht_control_record` 表（带错误堆栈），抛 `ToolExecutionException`<br>6. 成功时写 `ht_control_record` 记录指令 |

**涉及文件**：
- `DispatchValveCommandTool.java` — 精简为薄层
- `IPrHeatValveArchiveService.java` — 新增 `dispatchFromAi` 签名
- `PrHeatValveArchiveServiceImpl.java` — 实现 `dispatchFromAi`
- 若 `ht_control_record` 表/Mapper 不存在，一并补充

### 9.4 assistantChatClient system prompt

**涉及文件**：`SdkjAiAutoConfiguration.java:78-102`

**改动**：将 `defaultSystem` 替换为 §3.4 给出的"目标新 system prompt"。

---

## 10. 附录

### A. 现状代码与目标代码对比

详见 §9 各小节"涉及文件"列表，配合 git diff 阅读。

### B. 术语表

| 术语 | 含义 |
|------|------|
| WriteTool | 带 `@WriteTool` 注解的 Tool 方法（risk ≥ MEDIUM） |
| CRUD 类 | 写业务表的 WriteTool |
| Command 类 | 触发外部动作的 WriteTool（不写业务表） |
| FromAi 方法 | Service 层为 AI Tool 准备的入口方法，约定命名 `xxxFromAi` |
| 二次确认 | MEDIUM/HIGH 风险写操作触发的前端确认弹窗（`/ai/tool-calls/{callId}/confirm`）|
| 字段补全 | 由 ID 反查关联实体，补全业务表的其他字段（buildingId、orgName 等） |
| dryRun | Command 类 Tool 仅生成预览不真实下发的模式 |
| 逻辑必填 | `@ToolParam(required=false)` 但在某些条件下必填的参数 |
| 降级引导 | 关键信息缺失时，由 system prompt 指引用户去 UI 手动操作 |

### C. 相关文件索引

| 用途 | 文件 |
|------|------|
| 范式本身 | `docs/superpowers/specs/2026-05-15-write-tool-data-completeness-design.md`（本文件） |
| Tool 注解定义 | `sdkj-ai/src/main/java/org/sdkj/ai/tools/annotation/WriteTool.java` |
| 风险等级枚举 | `sdkj-ai/src/main/java/org/sdkj/ai/tools/annotation/RiskLevel.java` |
| Tool 注册扫描 | `sdkj-ai/src/main/java/org/sdkj/ai/tools/registry/ToolRegistry.java` |
| 调度分流 | `sdkj-ai/src/main/java/org/sdkj/ai/tools/dispatcher/ToolCallDispatcher.java` |
| 确认执行 | `sdkj-ai/src/main/java/org/sdkj/ai/tools/dispatcher/ToolExecutor.java` |
| 确认 HTTP 端点 | `sdkj-ai/src/main/java/org/sdkj/ai/tools/controller/AiToolCallController.java` |
| 数据源切换 | `sdkj-common/sdkj-common-tenant/src/main/java/org/sdkj/common/tenant/core/TenantFilter.java` |
| 审计字段自动注入 | `sdkj-common/sdkj-common-mybatis/src/main/java/org/sdkj/common/mybatis/handler/InjectionMetaObjectHandler.java` |
| AI ChatClient 配置 | `sdkj-ai/src/main/java/org/sdkj/ai/config/SdkjAiAutoConfiguration.java` |
| 现有 WriteTool | `sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/*.java` |
| 现有 readonly Tool | `sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/readonly/*.java` |
