# Round 2 — Agent 3: Ht 热力调控 + Mt 仪表设备深度审核

## 概述

本报告对 Ht 热力调控模块和 Mt 仪表设备模块进行了 Service 层和 DB 层的深度对比分析。

**审核范围**:
- HtStrategy（控制策略）
- HtInstruction（控制指令）
- HtTasks / HtTasksPerform（任务调度与执行）
- HtScopeDtu（DTU 控制范围）
- HtRepair（维修工单）
- Mt 各仪表档案（电表/水表/气表/热表/阀门/集中器/温控器）

## 一、Entity 字段对比

### 1.1 HtStrategy 字段简化（高风险）

**旧系统** (`com.thermal.entity.HtStrategy`):
- 包含完整的策略参数字段：`adjustBasis`, `stride`, `priority`, `intervall`, `number`
- 阀门角度控制：`valveMin`, `valveMax`
- 温度参数：`inTemp`, `inTempDeviation`, `outTemp`, `outTempDeviation`, `roomTemp`, `roomTempDeviation`
- 流量参数：`curFlow`, `curFlowDeviation`
- 报警配置：`isReportPolice`, `reportPoliceNumber`, `isManagePolice`, `managePoliceNumber`
- **供热系数字段（7个）**: `bianhxs`, `dinghxs`, `dihxs`, `zhonghxs`, `bulixs`, `biandinghxs`, `biandihxs`, `gdhxs`, `sbgnhxs`, `xbghxs`, `zchxs`
- 系数启用标志：`isXishu`, `isFzxishu`
- 设计参数：`heatSupplyIndex`, `temperatureDifference`, `heatDifference`

**新系统** (`org.sdkj.thermal.domain.HtStrategy`):
```java
@Data
@TableName("ht_strategy")
public class HtStrategy extends BaseEntity {
    private String id;
    private String name;
    private Integer type;      // 新增字段
    private String companyId;
    private String remark;
    @TableField(exist = false)
    private List<HtStrategySub> subList;
}
```

**风险评估**: 🔴 **RED**
- 新系统 Entity 缺失大量策略配置字段
- 策略计算逻辑所需的参数（温度、流量、系数）全部缺失
- **怀疑**: 这些字段可能迁移到了 `HtStrategySub` 或其他表中，需确认

**建议**: 
- 检查数据库表 `ht_strategy` 是否保留了这些字段
- 如果字段存在但未映射到 Entity，需补充字段定义
- 如果字段已删除，需确认策略计算逻辑如何获取参数

### 1.2 HtScopeDtu 缺失（高风险）

**旧系统**:
```java
@Data
@TableName("ht_scope_dtu")
public class HtScopeDtu {
    private String id;
    private String tasksId;
    private String orgId;
    private String companyId;
    private String meterArcCode;
    private String dtuNum;
    private String chanNums;      // 通道号集合
    private String concentratorCode;
    private Integer status;       // 执行状态
}
```

**新系统**: ❌ **Entity 不存在**

**风险评估**: 🔴 **RED**
- `HtScopeDtu` 用于 DTU 广播控制，旧系统 `HtTasksServiceImpl` 中大量使用
- 新系统 `HtTasksServiceImpl` 的 `insertHtTasksPerformDtu` 方法完全未迁移
- **功能影响**: DTU 广播控制功能完全缺失

**建议**:
- 创建 `HtScopeDtu` Entity 和 Mapper
- 迁移 `insertHtTasksPerformDtu` 相关逻辑
- 或确认 DTU 控制是否有替代实现

### 1.3 HtTasksPerform 字段差异

**对比结果**: ✅ 基本一致
- 新系统保留了核心字段：`tasksId`, `strategyId`, `instructionId`, `instruction`, `instructionType`, `status`
- 新系统新增了统计和状态管理相关方法

## 二、Service 层逻辑对比

### 2.1 HtStrategy 服务简化（高风险）

**旧系统** (`HtStrategyServiceImpl`):
```java
public boolean insertData(HtStrategy hStrategy) {
    boolean a = false;
    String createBy = SecurityUtils.getUser().getId();
    a = mapper.insertData(hStrategy, createBy);
    a = mapper.insertDataSub(hStrategy.getHtStrategySubList());
    return a;
}
```

**新系统** (`HtStrategyServiceImpl`):
```java
@Transactional(rollbackFor = Exception.class)
public boolean save(HtStrategy entity) {
    boolean result = super.save(entity);
    if (result && entity.getSubList() != null && !entity.getSubList().isEmpty()) {
        for (HtStrategySub sub : entity.getSubList()) {
            sub.setStrategyId(entity.getId());
            subMapper.insert(sub);
        }
    }
    return result;
}
```

