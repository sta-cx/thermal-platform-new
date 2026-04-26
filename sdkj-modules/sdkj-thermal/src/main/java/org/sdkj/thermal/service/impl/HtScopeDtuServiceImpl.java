package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.HtScopeDtu;
import org.sdkj.thermal.mapper.HtScopeDtuMapper;
import org.sdkj.thermal.service.IHtScopeDtuService;
import org.springframework.stereotype.Service;

/**
 * DTU控制范围表服务实现
 */
@Service
@RequiredArgsConstructor
public class HtScopeDtuServiceImpl extends ServiceImpl<HtScopeDtuMapper, HtScopeDtu> implements IHtScopeDtuService {

}
