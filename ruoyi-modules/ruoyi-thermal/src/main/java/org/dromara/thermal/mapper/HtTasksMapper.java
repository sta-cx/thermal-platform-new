package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.vo.HtTasksVo;

import java.util.List;
import java.util.Map;

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

    /**
     * 更新任务状态
     */
    int updateTaskStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新最后执行时间
     */
    int updateLastTime(@Param("id") Integer id);

    /**
     * 标记特殊户/单元
     */
    int markScopeAsSpecial(@Param("scopeIds") List<String> scopeIds, @Param("val") String val, @Param("remark") String remark);

    /**
     * 标记特殊单元
     */
    int markUnitAsSpecial(@Param("scopeIds") List<String> scopeIds, @Param("val") String val, @Param("remark") String remark);

    /**
     * 设置缴费状态
     */
    int markPayStatus(@Param("scopeIds") List<String> scopeIds, @Param("val") String val);

    /**
     * 查询平衡率
     */
    double queryBalanceRate(@Param("taskId") String taskId);

    /**
     * 查询平均回水温度
     */
    double queryAvgReturnTemp(@Param("taskId") String taskId);

    /**
     * 保存设定开度（户）
     */
    int updateValveAngleH(@Param("taskId") String taskId);

    /**
     * 保存设定开度（单元）
     */
    int updateValveAngleD(@Param("taskId") String taskId);

    /**
     * 更新控制范围状态
     */
    int updateScopeStatus(@Param("taskId") String taskId, @Param("status") Integer status);

    /**
     * 查询汇总统计
     */
    Map<String, Object> querySummary(
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode
    );
}
