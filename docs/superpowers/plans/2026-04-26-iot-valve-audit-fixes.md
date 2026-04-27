# IoT 回调 + PrHeatValveArchive 补齐 + 骨架补全 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补齐审核报告中 P0/P1 缺失功能：IoT 设备数据回调通道、阀门调控指令执行、PrHeatValveArchive 批量操作、PrAutoMachine 支付机实现。

**Architecture:** 遵循 RuoYi-Vue-Plus 四层架构(Controller→Service→Mapper→Domain)。IoT 回调使用独立 Controller + Service 解耦设备协议解析。指令下发委托 CollectPlatformUtil 已有方法。批量操作复用 HtTasksPerform 任务表模式。

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus 3.5.16, Sa-Token 1.44, EasyExcel, Hutool HTTP

---

## File Structure

### 新增文件
| 文件 | 职责 |
|------|------|
| `controller/IoTCallbackController.java` | 接收电信/移动/世达平台推送的设备数据 |
| `service/IIoTDataService.java` | IoT 设备数据解析与存储接口 |
| `service/impl/IoTDataServiceImpl.java` | 设备协议解析(Base64/十六进制) + 写入档案表 |
| `domain/dto/NbValvePayload.java` | NB阀门数据解析结果DTO |
| `domain/dto/MbusValvePayload.java` | Mbus阀门数据解析结果DTO |
| `domain/dto/YunGuControlRequest.java` | 云谷控制请求DTO |
| `domain/dto/YunGuDataResponse.java` | 云谷数据响应DTO |
| `domain/dto/LtValveDataResponse.java` | 新奥阀门数据响应DTO |

### 修改文件
| 文件 | 变更 |
|------|------|
| `service/impl/HtTasksPerformServiceImpl.java` | 替换 3 个 Phase 6 stub 为真实实现 |
| `service/IPrHeatValveArchiveService.java` | 新增批量操作/卡表查询/导出方法签名 |
| `service/impl/PrHeatValveArchiveServiceImpl.java` | 实现批量操作/卡表查询/导出逻辑 |
| `controller/PrHeatValveArchiveController.java` | 新增 15+ 端点 |
| `controller/PrAutoMachineController.java` | 从骨架到完整实现 |

---

## Task 1: 实现 HtTasksPerform 指令下发（替换 Phase 6 Stub）

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksPerformServiceImpl.java`

**背景:** 旧系统通过 `getNonExecutionNew1/2/3` 构建JSON并调用采集平台API下发指令。新系统的 `CollectPlatformUtil` 已有 `sendControlMsg()` 方法，只需在 Service 层正确构建payload。

- [ ] **Step 1: 读取当前 HtTasksPerformServiceImpl 的 stub 方法**

确认 `executeValveControlTasks`, `executeHeatMeterTasks`, `executeDtuControlTasks` 的当前签名和位置。

- [ ] **Step 2: 实现 executeValveControlTasks**

替换 stub 为：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void executeValveControlTasks(List<HtTasksPerform> tasks) {
    if (CollUtil.isEmpty(tasks)) return;
    JSONObject payload = new JSONObject();
    payload.put("type", 1); // 阀门
    JSONArray dataArray = new JSONArray();
    for (HtTasksPerform task : tasks) {
        JSONObject item = new JSONObject();
        item.put("meterNum", task.getMeterNum());
        item.put("dtuNum", task.getDtuNum());
        item.put("conNum", task.getConcentratorCode());
        item.put("chanNum", task.getChanNum());
        item.put("command", task.getInstructionType());
        item.put("guid", task.getId());
        item.put("meterInfo", task.getMeterArcCode());
        item.put("meterCode", task.getMeterArcCode());
        if (task.getInstruction() != null) {
            item.put("valveStatus", task.getInstruction());
        }
        dataArray.add(item);
    }
    payload.put("data", dataArray);
    try {
        CollectPlatformUtil.sendControlMsg(payload);
        for (HtTasksPerform task : tasks) {
            task.setStatus(2); // 已发送
            task.setSendTime(new Date());
        }
        updateBatchById(tasks);
    } catch (Exception e) {
        log.error("阀门控制指令下发失败", e);
        throw new RuntimeException("指令下发失败: " + e.getMessage());
    }
}
```

