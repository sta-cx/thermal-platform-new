package org.sdkj.thermal.service;

import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtStrategyPerform;
import org.sdkj.thermal.domain.bo.HtStrategyPerformBo;
import org.sdkj.thermal.domain.vo.HtStrategyPerformVo;

import java.util.List;

/**
 * 策略执行记录 Service 接口
 */
public interface IHtStrategyPerformService {

    /**
     * 分页查询策略执行记录
     * @param strategyId 策略ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<HtStrategyPerformVo> selectPageList(Long strategyId, PageQuery pageQuery);

    /**
     * 根据策略ID查询全部执行记录
     * @param strategyId 策略ID
     * @return 执行记录列表
     */
    List<HtStrategyPerformVo> selectByStrategyId(Long strategyId);

    /**
     * 批量保存策略执行记录
     * @param strategyId 策略ID
     * @param list 执行记录列表
     */
    void insertBatch(Long strategyId, List<HtStrategyPerformBo> list);

    /**
     * 根据策略ID删除执行记录
     * @param strategyId 策略ID
     */
    void deleteByStrategyId(Long strategyId);
}
