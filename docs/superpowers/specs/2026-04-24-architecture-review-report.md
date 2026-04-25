# 架构迁移审核总报告

**日期**: 2026-04-24
**范围**: thermal-balance-backend → thermal-platform-new
**审核方式**: Gap Analyst + Code Auditor + Equivalence Tester 三维度并行

---

## 一、执行摘要

| 指标 | 数值 |
|------|------|
| 老系统 Controller 数 | 110 |
| 新系统 Controller 数 | 58 |
| API 端点迁移率 | **约 25%**（130/520） |
| 代码质量得分 | **65/100** |
| 未迁移模块 | **58 个**（排除已替代项） |
| 代码问题 | 3 个 P0 + 8 个 P1 + 15 个 P2 + 12 个 P3 |

**关键发现**:
1. 核心 CRD 模块已等价迁移（策略/指令/报警/报修/仪表/收费基础）
2. 配表模块（9 个）和房屋/楼宇/单元管理完全缺失 — 直接影响业务闭环
3. 3 个 P0 级代码质量问题需立即修复（死类引用、支付回调无鉴权、存根 Controller 暴露）
4. HtTasksController 存在逻辑简化，丢失运行状态校验和精细查询

---

## 二、迁移缺口清单（Gap Analyst）

### 未迁移模块（按优先级排序）

#### 第一阶段：核心业务补全（15-20 天）

| 序号 | 模块名 | 业务域 | Entity数 | 复杂度 | 工作量 | 说明 |
|------|--------|--------|----------|--------|--------|------|
| 1 | PrHeatArchiveController | 热力配表 | 13 | 大 | >3天 | 房屋热表配表，含阀门调控、巡测、充值 |
| 2 | PrHeatCommandValveArchiveController | 热力配表 | 12 | 大 | >3天 | 户间控制阀门，NB/MBus 数据接入 |
| 3 | PrHeatHotArchiveController | 热力配表 | 10 | 大 | >3天 | 房屋热量表配表 |
| 4 | PrHeatCommandUnitValveArchiveController | 热力配表 | 10 | 大 | >3天 | 单元控制阀门配表 |
| 5 | PrHeatValveArchiveController | 热力配表 | 9 | 大 | >3天 | 户间阀门配表 |
| 6 | PrHeatDtuArchiveController | 热力配表 | 8 | 大 | 2-3天 | DTU 采集器配表 |
| 7 | PrHeatUnitHotArchiveController | 热力配表 | 7 | 中 | 2-3天 | 单元热表配表 |
| 8 | PrHeatUnitValveArchiveController | 热力配表 | 7 | 中 | 2-3天 | 单元阀门配表 |
| 9 | PrHeatTempArchiveController | 热力配表 | 5 | 中 | 1-3天 | 温采器配表 |
| 10 | PrHeatDailyController | 热力配表 | 6 | 中 | 1-2天 | 热表日表 |
| 11 | PrHeatMonthController | 热力月表 | 5 | 中 | 1-2天 | 热表月表 |
| 12 | PrHeatReadingController | 热力抄表 | 7 | 中 | 2-3天 | 抄表数据 |
| 13 | PrHouseController | 物业房屋 | 5 | 中 | 1-3天 | 房屋管理 |
| 14 | PrBuildingController | 物业房屋 | 2 | 小 | <1天 | 楼宇管理 |
| 15 | PrCompanyController | 物业组织 | 4 | 中 | 1-2天 | 组织机构树 |

#### 第二阶段：辅助功能（5-8 天）

| 序号 | 模块名 | 业务域 | Entity数 | 复杂度 | 工作量 |
|------|--------|--------|----------|--------|--------|
| 16-17 | PrHeatStation + Partition | 热力站 | 3-5 | 小-中 | <1天 |
| 18-20 | 巡检 + 报修 | 物业 | 2-3 | 小 | 1-2天 |
| 21 | PrImport* (6个) | 导入导出 | 1-4 | 小 | 2-3天 |
| 22 | WechatAuth + WechatPay | 微信 | 2-4 | 中 | 1-3天 |
| 23 | Reconciliation | 对账 | 4 | 中 | 1-2天 |

#### 第三阶段：边缘功能（2-3 天）

| 序号 | 模块名 | 优先级 | 说明 |
|------|--------|--------|------|
| 24-31 | Notice/Scheduling/Pet/Abnormal/Regional 等 | P2-P3 | 边缘功能 |
| 32-37 | 空 Controller（HtScopeDtu/HtStrategyPerform 等） | P3 | 确认是否需要 |

#### 不需要迁移（已被新系统架构替代）

| 老模块 | 替代方案 |
|--------|----------|
| AgentCompany/Property/Role/User | 新系统多租户架构（tenant_id） |
| MenuController/RoleController | SysMenuController/SysRoleController |
| SysCompanyController | SysDeptController |
| SaOAuth2ServerController | 新系统 Sa-Token 认证 |

---

## 三、代码质量报告（Code Auditor）

### 问题统计

| 级别 | 数量 | 处理要求 |
|------|------|----------|
| **P0 阻塞** | 3 | 必须立即修复 |
| **P1 严重** | 8 | 本轮迭代修复 |
| **P2 建议** | 15 | 计划内修复 |
| **P3 优化** | 12 | 择机修复 |

### Top 5 关键问题

1. **P0 — HtTasksController 死类引用** (`HtTasksController.java:87`)
   - `task.setBeanClass("com.thermal.job.ControlJob")` — 类不存在，运行时报 ClassNotFoundException
   - 修复：替换为 `"org.sdkj.job.ControlJob"` 或实现真实 Quartz Job

