package org.sdkj.thermal.quartz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阀门状态查询 Job
 * 移植自旧系统 GetValveStatusGetByPayNew
 *
 * 根据缴费状态查询三方户阀的实际阀门状态：
 * - 已缴费 -> 查询开阀状态
 * - 未缴费 -> 查询关阀状态
 *
 * instructionType=4 表示查询指令
 *
 * @author sdkj
 */
@Slf4j
public class ValveStatusQueryJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String orgIdStr = data.getString("orgId");

        log.info("阀门状态查询 Job 启动: {} (小区: {})", jobName, orgIdStr);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHeatValveArchiveService valveArchiveService = ctx.getBean(IPrHeatValveArchiveService.class);
            IHtTasksPerformService performService = ctx.getBean(IHtTasksPerformService.class);
            IPrOptionsHeatService optionsHeatService = ctx.getBean(IPrOptionsHeatService.class);

            if (StrUtil.isBlank(orgIdStr)) {
                log.warn("阀门状态查询 Job: orgId 为空，跳过执行");
                return;
            }

            String[] orgIds = orgIdStr.split(",");
            for (String orgId : orgIds) {
                orgId = orgId.trim();
                log.info("执行阀门状态查询: 小区ID={}", orgId);
                try {
                    processValveStatusQuery(valveArchiveService, performService,
                        optionsHeatService, orgId);
                } catch (Exception e) {
                    log.error("阀门状态查询失败: 小区ID={}", orgId, e);
                }
            }
            log.info("阀门状态查询 Job 完成: {}", jobName);
        } catch (Exception e) {
            log.error("阀门状态查询 Job 失败: {} (小区: {})", jobName, orgIdStr, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }

    private void processValveStatusQuery(IPrHeatValveArchiveService valveArchiveService,
                                          IHtTasksPerformService performService,
                                          IPrOptionsHeatService optionsHeatService,
                                          String orgId) {
        // 查询已缴费但未开阀的阀门 -> 查询其开阀状态
        List<PrHeatValveArchive> paidClosedList = valveArchiveService.queryPaidClosedValves(orgId);
        // 查询未缴费但已开阀的阀门 -> 查询其关阀状态
        List<PrHeatValveArchive> unpaidOpenList = valveArchiveService.queryUnpaidOpenValves(orgId);

        PrOptionsHeat optionsHeat = optionsHeatService.getDataById(orgId, "2");
        if (optionsHeat == null) {
            log.warn("未找到小区 {} 的调控配置，跳过", orgId);
            return;
        }

        // 为已缴费用户生成状态查询指令
        if (CollUtil.isNotEmpty(paidClosedList)) {
            List<HtTasksPerform> tasks = buildQueryTasks(paidClosedList, orgId,
                optionsHeat, "valve_pay_query_open_");
            if (CollUtil.isNotEmpty(tasks)) {
                try {
                    performService.saveBatchTasks(tasks);
                    performService.executeValveControlTasks(tasks);
                    log.info("缴费阀门状态查询指令发送完成: 小区={}, 数量={}", orgId, tasks.size());
                } catch (Exception e) {
                    log.error("缴费阀门状态查询指令执行失败: 小区={}", orgId, e);
                }
            }
        }

        // 为未缴费用户生成状态查询指令
        if (CollUtil.isNotEmpty(unpaidOpenList)) {
            List<HtTasksPerform> tasks = buildQueryTasks(unpaidOpenList, orgId,
                optionsHeat, "valve_pay_query_close_");
            if (CollUtil.isNotEmpty(tasks)) {
                try {
                    performService.saveBatchTasks(tasks);
                    performService.executeValveControlTasks(tasks);
                    log.info("未缴费阀门状态查询指令发送完成: 小区={}, 数量={}", orgId, tasks.size());
                } catch (Exception e) {
                    log.error("未缴费阀门状态查询指令执行失败: 小区={}", orgId, e);
                }
            }
        }
    }

    private List<HtTasksPerform> buildQueryTasks(List<PrHeatValveArchive> archives,
                                                  String orgId,
                                                  PrOptionsHeat optionsHeat, String instructionIdPrefix) {
        List<HtTasksPerform> tasks = new ArrayList<>();
        Date now = new Date();
        for (PrHeatValveArchive archive : archives) {
            HtTasksPerform task = new HtTasksPerform();
            task.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
            task.setInstructionType(4); // 4=查询
            task.setInstruction(0);
            task.setNumber(0);
            task.setOrgId(orgId);
            task.setDeviceId(archive.getDeviceId());
            task.setMeterArcCode(archive.getMeterArcCode());
            task.setMeterId(archive.getId());
            task.setMeterNum(archive.getMeterNum());
            task.setStatus(1);
            task.setInstructionStatus(0);
            task.setImei(archive.getImeiNum());
            task.setDtuNum(archive.getDtuNum());
            task.setConcentratorCode(archive.getConcentratorCode());
            task.setChanNum(archive.getChanNum());
            task.setInstructionId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
            task.setCreateTime(now);
            tasks.add(task);
        }
        return tasks;
    }
}
