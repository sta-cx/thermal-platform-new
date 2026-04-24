package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatUnitValveArchiveMapper;
import org.sdkj.thermal.service.IPrHeatUnitValveArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单元阀门配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatUnitValveArchiveServiceImpl extends ServiceImpl<PrHeatUnitValveArchiveMapper, PrHeatUnitValveArchive> implements IPrHeatUnitValveArchiveService {

    private final PrHeatUnitValveArchiveMapper baseMapper;

    @Override
    public PrHeatUnitValveArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatUnitValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                                   String unit, String search, String parentId,
                                                                   PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatUnitValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitValveArchive::getOrgId, orgId);
        // parentId maps to unitId for this entity (uses unitId, not houseId)
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatUnitValveArchive::getUnitId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatUnitValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatUnitValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitValveArchive::getCreateTime);
        Page<PrHeatUnitValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatUnitValveArchive entity) {
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
