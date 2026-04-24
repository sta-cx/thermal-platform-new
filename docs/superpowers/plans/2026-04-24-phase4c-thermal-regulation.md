# Phase 4c: Thermal Regulation Completion Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the thermal regulation module — instruction generation pipeline, Quartz scheduler integration, and MBus hardware communication.

**Architecture:** Three sub-phases: 4c-2 (task CRUD with scope management, done), 4c-3 (instruction generation pipeline, done), 4c-4 (Quartz scheduler + MBus hardware communication, remaining). The existing `HtTasksController` has 2 TODO endpoints for scheduler integration. The `HeatMeterControl` utility needs HTTP POST implementation.

**Tech Stack:** Spring Boot 3.5.12, Java 17, MyBatis-Plus 3.5.16, Sa-Token 1.44.0, Quartz (or snail-job), RestTemplate for MBus HTTP

---

## Current State (As of 2026-04-24)

### Already Complete
- **HtTasksServiceImpl.saveValveAngle** — Full instruction generation: queries strategy sub-table → iterates scope data → batch inserts HtTasksPerform records → updates scope status to 9
- **HtTasksPerformServiceImpl** — Full service with status updates, pending queries, execution stats
- **HtTasksMapper.xml** — `selectScopeForAngleH/D`, `insertTasksPerformBatch`, `querySummary` with balance rate
- **All controllers** — HtTasksController (19 endpoints), HtTasksPerformController (5 endpoints)

### Remaining TODOs (4 items)
1. `HtTasksServiceImpl.changeStatus()` — DB-only, needs Quartz scheduler start/stop
2. `HtTasksServiceImpl.runTask()` — DB-only, needs Quartz trigger
3. `HeatMeterControl.postData()` — logs but doesn't HTTP POST to MBus middleware
4. `HtTasksController` — `changeStatus` and `runTask` endpoints marked TODO in comments

---

## File Structure

### Files to Create
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/quartz/ThermalJob.java` — Quartz job class for task execution
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/quartz/ThermalJobManager.java` — Scheduler management (add/remove/start/stop jobs)

### Files to Modify
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/HtTasksServiceImpl.java:106-130` — Connect changeStatus/runTask to scheduler
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/utils/HeatMeterControl.java:30-48` — Add RestTemplate HTTP POST
- `ruoyi-modules/ruoyi-thermal/pom.xml` — Add quartz dependency (if not present)

---

### Task 1: MBus HTTP Communication

**Files:**
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/utils/HeatMeterControl.java`

- [ ] **Step 1: Add RestTemplate to HeatMeterControl**

Add `@Slf4j` is already present. Add RestTemplate injection:

```java
    private final RestTemplate restTemplate = new RestTemplate();
```

- [ ] **Step 2: Implement postData HTTP POST**

Replace the TODO block in `postData()` with:

```java
    public boolean postData(String[] array) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"counts\":").append(array.length).append(",\"data\":[");
            for (int i = 0; i < array.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(array[i]);
            }
            sb.append("]}");
            String payload = sb.toString();
            log.info("MBus 发送: {}", payload);

            org.springframework.http.HttpEntity<String> request =
                new org.springframework.http.HttpEntity<>(payload);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.ResponseEntity<String> response =
                restTemplate.postForEntity(mbusUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("MBus 发送成功: {}", response.getBody());
                return true;
            } else {
                log.error("MBus 发送失败, HTTP状态: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("MBus 发送失败", e);
            return false;
        }
    }
```

- [ ] **Step 3: Compile and verify**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```
Expected: EXIT_CODE=0

- [ ] **Step 4: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/utils/HeatMeterControl.java
git commit -m "feat(phase4c): 实现 MBus HTTP 通信"
```

---

### Task 2: Quartz Scheduler Integration

**Files:**
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/quartz/ThermalJob.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/quartz/ThermalJobManager.java`

- [ ] **Step 1: Create ThermalJob.java**

