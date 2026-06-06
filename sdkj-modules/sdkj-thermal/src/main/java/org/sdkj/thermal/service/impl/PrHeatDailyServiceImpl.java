package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatDaily;
import org.sdkj.thermal.domain.vo.PrHeatDailyVo;
import org.sdkj.thermal.mapper.PrHeatDailyMapper;
import org.sdkj.thermal.service.IPrHeatDailyService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.mapper.PrHouseMapper;

/**
 * 热表日记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatDailyServiceImpl extends OrgScopedServiceImpl<PrHeatDailyMapper, PrHeatDaily> implements IPrHeatDailyService {

    private final PrHeatDailyMapper baseMapper;
    private final PrHouseMapper houseMapper;

    @Override
    public PrHeatDailyVo selectById(Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatDailyVo> selectPageList(String orgId, String buildingId,
                                                        String unitCode, String search, Integer isCharged,
                                                        String startTime, String endTime, PageQuery pageQuery) {
        // 楼宇/单元筛选：两步无 JOIN —— 先查 pr_house 拿 house_id 集合，再 IN 进日表查询
        List<Long> houseIds = resolveHouseIds(orgId, buildingId, unitCode);
        if (houseIds != null && houseIds.isEmpty()) {
            return TableDataInfo.build(new java.util.ArrayList<>());
        }

        LambdaQueryWrapper<PrHeatDaily> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatDaily::getOrgId, orgId);
        lqw.in(houseIds != null, PrHeatDaily::getHouseId, houseIds);
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

    /**
     * 楼宇/单元侧筛选 → house_id 集合（两步无 JOIN）。
     * null = 无楼宇/单元筛选（调用方跳过 IN）；非 null = 命中 house_id（空 → 列表返回空）。
     */
    private List<Long> resolveHouseIds(String orgId, String buildingId, String unitCode) {
        boolean hasFilter = StringUtils.isNotBlank(buildingId) || StringUtils.isNotBlank(unitCode);
        if (!hasFilter) return null;
        LambdaQueryWrapper<PrHouse> hq = new LambdaQueryWrapper<>();
        hq.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId)
          .eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId)
          .eq(StringUtils.isNotBlank(unitCode), PrHouse::getUnitCode, unitCode)
          .select(PrHouse::getId);
        return houseMapper.selectList(hq).stream().map(PrHouse::getId).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateHeatDaily(String orgId) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        java.util.Date targetDate = now.getTime();

        boolean result = true;
        result = result && baseMapper.setIsValid(targetDate, orgId);
        result = result && baseMapper.deleteDaily(targetDate, orgId);
        result = result && baseMapper.setHeatDaily(targetDate, orgId);
        result = result && baseMapper.setSteps(targetDate, orgId);
        result = result && baseMapper.setQtyStepsN(targetDate, orgId);
        result = result && baseMapper.setCurrentReading(targetDate, orgId);
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
