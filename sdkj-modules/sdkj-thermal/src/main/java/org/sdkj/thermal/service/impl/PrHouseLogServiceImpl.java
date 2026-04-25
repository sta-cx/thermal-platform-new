package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrHouseLog;
import org.sdkj.thermal.mapper.PrHouseLogMapper;
import org.sdkj.thermal.service.IPrHouseLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrHouseLogServiceImpl implements IPrHouseLogService {

    private final PrHouseLogMapper houseLogMapper;

    @Override
    public List<PrHouseLog> selectHouseChangeData(String changeType) {
        return houseLogMapper.selectHouseChangeData(changeType);
    }

    @Override
    public List<PrHouseLog> selectUnitChangeData(String changeType) {
        return houseLogMapper.selectUnitChangeData(changeType);
    }
}