2. **P0 — 支付回调无鉴权** (`PrAutoMachineController.java:111-126`)
   - `/callback/wechat-heat` 和 `/callback/ali-heat` 无 `@SaCheckLogin`、无签名验证，任何人可 POST
   - 修复：添加签名校验或 IP 白名单

3. **P0 — PrAutoMachineController 全部为存根**
   - 13 个方法全部返回 `R.fail("此功能需要完整实现")`，暴露无用 API
   - 修复：从路由移除或标记 `@Deprecated` + 从 API 文档隐藏

4. **P1 — 硬编码 JWT 密钥** (`application.yml:104`)
   - `jwt-secret-key: sdkj2024`
   - 修复：使用环境变量 `${JWT_SECRET_KEY}`

5. **P1 — HeatMeterControl HTTP 头未附加** (`HeatMeterControl.java:45-51`)
   - 创建 HttpHeaders 但从未传递给 HttpEntity，Content-Type 丢失
   - 修复：使用 `new HttpEntity<>(payload, headers)`

### 架构规范遵守度

| 维度 | sdkj-system | sdkj-meter | sdkj-thermal | 结论 |
|------|:-----------:|:----------:|:------------:|------|
| Mapper 继承 BaseMapperPlus | 100% | 100% | 100% | 合规 |
| @AutoMapper 注解 | 100% | 100% | 100% | 合规 |
| Bo/Vo 分离 | 100% | **0%** | **0%** | **不合规** |
| @SaCheckPermission | 100% | **0%** | **0%** | **不合规** |
| @Transactional 写操作 | 95% | 95% | 90% | 基本合规 |
| 旧包引用残留 | 100% | 100% | 98% | 基本合规 |

**总体得分: 65/100**

---

## 四、功能等价验证（Equivalence Tester）

### 覆盖率统计

| 状态 | 端点数 | 占比 |
|------|--------|------|
| ✅ 等价 | ~130 | 25% |
| ⚠️ 部分等价 | ~25 | 5% |
| ❌ 缺失 | ~340 | 65% |
| 🔴 有差异 | ~25 | 5% |

### Top 5 高风险差异

1. **房屋管理模块完全缺失**（PrHouseController — 40+ 端点）
   - 风险等级：**致命** — 没有房屋数据，收费和调控业务流程无法运转

2. **任务状态/删除校验缺失**（HtTasksController）
   - 旧版 edit/remove 前有 `JobStatusEnum.RUNNING` 校验，新版直接操作
   - 风险等级：**高** — 运行中的任务可能被修改或删除

3. **任务精细查询参数大量丢失**（HtTasksController pageList）
   - 旧版 25+ 参数过滤，新版仅 3 个
   - 风险等级：**高** — 前端列表过滤能力大幅降级

4. **阀门档案模块完全缺失**（PrHeatValveArchiveController — 40+ 端点）
   - 风险等级：**高** — 直接影响终端设备控制能力

5. **自助机支付端点全为占位**（PrAutoMachineController）
   - 全部返回 `R.fail()`，线下自助缴费不可用
   - 风险等级：**高**

---

## 五、行动建议

### 立即修复（当前 Sprint，1-2 天）

| # | 任务 | 来源 | 预计工作量 |
|---|------|------|-----------|
| 1 | 修复 `com.thermal.job.ControlJob` 死类引用 | P0 代码质量 | 10分钟 |
| 2 | PrAutoMachineController 回调端点添加鉴权 | P0 安全 | 30分钟 |
| 3 | PrAutoMachineController 从路由移除或隐藏 | P0 存根 | 10分钟 |
| 4 | HtTasksController 补充 jobStatus 校验（edit/remove） | 等价验证 #2 | 30分钟 |
| 5 | HeatMeterControl HTTP 头修复 | P1 代码质量 | 30分钟 |
| 6 | application.yml JWT 密钥改为环境变量 | P1 安全 | 10分钟 |
| 7 | PrUseCardLogServiceImpl.changeValveStatus 加 @Transactional | P1 代码质量 | 5分钟 |
| 8 | application.yml RSA 密钥改为环境变量 | P1 安全 | 10分钟 |

### 短期迁移（Phase 6，15-20 天）

| # | 任务 | 预计工作量 | 依赖 |
|---|------|-----------|------|
| 1 | 迁移 9 个配表模块（PrHeatArchive 等） | 10-15天 | — |
| 2 | 迁移 PrHouse/PrBuilding/PrUnit 房屋管理 | 3-5天 | 无 |
| 3 | 补充 HtTasksController 精细查询参数 | 1-2天 | 无 |
| 4 | 创建 sdkj-meter Bo 类（10个） | 2天 | 无 |
| 5 | 创建 sdkj-thermal Bo 类（26个） | 3天 | 无 |

### 中期迁移（Phase 7，5-8 天）

| # | 任务 | 预计工作量 |
|---|------|-----------|
| 1 | 迁移热力站/巡检/报修模块 | 2-3天 |
| 2 | 迁移 6 个 PrImport* 导入模块 | 2-3天 |
| 3 | 集成微信认证与支付 | 1-3天 |

### 长期规划（Phase 8+）

| # | 任务 | 优先级 |
|---|------|--------|
| 1 | 所有业务 Controller 添加 @SaCheckPermission | P2 |
| 2 | 补充 @TableLogic 软删除注解 | P3 |
| 3 | 微信生态完整集成 | P2 |
| 4 | Quartz 任务调度完整实现 | P1 |
| 5 | 清理空 Controller 和存根模块 | P3 |

---

## 附录

| 文档 | 路径 |
|------|------|
| 设计文档 | `docs/superpowers/specs/2026-04-24-architecture-review-design.md` |
| 代码审计报告 | `AUDIT_REPORT.md` |
| 等价验证报告 | `docs/equivalence-test-report.md` |
