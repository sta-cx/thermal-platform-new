package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrUserHouse;
import org.sdkj.thermal.domain.vo.PrUserHouseVo;
import org.sdkj.thermal.mapper.PrUserHouseMapper;
import org.sdkj.thermal.service.IPrUserHouseService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrUserHouseServiceImpl extends OrgScopedServiceImpl<PrUserHouseMapper, PrUserHouse> implements IPrUserHouseService {

    private final PrUserHouseMapper baseMapper;

    @Override
    public List<PrUserHouseVo> selectChangeHistory(Long houseId, String startDate, String endDate) {
        return baseMapper.selectChangeHistory(houseId, startDate, endDate);
    }
}
