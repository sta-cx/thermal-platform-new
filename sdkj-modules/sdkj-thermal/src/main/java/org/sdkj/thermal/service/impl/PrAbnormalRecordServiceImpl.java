package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrAbnormalRecord;
import org.sdkj.thermal.mapper.PrAbnormalRecordMapper;
import org.sdkj.thermal.service.IPrAbnormalRecordService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrAbnormalRecordServiceImpl extends ServiceImpl<PrAbnormalRecordMapper, PrAbnormalRecord>
        implements IPrAbnormalRecordService {

    private final PrAbnormalRecordMapper baseMapper;
}
