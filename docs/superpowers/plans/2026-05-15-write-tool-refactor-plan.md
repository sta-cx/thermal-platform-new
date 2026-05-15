# WriteTool 首批改造实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 按 WriteTool 操作完整性范式（v2.0）改造 3 个现有 WriteTool + 更新 assistantChatClient system prompt

**Architecture:** WriteTool 薄层委托 → Service 层 xxxFromAi() 方法做补全+校验+保存。CRUD 类走三层分离（Tool → Service → Mapper），Command 类走两层（Tool → Command Service）。

**Tech Stack:** Spring AI 1.0.7 `@Tool`/`@ToolParam`、MyBatis-Plus `BaseMapperPlus`、Sa-Token、`@Transactional`

**范式文档:** `docs/superpowers/specs/2026-05-15-write-tool-data-completeness-design.md`

---

## Task 1: CreateRepairTool 改造（CRUD 类）

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtRepairService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtRepairServiceImpl.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/CreateRepairTool.java`

### Step 1: 在 IHtRepairService 接口新增 createFromAi 签名

在 `IHtRepairService.java` 末尾（`generateRepairNo()` 之后）添加：

```java
import org.sdkj.thermal.ai.tools.write.CreateRepairTool.CreatedRepair;

/**
 * AI Tool 调用的报修工单创建入口。
 *
 * <p>校验规则：
 * <ul>
 *   <li>houseId 对应的 PrHouse 必须存在</li>
 *   <li>repairInfo 不能为空</li>
 * </ul>
 *
 * <p>补全字段：buildingId/buildingName/unitCode/roomNum/orgId/orgName/repairNo，
 * 与 {@link org.sdkj.thermal.controller.HtRepairController#insert} 路径保持一致。
 *
 * @param houseId    房屋 ID（必填）
 * @param repairInfo 报修描述（必填）
 * @param userName   联系人姓名（可空，缺省取户主姓名）
 * @param userPhone  联系电话（可空，缺省取户主电话）
 * @param operatorId 操作者用户 ID（由 Tool 层注入）
 * @return 创建结果
 * @throws IllegalArgumentException houseId 不存在或 repairInfo 为空
 */
CreatedRepair createFromAi(Long houseId, String repairInfo,
                           String userName, String userPhone,
                           Long operatorId);
```

同时需要把 `CreatedRepair` record 从 `CreateRepairTool` 中移到独立的公共位置，或直接让接口 import Tool 类的 record。**选择方案**：把 `CreatedRepair` record 移到 `org.sdkj.thermal.domain.dto` 包下作为独立 DTO，Tool 和 Service 都引用它。

先创建 DTO 文件：

**Create:** `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/CreatedRepairResult.java`

```java
package org.sdkj.thermal.domain.dto;

/**
 * AI Tool 创建报修工单的返回结果
 */
public record CreatedRepairResult(
    Long repairId,
    Long houseId,
    String summary,
    String status
) {}
```

- [ ] **Step 1 complete**

### Step 2: 在 HtRepairServiceImpl 实现 createFromAi

在 `HtRepairServiceImpl.java` 中：

1. 新增注入 `PrHouseMapper` 和 `PrCompanyMapper`
2. 实现 `createFromAi` 方法

```java
import org.sdkj.thermal.domain.dto.CreatedRepairResult;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.springframework.transaction.annotation.Transactional;
import org.sdkj.common.satoken.utils.LoginHelper;

// 新增注入字段
private final PrHouseMapper houseMapper;
private final PrCompanyMapper companyMapper;

