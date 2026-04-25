package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtStrategyPerform;
import org.sdkj.thermal.domain.bo.HtStrategyPerformBo;
import org.sdkj.thermal.domain.vo.HtStrategyPerformVo;
import org.sdkj.thermal.mapper.HtStrategyPerformMapper;
import org.sdkj.thermal.service.IHtStrategyPerformService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 策略执行记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class HtStrategyPerformServiceImpl extends ServiceImpl<HtStrategyPerformMapper, HtStrategyPerform> implements IHtStrategyPerformService {

    private final HtStrategyPerformMapper baseMapper;

    @Override
    public TableDataInfo<HtStrategyPerformVo> selectPageList(Long strategyId, PageQuery pageQuery) {
        LambdaQueryWrapper<HtStrategyPerform> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtStrategyPerform::getStrategyId, strategyId);
        lqw.orderByAsc(HtStrategyPerform::getOrderr);
        Page<HtStrategyPerformVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtStrategyPerformVo> selectByStrategyId(Long strategyId) {
        LambdaQueryWrapper<HtStrategyPerform> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtStrategyPerform::getStrategyId, strategyId);
        lqw.orderByAsc(HtStrategyPerform::getOrderr);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(Long strategyId, List<HtStrategyPerformBo> list) {
        deleteByStrategyId(strategyId);
        if (list != null && !list.isEmpty()) {
            for (HtStrategyPerformBo bo : list) {
                HtStrategyPerform entity = MapstructUtils.convert(bo, HtStrategyPerform.class);
                entity.setStrategyId(strategyId);
                baseMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByStrategyId(Long strategyId) {
        LambdaQueryWrapper<HtStrategyPerform> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtStrategyPerform::getStrategyId, strategyId);
        baseMapper.delete(lqw);
    }
}
