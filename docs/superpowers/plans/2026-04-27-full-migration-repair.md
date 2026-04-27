# Migration Full Repair Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Based on the 8-module audit results, systematically repair all blocking and high-priority issues in the thermal-platform-new migration from thermal-balance-backend.

**Architecture:** 4 phases executed sequentially. Phase 1 (Security + Foundation) must complete first. Phase 2 (Core Engine) depends on Phase 1. Phase 3 (Feature Completeness) and Phase 4 (Enhancement) can overlap where independent.

**Tech Stack:** Spring Boot 3.5.12, Java 17, MyBatis-Plus 3.5.16, Sa-Token 1.44.0, Quartz, WXPay SDK, MySQL 8.0

---

## Phase 1: Security & Foundation (P0 - Blocking)

> Estimated: 15 tasks. All must complete before Phase 2 begins.

### Task 1: Remove hardcoded WeChat secrets

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatAuthServiceImpl.java:L32-L33`
- Modify: `sdkj-admin/src/main/resources/application-thermal.yml`

- [ ] **Step 1: Add config properties to application-thermal.yml**

Under the existing `thermal.wechat.auth` section, ensure these keys exist:
```yaml
thermal:
  wechat:
    auth:
      appid: wx4891a006517c8816
      app-secret: 0be23fed653d219cf0419ea82009593c
    pay:
      appid: ${thermal.wechat.auth.appid}
      mch-id: ""
      key: ""
      notify-url: ""
      refund-notify-url: ""
```

- [ ] **Step 2: Create WechatAuthConfig properties class**

Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/config/WechatAuthConfig.java`
```java
package org.sdkj.thermal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "thermal.wechat.auth")
public class WechatAuthConfig {
    private String appid;
    private String appSecret;
}
```

- [ ] **Step 3: Create WechatPayConfig properties class**

Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/config/WechatPayConfig.java`
```java
package org.sdkj.thermal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "thermal.wechat.pay")
public class WechatPayConfig {
    private String appid;
    private String mchId;
    private String key;
    private String notifyUrl;
    private String refundNotifyUrl;
}
```

- [ ] **Step 4: Update PrWechatAuthServiceImpl to use injected config**

Modify: `PrWechatAuthServiceImpl.java` - Remove lines 32-33 (hardcoded constants), inject `WechatAuthConfig` via constructor:
```java
@RequiredArgsConstructor
public class PrWechatAuthServiceImpl extends ServiceImpl<PrWechatUserMapper, PrWechatUser> implements IPrWechatAuthService {

    private final WechatAuthConfig wechatAuthConfig;