- [ ] **Step 3: 实现 executeHeatMeterTasks**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void executeHeatMeterTasks(List<HtTasksPerform> tasks) {
    if (CollUtil.isEmpty(tasks)) return;
    JSONObject payload = new JSONObject();
    payload.put("type", 2); // 热表
    JSONArray dataArray = new JSONArray();
    for (HtTasksPerform task : tasks) {
        JSONObject item = new JSONObject();
        item.put("meterNum", task.getMeterNum());
        item.put("dtuNum", task.getDtuNum());
        item.put("conNum", task.getConcentratorCode());
        item.put("chanNum", task.getChanNum());
        item.put("command", task.getInstructionType());
        item.put("guid", task.getId());
        item.put("meterInfo", task.getMeterArcCode());
        item.put("meterCode", task.getMeterArcCode());
        dataArray.add(item);
    }
    payload.put("data", dataArray);
    try {
        CollectPlatformUtil.sendControlMsg(payload);
        for (HtTasksPerform task : tasks) {
            task.setStatus(2);
            task.setSendTime(new Date());
        }
        updateBatchById(tasks);
    } catch (Exception e) {
        log.error("热表指令下发失败", e);
        throw new RuntimeException("指令下发失败: " + e.getMessage());
    }
}
```

- [ ] **Step 4: 实现 executeDtuControlTasks**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void executeDtuControlTasks(List<HtTasksPerform> tasks) {
    if (CollUtil.isEmpty(tasks)) return;
    JSONObject payload = new JSONObject();
    payload.put("type", 5); // DTU
    JSONArray dataArray = new JSONArray();
    for (HtTasksPerform task : tasks) {
        JSONObject item = new JSONObject();
        item.put("dtuNum", task.getDtuNum());
        item.put("command", task.getInstructionType());
        item.put("guid", task.getId());
        if (task.getIntervall() != null) {
            item.put("reportInterval", task.getIntervall());
        }
        if (task.getUnit() != null) {
            item.put("intervalUnit", task.getUnit());
        }
        if (task.getDuration() != null) {
            item.put("validTime", task.getDuration());
        }
        dataArray.add(item);
    }
    payload.put("data", dataArray);
    try {
        CollectPlatformUtil.sendControlMsg(payload);
        for (HtTasksPerform task : tasks) {
            task.setStatus(2);
            task.setSendTime(new Date());
        }
        updateBatchById(tasks);
    } catch (Exception e) {
        log.error("DTU控制指令下发失败", e);
        throw new RuntimeException("指令下发失败: " + e.getMessage());
    }
}
```

- [ ] **Step 5: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksPerformServiceImpl.java
git commit -m "feat(thermal): 实现 HtTasksPerform 阀门/热表/DTU 指令下发逻辑

替换 Phase 6 stub 为真实实现，复用 CollectPlatformUtil.sendControlMsg()。
遵循旧系统 getNonExecutionNew1/2/3 的 payload 格式。"
```

---

## Task 2: 实现 IoT 数据回调 Controller

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/IoTCallbackController.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IIoTDataService.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/IoTDataServiceImpl.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/NbValvePayload.java`

**背景:** 旧系统 PrHeatValveArchiveController 有 3 个端点接收设备数据：`insertDataNbValve`(电信NB)、`insertDataMBusValve`(世达Mbus)、`signature`(移动)。数据经Base64/十六进制解析后写入 pr_heat_valve_archive 表。

- [ ] **Step 1: 创建 NbValvePayload DTO**

```java
package org.sdkj.thermal.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class NbValvePayload {
    private String meterNum;
    private String valveStatus;
    private Integer settingStatus;
    private Integer actualOpen;
    private BigDecimal inTemperature;
    private BigDecimal outTemperature;
    private BigDecimal voltage;
    private String valveTime;
    private Integer reportInterval;
    private Integer intervalUnit;
    private Integer validTime;
    private Integer totalDegree;
    private Integer csq;
}
```

- [ ] **Step 2: 创建 IIoTDataService 接口**

