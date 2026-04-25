# Migration Audit Design: thermal-balance-backend → thermal-platform-new

**Date**: 2026-04-25
**Scope**: Full audit of 6 functional modules (excluding WeChat ecosystem)
**Approach**: Module-based parallel agent audit

## Audit Modules

| # | Module | Old Controllers | New Module | Split |
|---|--------|----------------|------------|-------|
| 1 | Pr 物业/收费-核心 | ~25 (收费/交易/账户/房屋/对账) | sdkj-thermal | Agent 1 |
| 2 | Pr 物业/收费-档案导入 | ~25 (热表档案/导入/抄表/巡检/打印) | sdkj-thermal | Agent 2 |
| 3 | Ht 热力调控 | ~12 (策略/报警/指令/报修/任务) | sdkj-thermal | Agent 3 |
| 4 | Agent 代理商 | ~10 (代理商公司/用户/角色/菜单) | sdkj-thermal | Agent 4 |
| 5 | Mt 仪表管理 | ~11 (仪表档案/厂商/分类) | sdkj-meter | Agent 5 |
| 6 | 系统管理+其他 | ~15 (用户/角色/字典/公司/推送/对账) | sdkj-system + sdkj-thermal | Agent 6 |

## Per-Agent Audit Checklist

### Part 1: Feature Completeness Inventory
- Enumerate all Controllers/Services/Mappers in old system for the module
- Match each to new system counterpart
- Tag status: `migrated` / `partial` / `not-migrated` / `deprecated`

### Part 2: Migrated Code Review
- API compatibility: route path, HTTP method, params, response structure
- Business logic fidelity: Service layer core flow comparison, flag logic differences
- Code quality: exception handling, transaction management, SQL injection risks, hardcoded values

### Part 3: Report Output
- Markdown report per module in `docs/superpowers/audit/`
- Format: migration status table → per-item audit results → unmigrated list with priority → issues (Critical/Important/Minor)

## Deliverables

```
docs/superpowers/audit/
├── 01-pr-core-audit.md
├── 02-pr-archive-import-audit.md
├── 03-ht-regulation-audit.md
├── 04-agent-management-audit.md
├── 05-meter-audit.md
└── 06-system-misc-audit.md
```

## Execution

- 6 agents dispatched in parallel via TeamCreate
- Each agent uses Explore type to scan both codebases independently
- After all agents complete, findings summarized inline with action items

## Excluded

- WeChat ecosystem (WechatAuth, WechatPay, WxMa, WxPortal) — not priority