    // Replace all APP_ID references with wechatAuthConfig.getAppid()
    // Replace all APP_SECRET references with wechatAuthConfig.getAppSecret()
```

- [ ] **Step 5: Compile and verify**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/config/ sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatAuthServiceImpl.java sdkj-admin/src/main/resources/application-thermal.yml
git commit -m "fix: remove hardcoded wechat secrets, use config injection"
```

---

### Task 2: Fix hardcoded password in AgCompanyServiceImpl

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AgCompanyServiceImpl.java:L100,L159`

- [ ] **Step 1: Unify password handling in registerCompany**

In `AgCompanyServiceImpl.java`, line 159, replace hardcoded bcrypt hash with dynamic BCrypt:
```java
// Before (L159):
adminUser.setUserPwd("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs");

// After:
adminUser.setUserPwd(BCrypt.hashpw("123456"));
```

Verify `BCrypt` import exists (already used at L100).

- [ ] **Step 2: Compile and verify**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AgCompanyServiceImpl.java
git commit -m "fix: use dynamic BCrypt hash instead of hardcoded password in registerCompany"
```

---

### Task 3: Fix AccessCodeController SQL injection risk

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AccessCodeController.java:L50-L75`

- [ ] **Step 1: Add table name whitelist validation**

In `AccessCodeController.java`, replace the switch-based table name resolution with a whitelist Map and add validation:
```java
private static final Map<String, String> METER_TABLE_MAP = Map.of(
    "01", "mt_electric_archive",
    "02", "mt_water_archive",
    "03", "mt_heat_archive",
    "04", "mt_gas_archive"
);

// In meterCode method, replace switch block:
String tableName = METER_TABLE_MAP.get(meterType);
if (tableName == null) {
    return R.fail("Invalid meter type: " + meterType);
}
```

- [ ] **Step 2: Unify return type to R<String>**

Change all three methods' return type from `String` to `R<String>`:
```java
// Before:
public String vendorCode(...)
// After:
public R<String> vendorCode(...)
```

Wrap all return values in `R.ok(result)`.

- [ ] **Step 3: Compile and verify**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AccessCodeController.java
git commit -m "fix: add SQL table name whitelist and unify return type in AccessCodeController"
```

---

### Task 4: Fix IoT callback token bypass

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/IoTCallbackController.java:L238-L243`

- [ ] **Step 1: Enforce token validation**

In `verifyCallbackToken` method (around L238), change empty-token behavior:
```java
// Before: if token is empty, return true (bypass)
// After:
private boolean verifyCallbackToken(String token) {
    String expectedToken = iotCallbackToken; // injected from config
    if (expectedToken == null || expectedToken.isEmpty()) {
        log.warn("IoT callback token not configured, skipping validation");
        return true; // only skip if server-side config is missing
    }
    if (token == null || token.isEmpty()) {
        log.warn("IoT callback request missing token");
        return false;
    }
    return expectedToken.equals(token);
}
```

- [ ] **Step 2: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/IoTCallbackController.java
git commit -m "fix: enforce IoT callback token validation, block empty-token requests"
```

---

### Task 5: Add missing mt_formula_file DDL

**Files:**
- Modify: `script/sql/phase3_meter_tables.sql`

- [ ] **Step 1: Read old system formula_file table structure**

Read the old system SQL files to find the `mt_formula_file` table definition:
- Check: `D:/chonggou/thermal-balance-backend/sql/`

- [ ] **Step 2: Add mt_formula_file CREATE TABLE to phase3_meter_tables.sql**

Append to the end of `phase3_meter_tables.sql`:
```sql
-- 公式档案表
CREATE TABLE IF NOT EXISTS `mt_formula_file` (
  `id` bigint NOT NULL COMMENT 'ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT 'tenant_id',
  `create_dept` bigint DEFAULT NULL COMMENT 'create_dept',
  `create_by` bigint DEFAULT NULL COMMENT 'create_by',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `update_by` bigint DEFAULT NULL COMMENT 'update_by',
  `update_time` datetime DEFAULT NULL COMMENT 'update_time',
  `name` varchar(100) DEFAULT NULL COMMENT 'formula_name',
  `type` varchar(10) DEFAULT NULL COMMENT 'formula_type',
  `content` text COMMENT 'formula_content',
  `elements` text COMMENT 'formula_elements',
  `status` char(1) DEFAULT '0' COMMENT 'status(0_enabled 1_disabled)',
  `remark` varchar(500) DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='formula_file';
```

Adjust field names/types to match the existing `MtFormulaFile.java` domain class fields.

- [ ] **Step 3: Also add to sdkj-init.sql if it contains meter tables**

Check if `sdkj-init.sql` includes meter tables and add the same DDL there.

- [ ] **Step 4: Commit**

```bash
git add script/sql/phase3_meter_tables.sql script/sql/sdkj-init.sql
git commit -m "fix: add missing mt_formula_file DDL to SQL scripts"
```

---

### Task 6: Restore WeChat payment - unified order API call

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java:L31-L52`
- Reference: Old system `WechatPayServiceImpl.java:L78-L187`

- [ ] **Step 1: Inject WechatPayConfig and initialize WXPay instance**

Add to `PrWechatPayServiceImpl`:
```java
private final WechatPayConfig payConfig;
private WXPay wxPay;

@PostConstruct
public void init() {
    WXPayConfigImpl config = new WXPayConfigImpl();
    config.setAppId(payConfig.getAppid());
    config.setMchId(payConfig.getMchId());
    config.setKey(payConfig.getKey());
    this.wxPay = new WXPay(config);
}
```

Create `WXPayConfigImpl` (implements the abstract `WXPayConfig` already in `wechat/wxPay/`) reading from `WechatPayConfig`.

- [ ] **Step 2: Restore createOrder with real WXPay.unifiedOrder()**

Replace the simplified `createOrder` method with logic from old system L78-L187:
```java
@Override
@Transactional(rollbackFor = Exception.class)
public Map<String, Object> createOrder(String openId, String otherCode, String houseAddress,
                                        BigDecimal totalFee, String body, String attach, String operator) {
    String outTradeNo = "WX" + System.currentTimeMillis() + (int)(Math.random() * 9000 + 1000);

    // Create local order record
    PrWechatOrder order = new PrWechatOrder();
    order.setOutTradeNo(outTradeNo);
    // ... set fields ...
    baseMapper.insert(order);

    // Call WeChat unified order API
    Map<String, String> params = new HashMap<>();
    params.put("appid", payConfig.getAppid());
    params.put("mch_id", payConfig.getMchId());
    params.put("nonce_str", WXPayUtil.generateNonceStr());
    params.put("body", body);
    params.put("out_trade_no", outTradeNo);
    params.put("total_fee", String.valueOf(totalFee.multiply(new BigDecimal("100")).intValue()));
    params.put("spbill_create_ip", "127.0.0.1");
    params.put("notify_url", payConfig.getNotifyUrl());
    params.put("trade_type", "JSAPI");
    params.put("openid", openId);
    params.put("attach", attach);

    try {
        Map<String, String> result = wxPay.unifiedOrder(params);
        String returnCode = result.get("return_code");
        String resultCode = result.get("result_code");
        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            String prepayId = result.get("prepay_id");
            // Generate JSAPI payment parameters for frontend
            Map<String, Object> jsapiParams = new HashMap<>();
            jsapiParams.put("appId", payConfig.getAppid());
            jsapiParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            jsapiParams.put("nonceStr", WXPayUtil.generateNonceStr());
            jsapiParams.put("package", "prepay_id=" + prepayId);
            jsapiParams.put("signType", "MD5");
            jsapiParams.put("paySign", WXPayUtil.generateSignature(jsapiParams, payConfig.getKey()));
            jsapiParams.put("outTradeNo", outTradeNo);
            return jsapiParams;
        } else {
            throw new ServiceException("WeChat unified order failed: " + result.get("return_msg"));
        }
    } catch (Exception e) {
        throw new ServiceException("WeChat unified order error: " + e.getMessage());
    }
}
```

- [ ] **Step 3: Compile and verify**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/config/WechatPayConfig.java
git commit -m "fix: restore WeChat unified order API call in createOrder"
```

---

