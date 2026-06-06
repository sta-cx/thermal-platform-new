package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.HtScopeDtu;
import org.sdkj.thermal.mapper.HtScopeDtuMapper;
import org.sdkj.thermal.service.IHtScopeDtuService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;

/**
 * DTU控制范围表服务实现
 */
@Service
@RequiredArgsConstructor
public class HtScopeDtuServiceImpl extends OrgScopedServiceImpl<HtScopeDtuMapper, HtScopeDtu> implements IHtScopeDtuService {

}
