package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtStrategy;
import org.dromara.thermal.domain.HtStrategySub;
import org.dromara.thermal.domain.vo.HtStrategySubVo;
import org.dromara.thermal.domain.vo.HtStrategyVo;
import org.dromara.thermal.mapper.HtStrategyMapper;
import org.dromara.thermal.mapper.HtStrategySubMapper;
import org.dromara.thermal.service.IHtStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 控制策略 Service 实现
 * 迁移自旧系统 HtStrategyServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtStrategyServiceImpl extends ServiceImpl<HtStrategyMapper, HtStrategy> implements IHtStrategyService {

    private final HtStrategyMapper baseMapper;
    private final HtStrategySubMapper subMapper;

    @Override
    public TableDataInfo<HtStrategyVo> selectPageList(LambdaQueryWrapper<HtStrategy> lqw, PageQuery pageQuery) {
        Page<HtStrategyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public HtStrategyVo selectDetailById(String id) {
        HtStrategyVo vo = baseMapper.selectVoById(id);
        if (vo != null) {
            List<HtStrategySubVo> subList = subMapper.selectByStrategyId(id);
            vo.setSubList(subList);
        }
        return vo;
    }

    @Override
    public List<HtStrategy> selectAllList() {
        return baseMapper.selectAllList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(HtStrategy entity) {
        boolean result = super.save(entity);
        if (result && entity.getSubList() != null && !entity.getSubList().isEmpty()) {
            for (HtStrategySub sub : entity.getSubList()) {
                sub.setStrategyId(entity.getId());
                subMapper.insert(sub);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(HtStrategy entity) {
        boolean result = super.updateById(entity);
        if (result && entity.getSubList() != null) {
            subMapper.deleteByStrategyId(entity.getId());
            for (HtStrategySub sub : entity.getSubList()) {
                sub.setStrategyId(entity.getId());
                subMapper.insert(sub);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        subMapper.deleteByStrategyId((String) id);
        return super.removeById(id);
    }

}
