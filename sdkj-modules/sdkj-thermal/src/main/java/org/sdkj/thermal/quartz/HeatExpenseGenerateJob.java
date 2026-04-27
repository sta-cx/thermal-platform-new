package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.service.IPrExpenseItemService;
import org.sdkj.thermal.service.IPrExpenseService;
import org.sdkj.thermal.service.IPrHouseExpenseService;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 采暖费生成 Job
 * 迁移自旧系统 GenerateHeatExpenseJob
 *
 * @author sdkj
 */
@Slf4j
public class HeatExpenseGenerateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String companyId = data.getString("companyId");
        String orgId = data.getString("orgId");

        log.info("采暖费生成 Job 启动: {} (公司: {}, 小区: {})", jobName, companyId, orgId);

        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrExpenseService expenseService = ctx.getBean(IPrExpenseService.class);
            IPrExpenseItemService expenseItemService = ctx.getBean(IPrExpenseItemService.class);
            IPrHouseExpenseService houseExpenseService = ctx.getBean(IPrHouseExpenseService.class);

            String[] orgIds = orgId.split(",");
            for (String realOrgId : orgIds) {
                List<PrExpenseItemVo> items = expenseItemService.selectByItemGroup(
                    companyId, realOrgId, "6", null);
                if (items == null || items.isEmpty()) {
                    log.debug("无采暖费项目: 公司={}, 小区={}", companyId, realOrgId);
                    continue;
                }
                for (PrExpenseItemVo item : items) {
                    List<PrHouseExpense> houseExpenses = houseExpenseService.lambdaQuery()
                        .eq(PrHouseExpense::getCompanyId, companyId)
                        .eq(PrHouseExpense::getOrgId, realOrgId)
                        .eq(PrHouseExpense::getItemGroup, item.getItemGroup())
                        .eq(PrHouseExpense::getItemCode, item.getItemCode())
                        .list();
                    if (houseExpenses != null && !houseExpenses.isEmpty()) {
                        expenseService.insertData(houseExpenses);
                        expenseService.updateFormula(companyId, realOrgId);
                        log.info("采暖费生成: 公司={}, 小区={}, 项目={}, 房屋数={}",
                            companyId, realOrgId, item.getItemCode(), houseExpenses.size());
                    }
                }
            }
            log.info("采暖费生成 Job 完成: {}", jobName);
        } catch (Exception e) {
            log.error("采暖费生成 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgId, e);
        }
    }
}
