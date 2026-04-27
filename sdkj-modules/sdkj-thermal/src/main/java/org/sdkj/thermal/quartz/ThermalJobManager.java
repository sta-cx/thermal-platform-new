package org.sdkj.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.tenant.helper.TenantHelper;
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

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        String tenantId = TenantHelper.getTenantId();

        JobDetail jobDetail = JobBuilder.newJob(ThermalJob.class)
            .withIdentity(jobKey)
            .usingJobData("taskId", taskId)
            .usingJobData("taskName", task.getName())
            .build();

        if (tenantId != null && !tenantId.isEmpty()) {
            jobDetail.getJobDataMap().put("tenantId", tenantId);
        }

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

    public boolean resumeJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (!scheduler.checkExists(jobKey)) {
            return addJob(taskId);
        }
        scheduler.resumeJob(jobKey);
        log.info("恢复调控任务: {}", taskId);
        return true;
    }

    public boolean pauseJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
            log.info("暂停调控任务: {}", taskId);
        }
        return true;
    }

    public boolean deleteJob(Integer taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("thermal_" + taskId, JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("删除调控任务: {}", taskId);
        }
        return true;
    }

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