### Task 7: Restore WeChat payment callback - signature verification

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java:L56-L80`
- Reference: Old system `WechatPayServiceImpl.java:L191-L283`

- [ ] **Step 1: Restore handlePayNotify with full security**

Replace the simplified `handlePayNotify`:
```java
@Override
@Transactional(rollbackFor = Exception.class)
public String handlePayNotify(HttpServletRequest request) {
    try {
        String xml = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

        // 1. Signature verification
        if (!WXPayUtil.isSignatureValid(resultMap, payConfig.getKey())) {
            log.error("WeChat pay notify: invalid signature");
            return buildXmlResponse("FAIL", "Invalid signature");
        }

        // 2. Return status check
        if (!"SUCCESS".equals(resultMap.get("return_code"))) {
            return buildXmlResponse("FAIL", "Return code not SUCCESS");
        }
        if (!"SUCCESS".equals(resultMap.get("result_code"))) {
            return buildXmlResponse("SUCCESS", "OK"); // Acknowledge but no action
        }

        String outTradeNo = resultMap.get("out_trade_no");
        String transactionId = resultMap.get("transaction_id");
        BigDecimal notifyAmount = new BigDecimal(resultMap.get("total_fee")).divide(new BigDecimal("100"));

        // 3. Query local order
        PrWechatOrder order = lambdaQuery().eq(PrWechatOrder::getOutTradeNo, outTradeNo).one();
        if (order == null) {
            log.error("WeChat pay notify: order not found {}", outTradeNo);
            return buildXmlResponse("FAIL", "Order not found");
        }

        // 4. Idempotency: already paid
        if ("1".equals(order.getStatus())) {
            return buildXmlResponse("SUCCESS", "OK");
        }

        // 5. Amount verification
        if (order.getTotalFee().compareTo(notifyAmount) != 0) {
            log.error("WeChat pay notify: amount mismatch. Order={}, Notify={}", order.getTotalFee(), notifyAmount);
            return buildXmlResponse("FAIL", "Amount mismatch");
        }

        // 6. Update order status
        order.setStatus("1");
        order.setTransactionId(transactionId);
        order.setPayTime(LocalDateTime.now());
        baseMapper.updateById(order);

        // 7. Business logic: update transaction record, expense, house status
        // These should call existing service methods in the new system
        // insertPrTransactionRecordByWechat(order);
        // updatePrExponseByWechat(order);
        // updatePrHouse(order);

        return buildXmlResponse("SUCCESS", "OK");
    } catch (Exception e) {
        log.error("WeChat pay notify error", e);
        return buildXmlResponse("FAIL", "System error");
    }
}
```

- [ ] **Step 2: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java
git commit -m "fix: restore payment callback signature verification and amount check"
```

---

### Task 8: Restore WeChat refund API call

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java:L89-L120,L124-L147`

- [ ] **Step 1: Restore applyRefund with real WXPay.refund()**

Following the pattern from old system `WechatPayServiceImpl.java:L296-L396`:
```java
@Override
@Transactional(rollbackFor = Exception.class)
public Map<String, Object> applyRefund(String outTradeNo, BigDecimal refundFee, String reason, String operator) {
    PrWechatOrder order = lambdaQuery().eq(PrWechatOrder::getOutTradeNo, outTradeNo).one();
    if (order == null) throw new ServiceException("Order not found");
    if (!"1".equals(order.getStatus())) throw new ServiceException("Order not paid, cannot refund");

    String outRefundNo = "RF" + System.currentTimeMillis();

    Map<String, String> params = new HashMap<>();
    params.put("appid", payConfig.getAppid());
    params.put("mch_id", payConfig.getMchId());
    params.put("nonce_str", WXPayUtil.generateNonceStr());
    params.put("out_trade_no", outTradeNo);
    params.put("out_refund_no", outRefundNo);
    params.put("total_fee", String.valueOf(order.getTotalFee().multiply(new BigDecimal("100")).intValue()));
    params.put("refund_fee", String.valueOf(refundFee.multiply(new BigDecimal("100")).intValue()));
    params.put("notify_url", payConfig.getRefundNotifyUrl());

    try {
        Map<String, String> result = wxPay.refund(params);
        if ("SUCCESS".equals(result.get("return_code")) && "SUCCESS".equals(result.get("result_code"))) {
            // Create local refund record
            PrWechatRefund refund = new PrWechatRefund();
            refund.setOutRefundNo(outRefundNo);
            refund.setOutTradeNo(outTradeNo);
            refund.setRefundFee(refundFee);
            refund.setStatus("0"); // processing
            // ... save ...
            return Map.of("outRefundNo", outRefundNo, "status", "PROCESSING");
        } else {
            throw new ServiceException("WeChat refund failed: " + result.get("return_msg"));
        }
    } catch (Exception e) {
        throw new ServiceException("WeChat refund error: " + e.getMessage());
    }
}
```

- [ ] **Step 2: Restore handleRefundNotify with signature verification**

```java
@Override
public String handleRefundNotify(HttpServletRequest request) {
    try {
        String xml = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

        if (!WXPayUtil.isSignatureValid(resultMap, payConfig.getKey())) {
            return buildXmlResponse("FAIL", "Invalid signature");
        }

        String outRefundNo = resultMap.get("out_refund_no");
        String refundStatus = resultMap.get("refund_status");

        PrWechatRefund refund = refundMapper.selectByOutRefundNo(outRefundNo);
        if (refund != null) {
            refund.setStatus("SUCCESS".equals(refundStatus) ? "1" : "2");
            refundMapper.updateById(refund);
        }
        return buildXmlResponse("SUCCESS", "OK");
    } catch (Exception e) {
        log.error("WeChat refund notify error", e);
        return buildXmlResponse("FAIL", "System error");
    }
}
```

- [ ] **Step 3: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java
git commit -m "fix: restore real WeChat refund API call and refund callback verification"
```

---

