package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatCommandUnitValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatCommandUnitValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatCommandUnitValveArchiveMapper;
import org.sdkj.thermal.service.IPrHeatCommandUnitValveArchiveService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单元控制阀门配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatCommandUnitValveArchiveServiceImpl extends OrgScopedServiceImpl<PrHeatCommandUnitValveArchiveMapper, PrHeatCommandUnitValveArchive> implements IPrHeatCommandUnitValveArchiveService {

    private final PrHeatCommandUnitValveArchiveMapper baseMapper;

    @Override
    public PrHeatCommandUnitValveArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatCommandUnitValveArchiveVo> selectPageList(String orgId, String buildingId,
                                                                          String unit, String search, String parentId,
                                                                          PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatCommandUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatCommandUnitValveArchive::getOrgId, orgId);
        // parentId maps to unitId for this entity (uses unitId, not houseId)
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatCommandUnitValveArchive::getUnitId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatCommandUnitValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatCommandUnitValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatCommandUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatCommandUnitValveArchive::getCreateTime);
        Page<PrHeatCommandUnitValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatCommandUnitValveArchive entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> ids) {
        return super.removeByIds(ids);
    }
}