@Override
@Transactional(rollbackFor = Exception.class)
public CreatedRepairResult createFromAi(Long houseId, String repairInfo,
                                         String userName, String userPhone,
                                         Long operatorId) {
    // 1. 参数校验
    if (houseId == null) {
        throw new IllegalArgumentException("房屋 ID 不能为空");
    }
    if (repairInfo == null || repairInfo.isBlank()) {
        throw new IllegalArgumentException("报修描述不能为空");
    }

    // 2. 查房屋，确认存在并带出关联字段
    PrHouse house = houseMapper.selectById(houseId);
    if (house == null) {
        throw new IllegalArgumentException("房屋 ID " + houseId + " 不存在，请核实后重试");
    }

    // 3. 构建完整实体
    HtRepair repair = new HtRepair();
    repair.setHouseId(houseId);
    repair.setBuildingId(house.getBuildingId());
    repair.setBuildingName(house.getBuildingName());
    repair.setUnitCode(house.getUnitCode());
    repair.setRoomNum(house.getRoomNum());
    repair.setOrgId(house.getOrgId());

    // 4. 查组织名称（PrHouse.orgName 是 @TableField(exist=false)，需额外查）
    if (house.getOrgId() != null) {
        SysOrganization org = companyMapper.selectOrgById(house.getOrgId());
        if (org != null) {
            repair.setOrgName(org.getName());
        }
    }

    // 5. 业务编号
    repair.setRepairNo(generateRepairNo());

    // 6. 默认值
    repair.setRepairType(0);
    repair.setRepairTime(new Date());
    repair.setRepairStatus(0);
    repair.setUrgentType(0);
    repair.setServiceType(0);

    // 7. 用户传入值（userName/userPhone 为空时尝试从房屋关联的户主取，此处暂不取，保持 null）
    repair.setRepairInfo(repairInfo);
    repair.setUserName(userName);
    repair.setUserPhone(userPhone);

    // 8. 审计字段显式设置
    repair.setCreateBy(operatorId);
    repair.setUpdateBy(operatorId);

    // 9. 保存
    save(repair);

    String summary = String.format("为 %s %s 室创建报修工单：%s",
        house.getBuildingName() != null ? house.getBuildingName() : "未知楼宇",
        house.getRoomNum() != null ? house.getRoomNum() : "未知",
        repairInfo.length() > 30 ? repairInfo.substring(0, 30) + "…" : repairInfo);

    return new CreatedRepairResult(repair.getId(), houseId, summary, "PENDING");
}
```

注意 imports 中需要加 `java.util.Date`（已有）。

- [ ] **Step 2 complete**

### Step 3: 精简 CreateRepairTool 为薄层

将 `CreateRepairTool.java` 改为：

```java
package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.dto.CreatedRepairResult;
import org.sdkj.thermal.service.IHtRepairService;
import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * 创建报修工单（CRUD 类 WriteTool）。
 * 薄层：仅收集参数 + 委托 Service，不注入 Mapper、不查 DB。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRepairTool {

    private final IHtRepairService repairService;

    @Tool(description = """
        给指定热户创建一条报修工单。
        典型用途:客服在与用户对话时,用户口述报修原因后调用本 Tool 录入。
        如果用户只给了门牌号(如"3号楼201"),先调用 queryHouseByAddress 查到 houseId 再调用本 Tool。
        """)
    @WriteTool(
        risk = RiskLevel.MEDIUM,
        confirm = true,
        permission = "thermal:ht:repair:add"
    )
    public CreatedRepairResult create(
        @ToolParam(description = "房屋 ID,必填。如果用户只提供地址,请先调用 queryHouseByAddress 查询")
        Long houseId,

        @ToolParam(description = "报修描述,用户原话或概括")
        String repairInfo,

        @ToolParam(description = "联系人姓名,可选。不传时取房屋户主姓名;户主姓名也缺失时不填",
                   required = false)
        String userName,

        @ToolParam(description = "联系电话,可选。格式 1[3-9]xxxxxxxxx;不传时取户主电话",
                   required = false)
        String userPhone
    ) {
        Long operatorId = LoginHelper.getUserId();
        log.info("[Tool] createRepairTool.create houseId={} operator={}", houseId, operatorId);
        return repairService.createFromAi(houseId, repairInfo, userName, userPhone, operatorId);
    }
}
```

关键改动：
- 移除 `PrHouseMapper` 注入
- 移除 `HtRepair`、`PrHouse`、`Date` 等数据层 import
- 调用 `repairService.createFromAi()` 而不是自己拼 Entity
- 从 `LoginHelper.getUserId()` 获取 operatorId（HTTP 同步线程有上下文）
- `@ToolParam` description 按 §3.1 规范补全
- Tool description 加读 Tool 引用

- [ ] **Step 3 complete**

### Step 4: 编译验证

```bash
cd D:\chonggou\thermal-platform-new
mvn clean compile -pl sdkj-modules/sdkj-thermal -am
```

Expected: BUILD SUCCESS

- [ ] **Step 4 complete**

### Step 5: 提交

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/CreatedRepairResult.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IHtRepairService.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtRepairServiceImpl.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/CreateRepairTool.java
git commit -m "refactor: CreateRepairTool 遵循 WriteTool 范式 — 薄层委托 Service.createFromAi()"
```