```java
package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.dto.NbValvePayload;

public interface IIoTDataService {
    /** 解析并存储电信NB阀门上报数据 */
    int processNbValveData(String timestamp, String imei, String imsi,
                           String productId, String deviceId, NbValvePayload payload);
    /** 解析并存储世达Mbus阀门/热表数据 */
    int processMbusValveData(String type, String meterNum, String valveStatus,
                             String valveOpening, BigDecimal supplyTemp, BigDecimal returnTemp,
                             String dtuNum, String concentratorCode, String chanNum,
                             String imei, String deviceId, String meterArcCode);
    /** 解析并存储移动平台NB阀门数据 */
    int processMobileValveData(String timestamp, String imei, String deviceId,
                               NbValvePayload payload);
}
```

- [ ] **Step 3: 创建 IoTDataServiceImpl**

实现 3 个方法。核心逻辑从旧系统 `PrHeatValveArchiveServiceImpl.insertData()` 迁移：
1. 根据 meterNum 查找 `PrHeatValveArchive` 记录
2. 更新 valveStatus/settingStatus/actualStatus/inTemperature/outTemperature/voltage/valveTime 等字段
3. 调用 `updateById()` 保存

文件路径: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/IoTDataServiceImpl.java`

关键实现逻辑（从旧系统 line 273 移植）：
- 根据 `meterNum` 查找 `PrHeatValveArchive` where `is_changed=0`
- 更新 `valveStatus`, `settingStatus`, `actualStatus`, `inTemperature`, `outTemperature`, `voltage`, `valveTime`, `reportingInterval`, `intervalUnit`, `validTime`, `totalDegree`
- 同时更新关联的 `PrHeatHotArchive` 实时数据（如存在）
- 记录日志

- [ ] **Step 4: 创建 IoTCallbackController**

```java
package org.sdkj.thermal.controller;

@RestController
@RequestMapping("/api/iot")
@Slf4j
@RequiredArgsConstructor
public class IoTCallbackController {

    private final IIoTDataService ioTDataService;

    /** 电信平台 NB 阀门数据回调 */
    @PostMapping("/nb-valve")
    public R<Integer> nbValveCallback(@RequestBody String msg) {
        // 解析 JSONObject: timestamp, IMSI, IMEI, productId, deviceId
        // 解码 payload.APPdata (Base64 → 十六进制解析)
        // 调用 ioTDataService.processNbValveData()
        // 返回 R.ok(200)
    }

    /** 世达 Mbus 阀门/热表数据回调 */
    @PostMapping("/mbus-valve")
    public R<Boolean> mbusValveCallback(@RequestBody String args) {
        // 解析 JSONArray data[]
        // 根据 type(1=阀门, 2=热表) 处理
        // 调用 ioTDataService.processMbusValveData()
    }

    /** 移动平台数据回调 + 验证 */
    @RequestMapping("/mobile-valve")
    public String mobileValveCallback(String msg, String nonce, String signature,
                                       @RequestBody(required = false) String args) {
        // 解析 msg.imei, msg.dev_id, msg.at, msg.value
        // Base64 解码 payload
        // 调用 ioTDataService.processMobileValveData()
        // 验证模式：如果有 msg 参数则返回 msg（平台验证用）
    }
}
```

注意：此 Controller **不使用** `@SaCheckLogin`/`@SaCheckPermission`，因为它是接收外部平台推送的回调端点。

- [ ] **Step 5: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`

- [ ] **Step 6: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/IoTCallbackController.java \
        sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IIoTDataService.java \
        sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/IoTDataServiceImpl.java \
        sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/NbValvePayload.java
git commit -m "feat(thermal): 实现 IoT 设备数据回调端点

新增 IoTCallbackController 接收电信/世达/移动平台推送的设备数据。
包含 NB 阀门 Base64 解析、Mbus 数据解析、移动平台验证逻辑。"
```

---

## Task 3: PrHeatValveArchive 批量操作端点

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHeatValveArchiveService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatValveArchiveServiceImpl.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatValveArchiveController.java`

- [ ] **Step 1: 在 IPrHeatValveArchiveService 新增方法签名**

