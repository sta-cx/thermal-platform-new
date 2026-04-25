package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrNotice;
import org.sdkj.thermal.mapper.PrNoticeMapper;
import org.sdkj.thermal.service.IPrNoticeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrNoticeServiceImpl extends ServiceImpl<PrNoticeMapper, PrNotice>
        implements IPrNoticeService {

    private final PrNoticeMapper baseMapper;
}
