package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.vo.HtTasksVo;

import java.util.List;
import java.util.Map;

/**
 * 调控任务服务接口
 */
public interface IHtTasksService extends IService<HtTasks> {

    /**
     * 分页查询任务列表
     */
    TableDataInfo<HtTasksVo> selectPageList(LambdaQueryWrapper<HtTasks> lqw, PageQuery pageQuery);

    /**
     * 查询所有任务（按组织）
     */
    List<HtTasks> selectAllByOrgId(String orgId);

    /**
     * 保存任务及关联范围
     */
    boolean saveWithScope(HtTasks entity, List<String> scopeIds);

    /**
     * 更新任务及关联范围
     */
    boolean updateWithScope(HtTasks entity, List<String> scopeIds);

    /**
     * 启动/停止任务（TODO: Quartz/snail-job集成）
     */
    boolean changeStatus(Integer id, Integer status);

    /**
     * 立即运行任务（TODO: Quartz/snail-job集成）
     */
    boolean runTask(Integer id);

    /**
     * 标记特殊户
     */
    boolean markSpecial(List<String> scopeIds, String val, String remark);

    /**
     * 标记特殊单元
     */
    boolean markSpecialUnit(List<String> scopeIds, String val, String remark);

    /**
     * 设置缴费状态
     */
    boolean markPayStatus(List<String> scopeIds, String val);

    /**
     * 刷新平衡率
     */
    double refreshBalanceRate(String taskId);

    /**
     * 保存设定开度
     */
    boolean saveValveAngle(String taskId, String scopeType);

    /**
     * 查询汇总统计
     */
    Map<String, Object> querySummary(String orgId, String buildingId, String unitCode);
}
