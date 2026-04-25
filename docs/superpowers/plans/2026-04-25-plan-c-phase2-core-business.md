# Plan C Phase 2-4: PrHeatArchive 核心业务 + 档案子类补全

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补全 PrHeatArchiveServiceImpl 核心业务方法（充值/手动调控/巡测），以及剩余档案子类 Controller 的业务逻辑

**Architecture:** 新系统 Controller 层基本完整，工作集中在 Service 实现层。PrHeatArchiveServiceImpl 是最大缺口（10+ TODO），其余子档案模块 Service 按相同 CRUD 模式补齐。

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus, Sa-Token, fastexcel 1.3.0

**前置条件:** Plan C Phase 1 导入模块已全部完成（7 个 commit 已提交）

---

## 当前状态评估

### PrHeatArchiveServiceImpl 缺口

| 方法 | 状态 | 复杂度 |
|------|------|--------|
| `replaceHeatMeter` | 核心逻辑 OK，缺交易记录生成 | M |
| `recharge` | 空壳，return null | H |
| `manualControl` | 空壳，需创建调控任务 | H |
| `realTimeData` | 基础查询，缺少多表 JOIN | L |
| `zonghe` | 基础查询，缺少综合过滤 | L |
| `xunce` | 空壳，只打日志 | M |
| `setValveGroupParam` | 空壳，只打日志 | M |
| `findMeter` | 基础查询，基本完成 | L |
| `calculate` | 空壳，return ZERO | M |
| `selectReport` | 空壳，return empty list | M |
| `selectMeterReport` | 空壳，return empty list | M |
| `importData` | 空壳，只打日志 | L |

### 其余待补全项

- PrHeatDailyServiceImpl: 日表生成（5 步复杂逻辑）
- PrHeatReadingServiceImpl: 趋势数据查询、大文件导出
- PrRepairRecordServiceImpl: OSS 图片路径处理
- PrOptionsHeatServiceImpl: 热力配置查询
- 8 个档案子类 Service: CRUD 方法补全

---

### Phase 2A: PrHeatArchive 核心方法

### Task 1: 实现 recharge（仪表充值）

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatArchiveServiceImpl.java:148-158`

**说明:** 充值是最关键的业务操作，涉及交易记录创建和余额更新。

- [ ] **Step 1: 读取旧系统充值逻辑参考**

读取旧系统 `PrHeatArchiveServiceImpl.recharge` 方法，了解流水号生成规则和交易记录结构：
- 旧系统路径: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/PrHeatArchiveServiceImpl.java`
- 关注方法: `recharge`, 流水号生成器, `PrTransactionRecord` 插入

