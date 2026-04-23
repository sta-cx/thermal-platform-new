package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasksPerform;
import org.dromara.thermal.domain.vo.HtTasksPerformVo;

import java.util.List;

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
}
