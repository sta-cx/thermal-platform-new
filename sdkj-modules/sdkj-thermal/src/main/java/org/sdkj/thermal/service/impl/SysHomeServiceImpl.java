package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.tenant.core.TenantDataSourceHelper;
import org.sdkj.thermal.mapper.ThermalSysHomeMapper;
import org.sdkj.thermal.service.ISysHomeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("thermalSysHomeServiceImpl")
@RequiredArgsConstructor
public class SysHomeServiceImpl implements ISysHomeService {

    private final ThermalSysHomeMapper sysHomeMapper;

    @Override
    public Map<String, Object> aggregateHomeData(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> result = new HashMap<>();
        String tenantCode = LoginHelper.getTenantCode();

        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() ->
                withTenant(tenantCode, () -> result.putAll(queryBlock1(userId, stationId, stationPartitionId))));
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() ->
                withTenant(tenantCode, () -> result.putAll(queryBlock2(userId, stationId, stationPartitionId))));
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() ->
                withTenant(tenantCode, () -> result.putAll(queryBlock3(userId, stationId, stationPartitionId))));
        CompletableFuture<Void> f4 = CompletableFuture.runAsync(() ->
                withTenant(tenantCode, () -> result.putAll(queryBlock4(userId, stationId, stationPartitionId))));
        CompletableFuture<Void> f6 = CompletableFuture.runAsync(() ->
                withTenant(tenantCode, () -> result.putAll(queryBlock6(userId, stationId, stationPartitionId))));
        CompletableFuture<Void> f7 = CompletableFuture.runAsync(() ->
                withTenant(tenantCode, () -> result.putAll(queryBlock7(userId, stationId, stationPartitionId))));

        CompletableFuture.allOf(f1, f2, f3, f4, f6, f7).join();
        return result;
    }

    private void withTenant(String tenantCode, Runnable action) {
        boolean pushed = TenantDataSourceHelper.pushTenant(tenantCode);
        try {
            action.run();
        } finally {
            TenantDataSourceHelper.clearTenant(pushed);
        }
    }

    private Map<String, Object> queryBlock1(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("orgNum", sysHomeMapper.queryOrgNum(userId, stationId, stationPartitionId));
            data.put("orgStationNum", sysHomeMapper.queryOrgStationNum(userId, stationId, stationPartitionId));
            data.put("buildingNum", sysHomeMapper.queryBuildingNum(userId, stationId, stationPartitionId));
            data.put("buildingAllNum", sysHomeMapper.queryAllBuildingNum(userId, stationId, stationPartitionId));
            data.put("stationPartitionNum", sysHomeMapper.queryStationPartitionNum(userId, stationId, stationPartitionId));
            data.put("houseNum", sysHomeMapper.queryHouseNum(userId, stationId, stationPartitionId));
            data.put("prUserNum", sysHomeMapper.queryPrUserNum(userId, stationId, stationPartitionId));
            data.put("heatingArea", sysHomeMapper.queryHeatingArea(userId, stationId, stationPartitionId));
            data.put("heatingAreaSum", sysHomeMapper.queryHeatingAreaSum(userId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block1失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock2(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("valveNum", sysHomeMapper.queryValveNum(userId, stationId, stationPartitionId));
            data.put("valveNumZ", sysHomeMapper.queryValveNumZ(userId, stationId, stationPartitionId));
            data.put("valveNumList", sysHomeMapper.queryValveNumL(userId, stationId, stationPartitionId));
            data.put("unitValveNum", sysHomeMapper.queryUnitValveNum(userId, stationId, stationPartitionId));
            data.put("unitValveNumZ", sysHomeMapper.queryUnitValveNumZ(userId, stationId, stationPartitionId));
            data.put("unitValveNumList", sysHomeMapper.queryUnitValveNumL(userId, stationId, stationPartitionId));
            data.put("hotArchiveNum", sysHomeMapper.queryHotArchiveNum(userId, stationId, stationPartitionId));
            data.put("hotArchiveNumZ", sysHomeMapper.queryHotArchiveNumZ(userId, stationId, stationPartitionId));
            data.put("hotArchiveNumList", sysHomeMapper.queryHotArchiveNumL(userId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block2失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock3(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("unitHotArchiveNum", sysHomeMapper.queryUnitHotArchiveNum(userId, stationId, stationPartitionId));
            data.put("unitHotArchiveNumZ", sysHomeMapper.queryUnitHotArchiveNumZ(userId, stationId, stationPartitionId));
            data.put("unitHotArchiveNumList", sysHomeMapper.queryUnitHotArchiveNumL(userId, stationId, stationPartitionId));
            data.put("tempArchiveNum", sysHomeMapper.queryTempArchiveNum(userId, stationId, stationPartitionId));
            data.put("tempArchiveNumZ", sysHomeMapper.queryTempArchiveNumZ(userId, stationId, stationPartitionId));
            data.put("tempArchiveNumList", sysHomeMapper.queryTempArchiveNumL(userId, stationId, stationPartitionId));
            data.put("dtuNum", sysHomeMapper.queryDtuNum(userId, stationId, stationPartitionId));
            data.put("dtuNumZ", sysHomeMapper.queryDtuNumZ(userId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block3失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock4(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("valveArchiveOut", sysHomeMapper.queryValveArchiveOut(userId, stationId, stationPartitionId));
            data.put("valveArchiveIn", sysHomeMapper.queryValveArchiveIn(userId, stationId, stationPartitionId));
            data.put("valveArchiveActualStatus", sysHomeMapper.queryValveArchiveActualStatus(userId, stationId, stationPartitionId));
            data.put("unitValveArchiveOut", sysHomeMapper.queryUnitValveArchiveOut(userId, stationId, stationPartitionId));
            data.put("unitValveArchiveIn", sysHomeMapper.queryUnitValveArchiveIn(userId, stationId, stationPartitionId));
            data.put("unitValveArchiveActualStatus", sysHomeMapper.queryUnitValveArchiveActualStatus(userId, stationId, stationPartitionId));
            data.put("tempArchiveTemper", sysHomeMapper.queryTempArchiveTemper(userId, stationId, stationPartitionId));
            data.put("hotArchiveTotal", sysHomeMapper.queryHotArchiveTotal(userId, stationId, stationPartitionId));
            data.put("unitHotArchiveTotal", sysHomeMapper.queryUnitHotArchiveTotal(userId, stationId, stationPartitionId));
            data.put("hotArchiveTotalAll", sysHomeMapper.queryHotArchiveTotalAll(userId, stationId, stationPartitionId));
            data.put("unitHotArchiveTotalAll", sysHomeMapper.queryUnitHotArchiveTotalAll(userId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block4失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock6(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("stationData", sysHomeMapper.queryStationData(userId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block6失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock7(Long userId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("jfNum", sysHomeMapper.queryJFNum(userId, stationId, stationPartitionId));
            data.put("qfNum", sysHomeMapper.queryQFNum(userId, stationId, stationPartitionId));
            data.put("kzNum", sysHomeMapper.queryKZNum(userId, stationId, stationPartitionId));
            data.put("tgNum", sysHomeMapper.queryTGNum(userId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block7失败", e);
        }
        return data;
    }
}