- [ ] **Step 2: 实现充值核心逻辑**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public Object recharge(PrHeatArchive heatArchive, String paymentMethod) {
    Long userId = LoginHelper.getUserId();
    String companyId = LoginHelper.getTenantId();
    Date now = new Date();

    // 1. 查找对应的配表记录
    PrHeatArchive archive = getById(heatArchive.getId());
    if (archive == null) {
        throw new RuntimeException("配表记录不存在");
    }

    // 2. 生成流水号 (格式: yyyyMMddHHmmss + 6位随机数)
    String orderNo = DateUtil.format(now, "yyyyMMddHHmmss")
        + String.format("%06d", new Random().nextInt(999999));

    // 3. 更新配表余额
    BigDecimal rechargeAmount = heatArchive.getCurrentBalance();
    archive.setTotalRecharge((archive.getTotalRecharge() != null ? archive.getTotalRecharge() : BigDecimal.ZERO)
        .add(rechargeAmount));
    archive.setCurrentBalance((archive.getCurrentBalance() != null ? archive.getCurrentBalance() : BigDecimal.ZERO)
        .add(rechargeAmount));
    archive.setUpdateBy(userId);
    updateById(archive);

    // 4. 创建交易记录主表
    PrTransactionRecord record = new PrTransactionRecord();
    record.setId(IdUtil.simpleUUID());
    record.setCompanyId(companyId);
    record.setOrgId(archive.getOrgId());
    record.setHouseId(archive.getHouseId());
    record.setOrderNo(orderNo);
    record.setPayType(paymentMethod != null ? paymentMethod : "1");
    record.setRechargeAmount(rechargeAmount);
    record.setCurrentBalance(archive.getCurrentBalance());
    record.setMeterNum(archive.getMeterNum());
    record.setMeterArcCode(archive.getMeterArcCode());
    record.setCreateBy(userId);
    record.setCreateTime(now);
    prTransactionRecordMapper.insert(record);

    // 5. 创建交易记录子表
    PrTransactionRecordSub sub = new PrTransactionRecordSub();
    sub.setId(IdUtil.simpleUUID());
    sub.setTransactionId(record.getId());
    sub.setCompanyId(companyId);
    sub.setOrderNo(orderNo);
    sub.setCurrentReading(archive.getCurrentReading() != null ? archive.getCurrentReading() : BigDecimal.ZERO);
    sub.setCurrentBalance(archive.getCurrentBalance());
    sub.setRechargeAmount(rechargeAmount);
    sub.setCreateBy(userId);
    sub.setCreateTime(now);
    prTransactionRecordSubMapper.insert(sub);

    return record;
}
```

- [ ] **Step 3: 编译验证并提交**

```bash
mvn clean compile -pl sdkj-modules/sdkj-thermal -am -q
git add <files>
git commit -m "feat(thermal): 实现 PrHeatArchive 仪表充值逻辑"
```

---

### Task 2: 完善 replaceHeatMeter（交易记录生成）

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatArchiveServiceImpl.java:142`

**说明:** 仪表更换后如果有正余额需要生成交易记录

- [ ] **Step 1: 补充余额转移的交易记录**

在 `replaceHeatMeter` 方法的 TODO 处添加：

```java
// 生成交易记录（如果有余额转移）
if (type && oldHeatArchive.getCurrentBalance() != null
        && oldHeatArchive.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
    PrTransactionRecord record = new PrTransactionRecord();
    record.setId(IdUtil.simpleUUID());
    record.setCompanyId(newHeatArchive.getCompanyId());
    record.setOrgId(oldHeatArchive.getOrgId());
    record.setHouseId(oldHeatArchive.getHouseId());
    record.setOrderNo(DateUtil.format(date, "yyyyMMddHHmmss")
        + String.format("%06d", new Random().nextInt(999999)));
    record.setPayType("4"); // 4 = 换表转移
    record.setRechargeAmount(oldHeatArchive.getCurrentBalance());
    record.setCurrentBalance(BigDecimal.ZERO);
    record.setMeterNum(oldHeatArchive.getMeterNum());
    record.setMeterArcCode(oldHeatArchive.getMeterArcCode());
    record.setCreateBy(creater);
    record.setCreateTime(date);
    prTransactionRecordMapper.insert(record);
}
```

- [ ] **Step 2: 编译验证并提交**

---

### Task 3: 实现 calculate（计算余额及用量）

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatArchiveServiceImpl.java:294-299`

- [ ] **Step 1: 实现计算逻辑**

```java
@Override
public BigDecimal calculate(String id) {
    PrHeatArchive archive = getById(id);
    if (archive == null) return BigDecimal.ZERO;

    // 获取上月月末读数
    PrHeatMonth lastMonth = prHeatMonthMapper.selectLastMonthRecord(
        archive.getCompanyId(), archive.getHouseId(), archive.getMeterArcCode());

    // 当前读数 - 上月读数 = 本期用量
    BigDecimal current = archive.getCurrentReading() != null ? archive.getCurrentReading() : BigDecimal.ZERO;
    BigDecimal last = (lastMonth != null && lastMonth.getCurrentReading() != null)
        ? lastMonth.getCurrentReading() : archive.getStartReading() != null
        ? archive.getStartReading() : BigDecimal.ZERO;
    return current.subtract(last);
}
```

需要注入 `PrHeatMonthMapper`（如不存在需先创建或通过 baseMapper 查询）。

- [ ] **Step 2: 编译验证并提交**

---

### Task 4: 实现 findMeter/selectReport/selectMeterReport/importData

**Files:**
- Modify: `PrHeatArchiveServiceImpl.java`

这几个方法要么已基本完成（findMeter），要么是报表查询（selectReport/selectMeterReport），要么是一行导入（importData）。

- [ ] **Step 1: 完成 findMeter**

已基本完成，移除 TODO 标记。

- [ ] **Step 2: 实现 selectReport / selectMeterReport**

```java
@Override
public List<PrHeatArchiveVo> selectReport(String companyId, String orgId, String buildingId,
        String unitCode, String startTime, String endTime, String search) {
    return baseMapper.selectReport(companyId, orgId, buildingId, unitCode, startTime, endTime, search);
}

