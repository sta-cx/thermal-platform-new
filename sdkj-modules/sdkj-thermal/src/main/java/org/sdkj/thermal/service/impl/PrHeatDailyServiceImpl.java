package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatDaily;
import org.sdkj.thermal.domain.vo.PrHeatDailyVo;
import org.sdkj.thermal.mapper.PrHeatDailyMapper;
import org.sdkj.thermal.service.IPrHeatDailyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 热表日记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatDailyServiceImpl extends ServiceImpl<PrHeatDailyMapper, PrHeatDaily> implements IPrHeatDailyService {

    private final PrHeatDailyMapper baseMapper;

    @Override
    public PrHeatDailyVo selectById(Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatDailyVo> selectPageList(String companyId, String orgId, String buildingId,
                                                        String unitCode, String search, Integer isCharged,
                                                        String startTime, String endTime, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatDaily> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatDaily::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatDaily::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatDaily::getMeterNum, search.trim())
                .or().like(PrHeatDaily::getMeterArcCode, search.trim()));
        }
        lqw.eq(isCharged != null, PrHeatDaily::getIsCalc, isCharged);
        lqw.ge(StringUtils.isNotBlank(startTime), PrHeatDaily::getCreateTime, startTime);
        lqw.le(StringUtils.isNotBlank(endTime), PrHeatDaily::getCreateTime, endTime);
        lqw.orderByDesc(PrHeatDaily::getCreateTime);
        Page<PrHeatDailyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateHeatDaily(String companyId, String orgId) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        java.util.Date targetDate = now.getTime();

        boolean result = true;
        result = result && baseMapper.setIsValid(targetDate, companyId, orgId);
        result = result && baseMapper.deleteDaily(targetDate, companyId, orgId);
        result = result && baseMapper.setHeatDaily(targetDate, companyId, orgId);
        result = result && baseMapper.setSteps(targetDate, companyId, orgId);
        result = result && baseMapper.setQtyStepsN(targetDate, companyId, orgId);
        result = result && baseMapper.setCurrentReading(targetDate, companyId, orgId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatDaily entity) {
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
}