### Task 9: Add Quartz job startup loading mechanism

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/QuartzJobInitRunner.java`
- Reference: Old system `ScheduleJobInitListener.java` (51 lines)

- [ ] **Step 1: Create startup runner**

Create `QuartzJobInitRunner.java`:
```java
package org.sdkj.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.service.IHtTasksService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJobInitRunner implements CommandLineRunner {

    private final IHtTasksService tasksService;
    private final ThermalJobManager thermalJobManager;

    @Override
    public void run(String... args) {
        log.info("Initializing Quartz jobs from database...");
        try {
            // Load all active thermal tasks (status=1) and schedule them
            List<HtTasks> activeTasks = tasksService.lambdaQuery()
                .eq(HtTasks::getStatus, 1)
                .isNotNull(HtTasks::getCron)
                .list();

            for (HtTasks task : activeTasks) {
                try {
                    thermalJobManager.addJob(task.getId());
                    log.info("Scheduled thermal task: id={}, name={}", task.getId(), task.getName());
                } catch (Exception e) {
                    log.error("Failed to schedule task id={}: {}", task.getId(), e.getMessage());
                }
            }
            log.info("Quartz job initialization complete. {} tasks scheduled.", activeTasks.size());
        } catch (Exception e) {
            log.error("Quartz job initialization failed", e);
        }
    }
}
```

- [ ] **Step 2: Add tenant-aware job scheduling (multi-tenant support)**

The old system iterates tenants and switches data sources. In the new system's shared-database model, tasks already have `tenant_id`. Verify that `HtTasks` entity has `tenant_id` and that `@TenantLine` filter works in Quartz context. If `@TenantLine` AOP doesn't work outside HTTP context, add explicit tenant handling:

```java
// If needed, use TenantHelper.ignore() to load all tenants' tasks
List<HtTasks> activeTasks = TenantHelper.ignore(() ->
    tasksService.lambdaQuery()
        .eq(HtTasks::getStatus, 1)
        .isNotNull(HtTasks::getCron)
        .list()
);
```

- [ ] **Step 3: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/QuartzJobInitRunner.java
git commit -m "feat: add Quartz job startup loader for thermal tasks"
```

---

### Task 10: Add thermal cascade sync for meter updates

**Files:**
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtHeatArchiveServiceImpl.java` (update method)
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtTcArchiveServiceImpl.java` (update method)
- Modify: `sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/MtTcValveServiceImpl.java` (update method)

- [ ] **Step 1: Read old system cascade logic**

Read the old system update methods to understand which downstream tables are updated:
- `MtHeatArchiveServiceImpl.updateData` -> updates `pr_heat_hot_archive` and `pr_heat_unit_hot_archive` name field
- `MtTcArchiveServiceImpl.updateData` -> updates `pr_heat_temp_archive` name field
- `MtTcValveServiceImpl.updateData` -> updates `pr_heat_valve_archive` and `pr_heat_unit_valve_archive` name field

- [ ] **Step 2: Add cascade update after MtHeatArchiveServiceImpl.update**

In the update method, after `updateById()`, add cascade:
```java
// After updating the heat archive, sync name to property archives
if (bo.getName() != null) {
    // Update pr_heat_hot_archive name where meter_num matches
    // Update pr_heat_unit_hot_archive name where meter_num matches
    // Use Mapper methods from sdkj-thermal or direct SQL
}
```

Note: Since `sdkj-meter` cannot directly depend on `sdkj-thermal`, use an event mechanism or add Mapper in `sdkj-meter` that queries thermal tables via cross-module Mapper XML.

- [ ] **Step 3: Add cascade for MtTcArchiveServiceImpl and MtTcValveServiceImpl**

Same pattern as Step 2 for the other two archives.

- [ ] **Step 4: Compile and commit**

```bash
git add sdkj-modules/sdkj-meter/src/main/java/org/sdkj/meter/service/impl/
git commit -m "fix: add cascade name sync when updating meter archives"
```

---

### Task 11: Add thermal task termination conditions

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksServiceImpl.java` (saveValveAngle method)
- Reference: Old system `ControlJob.java:L76-L148`

- [ ] **Step 1: Add balance rate and days checking to saveValveAngle**

In `HtTasksServiceImpl.saveValveAngle()`, add termination logic before executing regulation:
```java
// After fetching task and strategy, before regulation logic:

// 1. Check regulation days
long regulatedDays = calculateRegulatedDays(task);
long remainingDays = task.getDays() - regulatedDays;
if (remainingDays <= 0) {
    stopTask(taskId, "Regulation period ended");
    return;
}

// 2. Check balance rate
BigDecimal balanceRate = calculateBalanceRate(task, scopeType);
BigDecimal standardRate = task.getStandard();
if (balanceRate != null && standardRate != null && balanceRate.compareTo(standardRate) <= 0) {
    stopTask(taskId, "Balance rate achieved");
    return;
}

// 3. Check regulation count limit
if (task.getNums() != null && task.getNums() > 0 && task.getNumber() >= task.getNums()) {
    stopTask(taskId, "Regulation count limit reached");
    return;
}
```

- [ ] **Step 2: Implement stopTask helper method**

```java
private void stopTask(Integer taskId, String reason) {
    lambdaUpdate().eq(HtTasks::getId, taskId)
        .set(HtTasks::getStatus, 0)
        .update();
    thermalJobManager.deleteJob(taskId);
    log.info("Task {} stopped: {}", taskId, reason);
}
```

- [ ] **Step 3: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksServiceImpl.java
git commit -m "feat: add task termination conditions (days/balance/count) to thermal regulation"
```

---

### Task 12: SysTenant table extension - add company business fields

**Files:**
- Modify: `script/sql/sdkj-init.sql` (sys_tenant table)
- Modify: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/domain/SysTenant.java`
- Modify: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/domain/bo/SysTenantBo.java`
- Modify: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/domain/vo/SysTenantVo.java`

- [ ] **Step 1: Add columns to sys_tenant table**

Execute SQL to add missing business fields from old `sys_company`:
```sql
ALTER TABLE `sys_tenant`
  ADD COLUMN `nature` char(1) DEFAULT NULL COMMENT 'nature(0_operator 1_agent 2_property 3_merchant 4_service 5_other)',
  ADD COLUMN `business_license` varchar(50) DEFAULT NULL COMMENT 'business_license',
  ADD COLUMN `legal_person` varchar(50) DEFAULT NULL COMMENT 'legal_person',
  ADD COLUMN `bank_name` varchar(100) DEFAULT NULL COMMENT 'bank_name',
  ADD COLUMN `bank_address` varchar(200) DEFAULT NULL COMMENT 'bank_address',
  ADD COLUMN `account_name` varchar(100) DEFAULT NULL COMMENT 'account_name',
  ADD COLUMN `corporate_account` varchar(50) DEFAULT NULL COMMENT 'corporate_account',
  ADD COLUMN `province` varchar(50) DEFAULT NULL COMMENT 'province',
  ADD COLUMN `city` varchar(50) DEFAULT NULL COMMENT 'city',
  ADD COLUMN `county` varchar(50) DEFAULT NULL COMMENT 'county',
  ADD COLUMN `address` varchar(300) DEFAULT NULL COMMENT 'address',
  ADD COLUMN `longitude` decimal(10,6) DEFAULT NULL COMMENT 'longitude',
  ADD COLUMN `latitude` decimal(10,6) DEFAULT NULL COMMENT 'latitude';