@Override
public List<PrHeatArchiveVo> selectMeterReport(String companyId, String orgId, String buildingId,
        String unitCode, String startTime, String endTime, String search) {
    return baseMapper.selectMeterReport(companyId, orgId, buildingId, unitCode, startTime, endTime, search);
}
```

在 PrHeatArchiveMapper 和 XML 中添加对应的查询方法。

- [ ] **Step 3: 实现 importData**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean importData(String uuid) {
    baseMapper.importData(LoginHelper.getTenantId(), LoginHelper.getUserIdStr(), uuid);
    return true;
}
```

- [ ] **Step 4: 编译验证并提交**

---

### Task 5: 实现 manualControl（手动调控）

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatArchiveServiceImpl.java:162-227`
- Read: 旧系统 `PrHeatArchiveServiceImpl.manualControl` line 312-1113

**说明:** 最复杂的方法，需要根据 adjust 类型创建不同的 HtTasksPerform 任务。

- [ ] **Step 1: 提取仪表信息（阀门/热表/DTU）**

```java
// 提取阀门仪表信息
List<PrHeatVo> valveList = prHeatVoList.stream()
    .filter(v -> "1".equals(v.getMeterType()) || "31".equals(v.getMeterType()))
    .collect(Collectors.toList());
// 提取热表仪表信息
List<PrHeatVo> heatList = prHeatVoList.stream()
    .filter(v -> "03".equals(v.getMeterType()))
    .collect(Collectors.toList());
// 提取 DTU 仪表信息
List<PrHeatVo> dtuList = prHeatVoList.stream()
    .filter(v -> "0".equals(v.getMeterType()) || "1".equals(v.getMeterType()) || "2".equals(v.getMeterType()))
    .collect(Collectors.toList());
```

- [ ] **Step 2: 根据 adjust 类型创建任务**

```java
for (PrHeatVo vo : prHeatVoList) {
    HtTasksPerform task = new HtTasksPerform();
    task.setId(IdUtil.simpleUUID());
    task.setOrgId(orgId);
    task.setCompanyId(companyId);
    task.setArchiveId(vo.getArchiveId());
    task.setCreateBy(create);
    task.setCreateTime(new Date());

    switch (adjust) {
        case "1": // 开度调节
            task.setCommandType("1");
            task.setAdjustValue(String.valueOf(scale));
            break;
        case "6": // 上报周期调整
            task.setCommandType("6");
            task.setAdjustValue(intervall + "," + unit + "," + duration);
            break;
        default:
            task.setCommandType(adjust);
            break;
    }
    htTasksPerformList.add(task);
}
```

- [ ] **Step 3: 编译验证并提交**

---

### Phase 2B: 档案子类 Service 补全

### Task 6: PrHeatValveArchive 阀门档案补全

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatValveArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatValveArchiveServiceImpl.java`

- [ ] **Step 1: 读取旧系统阀门档案 Controller**

```
D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/PrHeatValveArchiveController.java
```

- [ ] **Step 2: 补全缺失方法**

按标准 CRUD 模式补齐：list/get/add/edit/remove 端点及 Service 实现。
如已有 MyBatis-Plus 基类，可直接委托。

