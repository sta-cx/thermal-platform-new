package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.bo.MonitorBo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatTempArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatUnitHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatUnitValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.service.IPrMonitorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 运行监控 Service 实现
 */
@RequiredArgsConstructor
@Service
public class PrMonitorServiceImpl implements IPrMonitorService {

    private final PrHeatArchiveMapper heatArchiveMapper;
    private final PrHeatValveArchiveMapper valveArchiveMapper;
    private final PrHeatUnitHotArchiveMapper unitHotArchiveMapper;
    private final PrHeatUnitValveArchiveMapper unitValveArchiveMapper;
    private final PrHeatTempArchiveMapper tempArchiveMapper;

    @Override
    public TableDataInfo<PrHeatArchiveVo> heatList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, kw)
                .or().like(PrHeatArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = heatArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatValveArchiveVo> valveList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatValveArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatValveArchive::getMeterNum, kw)
                .or().like(PrHeatValveArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        Page<PrHeatValveArchiveVo> result = valveArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatUnitHotArchiveVo> unitHeatList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatUnitHotArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatUnitHotArchive::getMeterNum, kw)
                .or().like(PrHeatUnitHotArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitHotArchive::getCreateTime);
        Page<PrHeatUnitHotArchiveVo> result = unitHotArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatUnitValveArchiveVo> unitValveList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatUnitValveArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatUnitValveArchive::getMeterNum, kw)
                .or().like(PrHeatUnitValveArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitValveArchive::getCreateTime);
        Page<PrHeatUnitValveArchiveVo> result = unitValveArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatTempArchiveVo> tempList(MonitorBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrHeatTempArchive::getOrgId, bo.getOrgId());
        if (StringUtils.isNotBlank(bo.getSearch())) {
            String kw = bo.getSearch().trim();
            lqw.and(w -> w.like(PrHeatTempArchive::getMeterNum, kw)
                .or().like(PrHeatTempArchive::getMeterArcName, kw));
        }
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        Page<PrHeatTempArchiveVo> result = tempArchiveMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public MonitorAggregateVo aggregate(MonitorBo bo) {
        MonitorAggregateVo vo = new MonitorAggregateVo();
        if (StringUtils.isBlank(bo.getOrgId())) {
            return vo;
        }

        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatArchive::getOrgId, bo.getOrgId());
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.select(PrHeatArchive::getOutTemperature, PrHeatArchive::getInTemperature);
        List<PrHeatArchive> archives = heatArchiveMapper.selectList(lqw);

        vo.setTotalCount(archives.size());
        BigDecimal sumOut = BigDecimal.ZERO;
        BigDecimal sumIn = BigDecimal.ZERO;
        int outCount = 0;
        int inCount = 0;
        for (PrHeatArchive a : archives) {
            if (a.getOutTemperature() != null) {
                sumOut = sumOut.add(a.getOutTemperature());
                outCount++;
            }
            if (a.getInTemperature() != null) {
                sumIn = sumIn.add(a.getInTemperature());
                inCount++;
            }
        }
        if (outCount > 0) {
            vo.setAvgOutTemp(sumOut.divide(BigDecimal.valueOf(outCount), 2, RoundingMode.HALF_UP));
        }
        if (inCount > 0) {
            vo.setAvgInTemp(sumIn.divide(BigDecimal.valueOf(inCount), 2, RoundingMode.HALF_UP));
        }
        vo.setOnlineCount(0);
        vo.setOfflineCount(vo.getTotalCount());
        return vo;
    }
}
