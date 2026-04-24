package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtInstruction;
import org.sdkj.thermal.domain.vo.HtInstructionVo;

import java.util.List;

/**
 * 控制指令 Mapper
 * 迁移自旧系统 HtInstructionMapper
 */
public interface HtInstructionMapper extends BaseMapperPlus<HtInstruction, HtInstructionVo> {

    /**
     * 统计指令被策略子表引用的次数
     * @param instructionId 指令ID
     * @return 引用次数
     */
    int countByStrategySub(@Param("instructionId") String instructionId);

    /**
     * 查询所有指令列表
     * @return 指令列表
     */
    List<HtInstruction> selectAllList();

}
