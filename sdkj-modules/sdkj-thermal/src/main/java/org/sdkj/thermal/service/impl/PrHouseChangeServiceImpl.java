package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IPrHouseChangeService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 房屋变更管理 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHouseChangeServiceImpl extends OrgScopedServiceImpl<PrHouseMapper, PrHouse>
        implements IPrHouseChangeService {

    private final PrHouseMapper baseMapper;

    @Override
    public TableDataInfo<PrHouse> selectPageList(LambdaQueryWrapper<PrHouse> lqw, PageQuery pageQuery) {
        Page<PrHouse> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(PrHouse house) {
        return updateById(house);
    }
}
