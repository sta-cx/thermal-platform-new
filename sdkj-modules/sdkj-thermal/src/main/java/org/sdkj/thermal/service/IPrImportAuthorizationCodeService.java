package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.vo.MdbVo;

import java.util.List;

public interface IPrImportAuthorizationCodeService {

    void insertAuthorizationCode(List<MdbVo> list);

    void insertNumberSegmentCode(String numberSegmentCode);
}
