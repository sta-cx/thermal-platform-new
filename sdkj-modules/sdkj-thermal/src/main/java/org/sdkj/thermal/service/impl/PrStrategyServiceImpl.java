package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrStrategy;
import org.sdkj.thermal.mapper.PrStrategyMapper;
import org.sdkj.thermal.service.IPrStrategyService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrStrategyServiceImpl extends ServiceImpl<PrStrategyMapper, PrStrategy>
        implements IPrStrategyService {

    private final PrStrategyMapper baseMapper;
}
