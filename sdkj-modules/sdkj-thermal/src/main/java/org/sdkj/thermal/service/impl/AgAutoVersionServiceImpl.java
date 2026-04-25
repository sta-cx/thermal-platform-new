package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sdkj.thermal.domain.AgAutoVersion;
import org.sdkj.thermal.mapper.AgAutoVersionMapper;
import org.sdkj.thermal.service.IAgAutoVersionService;
import org.springframework.stereotype.Service;

@Service
public class AgAutoVersionServiceImpl extends ServiceImpl<AgAutoVersionMapper, AgAutoVersion>
        implements IAgAutoVersionService {
}
