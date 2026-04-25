package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrScheduling;
import org.sdkj.thermal.mapper.PrSchedulingMapper;
import org.sdkj.thermal.service.IPrSchedulingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrSchedulingServiceImpl extends ServiceImpl<PrSchedulingMapper, PrScheduling>
        implements IPrSchedulingService {

    private final PrSchedulingMapper baseMapper;
}
