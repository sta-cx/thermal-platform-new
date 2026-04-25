# Plan A: P0 阻塞性修复实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复所有阻止系统基本运行的致命问题，使新系统达到可测试状态

**Architecture:** 在现有 sdkj-thermal 和 sdkj-meter 模块中补充缺失的业务逻辑和数据库表，不改变已有的包结构和框架约定

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus, MySQL 8.0, Sa-Token, Quartz

**前置条件:** 已完成迁移审核报告，见 `docs/superpowers/audit/` 目录

---

## File Structure

### 新建文件
- `sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksPerformMapper.xml` — 执行记录复杂 SQL（档案更新/回读数据处理）
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/HtStrategyPerform.java` — 策略执行顺序实体
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/bo/HtStrategyPerformBo.java` — 策略执行顺序 Bo
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/vo/HtStrategyPerformVo.java` — 策略执行顺序 Vo
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtStrategyPerformMapper.java` — 策略执行顺序 Mapper
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtStrategyPerformService.java` — 策略执行顺序 Service 接口
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtStrategyPerformServiceImpl.java` — 策略执行顺序 Service 实现
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtStrategyPerformController.java` — 策略执行顺序 Controller

### 修改文件
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtTasksPerformMapper.java` — 添加设备档案更新方法
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksPerformServiceImpl.java` — 补充业务逻辑
- `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java` — 添加级联更新
- `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtTcValveServiceImpl.java` — 添加级联更新
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java` — 移除不安全的支付回调骨架
- `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AreaController.java` — 实现省市区查询

### SQL 脚本
- `script/sql/phase6_p0_fixes.sql` — 新增表和字段

---

## Task 1: 创建 ht_strategy_perform 表

**Files:**
- Create: `script/sql/phase6_p0_fixes.sql`

- [ ] **Step 1: 读取旧系统 ht_strategy_perform 表结构**

读取旧系统相关 Entity：
`D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/entity/HtStrategyPerform.java`

- [ ] **Step 2: 创建 SQL 脚本**

```sql
-- 策略执行顺序表
CREATE TABLE IF NOT EXISTS `ht_strategy_perform` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `command_index` int DEFAULT NULL COMMENT '命令顺序',
  `strategy_id` bigint DEFAULT NULL COMMENT '策略ID',
  `instruction_id` bigint DEFAULT NULL COMMENT '指令ID',
  `intervall` int DEFAULT NULL COMMENT '时间间隔(秒)',
  `xunhuan` int DEFAULT NULL COMMENT '循环次数',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='策略执行顺序表';
```

- [ ] **Step 3: 提交**

```bash
git add script/sql/phase6_p0_fixes.sql
git commit -m "feat(sql): add ht_strategy_perform table"
```

---

## Task 2: 创建 HtStrategyPerform 实体和 Mapper

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/HtStrategyPerform.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/bo/HtStrategyPerformBo.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/vo/HtStrategyPerformVo.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtStrategyPerformMapper.java`

- [ ] **Step 1: 参考旧系统 Entity 创建新 Entity**

读取旧系统文件 `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/entity/HtStrategyPerform.java`，然后参考新系统中已有的 Entity（如 `HtStrategy.java`）的注解风格创建：

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy_perform")
@AutoMapper(target = HtStrategyPerformVo.class)
public class HtStrategyPerform extends TenantEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer commandIndex;
    private Long strategyId;
    private Long instructionId;
    private Integer intervall;
    private Integer xunhuan;
}
```

Bo 和 Vo 类参考同目录下已有文件的模式创建。

- [ ] **Step 2: 创建 Mapper 接口**

```java
@Mapper
public interface HtStrategyPerformMapper extends BaseMapperPlus<HtStrategyPerform, HtStrategyPerformVo> {
}
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/HtStrategyPerform.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/bo/HtStrategyPerformBo.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/vo/HtStrategyPerformVo.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtStrategyPerformMapper.java
git commit -m "feat(ht): add HtStrategyPerform entity and mapper"
```

---

## Task 3: 创建 HtStrategyPerform Service 和 Controller

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtStrategyPerformService.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtStrategyPerformServiceImpl.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtStrategyPerformController.java`

- [ ] **Step 1: 读取旧系统 Service 和 Controller**

读取旧系统文件获取业务逻辑：
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/HtStrategyPerformServiceImpl.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/HtStrategyPerformController.java`

- [ ] **Step 2: 参考 HtStrategyService 创建 Service 接口和实现**

按新系统已有的 `IHtStrategyService` / `HtStrategyServiceImpl` 的模式创建，实现以下核心方法：
- `queryByStrategyId(Long strategyId)` — 按策略查询执行顺序列表
- `insertBatch(Long strategyId, List<HtStrategyPerformBo> list)` — 批量保存
- `deleteByStrategyId(Long strategyId)` — 按策略删除

- [ ] **Step 3: 创建 Controller**

路由前缀: `/thermal/ht/strategy-perform`
参考 `HtStrategyController` 的注解风格。