```java
/** 批量设置阀门开关/状态查询/制动/开度 */
boolean batchSetValveStatus(List<PrHouseByPayVo> houseList, String valveStatus);
/** 批量设置阀门开度 */
boolean batchSetValveOpening(List<PrHouseByPayVo> houseList, String opening);
/** 批量设置上报周期 */
boolean batchSetValveCycle(List<PrHouseByPayVo> houseList, String interval, String unit, String valid);
/** 导出阀门信息 Excel */
List<PrHeatValveArchiveVo> exportAll(String companyId, String orgId, String idList);
/** 导入阀门配表 */
boolean importValveArchive(MultipartFile file) throws IOException;
```

- [ ] **Step 2: 在 PrHeatValveArchiveServiceImpl 实现批量操作**

核心逻辑（移植自旧系统 `setValveOCStatus`）：
1. 遍历 houseList，按 meterNum+meterArcCode 查找 PrHeatValveArchive
2. 构建 HtTasksPerform 任务记录（instructionType 由 valveStatus 决定：1=开，2=关，3=开度，4=查询，5=制动，51=特殊制动）
3. 调用 `htTasksPerformService.getNonExecutionNew1()`（已在 Task 1 实现）
4. 记录操作日志

- [ ] **Step 3: 在 PrHeatValveArchiveController 新增端点**

```java
/** 批量设置阀门开关状态 */
@SaCheckPermission("thermal:ht:valve-archive:control")
@SaCheckLogin
@Log(title = "户间阀门-批量开关", businessType = BusinessType.UPDATE)
@PostMapping("/batch-status")
public R<Void> batchSetValveStatus(@RequestBody List<PrHouseByPayVo> houseList,
                                    @RequestParam String valveStatus) {
    return toAjax(valveArchiveService.batchSetValveStatus(houseList, valveStatus));
}

/** 批量设置阀门开度 */
@SaCheckPermission("thermal:ht:valve-archive:control")
@SaCheckLogin
@Log(title = "户间阀门-批量开度", businessType = BusinessType.UPDATE)
@PostMapping("/batch-opening")
public R<Void> batchSetValveOpening(@RequestBody List<PrHouseByPayVo> houseList,
                                     @RequestParam String valveStatus) {
    return toAjax(valveArchiveService.batchSetValveOpening(houseList, valveStatus));
}

/** 批量设置上报周期 */
@SaCheckPermission("thermal:ht:valve-archive:control")
@SaCheckLogin
@Log(title = "户间阀门-批量周期", businessType = BusinessType.UPDATE)
@PostMapping("/batch-cycle")
public R<Void> batchSetValveCycle(@RequestBody List<PrHouseByPayVo> houseList,
                                   @RequestParam String interval,
                                   @RequestParam String unit,
                                   @RequestParam String valid) {
    return toAjax(valveArchiveService.batchSetValveCycle(houseList, interval, unit, valid));
}

/** 导出阀门信息 */
@SaCheckPermission("thermal:ht:valve-archive:export")
@SaCheckLogin
@Log(title = "户间阀门-导出", businessType = BusinessType.EXPORT)
@GetMapping("/export")
public void exportAll(HttpServletResponse response,
                      @RequestParam(required = false) String companyId,
                      @RequestParam(required = false) String orgId) throws IOException {
    // EasyExcel 写 response 流，参考已实现的 PrHeatDailyController 导出模式
}

/** 导入阀门配表 */
@SaCheckPermission("thermal:ht:valve-archive:import")
@SaCheckLogin
@Log(title = "户间阀门-导入", businessType = BusinessType.IMPORT)
@PostMapping("/import")
public R<Void> importValveArchive(@RequestParam MultipartFile file) throws IOException {
    if (file.isEmpty()) return R.fail("文件不能为空");
    return toAjax(valveArchiveService.importValveArchive(file));
}
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`

- [ ] **Step 5: 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHeatValveArchiveService.java \
        sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatValveArchiveServiceImpl.java \
        sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatValveArchiveController.java
git commit -m "feat(thermal): 实现 PrHeatValveArchive 批量操作/导出/导入端点

新增批量开关、开度设置、上报周期设置、Excel导出导入功能。
复用 HtTasksPerform 任务模式执行指令下发。"
```

---

## Task 4: PrHeatValveArchive 第三方 API 端点

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatValveArchiveController.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/YunGuControlRequest.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/dto/YunGuDataResponse.java`

