package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrHouse;
import org.dromara.thermal.mapper.PrHouseMapper;
import org.dromara.thermal.service.IPrHouseChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 房屋变更管理 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHouseChangeServiceImpl extends ServiceImpl<PrHouseMapper, PrHouse>
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
