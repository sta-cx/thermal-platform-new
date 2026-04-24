package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.mapper.PrHeatUnitHotArchiveMapper;
import org.sdkj.thermal.service.IPrHeatUnitHotArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单元热表配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatUnitHotArchiveServiceImpl extends ServiceImpl<PrHeatUnitHotArchiveMapper, PrHeatUnitHotArchive> implements IPrHeatUnitHotArchiveService {

    private final PrHeatUnitHotArchiveMapper baseMapper;

    @Override
    public PrHeatUnitHotArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatUnitHotArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                                 String unit, String search, String parentId,
                                                                 PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatUnitHotArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitHotArchive::getOrgId, orgId);
        // parentId maps to unitId for this entity (uses unitId, not houseId)
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatUnitHotArchive::getUnitId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatUnitHotArchive::getMeterNum, search.trim())
                .or().like(PrHeatUnitHotArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitHotArchive::getCreateTime);
        Page<PrHeatUnitHotArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatUnitHotArchive entity) {
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
