package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.mapper.PrOptionsHeatMapper;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供热系统选项 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrOptionsHeatServiceImpl extends ServiceImpl<PrOptionsHeatMapper, PrOptionsHeat>
        implements IPrOptionsHeatService {

    private final PrOptionsHeatMapper baseMapper;

    @Override
    public PrOptionsHeat getByOrgAndCompany(String orgId, String companyId, String level) {
        LambdaQueryWrapper<PrOptionsHeat> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrOptionsHeat::getOrgId, orgId)
                .eq(PrOptionsHeat::getCompanyId, companyId);
        if (level != null) {
            lqw.eq(PrOptionsHeat::getLevel, level);
        }
        lqw.last("LIMIT 1");
        return baseMapper.selectOne(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initData(String orgId, String companyId) {
        PrOptionsHeat options = new PrOptionsHeat();
        options.setOrgId(orgId);
        options.setCompanyId(companyId);
        return save(options);
    }
}