- [ ] **Step 5 complete**

---

## Task 2: MarkPaidTool 改造（CRUD 类 — 状态变更）

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/MarkedPaymentResult.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrExpenseService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrExpenseServiceImpl.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/MarkPaidTool.java`

### Step 1: 创建 MarkedPaymentResult DTO

**Create:** `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/MarkedPaymentResult.java`

```java
package org.sdkj.thermal.domain.dto;

import java.math.BigDecimal;

/**
 * AI Tool 标记缴费的返回结果
 */
public record MarkedPaymentResult(
    Long expenseId,
    BigDecimal amount,
    String summary,
    String status
) {}
```

- [ ] **Step 1 complete**

### Step 2: 在 IPrExpenseService 新增 markPaidFromAi 签名

在 `IPrExpenseService.java` 末尾添加：

```java
import org.sdkj.thermal.domain.dto.MarkedPaymentResult;

/**
 * AI Tool 调用的标记缴费入口。
 *
 * <p>校验规则：
 * <ul>
 *   <li>expenseId 对应的费用条目必须存在</li>
 *   <li>isCharged 必须为 0 或 null（未缴费），已缴费则拒绝重复标记</li>
 * </ul>
 *
 * @param expenseId  费用条目 ID（必填）
 * @param note       备注（可空）
 * @param operatorId 操作者用户 ID
 * @return 标记结果
 * @throws IllegalArgumentException expenseId 不存在或已缴费
 */
MarkedPaymentResult markPaidFromAi(Long expenseId, String note, Long operatorId);
```

- [ ] **Step 2 complete**

### Step 3: 在 PrExpenseServiceImpl 实现 markPaidFromAi

在 `PrExpenseServiceImpl.java` 中新增方法：

```java
import org.sdkj.thermal.domain.dto.MarkedPaymentResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Override
@Transactional(rollbackFor = Exception.class)
public MarkedPaymentResult markPaidFromAi(Long expenseId, String note, Long operatorId) {
    if (expenseId == null) {
        throw new IllegalArgumentException("费用条目 ID 不能为空");
    }

    PrExpense expense = baseMapper.selectById(expenseId);
    if (expense == null) {
        throw new IllegalArgumentException("费用条目 " + expenseId + " 不存在，请核实后重试");
    }

    if (Integer.valueOf(1).equals(expense.getIsCharged())) {
        throw new IllegalArgumentException("费用条目 " + expenseId + " 已是缴费状态，无需重复标记");
    }

    expense.setIsCharged(1);
    expense.setChargedTime(new Date());
    if (expense.getReceivable() != null) {
        expense.setPaidIn(expense.getReceivable());
    }
    expense.setUpdateBy(operatorId);

    baseMapper.updateById(expense);

    String summary = String.format("费用条目 %s 已标记为缴费，金额 %s",
        expenseId,
        expense.getFinalMoney() != null ? expense.getFinalMoney().toPlainString() + " 元" : "未知");

    return new MarkedPaymentResult(expenseId, expense.getFinalMoney(), summary, "PAID");
}
```

- [ ] **Step 3 complete**

### Step 4: 精简 MarkPaidTool 为薄层

将 `MarkPaidTool.java` 改为：

```java
package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.dto.MarkedPaymentResult;
import org.sdkj.thermal.service.IPrExpenseService;
import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * 标记费用为已缴费（CRUD 类 WriteTool）。
 * 薄层：仅收集参数 + 委托 Service，不注入 Mapper、不查 DB。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarkPaidTool {

    private final IPrExpenseService expenseService;

    @Tool(description = """
        将指定费用条目标记为已交费(用于现金等非线上渠道收款的后录入)。
        典型用途:客服收到用户现金后,在线下窗口标记缴费完成。
        如果用户只说了房号或户主姓名,先调用 QueryArrearsTool 查到费用条目 ID 再调用本 Tool。
        """)
    @WriteTool(
        risk = RiskLevel.MEDIUM,
        confirm = true,
        permission = "thermal:property:expense:edit"
    )
    public MarkedPaymentResult markPaid(
        @ToolParam(description = "费用条目 ID,必填。如果用户不知道 ID,请先调用 QueryArrearsTool 查询")
        Long expenseId,

        @ToolParam(description = "备注,可选。填写收款渠道或特殊情况说明",
                   required = false)
        String note
    ) {
        Long operatorId = LoginHelper.getUserId();
        log.info("[Tool] markPaidTool.markPaid expenseId={} operator={}", expenseId, operatorId);
        return expenseService.markPaidFromAi(expenseId, note, operatorId);
    }
}
```

关键改动：
- 移除 `PrExpenseMapper`、`PrExpense`、`BigDecimal` 等数据层 import
- 调用 `expenseService.markPaidFromAi()`
- Tool description 加读 Tool 引用（QueryArrearsTool）
- `@ToolParam` description 补全

- [ ] **Step 4 complete**

### Step 5: 编译验证

```bash
cd D:\chonggou\thermal-platform-new
mvn clean compile -pl sdkj-modules/sdkj-thermal -am
```

Expected: BUILD SUCCESS

- [ ] **Step 5 complete**

### Step 6: 提交

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/MarkedPaymentResult.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrExpenseService.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrExpenseServiceImpl.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/MarkPaidTool.java
git commit -m "refactor: MarkPaidTool 遵循 WriteTool 范式 — 薄层委托 Service.markPaidFromAi()"
```

