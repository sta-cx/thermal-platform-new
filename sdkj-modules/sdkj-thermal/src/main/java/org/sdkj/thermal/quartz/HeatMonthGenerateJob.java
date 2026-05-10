package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrHeatMonthService;
import org.springframework.context.ApplicationContext;

/**
 * 热表月表生成 Job
 * 移植自旧系统 JieXiuHeatMonthJob
 *
 * 每月定时执行，对指定小区的热表数据执行5步月表生成流程：
 * 1. insertPrHeatMonth - 插入月表记录
 * 2. updateStartReading - 更新起始读数
 * 3. updateQty - 更新用量
 * 4. setFee - 计算费用
 * 5. updateArrearage - 更新欠费状态
 *
 * @author sdkj
 */
@Slf4j
public class HeatMonthGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String orgIdStr = data.getString("orgId");

        log.info("热表月表生成 Job 启动: {} (小区: {})", jobName, orgIdStr);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHeatMonthService heatMonthService = ctx.getBean(IPrHeatMonthService.class);

            String[] orgIds = orgIdStr.split(",");
            for (String orgId : orgIds) {
                try {
                    boolean result = heatMonthService.generateHeatMonth(orgId.trim());
                    log.info("热表月表生成完成: 小区={}, 结果={}", orgId.trim(), result);
                } catch (Exception e) {
                    log.error("热表月表生成失败: 小区={}", orgId.trim(), e);
                }
            }
            log.info("热表月表生成 Job 完成: {}", jobName);
        } catch (Exception e) {
            log.error("热表月表生成 Job 失败: {} (小区: {})", jobName, orgIdStr, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }
}
