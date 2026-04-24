package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 户间阀门配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatValveArchiveServiceImpl extends ServiceImpl<PrHeatValveArchiveMapper, PrHeatValveArchive> implements IPrHeatValveArchiveService {

    private final PrHeatValveArchiveMapper baseMapper;

    @Override
    public PrHeatValveArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                               String unit, String search, String parentId,
                                                               PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatValveArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        Page<PrHeatValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatValveArchive entity) {
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