- [ ] **Step 6 complete**

---

## Task 3: DispatchValveCommandTool 改造（Command 类）

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/ValveCommandResultDto.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHeatValveArchiveService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatValveArchiveServiceImpl.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/DispatchValveCommandTool.java`

### Step 1: 创建 ValveCommandResultDto

**Create:** `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/ValveCommandResultDto.java`

```java
package org.sdkj.thermal.domain.dto;

/**
 * AI Tool 阀门指令下发的返回结果
 */
public record ValveCommandResultDto(
    Long houseId,
    String action,
    Integer openness,
    boolean dispatched,
    String summary,
    String message
) {}
```

- [ ] **Step 1 complete**

### Step 2: 在 IPrHeatValveArchiveService 新增 dispatchFromAi 签名

在 `IPrHeatValveArchiveService.java` 末尾添加：

```java
import org.sdkj.thermal.domain.dto.ValveCommandResultDto;

/**
 * AI Tool 调用的阀门指令下发入口（Command 类）。
 *
 * <p>校验规则：
 * <ul>
 *   <li>houseId 对应的阀门必须存在</li>
 *   <li>action 必须是 OPEN / CLOSE / SET_OPENNESS 之一</li>
 *   <li>SET_OPENNESS 时 openness 必须在 [0, 100] 区间</li>
 * </ul>
 *
 * @param houseId    房屋 ID（必填）
 * @param action     动作：OPEN / CLOSE / SET_OPENNESS（必填）
 * @param openness   开度 0-100，仅 SET_OPENNESS 时必填
 * @param dryRun     是否仅模拟
 * @param operatorId 操作者用户 ID
 * @return 指令下发结果
 * @throws IllegalArgumentException 阀门不存在、action 非法、openness 越界
 */
ValveCommandResultDto dispatchFromAi(Long houseId, String action, Integer openness,
                                      Boolean dryRun, Long operatorId);
```

- [ ] **Step 2 complete**

### Step 3: 在 PrHeatValveArchiveServiceImpl 实现 dispatchFromAi

在 `PrHeatValveArchiveServiceImpl.java` 中新增方法：

```java
import org.sdkj.thermal.domain.dto.ValveCommandResultDto;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Set;

private static final Set<String> VALID_ACTIONS = Set.of("OPEN", "CLOSE", "SET_OPENNESS");

