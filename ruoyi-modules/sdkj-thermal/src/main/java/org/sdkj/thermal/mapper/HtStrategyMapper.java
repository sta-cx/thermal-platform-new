package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtStrategy;
import org.sdkj.thermal.domain.vo.HtStrategyVo;

import java.util.List;

/**
 * 控制策略主表 Mapper
 * 迁移自旧系统 HtStrategyMapper
 */
public interface HtStrategyMapper extends BaseMapperPlus<HtStrategy, HtStrategyVo> {

    /**
     * 查询所有策略列表
     * @return 策略列表
     */
    List<HtStrategy> selectAllList();

}
