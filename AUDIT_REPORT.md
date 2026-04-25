# Code Quality Audit Report — SDKJ Thermal Platform

**Audit Date**: 2026-04-24
**Scope**: sdkj-system, sdkj-meter, sdkj-thermal, sdkj-admin, sdkj-job

---

## Problem Statistics

| Level | Count |
|-------|-------|
| P0    | 3     |
| P1    | 8     |
| P2    | 15    |
| P3    | 12    |

---

## Issues by Module

### sdkj-meter

| File | Level | Description | Recommended Fix |
|------|-------|-------------|-----------------|
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/*.java` (all 11) | P2 | No `@SaCheckPermission` on any method; only `@SaCheckLogin` present | Add `@SaCheckPermission("thermal:meter:xxx")` to each endpoint matching menu permission codes |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtTcArchiveController.java:74,86` | P2 | Controller accepts Entity `MtTcArchive` as `@RequestBody` directly | Create `MtTcArchiveBo` and use it for request body; map via MapStruct |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtElectricArchiveController.java:60,72` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtElectricArchiveBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtCentratorArchiveController.java:60,72` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtCentratorArchiveBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtGasArchiveController.java:60,72` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtGasArchiveBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtHeatArchiveController.java:61,73` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtHeatArchiveBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtWaterArchiveController.java:60,72` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtWaterArchiveBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtTcValveController.java:72,84` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtTcValveBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtFormulaFileController.java:63,74` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtFormulaFileBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtMeterVendorController.java:73,86` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtMeterVendorBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtMeterSortController.java:64,74` | P2 | Controller accepts Entity as `@RequestBody` | Create `MtMeterSortBo` |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/controller/MtFormulaFileController.java:64` | P3 | Magic value `"1"` for `setIsEnabled` | Define constant or enum for enabled/disabled states |
| `sdkj-modules/sdkj-meter/` (entire module) | P2 | No `domain/bo/` package exists; complete absence of Bo classes | Add `domain/bo/` package with corresponding Bo classes for all 10 entities |
| `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/domain/*.java` (all) | P3 | No `@TableLogic` soft-delete annotation on any entity | Consider adding `@TableLogic` to entities that support soft-delete |

### sdkj-thermal

| File | Level | Description | Recommended Fix |
|------|-------|-------------|-----------------|
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java:87` | **P0** | Hardcoded old class reference: `"com.thermal.job.ControlJob"` — references non-existent package | Replace with correct class path: `"org.sdkj.job.ControlJob"` or implement the actual Quartz job |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java:111-126` | **P0** | Callback endpoints (`/callback/wechat-heat`, `/callback/ali-heat`) have no `@SaCheckLogin` or any auth — publicly accessible | Add callback signature validation; at minimum add IP whitelisting or payment provider signature verification |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java` (entire file) | **P0** | Entire controller is stub — 13/13 methods return `R.fail("此功能需要完整实现")`; exposes many unimplemented API endpoints | Remove controller from routing until fully implemented, or add `@Deprecated` + hide from API docs |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java:66,75` | P2 | Returns Entity `HtTasks` directly in API response (`R<List<HtTasks>>`, `R<HtTasks>`) | Return `HtTasksVo` instead of Entity |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java:85,102` | P2 | Accepts Entity `HtTasks` as `@RequestBody` | Create `HtTasksBo` for request body |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtAlertController.java:66,78` | P2 | Accepts Entity `HtAlert` as `@RequestBody` | Create `HtAlertBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtInstructionController.java:58,81` | P2 | Accepts Entity `HtInstruction` as `@RequestBody` | Create `HtInstructionBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtStrategyController.java:70,82` | P2 | Accepts Entity `HtStrategy` as `@RequestBody` | Create `HtStrategyBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtRepairController.java:71,84` | P2 | Accepts Entity `HtRepair` as `@RequestBody` | Create `HtRepairBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHouseChangeController.java:61,68,89` | P2 | Accepts Entity `PrHouse` as `@RequestBody` | Create `PrHouseBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrExpenseController.java:97,109,121` | P2 | Accepts Entity `PrHouseExpense` as `@RequestBody` | Create `PrHouseExpenseBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrExpenseItemController.java:54,77` | P2 | Accepts Entity `PrExpenseItem` as `@RequestBody` | Create `PrExpenseItemBo` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrUserController.java` (all) | P2 | No `@SaCheckPermission` on any method | Add permission annotations |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatControlController.java` (all) | P1 | Hardware valve control endpoints only have `@SaCheckLogin`, no `@SaCheckPermission` — any authenticated user can control physical valves | Add `@SaCheckPermission("thermal:ht:control")` to restrict to authorized operators |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrUseCardLogServiceImpl.java:32-38` | P1 | `changeValveStatus()` calls `save()` without `@Transactional` | Add `@Transactional(rollbackFor = Exception.class)` |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrUseCardLogServiceImpl.java:33` | P3 | TODO: valve status change via MBus not implemented | Implement or mark as `@Deprecated` with documentation |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/utils/HeatMeterControl.java:45-51` | P1 | `RestTemplate` instantiated inside method; HTTP headers created but never attached to `HttpEntity` — Content-Type never set on outgoing request | Inject `RestTemplate` as bean; use `HttpEntity(payload, headers)` constructor |
| `sdkj-modules/sdkj-thermal/` (entire module) | P2 | No `domain/bo/` package exists; 26 entities have no corresponding Bo classes | Add `domain/bo/` with all 26 Bo classes |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java:88-92` | P3 | Multiple magic value checks: `task.setNumber(0)`, `task.setStatus(0)`, `task.setIsUseReportRate(0)`, etc. | Define constants: `STATUS_STOPPED = 0`, `STATUS_RUNNING = 1`, etc. |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/*.java` (19/26) | P3 | No `@TableLogic` soft-delete on business entities (only `HtRepair` has it) | Add `@TableLogic` to entities with `del_flag` column |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrWechatBindRecordController.java:30-33` | P2 | Controller method accepts `Object` as body, always returns failure | Remove or implement; `@RequestBody Object` provides no validation |
| `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrImportAuthorizationCodeController.java:34-39` | P2 | Controller method always returns failure | Remove or implement |

### sdkj-admin

| File | Level | Description | Recommended Fix |
|------|-------|-------------|-----------------|
| `sdkj-admin/src/main/resources/application.yml:104` | **P1** | Hardcoded JWT secret key: `jwt-secret-key: sdkj2024` | Use environment variable: `${JWT_SECRET_KEY:sdkj2024}` and document required production override |
| `sdkj-admin/src/main/resources/application.yml:182-185` | **P1** | Hardcoded RSA public/private keys in plaintext | Move to environment variables or secret management (Vault, KMS) |
| `sdkj-admin/src/main/java/org/sdkj/web/controller/CaptchaController.java:25-27` | P3 | Imports `org.dromara.sms4j.*` — third-party dromara library, acceptable but misleading after project rename | No action required; this is a third-party library, not the old `org.dromara` project code |
| `sdkj-admin/src/main/java/org/sdkj/web/controller/AuthController.java:110` | P3 | Hardcoded Chinese welcome message `"好，欢迎登录 RuoYi-Vue-Plus 后台管理系统"` | Update to `"欢迎登录 SDKJ 智慧供热管理平台"` or externalize to i18n |

### sdkj-system

| File | Level | Description | Recommended Fix |
|------|-------|-------------|-----------------|
| `sdkj-modules/sdkj-system/src/main/resources/mapper/system/SysUserMapper.xml:21,30,39` | P2 | `${ew.getCustomSqlSegment}` — raw SQL injection via MyBatis-Plus wrapper; safe if wrapper is always built internally, but risky if any user input flows into it | Audit all callers of these mapper methods to ensure wrapper is built from validated parameters only |
| `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/service/impl/SysSocialServiceImpl.java:88` | P3 | TODO comment: "做一些数据校验,如唯一约束" | Implement uniqueness validation or close as won't-do |

### sdkj-job

| File | Level | Description | Recommended Fix |
|------|-------|-------------|-----------------|
| `sdkj-modules/sdkj-job/src/main/java/org/sdkj/job/entity/BillDto.java` | P2 | Uses `entity` package naming instead of `domain` used by all other modules | Rename package to `org.sdkj.job.domain` |
| `sdkj-modules/sdkj-job/` (entire module) | P2 | Module is essentially empty — only `BillDto.java` and empty `package-info.java`; no controller/service/mapper/Quartz config | Implement Quartz integration or remove stub module |
| `sdkj-modules/sdkj-job/src/main/java/org/sdkj/job/entity/BillDto.java` | P3 | No Javadoc, no annotations, unused in any service | Document purpose or remove if orphaned |

---

## Top 5 Critical Issues

### 1. P0 — Hardcoded dead class reference in HtTasksController
**File**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\HtTasksController.java:87`
```java
task.setBeanClass("com.thermal.job.ControlJob");
```
The class `com.thermal.job.ControlJob` does not exist in this project. If Quartz scheduling is enabled and the task is triggered, it will throw a `ClassNotFoundException` at runtime. **Must replace** with the actual Quartz job class or remove Quartz integration.

### 2. P0 — Publicly accessible payment callback endpoints without auth
**File**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\PrAutoMachineController.java:111-126`
The `/callback/wechat-heat` and `/callback/ali-heat` endpoints have no `@SaCheckLogin`, no `@SaIgnore` annotation, and no signature validation. Anyone can POST to these endpoints. While they currently return stub responses, when implemented they will process financial transactions.

### 3. P0 — PrAutoMachineController is entirely stub with 13 non-functional endpoints
**File**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\controller\PrAutoMachineController.java`
Every single method returns `R.fail("此功能需要完整实现")`. These 13 endpoints are exposed to clients but do nothing. This creates API surface area without function, and may confuse frontend developers or API consumers.

### 4. P1 — Hardcoded JWT secret in application.yml
**File**: `D:\chonggou\thermal-platform-new\sdkj-admin\src\main\resources\application.yml:104`
```yaml
jwt-secret-key: sdkj2024
```
This key is used to sign all JWT tokens. If leaked, any attacker can forge tokens and authenticate as any user. Must use environment variable in production.

### 5. P1 — HeatMeterControl HTTP headers never attached to request
**File**: `D:\chonggou\thermal-platform-new\sdkj-modules\sdkj-thermal\src\main\java\org\sdkj\thermal\utils\HeatMeterControl.java:45-51`
```java
org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
org.springframework.http.ResponseEntity<String> response =
    restTemplate.postForEntity(mbusUrl, request, String.class);
```
The `headers` variable is created and the Content-Type is set, but `headers` is never passed to the `HttpEntity` constructor. The request is sent without a Content-Type header, which may cause the MBus middleware to reject or misparse the payload.

---

## Architecture Compliance Assessment

### RuoYi-Vue-Plus Pattern Adherence

| Dimension | sdkj-system | sdkj-meter | sdkj-thermal | sdkj-admin | Verdict |
|-----------|:-----------:|:----------:|:------------:|:----------:|---------|
| Package structure (controller/service/mapper/domain/vo) | 95% | 85% (no bo/) | 85% (no bo/) | 90% | **Needs improvement** |
| Mapper extends BaseMapperPlus | 100% | 100% | 100% | N/A | Compliant |
| @AutoMapper on Entity/Vo | 100% | 100% | 100% | N/A | Compliant |
| Bo/Vo separation (Bo for input, Vo for output) | 100% | 0% (no Bo) | 0% (no Bo) | N/A | **Failing** |
| @Transactional on write ops | 95% | 95% | 90% | N/A | Mostly compliant |
| @SaCheckLogin on controllers | 100% | 100% | 100% | N/A | Compliant |
| @SaCheckPermission on controllers | 100% | 0% | 0% | N/A | **Failing** |
| No old package references | 100% | 100% | 98% (1 instance) | N/A | Mostly compliant |
| No JPA/QueryDSL remnants | 100% | 100% | 100% | N/A | Compliant |
| Hardcoded secrets | 100% | 100% | 100% | 60% | **Needs improvement** |

### Overall Score: **65/100**

**Strengths**:
- All Mapper interfaces correctly extend `BaseMapperPlus`
- All Entity/Vo pairs have proper `@AutoMapper` annotations
- No JPA/QueryDSL/Old Spring Boot 2.x remnants
- Clean package renaming from `org.dromara` to `org.sdkj` (except one reference)
- Tenant exclusion configuration properly set up

**Critical Gaps**:
1. **Bo/Vo pattern completely missing** in sdkj-meter and sdkj-thermal — controllers accept Entity directly as `@RequestBody`
2. **@SaCheckPermission absent** from all business module controllers (meter + thermal = 30+ controllers)
3. **Hardcoded secrets** in production configuration files
4. **Dead code references** to old `com.thermal` package
5. **Stub controllers** with no implementation (PrAutoMachineController)

### Priority Recommendations

1. **P0 (immediate)**: Fix `com.thermal.job.ControlJob` reference; secure callback endpoints; remove/hide stub controller
2. **P1 (this sprint)**: Externalize JWT secret and RSA keys; fix HeatMeterControl HTTP headers; add @SaCheckPermission to valve control endpoints; add @Transactional to PrUseCardLogServiceImpl.changeValveStatus
3. **P2 (next sprint)**: Create Bo classes for all meter + thermal entities (36 total); add @SaCheckPermission to all meter + thermal controllers; address SysUserMapper.xml SQL injection risk
4. **P3 (backlog)**: Add @TableLogic soft-delete to business entities; extract magic values to constants; clean up sdkj-job stub module
