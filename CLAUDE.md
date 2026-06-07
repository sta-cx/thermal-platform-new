# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

**SDKJ 智慧供热综合管理平台** 后端 — 基于 RuoYi-Vue-Plus 5.6.0 改造的单体 Spring Boot 应用。包名 `org.sdkj`，模块前缀 `sdkj-`。

## 构建 & 运行

```bash
cd D:\chonggou\thermal-platform-new

mvn clean compile                          # 编译
mvn clean package -DskipTests              # 打包（默认 skipTests=true）
mvn spring-boot:run -pl sdkj-admin         # 开发运行（端口 8080）
```

- 主类: `org.sdkj.SdkjApplication`
- Spring profiles 硬编码为 `dev,thermal`（`application.yml` → `spring.profiles.active`）
- Maven profiles (`local`/`dev`/`prod`) 仅控制 `@logging.level@` 变量
- `sdkj-ai` 已有 2 个 JUnit 单测（BM25/中文预处理）；其他模块基本无测试，常规验证仍以编译、启动和接口手测为主

## 模块结构

```
sdkj-admin/           # 启动入口 + 配置文件，依赖四个业务模块
sdkj-common/          # 24个公共模块（core/mybatis/redis/security/satoken/oss/sms/...）
sdkj-modules/
  sdkj-system/        # 系统管理（用户/角色/菜单/部门/字典/租户/OSS）
  sdkj-meter/         # 仪表管理（热力表/水表/电表/燃气表/集中器/温控器/阀门）
  sdkj-thermal/       # 热力调控 + 物业收费（核心业务，~65 个 Controller）
  sdkj-ai/            # AI 集成（Spring AI 1.0.7，Alt+A 旁注 + Alt+K 助手 + Tool Calling 基建，OpenAI 兼容协议）
```

**模块依赖**: `sdkj-thermal` → `sdkj-system`（唯一跨业务模块依赖）。`sdkj-meter`、`sdkj-ai` 与 `sdkj-system` 无互相依赖。四个业务模块都依赖 `sdkj-common-*` 公共模块。

## 分层架构

```
前端 Bo → Controller(接收Bo, MapstructUtils.convert→Entity) → Service → Mapper
Mapper.selectVoXxx() → MapstructUtils.convert→Vo → 返回前端
```

- **Entity** (`domain/`): 继承 `BaseEntity`，`@TableName` + `@AutoMapper(target=Vo.class)` + `@TableId`
- **Vo** (`domain/vo/`): 纯 POJO，`@AutoMapper(target=Entity.class)` 双向映射
- **Bo** (`domain/bo/`): 继承 `BaseEntity`，`@AutoMapper(target=Entity.class, reverseConvertGenerate=false)` 单向，带 JSR-303 校验
- **Mapper**: 继承 `BaseMapperPlus<Entity, Vo>`，内置 `selectVoById/selectVoList/selectVoPage/insertBatch/updateBatchById`
- **Service**: thermal 模块继承 `ServiceImpl<Mapper, Entity>`；跨表查询注入多个 Mapper 用 Stream 组装（非 JOIN）
- **Controller**: 继承 `BaseController`，`@SaCheckPermission` + `@Log`，返回 `R<T>` 或 `TableDataInfo<Vo>`

## 关键业务前缀

| 前缀 | 模块 | 说明 |
|------|------|------|
| `Ht*` | sdkj-thermal | 热力调控 |
| `Pr*` | sdkj-thermal | 物业收费 |
| `Mt*` | sdkj-meter | 仪表 |
| `IoT*`/`Control*` | sdkj-thermal | IoT 回调与控制指令返回 |
| `PrImport*` | sdkj-thermal | 批量导入 |
| `Ai*` | sdkj-ai | AI 调用记录 / 用量日志 / 上下文视图 |

**已删除不再使用的前缀**: `Ag*`（物业代理）、`Wx*`/`Wechat*`（微信）、`Pm*`（停车）、`PrInspection*`（巡检）。

## 数据源 & 多租户

