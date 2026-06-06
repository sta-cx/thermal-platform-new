package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrRepairPerson;
import org.sdkj.thermal.mapper.PrRepairPersonMapper;
import org.sdkj.thermal.service.IPrRepairPersonService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrRepairPersonServiceImpl extends OrgScopedServiceImpl<PrRepairPersonMapper, PrRepairPerson>
        implements IPrRepairPersonService {

    private final PrRepairPersonMapper baseMapper;
}