```

- [ ] **Step 2: Update SysTenant domain/bo/vo classes**

Add the new fields to `SysTenant.java` entity, `SysTenantBo.java` and `SysTenantVo.java`.

- [ ] **Step 3: Update sdkj-init.sql DDL**

Update the `CREATE TABLE sys_tenant` in `sdkj-init.sql` to include the new columns.

- [ ] **Step 4: Compile and commit**

```bash
git add script/sql/sdkj-init.sql sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/domain/
git commit -m "feat: extend sys_tenant with company business fields from old sys_company"
```

---

## Phase 2: Core Engine Restoration (P0 - Blocking)

> Estimated: 8 tasks. Depends on Phase 1 completion.

### Task 13: Restore ControlJob full regulation engine

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ThermalJob.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtTasksServiceImpl.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/ThermalRegulationEngine.java`
- Reference: Old system `ControlJob.java` (154 lines) + `HtTasksMapper.xml` SQL methods

- [ ] **Step 1: Create ThermalRegulationEngine service**

Extract the regulation engine as a dedicated service to keep ThermalJob simple:

```java
package org.sdkj.thermal.service.impl;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThermalRegulationEngine {

    private final IHtTasksService tasksService;
    private final HtTasksMapper tasksMapper;

    /**
     * Execute full regulation cycle for a task.
     * Ported from old system ControlJob.executeJob()
     */
    public void executeRegulation(Integer taskId, Integer scopeType) {
        HtTasks task = tasksService.getById(taskId);
        HtStrategy strategy = // load strategy;

        // 1. Calculate regulated days and duration
        // 2. Get average return water temperature based on adjustBasis + scopeType
        // 3. Update execution stats
        // 4. Check termination conditions (days, balance rate, count)
        // 5. Execute regulation based on scopeType:
        //    scopeType 1/2/4: single valve - 5 step process
        //    scopeType 3: DTU broadcast
        // 6. Auto-stop if conditions met
    }
}
```

- [ ] **Step 2: Port the 5-step single valve regulation process**

For `scopeType 1/2/4`, the old system executes:
```
deleteHtTasksPerformLs -> insertHtTasksPerformLs -> instructionGeneration -> updateInstructionGeneration -> insertHtTasksPerform
```

Port each step by:
1. Copying the Mapper XML SQL from old system to new `HtTasksMapper.xml`
2. Adding corresponding Mapper interface methods
3. Calling them in sequence from `ThermalRegulationEngine`

- [ ] **Step 3: Port the adjustBasis branches (0/1/3/4)**

The old system `ControlJob` L55-L71 has 4 `adjustBasis` branches for getting average return water temperature:
- 0: Single strategy (no temperature calc)
- 1: Return water temperature
- 3: Average return water temperature (DTU broadcast)
- 4: Return water temperature with heating coefficient

Port each branch's temperature calculation logic.

- [ ] **Step 4: Update ThermalJob to use the engine**

```java
@Override
public void execute(JobExecutionContext context) throws JobExecutionException {
    Integer taskId = (Integer) context.getMergedJobDataMap().get("taskId");
    Integer scopeType = (Integer) context.getMergedJobDataMap().get("scopeType");

    // Get Spring bean from SchedulerContext
    ApplicationContext ctx = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
    ThermalRegulationEngine engine = ctx.getBean(ThermalRegulationEngine.class);

    try {
        engine.executeRegulation(taskId, scopeType);
    } catch (Exception e) {
        log.error("Thermal regulation failed for task {}", taskId, e);
    }
}
```

- [ ] **Step 5: Copy all regulation-related Mapper XML SQL from old system**

The old system `HtTasksMapper.xml` contains critical SQL methods:
- `deleteHtTasksPerformLs`
- `insertHtTasksPerformLs` (multiple variants for different adjustBasis)
- `instructionGeneration` (multiple variants)
- `updateInstructionGeneration` (multiple variants)
- `insertHtTasksPerform`
- `insertHtTasksPerformDtu`

Copy all of these to the new system's `sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksMapper.xml`, adapting table names and field names if needed.

- [ ] **Step 6: Compile, test, and commit**

Run: `mvn compile -pl sdkj-modules/sdkj-thermal -am -q`

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/ThermalRegulationEngine.java sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ThermalJob.java sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksMapper.xml
git commit -m "feat: restore full thermal regulation engine with all adjustBasis branches"
```

---

### Task 14: Add ControlReturnApi callback endpoints

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/ControlReturnApiController.java`
- Reference: Old system `ControlReturnApi.java` (869 lines)

- [ ] **Step 1: Create the controller with 5 endpoints**

```java
@RestController
@RequestMapping("/api/returnControl")
@Slf4j
@RequiredArgsConstructor
public class ControlReturnApiController {

    private final IHtTasksPerformService tasksPerformService;

    @PostMapping("/returnControl")
    public String returnControl(@RequestParam String msg) {
        // Port from old ControlReturnApi.returnControl (L41-L277)
        // Parse msg JSON, switch on type (1-5), process control feedback
    }

    @PostMapping("/valveData")
    public String valveData(@RequestParam String msg) {
        // Port from old ControlReturnApi.valveData (L280-L559)
        // Parse valve data, insert readings, update archives, detect anomalies
    }

    @PostMapping("/tempData")
    public String tempData(@RequestParam String msg) {
        // Port from old ControlReturnApi.tempData (L561-L697)
    }

    @PostMapping("/hotData")
    public String hotData(@RequestParam String msg) {
        // Port from old ControlReturnApi.hotData (L699-L821)
    }

    @PostMapping("/dtuData")
    public String dtuData(@RequestParam String msg) {
        // Port from old ControlReturnApi.dtuData (L823-L867)
    }
}
```