- [ ] **Step 4: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtStrategyPerformService.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtStrategyPerformServiceImpl.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtStrategyPerformController.java
git commit -m "feat(ht): add HtStrategyPerform service and controller"
```

---

## Task 4: 补充 HtTasksPerformMapper 设备状态更新方法

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtTasksPerformMapper.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksPerformMapper.xml`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksPerformServiceImpl.java`

- [ ] **Step 1: 读取旧系统 HtTasksPerformMapper**

读取旧系统文件：
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/mapper/HtTasksPerformMapper.java`
- `D:/chonggou/thermal-balance-backend/src/main/resources/mapper/agent/HtTasksPerformMapper.xml`

重点关注以下方法：
- `updateValveArchive` / `updateValveArchiveBatch` — 阀门档案更新
- `updateUnitValveArchive` — 单元阀门档案更新
- `updateTempArchive` — 温度档案更新
- `updateHotArchive` / `updateUnitHotArchive` — 热力档案更新
- `updateDtu` / `updateDtuHot` — DTU 状态更新
- `insertReading` — 插入回读数据
- `updateValveHouse` / `updateValveUnit` — 更新房屋/单元阀门状态

- [ ] **Step 2: 在新系统 Mapper 接口添加方法声明**

在 `HtTasksPerformMapper.java` 中添加核心方法（按旧系统 SQL 适配新表名和字段）：

```java
int updateValveArchive(@Param("perform") HtTasksPerform perform);
int updateValveArchiveBatch(@Param("list") List<HtTasksPerform> list);
int updateUnitValveArchive(@Param("perform") HtTasksPerform perform);
int updateTempArchive(@Param("perform") HtTasksPerform perform);
int updateHotArchive(@Param("perform") HtTasksPerform perform);
int updateUnitHotArchive(@Param("perform") HtTasksPerform perform);
int updateDtu(@Param("perform") HtTasksPerform perform);
int insertReading(@Param("perform") HtTasksPerform perform);
int updateValveHouse(@Param("perform") HtTasksPerform perform);
int updateValveUnit(@Param("perform") HtTasksPerform perform);
```

- [ ] **Step 3: 创建 Mapper XML，从旧系统 SQL 迁移**

将旧系统 `HtTasksPerformMapper.xml` 中的 SQL 逐个迁移，适配新表名（如有变化）和字段名。
注意：旧系统使用 `#{}` 占位符，新系统保持一致。

- [ ] **Step 4: 在 Service 实现中补充调用逻辑**

在 `HtTasksPerformServiceImpl` 中添加对上述 Mapper 方法的调用。
参考旧系统 `HtTasksPerformServiceImpl` 中的 `updateValveArchive`、`insertReading` 等方法的业务逻辑。

- [ ] **Step 5: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtTasksPerformMapper.java
git add sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksPerformMapper.xml
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksPerformServiceImpl.java
git commit -m "feat(ht): add device state update methods to HtTasksPerformMapper"
```

---

## Task 5: 修复 Mt 热力表修改级联更新

**Files:**
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java`
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/mapper/MtHeatArchiveMapper.java`
- Modify: `sdkj-modules/sdkj-meter/src/main/resources/mapper/MtHeatArchiveMapper.xml`

- [ ] **Step 1: 读取旧系统 MtHeatArchiveService 的 updateData 方法**

读取: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/MtHeatArchiveServiceImpl.java`

找到修改方法中更新 `pr_heat_hot_archive` 和 `pr_heat_unit_hot_archive` 的逻辑。

- [ ] **Step 2: 读取新系统当前实现**

读取: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java`

- [ ] **Step 3: 添加 Mapper 方法声明和 XML SQL**

在 `MtHeatArchiveMapper.java` 中添加级联更新方法：
```java
int updateHeatHotArchive(@Param("archive") MtHeatArchive archive);
int updateHeatUnitHotArchive(@Param("archive") MtHeatArchive archive);
```

在 XML 中添加对应 SQL（从旧系统迁移）。

- [ ] **Step 4: 修改 Service 的 edit 方法**

在 `MtHeatArchiveServiceImpl.edit()` 方法中，`updateById` 之后添加级联更新调用：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void edit(MtHeatArchiveBo bo) {
    MtHeatArchive archive = MapstructUtils.convert(bo, MtHeatArchive.class);
    // ... 已有逻辑
    updateById(archive);
    // 级联更新关联表
    baseMapper.updateHeatHotArchive(archive);
    baseMapper.updateHeatUnitHotArchive(archive);
}
```

- [ ] **Step 5: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-meter -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/mapper/MtHeatArchiveMapper.java
git add sdkj-modules/sdkj-meter/src/main/resources/mapper/MtHeatArchiveMapper.xml
git commit -m "fix(meter): add cascade update for heat archive edit"
```

---

## Task 6: 修复 Mt 阀门修改级联更新

**Files:**
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtTcValveServiceImpl.java`
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/mapper/MtTcValveMapper.java`
- Modify: `sdkj-modules/sdkj-meter/src/main/resources/mapper/MtTcValveMapper.xml`

- [ ] **Step 1: 读取旧系统 MtTcValveService 的 updateData 方法**

读取: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/MtTcValveServiceImpl.java`

