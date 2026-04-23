package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasksPerform;
import org.dromara.thermal.domain.vo.HtTasksPerformVo;
import org.dromara.thermal.mapper.HtTasksPerformMapper;
import org.dromara.thermal.service.IHtTasksPerformService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 调控执行记录服务实现
 */
@Service
@RequiredArgsConstructor
public class HtTasksPerformServiceImpl extends ServiceImpl<HtTasksPerformMapper, HtTasksPerform> implements IHtTasksPerformService {

    private final HtTasksPerformMapper baseMapper;

    @Override
    public TableDataInfo<HtTasksPerformVo> selectPageList(LambdaQueryWrapper<HtTasksPerform> lqw, PageQuery pageQuery) {
        Page<HtTasksPerformVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtTasksPerformVo> selectByMeterId(String meterId) {
        return baseMapper.selectByMeterId(meterId);
    }

    @Override
    public List<HtTasksPerformVo> selectByMeterIdDetail(String meterId) {
        return baseMapper.selectByMeterIdDetail(meterId);
    }
}
