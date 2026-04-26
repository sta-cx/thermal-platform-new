package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtStrategyPerform;
import org.sdkj.thermal.domain.vo.HtStrategyPerformVo;

/**
 * 策略执行记录Mapper
 */
@Mapper
public interface HtStrategyPerformMapper extends BaseMapperPlus<HtStrategyPerform, HtStrategyPerformVo> {

    /**
     * 根据任务ID和命令索引查询策略执行记录
     */
    HtStrategyPerform queryStrategyPerformListByTasksIdAndIndex(@Param("tasksId") Long tasksId, @Param("commandIndex") Integer commandIndex);
}