```java
package org.dromara.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.service.IHtTasksService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * Quartz 任务执行器 — 调控任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThermalJob implements Job {

    // 通过 ApplicationContext 手动获取，Quartz 创建的 job 不由 Spring 管理
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        Integer taskId = data.getInt("taskId");
        String taskName = data.getString("taskName");

        log.info("Quartz 触发调控任务: {} (ID: {})", taskName, taskId);

        try {
            // 注意: 此处通过 ApplicationContext 获取 service
            // 因为 Quartz 创建的 job 不是 Spring bean
            Object bean = context.getScheduler().getContext().get("applicationContext");
            if (bean instanceof org.springframework.context.ApplicationContext) {
                org.springframework.context.ApplicationContext ctx =
                    (org.springframework.context.ApplicationContext) bean;
                IHtTasksService tasksService = ctx.getBean(IHtTasksService.class);

                // 执行指令生成
                HtTasks task = tasksService.getById(taskId);
                if (task != null && task.getStatus() == 1) {
                    tasksService.saveValveAngle(String.valueOf(taskId), task.getScopeType() != null ? String.valueOf(task.getScopeType()) : "1");
                    log.info("调控任务执行完成: {} (ID: {})", taskName, taskId);
                } else {
                    log.warn("调控任务已停止，跳过执行: {} (ID: {})", taskName, taskId);
                }
            }
        } catch (Exception e) {
            log.error("调控任务执行失败: {} (ID: {})", taskName, taskId, e);
        }
    }
}
```

- [ ] **Step 2: Create ThermalJobManager.java**

```java
package org.dromara.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.mapper.HtTasksMapper;
import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * Quartz 调度管理器 — 调控任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThermalJobManager {

    private final Scheduler scheduler;
    private final HtTasksMapper tasksMapper;

    private static final String JOB_GROUP = "THERMAL_JOBS";
    private static final String TRIGGER_GROUP = "THERMAL_TRIGGERS";

    /**
     * 添加调控任务到调度器
     */
    public boolean addJob(Integer taskId) throws SchedulerException {
        HtTasks task = tasksMapper.selectById(taskId);
        if (task == null) {
            log.warn("任务不存在: {}", taskId);
            return false;
        }
        if (task.getCronExpression() == null || task.getCronExpression().isEmpty()) {
            log.warn("任务无 Cron 表达式: {}", taskId);
            return false;
        }

        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);

        // 如果已存在，先删除
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(ThermalJob.class)
            .withIdentity(jobKey)
            .usingJobData("taskId", taskId)
            .usingJobData("taskName", task.getName())
            .build();

        CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpression())
            .withMisfireHandlingInstructionDoNothing();

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger_" + taskId, TRIGGER_GROUP)
            .withSchedule(cronBuilder)
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

        if (task.getStatus() != null && task.getStatus() == 0) {
            scheduler.pauseJob(jobKey);
        }

        log.info("添加调控任务到调度器: {} (ID: {}, Cron: {})", task.getName(), taskId, task.getCronExpression());
        return true;
    }

    /**
     * 启动任务
     */
    public boolean resumeJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (!scheduler.checkExists(jobKey)) {
            return addJob(taskId);
        }
        scheduler.resumeJob(jobKey);
        log.info("恢复调控任务: {}", taskId);
        return true;
    }

    /**
     * 暂停任务
     */
    public boolean pauseJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
            log.info("暂停调控任务: {}", taskId);
        }
        return true;
    }

    /**
     * 删除任务
     */
    public boolean deleteJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("删除调控任务: {}", taskId);
        }
        return true;
    }

    /**
     * 立即触发任务
     */
    public boolean triggerJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.triggerJob(jobKey);
            log.info("立即触发调控任务: {}", taskId);
            return true;
        }
        return addJob(taskId);
    }
}
```

- [ ] **Step 3: Compile and verify**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```
Expected: EXIT_CODE=0 (if quartz dependency missing, see Step 4)

- [ ] **Step 4: Add Quartz dependency if needed**

If compilation fails due to missing Quartz classes, add to `ruoyi-modules/ruoyi-thermal/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

- [ ] **Step 5: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/quartz/
git add ruoyi-modules/ruoyi-thermal/pom.xml
git commit -m "feat(phase4c): 集成 Quartz 调度器"
```

---

### Task 3: Connect Scheduler to Service

**Files:**
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/HtTasksServiceImpl.java`
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IHtTasksService.java`

- [ ] **Step 1: Add scheduler methods to IHtTasksService**

Add after `runTask()`:

```java
    /**
     * 添加任务到调度器
     */
    boolean addToScheduler(Integer id);

    /**
     * 从调度器删除任务
     */
    boolean removeFromScheduler(Integer id);
