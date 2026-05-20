# sdkj-ai — AI 集成模块

Spring AI 1.0.7 + OpenAI 兼容协议（可对接 DeepSeek / Qwen 等）。

## Phase 1 — AI 旁注 (Alt+Shift+A)

**端点**: `POST /ai/contextual-view`

- SPI 机制：`ContextualPrompt` 接口 + `ContextualPromptRegistry` Spring bean 扫描
- 3 个 demo Prompt：`UserListPrompt`、`RoleListPrompt`、`DeptListPrompt`
- Fallback：未注册路由返回 `GenericContextualView`
- 缓存：Redis-backed `AiViewCache`，sha1 cache key
- 安全护栏：`AiCircuitBreaker`、限流 30/min、`PiiMasker` + `ApiKeyLogMasker`、3 个 Advisor

## Phase 2A — AI 助手 + RAG (Alt+K)

**端点**: `POST /ai/assistant/chat`、`POST /ai/assistant/chat/stream`、`/ai/assistant/sessions/*`

- **租户 AI 总闸**：`sys_tenant.ai_enabled` + `AiTenantGate` + `AiDisabledException`(503)
- **Qdrant VectorStore**：每租户独立 collection `sdkj_kb_${tenantId}`
- **DeepSeek Embedding**：`text-embedding-v1`
- **知识库管道**：`ChunkingService` → `EmbeddingService` → `KbIngestPipeline`
- **KB 管理后台**：`AiKbController`(/ai/admin/kb/*)
- **ChatMemory**：`JdbcChatMemoryRepository` + `MessageWindowChatMemory`(20 条窗口)
- **RAG Citations**：`KbRetrievalService.retrieveWithCitations()` 返回引用来源

## Phase 2B — Tool Calling + ConfirmationGate

**端点**: `/ai/tool-calls/{id}/{confirm,reject,status}`、`/ai/tool-calls/by-message/{id}`、`/ai/admin/tools`

### 风险等级矩阵

| 级行为 | 前端体验 |
|--------|---------|
| LOW | 直接执行，无弹窗 |
| MEDIUM | 弹确认框，确认后执行 |
| HIGH | 弹确认框 + 3 秒倒计时 |
| CRITICAL | 一律拒绝，不弹框 |

### 核心机制

- **`@WriteTool` 注解**：标记写操作 Tool，属性 `risk()`/`confirm()`/`permission()`
- **`ToolRegistry`**：启动时扫描 @Tool beans，管理黑名单
- **`ToolCallDispatcher`**：按风险级别分流 — LOW 自动执行、MEDIUM/HIGH 待确认、CRITICAL 拒绝
- **`ToolExecutor`**：权限检查 + dryRun 策略 + 反射执行
- **`ConfirmationStore`**：Redis 主 (`ai:ptc:{callId}`, TTL 30min) + MySQL 异步双写 (`ai_pending_tool_call`)
- **两阶段 SSE 流**：
  1. 第一阶段：LLM 生成 → 遇到写工具 → `PendingConfirmationException` → SSE `toolCallPending` 帧 → 流结束
  2. 用户确认 → `POST /{callId}/confirm` → 第二阶段 SSE：执行工具 → LLM 继续生成

### dryRun 策略（阀门 Tool）

- `ai.tools.valve.dry-run: true` 默认开启
- 用户持 `thermal:ht:valve:execute` 权限时，确认阶段强制 `dryRun=false`
- `ToolExecutor.applyDryRunPolicy` 单点处理

### 数据表（master 库 ry-vue）

- `ai_pending_tool_call` — 待确认状态机 (PENDING → APPROVED → EXECUTED/FAILED/REJECTED)
- `ai_tool_invocation` — Tool 调用详情（完整入参出参/耗时/风险等级）
- `ai_chat_message.role` 枚举新增 `TOOL`

### 配置

```yaml
sdkj:
  ai:
    tools:
      disabled: [valveControlTool]           # Bean 名黑名单
      write-rate-limit-per-minute: 10        # 写 Tool 限流
      valve:
        dry-run: true                        # 阀门 dryRun 默认开启
```

### 已注册 Tool

**只读 (LOW)**：`queryArrearsTool`、`getRepairHistoryTool`、`getValveStatusTool`、`getStationDailyStatTool`

**写操作**：`createRepairTool`(MEDIUM)、`markPaidTool`(MEDIUM)、`dispatchValveCommandTool`(HIGH)

**演示**：`batchValveDispatchTool`(CRITICAL，本期一律拒绝)

### 关键 API 路径

- `ToolCallingChatOptions` 在 `org.springframework.ai.model.tool` 包
- 工具注册在请求时 `.tools(toolRegistry.getToolBeans())`，非 Bean 创建时
- 基础设施在 sdkj-ai，Tool 实现在 sdkj-thermal

## Advisor Chain

```
TenantContextAdvisor (order=0)
  → MemoryAdvisor (order=10)
  → SafetyAuditAdvisor (order=15)
  → UsageMetricsAdvisor (order=25)
```

Phase 2B 的 `ToolCallDispatcher` 在 Tool 调用阶段拦截，不在 Advisor Chain 中。
