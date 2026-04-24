package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtInstruction;
import org.sdkj.thermal.domain.vo.HtInstructionVo;
import org.sdkj.thermal.mapper.HtInstructionMapper;
import org.sdkj.thermal.service.IHtInstructionService;
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
