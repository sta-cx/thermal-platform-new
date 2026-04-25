package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrInspectionPlan;
import org.sdkj.thermal.mapper.PrInspectionPlanMapper;
import org.sdkj.thermal.service.IPrInspectionPlanService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrInspectionPlanServiceImpl extends ServiceImpl<PrInspectionPlanMapper, PrInspectionPlan>
        implements IPrInspectionPlanService {

    private final PrInspectionPlanMapper baseMapper;
}
