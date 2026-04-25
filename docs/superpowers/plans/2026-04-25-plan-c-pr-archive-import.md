# Plan C: Pr 档案导入模块补全实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补全 Pr 物业/收费档案导入模块的核心业务逻辑和缺失 Controller

**Architecture:** 在 sdkj-thermal 模块中补充导入框架、热表档案子类 Controller 和核心业务逻辑

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus, EasyExcel, Sa-Token, Aliyun OSS

**前置条件:** Plan A (P0 阻塞性修复) 已完成

---

## 修复清单（来自审核报告 02-pr-archive-import-audit.md）

### Critical 问题

| # | 问题 | 说明 |
|---|------|------|
| C-1 | 导入功能完全缺失 | 7 个 PrImport*Controller 只有骨架 |
| C-2 | PrHeatArchiveController 核心业务缺失 | 仪表更换/充值/手动控制等 |
| C-3 | 热表档案子类未迁移 | 8 个 Controller（DTU/阀门/温控器等） |
| C-4 | PrHeatDailyController 日表生成未实现 | 5 步复杂逻辑 |

### Important 问题

| # | 问题 | 说明 |
|---|------|------|
| I-1 | PrHeatReadingController 导出和趋势缺失 | 大文件分批处理 |
| I-2 | PrRepairRecordController OSS 图片处理缺失 | 阿里云 OSS 路径转换 |
| I-3 | PrOptionsHeatController 未迁移 | 热力配置基础 |
| I-4 | PrHeatArchiveController 缺少报表查询 | 综合/历史/收费报表 |

---

## 实施阶段

### Phase C1: 导入框架（建议最先实施）

## Task 1: 创建统一导入基础服务

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IBaseImportService.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/BaseImportServiceImpl.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/ImportResult.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/ImportPreview.java`

- [ ] **Step 1: 读取旧系统导入模式**

读取旧系统一个完整的导入 Controller 及其 Service：
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/PrImportHeatController.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/PrImportHeatServiceImpl.java`

提炼通用流程：
1. `downloadTemplate()` — 下载含已有数据的 Excel 模板
2. `checkUnsubmitted()` — 检查是否有未提交数据
3. `importData()` — 解析 Excel 写入临时表
4. `updateHouseId()` — 匹配房屋 ID
5. `check()` — 数据校验（仪表/单价匹配）
6. `getUnmatched()` — 返回未匹配记录
7. `submitData()` — 从临时表提交到正式表
8. `deleteData()` — 清理临时数据

- [ ] **Step 2: 创建 ImportResult DTO**

```java
@Data
public class ImportResult {
    private int totalRows;
    private int successRows;
    private int failedRows;
    private List<String> errorMessages;
    private List<Map<String, Object>> unmatchedRecords;
}
```

- [ ] **Step 3: 创建 IBaseImportService 接口**

定义通用的模板下载、数据导入、校验、提交、清理方法。

- [ ] **Step 4: 编译验证并提交**

```bash
mvn compile -pl sdkj-modules/sdkj-thermal -am -q
git commit -m "feat(thermal): add base import service framework"
```

---

## Task 2: 实现 PrImportHeatController 完整导入逻辑

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrImportHeatController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrImportHeatServiceImpl.java`（如存在）

- [ ] **Step 1: 迁移旧系统导入逻辑**

从旧系统 `PrImportHeatServiceImpl` 迁移完整的 8 步导入流程到新系统。

- [ ] **Step 2: 编译验证并提交**

```bash
git commit -m "feat(thermal): implement PrImportHeat full import workflow"
```

---

## Task 3: 实现其余导入 Controller

**Files:** PrImportValveController, PrImportHistoryController, PrImportRecordController 等

- [ ] **Step 1: 依次读取旧系统各导入 Service**
- [ ] **Step 2: 按 Task 2 的模式实现每个导入 Controller**
- [ ] **Step 3: 逐个编译验证并提交**

每个导入模块一个 commit。

---

### Phase C2: PrHeatArchive 核心业务

## Task 4: 实现 PrHeatArchiveController 核心功能

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatArchiveServiceImpl.java`

- [ ] **Step 1: 读取旧系统核心功能**

读取旧系统 `PrHeatArchiveController` line 164-1441 范围的方法：
- `replaceHeatMeter` — 仪表更换（含余额处理）
- `realTimeData` — 实时数据查询
- `zonghe` — 综合查询
- `manualControl` — 手动控制（line 312-1113，最复杂）
- `setValveGroupParam` — 设置阀门组号
- `xunce` — 巡测
- `exportAll` / `importData` — 导出导入
- `findMeter` / `recharge` — 充值查询和仪表充值
- `selectReport` / `selectMeterReport` — 报表

- [ ] **Step 2: 按优先级实现核心方法**

优先级排序：
1. `replaceHeatMeter`（涉及财务）
2. `recharge`（涉及交易记录）
3. `manualControl`（调控核心）
4. `realTimeData`（基础查询）
5. 其余功能

- [ ] **Step 3: 编译验证并提交**

```bash
git commit -m "feat(thermal): implement PrHeatArchive core business logic"
```

---

### Phase C3: 热表档案子类

## Task 5: 迁移热表档案子类 Controller（8 个）

**Files:** 以下 Controller 及其 Service/Mapper：

| Controller | 说明 | 优先级 |
|-----------|------|--------|
| PrHeatDtuArchiveController | DTU 档案 | High |
| PrHeatValveArchiveController | 阀门档案 | High |
| PrHeatHotArchiveController | 热表档案 | High |
| PrHeatCommandValveArchiveController | 指令阀门 | Medium |
| PrHeatCommandUnitValveArchiveController | 指令单元阀门 | Medium |
| PrHeatTempArchiveController | 温控器 | Medium |
| PrHeatUnitHotArchiveController | 单元热表 | Medium |
| PrHeatUnitValveArchiveController | 单元阀门 | Medium |

- [ ] **Step 1: 逐个读取旧系统 Controller 和 Service**
- [ ] **Step 2: 确认新系统已有骨架，补充业务逻辑**
- [ ] **Step 3: 每完成 2-3 个 Controller 提交一次**

---

### Phase C4: 其他补全

## Task 6: 实现 PrHeatDailyController 日表生成

- 迁移旧系统 5 步日表生成逻辑

## Task 7: 补充 PrHeatReadingController 导出和趋势

- 大文件分批导出（每批 2000 条）
- 阀门趋势数据查询

## Task 8: 补充 PrRepairRecordController OSS 图片处理

- 阿里云 OSS 路径转换逻辑

## Task 9: 迁移 PrOptionsHeatController

- 热力配置（电信 IoT 配置、流水号配置等）
