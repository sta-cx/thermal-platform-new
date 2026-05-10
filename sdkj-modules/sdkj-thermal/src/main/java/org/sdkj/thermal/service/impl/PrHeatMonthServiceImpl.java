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
import org.sdkj.thermal.mapper.PrHeatDailyMapper;
import org.sdkj.thermal.mapper.PrHeatMonthMapper;
import org.sdkj.thermal.service.IPrHeatMonthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * 热表月记录 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrHeatMonthServiceImpl extends ServiceImpl<PrHeatMonthMapper, PrHeatMonth> implements IPrHeatMonthService {

    private final PrHeatMonthMapper baseMapper;
    private final PrHeatDailyMapper prHeatDailyMapper;

    @Override
    public PrHeatMonthVo selectById(Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatMonthVo> selectPageList(String orgId, String buildingId,
                                                        String unitCode, String search, String isCharged,
                                                        String startTime, String endTime, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatMonth> lqw = new LambdaQueryWrapper<>();
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
    public boolean generateHeatMonth(String orgId) {
        // TODO: Implement monthly heat report generation (5-step flow from old system)
        // 1. insertPrHeatMonth - Insert monthly records
        // 2. updateStartReading - Update start reading
        // 3. updateQty - Update quantity
        // 4. setFee - Calculate fees
        // 5. updateArrearage - Update arrearage status
        log.warn("generateHeatMonth not yet implemented for orgId={}", orgId);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String setHeat(String orgId, Boolean force) {
        // 1. 确定生成月份: 默认上月, force=true时当月
        Calendar cal = Calendar.getInstance();
        if (!Boolean.TRUE.equals(force)) {
            cal.add(Calendar.MONTH, -1);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        String recordYm = String.format("%04d%02d", year, month);

        log.info("开始生成月表数据: year={}, month={}, recordYm={}, orgId={}, force={}",
            year, month, recordYm, orgId, force);

        // 2. 检查是否已生成
        long exists = baseMapper.selectCount(
            new LambdaQueryWrapper<PrHeatMonth>()
                .eq(PrHeatMonth::getRecordYm, recordYm)
                .eq(PrHeatMonth::getOrgId, orgId));

        if (exists > 0 && !Boolean.TRUE.equals(force)) {
            String msg = String.format("月表数据已生成（%s），共 %d 条，如需重算请使用 force=true 参数", recordYm, exists);
            log.warn(msg);
            return msg;
        }

        // 3. 从日表汇总
        List<PrHeatMonth> monthList = prHeatDailyMapper.aggregateToMonth(year, month, orgId);

        if (monthList.isEmpty()) {
            String msg = String.format("未找到 %s 的日表数据，请先生成日表", recordYm);
            log.warn(msg);
            return msg;
        }

        // 4. 删除旧数据 + 批量插入
        if (exists > 0) {
            baseMapper.delete(
                new LambdaQueryWrapper<PrHeatMonth>()
                    .eq(PrHeatMonth::getRecordYm, recordYm)
                    .eq(PrHeatMonth::getOrgId, orgId));
            log.info("已删除 {} 条旧月表数据", exists);
        }

        // 设置 UUID (ASSIGN_UUID 策略自动处理, 但显式设置更可靠)
        for (PrHeatMonth m : monthList) {
        }

        super.saveBatch(monthList);

        String msg = String.format("月表生成成功（%s），共生成 %d 条记录", recordYm, monthList.size());
        log.info(msg);
        return msg;
    }
}
