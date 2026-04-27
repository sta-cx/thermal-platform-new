# 新老系统迁移全面审核 — 总体汇总报告

**审核日期**: 2026-04-26
**审核范围**: thermal-balance-backend (Spring Boot 2.2) → thermal-platform-new (Spring Boot 3.5)
**审核模式**: 逐 Controller 精细对比（API 覆盖度 + 业务逻辑 + 代码质量）

---

## 一、整体迁移状态

| 批次 | 模块 | 旧 Controller | 新 Controller | 完全匹配 | 部分匹配 | 骨架/缺失 |
|------|------|:---:|:---:|:---:|:---:|:---:|
| Batch 1 | Pr-基础数据 | 16 | 14 | 8 | 5 | 3 |
| Batch 1 | Pr-设备档案 | 13 | 13 | 0 | 9 | 4 |
| Batch 1 | Pr-收费运维 | 33 | 33 | 24 | 7 | 2 |
| Batch 2 | Ht-供热策略 | 11 | 9 | 4 | 3 | 4 |
| Batch 2 | Mt-仪表设备 | 10 | 10 | 8 | 2 | 0 |
| Batch 2 | Agent-代理管理 | 6 | 0 | 0 | 0 | 6 (全部未迁移) |
| Batch 3 | Wechat-微信支付 | 7 | 7 | 0 | 1 | 6 |
| Batch 3 | Sys-系统管理 | 13 | 11 | 8 | 3 | 2 |
| **合计** | | **109** | **97** | **52** | **30** | **27** |

**总体迁移完成度**: ~48% 完全匹配，~28% 部分匹配，~24% 骨架/缺失/未迁移

---

## 二、按优先级分类的关键发现

### 高优先级（阻塞上线）

#### 1. Agent 代理管理模块 — 完全未迁移
- **影响**: 6 个 Controller（AgentCompany/Meter/Property/PropertyMenu/Role/User）全部未迁移
- **涉及端点**: ~30+ 个 API
- **状态**: 新系统中无任何对应实现

#### 2. 微信/支付模块 — 骨架迁移
- **影响**: 5/7 个 Controller 仅有骨架（Controller 存在，Service 返回占位数据）
- **核心缺失**: 微信 SDK 未集成、支付回调未实现、对账逻辑未完成
- **状态**: 标记为 Phase 6 待实现

#### 3. Pr-设备档案模块 — 核心同步逻辑缺失
- **影响**: 9/13 个 Controller 为部分匹配
- **核心缺失**: 设备数据同步（IoT 下行指令、数据上报处理）、导入导出功能
- **3 个骨架**: PrHeatControl、PrAutoMachine、PrAbnormalRecord

#### 4. PrHouseController — 严重功能缺失
- **缺失 ~20 个端点**: 导入导出、孤岛户管理、微信绑定、多条件查询、阀门热表关联查询
- 仅保留基础 CRUD

### 中优先级（影响完整功能）

#### 5. 运维管理模块 — 未迁移
- PrInspectionPerson/Plan/Record（巡检）
- PrRepairPerson/Record（维修）
- PrScheduling（排班）
- PrNotice（通知）
- Push/Task/Tools（辅助功能）

#### 6. 数据导入模块 — 未迁移
- 8 个 PrImport 系列全部未迁移
- 涵盖：基础数据/热表/单元热表/阀门/温度/历史数据导入

#### 7. PrCompany/PrRole — 疑似迁移至系统模块
- 17 + 13 = 30 个端点在 thermal 模块中未找到
- 可能由 sdkj-system 的 SysTenant/SysRole 等替代，但需验证功能等价性

#### 8. Ht 供热策略 — 部分功能缺失
- HtScopeDtuController（DTU 范围管理）未迁移
- HtTasksPerformLsController（任务执行历史）未迁移
- HtTasksController 缺失 3 个端点（SDK 指令下发相关）

### 低优先级（可后续补齐）

- PrBuilding: getBuildingNumByUserId
- PrHouseChange: getHouseByRoomNum
- PrOptionsHeat: getOptionById
- PrPrintTemplate: downloadTemplate, getTemplateByName
- PrUseCardLog（写卡日志）
- PrWechatBindRecord（微信绑定记录）

