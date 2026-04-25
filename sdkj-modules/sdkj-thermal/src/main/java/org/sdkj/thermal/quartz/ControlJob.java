package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.service.IHtTasksService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

@Slf4j
public class ControlJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        Integer taskId = data.getInt("taskId");
        String taskName = data.getString("taskName");

        log.info("ControlJob 触发调控任务: {} (ID: {})", taskName, taskId);

        try {
            Object bean = context.getScheduler().getContext().get("applicationContext");
            if (bean instanceof org.springframework.context.ApplicationContext ctx) {
                IHtTasksService tasksService = ctx.getBean(IHtTasksService.class);
                HtTasks task = tasksService.getById(taskId);
                if (task != null && task.getStatus() == 1) {
                    tasksService.saveValveAngle(String.valueOf(taskId),
                        task.getScopeType() != null ? String.valueOf(task.getScopeType()) : "1");
                    log.info("ControlJob 执行完成: {} (ID: {})", taskName, taskId);
                } else {
                    log.warn("调控任务已停止，跳过执行: {} (ID: {})", taskName, taskId);
                }
            }
        } catch (Exception e) {
            log.error("ControlJob 执行失败: {} (ID: {})", taskName, taskId, e);
        }
    }
}
