package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.PrHouseLog;

import java.util.List;

public interface IPrHouseLogService {

    List<PrHouseLog> selectHouseChangeData(String changeType);

    List<PrHouseLog> selectUnitChangeData(String changeType);
}
