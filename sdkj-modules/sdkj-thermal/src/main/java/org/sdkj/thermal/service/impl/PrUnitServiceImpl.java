package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.PrUnit;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrUnitVo;
import org.sdkj.thermal.mapper.PrBuildingMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrUnitMapper;
import org.sdkj.thermal.service.IPrUnitService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 单元信息 Service 实现
 * 迁移自旧系统 PrUnitServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrUnitServiceImpl extends OrgScopedServiceImpl<PrUnitMapper, PrUnit> implements IPrUnitService {

    private final PrUnitMapper baseMapper;
    private final PrBuildingMapper buildingMapper;
    private final PrCompanyMapper prCompanyMapper;

    @Override
    public PrUnitVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrUnitVo> selectPageList(String search, String buildingId, String orgId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.like(PrUnit::getName, search.trim())
                .or().like(PrUnit::getCode, search.trim()));
        }
        if (buildingId != null && !buildingId.trim().isEmpty()) {
            lqw.eq(PrUnit::getBuildingId, buildingId);
        }
        if (orgId != null && !orgId.trim().isEmpty()) {
            lqw.eq(PrUnit::getOrgId, orgId);
        }
        lqw.orderByAsc(PrUnit::getSeq).orderByDesc(PrUnit::getCreateTime);
        Page<PrUnitVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillAssociatedNames(result.getRecords());
        return TableDataInfo.build(result);
    }

    @Override
    public boolean validateName(String name, String id) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrUnit::getName, name);
        if (id != null && !id.trim().isEmpty()) {
            lqw.ne(PrUnit::getId, id);
        }
        return baseMapper.selectCount(lqw) > 0;
    }

    @Override
    public List<PrUnitVo> selectByBuildingId(String buildingId) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrUnit::getBuildingId, buildingId);
        lqw.orderByAsc(PrUnit::getSeq);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrUnit entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    private void fillAssociatedNames(List<PrUnitVo> list) {
        if (list == null || list.isEmpty()) return;

        Set<Long> buildingIds = list.stream()
            .map(PrUnitVo::getBuildingId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (!buildingIds.isEmpty()) {
            Map<Long, String> buildingNameMap = buildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(PrBuilding::getId,
                    b -> b.getName() != null ? b.getName() : "", (a, b) -> a));
            list.forEach(vo -> vo.setBuildingName(buildingNameMap.getOrDefault(vo.getBuildingId(), "")));
        }

        Set<String> orgIds = list.stream()
            .map(PrUnitVo::getOrgId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (!orgIds.isEmpty()) {
            Map<String, String> orgNameMap = prCompanyMapper.selectOrgByIds(orgIds).stream()
                .collect(Collectors.toMap(SysOrganization::getId, SysOrganization::getName, (a, b) -> a));
            list.forEach(vo -> vo.setOrgName(orgNameMap.getOrDefault(vo.getOrgId(), "")));
        }
    }

}
