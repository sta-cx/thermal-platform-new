package org.sdkj.thermal.service.impl;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.PrHeatCommandValveArchive;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrHouseLog;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.bo.ChangeHistoryQueryBo;
import org.sdkj.thermal.domain.bo.MonitorBo;
import org.sdkj.thermal.domain.bo.MonitorManualControlBo;
import org.sdkj.thermal.domain.bo.MonitorOtherCodeReadBo;
import org.sdkj.thermal.domain.bo.MonitorOtherCodeWriteBo;
import org.sdkj.thermal.domain.bo.MonitorSetValveGroupBo;
import org.sdkj.thermal.domain.bo.MonitorXunceBo;
import org.sdkj.thermal.domain.bo.SpecialHouseBo;
import org.sdkj.thermal.domain.bo.StopSupplyBo;
import org.sdkj.thermal.domain.vo.ChangeHistoryVo;
import org.sdkj.thermal.domain.vo.HouseDeviceDetailVo;
import org.sdkj.thermal.domain.vo.HouseDeviceDetailVo.DeviceDataVo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo.SiteGroupStat;
import org.sdkj.thermal.domain.vo.MonitorExportVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatCommandValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.domain.dto.PrHeatVo;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHeatArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatCommandValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatTempArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatUnitHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatUnitValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseLogMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IPrHeatArchiveService;
import org.sdkj.thermal.service.IPrMonitorService;
import org.sdkj.system.mapper.SysUserMapper;
import org.sdkj.system.domain.vo.SysUserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运行监控 Service 实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PrMonitorServiceImpl implements IPrMonitorService {

    private static final BigDecimal DEFAULT_TEMP_DEVIATION = new BigDecimal("3.0");

    private final PrHeatArchiveMapper heatArchiveMapper;
    private final PrHeatValveArchiveMapper valveArchiveMapper;
    private final PrHeatUnitHotArchiveMapper unitHotArchiveMapper;
    private final PrHeatUnitValveArchiveMapper unitValveArchiveMapper;
    private final PrHeatTempArchiveMapper tempArchiveMapper;
    private final PrHeatCommandValveArchiveMapper commandValveArchiveMapper;
    private final PrHeatHotArchiveMapper hotArchiveMapper;
    private final PrHouseMapper houseMapper;
    private final PrCompanyMapper companyMapper;
    private final PrHouseLogMapper houseLogMapper;
    private final IPrHeatArchiveService heatArchiveService;
    private final SysUserMapper sysUserMapper;
    private final ObjectMapper objectMapper;

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

    // ========== M-BE 端点实现 ==========

    @Override
    public HouseDeviceDetailVo houseDetail(Long houseId) {
        PrHouseVo house = houseMapper.selectVoById(houseId);
        HouseDeviceDetailVo detail = new HouseDeviceDetailVo();
        if (house != null) {
            detail.setRoomNum(house.getRoomNum());
            detail.setPhone(house.getPhone());
            detail.setUserName(house.getUserName());
            detail.setCode(house.getCode());
            detail.setBuildingName(house.getBuildingName());
            detail.setUnitCode(house.getUnitCode());
            detail.setAddress(house.getAddress());
            // paymentStatus: pr_house.is_charged 映射为缴费状态
            detail.setPaymentStatus(house.getIsCharged());
            // orgName: 查小区名称
            if (StringUtils.isNotBlank(house.getOrgId())) {
                SysOrganization org = companyMapper.selectOrgById(house.getOrgId());
                if (org != null) {
                    detail.setOrgName(org.getName());
                }
            }
            // roomTemp: 取 pr_house.room_temp
            detail.setRoomTemp(house.getRoomTemp() != null ? house.getRoomTemp().toPlainString() : null);
        }

        // 调节阀（pr_heat_valve_archive）
        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.eq(PrHeatValveArchive::getHouseId, houseId);
        valveLqw.eq(PrHeatValveArchive::getIsChanged, 0);
        List<PrHeatValveArchiveVo> valveVos = valveArchiveMapper.selectVoList(valveLqw);
        detail.setHeatValveArchiveList(valveVos.stream().map(this::toValveDeviceData).toList());

        // 开关阀（pr_heat_command_valve_archive）
        LambdaQueryWrapper<PrHeatCommandValveArchive> cmdLqw = new LambdaQueryWrapper<>();
        cmdLqw.eq(PrHeatCommandValveArchive::getHouseId, houseId);
        cmdLqw.eq(PrHeatCommandValveArchive::getIsChanged, 0);
        List<PrHeatCommandValveArchiveVo> cmdVos = commandValveArchiveMapper.selectVoList(cmdLqw);
        detail.setHeatCommandValveArchives(cmdVos.stream().map(this::toCommandValveDeviceData).toList());

        // 热表（pr_heat_hot_archive）
        LambdaQueryWrapper<PrHeatHotArchive> hotLqw = new LambdaQueryWrapper<>();
        hotLqw.eq(PrHeatHotArchive::getHouseId, houseId);
        hotLqw.eq(PrHeatHotArchive::getIsChanged, 0);
        List<PrHeatHotArchiveVo> hotVos = hotArchiveMapper.selectVoList(hotLqw);
        detail.setHeatHotArchiveList(hotVos.stream().map(this::toHotDeviceData).toList());

        // 温采器（pr_heat_temp_archive）
        LambdaQueryWrapper<PrHeatTempArchive> tempLqw = new LambdaQueryWrapper<>();
        tempLqw.eq(PrHeatTempArchive::getHouseId, houseId);
        tempLqw.eq(PrHeatTempArchive::getIsChanged, 0);
        List<PrHeatTempArchiveVo> tempVos = tempArchiveMapper.selectVoList(tempLqw);
        detail.setHeatTempArchiveList(tempVos.stream().map(this::toTempDeviceData).toList());

        return detail;
    }

    private DeviceDataVo toValveDeviceData(PrHeatValveArchiveVo v) {
        DeviceDataVo d = new DeviceDataVo();
        d.setId(v.getId());
        d.setMeterArcName(v.getMeterArcName());
        d.setMeterNum(v.getMeterNum());
        d.setDtuNum(v.getDtuNum());
        d.setChanNum(v.getChanNum());
        d.setSettingStatus(v.getSettingStatus());
        d.setActualStatus(v.getActualStatus());
        d.setVoltage(v.getVoltage());
        d.setValveStatus(v.getValveStatus());
        d.setSignalStrength(v.getSignalStrength());
        d.setReportingInterval(v.getReportingInterval());
        d.setIntervalUnit(v.getIntervalUnit());
        return d;
    }

    private DeviceDataVo toCommandValveDeviceData(PrHeatCommandValveArchiveVo v) {
        DeviceDataVo d = new DeviceDataVo();
        d.setId(v.getId());
        d.setMeterArcName(v.getMeterArcName());
        d.setMeterNum(v.getMeterNum());
        d.setDtuNum(v.getDtuNum());
        d.setChanNum(v.getChanNum());
        d.setSettingStatus(v.getSettingStatus());
        d.setActualStatus(v.getActualStatus());
        d.setVoltage(v.getVoltage());
        d.setValveStatus(v.getValveStatus());
        d.setSignalStrength(v.getSignalStrength());
        d.setReportingInterval(v.getReportingInterval());
        d.setIntervalUnit(v.getIntervalUnit());
        return d;
    }

    private DeviceDataVo toHotDeviceData(PrHeatHotArchiveVo v) {
        DeviceDataVo d = new DeviceDataVo();
        d.setId(v.getId());
        d.setMeterArcName(v.getMeterArcName());
        d.setMeterNum(v.getMeterNum());
        d.setDtuNum(v.getDtuNum());
        d.setChanNum(v.getChanNum());
        d.setValveStatus(v.getValveStatus());
        d.setVoltage(v.getVoltage());
        d.setSignalStrength(v.getSignalStrength());
        d.setCurrentBalance(v.getCurrentBalance());
        d.setStatus1(v.getStatus1());
        d.setCellStatus(v.getCellStatus());
        d.setReportingInterval(null);
        d.setIntervalUnit(null);
        return d;
    }

    private DeviceDataVo toTempDeviceData(PrHeatTempArchiveVo v) {
        DeviceDataVo d = new DeviceDataVo();
        d.setId(v.getId());
        d.setMeterArcName(v.getMeterArcName());
        d.setMeterNum(v.getMeterNum());
        d.setVoltage(v.getVoltage());
        d.setSignalStrength(v.getSignalStrength());
        d.setTemper(v.getTemper());
        d.setHumidity(v.getHumidity());
        d.setReportingInterval(v.getReportingInterval());
        d.setIntervalUnit(v.getIntervalUnit());
        return d;
    }

    @Override
    public R<Void> manualControl(MonitorManualControlBo bo) {
        List<PrHeatVo> heatVos = toPrHeatVoList(bo.getIds());
        if (heatVos == null) return R.fail("ids 包含非法数字");

        boolean switch1;
        Integer scale = null;
        String adjust = null;

        switch (bo.getControlType()) {
            case "open" -> switch1 = true;
            case "close" -> switch1 = false;
            case "adjust" -> {
                switch1 = true; // 调节时开阀
                scale = bo.getOpening();
                adjust = "manual";
            }
            default -> {
                log.warn("未知的控制类型: {}", bo.getControlType());
                return R.fail("未知的控制类型: " + bo.getControlType());
            }
        }

        boolean result = heatArchiveService.manualControl(heatVos, switch1, scale, adjust,
            bo.getOrgId(), null, null, null);
        return result ? R.ok() : R.fail("阀门控制指令发送失败");
    }

    @Override
    public R<Void> xunce(MonitorXunceBo bo) {
        List<PrHeatVo> heatVos = toPrHeatVoList(bo.getIds());
        if (heatVos == null) return R.fail("ids 包含非法数字");

        boolean result = heatArchiveService.xunce(heatVos, bo.getOrgId());
        return result ? R.ok() : R.fail("巡测指令发送失败");
    }

    @Override
    public R<Void> setValveGroup(MonitorSetValveGroupBo bo) {
        List<PrHeatVo> heatVos = toPrHeatVoList(bo.getIds());
        if (heatVos == null) return R.fail("ids 包含非法数字");

        // heatArchiveService.setValveGroupParam 内部做 Integer.valueOf(commandParam)，
        // 所以 commandParam 必须是纯数字字符串（组号/信道号）
        String commandParam;
        if ("channel".equals(bo.getMode()) && bo.getChannel() != null) {
            commandParam = String.valueOf(bo.getChannel());
        } else if (bo.getOpening() != null) {
            commandParam = String.valueOf(bo.getOpening());
        } else {
            return R.fail("缺少 channel 或 opening 参数");
        }

        boolean result = heatArchiveService.setValveGroupParam(heatVos, commandParam, bo.getOrgId());
        return result ? R.ok() : R.fail("参数设置指令发送失败");
    }

    @Override
    public Map<String, String> readOtherCode(MonitorOtherCodeReadBo bo) {
        // 1) 通过 meterNums 查阀门拿 houseId 映射
        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.in(PrHeatValveArchive::getMeterNum, bo.getMeterNums())
                 .eq(PrHeatValveArchive::getIsChanged, 0);
        List<PrHeatValveArchiveVo> valves = valveArchiveMapper.selectVoList(valveLqw);

        // meterNum → houseId
        Map<String, Long> meterToHouseId = valves.stream()
            .filter(v -> v.getHouseId() != null)
            .collect(Collectors.toMap(PrHeatValveArchiveVo::getMeterNum,
                PrHeatValveArchiveVo::getHouseId, (a, b) -> a));

        if (meterToHouseId.isEmpty()) {
            return bo.getMeterNums().stream()
                .collect(Collectors.toMap(m -> m, m -> ""));
        }

        // 2) 批量查 pr_house.other_code
        List<Long> houseIds = meterToHouseId.values().stream().distinct().toList();
        LambdaQueryWrapper<PrHouse> houseLqw = new LambdaQueryWrapper<>();
        houseLqw.in(PrHouse::getId, houseIds);
        Map<Long, String> houseOtherCode = houseMapper.selectVoList(houseLqw).stream()
            .collect(Collectors.toMap(PrHouseVo::getId,
                h -> h.getOtherCode() != null ? h.getOtherCode() : "",
                (a, b) -> a));

        // 3) 拼回 meterNum → otherCode
        return meterToHouseId.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                e -> houseOtherCode.getOrDefault(e.getValue(), "")));
    }

    @Override
    @Transactional
    public void writeOtherCode(MonitorOtherCodeWriteBo bo) {
        // 1) 通过 meterNums 查阀门拿 houseId
        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.in(PrHeatValveArchive::getMeterNum, bo.getMeterNums())
                 .eq(PrHeatValveArchive::getIsChanged, 0);
        List<PrHeatValveArchiveVo> valves = valveArchiveMapper.selectVoList(valveLqw);

        List<Long> houseIds = valves.stream()
            .map(PrHeatValveArchiveVo::getHouseId)
            .filter(id -> id != null)
            .distinct()
            .toList();

        if (houseIds.isEmpty()) return;

        // 2) 批量更新 other_code（BaseEntity.updateTime 由 MetaObjectHandler 自动填充）
        LambdaUpdateWrapper<PrHouse> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(PrHouse::getId, houseIds)
                     .set(PrHouse::getOtherCode, bo.getCode());
        houseMapper.update(null, updateWrapper);
    }

    @Override
    public List<ChangeHistoryVo> changeHistory(ChangeHistoryQueryBo bo) {
        // houseId + changeType 场景：走带 LIMIT 的高效查询
        if (bo.getHouseId() != null) {
            List<PrHouseLog> logs = houseLogMapper.selectByHouseIdAndType(
                bo.getHouseId(), bo.getChangeType());
            return toChangeHistoryVoList(logs);
        }

        // 无 houseId：按 scope 走全量查询（保留现有逻辑兼容性）
        List<PrHouseLog> logs;
        if ("unit".equals(bo.getScope())) {
            logs = houseLogMapper.selectUnitChangeData(bo.getChangeType());
        } else {
            logs = houseLogMapper.selectHouseChangeData(bo.getChangeType());
        }

        // 限制最多 50 条
        if (logs.size() > 50) {
            logs = logs.subList(0, 50);
        }

        return toChangeHistoryVoList(logs);
    }

    private List<ChangeHistoryVo> toChangeHistoryVoList(List<PrHouseLog> logs) {
        if (logs.isEmpty()) return Collections.emptyList();

        // 批量查 createByName（sys_user 在 master 库，SysUserMapper 有 @DS("master")）
        List<Long> createByIds = logs.stream()
            .map(PrHouseLog::getCreateBy)
            .filter(id -> id != null)
            .distinct()
            .toList();
        Map<Long, String> userNameMap;
        if (!createByIds.isEmpty()) {
            userNameMap = sysUserMapper.selectVoList(
                new LambdaQueryWrapper<org.sdkj.system.domain.SysUser>()
                    .in(org.sdkj.system.domain.SysUser::getUserId, createByIds)
                    .select(org.sdkj.system.domain.SysUser::getUserId, org.sdkj.system.domain.SysUser::getNickName)
            ).stream().collect(Collectors.toMap(SysUserVo::getUserId, SysUserVo::getNickName, (a, b) -> a));
        } else {
            userNameMap = Collections.emptyMap();
        }

        // 批量查 orgName（sys_organization 在租户库）
        List<String> orgIds = logs.stream()
            .map(PrHouseLog::getOrgId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        Map<String, String> orgNameMap = new HashMap<>();
        for (String oid : orgIds) {
            SysOrganization org = companyMapper.selectOrgById(oid);
            if (org != null) orgNameMap.put(oid, org.getName());
        }

        return logs.stream().map(l -> {
            ChangeHistoryVo vo = new ChangeHistoryVo();
            vo.setId(l.getId());
            vo.setHouseId(l.getHouseId());
            vo.setChangeType(l.getChangeType());
            vo.setChangeVal(l.getChangeVal());
            vo.setRemark(l.getRemark());
            vo.setCreateTime(l.getCreateTime());
            vo.setCreateByName(l.getCreateBy() != null ? userNameMap.get(l.getCreateBy()) : null);
            vo.setRoomNum(l.getRoomNum());
            vo.setOrgName(l.getOrgId() != null ? orgNameMap.get(l.getOrgId()) : null);
            vo.setBuildingName(l.getBuildingName());
            vo.setUnitCode(l.getUnitCode());
            return vo;
        }).toList();
    }

    @Override
    @Transactional
    public void specialHouse(SpecialHouseBo bo) {
        List<Long> houseIds = parseIds(bo.getIds());
        if (houseIds.isEmpty()) return;

        // 批量更新 is_special（XML mapper，绕过 @TableField(exist=false)）
        houseMapper.updateIsSpecialBatch(houseIds, bo.getFlag());

        // 写 pr_house_log
        for (Long houseId : houseIds) {
            appendHouseLog(houseId, "special", bo.getFlag(), bo.getOrgId(),
                "特殊户标记变更");
        }
    }

    @Override
    @Transactional
    public void stopSupply(StopSupplyBo bo) {
        List<Long> houseIds = parseIds(bo.getIds());
        if (houseIds.isEmpty()) return;

        // 批量更新 pay_status（XML mapper，绕过 @TableField(exist=false)）
        houseMapper.updatePayStatusBatch(houseIds, bo.getFlag());

        // 写 pr_house_log
        for (Long houseId : houseIds) {
            appendHouseLog(houseId, "payment", bo.getFlag(), bo.getOrgId(),
                "缴费/停供状态变更");
        }
    }

    /**
     * 追加房屋变更日志
     */
    private void appendHouseLog(Long houseId, String changeType, Integer changeVal,
                                String orgId, String remark) {
        PrHouseLog logEntry = new PrHouseLog();
        logEntry.setHouseId(houseId);
        logEntry.setChangeType(changeType);
        logEntry.setChangeVal(changeVal);
        logEntry.setOrgId(orgId);
        logEntry.setRemark(remark);
        houseLogMapper.insert(logEntry);
    }

    /**
     * 将 String ID 列表安全转为 Long 列表，过滤无效值
     */
    private List<Long> parseIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return ids.stream()
            .map(id -> {
                try {
                    return Long.valueOf(id);
                } catch (NumberFormatException e) {
                    log.warn("无效的 ID 格式: {}", id);
                    return null;
                }
            })
            .filter(id -> id != null)
            .toList();
    }

    /**
     * 将 String ID 列表转为 PrHeatVo 列表（委托 heat-archive 用）
     * @return null 表示存在非法 ID
     */
    private List<PrHeatVo> toPrHeatVoList(List<String> ids) {
        List<PrHeatVo> result = new ArrayList<>(ids.size());
        for (String id : ids) {
            try {
                PrHeatVo v = new PrHeatVo();
                v.setId(Long.valueOf(id));
                result.add(v);
            } catch (NumberFormatException e) {
                log.warn("无效的 ID 格式: {}", id);
                return null;
            }
        }
        return result;
    }
}
