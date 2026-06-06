package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrHeatStation;
import org.sdkj.thermal.mapper.PrHeatStationMapper;
import org.sdkj.thermal.service.IPrHeatStationService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrHeatStationServiceImpl extends OrgScopedServiceImpl<PrHeatStationMapper, PrHeatStation>
        implements IPrHeatStationService {

    private final PrHeatStationMapper baseMapper;

    @Override
    public List<PrHeatStation> selectByOrgId(String orgId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<PrHeatStation>()
                .eq(PrHeatStation::getOrgId, orgId)
                .orderByAsc(PrHeatStation::getSeq));
    }
}