独立库模式：master 库 `ry-vue` 存系统表（sys_user/sys_dept/sys_role 等），每个租户有独立业务库（sys_company/sys_organization/pr_* /ht_* 等）。

- `TenantDataSourceInitializer` 启动时从 `sys_tenant` 表读取配置动态注册 `tenant_{id}` 数据源
- `TenantFilter` 对 `/thermal/` `/dashboard/` `/api/iot/` `/api/returnControl/` 自动切换数据源
- `tenant.enable: false` 关闭 `tenant_id` 行级 SQL 过滤（独立库模式下不需要）
- 异步线程通过 `TenantTaskDecorator` 传递租户上下文
- `sys_dept`/`sys_user` 等系统表在主库（`/system/*` 路径不切数据源）；`sys_company`/`sys_organization` 等业务表在分库（`/thermal/*` 路径切到分库）

其他数据源：`sqlserver`（IoT）、`legacy`（旧系统迁移 `rltk_pro`）。

**异步线程数据源陷阱**: `@Async` 方法通过 `TenantTaskDecorator` 继承租户上下文。如果异步任务需要写主库系统表（如 `sys_oper_log`/`sys_logininfor`），必须用 `DynamicDataSourceContextHolder.push("master")` 强制切回主库。

## 配置文件

| 文件 | 内容 |
|------|------|
| `application.yml` | 端口/日志/密码策略/XSS/验证码关闭/加密关闭/tenant关闭 |
| `application-dev.yml` | 数据源(master+sqlserver)/Redis/邮件/短信/第三方 |
| `application-thermal.yml` | 供热专属：采集平台API/MBus阀控/迁移/微信支付/OSS/短信 |
| `application-prod.yml` | 生产环境 |

MyBatis-Plus 默认配置在 `sdkj-common-mybatis/src/main/resources/common-mybatis.yml`：主键雪花ID、逻辑删除 1/0。

## 请求处理链路

```
HTTP → TenantFilter(租户数据源切换) → SaInterceptor(登录+clientId校验)
     → Controller(权限+日志+Bo→Entity) → Service(LambdaQueryWrapper+Mapper)
     → MybatisPlusInterceptor:
         TenantLineInnerInterceptor → 按 tenant_id 过滤（主库）
         PlusDataPermissionInterceptor → 按 dept_id 过滤（系统表，@DataPermission）
         OrgPermissionInterceptor → 按 org_id 过滤（业务表，@OrgPermission）
         PaginationInnerInterceptor → 分页
         OptimisticLockerInnerInterceptor → 乐观锁
     → InjectionMetaObjectHandler(审计字段自动填充)
     ← GlobalExceptionHandler(14+种异常→R<Void>)
```

## 认证

Sa-Token JWT 简单模式：`Authorization: Bearer <token>`，允许并发登录，每次生成新 token。

登录请求体需包含 `clientId` + `grantType` 字段：
```json
{"username":"admin","password":"admin123","tenantId":"000000","clientId":"e5cd7e4891bf95d1d19206ce24a7b32e","grantType":"password"}
```

## 组织级数据权限（@OrgPermission）

独立于 `@DataPermission`（部门级），按 `org_id` 过滤业务数据。

- 注解：`@OrgPermission` 加在 Mapper 方法或类上，`value()` 指定列名（默认 `org_id`）
- 超管/租户管理员：跳过过滤
- 普通用户：子查询 `org_id IN (SELECT org_id FROM pr_data_grant WHERE user_id = ?)`
- 无 `pr_data_grant` 记录的用户：全量可见（不过滤）
- `OrgPermissionInterceptor` 在非 HTTP 上下文（启动阶段、定时任务）自动跳过
- `ThermalSysHomeMapper` 中已手动 JOIN `pr_data_grant` 的查询不要加 `@OrgPermission`

## 开发约定