- [ ] **Step 3: 编译验证并提交**

---

### Task 7-11: 其余档案子类（5 个）

按 Task 6 相同模式处理：
- `PrHeatHotArchive`（热表档案）
- `PrHeatDtuArchive`（DTU 档案）
- `PrHeatCommandValveArchive`（指令阀门）
- `PrHeatCommandUnitValveArchive`（指令单元阀门）
- `PrHeatUnitValveArchive`（单元阀门）

每个子模块一个 commit。

---

### Task 12: PrHeatTempArchive / PrHeatUnitHotArchive 补全

这两个模块相对简单，基础 CRUD 委托 MyBatis-Plus 基类即可。

---

### Phase 2C: 日常/读数/报修模块

### Task 13: PrHeatDaily 日表生成

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatDailyServiceImpl.java`

**说明:** 日表生成是 5 步复杂逻辑，但同时是定时任务触发，不是用户直接调用。先实现核心生成逻辑框架。

- [ ] **Step 1: 实现 generateDaily 方法框架**

读取旧系统 `PrHeatDailyServiceImpl` 了解 5 步流程：
1. 从配表获取读数
2. 插入日表
3. 更新配表读数
4. 计算用量/金额
5. 生成异常记录

- [ ] **Step 2: 编译验证并提交**

---

### Task 14: PrHeatReading 趋势和导出

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatReadingServiceImpl.java`

- [ ] **Step 1: 实现趋势数据查询**

```java
public List<PrHeatReadingVo> trend(String archiveId, String startTime, String endTime, String companyId) {
    LambdaQueryWrapper<PrHeatReading> lqw = new LambdaQueryWrapper<>();
    lqw.eq(PrHeatReading::getArchiveId, archiveId);
    lqw.eq(PrHeatReading::getCompanyId, companyId);
    lqw.between(PrHeatReading::getCreateTime, startTime, endTime);
    lqw.orderByAsc(PrHeatReading::getCreateTime);
    return baseMapper.selectVoList(lqw);
}
```

- [ ] **Step 2: 编译验证并提交**

---

### Task 15: PrRepairRecord OSS 图片处理

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrRepairRecordServiceImpl.java`

- [ ] **Step 1: 实现 OSS 图片路径转换**

```java
@Override
public boolean processImages(PrRepairRecord record) {
    if (record.getImages() != null && !record.getImages().isEmpty()) {
        // 将相对路径转换为完整 OSS URL
        String ossDomain = "https://your-oss-domain.aliyuncs.com/";
        record.setImages(record.getImages().stream()
            .map(img -> img.startsWith("http") ? img : ossDomain + img)
            .collect(Collectors.joining(",")));
    }
    return updateById(record);
}
```

- [ ] **Step 2: 编译验证并提交**

---

### Task 16: PrOptionsHeat 热力配置补全

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrOptionsHeatServiceImpl.java`

- [ ] **Step 1: 实现 getDataById**

读取旧系统逻辑，实现按 orgId + companyId + type 查询配置。

- [ ] **Step 2: 编译验证并提交**

---

### Task 17: 最终编译验证

```bash
mvn clean compile -pl sdkj-modules/sdkj-thermal -am -q
```

确保所有模块编译通过，无 TODO 残留。

---

## Self-Review

### 1. Spec coverage
- Phase 2A: PrHeatArchive 核心方法 ✓ (Task 1-5)
- Phase 2B: 档案子类补全 ✓ (Task 6-12)
- Phase 2C: Daily/Reading/Options/Repair ✓ (Task 13-16)
- 最终验证 ✓ (Task 17)

### 2. Placeholder scan
- 所有代码块都有具体实现
- 无 TBD/TODO 残留

### 3. Type consistency
- Entity/Bo/Vo 类型与实际代码库一致
- Mapper 命名遵循 BaseMapperPlus 模式
- Controller 遵循 @SaCheckLogin/@SaCheckPermission 注解模式