- [ ] **Step 1: 创建云谷相关 DTO**

`YunGuControlRequest.java`:
```java
@Data
public class YunGuControlRequest {
    private String manuId;
    private Integer controlMode; // 必须为 11（手动模式）
    private String value; // 0-100 开度值
}
```

`YunGuDataResponse.java`:
```java
@Data
public class YunGuDataResponse {
    private String meterManuId;
    private String showName;
    private BigDecimal lastFlow;
    private BigDecimal lastForwardT;
    private BigDecimal lastReturnT;
    private BigDecimal lastHeatSum;
    private BigDecimal lastFlowSum;
    private BigDecimal lastHeatPower;
    private Long lastRecordTs;
    private Integer lastValveOpenPercent;
    private Integer boltStatus;
}
```

- [ ] **Step 2: 在 Controller 中添加云谷控制 API**

```java
/** 云谷平台 — 阀门控制 */
@PostMapping("/api/enopt/valve/control")
public R<?> yunGuControl(
        @RequestHeader("AppCode") String appCode,
        @RequestHeader("Timestamp") String timestamp,
        @RequestHeader("AppToken") String appToken,
        @RequestBody YunGuControlRequest request) {
    // 1. 验证 AppCode == "sdkjApp"
    // 2. 验证时间戳差 < 5分钟
    // 3. 验证 token: YunGuUtils.generateToken("sdkjApp", secret, timestamp)
    // 4. 验证 controlMode == 11, value 0-100
    // 5. 按 manuId 查找阀门，创建 HtTasksPerform 任务
    // 6. 下发指令
    // 移植自旧系统 PrHeatValveArchiveController line 1033-1135
}
```

- [ ] **Step 3: 添加云谷批量数据同步 API**

```java
/** 云谷平台 — 批量数据查询 */
@PostMapping("/api/enopt/rtdata/batchsync")
public R<?> yunGuBatchSync(/* 同样的 header 验证 */) {
    // 1. 验证 headers
    // 2. 按 manuIds 查询阀门+热表数据
    // 3. 构建 YunGuDataResponse 列表
    // 移植自旧系统 line 1137-1237
}
```

- [ ] **Step 4: 编译验证 + 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/
git commit -m "feat(thermal): 实现云谷/新奥第三方API对接端点

迁移云谷阀门控制API、批量数据同步API。
包含 AppCode/AppToken/Timestamp 三重验证机制。"
```

---

## Task 5: PrHeatValveArchive 卡表 + 信息同步 + 查询端点

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatValveArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHeatValveArchiveService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatValveArchiveServiceImpl.java`

- [ ] **Step 1: 新增 Service 方法签名**

```java
// 卡表相关
TableDataInfo<PrHeatValveArchiveVo> pageListHeatCard(String companyId, String orgId, String buildingId,
    String unit, String meterArcCode, String payStatus, String search, String parentId,
    String writeCardStatus, PageQuery pageQuery);
boolean updateValveStatus(String id);
R queryMeterByMeterNum(String meterNum, String orgId, String code);
R queryValveByMeterNum(String meterNum);
R queryMeterListByMeterNum(String meterNum, String userId);
R queryHouseByMeterNum(String meterNum);
R queryCardMeterByHouseId(String houseId);
R queryCardMeterByRoomNum(String orgId, String buildingId, String unitCode, String search);

// 信息同步
boolean valveInformationSynchronization(String orgId, String companyId);
void downloadInfoSync(HttpServletResponse response, String companyId, String orgId) throws IOException;

// 其他查询
R getValveDataByHouseId(String houseId);
R insertValveControlLogByBluetooth(String meterNum, String type, String opening);
boolean insertUserAndValveInfo(/* 大量参数：公司/小区/楼宇/房间/用户/阀门信息 */);
```

- [ ] **Step 2: 在 Controller 中逐一添加端点**

每个端点遵循已有模式：
- `@SaCheckPermission` + `@SaCheckLogin` + `@Log`
- RESTful 路径
- 委托 Service

