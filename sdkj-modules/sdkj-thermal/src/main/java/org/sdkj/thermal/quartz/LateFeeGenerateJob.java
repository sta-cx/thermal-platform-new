package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrExpenseService;
import org.springframework.context.ApplicationContext;

/**
 * 滞纳金生成 Job
 * 迁移自旧系统 latefeeExpenseJob
 *
 * @author sdkj
 */
@Slf4j
public class LateFeeGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String orgId = data.getString("orgId");
        String latefeeFormula = data.getString("latefeeFormula");
        Long standardId = Long.parseLong(data.getString("standardId"));
        String type = data.getString("type");

        log.info("滞纳金生成 Job 启动: {} (小区: {}, 类型: {})", jobName, orgId, type);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrExpenseService expenseService = ctx.getBean(IPrExpenseService.class);

            boolean result = switch (type) {
                case "qs" -> expenseService.updateLatefeeQs(orgId, latefeeFormula, standardId);
                case "js" -> expenseService.updateLatefeeJs(orgId, latefeeFormula, standardId);
                case "sjhc" -> expenseService.updateLatefeeSJHC(orgId, latefeeFormula, standardId, null, null);
                default -> expenseService.updateLatefeeQs(orgId, latefeeFormula, standardId);
            };

            if (result) {
                expenseService.updateFinalMoneyAfterLateFee(orgId, standardId);
            }

            log.info("滞纳金生成 Job 完成: {} (结果: {})", jobName, result);
        } catch (Exception e) {
            log.error("滞纳金生成 Job 失败: {} (小区: {})", jobName, orgId, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }
}