- [ ] **Step 2: Port the returnControl method (5 device types)**

Port the L41-L277 logic. The old system parses JSON `msg` containing `type` field, then processes:
- type 1: Valve control feedback
- type 2: Heat meter feedback
- type 3: Thermostat feedback
- type 4: Wireless bypass valve feedback
- type 5: Collector feedback

Each branch updates `ht_tasks_perform` via Mapper methods that already exist in new system (`HtTasksPerformMapper`).

- [ ] **Step 3: Port valveData with anomaly detection**

Port L280-L559. Key logic:
- Insert reading data
- Update valve archive (4 variants based on `dataFeild`: 31D1/1F90/2190/other)
- Anomaly detection (L526-L552): paid-but-closed valve, unpaid-but-open valve, temperature anomaly

- [ ] **Step 4: Port tempData, hotData, dtuData**

Port the remaining 3 endpoints. These are simpler than returnControl and valveData.

- [ ] **Step 5: Ensure all referenced Mapper methods exist**

Verify that `HtTasksPerformMapper.java` and its XML have all methods called by the callback logic:
- `insertReading`, `insertTempReading`, `insertHotReading`
- `updateValveArchive`, `updateTempArchive`, `updateHotArchive`, `updateDtuArchive`
- `updateByreturnControl`
- `inserHtAlert`

- [ ] **Step 6: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/ControlReturnApiController.java
git commit -m "feat: add ControlReturnApi callback endpoints for device control feedback"
```

---

### Task 15: Create missing scheduled jobs - business logic

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/HeatDailyGenerateJob.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/HeatMonthGenerateJob.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/SendMeterCommandJob.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ValvePaymentControlJob.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ValveStatusQueryJob.java`

- [ ] **Step 1: Create HeatDailyGenerateJob**

```java
@Slf4j
@Component
public class HeatDailyGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ApplicationContext ctx = (ApplicationContext) context.getScheduler()
            .getContext().get("applicationContext");
        IPrHeatDailyService heatDailyService = ctx.getBean(IPrHeatDailyService.class);

        try {
            log.info("Starting heat daily generation job");
            heatDailyService.generateHeatDaily();
            log.info("Heat daily generation job completed");
        } catch (Exception e) {
            log.error("Heat daily generation job failed", e);
        }
    }
}
```

- [ ] **Step 2: Create HeatMonthGenerateJob**

First ensure `IPrHeatMonthService.generateHeatMonth()` exists. If not, port from old `JieXiuHeatMonthJob`:
```java
// In IPrHeatMonthService:
void generateHeatMonth();

// In PrHeatMonthServiceImpl:
@Override
@Transactional(rollbackFor = Exception.class)
public void generateHeatMonth() {
    // Port from old JieXiuHeatMonthJob 5-step logic:
    // 1. insertPrHeatMonth - create monthly records
    // 2. updateStartReading - set start reading from previous month end
    // 3. updateQty - calculate consumption
    // 4. setFee - calculate fees
    // 5. updateArrearage - update arrears status
}
```

- [ ] **Step 3: Create SendMeterCommandJob**

```java
@Slf4j
@Component
public class SendMeterCommandJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Query pending commands and send them
        // Port from old StartSendMeterCommandJob
        // Uses tasksPerformService.executeValveControlTasks()
    }
}
```

- [ ] **Step 4: Create ValvePaymentControlJob and ValveStatusQueryJob**

Port from old `SetValveStatusGetByPayNew` and `GetValveStatusGetByPayNew`:
```java
@Slf4j
@Component
public class ValvePaymentControlJob implements Job {
    // Query paid-but-closed valves -> generate open commands
    // Query unpaid-but-open valves -> generate close commands
}
```

- [ ] **Step 5: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/
git commit -m "feat: create heat daily/monthly generate jobs, meter command job, valve payment control jobs"
```

---

### Task 16: Create agent meter allocation module

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgMeterController.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAgMeterService.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AgMeterServiceImpl.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/resources/mapper/AgMeterMapper.xml`
- Reference: Old system `AgentMeterController.java` (52 lines)

- [ ] **Step 1: Create AgMeterController with 3 endpoints**

```java
@RestController
@RequestMapping("/thermal/agent/meter")
@SaCheckLogin
@RequiredArgsConstructor
public class AgMeterController {

    private final IAgMeterService agMeterService;

    @GetMapping("/allocated")
    @SaCheckPermission("thermal:agent:meter:list")
    public R<TableDataInfo<MtMeterMatchVo>> allocatedMeters(
            @RequestParam String companyId,
            @RequestParam String meterType,
            PageQuery pageQuery) {
        return R.ok(agMeterService.getAllocatedMeters(companyId, meterType, pageQuery));
    }

    @GetMapping("/all")
    @SaCheckPermission("thermal:agent:meter:list")
    public R<TableDataInfo<MtArchiveVo>> allMeters(
            @RequestParam String companyId,
            @RequestParam String meterType,
            PageQuery pageQuery) {
        return R.ok(agMeterService.getAllMeters(companyId, meterType, pageQuery));
    }

    @PostMapping("/allocate")
    @SaCheckPermission("thermal:agent:meter:add")
    @Log(title = "Agent meter allocation", businessType = BusinessType.INSERT)
    public R<Void> allocate(@RequestBody AgMeterAllocationBo bo) {
        agMeterService.allocateMeters(bo);
        return R.ok();
    }
}
```

- [ ] **Step 2: Implement IAgMeterService**

