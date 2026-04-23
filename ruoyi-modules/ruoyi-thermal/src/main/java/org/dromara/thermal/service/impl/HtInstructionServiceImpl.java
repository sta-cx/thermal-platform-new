package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;
import org.dromara.thermal.mapper.HtInstructionMapper;
import org.dromara.thermal.service.IHtInstructionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 控制指令 Service 实现
 * 迁移自旧系统 HtInstructionServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtInstructionServiceImpl extends ServiceImpl<HtInstructionMapper, HtInstruction> implements IHtInstructionService {

    private final HtInstructionMapper baseMapper;

    @Override
    public TableDataInfo<HtInstructionVo> selectPageList(LambdaQueryWrapper<HtInstruction> lqw, PageQuery pageQuery) {
        Page<HtInstructionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public int countByStrategySub(String instructionId) {
        return baseMapper.countByStrategySub(instructionId);
    }

    @Override
    public List<HtInstruction> selectAllList() {
        return baseMapper.selectAllList();
    }

}
