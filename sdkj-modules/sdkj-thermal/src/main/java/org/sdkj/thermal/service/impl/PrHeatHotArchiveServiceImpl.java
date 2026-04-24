package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 房屋热量表配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatHotArchiveServiceImpl extends ServiceImpl<PrHeatHotArchiveMapper, PrHeatHotArchive> implements IPrHeatHotArchiveService {

    private final PrHeatHotArchiveMapper baseMapper;

    @Override
    public PrHeatHotArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatHotArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                             String unit, String search, String parentId,
                                                             PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatHotArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatHotArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatHotArchive::getMeterNum, search.trim())
                .or().like(PrHeatHotArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatHotArchive::getCreateTime);
        Page<PrHeatHotArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatHotArchive entity) {
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