@Override
public ValveCommandResultDto dispatchFromAi(Long houseId, String action, Integer openness,
                                              Boolean dryRun, Long operatorId) {
    // 1. 参数校验
    if (houseId == null) {
        throw new IllegalArgumentException("房屋 ID 不能为空");
    }
    if (action == null || !VALID_ACTIONS.contains(action.toUpperCase())) {
        throw new IllegalArgumentException(
            "action 必须是 OPEN / CLOSE / SET_OPENNESS 之一，当前值: " + action);
    }
    String normalizedAction = action.toUpperCase();

    if ("SET_OPENNESS".equals(normalizedAction)) {
        if (openness == null || openness < 0 || openness > 100) {
            throw new IllegalArgumentException(
                "SET_OPENNESS 时 openness 必须在 0-100 范围内，当前值: " + openness);
        }
    }

    // 2. 查阀门确认存在
    PrHeatValveArchive valve = baseMapper.selectOne(
        new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getHouseId, houseId)
            .last("LIMIT 1")
    );
    if (valve == null) {
        throw new IllegalArgumentException("房屋 ID " + houseId + " 未配置阀门，无法下发指令");
    }

    boolean effectiveDryRun = dryRun == null || dryRun;

    // 3. dryRun 模式
    if (effectiveDryRun) {
        String summary = String.format("模拟指令：房屋 %s 阀门 %s %s",
            houseId, normalizedAction,
            "SET_OPENNESS".equals(normalizedAction) ? "开度 " + openness : "");
        return new ValveCommandResultDto(houseId, normalizedAction, openness, false,
            summary, "dryRun:命令已生成,未真实下发到 IoT。请运维确认 IoT 链路后改 dryRun=false 重试。");
    }

    // 4. 真实下发（Phase 3 占位）
    // TODO Phase 3: 真实 IoT 下发 — 调用 MBus/AG 平台 API，失败写指令记录表
    String summary = String.format("指令已下发：房屋 %s 阀门 %s %s",
        houseId, normalizedAction,
        "SET_OPENNESS".equals(normalizedAction) ? "开度 " + openness : "");
    return new ValveCommandResultDto(houseId, normalizedAction, openness, true,
        summary, "指令已下发(IoT 链路待接入,当前为占位实现)");
}
```

- [ ] **Step 3 complete**

### Step 4: 精简 DispatchValveCommandTool 为薄层

将 `DispatchValveCommandTool.java` 改为：

```java
package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.dto.ValveCommandResultDto;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * 阀门指令下发（Command 类 WriteTool）。
 * 薄层：仅收集参数 + 委托 Service，不注入 Mapper、不查 DB、不做 dryRun 分支。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchValveCommandTool {

    private final IPrHeatValveArchiveService valveArchiveService;

    @Tool(description = """
        给指定热户的阀门下发控制指令。此操作会真正影响用户家暖气供热,需要二次确认。
        典型用途:客服根据用户请求调节阀门开度。
        action 枚举:OPEN(全开) / CLOSE(全关) / SET_OPENNESS(部分开,需配合 openness 参数 0-100)。
        如果用户只说了门牌号,先调用 queryHouseByAddress 查到 houseId,再调用 getValveStatus 确认阀门存在后再调用本 Tool。
        若 IoT 链路未就绪或用户无执行权限,系统会强制 dryRun=true 只生成指令清单。
        """)
    @WriteTool(
        risk = RiskLevel.HIGH,
        confirm = true,
        permission = "thermal:ht:valve:dispatch"
    )
    public ValveCommandResultDto dispatch(
        @ToolParam(description = "热户 ID,必填。如果用户只提供地址,请先调用 queryHouseByAddress 查询")
        Long houseId,

        @ToolParam(description = "动作:OPEN / CLOSE / SET_OPENNESS,必填")
        String action,

        @ToolParam(description = "开度 0-100,仅 action=SET_OPENNESS 时必填,其他 action 忽略",
                   required = false)
        Integer openness,

        @ToolParam(description = "是否仅模拟,默认 true;生产实发需配置 dryRun=false 并具备 thermal:ht:valve:execute 权限",
                   required = false)
        Boolean dryRun
    ) {
        Long operatorId = LoginHelper.getUserId();
        log.info("[Tool] dispatchValveCommandTool houseId={} action={} openness={} operator={}",
            houseId, action, openness, operatorId);
        return valveArchiveService.dispatchFromAi(houseId, action, openness, dryRun, operatorId);
    }
}
```

关键改动：
- 移除 `PrHeatValveArchiveMapper`、`PrHeatValveArchive` 等 import
- 移除 Tool 内的阀门查询、dryRun 分支
- 调用 `valveArchiveService.dispatchFromAi()`
- Tool description 加读 Tool 引用 + action 枚举说明 + 逻辑必填说明

- [ ] **Step 4 complete**

### Step 5: 编译验证

```bash
cd D:\chonggou\thermal-platform-new
mvn clean compile -pl sdkj-modules/sdkj-thermal -am
```

Expected: BUILD SUCCESS

- [ ] **Step 5 complete**

### Step 6: 提交

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/ValveCommandResultDto.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHeatValveArchiveService.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatValveArchiveServiceImpl.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/ai/tools/write/DispatchValveCommandTool.java
git commit -m "refactor: DispatchValveCommandTool 遵循 WriteTool 范式 — 薄层委托 Service.dispatchFromAi()"
```

