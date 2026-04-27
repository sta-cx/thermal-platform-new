# 新老系统迁移全量重新审核 — 总体汇总报告

**审核日期**: 2026-04-26
**审核范围**: thermal-balance-backend (Spring Boot 2.2) → thermal-platform-new (Spring Boot 3.5)
**审核模式**: 逐 Controller 端点级精细对比
**基线对比**: 与之前 4 月 26 日的首次审核对比，反映最近 14 个提交的补齐效果

---

## 一、整体迁移状态

| 批次 | 模块 | 旧端点数 | 完全匹配 | 部分匹配 | 骨架 | 缺失 | 新增 |
|------|------|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | Pr 基础数据 | ~120 | 95 | 4 | 0 | 20 | 1 |
| 2 | Pr 设备档案 | ~132 | 62 | 35 | 8 | 22 | 5 |
| 3 | Pr 收费运维 | ~34 | 12 | 10 | 3 | 7 | 2 |
| 4 | Ht 热力调控 | ~57 | 51 | 4 | 0 | 2 | 0 |
| 5 | Mt 仪表设备 | ~54 | 28 | 12 | 0 | 4 | 10 |
| 6 | Agent 代理管理 | ~50 | 42 | 6 | 0 | 2 | 0 |
| 7 | 微信/支付 | ~40 | 0 | 1 | 28 | 8 | 3 |
| 8 | 系统管理 | ~47 | 25 | 17 | 0 | 4 | 3 |
| **合计** | | **~534** | **315** | **89** | **39** | **69** | **24** |

**总体迁移完成度**: ~59% 完全匹配，~17% 部分匹配，~7% 骨架，~13% 缺失，~4% 新增

### 与首次审核对比（4月26日前 vs 现在）

| 指标 | 首次审核 | 重新审核 | 变化 |
|------|---------|---------|------|
| 完全匹配率 | 48% | 59% | **+11%** |
| 缺失率 | 24% | 13% | **-11%** |
| Agent 模块 | 0% (完全未迁移) | 84% | **+84%** |
| 微信/支付 | 0% 完全匹配 | 0% 完全匹配 | 骨架数量增加 |
| Mt 仪表 | 80% | 52% | 端点粒度更精细(之前偏高) |

---

## 二、按优先级分类的关键发现

### P0 — 阻塞上线

#### 1. IoT 数据回调通道缺失
- **影响**: PrHeatValveArchive 的 `insertDataNbValve`(电信)、`insertDataMBusValve`(世达)、`signature`(移动) 三个端点
- **风险**: 设备数据无法上报入库，供热监控完全失效
- **状态**: 未迁移

#### 2. 微信/支付模块仍为骨架
- **影响**: 28 个端点仅有 Controller 骨架，Service 返回占位数据
- **核心缺失**: 微信支付回调、支付签名验证、小程序授权、对账逻辑
- **状态**: 标记为后续实现

#### 3. PrHeatArchive.manualControl 指令覆盖
- **影响**: 旧系统支持 adjust=1/2/3/4/5/6/7/27/28-1/28-2/29/30/51 共 13 种指令类型
- **风险**: 新系统 Service 层可能未覆盖所有指令类型，影响阀门/热表/DTU调控
- **状态**: 需验证 Service 层实现

### P1 — 功能完整性

#### 4. PrHeatValveArchive 大量端点缺失
- 旧系统 43+ 端点，新系统仅 11 个
- 缺失功能: 卡表管理、批量操作、第三方API(云谷/新奥)、蓝牙控制、信息同步
- 特别是 `setValveOCStatus`/`setValveOpening`/`setValveCycle` 等批量操作端点

#### 5. Pr 基础数据模块 — PrHouse 缺失端点
- 缺失 ~15 个端点: 导入导出、孤岛户管理、多条件查询、阀门热表关联
- `exportAll`/`importAll` 未迁移

#### 6. PrHeatControl / PrAbnormalRecord / PrAutoMachine — 骨架
- 3 个 Controller 仅有端点定义，Service 返回空

### P2 — 中期优化

#### 7. 导出行为变更
- 旧系统直接写 HttpServletResponse 流(EasyExcel)
- 新系统部分改为返回 `R<List>` 数据列表
- 前端需要适配新的导出方式

#### 8. 参数简化导致高级过滤丢失
- `realTimeData`/`zonghe` 缺少 stationId/partitionId 等高级参数
- PrExpense 部分查询端点参数简化

