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
 * 缴费自动开关阀 Job
 * 移植自旧系统 SetValveStatusGetByPayNew
 *
 * 根据缴费状态自动控制三方户阀：
 * - 已缴费但阀门关闭 -> 生成开阀指令
 * - (可选) 未缴费但阀门开启 -> 生成关阀指令
 *
 * @author sdkj
 */
@Slf4j
public class ValvePaymentControlJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String companyId = data.getString("companyId");
        String orgIdStr = data.getString("orgId");

        log.info("缴费自动开关阀 Job 启动: {} (公司: {}, 小区: {})", jobName, companyId, orgIdStr);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHeatValveArchiveService valveArchiveService = ctx.getBean(IPrHeatValveArchiveService.class);
            IHtTasksPerformService performService = ctx.getBean(IHtTasksPerformService.class);
            IPrOptionsHeatService optionsHeatService = ctx.getBean(IPrOptionsHeatService.class);

            if (StrUtil.isBlank(orgIdStr)) {
                log.warn("缴费自动开关阀 Job: orgId 为空，跳过执行");
                return;
            }

            String[] orgIds = orgIdStr.split(",");
            for (String orgId : orgIds) {
                orgId = orgId.trim();
                log.info("执行缴费自动开关阀: 小区ID={}", orgId);
                try {
                    processValveControl(ctx, valveArchiveService, performService,
                        optionsHeatService, companyId, orgId);
                } catch (Exception e) {
                    log.error("缴费自动开关阀失败: 小区ID={}", orgId, e);
                }
            }
            log.info("缴费自动开关阀 Job 完成: {} (公司: {})", jobName, companyId);
        } catch (Exception e) {
            log.error("缴费自动开关阀 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgIdStr, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }

    private void processValveControl(ApplicationContext ctx,
                                      IPrHeatValveArchiveService valveArchiveService,
                                      IHtTasksPerformService performService,
                                      IPrOptionsHeatService optionsHeatService,
                                      String companyId, String orgId) {
        // 查询已缴费但未开阀的阀门
        List<PrHeatValveArchive> paidClosedList = valveArchiveService.queryPaidClosedValves(companyId, orgId);

        // 获取调控参数
        PrOptionsHeat optionsHeat = optionsHeatService.getDataById(orgId, companyId, "2");
        if (optionsHeat == null) {
            log.warn("未找到小区 {} 的调控配置，跳过", orgId);
            return;
        }
        int max = optionsHeat.getControlMax() != null ? optionsHeat.getControlMax() : 100;
        int min = optionsHeat.getControlMin() != null ? optionsHeat.getControlMin() : 0;

        // 生成开阀指令（已缴费 -> 开阀）
        if (CollUtil.isNotEmpty(paidClosedList)) {
            List<HtTasksPerform> tasks = buildOpenValveTasks(paidClosedList, companyId, orgId,
                max, optionsHeat);
            if (CollUtil.isNotEmpty(tasks)) {
                try {
                    performService.saveBatchTasks(tasks);
                    performService.executeValveControlTasks(tasks);
                    performService.insertValveOCLog(tasks);
                    log.info("开阀指令生成并发送完成: 小区={}, 数量={}", orgId, tasks.size());
                } catch (Exception e) {
                    log.error("开阀指令执行失败: 小区={}", orgId, e);
                }
            }
        }

        // 关阀逻辑（未缴费 -> 关阀）- 旧系统已注释，保留接口备用
        // List<PrHeatValveArchive> unpaidOpenList = valveArchiveService.queryUnpaidOpenValves(companyId, orgId);
        // if (CollUtil.isNotEmpty(unpaidOpenList)) { ... }
    }

    private List<HtTasksPerform> buildOpenValveTasks(List<PrHeatValveArchive> archives,
                                                      String companyId, String orgId,
                                                      int instruction, PrOptionsHeat optionsHeat) {
        List<HtTasksPerform> tasks = new ArrayList<>();
        Date now = new Date();
        for (PrHeatValveArchive archive : archives) {
            HtTasksPerform task = new HtTasksPerform();
            task.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
            task.setInstructionType(1); // 1=开阀
            task.setInstruction(instruction);
            task.setNumber(0);
            task.setOrgId(orgId);
            task.setCompanyId(companyId);
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
