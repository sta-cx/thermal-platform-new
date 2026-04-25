package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sdkj.thermal.domain.AgReaderParam;
import org.sdkj.thermal.mapper.AgReaderParamMapper;
import org.sdkj.thermal.service.IAgReaderParamService;
import org.springframework.stereotype.Service;

@Service
public class AgReaderParamServiceImpl extends ServiceImpl<AgReaderParamMapper, AgReaderParam>
        implements IAgReaderParamService {
}
