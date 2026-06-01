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
import org.sdkj.thermal.domain.PrHeatStation;
import org.sdkj.thermal.domain.PrHeatStationPartition;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrHouseLog;
import org.sdkj.thermal.domain.PrUnit;
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
import org.sdkj.thermal.mapper.PrHeatStationMapper;
import org.sdkj.thermal.mapper.PrHeatStationPartitionMapper;
import org.sdkj.thermal.mapper.PrHouseLogMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrUnitMapper;
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
import java.util.Objects;
import java.util.Set;
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
    private final PrHeatStationPartitionMapper stationPartitionMapper;
    private final PrHeatStationMapper stationMapper;
    private final PrCompanyMapper companyMapper;
    private final PrHouseLogMapper houseLogMapper;
    private final PrUnitMapper unitMapper;
    private final IPrHeatArchiveService heatArchiveService;
    private final SysUserMapper sysUserMapper;
    private final ObjectMapper objectMapper;
    private final org.sdkj.thermal.service.support.HeatValveVoEnricher heatValveVoEnricher;

    @Override
    public TableDataInfo<PrHeatArchiveVo> heatList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, kw)
                .or().like(PrHeatArchive::getMeterArcName, kw));
        }
        // 档案侧筛选
        lqw.eq(StringUtils.isNotBlank(bo.getValveStatus()), PrHeatArchive::getValveStatus, bo.getValveStatus());
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        // 房屋侧筛选 → house_id IN
        List<Long> houseIds = resolveFilteredHouseIds(bo);
        if (houseIds != null) {
            if (houseIds.isEmpty()) return TableDataInfo.build(new Page<>());
            lqw.in(PrHeatArchive::getHouseId, houseIds);
        }
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
        // 档案侧筛选
        lqw.eq(StringUtils.isNotBlank(bo.getValveStatus()), PrHeatValveArchive::getValveStatus, bo.getValveStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getChanNum()), PrHeatValveArchive::getChanNum, bo.getChanNum());
        applyVoltageFilter(lqw, bo);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        // 房屋侧筛选 → house_id IN
        List<Long> houseIds = resolveFilteredHouseIds(bo);
        if (houseIds != null) {
            if (houseIds.isEmpty()) return TableDataInfo.build(new Page<>());
            lqw.in(PrHeatValveArchive::getHouseId, houseIds);
        }
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        Page<PrHeatValveArchiveVo> result = valveArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        heatValveVoEnricher.enrich(result.getRecords());
        return TableDataInfo.build(result);
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

    private String computeScopeStatus(Date valveTime) {
        if (valveTime == null) return "3"; // 无通讯记录→通讯中断
        long hours = (System.currentTimeMillis() - valveTime.getTime()) / (1000 * 60 * 60);
        if (hours > 24) return "3"; // 超过24小时→通讯中断
        return "1"; // 正常
    }

    /**
     * 按房屋侧筛选条件解析 house_id 集合。
     * 两步解析（不 JOIN）：先查 PrHouse 拿 ID 集合，再 IN 进档案查询。
     * PrHouseMapper @OrgPermission 会自动注入 org_id 过滤。
     *
     * @return null = 无房屋侧筛选（调用方跳过 IN）；
     *         非 null = 命中的 house_id（可能为空，空→列表应返回空）
     */
    private List<Long> resolveFilteredHouseIds(MonitorBo bo) {
        boolean hasHouseFilter = StringUtils.isNotBlank(bo.getBuildingId())
            || StringUtils.isNotBlank(bo.getUnit())
            || StringUtils.isNotBlank(bo.getFloor())
            || StringUtils.isNotBlank(bo.getIsCharged())
            || StringUtils.isNotBlank(bo.getHouseType())
            || StringUtils.isNotBlank(bo.getSiteType())
            || StringUtils.isNotBlank(bo.getSpecialType())
            || StringUtils.isNotBlank(bo.getStationId())
            || StringUtils.isNotBlank(bo.getPartitionId());
        if (!hasHouseFilter) return null;

        LambdaQueryWrapper<PrHouse> hq = new LambdaQueryWrapper<>();
        hq.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHouse::getOrgId, bo.getOrgId())
          .eq(StringUtils.isNotBlank(bo.getBuildingId()), PrHouse::getBuildingId, bo.getBuildingId())
          .eq(StringUtils.isNotBlank(bo.getUnit()), PrHouse::getUnitCode, bo.getUnit());

        // 前端发 String，DB 列为 tinyint/int → 安全转换
        Integer floorVal = toInteger(bo.getFloor());
        hq.eq(floorVal != null, PrHouse::getFloor, floorVal);
        Integer isChargedVal = toInteger(bo.getIsCharged());
        hq.eq(isChargedVal != null, PrHouse::getIsCharged, isChargedVal);
        // houseType="分组"=缴费位置属性(1孤岛/2上不供/3下不供/4正常) → pay_sit_type
        // ⚠️ 不是 station_type！station_type 存的是分区id(见下方分区筛选)
        Integer paySitTypeVal = toInteger(bo.getHouseType());
        hq.eq(paySitTypeVal != null, PrHouse::getPaySitType, paySitTypeVal);
        hq.eq(StringUtils.isNotBlank(bo.getSiteType()), PrHouse::getSiteType, bo.getSiteType());
        Integer isSpecialVal = toInteger(bo.getSpecialType());
        hq.eq(isSpecialVal != null, PrHouse::getIsSpecial, isSpecialVal);

        // 分区筛选：pr_house.station_type 存的就是分区id(pr_heat_station_partition.id)
        hq.eq(StringUtils.isNotBlank(bo.getPartitionId()), PrHouse::getStationType, bo.getPartitionId());

        // 换热站筛选：先查该站下所有分区id，再 station_type IN
        if (StringUtils.isNotBlank(bo.getStationId())) {
            List<String> partitionIds = stationPartitionMapper.selectList(
                new LambdaQueryWrapper<PrHeatStationPartition>()
                    .eq(PrHeatStationPartition::getStationId, bo.getStationId())
                    .select(PrHeatStationPartition::getId)
            ).stream().map(p -> String.valueOf(p.getId())).toList();
            if (partitionIds.isEmpty()) return Collections.emptyList(); // 该站无分区 → 无房屋
            hq.in(PrHouse::getStationType, partitionIds);
        }

        hq.select(PrHouse::getId);
        return houseMapper.selectList(hq).stream().map(PrHouse::getId).toList();
    }

    /**
     * 单元侧筛选 → unit_id 集合（与 resolveFilteredHouseIds 平行）。
     * pr_unit.station_id 存的是分区id(Long，老系统真实数据已证)，building_id 楼宇id。
     * 单元仅支持 楼宇/分区/换热站 维度；房屋特有的缴费/分组/特殊户/楼层不适用单元。
     *
     * @return null = 无单元侧筛选（跳过 IN）；非 null = 命中的 unit_id（空 → 列表返回空）
     */
    private List<Long> resolveFilteredUnitIds(MonitorBo bo) {
        boolean hasUnitFilter = StringUtils.isNotBlank(bo.getBuildingId())
            || StringUtils.isNotBlank(bo.getStationId())
            || StringUtils.isNotBlank(bo.getPartitionId());
        if (!hasUnitFilter) return null;

        LambdaQueryWrapper<PrUnit> uq = new LambdaQueryWrapper<>();
        uq.eq(StringUtils.isNotBlank(bo.getOrgId()), PrUnit::getOrgId, bo.getOrgId());

        // 楼宇：pr_unit.building_id 是 bigint，前端发 String → 转 Long
        Long buildingId = parseLongOrNull(bo.getBuildingId());
        uq.eq(buildingId != null, PrUnit::getBuildingId, buildingId);

        // 分区筛选：pr_unit.station_id 存的就是分区id（与户级 station_type 同语义，但此处列为 Long）
        Long partitionId = parseLongOrNull(bo.getPartitionId());
        uq.eq(partitionId != null, PrUnit::getStationId, partitionId);

        // 换热站筛选：先查该站所有分区id，再 station_id IN
        if (StringUtils.isNotBlank(bo.getStationId())) {
            List<Long> partitionIds = stationPartitionMapper.selectList(
                new LambdaQueryWrapper<PrHeatStationPartition>()
                    .eq(PrHeatStationPartition::getStationId, bo.getStationId())
                    .select(PrHeatStationPartition::getId)
            ).stream().map(PrHeatStationPartition::getId).toList();
            if (partitionIds.isEmpty()) return Collections.emptyList(); // 该站无分区 → 无单元
            uq.in(PrUnit::getStationId, partitionIds);
        }

        uq.select(PrUnit::getId);
        return unitMapper.selectList(uq).stream().map(PrUnit::getId).toList();
    }

    /**
     * 电压区间筛选。
     * voltage 列在 pr_heat_valve_archive 为 varchar(20)，需 CAST 为 DECIMAL 比较。
     * 若实际数据非规范数值则本筛选无效（待数据验证后决定是否降级）。
     */
    private void applyVoltageFilter(LambdaQueryWrapper<?> lqw, MonitorBo bo) {
        if (StringUtils.isNotBlank(bo.getVoltageOp()) && bo.getVoltageValue() != null) {
            BigDecimal val = bo.getVoltageValue();
            switch (bo.getVoltageOp()) {
                // voltage 列为 varchar，真实值纯数值且带小数(老系统实测 0~5.81)；
                // 必须 DECIMAL(10,2)——默认 CAST AS DECIMAL 是 DECIMAL(10,0) 会截断小数致区间比较失真
                case "1" -> lqw.apply("CAST(voltage AS DECIMAL(10,2)) >= {0}", val);
                case "2" -> lqw.apply("CAST(voltage AS DECIMAL(10,2)) <= {0}", val);
                default -> log.debug("未知的电压比较符: {}", bo.getVoltageOp());
            }
        }
    }

    /** 前端 String → Integer 安全转换 */
    private Integer toInteger(String val) {
        if (StringUtils.isBlank(val)) return null;
        try { return Integer.valueOf(val); }
        catch (NumberFormatException e) { return null; }
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
        // 单元侧筛选 → unit_id IN
        List<Long> unitIds = resolveFilteredUnitIds(bo);
        if (unitIds != null) {
            if (unitIds.isEmpty()) return TableDataInfo.build(new Page<>());
            lqw.in(PrHeatUnitHotArchive::getUnitId, unitIds);
        }
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
        // 单元侧筛选 → unit_id IN
        List<Long> unitIds = resolveFilteredUnitIds(bo);
        if (unitIds != null) {
            if (unitIds.isEmpty()) return TableDataInfo.build(new Page<>());
            lqw.in(PrHeatUnitValveArchive::getUnitId, unitIds);
        }
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
        // 档案侧筛选
        lqw.eq(StringUtils.isNotBlank(bo.getValveStatus()), PrHeatTempArchive::getValveStatus, bo.getValveStatus());
        applyVoltageFilter(lqw, bo);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        // 房屋侧筛选 → house_id IN
        List<Long> houseIds = resolveFilteredHouseIds(bo);
        if (houseIds != null) {
            if (houseIds.isEmpty()) return TableDataInfo.build(new Page<>());
            lqw.in(PrHeatTempArchive::getHouseId, houseIds);
        }
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        Page<PrHeatTempArchiveVo> result = tempArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public MonitorAggregateVo aggregate(MonitorBo bo) {
        MonitorAggregateVo vo = new MonitorAggregateVo();

        // 房屋侧筛选 → house_id IN，与列表口径一致（楼宇/单元/缴费/楼层/分组/位置/特殊户）。
        // 仅跟随"房屋侧"筛选，不跟随档案侧设备状态(valveStatus/voltage)——
        // 否则按"开"状态筛选后在线率恒为 100%，会扭曲汇总统计。
        List<Long> houseIds = resolveFilteredHouseIds(bo);
        if (houseIds != null && houseIds.isEmpty()) {
            vo.setTotalCount(0);
            vo.setOnlineCount(0);
            vo.setOfflineCount(0);
            return vo;
        }

        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId());
        valveLqw.eq(PrHeatValveArchive::getIsChanged, 0);
        if (houseIds != null) {
            valveLqw.in(PrHeatValveArchive::getHouseId, houseIds);
        }
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
            buildSiteGroups(vo, bo.getOrgId(), valves, houseIds);
        }
        return vo;
    }

    private void buildSiteGroups(MonitorAggregateVo vo, String orgId, List<PrHeatValveArchive> valves, List<Long> houseIds) {
        LambdaQueryWrapper<PrHouse> houseLqw = new LambdaQueryWrapper<>();
        houseLqw.eq(PrHouse::getOrgId, orgId);
        if (houseIds != null) {
            houseLqw.in(PrHouse::getId, houseIds);
        }
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
        // 所以 commandParam 必须是纯数字字符串（组号/信道号）。
        // 注意：setting 模式的 targetTemp/period/flowRate 暂无对应下发通道（委托方法仅收单个数值），
        //      当前只下发 opening；完整 5 参数下发待真实 IoT 协议接入（AI Phase 3）后补。
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
        // 1) 通过 meterNums 查阀门拿 houseId 映射（限定 orgId，避免租户内重号跨小区误读）
        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.in(PrHeatValveArchive::getMeterNum, bo.getMeterNums())
                 .eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId())
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
        // 1) 通过 meterNums 查阀门拿 houseId（限定 orgId，避免租户内重号跨小区误写）
        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.in(PrHeatValveArchive::getMeterNum, bo.getMeterNums())
                 .eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId())
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
        // houseId 场景：单表查 pr_house_log（@OrgPermission 自动注入 org_id 过滤，单表无 JOIN 列歧义），
        // 房屋字段（房号/楼宇/单元）由 toChangeHistoryVoList 按 houseId 批量查 pr_house 组装（遵循"跨表不 JOIN"硬规则）
        if (bo.getHouseId() != null) {
            LambdaQueryWrapper<PrHouseLog> lqw = new LambdaQueryWrapper<>();
            lqw.eq(StringUtils.isNotBlank(bo.getChangeType()), PrHouseLog::getChangeType, bo.getChangeType())
               .eq(PrHouseLog::getHouseId, bo.getHouseId())
               .orderByDesc(PrHouseLog::getCreateTime)
               .last("LIMIT 50");
            return toChangeHistoryVoList(houseLogMapper.selectList(lqw));
        }

        // 无 houseId：按 scope 走全量查询（前端当前总是传 houseId，此路径基本不触发，保留兼容）
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

        // 房屋字段补全：houseId 单表查询路径下 log 不含房屋信息，按 houseId 批量查 pr_house 组装（不 JOIN）
        List<Long> needHouseIds = logs.stream()
            .filter(l -> l.getRoomNum() == null && l.getHouseId() != null)
            .map(PrHouseLog::getHouseId)
            .distinct()
            .toList();
        Map<Long, PrHouseVo> houseMap;
        if (needHouseIds.isEmpty()) {
            houseMap = Collections.emptyMap();
        } else {
            houseMap = houseMapper.selectVoList(
                new LambdaQueryWrapper<PrHouse>().in(PrHouse::getId, needHouseIds)
            ).stream().collect(Collectors.toMap(PrHouseVo::getId, h -> h, (a, b) -> a));
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
            PrHouseVo h = l.getHouseId() != null ? houseMap.get(l.getHouseId()) : null;
            vo.setRoomNum(l.getRoomNum() != null ? l.getRoomNum() : (h != null ? h.getRoomNum() : null));
            vo.setOrgName(l.getOrgId() != null ? orgNameMap.get(l.getOrgId()) : null);
            vo.setBuildingName(l.getBuildingName() != null ? l.getBuildingName() : (h != null ? h.getBuildingName() : null));
            vo.setUnitCode(l.getUnitCode() != null ? l.getUnitCode() : (h != null ? h.getUnitCode() : null));
            return vo;
        }).toList();
    }

    @Override
    @Transactional
    public void specialHouse(SpecialHouseBo bo) {
        List<Long> houseIds = parseIds(bo.getIds());
        if (houseIds.isEmpty()) return;

        // 批量更新 is_special（XML mapper 批量操作）
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
