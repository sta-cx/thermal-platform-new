# 第三轮全量审核 — 最终汇总报告

**审核日期**: 2026-04-26
**审核范围**: thermal-balance-backend (Spring Boot 2.2) → thermal-platform-new (Spring Boot 3.5)
**审核模式**: 两轮串行 — Round 1 API 覆盖度扫描 + Round 2 Service/DB 深度对比
**总端点数**: 729 个旧端点

---

## 一、整体迁移状态

| 模块 | 旧端点数 | MATCH | PARTIAL | SKELETON | MISSING | NEW | 匹配率 |
|------|---------|-------|---------|----------|---------|-----|--------|
| Pr 基础数据 | 133 | 89 | 21 | 8 | 15 | 0 | 66.9% |
| Pr 设备档案 | 168 | 98 | 32 | 18 | 20 | 0 | 58.3% |
| Pr 收费运维 | 136 | 78 | 24 | 22 | 12 | 8 | 57.4% |
| Agent 代理管理 | 47 | 28 | 10 | 6 | 3 | 5 | 59.6% |
| Ht 热力调控 | 87 | 62 | 8 | 3 | 10 | 7 | 71.3% |
| Mt 仪表设备 | 46 | 34 | 4 | 2 | 4 | 3 | 73.9% |
| 微信/支付 | 34 | 8 | 3 | 23 | 0 | 0 | 23.5% |
| 系统管理 | 78 | 0 | 0 | 42 | 36 | 0 | 0.0% |
| **合计** | **729** | **397** | **102** | **124** | **100** | **26** | **54.5%** |

**MATCH+PARTIAL 覆盖率**: 68.5% (499/729)

---

## 二、迁移完成度热力图

```
Mt 仪表设备       ████████████████░░░░ 73.9%  (34/46 端点匹配)
Ht 热力调控       ███████████████░░░░░ 71.3%  (62/87 端点匹配)
Pr 基础数据       █████████████░░░░░░░ 66.9%  (89/133 端点匹配)
Agent 代理管理    █████████████░░░░░░░ 59.6%  (28/47 端点匹配)
Pr 设备档案       ████████████░░░░░░░░ 58.3%  (98/168 端点匹配)
Pr 收费运维       ████████████░░░░░░░░ 57.4%  (78/136 端点匹配)
微信/支付         █████░░░░░░░░░░░░░░░ 23.5%  (8/34 端点匹配)
系统管理          ░░░░░░░░░░░░░░░░░░░░  0.0%  (0/78 端点匹配)
```

---

## 三、Service 层关键发现

### P0 — 阻塞上线（5项）

| # | 模块 | 问题 | 影响 |
|---|------|------|------|
| 1 | **Pr 收费运维** | 费用生成规则仅实现 1/6（缺按年/按季/按半年/固定期限/自然月） | 无法正确生成多种周期费用 |
| 2 | **Pr 收费运维** | 阶梯单价计算和金额公式计算为占位实现（TODO Phase 5b） | 费用计算不准确 |
| 3 | **微信支付** | 全部支付逻辑为骨架，无 SDK 集成/签名验证/幂等性 | 线上支付不可用 |
| 4 | **Ht 热力调控** | HtStrategy Entity 缺失大量策略参数字段（温度/流量/系数） | 策略计算无法执行 |
| 5 | **Ht 热力调控** | HtScopeDtu（DTU 广播控制）Entity/功能完全不存在 | DTU 广播控制功能缺失 |

### P1 — 功能完整性（8项）

| # | 模块 | 问题 | 影响 |
|---|------|------|------|
| 6 | **Pr 设备档案** | PrHeatArchive.manualControl adjust 参数不全（缺1~6, 8~26 等基础指令类型） | 部分阀门控制指令无法下发 |
| 7 | **Pr 基础数据** | 孤岛户功能（queryGDH/setGDH）完全缺失 | 热力平衡分析核心功能缺失 |
| 8 | **Pr 设备档案** | NB/MBus 阀门数据接收缺少房屋温度反写逻辑 | 温度数据不同步到 pr_house |
| 9 | **Pr 收费运维** | 滞纳金计算未实现（4种计算方式） | 逾期费用无法自动计算 |
| 10 | **Pr 设备档案** | PrAutoMachine 支付回调返回 fail | 自助缴费机线上不可用 |
| 11 | **Ht 热力调控** | HtTasksServiceImpl.insertHtTasksPerformDtu 方法未迁移 | DTU 任务执行流程不完整 |
| 12 | **系统管理** | 78 个端点 0 匹配（42 骨架 + 36 缺失），需确认 sdkj-system 基座覆盖度 | 系统管理功能可能不完整 |
| 13 | **Agent 代理** | 角色菜单管理功能缺失 | Agent 权限控制不完整 |

### P2 — 中期优化（6项）

| # | 模块 | 问题 |
|---|------|------|
| 14 | Pr 收费运维 | 操作日志缺失，审计追踪能力下降 |
| 15 | Pr 设备档案 | 交易记录结构变化（PrTransactionDetail → PrTransactionRecordSub），需确认等价性 |
| 16 | Pr 基础数据 | 导入逻辑简化（旧系统关联 UserHouse/HouseChange/User 多表操作 → 新系统单表） |
| 17 | Pr 设备档案 | 热表更换余额转移逻辑不完整（缺 PrTransactionDetail 明细） |
| 18 | Pr 设备档案 | realTimeData/zonghe 查询参数简化，去除了高级过滤 |
| 19 | Pr 收费运维 | 费用精度处理简化（旧系统支持四舍五入/进位/截位三种模式） |