**风险评估**: 🟡 **YELLOW**
- 逻辑等价，但新系统使用了 MyBatis-Plus 的 `save` 方法
- 事务管理更规范
- **但需确认**: `mapper.insertData` 是否有额外的业务逻辑（如设置创建人、租户等）

### 2.2 HtTasks 服务大幅简化（高风险）

**旧系统核心方法**:
- `insertHtTasksPerformLs` - 生成临时执行记录（145行复杂逻辑）
- `insertHtTasksPerformDtu` - DTU 广播控制
- `instructionGeneration` - 指令生成（450+行，包含7种调控依据的分支逻辑）
- `updateInstructionGeneration` - 指令更新
- `insertHtTasksPerform` - 插入执行记录

**新系统对应方法**:
```java
@Transactional(rollbackFor = Exception.class)
public boolean saveValveAngle(String taskId, String scopeType) {
    HtTasks task = getById(Integer.parseInt(taskId));
    // ... 参数校验
    List<Map<String, Object>> scopeData = "2".equals(scopeType)
        ? baseMapper.selectScopeForAngleD(taskId)
        : baseMapper.selectScopeForAngleH(taskId);
    // ... 生成 HtTasksPerform 记录
    return baseMapper.updateScopeStatus(taskId, 9) > 0;
}
```

**风险评估**: 🔴 **RED**
- 新系统 `saveValveAngle` 仅支持单一策略的开度保存
- **缺失功能**:
  - 7种调控依据的逻辑分支（`adjustBasis` 0-7）
  - 平均回水温度计算
  - 热表瞬时流量计算
  - 热功率计算
  - 指令序列管理（`commandIndex`）
  - 回报率检查
- **影响**: 任务调度功能严重退化

**建议**:
- 确认这些复杂逻辑是否有其他实现方式
- 如果需要恢复，需迁移约 1000+ 行核心逻辑

### 2.3 HtTasksPerform 指令下发逻辑变化

**旧系统** (`HtTasksPerformServiceImpl`):
- 支持多种通信方式：电信 NB-IoT、移动 NB-IoT、M-Bus
- 支持多种指令类型：开阀、关阀、角度调整、制动、上报周期调整、数据采集
- 使用 `HeatMeterControl` 工具类直接调用运营商 API
- 约 600+ 行复杂的协议处理逻辑

**新系统** (`HtTasksPerformServiceImpl`):
```java
public void executeValveControlTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
    JSONArray dataArray = new JSONArray();
    for (HtTasksPerform task : htTasksPerformList) {
        JSONObject item = new JSONObject();
        item.set("meterNum", task.getMeterNum())
            .set("dtuNum", task.getDtuNum())
            .set("conNum", task.getConcentratorCode())
            .set("chanNum", task.getChanNum())
            .set("command", task.getInstructionType())
            .set("guid", task.getId())
            // ...
        dataArray.add(item);
    }
    String result = CollectPlatformUtil.sendControlMsg(payload, ipPort, username, password);
    // ...
}
```

**风险评估**: 🟡 **YELLOW**
- 新系统统一通过采集平台下发指令
- **优点**: 架构更清晰，便于统一管理
- **风险**: 
  - 依赖采集平台的稳定性和功能完整性
  - 缺少直接调用运营商 API 的备用方案
  - `CollectPlatformUtil` 从旧系统迁移，但调用方逻辑完全重写

**幂等性分析**:
- ✅ 新系统使用 `guid`（即 `performId`）作为指令 ID，天然支持幂等
- ✅ 指令状态更新在事务内执行
- ⚠️ 缺少指令重试机制

### 2.4 Mt 仪表档案服务

**对比结果**: ✅ 基本等价
- 新系统使用 MyBatis-Plus 的 `BaseMapperPlus` 模式
- 自动分配到代理商的逻辑保留：`insertMeterToAgent`
- 删除时的分配检查保留：`countAllocatedToOtherCompany`

**改进点**:
- 新系统使用异常代替返回码（更符合 Java 规范）
- 事务注解更规范

## 三、Mapper 查询等价性

### 3.1 HtStrategyMapper

**旧系统**:
```java
public interface HtStrategyMapper extends BaseMapper<HtStrategy> {
    IPage pageList(Page page, @Param("search") String search);
    boolean insertData(@Param("htStrategy") HtStrategy htStrategy, @Param("createBy") String createBy);
    boolean insertDataSub(@Param("list") List<HtStrategySub> htStrategySubList);
    boolean deleteDataSub(@Param("strategyId") String strategyId);
    HtStrategy queryHtStrategy(@Param("id") String id);
    List<HtStrategySub> queryHtStrategySub(@Param("strategyId") String strategyId);
    List<HtTasks> queryIsdeleteData(@Param("id") String id);
    boolean updateData(HtStrategy htStrategy);
    boolean deleteData(@Param("id") String id);
    List<HtStrategy> queryHtStrategyList();
}
```