---

## 三、代码质量总评

### 正面评价（新系统改进）
- **RESTful 规范**: 旧系统全部 POST → 新系统正确使用 GET/POST/PUT/DELETE
- **权限控制**: 全面使用 Sa-Token `@SaCheckPermission` 替代旧系统隐式检查
- **参数校验**: 使用 `@Validated` + Bean Validation
- **操作日志**: 全面使用 `@Log` 注解
- **统一响应**: 使用 `R<T>` 统一响应格式
- **分层清晰**: BO/VO 模式 + MapstructUtils 类型转换
- **多租户**: 内置租户隔离（从 Sa-Token 上下文获取 companyId）

### 风险项
1. **支付安全**: 微信支付回调缺少签名验证
2. **事务管理**: 收费/退费流程的事务正确性需验证
3. **骨架风险**: 大量 Controller 仅有骨架实现，返回占位数据，容易误认为功能完整
4. **SQL 注入**: 部分动态查询使用字符串拼接（需逐个验证）

---

## 四、迁移完成度热力图

```
Mt-仪表设备       ████████████████████ 80%  (8/10 完全匹配)
Pr-收费运维       ████████████████░░░░ 73%  (24/33 完全匹配)
Pr-基础数据       ██████████░░░░░░░░░░ 50%  (8/16 完全匹配)
Ht-供热策略       ████████░░░░░░░░░░░░ 36%  (4/11 完全匹配)
Sys-系统管理      ████████████████░░░░ 62%  (8/13 完全匹配)
Pr-设备档案       ░░░░░░░░░░░░░░░░░░░░  0%  (0/13 完全匹配，9 部分匹配)
Wechat-微信支付   ░░░░░░░░░░░░░░░░░░░░  0%  (0/7 完全匹配，1 部分匹配)
Agent-代理管理    ░░░░░░░░░░░░░░░░░░░░  0%  (完全未迁移)
```

---

## 五、建议行动项

### 立即处理（阻塞上线）
1. **实现 Agent 代理管理模块**（6 个 Controller, ~30 端点）
2. **完成微信/支付模块业务逻辑**（SDK 集成 + 支付回调 + 对账）
3. **实现 Pr-设备档案核心同步逻辑**（IoT 数据上报/下发）

### 短期处理（功能完整性）
4. 补齐 PrHouse 缺失的 ~20 个端点
5. 实现运维管理模块（巡检/维修/排班/通知）
6. 实现数据导入模块（8 个 PrImport Controller）
7. 验证 PrCompany/PrRole 是否由系统模块完全替代

### 中期优化
8. 验证收费/退费流程事务正确性
9. 补全支付回调签名验证
10. 完成前端 API 调用同步更新（路径/方法变更）

---

## 六、详细报告索引

| 文件 | 模块 |
|------|------|
| [batch1-pr-basic-audit.md](batch1-pr-basic-audit.md) | Pr-基础数据（楼栋/单元/房屋/住户/换热站/区域） |
| [batch1-pr-device-audit.md](batch1-pr-device-audit.md) | Pr-设备档案（热表/阀门/DTU/温度/抄表/日/月） |
| [batch1-pr-charge-audit.md](batch1-pr-charge-audit.md) | Pr-收费运维（费用/账单/交易/导入/运维） |
| [batch2-ht-audit.md](batch2-ht-audit.md) | Ht-供热策略（策略/指令/告警/任务/维修） |
| [batch2-mt-audit.md](batch2-mt-audit.md) | Mt-仪表设备（厂商/分类/电/水/气/热表） |
| [batch2-agent-audit.md](batch2-agent-audit.md) | Agent-代理管理（公司/物业/角色/用户） |
| [batch3-wechat-audit.md](batch3-wechat-audit.md) | Wechat-微信支付（授权/支付/对账/小程序） |
| [batch3-sys-audit.md](batch3-sys-audit.md) | Sys-系统管理（用户/角色/菜单/字典/首页） |
