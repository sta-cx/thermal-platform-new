package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtStrategy;
import org.sdkj.thermal.domain.vo.HtStrategyVo;

import java.util.List;

/**
 * 控制策略 Service 接口
 * 迁移自旧系统 HtStrategyService
 */
public interface IHtStrategyService extends IService<HtStrategy> {

    /**
     * 分页查询策略列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<HtStrategyVo> selectPageList(LambdaQueryWrapper<HtStrategy> lqw, PageQuery pageQuery);

    /**
     * 根据ID查询策略详情（包含子表记录）
     * @param id 策略ID
     * @return 策略详情
     */
    HtStrategyVo selectDetailById(Long id);

    /**
     * 查询所有策略
     * @return 策略列表
     */
    List<HtStrategy> selectAllList();

}
