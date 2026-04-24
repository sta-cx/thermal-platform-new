package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtHouseStrategy;
import org.sdkj.thermal.domain.vo.HtHouseStrategyVo;

import java.util.List;

/**
 * 单元房屋策略服务接口
 */
public interface IHtHouseStrategyService extends IService<HtHouseStrategy> {

    /**
     * 分页查询房屋策略绑定
     */
    TableDataInfo<HtHouseStrategyVo> selectPageList(LambdaQueryWrapper<HtHouseStrategy> lqw, PageQuery pageQuery);

    /**
     * 批量插入房屋策略绑定
     */
    boolean insertBatch(List<HtHouseStrategy> list, String orgId, String companyId);

    /**
     * 批量更新房屋策略绑定
     */
    boolean updateBatch(List<HtHouseStrategy> list);

    /**
     * 批量删除房屋策略绑定
     */
    boolean deleteBatch(List<HtHouseStrategy> list);
}
