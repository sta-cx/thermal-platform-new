package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.mapper.PrHeatTempArchiveMapper;
import org.sdkj.thermal.service.IPrHeatTempArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 温采器配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatTempArchiveServiceImpl extends ServiceImpl<PrHeatTempArchiveMapper, PrHeatTempArchive> implements IPrHeatTempArchiveService {

    private final PrHeatTempArchiveMapper baseMapper;

    @Override
    public PrHeatTempArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatTempArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                              String unit, String search, String parentId,
                                                              PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatTempArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatTempArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatTempArchive::getMeterNum, search.trim())
                .or().like(PrHeatTempArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        Page<PrHeatTempArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatTempArchive entity) {
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
