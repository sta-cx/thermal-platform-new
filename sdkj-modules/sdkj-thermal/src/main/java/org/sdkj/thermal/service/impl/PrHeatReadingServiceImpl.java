package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatReading;
import org.sdkj.thermal.domain.vo.PrHeatReadingVo;
import org.sdkj.thermal.mapper.PrHeatReadingMapper;
import org.sdkj.thermal.service.IPrHeatReadingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class PrHeatReadingServiceImpl extends ServiceImpl<PrHeatReadingMapper, PrHeatReading> implements IPrHeatReadingService {

    private final PrHeatReadingMapper baseMapper;

    @Override
    public PrHeatReadingVo selectById(Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatReadingVo> selectPageList(String companyId, String orgId, String buildingId,
                                                          String unitCode, String meterArcCode, String search,
                                                          String startTime, String endTime, String type,
                                                          String valveType, String parentId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatReading> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatReading::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatReading::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(meterArcCode), PrHeatReading::getMeterArcCode, meterArcCode);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatReading::getMeterNum, search.trim())
                .or().like(PrHeatReading::getMeterArcCode, search.trim()));
        }
        lqw.ge(StringUtils.isNotBlank(startTime), PrHeatReading::getReadTime, startTime);
        lqw.le(StringUtils.isNotBlank(endTime), PrHeatReading::getReadTime, endTime);
        lqw.orderByDesc(PrHeatReading::getReadTime);
        Page<PrHeatReadingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatReadingVo> selectPageListTrend(String companyId, String orgId, String startTime,
                                                               String endTime, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatReading> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatReading::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatReading::getOrgId, orgId);
        lqw.ge(StringUtils.isNotBlank(startTime), PrHeatReading::getReadTime, startTime);
        lqw.le(StringUtils.isNotBlank(endTime), PrHeatReading::getReadTime, endTime);
        lqw.orderByDesc(PrHeatReading::getReadTime);
        Page<PrHeatReadingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatReading entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
