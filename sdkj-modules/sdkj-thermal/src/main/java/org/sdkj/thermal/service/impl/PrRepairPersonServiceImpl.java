package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrRepairPerson;
import org.sdkj.thermal.mapper.PrRepairPersonMapper;
import org.sdkj.thermal.service.IPrRepairPersonService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrRepairPersonServiceImpl extends ServiceImpl<PrRepairPersonMapper, PrRepairPerson>
        implements IPrRepairPersonService {

    private final PrRepairPersonMapper baseMapper;
}
