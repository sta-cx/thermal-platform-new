# 迁移修复总计划

**日期**: 2026-04-25
**基于审核报告**: `docs/superpowers/audit/01-06`

---

## 总览

基于 6 个模块的并行审核，共发现 10 个 Critical 和 20+ 个 Important 问题。按优先级拆分为 4 个子计划：

| 计划 | 名称 | 优先级 | 任务数 | 预计工期 | 前置依赖 |
|------|------|--------|--------|---------|---------|
| [Plan A](./2026-04-25-plan-a-p0-blocking-fixes.md) | P0 阻塞性修复 | **最高** | 10 | 3-5 天 | 无 |
| [Plan B](./2026-04-25-plan-b-ht-regulation.md) | Ht 热力调控补全 | 高 | 4 | 2-3 天 | Plan A |
| [Plan C](./2026-04-25-plan-c-pr-archive-import.md) | Pr 档案导入补全 | 高 | 9 | 5-7 天 | Plan A |
| [Plan D](./2026-04-25-plan-d-agent-module.md) | Agent 代理商迁移 | 中 | 8 | 5-7 天 | Plan A + 架构决策 |

---

## 执行依赖图

```
Plan A (P0 阻塞性修复)
├── Task 1-3: ht_strategy_perform 表+Entity+Service  [串行]
├── Task 4:   HtTasksPerform 设备状态更新             [并行]
├── Task 5:   Mt 热力表级联更新                        [并行]
├── Task 6:   Mt 阀门级联更新                          [并行]
├── Task 7:   支付回调安全修复                          [并行]
├── Task 8:   AreaController 实现                      [并行]
├── Task 9:   Mt 查询端点补充                          [并行]
└── Task 10:  全量编译验证                             [最终]

Plan A 完成后 ↓ 可并行 ↓

Plan B (Ht 调控补全) ──┐
Plan C (Pr 档案导入)  ──┼── 可并行执行
Plan D (Agent 代理商) ──┘  （Plan D 需先完成架构决策）
```

---

## 各计划覆盖的问题

### Plan A 覆盖的 Critical 问题
1. HtTasksPerformMapper 核心功能缺失 → Task 4
2. ht_strategy_perform 表缺失 → Task 1-3
3. 支付回调签名校验缺失 → Task 7
4. 热力表/阀门级联更新缺失 → Task 5, 6
5. AreaController 仅骨架 → Task 8
6. Mt 热力表查询端点缺失 → Task 9

### Plan B 覆盖的 Important 问题
1. HtTasksController 缺失端点
2. 报修编号生成逻辑不一致
3. HtTasksPerformController 功能简化
4. HtScopeDtu/HtTasksPerformLs 评估

### Plan C 覆盖的 Critical + Important 问题
1. 导入功能完全缺失（7 个 Controller）
2. PrHeatArchive 核心业务缺失
3. 热表档案子类未迁移（8 个 Controller）
4. 日表生成未实现
5. 导出/趋势/OSS 处理等

### Plan D 覆盖的问题
1. 代理商模块 8 个 Controller 未迁移
2. 多租户策略冲突
3. 公司层级关系缺失

---

## Pr 收费核心模块的 Important 问题（单独列出）

以下问题分布在 Pr 核心模块中，建议在 Plan A 完成后穿插修复：

| # | 问题 | 优先级 | 建议 |
|---|------|--------|------|
| 1 | PrHouseController 导出导入未迁移 | P1 | 参考 Plan C 的导入框架 |
| 2 | PrHouseController 孤岛户功能未迁移 | P1 | 独立迁移 |
| 3 | PrStandardController 引用复制未迁移 | P2 | 独立迁移 |
| 4 | ReconciliationController 核心对账未实现 | P1 | 需第三方对账文档 |
| 5 | PrUserController 头像上传未实现 | P2 | 集成 OSS |

---

## 风险提示

1. **Plan D 架构决策阻塞**: 代理商模块的多租户映射策略需要在实施前确定，否则无法编码
2. **Plan C 工作量最大**: 导入模块 + 热表档案子类 + 核心业务逻辑，预计 5-7 天
3. **旧系统文档缺失**: 部分复杂业务逻辑（如 PrHeatArchive.manualControl 有 800 行）需要仔细理解
4. **前端适配**: 所有 API 路由已改为 RESTful 风格，前端需要同步修改
