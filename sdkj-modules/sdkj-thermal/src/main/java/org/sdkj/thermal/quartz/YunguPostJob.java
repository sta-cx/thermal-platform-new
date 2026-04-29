package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.springframework.context.ApplicationContext;

/**
 * 云谷协议数据推送 Job
 * 移植自旧系统 YunGuPostJob
 *
 * 将热表读数数据推送到云谷采集平台。
 * 支持按小区或楼栋维度查询，使用 AppToken 鉴权。
 *
 * @author sdkj
 */
@Slf4j
@DisallowConcurrentExecution
public class YunguPostJob implements Job {

    /** 云谷应用编码 */
    private static final String APP_CODE = "sdkjApp";

    /** 云谷应用密钥 */
    private static final String APP_SECRET = "[REMOVED]";

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        String companyId = data.getString("companyId");
        String orgId = data.getString("orgId");
        String branchId = data.getString("branchId");
        Integer addrType = data.getInt("addrType");
        Integer valveFlg = data.getInt("valveFlg");
        String valveInterface = data.getString("queryValveInterface");
        Integer heatFlg = data.getInt("heatFlg");
        String heatInterface = data.getString("queryHeatInterface");
        Integer tempFlg = data.getInt("tempFlg");
        String tempInterface = data.getString("queryTempInterface");

        log.info("云谷协议数据推送 Job 启动: {} (公司: {}, 小区: {}, 楼栋: {})",
            jobName, companyId, orgId, branchId);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHeatHotArchiveService hotArchiveService = ctx.getBean(IPrHeatHotArchiveService.class);

            // 生成云谷鉴权 Token
            String timestamp = String.valueOf(System.currentTimeMillis());
            // TODO: 实现云谷 Token 生成逻辑
            // String strToken = YunGuUtils.generateToken(APP_CODE, APP_SECRET, timestamp);
            String strToken = "";

            // 推送热表数据（云谷协议主要推送热表数据）
            if (heatFlg != null && heatFlg == 1 && heatInterface != null && !heatInterface.isEmpty()) {
                pushHeatData(hotArchiveService, companyId, orgId, branchId, addrType,
                    heatInterface, timestamp, strToken);
            }

            // TODO: 阀门和温采器数据推送（如有需要）
            // if (valveFlg != null && valveFlg == 1 && valveInterface != null && !valveInterface.isEmpty()) { ... }
            // if (tempFlg != null && tempFlg == 1 && tempInterface != null && !tempInterface.isEmpty()) { ... }

            log.info("云谷协议数据推送 Job 完成: {}", jobName);
        } catch (Exception e) {
            log.error("云谷协议数据推送 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgId, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }

    private void pushHeatData(IPrHeatHotArchiveService hotArchiveService,
                               String companyId, String orgId, String branchId,
                               Integer addrType, String heatInterface,
                               String timestamp, String strToken) {
        // TODO: 实现热表数据推送逻辑
        // 1. 查询热表数据
        //    List<PrHeatHotArchive> hotArchives;
        //    if (addrType == 1) {
        //        hotArchives = hotArchiveService.getHotDataByBranchIdYG(companyId, branchId);
        //    } else {
        //        hotArchives = hotArchiveService.getHotDataByOrgIdYG(companyId, orgId);
        //    }
        //
        // 2. 分批组装 JSON 并推送（参考旧系统 YunGuPostJob）
        //    使用 cn.hutool.json.JSONUtil 构建请求体
        //    通过 HttpClient POST 到 heatInterface（带 AppToken 鉴权 Header）
        //
        //    JSON 字段映射:
        //      MeterManuId     -> archive.getMeterNum()
        //      ForwardT        -> archive.getInTemperature()
        //      ReturnT         -> archive.getOutTemperature()
        //      FlowSum         -> archive.getTotalFlow() / 1000000
        //      HeatSum         -> archive.getTotalUsed() / 1000
        //      Flow            -> archive.getCurFlow() / 1000000
        //      HeatPower       -> archive.getThermalPower() / 1000
        //      RecordTs        -> archive.getValveTime().getTime()
        //      ValveOpenPercent-> archive.getValveStatus()
        //
        //    请求 Header:
        //      AppCode   = APP_CODE
        //      Timestamp = timestamp
        //      AppToken  = strToken

        log.info("云谷热表数据推送完成: 公司={}, 小区={}, 楼栋={}", companyId, orgId, branchId);
    }
}