Port the 3-way meter type branching from old system (01=electric, 02=water, 03=heat, 04=gas).

- [ ] **Step 3: Create Mapper XML with allocation SQL**

Copy from old `AgentMeterMapper.xml`.

- [ ] **Step 4: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgMeterController.java sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAgMeterService.java sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AgMeterServiceImpl.java sdkj-modules/sdkj-thermal/src/main/resources/mapper/AgMeterMapper.xml
git commit -m "feat: add agent meter allocation module"
```

---

### Task 17: Add ControlJob tenant context handling

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ThermalJob.java`
- Reference: Old system `BaseJob.java` (42 lines)

- [ ] **Step 1: Add tenant context setup in ThermalJob**

The new system uses `@TenantLine` AOP which may not work in Quartz Job threads. Add explicit tenant handling:
```java
@Override
public void execute(JobExecutionContext context) throws JobExecutionException {
    String tenantId = (String) context.getMergedJobDataMap().get("tenantId");

    try {
        if (tenantId != null) {
            TenantHelper.setTenantId(tenantId);
        }

        // ... existing job logic ...

    } finally {
        TenantHelper.clearTenantId();
    }
}
```

- [ ] **Step 2: Update ThermalJobManager to pass tenantId in JobDataMap**

In `addJob` method, add tenant info:
```java
jobDataMap.put("tenantId", TenantHelper.getTenantId());
```

- [ ] **Step 3: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/
git commit -m "fix: add tenant context handling in Quartz thermal jobs"
```

---

### Task 18: Restore payment post-business logic

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrTransactionRecordService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrExpenseService.java`
- Reference: Old system `WechatPayServiceImpl.java:L259-L267`

- [ ] **Step 1: Add payment callback business methods**

In `handlePayNotify`, after order status update (Step 7 in Task 7), add:
```java
// Create transaction record
transactionRecordService.createWechatPaymentRecord(order);

// Update expense status
expenseService.updatePaymentStatus(order.getHouseId(), order.getExpenseItemId(), order.getTotalFee());

// Update house payment status
houseService.updateHousePayStatus(order.getHouseId(), true);
```

- [ ] **Step 2: Implement createWechatPaymentRecord in IPrTransactionRecordService**

Port from old system `insertPrTransactionRecordByWechat` Mapper SQL.

- [ ] **Step 3: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/
git commit -m "feat: restore payment callback business logic (transaction record + expense + house status)"
```

---

### Task 19: Add expired order cancellation

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java`
- Reference: Old system `WechatPayServiceImpl.java:L472-L491`

- [ ] **Step 1: Add cancelExpiredOrders scheduled method**

```java
@Scheduled(cron = "0 0 2 * * ?")
@Transactional(rollbackFor = Exception.class)
public void cancelExpiredOrders() {
    LocalDateTime expireTime = LocalDateTime.now().minusHours(24);
    List<PrWechatOrder> expiredOrders = lambdaQuery()
        .eq(PrWechatOrder::getStatus, "0")
        .lt(PrWechatOrder::getCreateTime, expireTime)
        .list();

    for (PrWechatOrder order : expiredOrders) {
        order.setStatus("2"); // cancelled
        baseMapper.updateById(order);
    }
    log.info("Cancelled {} expired WeChat orders", expiredOrders.size());
}
```

- [ ] **Step 2: Enable scheduling**

Ensure `@EnableScheduling` is on the main application class.

- [ ] **Step 3: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrWechatPayServiceImpl.java
git commit -m "feat: add expired WeChat order cancellation scheduled task"
```

---

### Task 20: Add HtTasksPerformLs temporary table mechanism

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/ThermalRegulationEngine.java`
- Create/Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtTasksPerformLsMapper.java`
- Create/Modify: `sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksPerformLsMapper.xml`

- [ ] **Step 1: Ensure domain and mapper exist for HtTasksPerformLs**

Check if `HtTasksPerformLs.java` domain and mapper already exist. If only domain shells, add CRUD methods.

- [ ] **Step 2: Add temporary table operations to ThermalRegulationEngine**

In the 5-step regulation process:
```java
// Step 1: Clear temporary table
tasksPerformLsMapper.deleteByTaskId(taskId);

// Step 2: Fill temporary table (based on adjustBasis)
tasksPerformLsMapper.insertByAdjustBasis(taskId, adjustBasis, params);

// Step 3: Generate instructions (temperature check + alarm)
tasksMapper.instructionGeneration(taskId, adjustBasis);

// Step 4: Update instructions (increase/decrease/complete)
tasksMapper.updateInstructionGeneration(taskId, adjustBasis);

// Step 5: Generate final execution records
tasksMapper.insertHtTasksPerform(taskId);
```

- [ ] **Step 3: Copy all HtTasksPerformLs SQL from old system**

Port all `insertHtTasksPerformLs*` variants from old `HtTasksMapper.xml`.

- [ ] **Step 4: Compile and commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/HtTasksPerformLsMapper.java sdkj-modules/sdkj-thermal/src/main/resources/mapper/HtTasksPerformLsMapper.xml sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/ThermalRegulationEngine.java
git commit -m "feat: add temporary table mechanism for thermal regulation instruction generation"
```

---

## Phase 3: Feature Completeness (P1 - High Priority)

> Estimated: 10 tasks. Can begin after Phase 2 core tasks (13-15) are done.

### Task 21: Complete PrHouse missing interfaces

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHouseController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHouseService.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHouseServiceImpl.java`
- Reference: Old system `PrHouseController.java` (35 endpoints)

Priority endpoints to add:
- `GET /byType` - getHouseListByUnitAndType (filter by type)
- `GET /byValveAndHot` - getHouseListByValveAndHotType (filter by valve type)
- `POST /export` - exportAll (Excel export)
- `POST /import` - importAll (Excel import)
- `GET /heatingCode` - queryGDH (query heating code)
- `PUT /heatingCode` - setGDH (set heating code)
- `GET /multiSearch` - getDataByMulSearch (comprehensive search)

- [ ] Add each endpoint, porting service logic from old system

---

### Task 22: Add device archive batch operations

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatHotArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatTempArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatUnitHotArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatUnitValveArchiveController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatValveArchiveController.java`

