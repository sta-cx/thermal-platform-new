package org.sdkj.thermal.service.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.sdkj.thermal.domain.PrHeatStation;
import org.sdkj.thermal.domain.PrHeatStationPartition;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHeatStationMapper;
import org.sdkj.thermal.mapper.PrHeatStationPartitionMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 户阀 Vo 层级字段填充器（小区/楼宇/单元/换热站/换热分站名 + 通讯状态）。
 * 户阀档案仅经 house_id 关联房屋，这些展示字段非数据库字段，需查询后回填。
 * 监控列表与手动控制台共用，避免两份填充逻辑漂移。
 */
@Component
@RequiredArgsConstructor
public class HeatValveVoEnricher {

    private final PrHouseMapper houseMapper;
    private final PrHeatStationPartitionMapper stationPartitionMapper;
    private final PrHeatStationMapper stationMapper;
    private final PrCompanyMapper companyMapper;

    public void enrich(List<PrHeatValveArchiveVo> rows) {
        if (rows == null || rows.isEmpty()) return;

        // 批量查关联房屋
        List<Long> houseIds = rows.stream()
            .map(PrHeatValveArchiveVo::getHouseId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<Long, PrHouseVo> houseMap;
        if (!houseIds.isEmpty()) {
            houseMap = houseMapper.selectVoList(
                new LambdaQueryWrapper<PrHouse>().in(PrHouse::getId, houseIds)
            ).stream().collect(Collectors.toMap(PrHouseVo::getId, h -> h, (a, b) -> a));
        } else {
            houseMap = Collections.emptyMap();
        }

        // 批量查分区 + 换热站名（house.station_type 存的是分区id）
        Set<Long> partitionIds = houseMap.values().stream()
            .map(PrHouseVo::getStationType)
            .map(HeatValveVoEnricher::parseLongOrNull)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, PrHeatStationPartition> partitionMap = Collections.emptyMap();
        Map<Long, PrHeatStation> stationMap = Collections.emptyMap();
        if (!partitionIds.isEmpty()) {
            List<PrHeatStationPartition> parts = stationPartitionMapper.selectBatchIds(partitionIds);
            partitionMap = parts.stream()
                .collect(Collectors.toMap(PrHeatStationPartition::getId, p -> p, (a, b) -> a));
            Set<Long> stationIds = parts.stream()
                .map(PrHeatStationPartition::getStationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            if (!stationIds.isEmpty()) {
                stationMap = stationMapper.selectBatchIds(stationIds).stream()
                    .collect(Collectors.toMap(PrHeatStation::getId, s -> s, (a, b) -> a));
            }
        }

        // 批量查 orgName（orgId → orgName）
        List<String> orgIds = rows.stream()
            .map(PrHeatValveArchiveVo::getOrgId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        Map<String, String> orgNameMap = new HashMap<>();
        for (String oid : orgIds) {
            SysOrganization org = companyMapper.selectOrgById(oid);
            if (org != null) orgNameMap.put(oid, org.getName());
        }

        // 回填
        for (PrHeatValveArchiveVo v : rows) {
            PrHouseVo h = v.getHouseId() != null ? houseMap.get(v.getHouseId()) : null;
            if (h != null) {
                v.setBuildingName(h.getBuildingName());
                v.setUnitName(h.getUnitCode());
                v.setRoomNum(h.getRoomNum());
                v.setIsCharged(h.getIsCharged());
                v.setHouseType(h.getPaySitType() != null ? String.valueOf(h.getPaySitType()) : null);
                v.setIsSpecial(h.getIsSpecial());
                v.setHeatingArea(h.getHeatingArea());
                v.setSiteType(h.getSiteType());
                v.setStationType(h.getStationType());
                Long pid = parseLongOrNull(h.getStationType());
                if (pid != null) {
                    PrHeatStationPartition p = partitionMap.get(pid);
                    if (p != null) {
                        v.setStationPartitionName(p.getName());
                        PrHeatStation st = stationMap.get(p.getStationId());
                        if (st != null) v.setStationName(st.getName());
                    }
                }
            }
            v.setOrgName(orgNameMap.get(v.getOrgId()));
            v.setCurFlow(v.getInsFlow());
            v.setIsVirtual(0);
            v.setScopeStatus(computeScopeStatus(v.getValveTime()));
        }
    }

    /** varchar 安全转 Long（station_type 存的是分区雪花id字符串） */
    private static Long parseLongOrNull(String val) {
        if (StringUtils.isBlank(val)) return null;
        try {
            return Long.valueOf(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 无通讯记录或超 24h → "3" 通讯中断，否则 "1" 正常 */
    private String computeScopeStatus(Date valveTime) {
        if (valveTime == null) return "3";
        long hours = (System.currentTimeMillis() - valveTime.getTime()) / (1000 * 60 * 60);
        if (hours > 24) return "3";
        return "1";
    }
}