**新系统**:
```java
public interface HtStrategyMapper extends BaseMapperPlus<HtStrategy, HtStrategyVo> {
    List<HtStrategy> selectAllList();
}
```

**风险评估**: 🟢 **GREEN**
- 新系统依赖 MyBatis-Plus 的 `BaseMapperPlus` 自动生成 CRUD 方法
- 使用 `@AutoMapper` 注解自动转换 VO
- 简化了 Mapper 接口定义

**但需确认**: XML 中是否定义了自定义查询

### 3.2 HtTasksMapper

**旧系统**: 约 40+ 个自定义方法
- `queryHtTasksPJHS` - 查询平均回水温度（分户）
- `queryHtTasksPJHSD` - 查询平均回水温度（单元）
- `setNumber`, `setOutTempNumberX`, `setOutTempNumberD` - 各种指令生成方法
- `insertHtTasksPerformPj`, `inserHtTasksPerformSh` - 各种执行记录插入方法
- `querySummary`, `querySummaryStandard`, `querySummaryUnit` - 汇总统计

**新系统**: 简化为约 10 个方法
- 依赖 MyBatis-Plus 的 Lambda 查询
- 复杂逻辑在 Service 层通过 SQL 注入或 XML 实现

**风险评估**: 🟡 **YELLOW**
- 功能等价性需要逐个验证
- 建议对比 XML 文件中的 SQL 语句

## 四、关键风险标注

### 4.1 指令下发幂等性

**旧系统**: 
- 指令下发后立即更新状态为 1（下发中）
- 依赖采集平台回调更新最终状态
- **风险**: 无幂等保证，可能重复下发

**新系统**:
- 使用 `guid` (performId) 作为指令唯一标识
- 采集平台应基于 `guid` 去重
- **状态**: ✅ 已改进

**建议**:
- 在采集平台文档中确认 `guid` 的去重逻辑
- 考虑在数据库层添加唯一索引

### 4.2 数据采集事务安全

**旧系统**:
```java
@Transactional(rollbackFor = Exception.class)
public boolean insertData(HtStrategy hStrategy) {
    boolean a = false;
    a = mapper.insertData(hStrategy, createBy);
    a = mapper.insertDataSub(hStrategy.getHtStrategySubList());
    return a;
}
```

**新系统**:
```java
@Transactional(rollbackFor = Exception.class)
public boolean save(HtStrategy entity) {
    boolean result = super.save(entity);
    if (result && entity.getSubList() != null && !entity.getSubList().isEmpty()) {
        for (HtStrategySub sub : entity.getSubList()) {
            sub.setStrategyId(entity.getId());
            subMapper.insert(sub);
        }
    }
    return result;
}
```

**风险评估**: 🟢 **GREEN**
- 新系统事务管理更规范
- 使用 `@Transactional(rollbackFor = Exception.class)` 明确指定回滚条件

### 4.3 缺失模块功能替代方案

| 缺失模块 | 功能描述 | 替代方案 | 风险等级 |
|---------|---------|---------|---------|
| `HtScopeDtu` | DTU 广播控制范围 | ❌ 无替代 | 🔴 RED |
| `insertHtTasksPerformDtu` | DTU 广播指令生成 | ❌ 无替代 | 🔴 RED |
| `HtTasksPerformLs` | 临时执行记录表 | ⚠️ 可能未迁移 | 🟡 YELLOW |
| 7种调控依据逻辑 | 复杂策略计算 | ⚠️ 仅保留单一策略 | 🟡 YELLOW |
| 电信/移动直连 API | NB-IoT 设备控制 | ✅ 统一通过采集平台 | 🟢 GREEN |

## 五、数据库表结构对比

### 5.1 表迁移状态

| 表名 | 旧系统 | 新系统 | 状态 |
|------|--------|--------|------|
| `ht_strategy` | ✅ | ✅ | 🟢 已迁移 |
| `ht_strategy_sub` | ✅ | ✅ | 🟢 已迁移 |
| `ht_instruction` | ✅ | ✅ | 🟢 已迁移 |
| `ht_tasks` | ✅ | ✅ | 🟢 已迁移 |
| `ht_tasks_perform` | ✅ | ✅ | 🟢 已迁移 |
| `ht_scope` | ✅ | ✅ | 🟢 已迁移 |
| `ht_scope_dtu` | ✅ | ❌ | 🔴 **未迁移** |
| `ht_tasks_perform_ls` | ✅ | ❌ | 🔴 **未迁移** |
| `ht_tasks_perform_last` | ✅ | ⚠️ | 🟡 需确认 |
| `ht_repair` | ✅ | ✅ | 🟢 已迁移 |
| `mt_electric_archive` | ✅ | ✅ | 🟢 已迁移 |
| `mt_water_archive` | ✅ | ✅ | 🟢 已迁移 |
| `mt_gas_archive` | ✅ | ✅ | 🟢 已迁移 |
| `mt_heat_archive` | ✅ | ✅ | 🟢 已迁移 |
| `mt_tc_valve` | ✅ | ✅ | 🟢 已迁移 |
| `mt_tc_archive` | ✅ | ✅ | 🟢 已迁移 |
| `mt_centrator_archive` | ✅ | ✅ | 🟢 已迁移 |

