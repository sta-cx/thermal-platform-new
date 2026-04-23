package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.vo.HtTasksVo;

import java.util.List;

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
}
