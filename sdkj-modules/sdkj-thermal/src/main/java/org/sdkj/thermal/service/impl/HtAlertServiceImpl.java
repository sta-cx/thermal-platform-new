package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.HtAlertVo;
import org.sdkj.thermal.mapper.HtAlertMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IHtAlertService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 报警记录 Service 实现
 * 迁移自旧系统 HtAlertServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtAlertServiceImpl extends OrgScopedServiceImpl<HtAlertMapper, HtAlert> implements IHtAlertService {

    private final HtAlertMapper baseMapper;
    private final PrHouseMapper houseMapper;
    private final PrHeatValveArchiveMapper valveArchiveMapper;
    private final PrCompanyMapper companyMapper;

    @Override
    public TableDataInfo<HtAlertVo> selectPageList(LambdaQueryWrapper<HtAlert> lqw, PageQuery pageQuery) {
        Page<HtAlertVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        enrich(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 回填 orgName（小区名）/ buildingName（楼栋名）/ roomNum（房号）/ meterNum（表号）。
     * 三步分表查询 + Stream 组装，禁 JOIN（复用 PrWriteCardLogServiceImpl 同模式）。
     * 报警表只存 org_id/house_id/meter_id（雪花 ID），前端展示需要可读名称。
     */
    private void enrich(List<HtAlertVo> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        // 小区名：org_id → sys_organization.name（小区数量少，沿用单查模式）
        Map<String, String> orgNameMap = new HashMap<>();
        rows.stream().map(HtAlertVo::getOrgId)
            .filter(StringUtils::isNotBlank).distinct()
            .forEach(oid -> {
                SysOrganization org = companyMapper.selectOrgById(oid);
                if (org != null) {
                    orgNameMap.put(oid, org.getName());
                }
            });
        // 楼栋名 + 房号：house_id → pr_house
        Set<Long> houseIds = rows.stream().map(HtAlertVo::getHouseId)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, PrHouse> houseMap = houseIds.isEmpty() ? Collections.emptyMap()
            : houseMapper.selectList(new LambdaQueryWrapper<PrHouse>()
                .in(PrHouse::getId, houseIds)
                .select(PrHouse::getId, PrHouse::getBuildingName, PrHouse::getRoomNum))
                .stream().collect(Collectors.toMap(PrHouse::getId, h -> h, (a, b) -> a));
        // 表号：meter_id → pr_heat_valve_archive.meter_num
        Set<Long> meterIds = rows.stream().map(HtAlertVo::getMeterId)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> meterNumMap = meterIds.isEmpty() ? Collections.emptyMap()
            : valveArchiveMapper.selectList(new LambdaQueryWrapper<PrHeatValveArchive>()
                .in(PrHeatValveArchive::getId, meterIds)
                .select(PrHeatValveArchive::getId, PrHeatValveArchive::getMeterNum))
                .stream().collect(Collectors.toMap(PrHeatValveArchive::getId, PrHeatValveArchive::getMeterNum, (a, b) -> a));

        for (HtAlertVo v : rows) {
            v.setOrgName(orgNameMap.get(v.getOrgId()));
            PrHouse h = v.getHouseId() == null ? null : houseMap.get(v.getHouseId());
            if (h != null) {
                v.setBuildingName(h.getBuildingName());
                v.setRoomNum(h.getRoomNum());
            }
            if (v.getMeterId() != null) {
                v.setMeterNum(meterNumMap.get(v.getMeterId()));
            }
        }
    }

    @Override
    public List<HtAlertVo> selectAbnormalAlarmList(String meterId) {
        return baseMapper.selectAbnormalAlarmList(meterId);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount() {
        return baseMapper.selectTypeCount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsertAlerts(List<HtAlert> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        return saveBatch(list);
    }

    @Override
    public List<Map<String, Object>> selectTypeCountDtu() {
        return baseMapper.selectTypeCountDtu();
    }

}
