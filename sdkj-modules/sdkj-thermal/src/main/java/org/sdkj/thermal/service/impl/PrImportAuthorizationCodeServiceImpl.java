package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.vo.MdbVo;
import org.sdkj.thermal.mapper.PrImportAuthorizationCodeMapper;
import org.sdkj.thermal.service.IPrImportAuthorizationCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrImportAuthorizationCodeServiceImpl implements IPrImportAuthorizationCodeService {

    private final PrImportAuthorizationCodeMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertAuthorizationCode(List<MdbVo> list) {
        for (MdbVo vo : list) {
            mapper.insertAuthorizationCode(vo);
        }
        log.info("导入授权码完成: {} 条", list.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNumberSegmentCode(String numberSegmentCode) {
        mapper.insertNumberSegmentCode(numberSegmentCode);
    }
}
