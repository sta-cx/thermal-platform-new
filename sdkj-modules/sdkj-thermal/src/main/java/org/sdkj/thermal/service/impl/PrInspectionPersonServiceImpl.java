package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrInspectionPerson;
import org.sdkj.thermal.mapper.PrInspectionPersonMapper;
import org.sdkj.thermal.service.IPrInspectionPersonService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrInspectionPersonServiceImpl extends ServiceImpl<PrInspectionPersonMapper, PrInspectionPerson>
        implements IPrInspectionPersonService {

    private final PrInspectionPersonMapper baseMapper;
}
