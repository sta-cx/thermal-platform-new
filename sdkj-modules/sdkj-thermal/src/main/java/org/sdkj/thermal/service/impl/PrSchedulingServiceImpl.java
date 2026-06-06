package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrScheduling;
import org.sdkj.thermal.mapper.PrSchedulingMapper;
import org.sdkj.thermal.service.IPrSchedulingService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrSchedulingServiceImpl extends OrgScopedServiceImpl<PrSchedulingMapper, PrScheduling>
        implements IPrSchedulingService {

    private final PrSchedulingMapper baseMapper;
}
