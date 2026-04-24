package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatDtuArchive;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;
import org.sdkj.thermal.mapper.PrHeatDtuArchiveMapper;
import org.sdkj.thermal.service.IPrHeatDtuArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DTU采集器配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatDtuArchiveServiceImpl extends ServiceImpl<PrHeatDtuArchiveMapper, PrHeatDtuArchive> implements IPrHeatDtuArchiveService {

    private final PrHeatDtuArchiveMapper baseMapper;

    @Override
    public PrHeatDtuArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatDtuArchiveVo> selectPageList(String companyId, String orgId, String search,
                                                             String status, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatDtuArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatDtuArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatDtuArchive::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.like(PrHeatDtuArchive::getDtuNum, search.trim());
        }
        lqw.eq(StringUtils.isNotBlank(status), PrHeatDtuArchive::getStatus, status);
        // DTU has no isChanged field, no filter needed
        lqw.orderByDesc(PrHeatDtuArchive::getCreateTime);
        Page<PrHeatDtuArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatDtuArchive entity) {
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
