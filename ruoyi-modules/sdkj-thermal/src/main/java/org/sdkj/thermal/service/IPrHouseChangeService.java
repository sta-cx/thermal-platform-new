package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouse;

/**
 * 房屋变更管理 Service 接口
 */
public interface IPrHouseChangeService extends IService<PrHouse> {

    /**
     * 分页查询房屋列表
     */
    TableDataInfo<PrHouse> selectPageList(LambdaQueryWrapper<PrHouse> lqw, PageQuery pageQuery);

    /**
     * 审核入住/迁出
     */
    boolean audit(PrHouse house);
}
