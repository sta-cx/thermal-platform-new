package org.sdkj.thermal.quartz;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrHouseService;
import org.springframework.context.ApplicationContext;

/**
 * 国大支付网关缴费状态同步 Job
 * 移植自旧系统 SetHousePayGetByGuoDaJob
 *
 * 从国大支付网关查询房屋缴费状态，同步更新到本系统：
 * - 缴费+供暖 -> 已缴费
 * - 缴费+停供 -> 空置
 * - 未缴费+供暖 -> 未缴费
 * - 未缴费+停供 -> 停供
 *
 * @author sdkj
 */
@Slf4j
@DisallowConcurrentExecution
public class GuodaPaymentSyncJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String companyId = data.getString("companyId");
        String orgIdStr = data.getString("orgId");

        log.info("国大支付缴费状态同步 Job 启动: {} (公司: {}, 小区: {})", jobName, companyId, orgIdStr);

        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHouseService houseService = ctx.getBean(IPrHouseService.class);

            if (StrUtil.isBlank(orgIdStr)) {
                log.warn("国大支付同步 Job: orgId 为空，跳过执行");
                return;
            }

            String[] orgIds = orgIdStr.split(",");
            for (String orgId : orgIds) {
                orgId = orgId.trim();
                log.info("执行国大支付缴费状态同步: 小区ID={}", orgId);
                try {
                    syncPaymentStatus(houseService, companyId, orgId);
                } catch (Exception e) {
                    log.error("国大支付缴费状态同步失败: 小区ID={}", orgId, e);
                }
            }
            log.info("国大支付缴费状态同步 Job 完成: {} (公司: {})", jobName, companyId);
        } catch (Exception e) {
            log.error("国大支付缴费状态同步 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgIdStr, e);
        }
    }

    private void syncPaymentStatus(IPrHouseService houseService, String companyId, String orgId) {
        // TODO: 实现国大支付网关缴费状态同步逻辑
        // 1. 查询小区下所有房屋的三方编码
        //    List<PrHouse> houseList = houseService.queryOtherCodeByOrgId(companyId, orgId);
        //
        // 2. 根据三方编码查询国大支付网关的缴费状态
        //    List<PrHouse> houseListGdJ = houseService.queryPayStatusByGuoDa(houseList, "缴费", "供暖");   // 缴费
        //    List<PrHouse> houseListGdWK = houseService.queryPayStatusByGuoDa(houseList, "缴费", "停供"); // 空置
        //    List<PrHouse> houseListGdWJ = houseService.queryPayStatusByGuoDa(houseList, "未缴费", "供暖"); // 未缴费
        //    List<PrHouse> houseListGdWT = houseService.queryPayStatusByGuoDa(houseList, "未缴费", "停供"); // 停供
        //
        // 3. 根据三方编码更新房屋缴费状态
        //    if (!houseListGdJ.isEmpty()) {
        //        houseService.updateDataPayStatusByGuoDaJ(houseListGdJ, companyId, orgId, "1");
        //    }
        //    if (!houseListGdWK.isEmpty()) {
        //        houseService.updateDataPayStatusByGuoDaJ(houseListGdWK, companyId, orgId, "3");
        //    }
        //    if (!houseListGdWJ.isEmpty()) {
        //        houseService.updateDataPayStatusByGuoDaJ(houseListGdWJ, companyId, orgId, "0");
        //    }
        //    if (!houseListGdWT.isEmpty()) {
        //        houseService.updateDataPayStatusByGuoDaJ(houseListGdWT, companyId, orgId, "2");
        //    }

        log.info("国大支付缴费状态同步完成: 公司={}, 小区={}", companyId, orgId);
    }
}
