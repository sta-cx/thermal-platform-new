# Stage 0: P0/P1 代码修复 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 3 个 P0 + 5 个 P1 代码质量问题，消除安全隐患和运行时错误。

**Architecture:** 逐文件修改，不改结构。每项修复独立提交，全部完成后编译+启动验证。

**Tech Stack:** Spring Boot 3.5.12, Sa-Token 1.44, MyBatis-Plus 3.5.16, Java 17

---

### Task 0.1: 修复 HtTasksController 死类引用

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java:87`

- [ ] **Step 1: 修改硬编码类路径**

将第 87 行：
```java
task.setBeanClass("com.thermal.job.ControlJob");
```
改为：
```java
task.setBeanClass("org.sdkj.job.ControlJob");
```

- [ ] **Step 2: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java
git commit -m "fix: 修复 HtTasksController 死类引用 com.thermal -> org.sdkj"
```

---

### Task 0.2: 支付回调端点添加安全处理

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java:111-126`

- [ ] **Step 1: 为回调端点添加 SaIgnore 和 TODO 注释**

回调端点是第三方支付平台主动调用，不能要求登录态，但需要签名校验。当前未实现签名校验，先添加 `@SaIgnore` 跳过认证并标注 TODO。

修改 import 区域，添加：
```java
import cn.dev33.satoken.annotation.SaIgnore;
```

修改 `wechatCallback` 方法（第 111 行附近）：
```java
@SaIgnore
@PostMapping("/callback/wechat-heat")
public String wechatCallback(@RequestBody String xmlData) {
    // TODO: 微信支付回调签名校验，验证通过后再处理业务
    log.warn("微信回调未实现签名校验，收到请求: {}", xmlData);
    return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名校验未实现]]></return_msg></xml>";
}
```

修改 `aliCallback` 方法（第 122 行附近）：
```java
@SaIgnore
@PostMapping("/callback/ali-heat")
public String aliCallback(@RequestBody Object data) {
    // TODO: 支付宝回调签名校验，验证通过后再处理业务
    log.warn("支付宝回调未实现签名校验，收到请求: {}", data);
    return "fail";
}
```

在类顶部添加 `log` 字段（如果没有）：
```java
import lombok.extern.slf4j.Slf4j;
```
类注解添加 `@Slf4j`。

- [ ] **Step 2: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java
git commit -m "fix: 支付回调端点添加 SaIgnore 和签名校验 TODO"
```

---

