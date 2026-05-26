package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.thermal.domain.PrStrategy;
import org.sdkj.thermal.domain.bo.PrStrategyBo;
import org.sdkj.thermal.mapper.PrStrategyMapper;
import org.sdkj.thermal.service.IPrStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrStrategyServiceImpl extends ServiceImpl<PrStrategyMapper, PrStrategy>
        implements IPrStrategyService {

    private final PrStrategyMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStrategy(PrStrategyBo bo) {
        checkBaseUnique(bo.getOrgId(), bo.getType(), null);
        PrStrategy entity = MapstructUtils.convert(bo, PrStrategy.class);
        return save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStrategy(PrStrategyBo bo) {
        if (bo.getId() == null) {
            throw new ServiceException("策略ID不能为空");
        }
        PrStrategy existing = getById(bo.getId());
        if (existing == null) {
            throw new ServiceException("策略不存在");
        }
        checkBaseUnique(bo.getOrgId(), bo.getType(), bo.getId());
        PrStrategy entity = MapstructUtils.convert(bo, PrStrategy.class);
        return updateById(entity);
    }

    private void checkBaseUnique(String orgId, String type, Long excludeId) {
        if (!"base".equals(type)) {
            return;
        }
        Long existingId = baseMapper.selectBaseStrategyId(orgId);
        if (existingId != null && !existingId.equals(excludeId)) {
            throw new ServiceException("该小区已存在基础策略");
        }
    }
}