找到修改方法中更新 `pr_heat_valve_archive` 和 `pr_heat_unit_valve_archive` 的逻辑。

- [ ] **Step 2: 添加 Mapper 方法和 XML SQL**

在 `MtTcValveMapper.java` 中添加：
```java
int updateValveArchive(@Param("valve") MtTcValve valve);
int updateUnitValveArchive(@Param("valve") MtTcValve valve);
```

- [ ] **Step 3: 修改 Service 的 edit 方法**

在 `MtTcValveServiceImpl.edit()` 中 `updateById` 后添加级联更新。

- [ ] **Step 4: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-meter -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtTcValveServiceImpl.java
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/mapper/MtTcValveMapper.java
git add sdkj-modules/sdkj-meter/src/main/resources/mapper/MtTcValveMapper.xml
git commit -m "fix(meter): add cascade update for valve archive edit"
```

---

## Task 7: 移除不安全的支付回调骨架代码

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java`

- [ ] **Step 1: 读取当前 PrAutoMachineController**

读取文件，找到支付回调相关的端点方法。

- [ ] **Step 2: 标记未实现的支付回调端点**

对所有未实现签名校验的支付回调方法，添加 `@Deprecated` 注解并在方法体首行抛出异常：

```java
@Deprecated
@PostMapping("/wechat/callback")
@Hidden
public R<Void> wechatCallback(HttpServletRequest request) {
    throw new ServiceException("支付回调功能尚未实现，请勿调用此端点");
}
```

对支付宝回调做同样处理。**删除所有直接返回文本提示的骨架代码**，替换为异常抛出。

- [ ] **Step 3: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java
git commit -m "fix(security): disable unsafe payment callback stubs"
```

---

## Task 8: 实现 AreaController 省市区查询

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AreaController.java`

- [ ] **Step 1: 读取旧系统 AreaController 和 AreaService**

读取:
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/AreaController.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/AreaServiceImpl.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/mapper/AreaMapper.java`

- [ ] **Step 2: 确认新系统是否有 sys_area 表**

检查 `script/sql/sdkj-init.sql` 中是否已有省市区表结构。如没有，在 `phase6_p0_fixes.sql` 中添加。

- [ ] **Step 3: 实现查询逻辑**

在新系统 AreaController 中实现以下端点：
- `GET /thermal/area/provinces` — 省份列表
- `GET /thermal/area/cities/{provinceCode}` — 城市列表
- `GET /thermal/area/districts/{cityCode}` — 区县列表

使用 MyBatis-Plus LambdaQueryWrapper 查询。如果旧系统使用行政区划 SQL 数据文件，直接复用。

- [ ] **Step 4: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AreaController.java
git add script/sql/phase6_p0_fixes.sql
git commit -m "feat(thermal): implement AreaController province/city/district queries"
```

---

## Task 9: 补充缺失的 Mt 热力表查询端点

**Files:**
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtHeatArchiveController.java`
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/IMtHeatArchiveService.java`
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java`

- [ ] **Step 1: 读取旧系统缺失方法**

读取旧系统 `MtHeatArchiveController` 中的 `getHeatList()` 和 `queryMtHeatArchive()` 方法。

- [ ] **Step 2: 添加查询端点**

在新系统 Controller 中添加：
- `GET /thermal/meter/heat/list` — 获取所有热力表列表（不分页）
- `GET /thermal/meter/heat/query` — 按条件查询热力表

- [ ] **Step 3: 实现 Service 逻辑**

- [ ] **Step 4: 编译验证并提交**

```bash
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtHeatArchiveController.java
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/IMtHeatArchiveService.java
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java
git commit -m "feat(meter): add missing heat archive list and query endpoints"
```

---

## Task 10: 全量编译验证和提交

**Files:**
- Modify: `script/sql/phase6_p0_fixes.sql`（最终版本）

- [ ] **Step 1: 全量编译**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 2: 检查 SQL 脚本完整性**

确认 `phase6_p0_fixes.sql` 包含所有新表和字段变更。

- [ ] **Step 3: 最终提交**

```bash
git add -A
git commit -m "feat(p0): complete all blocking fixes - strategy perform, device state sync, cascade updates, area controller"
```

---

## 执行顺序依赖图

```
Task 1 (SQL) ──→ Task 2 (Entity/Mapper) ──→ Task 3 (Service/Controller)
                                                      │
Task 4 (HtTasksPerform 设备状态更新)                   │ ← 可并行
Task 5 (Mt 热力表级联更新)                             │ ← 可并行
Task 6 (Mt 阀门级联更新)                               │ ← 可并行
Task 7 (支付回调安全)                                  │ ← 可并行
Task 8 (AreaController)                               │ ← 可并行
Task 9 (Mt 查询端点)                                   │ ← 可并行
                                                      │
Task 10 (全量编译验证) ← 依赖所有上述任务完成
```

Task 1-3 有串行依赖；Task 4-9 可并行执行；Task 10 是最终验证。
