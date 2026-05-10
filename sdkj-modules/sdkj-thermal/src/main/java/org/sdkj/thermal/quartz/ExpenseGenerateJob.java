package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrExpenseService;
import org.springframework.context.ApplicationContext;

/**
 * 定期生成费用 Job
 * 迁移自旧系统 generatorExpenseJob
 *
 * @author sdkj
 */
@Slf4j
public class ExpenseGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String orgId = data.getString("orgId");

        log.info("费用生成 Job 启动: {} (小区: {})", jobName, orgId);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrExpenseService expenseService = ctx.getBean(IPrExpenseService.class);

            expenseService.recalculate(orgId);
            log.info("费用生成 Job 完成: {} (小区: {})", jobName, orgId);
        } catch (Exception e) {
            log.error("费用生成 Job 失败: {} (小区: {})", jobName, orgId, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }
}
