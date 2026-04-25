package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrInspectionRecord;
import org.sdkj.thermal.mapper.PrInspectionRecordMapper;
import org.sdkj.thermal.service.IPrInspectionRecordService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrInspectionRecordServiceImpl extends ServiceImpl<PrInspectionRecordMapper, PrInspectionRecord>
        implements IPrInspectionRecordService {

    private final PrInspectionRecordMapper baseMapper;
}
