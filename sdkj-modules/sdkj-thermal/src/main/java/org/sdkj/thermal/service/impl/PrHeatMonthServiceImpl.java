package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatMonth;
import org.sdkj.thermal.domain.vo.PrHeatMonthVo;
import org.sdkj.thermal.mapper.PrHeatMonthMapper;
import org.sdkj.thermal.service.IPrHeatMonthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * 热表月记录 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrHeatMonthServiceImpl extends ServiceImpl<PrHeatMonthMapper, PrHeatMonth> implements IPrHeatMonthService {

    private final PrHeatMonthMapper baseMapper;

    @Override
    public PrHeatMonthVo selectById(Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatMonthVo> selectPageList(String companyId, String orgId, String buildingId,
                                                        String unitCode, String search, String isCharged,
                                                        String startTime, String endTime, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatMonth> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatMonth::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatMonth::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatMonth::getMeterNum, search.trim())
                .or().like(PrHeatMonth::getMeterArcCode, search.trim()));
        }
        lqw.eq(StringUtils.isNotBlank(isCharged), PrHeatMonth::getIsAudit, isCharged);
        lqw.ge(StringUtils.isNotBlank(startTime), PrHeatMonth::getCreateTime, startTime);
        lqw.le(StringUtils.isNotBlank(endTime), PrHeatMonth::getCreateTime, endTime);
        lqw.orderByDesc(PrHeatMonth::getCreateTime);
        Page<PrHeatMonthVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatMonth entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> ids) {
        return super.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateHeatMonth(String companyId, String orgId) {
        // TODO: Implement monthly heat report generation (5-step flow from old system)
        // 1. insertPrHeatMonth - Insert monthly records
        // 2. updateStartReading - Update start reading
        // 3. updateQty - Update quantity
        // 4. setFee - Calculate fees
        // 5. updateArrearage - Update arrearage status
        log.warn("generateHeatMonth not yet implemented for companyId={}, orgId={}", companyId, orgId);
        return false;
    }
}
