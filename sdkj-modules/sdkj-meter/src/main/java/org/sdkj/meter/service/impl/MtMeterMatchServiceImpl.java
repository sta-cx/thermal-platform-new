package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.meter.domain.MtMeterMatch;
import org.sdkj.meter.mapper.MtMeterMatchMapper;
import org.sdkj.meter.service.IMtMeterMatchService;
import org.springframework.stereotype.Service;

/**
 * 仪表分配服务实现
 */
@Service
@RequiredArgsConstructor
public class MtMeterMatchServiceImpl extends ServiceImpl<MtMeterMatchMapper, MtMeterMatch> implements IMtMeterMatchService {

    private final MtMeterMatchMapper baseMapper;

}
