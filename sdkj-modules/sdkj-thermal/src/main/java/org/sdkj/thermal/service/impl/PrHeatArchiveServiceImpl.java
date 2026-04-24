package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.mapper.PrHeatArchiveMapper;
import org.sdkj.thermal.service.IPrHeatArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房屋热表配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatArchiveServiceImpl extends ServiceImpl<PrHeatArchiveMapper, PrHeatArchive> implements IPrHeatArchiveService {

    private final PrHeatArchiveMapper baseMapper;

    @Override
    public PrHeatArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                          String unitCode, String search, String archiveId,
                                                          PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        // buildingId and unitCode are not direct fields on PrHeatArchive;
        // they are used for frontend filtering and may be resolved through house/building relations
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(StringUtils.isNotBlank(archiveId), PrHeatArchive::getArchiveId, archiveId);
        // isDeleted is handled by @TableLogic automatically, no need to filter
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<PrHeatArchiveVo> queryCompanyHeat(String companyId) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatArchive::getCompanyId, companyId);
        // isDeleted handled by @TableLogic
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatArchive entity) {
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
