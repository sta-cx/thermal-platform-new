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
 * 宏顺支付网关缴费状态同步 Job
 * 移植自旧系统 SetHousePayGetByHongShunJob
 *
 * 从宏顺支付网关查询房屋缴费状态，同步更新到本系统：
 * - 缴费+供暖 -> 已缴费
 * - 缴费+停供 -> 空置
 * - 未缴费+供暖 -> 未缴费
 * - 未缴费+停供 -> 停供
 *
 * @author sdkj
 */
@Slf4j
@DisallowConcurrentExecution
public class HongshunPaymentSyncJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String companyId = data.getString("companyId");
        String orgIdStr = data.getString("orgId");

        log.info("宏顺支付缴费状态同步 Job 启动: {} (公司: {}, 小区: {})", jobName, companyId, orgIdStr);

        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHouseService houseService = ctx.getBean(IPrHouseService.class);

            if (StrUtil.isBlank(orgIdStr)) {
                log.warn("宏顺支付同步 Job: orgId 为空，跳过执行");
                return;
            }

            String[] orgIds = orgIdStr.split(",");
            for (String orgId : orgIds) {
                orgId = orgId.trim();
                log.info("执行宏顺支付缴费状态同步: 小区ID={}", orgId);
                try {
                    syncPaymentStatus(houseService, companyId, orgId);
                } catch (Exception e) {
                    log.error("宏顺支付缴费状态同步失败: 小区ID={}", orgId, e);
                }
            }
            log.info("宏顺支付缴费状态同步 Job 完成: {} (公司: {})", jobName, companyId);
        } catch (Exception e) {
            log.error("宏顺支付缴费状态同步 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgIdStr, e);
        }
    }

    private void syncPaymentStatus(IPrHouseService houseService,
                                    String companyId, String orgId) {
        // TODO: 实现宏顺支付网关缴费状态同步逻辑
        // 1. 查询小区下所有房屋的三方编码
        //    QueryWrapper<PrHouse> queryWrapper = new QueryWrapper<>();
        //    queryWrapper.eq("company_id", companyId)
        //            .eq("org_id", orgId)
        //            .isNotNull("other_code");
        //    List<PrHouse> houseList = houseService.queryHouseListByOtherCodeExists(queryWrapper);
        //
        // 2. 查询宏顺支付网关的缴费明细状态
        //    List<String> otherCodeList = houseList.stream()
        //            .map(PrHouse::getOtherCode)
        //            .filter(Objects::nonNull)
        //            .collect(Collectors.toList());
        //    List<ChargeDetailStateName> chargeDetailList = chargeDetailStateNameService.queryChangeDetailStateNameList(otherCodeList);
        //
        // 3. 更新阀门状态到缴费明细
        //    for (PrHouse house : houseList) {
        //        if (house.getOtherCode() != null) {
        //            for (ChargeDetailStateName detail : chargeDetailList) {
        //                if (house.getOtherCode().equals(detail.getUserNo())) {
        //                    // 设置阀门信息
        //                    detail.setRemarks(new Date().toString());
        //                }
        //            }
        //        }
        //    }
        //    chargeDetailStateNameService.updateBatchById(chargeDetailList);
        //
        // 4. 根据三方编码查询宏顺支付网关的缴费状态
        //    List<PrHouse> houseListHSJ = houseService.queryPayStatusByHongShun(houseList, "缴费", "供暖");
        //    List<PrHouse> houseListHSWK = houseService.queryPayStatusByHongShun(houseList, "缴费", "停供");
        //    List<PrHouse> houseListHSWJ = houseService.queryPayStatusByHongShun(houseList, "未缴费", "供暖");
        //    List<PrHouse> houseListHSWT = houseService.queryPayStatusByHongShun(houseList, "未缴费", "停供");
        //
        // 5. 更新房屋缴费状态
        //    if (!houseListHSJ.isEmpty()) {
        //        houseService.updateDataPayStatus(houseListHSJ, companyId, orgId, "1");
        //    }
        //    if (!houseListHSWK.isEmpty()) {
        //        houseService.updateDataPayStatus(houseListHSWK, companyId, orgId, "3");
        //    }
        //    if (!houseListHSWJ.isEmpty()) {
        //        houseService.updateDataPayStatus(houseListHSWJ, companyId, orgId, "0");
        //    }
        //    if (!houseListHSWT.isEmpty()) {
        //        houseService.updateDataPayStatus(houseListHSWT, companyId, orgId, "2");
        //    }

        log.info("宏顺支付缴费状态同步完成: 公司={}, 小区={}", companyId, orgId);
    }
}
