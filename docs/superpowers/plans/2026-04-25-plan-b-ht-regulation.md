# Plan B: Ht 热力调控模块补全实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补全 Ht 热力调控模块的所有 Important 级别问题，使调控功能达到可用状态

**Architecture:** 在 sdkj-thermal 模块中补充缺失的 Controller、Service 方法和 Mapper XML

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus, Quartz, Sa-Token

**前置条件:** Plan A (P0 阻塞性修复) 已完成

---

## 修复清单（来自审核报告 03-ht-regulation-audit.md）

### Important 问题

| # | 问题 | 涉及文件 |
|---|------|---------|
| I-1 | HtTasksController 缺少复杂查询和管理功能 | HtTasksController, IHtTasksService, HtTasksServiceImpl |
| I-2 | 报修编号生成逻辑从 YMD 格式改为 UUID | HtRepairServiceImpl |
| I-3 | HtTasksPerformController 功能简化过多 | HtTasksPerformController, IHtTasksPerformService |

### 未迁移功能

| 模块 | 说明 |
|------|------|
| HtScopeDtuController | DTU 控制范围管理 |
| HtTasksPerformLsController | 任务执行临时表 |

---

## Task 1: 补充 HtTasksController 缺失的端点

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtTasksService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksServiceImpl.java`

- [ ] **Step 1: 读取旧系统缺失方法**

读取 `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/HtTasksController.java`，找到以下方法：
- `pageDeviceList` — 按设备类型分页查询阀门配表列表
- `querySummary` — 汇总统计
- `getHouseOtherCode` / `updateOtherCode` — 其他代码管理
- `updateSdkdLog` — 重新设定开度日志

- [ ] **Step 2: 在 Service 接口和实现中添加方法**

按新系统风格实现上述业务逻辑。

- [ ] **Step 3: 在 Controller 中添加端点**

```
GET  /thermal/ht/tasks/deviceList        — 设备类型分页
GET  /thermal/ht/tasks/summary           — 汇总统计
GET  /thermal/ht/tasks/otherCode/{id}    — 其他代码查询
PUT  /thermal/ht/tasks/otherCode         — 其他代码更新
PUT  /thermal/ht/tasks/valveAngleLog     — 重新设定开度日志
```

- [ ] **Step 4: 编译验证并提交**

```bash
mvn compile -pl sdkj-modules/sdkj-thermal -am -q
git commit -m "feat(ht): add missing task management endpoints"
```

---

## Task 2: 统一报修编号生成规则

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtRepairServiceImpl.java`

- [ ] **Step 1: 读取旧系统编号生成逻辑**

读取旧系统 `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/HtRepairServiceImpl.java`，找到 `GengerateRecordNoYMD()` 方法。

- [ ] **Step 2: 在新系统中恢复 YMD 格式编号生成**

将编号生成改为旧系统的 `BX + yyyyMMdd + 4位序号` 格式。可以在 HtRepairServiceImpl 中添加私有方法：

```java
private String generateRepairNo() {
    String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
    String prefix = "BX" + dateStr;
    // 查询当天最大编号
    Long maxNo = baseMapper.selectMaxRepairNo(prefix);
    return prefix + String.format("%04d", (maxNo != null ? maxNo : 0) + 1);
}
```

需要在 Mapper 中添加 `selectMaxRepairNo` 方法。

- [ ] **Step 3: 编译验证并提交**

```bash
mvn compile -pl sdkj-modules/sdkj-thermal -am -q
git commit -m "fix(ht): restore YMD-format repair number generation"
```

---

## Task 3: 补充 HtTasksPerformController 功能

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksPerformController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtTasksPerformService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksPerformServiceImpl.java`

- [ ] **Step 1: 读取旧系统缺失的功能**

读取旧系统 `HtTasksPerformController` 和 `HtTasksPerformServiceImpl`，找到新系统缺失的功能：
- 状态统计
- 批量操作
- 复杂的档案更新调度逻辑

- [ ] **Step 2: 添加统计端点**

```
GET  /thermal/ht/tasksPerform/statistics/{taskId}  — 任务执行统计
GET  /thermal/ht/tasksPerform/statusSummary         — 状态汇总
```

- [ ] **Step 3: 实现业务逻辑并提交**

```bash
mvn compile -pl sdkj-modules/sdkj-thermal -am -q
git commit -m "feat(ht): add task perform statistics and status summary"
```

---

## Task 4: 评估 HtScopeDtu 和 HtTasksPerformLs 是否需要迁移

**Files:** 无代码变更，产出评估文档

- [ ] **Step 1: 读取旧系统两个 Controller**

读取:
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/HtScopeDtuController.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/HtTasksPerformLsController.java`

- [ ] **Step 2: 评估业务必要性**

分析这两个功能是否被其他已迁移逻辑替代。在审核报告 03 中补充评估结论。

如果需要迁移，后续单独创建子任务。