---

## 四、DB 层关键发现

### Entity 字段缺失（高风险）

| Entity | 缺失情况 | 影响 |
|--------|---------|------|
| **HtStrategy** | 缺失 ~30 个策略参数字段（adjustBasis, stride, priority, valveMin/Max, inTemp/outTemp/roomTemp, bianhxs~zchxs 等供热系数） | 策略计算功能无法执行 |
| **HtScopeDtu** | 整个 Entity 不存在 | DTU 广播控制不可用 |
| **PrExpense** | 疑似缺失滞纳金/阶梯相关字段 | 费用计算不完整 |

### Mapper 查询差异

| 模块 | 差异 | 影响 |
|------|------|------|
| PrExpense | 动态 SQL 公式计算未迁移 | 自定义费用公式不可用 |
| PrHouse | 高级过滤查询参数缺失 | 多条件组合查询功能受限 |
| HtTasks | DTU 相关查询未迁移 | DTU 任务调度不可用 |

---

## 五、代码质量总评

### 架构改进（正面）

| 维度 | 旧系统 | 新系统 |
|------|--------|--------|
| HTTP 方法 | 几乎全部 POST | 正确使用 GET/POST/PUT/DELETE |
| 权限控制 | 隐式 SecurityUtils.getUser() | @SaCheckPermission + @SaCheckLogin |
| 参数校验 | 手动 if-else | @Validated + Bean Validation |
| 操作日志 | 无 | @Log 注解（但收费操作日志未接入） |
| 响应格式 | 混合(R/boolean/int/JSONObject) | 统一 R<T> |
| 数据传输 | 直接使用 Entity | Bo/Vo 分层 + MapstructUtils |
| 认证 | 自定义 SecurityUtils | Sa-Token JWT |
| 批量操作 | 分散在各 Controller | 统一通过 HtTasksPerform 调度 |

### 风险项

1. **支付安全**: 微信支付无签名验证、无幂等性、无持久化（使用内存 Map）
2. **事务管理**: 收费/退费流程的事务保护不完整
3. **第三方集成**: IoT 数据回调、微信 SDK、云谷/新奥 API 均未集成
4. **Entity 字段缺失**: HtStrategy 缺失 ~30 个字段，可能影响已有数据库表的映射
5. **骨架风险**: 大量 Controller 仅有骨架实现（124 个端点），容易误认为功能完整

---

## 六、建议行动项（按优先级）

### P0 — 立即处理（阻塞上线）

1. **补全 PrExpense 费用生成规则**: 实现 6 种生成规则 + 阶梯单价计算
2. **集成微信支付 SDK**: 实现支付创建/回调验签/退款/幂等性
3. **修复 HtStrategy Entity**: 补充策略参数字段映射
4. **实现 HtScopeDtu 模块**: Entity + Mapper + Service + Controller
5. **验证 sdkj-system 覆盖度**: 确认系统管理 78 端点是否由基座完全替代

### P1 — 短期处理

6. 补全 PrHeatArchive manualControl 所有 adjust 指令类型
7. 实现孤岛户功能（queryGDH/setGDH）
8. 补全 NB/MBus 阀门数据接收的温度反写逻辑
9. 实现滞纳金计算
10. 实现 PrAutoMachine 支付回调
11. 实现 Agent 角色菜单管理

### P2 — 中期优化

12. 补全收费操作日志
13. 统一导出方式
14. 补全高级过滤参数
15. 补全费用精度处理

---

## 七、详细报告索引

| 文件 | 轮次 | 模块 | 端点数 |
|------|------|------|--------|
| [round1-summary.md](round1-summary.md) | Round 1 汇总 | 全部 8 模块 | 729 |
| [round1-agent1-pr-basic-device.md](round1-agent1-pr-basic-device.md) | Round 1 | Pr 基础数据 + Pr 设备档案 | 301 |
| [round1-agent2-pr-charge-agent.md](round1-agent2-pr-charge-agent.md) | Round 1 | Pr 收费运维 + Agent 代理管理 | 183 |
| [round1-agent3-ht-mt.md](round1-agent3-ht-mt.md) | Round 1 | Ht 热力调控 + Mt 仪表设备 | 133 |
| [round1-agent4-wechat-sys.md](round1-agent4-wechat-sys.md) | Round 1 | 微信支付 + 系统管理 | 112 |
| [round2-agent1-pr-basic-device.md](round2-agent1-pr-basic-device.md) | Round 2 | Pr 基础数据 + Pr 设备档案 Service/DB | — |
| [round2-agent2-pr-charge-agent.md](round2-agent2-pr-charge-agent.md) | Round 2 | Pr 收费运维 + Agent 代理管理 Service/DB | — |
| [round2-agent3-ht-mt.md](round2-agent3-ht-mt.md) | Round 2 | Ht 热力调控 + Mt 仪表设备 Service/DB | — |
| [round2-agent4-wechat-sys.md](round2-agent4-wechat-sys.md) | Round 2 | 微信支付 + 系统管理 Service/DB | — |
