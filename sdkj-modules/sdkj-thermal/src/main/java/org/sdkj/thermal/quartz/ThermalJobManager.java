package org.sdkj.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.tenant.core.TenantContextHolder;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.mapper.HtTasksMapper;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThermalJobManager {

    private final Scheduler scheduler;
    private final HtTasksMapper tasksMapper;

    private static final String JOB_GROUP = "THERMAL_JOBS";
    private static final String TRIGGER_GROUP = "THERMAL_TRIGGERS";

    public boolean addJob(Long taskId) throws SchedulerException {
        HtTasks task = tasksMapper.selectById(taskId);
        if (task == null) {
            log.warn("任务不存在: {}", taskId);
            return false;
        }
        if (task.getCronExpression() == null || task.getCronExpression().isEmpty()) {
            log.warn("任务无 Cron 表达式: {}", taskId);
            return false;
        }

        String tenantCode = TenantContextHolder.getTenantCode();
        JobKey jobKey = jobKey(tenantCode, taskId);

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(ThermalJob.class)
            .withIdentity(jobKey)
            .usingJobData("taskId", taskId)
            .usingJobData("taskName", task.getName())
            .build();

        if (tenantCode != null && !tenantCode.isEmpty()) {
            jobDetail.getJobDataMap().put("tenantCode", tenantCode);
        }

        CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpression())
            .withMisfireHandlingInstructionDoNothing();

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerKey(tenantCode, taskId))
            .withSchedule(cronBuilder)
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

        if (task.getStatus() != null && task.getStatus() == 0) {
            scheduler.pauseJob(jobKey);
        }

        log.info("添加调控任务到调度器: {} (ID: {}, Cron: {})", task.getName(), taskId, task.getCronExpression());
        return true;
    }

    public boolean resumeJob(Long taskId) throws SchedulerException {
        JobKey jobKey = jobKey(TenantContextHolder.getTenantCode(), taskId);
        if (!scheduler.checkExists(jobKey)) {
            return addJob(taskId);
        }
        scheduler.resumeJob(jobKey);
        log.info("恢复调控任务: {}", taskId);
        return true;
    }

    public boolean pauseJob(Long taskId) throws SchedulerException {
        JobKey jobKey = jobKey(TenantContextHolder.getTenantCode(), taskId);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
            log.info("暂停调控任务: {}", taskId);
        }
        return true;
    }

    public boolean deleteJob(Long taskId) throws SchedulerException {
        JobKey jobKey = jobKey(TenantContextHolder.getTenantCode(), taskId);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("删除调控任务: {}", taskId);
        }
        return true;
    }

    public boolean triggerJob(Long taskId) throws SchedulerException {
        JobKey jobKey = jobKey(TenantContextHolder.getTenantCode(), taskId);
        if (scheduler.checkExists(jobKey)) {
            scheduler.triggerJob(jobKey);
            log.info("立即触发调控任务: {}", taskId);
            return true;
        }
        return addJob(taskId);
    }

    private JobKey jobKey(String tenantCode, Long taskId) {
        return new JobKey("thermal_" + tenantKey(tenantCode) + "_" + taskId, JOB_GROUP);
    }

    private TriggerKey triggerKey(String tenantCode, Long taskId) {
        return new TriggerKey("trigger_" + tenantKey(tenantCode) + "_" + taskId, TRIGGER_GROUP);
    }

    private String tenantKey(String tenantCode) {
        return tenantCode == null || tenantCode.isEmpty() ? "master" : tenantCode;
    }
}