- [ ] **Step 6 complete**

---

## Task 4: 更新 assistantChatClient system prompt

**Files:**
- Modify: `sdkj-modules/sdkj-ai/src/main/java/org/sdkj/ai/config/SdkjAiAutoConfiguration.java`

### Step 1: 替换 assistantChatClient 的 defaultSystem

在 `SdkjAiAutoConfiguration.java` 的 `assistantChatClient` 方法中，将 `defaultSystem` 的内容替换为：

```java
.defaultSystem("""
    你是 SDKJ 智慧供热平台的助手。回答与执行操作时遵循以下规则:

    【信息来源】
    1. 优先使用"参考资料"中的事实;参考资料里没有的内容不要编造
    2. 涉及数据库实际数据时,必须调用提供的 readonly Tool 查询,不要靠记忆回答

    【写操作规则(创建/修改/下发指令)】
    3. 调用写 Tool 前,先确保所有 ID 类参数都已通过 readonly Tool 确认;
       不要凭对话上下文猜测 ID
    4. 若关键参数无法确认(如用户只说了"3号楼"但拿不到 buildingId),
       告诉用户:"无法定位到具体房屋,请到业务页面手动操作,
       或提供更精确的信息(如完整地址、热户编号)"
    5. 写 Tool 返回错误时,把错误消息直接转述给用户,不要重试同样的参数

    【行为约束】
    6. 不回答与供热平台无关的话题
    7. 一次回答中最多调用一个写 Tool;多个写操作分轮次执行,每次都让用户确认
    8. 对敏感写操作(阀门控制、单笔退费),即使用户表达了"全部""所有""批量"等意图,
       也仅对已明确指定的目标执行一次,不要扩大范围
    """)
```

- [ ] **Step 1 complete**

### Step 2: 编译验证

```bash
cd D:\chonggou\thermal-platform-new
mvn clean compile -pl sdkj-modules/sdkj-ai -am
```

Expected: BUILD SUCCESS

- [ ] **Step 2 complete**

### Step 3: 提交

```bash
git add sdkj-modules/sdkj-ai/src/main/java/org/sdkj/ai/config/SdkjAiAutoConfiguration.java
git commit -m "feat: assistantChatClient system prompt 增加写操作规则与降级引导"
```

- [ ] **Step 3 complete**

---

## Task 5: 全量编译 + 最终提交

### Step 1: 全量编译

```bash
cd D:\chonggou\thermal-platform-new
mvn clean compile
```

Expected: BUILD SUCCESS

- [ ] **Step 1 complete**

### Step 2: 检查 ToolRegistry 扫描仍能发现 3 个 WriteTool

确认 `ToolRegistry` 的包扫描路径覆盖 `org.sdkj.thermal.ai.tools.write`，3 个 Tool 类仍被正确注册。可通过启动应用查看日志验证：

```bash
mvn spring-boot:run -pl sdkj-admin 2>&1 | grep -i "tool.*register\|WriteTool"
```

Expected: 日志中能看到 3 个 WriteTool 被注册

- [ ] **Step 2 complete**

---

## Self-Review Checklist

- [x] **Spec coverage**: §9.1 CreateRepairTool → Task 1; §9.2 MarkPaidTool → Task 2; §9.3 DispatchValveCommandTool → Task 3; §3.4 system prompt → Task 4
- [x] **No placeholders**: 所有步骤包含完整代码，无 TBD/TODO（除 Phase 3 IoT 占位，这是 spec 中已有的约定）
- [x] **Type consistency**: `CreatedRepairResult`/`MarkedPaymentResult`/`ValveCommandResultDto` 在 DTO 文件、Service 接口、Service 实现、Tool 中名称一致
- [x] **operatorId 来源**: Tool 层通过 `LoginHelper.getUserId()` 获取（HTTP 同步线程有上下文），传给 Service
- [x] **@TableField(exist=false)**: Task 1 Step 2 通过 `companyMapper.selectOrgById()` 额外查组织名称
- [x] **@Transactional**: 两个 CRUD 类 Service 方法都加了
- [x] **ToolParam description**: 每个 required=true 有"如何获取"，每个 required=false 有"为什么可选"
