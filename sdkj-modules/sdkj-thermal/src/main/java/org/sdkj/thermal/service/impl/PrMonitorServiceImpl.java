package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.bo.MonitorBo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo.SiteGroupStat;
import org.sdkj.thermal.domain.vo.MonitorExportVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHeatArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatTempArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatUnitHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatUnitValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IPrMonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运行监控 Service 实现
 */
@RequiredArgsConstructor
@Service
public class PrMonitorServiceImpl implements IPrMonitorService {

    private static final BigDecimal DEFAULT_TEMP_DEVIATION = new BigDecimal("3.0");

    private final PrHeatArchiveMapper heatArchiveMapper;
    private final PrHeatValveArchiveMapper valveArchiveMapper;
    private final PrHeatUnitHotArchiveMapper unitHotArchiveMapper;
    private final PrHeatUnitValveArchiveMapper unitValveArchiveMapper;
    private final PrHeatTempArchiveMapper tempArchiveMapper;
    private final PrHouseMapper houseMapper;
    private final PrCompanyMapper companyMapper;

    @Override
    public TableDataInfo<PrHeatArchiveVo> heatList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, kw)
                .or().like(PrHeatArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = heatArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatValveArchiveVo> valveList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatValveArchive::getMeterNum, kw)
                .or().like(PrHeatValveArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        Page<PrHeatValveArchiveVo> result = valveArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        fillHouseFields(result.getRecords());
        return TableDataInfo.build(result);
    }

    private void fillHouseFields(List<PrHeatValveArchiveVo> rows) {
        if (rows == null || rows.isEmpty()) return;

        // 批量查关联房屋
        List<Long> houseIds = rows.stream()
            .map(PrHeatValveArchiveVo::getHouseId)
            .filter(id -> id != null)
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

        // 批量查 orgName（orgId → orgName）
        List<String> orgIds = rows.stream()
            .map(PrHeatValveArchiveVo::getOrgId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        Map<String, String> orgNameMap = new java.util.HashMap<>();
        for (String oid : orgIds) {
            SysOrganization org = companyMapper.selectOrgById(oid);
            if (org != null) orgNameMap.put(oid, org.getName());
        }

        // 回填
        for (PrHeatValveArchiveVo v : rows) {
            PrHouseVo h = v.getHouseId() != null ? houseMap.get(v.getHouseId()) : null;
            if (h != null) {
                v.setBuildingName(h.getBuildingName());
                v.setRoomNum(h.getRoomNum());
                v.setIsCharged(h.getIsCharged());
                v.setHouseType(h.getStationType());
                v.setIsSpecial(h.getIsSpecial());
                v.setHeatingArea(h.getHeatingArea());
                v.setSiteType(h.getSiteType());
            }
            v.setOrgName(orgNameMap.get(v.getOrgId()));
            v.setCurFlow(v.getInsFlow());
            v.setIsVirtual(0);
            v.setScopeStatus(computeScopeStatus(v.getValveTime()));
        }
    }

    private String computeScopeStatus(Date valveTime) {
        if (valveTime == null) return "3"; // 无通讯记录→通讯中断
        long hours = (System.currentTimeMillis() - valveTime.getTime()) / (1000 * 60 * 60);
        if (hours > 24) return "3"; // 超过24小时→通讯中断
        return "1"; // 正常
    }

    @Override
    public TableDataInfo<PrHeatUnitHotArchiveVo> unitHeatList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatUnitHotArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatUnitHotArchive::getMeterNum, kw)
                .or().like(PrHeatUnitHotArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitHotArchive::getCreateTime);
        Page<PrHeatUnitHotArchiveVo> result = unitHotArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatUnitValveArchiveVo> unitValveList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatUnitValveArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatUnitValveArchive::getMeterNum, kw)
                .or().like(PrHeatUnitValveArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitValveArchive::getCreateTime);
        Page<PrHeatUnitValveArchiveVo> result = unitValveArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatTempArchiveVo> tempList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatTempArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatTempArchive::getMeterNum, kw)
                .or().like(PrHeatTempArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        Page<PrHeatTempArchiveVo> result = tempArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public MonitorAggregateVo aggregate(MonitorBo bo) {
        MonitorAggregateVo vo = new MonitorAggregateVo();

        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId());
        valveLqw.eq(PrHeatValveArchive::getIsChanged, 0);
        List<PrHeatValveArchive> valves = valveArchiveMapper.selectList(valveLqw);

        vo.setTotalCount(valves.size());
        BigDecimal sumOut = BigDecimal.ZERO;
        BigDecimal sumRoom = BigDecimal.ZERO;
        int outN = 0;
        int roomN = 0;
        int onlineN = 0;
        for (PrHeatValveArchive v : valves) {
            if (v.getOutTemperature() != null) {
                sumOut = sumOut.add(v.getOutTemperature());
                outN++;
            }
            if (v.getRoomTemp() != null) {
                sumRoom = sumRoom.add(v.getRoomTemp());
                roomN++;
            }
            // 通讯状态与列表行口径一致：computeScopeStatus 返回 "1"=正常(在线)
            if ("1".equals(computeScopeStatus(v.getValveTime()))) {
                onlineN++;
            }
        }
        if (outN > 0) {
            BigDecimal avgOut = sumOut.divide(BigDecimal.valueOf(outN), 2, RoundingMode.HALF_UP);
            vo.setAvgOutTemp(avgOut);
            vo.setOutTempPjMin(avgOut.subtract(DEFAULT_TEMP_DEVIATION).setScale(2, RoundingMode.HALF_UP));
            vo.setOutTempPjMax(avgOut.add(DEFAULT_TEMP_DEVIATION).setScale(2, RoundingMode.HALF_UP));
        }
        if (roomN > 0) {
            vo.setAvgInTemp(sumRoom.divide(BigDecimal.valueOf(roomN), 2, RoundingMode.HALF_UP));
        }
        vo.setOnlineCount(onlineN);
        vo.setOfflineCount(vo.getTotalCount() - onlineN);

        if (StringUtils.isNotBlank(bo.getOrgId())) {
            buildSiteGroups(vo, bo.getOrgId(), valves);
        }
        return vo;
    }

    private void buildSiteGroups(MonitorAggregateVo vo, String orgId, List<PrHeatValveArchive> valves) {
        LambdaQueryWrapper<PrHouse> houseLqw = new LambdaQueryWrapper<>();
        houseLqw.eq(PrHouse::getOrgId, orgId);
        houseLqw.select(PrHouse::getId, PrHouse::getSiteType);
        List<PrHouse> houses = houseMapper.selectList(houseLqw);
        if (houses.isEmpty()) {
            return;
        }

        Map<Long, PrHeatValveArchive> houseValveMap = valves.stream()
            .filter(v -> v.getHouseId() != null)
            .collect(Collectors.toMap(
                PrHeatValveArchive::getHouseId,
                v -> v,
                (a, b) -> a.getOutTemperature() != null ? a : b
            ));

        // 按 siteType 分组
        Map<String, List<PrHouse>> bySiteType = houses.stream()
            .filter(h -> StringUtils.isNotBlank(h.getSiteType()))
            .collect(Collectors.groupingBy(PrHouse::getSiteType));

        for (Map.Entry<String, List<PrHouse>> entry : bySiteType.entrySet()) {
            SiteGroupStat stat = buildGroupStat(entry.getValue(), houseValveMap);
            switch (entry.getKey()) {
                case "1" -> vo.setSideGroup(stat);
                case "2" -> vo.setTopGroup(stat);
                case "3" -> vo.setBottomGroup(stat);
                case "4" -> vo.setMidGroup(stat);
                default -> {}
            }
        }
    }

    private SiteGroupStat buildGroupStat(List<PrHouse> houses, Map<Long, PrHeatValveArchive> houseValveMap) {
        SiteGroupStat stat = new SiteGroupStat();
        stat.setCount(houses.size());
        BigDecimal sumOut = BigDecimal.ZERO;
        BigDecimal sumRoom = BigDecimal.ZERO;
        int outN = 0;
        int roomN = 0;
        for (PrHouse h : houses) {
            PrHeatValveArchive v = houseValveMap.get(h.getId());
            if (v != null) {
                if (v.getOutTemperature() != null) {
                    sumOut = sumOut.add(v.getOutTemperature());
                    outN++;
                }
                if (v.getRoomTemp() != null) {
                    sumRoom = sumRoom.add(v.getRoomTemp());
                    roomN++;
                }
            }
        }
        if (outN > 0) {
            stat.setAvgOutTemp(sumOut.divide(BigDecimal.valueOf(outN), 2, RoundingMode.HALF_UP));
        }
        if (roomN > 0) {
            stat.setAvgRoomTemp(sumRoom.divide(BigDecimal.valueOf(roomN), 2, RoundingMode.HALF_UP));
        }
        return stat;
    }

    @Override
    @Transactional
    public int generateVirtualDevice(String orgId) {
        LambdaQueryWrapper<PrHouse> houseLqw = new LambdaQueryWrapper<>();
        houseLqw.eq(PrHouse::getOrgId, orgId);
        List<PrHouseVo> houses = houseMapper.selectVoList(houseLqw);
        if (houses.isEmpty()) {
            return 0;
        }

        LambdaQueryWrapper<PrHeatTempArchive> tempLqw = new LambdaQueryWrapper<>();
        tempLqw.eq(PrHeatTempArchive::getOrgId, orgId);
        tempLqw.select(PrHeatTempArchive::getHouseId);
        List<PrHeatTempArchive> existing = tempArchiveMapper.selectList(tempLqw);
        java.util.Set<Long> coveredHouseIds = existing.stream()
            .map(PrHeatTempArchive::getHouseId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());

        List<PrHeatTempArchive> toInsert = houses.stream()
            .filter(h -> !coveredHouseIds.contains(h.getId()))
            .map(h -> {
                PrHeatTempArchive vd = new PrHeatTempArchive();
                vd.setHouseId(h.getId());
                vd.setOrgId(orgId);
                vd.setIsVirtual(1);
                vd.setMeterNum("V-" + h.getId());
                vd.setTemper(BigDecimal.valueOf(22.0));
                return vd;
            })
            .toList();

        if (!toInsert.isEmpty()) {
            tempArchiveMapper.insertBatch(toInsert);
        }
        return toInsert.size();
    }

    private static final Map<String, String> SITE_TYPE_LABELS = Map.of(
        "1", "边户", "2", "顶户", "3", "底户", "4", "中间户", "5", "不利环路户", "6", "未知"
    );

    private static final Map<Integer, String> PAYMENT_LABELS = Map.of(
        0, "未缴费", 1, "已缴费", 2, "停供", 3, "空置"
    );

    // 通讯状态标签，与 computeScopeStatus 返回值口径一致（"1"=正常，"3"=通讯中断）
    private static final Map<String, String> SCOPE_STATUS_LABELS = Map.of(
        "1", "正常", "3", "通讯中断"
    );

    @Override
    public List<MonitorExportVo> exportList(MonitorBo bo) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId());
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        List<PrHeatValveArchive> valves = valveArchiveMapper.selectList(lqw);
        if (valves.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查关联房屋
        List<Long> houseIds = valves.stream()
            .map(PrHeatValveArchive::getHouseId)
            .filter(id -> id != null)
            .distinct()
            .toList();
        Map<Long, PrHouseVo> houseMap;
        if (!houseIds.isEmpty()) {
            LambdaQueryWrapper<PrHouse> houseLqw = new LambdaQueryWrapper<>();
            houseLqw.in(PrHouse::getId, houseIds);
            if (StringUtils.isNotBlank(bo.getBuildingId())) {
                houseLqw.eq(PrHouse::getBuildingId, bo.getBuildingId());
            }
            if (StringUtils.isNotBlank(bo.getUnit())) {
                houseLqw.eq(PrHouse::getUnitCode, bo.getUnit());
            }
            List<PrHouseVo> houses = houseMapper.selectVoList(houseLqw);
            houseMap = houses.stream().collect(Collectors.toMap(PrHouseVo::getId, h -> h, (a, b) -> a));
        } else {
            houseMap = Collections.emptyMap();
        }

        // 查 orgName
        String orgName = null;
        if (StringUtils.isNotBlank(bo.getOrgId())) {
            SysOrganization org = companyMapper.selectOrgById(bo.getOrgId());
            if (org != null) {
                orgName = org.getName();
            }
        }

        List<MonitorExportVo> result = new ArrayList<>(valves.size());
        // 楼宇/单元筛选生效时，houseMap 已按条件过滤，关联不到房屋的阀门行应被排除
        boolean houseFilterActive = StringUtils.isNotBlank(bo.getBuildingId())
            || StringUtils.isNotBlank(bo.getUnit());
        for (PrHeatValveArchive v : valves) {
            MonitorExportVo row = new MonitorExportVo();
            PrHouseVo h = v.getHouseId() != null ? houseMap.get(v.getHouseId()) : null;
            if (houseFilterActive && h == null) {
                continue;
            }
            if (h != null) {
                row.setOrgName(orgName);
                row.setBuildingName(h.getBuildingName());
                row.setUnitCode(h.getUnitCode());
                row.setRoomNum(h.getRoomNum());
                row.setSiteType(SITE_TYPE_LABELS.getOrDefault(h.getSiteType(), h.getSiteType()));
                row.setPaymentLabel(PAYMENT_LABELS.get(h.getIsCharged()));
                row.setHeatingArea(h.getHeatingArea());
            }
            row.setRoomTemp(v.getRoomTemp());
            row.setOutTemperature(v.getOutTemperature());
            row.setCurFlow(v.getInsFlow());
            // "通讯状态"列：按 valveTime 计算通讯状态并转中文，与列表行口径一致（非阀门开关状态）
            row.setScopeStatus(SCOPE_STATUS_LABELS.getOrDefault(computeScopeStatus(v.getValveTime()), ""));
            result.add(row);
        }
        return result;
    }
}