### Task 0.3: PrAutoMachineController 标记废弃并隐藏

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java`

- [ ] **Step 1: 添加 @Deprecated 和 Swagger 隐藏注解**

在类注解区域添加：
```java
import io.swagger.v3.oas.annotations.Hidden;
```

在类声明上方添加：
```java
@Deprecated
@Hidden
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/auto-machine")
public class PrAutoMachineController extends BaseController {
```

- [ ] **Step 2: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java
git commit -m "fix: PrAutoMachineController 标记 @Deprecated 并从 API 文档隐藏"
```

---

### Task 0.4: JWT 密钥外部化

**Files:**
- Modify: `sdkj-admin/src/main/resources/application.yml:104`

- [ ] **Step 1: 修改 JWT 密钥配置**

将第 104 行：
```yaml
  jwt-secret-key: sdkj2024
```
改为：
```yaml
  jwt-secret-key: ${JWT_SECRET_KEY:sdkj2024}
```

- [ ] **Step 2: Commit**

```bash
git add sdkj-admin/src/main/resources/application.yml
git commit -m "fix: JWT 密钥改为环境变量注入"
```

---

### Task 0.5: RSA 密钥外部化

**Files:**
- Modify: `sdkj-admin/src/main/resources/application.yml:182-185`

- [ ] **Step 1: 修改 RSA 公钥**

将第 182 行：
```yaml
  publicKey: [REMOVED]==
```
改为：
```yaml
  publicKey: ${RSA_PUBLIC_KEY:[REMOVED]==}
```

- [ ] **Step 2: 修改 RSA 私钥**

将第 185 行：
```yaml
  privateKey: [REMOVED]=
```
改为：
```yaml
  privateKey: ${RSA_PRIVATE_KEY:[REMOVED]=}
```

- [ ] **Step 3: Commit**

```bash
git add sdkj-admin/src/main/resources/application.yml
git commit -m "fix: RSA 公私钥改为环境变量注入"
```

---

### Task 0.6: 修复 HeatMeterControl HTTP 头未附加

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/utils/HeatMeterControl.java:44-51`

- [ ] **Step 1: 修复 postData 方法**

将第 44-51 行的：
```java
RestTemplate restTemplate = new RestTemplate();
org.springframework.http.HttpEntity<String> request =
    new org.springframework.http.HttpEntity<>(payload);
org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

org.springframework.http.ResponseEntity<String> response =
    restTemplate.postForEntity(mbusUrl, request, String.class);
```
改为：
```java
RestTemplate restTemplate = new RestTemplate();
org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
org.springframework.http.HttpEntity<String> request =
    new org.springframework.http.HttpEntity<>(payload, headers);

org.springframework.http.ResponseEntity<String> response =
    restTemplate.postForEntity(mbusUrl, request, String.class);
```

- [ ] **Step 2: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/utils/HeatMeterControl.java
git commit -m "fix: HeatMeterControl HTTP 头正确附加到请求"
```

---

### Task 0.7: HtTasksController 添加运行状态校验

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java:101-114`

- [ ] **Step 1: 在 update 方法添加状态校验**

修改 `update` 方法（第 101 行附近），在 return 之前添加校验：
```java
@SaCheckLogin
@Log(title = "调控任务", businessType = BusinessType.UPDATE)
@PutMapping
public R<Void> update(@Validated @RequestBody HtTasks task,
                      @RequestParam(required = false) List<String> scopeIds) {
    HtTasks existing = tasksService.getById(task.getId());
    if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
        return R.fail("运行中的任务不允许修改，请先停止任务！");
    }
    return toAjax(tasksService.updateWithScope(task, scopeIds));
}
```

- [ ] **Step 2: 在 remove 方法添加状态校验**

修改 `remove` 方法（第 112 行附近）：
```java
@SaCheckLogin
@Log(title = "调控任务", businessType = BusinessType.DELETE)
@DeleteMapping("/{id}")
public R<Void> remove(@PathVariable Integer id) {
    HtTasks existing = tasksService.getById(id);
    if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
        return R.fail("运行中的任务不允许删除，请先停止任务！");
    }
    return toAjax(tasksService.removeById(id));
}
```

- [ ] **Step 3: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/HtTasksController.java
git commit -m "fix: HtTasksController 编辑/删除前校验任务运行状态"
```

---

### Task 0.8: PrUseCardLogServiceImpl 添加 @Transactional

**Files:**
- Modify: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrUseCardLogServiceImpl.java:32`

- [ ] **Step 1: 添加事务注解**

在 import 区域添加：
```java
import org.springframework.transaction.annotation.Transactional;
```

在 `changeValveStatus` 方法上添加注解：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean changeValveStatus(String meterId, Integer valveStatus) {
```

- [ ] **Step 2: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrUseCardLogServiceImpl.java
git commit -m "fix: PrUseCardLogServiceImpl.changeValveStatus 添加 @Transactional"
```

---

### Task 0.9: 编译与启动验证

- [ ] **Step 1: 编译验证**

```bash
cd D:\chonggou\thermal-platform-new
mvn clean compile -q
```
Expected: `BUILD SUCCESS`

- [ ] **Step 2: 启动验证**

```bash
mvn spring-boot:run -pl sdkj-admin -Dspring-boot.run.profiles=dev
```
Expected: 应用正常启动，无 ClassNotFoundException 或 Bean 创建失败。检查日志中无 `com.thermal` 相关错误。

Ctrl+C 停止应用。

- [ ] **Step 3: 最终 Commit（如有未提交的变更）**

```bash
git add -A
git commit -m "chore: Stage 0 P0/P1 修复完成，编译启动验证通过"
```
