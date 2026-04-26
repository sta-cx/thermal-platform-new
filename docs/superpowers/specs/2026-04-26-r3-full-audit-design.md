# 第三轮全量重新审核 — 设计文档

**日期**: 2026-04-26
**目标**: 对新老系统所有端点进行全量深度审核（API + 业务逻辑 + 数据库层）
**基线**: 忽略前两轮审核结论，从头对比

---

## 一、审核范围

老系统: `D:/chonggou/thermal-balance-backend` (Spring Boot 2.2, 包名 `com.thermal`)
新系统: `D:/chonggou/thermal-platform-new` (Spring Boot 3.5, 包名 `org.sdkj`)

8 大功能模块, 约 534 个旧端点:

| 序号 | 模块 | 旧端点数(估) |
|------|------|:---:|
| 1 | Pr 基础数据（楼栋/单元/房屋/住户/换热站/区域/标准/选项） | ~120 |
| 2 | Pr 设备档案（热表/阀门/DTU/温度/抄表/日表/月表/控制） | ~132 |
| 3 | Pr 收费运维（费用/账单/交易/导入/运维/用户） | ~34 |
| 4 | Ht 热力调控（策略/指令/告警/任务/维修） | ~57 |
| 5 | Mt 仪表设备（厂商/分类/电/水/气/热表） | ~54 |
| 6 | Agent 代理管理（公司/物业/角色/用户/菜单） | ~50 |
| 7 | 微信/支付（授权/支付/对账/小程序） | ~40 |
| 8 | 系统管理（用户/角色/菜单/字典/首页） | ~47 |

---

## 二、审核方案：两轮串行

### 第一轮：API 覆盖度扫描（4 并行 Agent）

4 个 Agent 并行工作，逐 Controller 对比端点存在性。

**Agent 分工:**

| Agent | 审核模块 |
|-------|---------|
| Agent 1 | Pr 基础数据 + Pr 设备档案 |
| Agent 2 | Pr 收费运维 + Agent 代理管理 |
| Agent 3 | Ht 热力调控 + Mt 仪表设备 |
| Agent 4 | 微信支付 + 系统管理 |

**每个 Agent 的工作流程:**
1. 读取旧系统 Controller 文件，提取所有端点（路径 + HTTP 方法 + 方法名）
2. 读取新系统对应 Controller 文件，提取所有端点
3. 逐端点对比，标注状态

**端点状态分类:**
- 完全匹配: 路径/方法/参数语义一致
- 部分匹配: 端点存在但参数简化/行为变更
- 骨架: Controller 存在但 Service 返回占位数据
- 缺失: 新系统中无对应端点
- 新增: 新系统新增的端点（旧系统中无）

**每个 Agent 产出格式:**

端点级清单（Markdown 表格）：
```
| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
```

**汇总产出:** `docs/superpowers/audit-r3/round1-summary.md`
- 合并 4 份清单
- 按模块统计完成度百分比
- 标注"部分匹配"和"缺失"端点供第二轮聚焦

### 第二轮：深度 Service/DB 验证（4 并行 Agent）

基于第一轮结果，同一组 Agent 深入验证业务逻辑和数据库层。

**检查维度:**

1. **Service 层对比**
   - 读取旧系统 `service/impl/` 实现逻辑
   - 读取新系统 `service/impl/` 实现
   - 标注核心业务逻辑差异（计算规则、条件分支、异常处理）
   - 特别关注: 收费计算、阀门控制指令、IoT 数据解析

2. **DB 层对比**
   - 旧系统 `entity/` vs 新系统 `domain/` 字段对比
   - 旧系统 `mapper/` XML vs 新系统 Mapper XML 查询等价性
   - 检查是否有字段遗漏、类型变更、索引缺失

3. **风险标注**
   - 事务安全（收费/退费操作是否有事务保护）
   - 参数校验缺失（边界条件）
   - 数据一致性风险（多表操作无事务）
   - 安全风险（SQL 注入、未鉴权端点）

**每个 Agent 产出:** 模块审核报告（含 API + 逻辑 + DB 全部发现）

**最终汇总:** `docs/superpowers/audit-r3/final-summary.md`
- 8 大模块的迁移完成度热力图
- 按优先级排列的行动项（P0/P1/P2）
- 代码质量总评

---

## 三、产出文件结构

```
docs/superpowers/audit-r3/
├── round1-summary.md              # 第一轮汇总
├── round1-agent1-pr-basic-device.md
├── round1-agent2-pr-charge-agent.md
├── round1-agent3-ht-mt.md
├── round1-agent4-wechat-sys.md
├── round2-agent1-pr-basic-device.md
├── round2-agent2-pr-charge-agent.md
├── round2-agent3-ht-mt.md
├── round2-agent4-wechat-sys.md
└── final-summary.md               # 最终汇总报告
```

---

## 四、技术约定

- 老系统路径前缀: 无统一前缀，Controller 直接 `@RequestMapping("/xxx")`
- 新系统路径前缀: 热力 `@RequestMapping("/thermal/ht/*")`, 物业 `@thermal/property/*`, 仪表 `/thermal/meter/*`
- 老系统 HTTP 方法: 几乎全部 POST
- 新系统 HTTP 方法: 正确使用 RESTful（GET/POST/PUT/DELETE）
- 端点匹配以**语义等价**为准，不要求路径完全相同
