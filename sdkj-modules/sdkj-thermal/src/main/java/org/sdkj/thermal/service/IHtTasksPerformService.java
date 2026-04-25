package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;

import org.sdkj.thermal.domain.dto.ValveArchiveInfo;

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

    /**
     * 查询全局状态汇总
     */
    Map<String, Object> selectGlobalStatusSummary();

    /**
     * 批量创建阀门控制任务并保存
     * @param archives 阀门配表信息列表
     * @param orgId 组织ID
     * @param companyId 公司ID
     * @param instruction 指令值 (100=开阀, 0=关阀)
     */
    boolean batchCreateValveControlTasks(List<ValveArchiveInfo> archives, String orgId, String companyId, int instruction);
}
