package org.sdkj.thermal.quartz;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.tenant.core.TenantContextHolder;
import org.sdkj.thermal.service.impl.ThermalRegulationEngine;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Thermal regulation Quartz job.
 *
 * Delegates to ThermalRegulationEngine which encapsulates the complete
 * regulation flow with all adjustBasis branches, temperature calculation,
 * instruction generation, and DTU broadcast support.
 */
@Slf4j
public class ThermalJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        Integer taskId = context.getJobDetail().getJobDataMap().getInt("taskId");
        String taskName = context.getJobDetail().getJobDataMap().getString("taskName");
        String tenantCode = context.getMergedJobDataMap().getString("tenantCode");

        log.info("Quartz triggered thermal regulation task: {} (ID: {}, Tenant: {})", taskName, taskId, tenantCode);

        try {
            if (tenantCode != null && !tenantCode.isEmpty()) {
                TenantContextHolder.setTenantCode(tenantCode);
                DynamicDataSourceContextHolder.push("tenant_" + tenantCode);
            }
            doExecute(context, taskId, taskName);
        } catch (Exception e) {
            log.error("Thermal regulation task failed: {} (ID: {})", taskName, taskId, e);
        } finally {
            DynamicDataSourceContextHolder.poll();
            TenantContextHolder.clear();
        }
    }

    private void doExecute(JobExecutionContext context, Integer taskId, String taskName) {
        try {
            Object bean = context.getScheduler().getContext().get("applicationContext");
            if (bean instanceof org.springframework.context.ApplicationContext ctx) {
                ThermalRegulationEngine engine = ctx.getBean(ThermalRegulationEngine.class);

                // Load task to get current scopeType
                org.sdkj.thermal.service.IHtTasksService tasksService =
                    ctx.getBean(org.sdkj.thermal.service.IHtTasksService.class);
                org.sdkj.thermal.domain.HtTasks task = tasksService.getById(taskId);

                if (task != null && task.getStatus() != null && task.getStatus() == 1) {
                    Integer scopeType = task.getScopeType() != null ? task.getScopeType() : 1;
                    engine.executeRegulation(taskId, scopeType);
                    log.info("Thermal regulation task completed: {} (ID: {})", taskName, taskId);
                } else {
                    log.warn("Thermal regulation task is stopped, skipping: {} (ID: {})", taskName, taskId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed during thermal regulation execution", e);
        }
    }
}
