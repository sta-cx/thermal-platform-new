package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtStrategySub;
import org.dromara.thermal.domain.vo.HtStrategySubVo;

import java.util.List;

/**
 * 控制策略子表 Mapper
 * 迁移自旧系统 HtStrategySubMapper
 */
public interface HtStrategySubMapper extends BaseMapperPlus<HtStrategySub, HtStrategySubVo> {

    /**
     * 根据策略ID查询子表记录
     * @param strategyId 策略ID
     * @return 子表记录列表
     */
    List<HtStrategySubVo> selectByStrategyId(@Param("strategyId") String strategyId);

    /**
     * 根据策略ID删除子表记录
     * @param strategyId 策略ID
     * @return 删除记录数
     */
    int deleteByStrategyId(@Param("strategyId") String strategyId);

}