```

- [ ] **Step 2: Update HtTasksServiceImpl imports and fields**

Add imports:
```java
import org.dromara.thermal.quartz.ThermalJobManager;
```

Add field:
```java
    private final ThermalJobManager jobManager;
```

- [ ] **Step 3: Replace changeStatus implementation**

Replace the existing `changeStatus` method:

```java
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Integer id, Integer status) {
        HtTasks task = getById(id);
        if (task == null) return false;

        try {
            if (status == 1) {
                jobManager.resumeJob(id);
            } else {
                jobManager.pauseJob(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("调度器操作失败: " + e.getMessage(), e);
        }

        return baseMapper.updateTaskStatus(id, status) > 0;
    }
```

- [ ] **Step 4: Replace runTask implementation**

Replace the existing `runTask` method:

```java
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean runTask(Integer id) {
        HtTasks task = getById(id);
        if (task == null) return false;
        if (task.getStatus() == null || task.getStatus() != 1) {
            throw new RuntimeException("请先启动任务！");
        }

        try {
            jobManager.triggerJob(id);
        } catch (Exception e) {
            throw new RuntimeException("触发任务失败: " + e.getMessage(), e);
        }

        baseMapper.updateLastTime(id);
        return true;
    }
```

- [ ] **Step 5: Implement addToScheduler and removeFromScheduler**

Add at end of class:

```java
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToScheduler(Integer id) {
        try {
            return jobManager.addJob(id);
        } catch (Exception e) {
            throw new RuntimeException("添加调度任务失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromScheduler(Integer id) {
        try {
            return jobManager.deleteJob(id);
        } catch (Exception e) {
            throw new RuntimeException("删除调度任务失败: " + e.getMessage(), e);
        }
    }
```

- [ ] **Step 6: Update saveWithScope to register scheduler**

In `saveWithScope`, after `save(entity)`, add:
```java
        if (saved && entity.getCronExpression() != null && !entity.getCronExpression().isEmpty()) {
            addToScheduler(entity.getId());
        }
```

- [ ] **Step 7: Update removeById to unregister scheduler**

In `removeById`, before `super.removeById(id)`, add:
```java
        removeFromScheduler((Integer) id);
```

- [ ] **Step 8: Compile and verify**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```
Expected: EXIT_CODE=0

- [ ] **Step 9: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/
git commit -m "feat(phase4c): 连接调度器到 Service 层"
```

---

### Task 4: Remove TODO Comments and Final Verification

**Files:**
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/HtTasksController.java`

- [ ] **Step 1: Update controller TODO comments**

Replace:
```java
    /**
     * 启动/停止任务（TODO: Quartz/snail-job集成）
     */
```
With:
```java
    /**
     * 启动/停止任务
     */
```

Replace:
```java
    /**
     * 立即运行任务（TODO: Quartz/snail-job集成）
     */
```
With:
```java
    /**
     * 立即运行任务
     */
```

Replace:
```java
     * 保存设定开度（TODO: 指令生成管线）
```
With:
```java
     * 保存设定开度
```

- [ ] **Step 2: Full compile verification**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am 2>&1 | grep -E "BUILD|ERROR"
```
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/HtTasksController.java
git commit -m "chore(phase4c): 清理 TODO 注释"
```

---

## Self-Review

### Spec coverage
| Requirement | Task | Status |
|---|---|---|
| MBus HTTP POST | Task 1 | Covered |
| Quartz scheduler job class | Task 2 | Covered |
| Quartz job manager | Task 2 | Covered |
| changeStatus connects to scheduler | Task 3 | Covered |
| runTask connects to scheduler | Task 3 | Covered |
| saveWithScope registers scheduler | Task 3 | Covered |
| removeById unregisters scheduler | Task 3 | Covered |
| Clean TODO comments | Task 4 | Covered |

### Placeholder scan
- No TBD/TODO/fill-in patterns found
- All code blocks contain actual implementation
- Quartz misfire handling uses `DoNothing` (safe default)

### Type consistency
- `Integer id` consistent with `HtTasks.getId()` returning `Integer`
- `ThermalJobManager` methods return `boolean` matching service layer
- All exception handling wraps in `RuntimeException` for transaction rollback
- `saveValveAngle` already accepts `String taskId` — matches existing signature
