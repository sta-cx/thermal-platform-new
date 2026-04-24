package org.sdkj.thermal.mapper;

import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;

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