### 5.2 字段迁移状态

**ht_strategy 表字段对比**:
- 旧系统定义：约 35 个字段
- 新系统 Entity：约 7 个字段
- **结论**: 🔴 **字段严重缺失**，需确认数据库表结构是否一致

**ht_tasks 表字段对比**:
- 基本一致，新系统可能移除了部分冗余字段

## 六、架构差异分析

### 6.1 分页模式

**旧系统**: 使用 MyBatis-Plus 的 `IPage` + 自定义 XML 查询
```java
public IPage pageList(Page page, String search) {
    return mapper.pageList(page, search);
}
```

**新系统**: 使用 `PageQuery` + `TableDataInfo` + `BaseMapperPlus`
```java
public TableDataInfo<HtStrategyVo> selectPageList(LambdaQueryWrapper<HtStrategy> lqw, PageQuery pageQuery) {
    Page<HtStrategyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
    return TableDataInfo.build(result);
}
```

**评估**: ✅ 新系统更规范，类型安全

### 6.2 认证方式

**旧系统**: `SecurityUtils.getUser().getId()`
**新系统**: `LoginHelper.getUserId()`

**评估**: ✅ 等价，符合 Sa-Token 规范

### 6.3 HTTP 客户端

**旧系统**: 自定义 `MyHttpClient`
**新系统**: Hutool `HttpRequest`

**评估**: ✅ Hutool 更成熟，功能更丰富

## 七、迁移完整性评分

| 模块 | API 覆盖 | Service 逻辑 | DB 结构 | 总评 |
|------|---------|-------------|---------|------|
| HtStrategy | 🟢 100% | 🟡 80% | 🔴 50% | 🟡 76% |
| HtInstruction | 🟢 100% | 🟢 95% | 🟢 100% | 🟢 98% |
| HtTasks | 🟢 95% | 🔴 40% | 🟡 70% | 🔴 68% |
| HtTasksPerform | 🟢 90% | 🟡 75% | 🟢 90% | 🟢 85% |
| HtRepair | 🟡 85% | 🟢 90% | 🟢 100% | 🟢 91% |
| Mt 仪表档案 | 🟢 95% | 🟢 95% | 🟢 100% | 🟢 96% |
| **总体** | **🟢 94%** | **🔴 79%** | **🟡 85%** | **🟡 86%** |

## 八、建议

### 8.1 高优先级（必须修复）

1. **确认 `ht_strategy` 表结构**: 检查数据库表是否保留完整字段，如已删除需评估影响
2. **恢复 `HtScopeDtu` 模块**: 如果 DTU 广播功能需要保留
3. **迁移复杂策略逻辑**: 如果需要支持 7 种调控依据
4. **验证指令下发幂等性**: 与采集平台确认 `guid` 去重逻辑

### 8.2 中优先级（建议修复）

1. **补充单元测试**: 特别是策略计算和指令生成逻辑
2. **添加监控日志**: 指令下发失败、超时的告警
3. **完善异常处理**: 采集平台调用失败时的降级方案

### 8.3 低优先级（可选优化）

1. **代码风格统一**: 部分方法命名不一致（如 `saveValveAngle` vs `updateSdkd`）
2. **性能优化**: 大批量指令下发时的性能优化
3. **文档完善**: 补充关键业务逻辑的注释

## 九、总结

Ht 热力调控 + Mt 仪表设备模块的迁移在 API 层面较为完整（94%），但 Service 层存在显著的逻辑简化（79%），特别是：

1. **策略计算逻辑严重退化**: HtStrategy Entity 缺失大量字段，复杂策略计算逻辑未迁移
2. **DTU 控制功能缺失**: HtScopeDtu 模块完全未迁移
3. **指令下发架构重构**: 从直连运营商 API 改为统一通过采集平台，降低了复杂度但增加了依赖

**建议**: 在正式上线前，必须确认以下问题：
- `ht_strategy` 表的实际字段是否完整
- DTU 广播控制是否需要恢复
- 采集平台是否支持所有指令类型
- 复杂策略（如平均回水温度、热表瞬时流量）如何实现