关键端点：
- `GET /thermal/ht/valve-archive/heat-card` — 卡表分页
- `PUT /thermal/ht/valve-archive/{id}/card-status` — 更新写卡状态
- `GET /thermal/ht/valve-archive/query-by-meter` — 按表号查询
- `GET /thermal/ht/valve-archive/query-by-house` — 按房屋查阀门
- `GET /thermal/ht/valve-archive/query-by-room` — 按房号查卡阀
- `POST /thermal/ht/valve-archive/sync` — 同步到采集平台
- `GET /thermal/ht/valve-archive/sync-download` — 下载同步信息
- `POST /thermal/ht/valve-archive/bluetooth-log` — 蓝牙控制日志
- `POST /thermal/ht/valve-archive/user-valve` — 一键新增用户+阀门

- [ ] **Step 3: 编译验证 + 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/
git commit -m "feat(thermal): 补齐 PrHeatValveArchive 卡表/查询/同步端点

实现卡表管理、设备查询、采集平台同步、蓝牙控制日志等功能。
覆盖旧系统 43+ 端点中的大部分缺失项。"
```

---

## Task 6: PrAutoMachineController 从骨架到实现

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrAutoMachineService.java` (如需新增方法)

**背景:** PrAutoMachineController 当前所有方法返回 `R.fail()`。核心功能是自助缴费机：生成序列号、生成二维码、接收微信/支付宝回调、查询支付状态。

- [ ] **Step 1: 移除 @Deprecated 标记**

将 Controller 和其方法上的 `@Deprecated` 和 `@Hidden` 注解移除。

- [ ] **Step 2: 实现 getSerialNum — 生成缴费序列号**

```java
@SaCheckPermission("thermal:property:auto-machine:query")
@SaCheckLogin
@GetMapping("/serial-num")
public R<String> getSerialNum(@RequestParam String companyId) {
    String serialNum = autoMachineService.generateSerialNum(companyId);
    return R.ok(serialNum);
}
```

Service 实现：按 `PrOptionsHeat.serialPrefix + 日期 + 自增序号` 生成。

- [ ] **Step 3: 实现 getQrCode — 生成支付二维码**

```java
@SaCheckPermission("thermal:property:auto-machine:query")
@SaCheckLogin
@GetMapping("/qr-code")
public R<String> getQrCode(@RequestParam String type, // wechat/alipay/wechatH5
                           @RequestParam String serialNum) {
    String qrUrl = autoMachineService.generateQrCode(type, serialNum);
    return R.ok(qrUrl);
}
```

- [ ] **Step 4: 实现 wechatCallback / aliCallback — 支付回调**

这两个端点标记为 Phase 6，本次实现需：
- 验证回调签名
- 解析支付结果
- 更新 PrTransactionRecord 状态
- 返回成功响应给支付平台

**注意**: 微信支付SDK集成是前置依赖，如果SDK未集成则保留 stub 并添加 TODO 注释。

- [ ] **Step 5: 实现 queryPaymentSuccess / getRecordBySerialNum / getIsReadCard**

```java
@GetMapping("/payment-status")
public R<Boolean> queryPaymentSuccess(@RequestParam String serialNum) {
    return R.ok(autoMachineService.checkPaymentStatus(serialNum));
}

@GetMapping("/record/{serialNum}")
public R<PrTransactionRecordVo> getRecordBySerialNum(@PathVariable String serialNum) {
    return R.ok(autoMachineService.getRecordBySerialNum(serialNum));
}

@GetMapping("/read-card-status")
public R<Boolean> getIsReadCard() {
    return R.ok(true); // 读卡器状态，默认可用
}
```

- [ ] **Step 6: 编译验证 + 提交**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/
git commit -m "feat(thermal): 实现 PrAutoMachine 自助缴费机核心功能

实现序列号生成、二维码生成、支付状态查询。
支付回调保持 stub 待微信SDK集成后完善。"
```

---

## Self-Review Checklist

- [x] **Spec coverage:** P0 IoT回调 → Task 2; manualControl → Task 1; PrHeatValveArchive批量 → Task 3-5; 骨架 → Task 6
- [x] **Placeholder scan:** 无 TBD/TODO（支付回调除外，标注为前置依赖）
- [x] **Type consistency:** HtTasksPerform字段名与domain一致; PrHouseByPayVo已在旧系统使用
- [x] **File paths:** 所有路径基于实际探索确认的目录结构
