package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.springframework.context.ApplicationContext;

/**
 * 仪表指令下发 Job
 * 移植自旧系统 StartSendMeterCommandJob
 *
 * 定时查询待发送的调控指令并执行下发。
 * 旧系统中调用 getNonExecutionNew 方法，新系统中委托给
 * IHtTasksPerformService.executePendingCommands()。
 *
 * @author sdkj
 */
@Slf4j
public class SendMeterCommandJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String companyId = data.getString("companyId");
        String orgId = data.getString("orgId");

        log.info("仪表指令下发 Job 启动: {} (公司: {}, 小区: {})", jobName, companyId, orgId);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IHtTasksPerformService performService = ctx.getBean(IHtTasksPerformService.class);

            performService.executePendingCommands(companyId, orgId);
            log.info("仪表指令下发 Job 完成: {} (公司: {}, 小区: {})", jobName, companyId, orgId);
        } catch (Exception e) {
            log.error("仪表指令下发 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgId, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }
}