---

## 三、代码质量总评

### 架构改进（正面）

| 维度 | 旧系统 | 新系统 |
|------|--------|--------|
| Controller 职责 | 含大量业务逻辑(PrHeatValveArchive 1770行) | 委托Service层(Controller <200行) |
| HTTP 方法 | 全部 POST | 正确使用 GET/POST/PUT/DELETE |
| 权限控制 | 隐式 SecurityUtils.getUser() | @SaCheckPermission + @SaCheckLogin |
| 参数校验 | 手动 if-else | @Validated + Bean Validation |
| 操作日志 | 无 | @Log 注解 |
| 响应格式 | 混合(R/boolean/int/JSONObject) | 统一 R<T> |
| 数据传输 | 直接使用 Entity | Bo/Vo 分层 + MapstructUtils |
| 认证 | 自定义 SecurityUtils | Sa-Token JWT |

### 风险项

1. **IoT回调缺失**: 设备数据上报通道中断
2. **支付安全**: 微信支付回调缺少签名验证
3. **事务管理**: 收费/退费流程的事务正确性需验证
4. **第三方集成**: 云谷/新奥API硬编码token密钥(旧系统也有此问题)

---

## 四、迁移完成度热力图

```
Mt-仪表设备       ██████████████░░░░░░ 70%  (28/40 端点匹配)
Ht-热力调控       ██████████████████░░ 89%  (51/57 端点匹配)
Agent-代理管理    ████████████████░░░░ 84%  (42/50 端点匹配)
Pr-基础数据       ████████████████░░░░ 79%  (95/120 端点匹配)
Pr-收费运维       ██████████░░░░░░░░░░ 35%  (12/34 端点匹配)
Pr-设备档案       █████████░░░░░░░░░░░ 47%  (62/132 端点匹配)
Sys-系统管理      ██████████████░░░░░░ 53%  (25/47 端点匹配)
Wechat-微信支付   ░░░░░░░░░░░░░░░░░░░░  0%  (0/40 完全匹配，28骨架)
```

---

## 五、建议行动项

### 立即处理（P0 — 阻塞上线）

1. **实现 IoT 数据回调端点** — 电信/世达/移动三个平台的数据接收+解析+存储
2. **验证 manualControl Service 覆盖度** — 确认所有 13 种 adjust 类型的指令处理逻辑
3. **完成微信支付核心逻辑** — SDK集成 + 支付回调 + 签名验证

### 短期处理（P1 — 功能完整性）

4. 补齐 PrHeatValveArchive 缺失的批量操作端点(setValveOCStatus等)
5. 实现 PrHeatControl / PrAbnormalRecord / PrAutoMachine 业务逻辑
6. 迁移第三方集成API(云谷/新奥)
7. 补齐 PrHouse 导入导出功能
8. 补齐 PrExpense 缺失的收费相关端点

### 中期优化（P2）

9. 统一导出方式(全部使用 EasyExcel 写 response 流)
10. 补全高级过滤参数(realTimeData/zonghe)
11. 验证收费/退费事务正确性
12. 清理第三方API中的硬编码密钥

---

## 六、详细报告索引

| 文件 | 模块 | 旧端点数 |
|------|------|---------|
| [reaudit-agent1-pr-basic-device.md](reaudit-agent1-pr-basic-device.md) | Pr 基础数据（楼栋/房屋/单元/换热站/区域/标准/选项） | ~120 |
| [reaudit-agent1-pr-device.md](reaudit-agent1-pr-device.md) | Pr 设备档案（热表/阀门/DTU/温度/日表/月表/控制） | ~132 |
| [reaudit-agent2-pr-charge-ops.md](reaudit-agent2-pr-charge-ops.md) | Pr 收费运维（费用/账单/交易/导入/运维/用户） | ~34 |
| [reaudit-agent3-ht.md](reaudit-agent3-ht.md) | Ht 热力调控（策略/指令/告警/任务/维修） | ~57 |
| [reaudit-agent3-mt.md](reaudit-agent3-mt.md) | Mt 仪表设备（厂商/分类/电/水/气/热表） | ~54 |
| [reaudit-agent4-agent-wechat-sys.md](reaudit-agent4-agent-wechat-sys.md) | Agent代理+微信支付+系统管理 | ~137 |
