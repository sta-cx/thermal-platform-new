package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrHeatDailyService;
import org.springframework.context.ApplicationContext;

/**
 * 热表日表生成 Job
 * 移植自旧系统 JieXiuHeatJob
 *
 * 每天定时执行，对指定小区的热表数据执行5步日表生成流程：
 * 1. setIsValid - 标记有效抄表记录
 * 2. setHeatDaily - 生成日表记录
 * 3. setSteps - 更新单价及金额
 * 4. setQtyStepsN - 计算用量和金额
 * 5. setCurrentReading - 更新配表档案当前读数
 *
 * @author sdkj
 */
@Slf4j
public class HeatDailyGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String orgIdStr = data.getString("orgId");

        log.info("热表日表生成 Job 启动: {} (小区: {})", jobName, orgIdStr);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHeatDailyService heatDailyService = ctx.getBean(IPrHeatDailyService.class);

            String[] orgIds = orgIdStr.split(",");
            for (String orgId : orgIds) {
                try {
                    boolean result = heatDailyService.generateHeatDaily(orgId.trim());
                    log.info("热表日表生成完成: 小区={}, 结果={}", orgId.trim(), result);
                } catch (Exception e) {
                    log.error("热表日表生成失败: 小区={}", orgId.trim(), e);
                }
            }
            log.info("热表日表生成 Job 完成: {}", jobName);
        } catch (Exception e) {
            log.error("热表日表生成 Job 失败: {} (小区: {})", jobName, orgIdStr, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }
}
