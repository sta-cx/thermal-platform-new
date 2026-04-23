package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.vo.HtTasksVo;

import java.util.List;

/**
 * 调控任务Mapper
 */
public interface HtTasksMapper extends BaseMapperPlus<HtTasks, HtTasksVo> {

    /**
     * 查询所有任务（按组织）
     */
    List<HtTasks> selectAllByOrgId(@Param("orgId") String orgId);

    /**
     * 删除任务关联的范围记录
     */
    int deleteScopeByTaskId(@Param("taskId") Integer taskId);
}