- **主子表更新**: 先删子表再重新插入（先删后插模式，不做 diff）
- **审计字段**: `createBy(Long)/updateBy(Long)/createTime/updateTime/createDept`，由 `InjectionMetaObjectHandler` 自动填充
- **分页**: `PageQuery` 封装，默认不传分页参数时查全部（pageSize=MAX_VALUE）
- **跨表查询**: 注入多个 Mapper 分开查询 + Stream 组装，不用 JOIN
- **性别字段**: 新系统 0=男/1=女/2=未知（老系统 1=男/0=女）
- **Long ID 序列化**: `BigNumberSerializer` 全局注册，雪花 ID 自动序列化为字符串给前端

## API 路由

| 业务 | 路由前缀 |
|------|---------|
| 系统管理 | `/system/*` |
| 仪表管理 | `/thermal/meter/*` |
| 热力调控 | `/thermal/ht/*` |
| 物业收费 | `/thermal/property/*` |
| 单笔收费 | `/thermal/property/singleCharge/*` |
| IoT | `/thermal/iot/*` |
| 认证 | `/auth/*` |
| AI 集成 | `/ai/*`（`/ai/contextual-view`、`/ai/health`、`/ai/admin/*`、`/ai/assistant/*`、`/ai/tool-calls/*`） |

## AI 模块（sdkj-ai）

基于 Spring AI 1.0.7 + OpenAI 兼容协议（可对接 DeepSeek / Qwen 等）。配置位于 `application-thermal.yml` 的 `spring.ai.openai.*` 与 `sdkj.ai.*`。

- **核心包**: `core/`（ContextualPrompt 注册表 + 视图模型）、`advisor/`（TenantContextAdvisor / SafetyAuditAdvisor / UsageMetricsAdvisor 三个 Spring AI Advisor）、`safety/`（PiiMasker / ApiKeyLogMasker / AiCircuitBreaker）、`cache/`（AiViewCache + 命中率指标）、`job/`（AiLogCleanupJob，每日清理 90 天前的 `ai_call_record`）、`tools/`（@WriteTool 注解 / RiskLevel / ToolRegistry / ToolCallDispatcher / ToolExecutor / ConfirmationStore）、`assistant/`（AssistantService / SessionService / SSE 流）、`kb/`（KbRetrievalService / RAG Citations）
- **Controller**: `AiContextualController`（POST `/ai/contextual-view`，限流 30/min）、`AiHealthController`、`AiAdminController`、`AiAssistantController`（chat/stream/session CRUD）、`AiToolCallController`（confirm/reject/status）、`AiToolsAdminController`（Tool 注册表看板）、`AiKbController`（知识库上传/检索）、`KbAdminController`（知识库管理操作）
- **可观测性**: Micrometer counter+timer + 缓存命中/未命中指标；`ApiKeyLogMasker` 作为 Logback conversion rule 自动注册
- **数据表**: `ai_call_record`、`ai_usage_log`、`ai_pending_tool_call`、`ai_tool_invocation`、`ai_chat_session`、`ai_chat_message`（**位于 master 库 `ry-vue`**,通过 Mapper 上 `@DS("master")` 强制路由；TenantFilter 把 `/ai/*` 推到租户 DS 后,这些 Mapper 仍会切回 master）
- **Tool 实现**: 8 个 Tool 在 `sdkj-thermal` 模块（`ai/tools/readonly/` 4 个 + `ai/tools/write/` 4 个），基础设施在 `sdkj-ai`
- **关闭开关**: `sys_tenant.ai_enabled = 0`（沿用 Phase 2A 总闸）；`ai.tools.disabled` 黑名单可禁用特定 Tool

## 数据库

- 主库连接：`mysql -uroot -proot -h localhost \`ry-vue\``（库名有横线需反引号）
- 分库连接：`mysql -uroot -proot -h localhost tenant_000000`
- 旧系统库：`mysql -uroot -proot -h localhost rltk_pro`
- 租户库 schema：`sdkj-admin/src/main/resources/sql/tenant_db_schema.sql`（新建租户时自动执行）
- 数据库转储：`script/sql/` 下的 `dump-*.sql` 文件
