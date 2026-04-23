package org.dromara.thermal.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtTasksPerform;
import org.dromara.thermal.domain.vo.HtTasksPerformVo;

import java.util.List;

/**
 * 调控执行记录Mapper
 */
public interface HtTasksPerformMapper extends BaseMapperPlus<HtTasksPerform, HtTasksPerformVo> {

    /**
     * 根据仪表ID分页查询执行记录
     */
    List<HtTasksPerformVo> selectByMeterId(String meterId);

    /**
     * 根据仪表ID查询执行记录详情
     */
    List<HtTasksPerformVo> selectByMeterIdDetail(String meterId);
}