Add missing batch operations to each:
- Sync endpoint (valveInformationSynchronization)
- Download sync endpoint (downloadInfoSync)
- Export all endpoint
- Import endpoint

- [ ] Add batch operation endpoints to each archive controller

---

### Task 23: Add agent property management endpoints

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgPropertyController.java`

Add missing endpoints from old `AgentPropertyController`:
- `PUT /{id}/audited` - approve/reject property
- `PUT /{id}/enabled` - enable/disable property
- `GET /{id}/detail` - query property info
- `PUT /{id}` - edit property info

- [ ] Add property management endpoints

---

### Task 24: Add repair record missing endpoints

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrRepairRecordController.java`

Add:
- `POST /items` - insertRepairItems
- `GET /owe` - getHouseIsOwe (check house arrears)
- `PUT /service` - updateDataService

- [ ] Add repair workflow endpoints

---

### Task 25: Add PrAutoMachine client management endpoints

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java`

Add:
- `GET /reader-param` - getReaderParam (reader parameters)
- `GET /client-version` - getClientVersion (latest client version)
- `GET /client-download` - getClientDownload (download client)
- `PUT /` - updateData (update self-service machine config)

- [ ] Add self-service machine management endpoints

---

### Task 26: Add DTU archive export/import

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatDtuArchiveController.java`

Add:
- `POST /export` - exportAll
- `POST /import` - importAll

- [ ] Add DTU batch export/import

---

### Task 27: Add HtHouseStrategy house query endpoint

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtHouseStrategyController.java`

Add `GET /houses` - queryPrHouse (query houses for strategy binding)

- [ ] Add house query for strategy binding

---

### Task 28: Add HtAlert batch insert and DTU statistics

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtAlertController.java`
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/HtAlertServiceImpl.java`

Add:
- `POST /batch` - batch insert alerts
- `GET /typeCountDtu` - DTU dimension type count

- [ ] Add batch alert and DTU statistics

---

### Task 29: Add HtRepair repair person view

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtRepairController.java`

Add `GET /repair-view` - pageListForRepair (repair person's own view)

- [ ] Add repair person view endpoint

---

### Task 30: Add PrImportBasicData heat-code import

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrImportBasicDataController.java`

Add:
- `POST /template-by-code` - downloadExcelByHeatCode
- `POST /import-by-code` - importDataByHeatCode
- `GET /check-house` - isCheckHouseD

- [ ] Add heat-code based import endpoints

---

## Phase 4: Enhancement (P2 - Standard Priority)

> Estimated: 6 tasks. Can be done in parallel with Phase 3 where independent.

### Task 31: Add homepage dashboard

**Files:**
- Create: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/controller/SysHomeController.java`
- Create: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/service/ISysHomeService.java`
- Create: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/service/impl/SysHomeServiceImpl.java`
- Reference: Old system `SysHomeController.java` (77 lines)

- [ ] Create dashboard controller with 6 parallel async queries:
  - Meter count statistics
  - Alert count statistics
  - Expense summary
  - House statistics
  - Device online rate
  - Payment summary

---

### Task 32: Add area/region management

**Files:**
- Create: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/controller/AreaController.java`
- Reference: Old system `AreaController.java`

- [ ] Create province/city/county three-level cascading API

---

### Task 33: Add SMS verification code

**Files:**
- Create: `sdkj-modules/sdkj-system/src/main/java/org/sdkj/system/controller/SysSmsController.java`
- Leverage: `sdkj-common/sms` module

- [ ] Create SMS verification code send/verify endpoints

---

### Task 34: Add third-party payment sync jobs

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/GuodaPaymentSyncJob.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/HongshunPaymentSyncJob.java`
- Reference: Old system `SetHousePayGetByGuoDaJob.java` and `SetHousePayGetByHongShunJob.java`

- [ ] Port third-party payment status sync jobs

---

### Task 35: Add data push jobs (Shida/Yungu)

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ShidaPostJob.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/YunguPostJob.java`
- Reference: Old system `ShiDaPostJob.java` and `YunGuPostJob.java`

- [ ] Port third-party data push jobs

---

### Task 36: Clean up deprecated ControlJob and evaluate sdkj-job module

**Files:**
- Delete: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/quartz/ControlJob.java` (marked @Deprecated)
- Evaluate: `sdkj-modules/sdkj-job/` (currently empty shell with only BillDto)

- [ ] Delete deprecated ControlJob
- [ ] Decide: populate sdkj-job with business jobs, or delete the empty module and keep all jobs in sdkj-thermal

---

## Execution Summary

| Phase | Tasks | Key Focus | Dependencies |
|-------|-------|-----------|-------------|
| **Phase 1** | Tasks 1-12 | Security fixes, foundation, config | None |
| **Phase 2** | Tasks 13-20 | Core engine restoration | Phase 1 complete |
| **Phase 3** | Tasks 21-30 | Feature completeness | Phase 2 Tasks 13-15 done |
| **Phase 4** | Tasks 31-36 | Enhancement | Independent of Phase 3 |

### Critical Path
```
Task 6+7+8 (WeChat payment) -> Task 18+19 (Payment business logic)
Task 9 (Job loader) -> Task 13+15 (Regulation engine + Jobs)
Task 13 (Regulation engine) -> Task 20 (Temp table mechanism)
```

### Verification Checkpoints
- After Phase 1: Run `mvn compile` + database migration scripts
- After Phase 2 Task 13: Manually test regulation engine with a test task
- After Phase 2 Task 14: Test IoT callback endpoints with sample payloads
- After Phase 3: Full API integration test of all new endpoints
- After Phase 4: End-to-end smoke test
