package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtHouseStrategy;
import org.dromara.thermal.domain.vo.HtHouseStrategyVo;
import org.dromara.thermal.mapper.HtHouseStrategyMapper;
import org.dromara.thermal.service.IHtHouseStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单元房屋策略服务实现
 */
@Service
@RequiredArgsConstructor
public class HtHouseStrategyServiceImpl extends ServiceImpl<HtHouseStrategyMapper, HtHouseStrategy> implements IHtHouseStrategyService {

    private final HtHouseStrategyMapper baseMapper;

    @Override
    public TableDataInfo<HtHouseStrategyVo> selectPageList(LambdaQueryWrapper<HtHouseStrategy> lqw, PageQuery pageQuery) {
        Page<HtHouseStrategyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(List<HtHouseStrategy> list, String orgId, String companyId) {
        if (list == null || list.isEmpty()) return false;
        for (HtHouseStrategy item : list) {
            item.setOrgId(orgId);
            item.setCompanyId(companyId);
            baseMapper.insert(item);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(List<HtHouseStrategy> list) {
        if (list == null || list.isEmpty()) return false;
        for (HtHouseStrategy item : list) {
            baseMapper.updateById(item);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<HtHouseStrategy> list) {
        if (list == null || list.isEmpty()) return false;
        for (HtHouseStrategy item : list) {
            baseMapper.deleteById(item.getId());
        }
        return true;
    }
}
