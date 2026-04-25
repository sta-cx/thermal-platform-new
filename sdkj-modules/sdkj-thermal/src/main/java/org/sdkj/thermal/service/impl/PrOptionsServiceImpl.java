package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrOptions;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrOptionsMapper;
import org.sdkj.thermal.service.IPrOptionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统选项 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrOptionsServiceImpl extends ServiceImpl<PrOptionsMapper, PrOptions>
        implements IPrOptionsService {

    private final PrOptionsMapper baseMapper;
    private final PrHouseMapper houseMapper;

    @Override
    public List<PrOptions> selectByOrgId(String orgId) {
        return lambdaQuery()
            .eq(PrOptions::getOrgId, orgId)
            .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initOptions(String orgId) {
        PrOptions defaultOption = new PrOptions();
        defaultOption.setOrgId(orgId);
        return save(defaultOption);
    }

    @Override
    public boolean forbiddenToBuyCheck(String houseId) {
        PrHouse house = houseMapper.selectById(houseId);
        if (house == null || house.getOrgId() == null) return false;
        var options = lambdaQuery().eq(PrOptions::getOrgId, house.getOrgId()).one();
        if (options == null) return false;
        return Boolean.TRUE.equals(options.getForbiddenBuyElectric())
            || Boolean.TRUE.equals(options.getForbiddenBuyWater());
    }
}
