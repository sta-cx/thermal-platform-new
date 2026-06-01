package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrUnit;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrBuildingVo;
import org.sdkj.thermal.mapper.PrBuildingMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrUnitMapper;
import org.sdkj.thermal.service.IPrBuildingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 楼宇信息 Service 实现
 * 迁移自旧系统 PrBuildingServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrBuildingServiceImpl extends ServiceImpl<PrBuildingMapper, PrBuilding> implements IPrBuildingService {

    private final PrBuildingMapper baseMapper;
    private final PrUnitMapper unitMapper;
    private final PrHouseMapper houseMapper;
    private final PrCompanyMapper prCompanyMapper;

    @Override
    public PrBuildingVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrBuildingVo> selectPageList(String search, String used, String orgId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrBuilding> lqw = new LambdaQueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.like(PrBuilding::getName, search.trim())
                .or().like(PrBuilding::getCode, search.trim()));
        }
        if (used != null && !used.trim().isEmpty()) {
            lqw.eq(PrBuilding::getUsed, used);
        }
        if (orgId != null && !orgId.trim().isEmpty()) {
            lqw.eq(PrBuilding::getOrgId, orgId);
        }
        lqw.orderByAsc(PrBuilding::getSeq).orderByDesc(PrBuilding::getCreateTime);
        Page<PrBuildingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillOrgName(result.getRecords());
        return TableDataInfo.build(result);
    }

    @Override
    public boolean validateName(String name, String id) {
        LambdaQueryWrapper<PrBuilding> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrBuilding::getName, name);
        if (id != null && !id.trim().isEmpty()) {
            lqw.ne(PrBuilding::getId, id);
        }
        return baseMapper.selectCount(lqw) > 0;
    }

    @Override
    public List<PrBuildingVo> selectByOrgId(String orgId) {
        LambdaQueryWrapper<PrBuilding> lqw = new LambdaQueryWrapper<>();
        if (orgId != null && !orgId.trim().isEmpty()) {
            lqw.eq(PrBuilding::getOrgId, orgId);
        }
        lqw.orderByAsc(PrBuilding::getSeq);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<String> selectUnitCodesByBuildingId(String buildingId) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrUnit::getBuildingId, buildingId);
        lqw.select(PrUnit::getCode);
        lqw.orderByAsc(PrUnit::getSeq);
        return unitMapper.selectList(lqw).stream()
            .map(PrUnit::getCode)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> selectRoomNumsByUnitCode(String unitCode) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHouse::getUnitCode, unitCode);
        lqw.select(PrHouse::getRoomNum);
        lqw.orderByAsc(PrHouse::getRoomNum);
        return houseMapper.selectList(lqw).stream()
            .map(PrHouse::getRoomNum)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> selectHouseUnitCodesByBuildingId(String buildingId) {
        if (buildingId == null || buildingId.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return houseMapper.selectList(
            new LambdaQueryWrapper<PrHouse>()
                .eq(PrHouse::getBuildingId, buildingId)
                .isNotNull(PrHouse::getUnitCode)
                .ne(PrHouse::getUnitCode, "")
                .select(PrHouse::getUnitCode)
        ).stream()
            .map(PrHouse::getUnitCode)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public List<PrBuildingVo> selectByStationId(String stationId) {
        LambdaQueryWrapper<PrBuilding> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrBuilding::getStationId, stationId);
        lqw.orderByAsc(PrBuilding::getSeq);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrBuilding entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    private void fillOrgName(List<PrBuildingVo> list) {
        if (list == null || list.isEmpty()) return;
        Set<String> orgIds = list.stream()
            .map(PrBuildingVo::getOrgId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (orgIds.isEmpty()) return;
        Map<String, String> orgNameMap = prCompanyMapper.selectOrgByIds(orgIds).stream()
            .collect(Collectors.toMap(SysOrganization::getId, SysOrganization::getName, (a, b) -> a));
        list.forEach(vo -> vo.setOrgName(orgNameMap.getOrDefault(vo.getOrgId(), "")));
    }

}
