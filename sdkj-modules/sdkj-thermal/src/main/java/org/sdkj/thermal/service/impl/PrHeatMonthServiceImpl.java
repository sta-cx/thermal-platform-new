package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.exception.ServiceException;
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
        HeatMonthGenerateResult result = generateFromDaily(orgId, false);
        return result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String setHeat(String orgId, Boolean force) {
        HeatMonthGenerateResult result = generateFromDaily(orgId, Boolean.TRUE.equals(force));
        if (!result.success()) {
            throw new ServiceException(result.message());
        }
        return result.message();
    }

    /**
     * 当前月表生成口径：从日表聚合生成月表。手动接口与 Quartz 定时任务共用这套逻辑，
     * 避免定时任务继续调用历史 5 步流程的空实现。
     */
    private HeatMonthGenerateResult generateFromDaily(String orgId, boolean forceCurrentMonth) {
        if (StringUtils.isBlank(orgId)) {
            String msg = "小区ID不能为空";
            log.warn(msg);
            return HeatMonthGenerateResult.failed(msg);
        }

        // 1. 确定生成月份: 默认上月, force=true时当月
        Calendar cal = Calendar.getInstance();
        if (!forceCurrentMonth) {
            cal.add(Calendar.MONTH, -1);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        String recordYm = String.format("%04d%02d", year, month);

        log.info("开始生成月表数据: year={}, month={}, recordYm={}, orgId={}, force={}",
            year, month, recordYm, orgId, forceCurrentMonth);

        // 2. 检查是否已生成
        long exists = baseMapper.selectCount(
            new LambdaQueryWrapper<PrHeatMonth>()
                .eq(PrHeatMonth::getRecordYm, recordYm)
                .eq(PrHeatMonth::getOrgId, orgId));

        if (exists > 0 && !forceCurrentMonth) {
            String msg = String.format("月表数据已生成（%s），共 %d 条，如需重算请使用 force=true 参数", recordYm, exists);
            log.warn(msg);
            return HeatMonthGenerateResult.success(msg);
        }

        // 3. 从日表汇总
        List<PrHeatMonth> monthList = prHeatDailyMapper.aggregateToMonth(year, month, orgId);

        if (monthList.isEmpty()) {
            String msg = String.format("未找到 %s 的日表数据，请先生成日表", recordYm);
            log.warn(msg);
            return HeatMonthGenerateResult.failed(msg);
        }

        // 4. 删除旧数据 + 批量插入
        if (exists > 0) {
            baseMapper.delete(
                new LambdaQueryWrapper<PrHeatMonth>()
                    .eq(PrHeatMonth::getRecordYm, recordYm)
                    .eq(PrHeatMonth::getOrgId, orgId));
            log.info("已删除 {} 条旧月表数据", exists);
        }

        super.saveBatch(monthList);

        String msg = String.format("月表生成成功（%s），共生成 %d 条记录", recordYm, monthList.size());
        log.info(msg);
        return HeatMonthGenerateResult.success(msg);
    }

    private record HeatMonthGenerateResult(boolean success, String message) {
        private static HeatMonthGenerateResult success(String message) {
            return new HeatMonthGenerateResult(true, message);
        }

        private static HeatMonthGenerateResult failed(String message) {
            return new HeatMonthGenerateResult(false, message);
        }
    }
}
