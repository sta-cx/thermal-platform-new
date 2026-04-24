package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasksPerform;
import org.dromara.thermal.domain.vo.HtTasksPerformVo;

import java.util.List;
import java.util.Map;

/**
 * 调控执行记录服务接口
 */
public interface IHtTasksPerformService extends IService<HtTasksPerform> {

    /**
     * 分页查询执行记录
     */
    TableDataInfo<HtTasksPerformVo> selectPageList(LambdaQueryWrapper<HtTasksPerform> lqw, PageQuery pageQuery);

    /**
     * 根据仪表ID查询执行记录（分页）
     */
    List<HtTasksPerformVo> selectByMeterId(String meterId);

    /**
     * 根据仪表ID查询执行记录详情
     */
    List<HtTasksPerformVo> selectByMeterIdDetail(String meterId);

    /**
     * 更新指令发送状态
     * @param performId 执行记录ID
     * @param status 新状态 (0=待发送 1=发送中 2=发送成功 3=发送失败)
     */
    boolean updateInstructionStatus(String performId, Integer status);

    /**
     * 批量更新指令发送状态
     */
    boolean batchUpdateInstructionStatus(List<String> performIds, Integer status);

    /**
     * 查询指定任务下待发送的指令
     */
    List<HtTasksPerformVo> selectPendingByTaskId(String taskId);

    /**
     * 查询执行统计
     */
    Map<String, Object> selectPerformStats(String taskId);
}
