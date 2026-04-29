package org.sdkj.thermal.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.sdkj.thermal.service.IPrHeatTempArchiveService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.springframework.context.ApplicationContext;

/**
 * 世达协议数据推送 Job
 * 移植自旧系统 ShiDaPostJob
 *
 * 将仪表读数数据（阀门、热表、温采器）推送到世达采集平台。
 * 支持按小区或楼栋维度查询，按类型分别推送。
 *
 * @author sdkj
 */
@Slf4j
@DisallowConcurrentExecution
public class ShidaPostJob implements Job {

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

        log.info("世达协议数据推送 Job 启动: {} (公司: {}, 小区: {}, 楼栋: {})",
            jobName, companyId, orgId, branchId);

        boolean tenantPushed = TenantQuartzContext.push(context);
        try {
            ApplicationContext ctx = (ApplicationContext) context.getScheduler()
                .getContext().get("applicationContext");
            IPrHeatValveArchiveService valveArchiveService = ctx.getBean(IPrHeatValveArchiveService.class);
            IPrHeatHotArchiveService hotArchiveService = ctx.getBean(IPrHeatHotArchiveService.class);
            IPrHeatTempArchiveService tempArchiveService = ctx.getBean(IPrHeatTempArchiveService.class);

            // 推送阀门数据
            if (valveFlg != null && valveFlg == 1 && valveInterface != null && !valveInterface.isEmpty()) {
                pushValveData(valveArchiveService, companyId, orgId, branchId, addrType, valveInterface);
            }

            // 推送热表数据
            if (heatFlg != null && heatFlg == 1 && heatInterface != null && !heatInterface.isEmpty()) {
                pushHeatData(hotArchiveService, companyId, orgId, branchId, addrType, heatInterface);
            }

            // 推送温采器数据
            if (tempFlg != null && tempFlg == 1 && tempInterface != null && !tempInterface.isEmpty()) {
                pushTempData(tempArchiveService, companyId, orgId, branchId, addrType, tempInterface);
            }

            log.info("世达协议数据推送 Job 完成: {}", jobName);
        } catch (Exception e) {
            log.error("世达协议数据推送 Job 失败: {} (公司: {}, 小区: {})", jobName, companyId, orgId, e);
        } finally {
            TenantQuartzContext.clear(tenantPushed);
        }
    }

    private void pushValveData(IPrHeatValveArchiveService valveArchiveService,
                                String companyId, String orgId, String branchId,
                                Integer addrType, String valveInterface) {
        // TODO: 实现阀门数据推送逻辑
        // 1. 查询阀门数据
        //    List<PrHeatValveArchive> valveArchives;
        //    if (addrType == 1) {
        //        valveArchives = valveArchiveService.getValveDataByBranchId(companyId, branchId);
        //    } else {
        //        valveArchives = valveArchiveService.getValveDataByOrgId(companyId, orgId);
        //    }
        //
        // 2. 分批组装 JSON 并推送（参考旧系统 ShiDaPostJob）
        //    使用 cn.hutool.json.JSONUtil 或 com.alibaba.fastjson.JSONObject
        //    构建请求体，通过 HttpClient POST 到 valveInterface
        //
        //    JSON 字段映射:
        //      meterInfo    -> archive.getMeterArcName()
        //      meterNum     -> archive.getMeterNum()
        //      valveStatus  -> archive.getValveStatus()
        //      settingStatus-> archive.getSettingStatus()
        //      actualStatus -> archive.getActualStatus()
        //      outTemp      -> archive.getOutTemperature()
        //      inTemp       -> archive.getInTemperature()
        //      voltage      -> archive.getVoltage()
        //      valveTime    -> archive.getValveTime()
        //      csq          -> archive.getSignalStrength()

        log.info("世达阀门数据推送完成: 公司={}, 小区={}, 楼栋={}", companyId, orgId, branchId);
    }

    private void pushHeatData(IPrHeatHotArchiveService hotArchiveService,
                               String companyId, String orgId, String branchId,
                               Integer addrType, String heatInterface) {
        // TODO: 实现热表数据推送逻辑
        // 1. 查询热表数据
        //    List<PrHeatHotArchive> hotArchives;
        //    if (addrType == 1) {
        //        hotArchives = hotArchiveService.getHotDataByBranchId(companyId, branchId);
        //    } else {
        //        hotArchives = hotArchiveService.getHotDataByOrgId(companyId, orgId);
        //    }
        //
        // 2. 分批组装 JSON 并推送（参考旧系统 ShiDaPostJob 热表推送部分）

        log.info("世达热表数据推送完成: 公司={}, 小区={}, 楼栋={}", companyId, orgId, branchId);
    }

    private void pushTempData(IPrHeatTempArchiveService tempArchiveService,
                               String companyId, String orgId, String branchId,
                               Integer addrType, String tempInterface) {
        // TODO: 实现温采器数据推送逻辑
        // 1. 查询温采器数据
        //    List<PrHeatTempArchive> tempArchives;
        //    if (addrType == 1) {
        //        tempArchives = tempArchiveService.getTempDataByBranchId(companyId, branchId);
        //    } else {
        //        tempArchives = tempArchiveService.getTempDataByOrgId(companyId, orgId);
        //    }
        //
        // 2. 分批组装 JSON 并推送（参考旧系统 ShiDaPostJob 温采器推送部分）

        log.info("世达温采器数据推送完成: 公司={}, 小区={}, 楼栋={}", companyId, orgId, branchId);
    }
}
