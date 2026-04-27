package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.mapper.SysHomeMapper;
import org.sdkj.thermal.service.ISysHomeService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysHomeServiceImpl implements ISysHomeService {

    private final SysHomeMapper sysHomeMapper;

    @Override
    public Map<String, Object> aggregateHomeData(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> result = new HashMap<>();

        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() ->
                result.putAll(queryBlock1(companyId, stationId, stationPartitionId)));
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() ->
                result.putAll(queryBlock2(companyId, stationId, stationPartitionId)));
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() ->
                result.putAll(queryBlock3(companyId, stationId, stationPartitionId)));
        CompletableFuture<Void> f4 = CompletableFuture.runAsync(() ->
                result.putAll(queryBlock4(companyId, stationId, stationPartitionId)));
        CompletableFuture<Void> f6 = CompletableFuture.runAsync(() ->
                result.putAll(queryBlock6(companyId, stationId, stationPartitionId)));
        CompletableFuture<Void> f7 = CompletableFuture.runAsync(() ->
                result.putAll(queryBlock7(companyId, stationId, stationPartitionId)));

        CompletableFuture.allOf(f1, f2, f3, f4, f6, f7).join();
        return result;
    }

    private Map<String, Object> queryBlock1(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("orgNum", sysHomeMapper.queryOrgNum(companyId, stationId, stationPartitionId));
            data.put("orgStationNum", sysHomeMapper.queryOrgStationNum(companyId, stationId, stationPartitionId));
            data.put("buildingNum", sysHomeMapper.queryBuildingNum(companyId, stationId, stationPartitionId));
            data.put("buildingAllNum", sysHomeMapper.queryAllBuildingNum(companyId, stationId, stationPartitionId));
            data.put("stationPartitionNum", sysHomeMapper.queryStationPartitionNum(companyId, stationId, stationPartitionId));
            data.put("houseNum", sysHomeMapper.queryHouseNum(companyId, stationId, stationPartitionId));
            data.put("prUserNum", sysHomeMapper.queryPrUserNum(companyId, stationId, stationPartitionId));
            data.put("heatingArea", sysHomeMapper.queryHeatingArea(companyId, stationId, stationPartitionId));
            data.put("heatingAreaSum", sysHomeMapper.queryHeatingAreaSum(companyId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block1失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock2(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("valveNum", sysHomeMapper.queryValveNum(companyId, stationId, stationPartitionId));
            data.put("valveNumZ", sysHomeMapper.queryValveNumZ(companyId, stationId, stationPartitionId));
            data.put("valveNumList", sysHomeMapper.queryValveNumL(companyId, stationId, stationPartitionId));
            data.put("unitValveNum", sysHomeMapper.queryUnitValveNum(companyId, stationId, stationPartitionId));
            data.put("unitValveNumZ", sysHomeMapper.queryUnitValveNumZ(companyId, stationId, stationPartitionId));
            data.put("unitValveNumList", sysHomeMapper.queryUnitValveNumL(companyId, stationId, stationPartitionId));
            data.put("hotArchiveNum", sysHomeMapper.queryHotArchiveNum(companyId, stationId, stationPartitionId));
            data.put("hotArchiveNumZ", sysHomeMapper.queryHotArchiveNumZ(companyId, stationId, stationPartitionId));
            data.put("hotArchiveNumList", sysHomeMapper.queryHotArchiveNumL(companyId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block2失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock3(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("unitHotArchiveNum", sysHomeMapper.queryUnitHotArchiveNum(companyId, stationId, stationPartitionId));
            data.put("unitHotArchiveNumZ", sysHomeMapper.queryUnitHotArchiveNumZ(companyId, stationId, stationPartitionId));
            data.put("unitHotArchiveNumList", sysHomeMapper.queryUnitHotArchiveNumL(companyId, stationId, stationPartitionId));
            data.put("tempArchiveNum", sysHomeMapper.queryTempArchiveNum(companyId, stationId, stationPartitionId));
            data.put("tempArchiveNumZ", sysHomeMapper.queryTempArchiveNumZ(companyId, stationId, stationPartitionId));
            data.put("tempArchiveNumList", sysHomeMapper.queryTempArchiveNumL(companyId, stationId, stationPartitionId));
            data.put("dtuNum", sysHomeMapper.queryDtuNum(companyId, stationId, stationPartitionId));
            data.put("dtuNumZ", sysHomeMapper.queryDtuNumZ(companyId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block3失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock4(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("valveArchiveOut", sysHomeMapper.queryValveArchiveOut(companyId, stationId, stationPartitionId));
            data.put("valveArchiveIn", sysHomeMapper.queryValveArchiveIn(companyId, stationId, stationPartitionId));
            data.put("valveArchiveActualStatus", sysHomeMapper.queryValveArchiveActualStatus(companyId, stationId, stationPartitionId));
            data.put("unitValveArchiveOut", sysHomeMapper.queryUnitValveArchiveOut(companyId, stationId, stationPartitionId));
            data.put("unitValveArchiveIn", sysHomeMapper.queryUnitValveArchiveIn(companyId, stationId, stationPartitionId));
            data.put("unitValveArchiveActualStatus", sysHomeMapper.queryUnitValveArchiveActualStatus(companyId, stationId, stationPartitionId));
            data.put("tempArchiveTemper", sysHomeMapper.queryTempArchiveTemper(companyId, stationId, stationPartitionId));
            data.put("hotArchiveTotal", sysHomeMapper.queryHotArchiveTotal(companyId, stationId, stationPartitionId));
            data.put("unitHotArchiveTotal", sysHomeMapper.queryUnitHotArchiveTotal(companyId, stationId, stationPartitionId));
            data.put("hotArchiveTotalAll", sysHomeMapper.queryHotArchiveTotalAll(companyId, stationId, stationPartitionId));
            data.put("unitHotArchiveTotalAll", sysHomeMapper.queryUnitHotArchiveTotalAll(companyId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block4失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock6(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("stationData", sysHomeMapper.queryStationData(companyId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block6失败", e);
        }
        return data;
    }

    private Map<String, Object> queryBlock7(String companyId, String stationId, String stationPartitionId) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("jfNum", sysHomeMapper.queryJFNum(companyId, stationId, stationPartitionId));
            data.put("qfNum", sysHomeMapper.queryQFNum(companyId, stationId, stationPartitionId));
            data.put("kzNum", sysHomeMapper.queryKZNum(companyId, stationId, stationPartitionId));
            data.put("tgNum", sysHomeMapper.queryTGNum(companyId, stationId, stationPartitionId));
        } catch (Exception e) {
            log.error("查询首页Block7失败", e);
        }
        return data;
    }
}
