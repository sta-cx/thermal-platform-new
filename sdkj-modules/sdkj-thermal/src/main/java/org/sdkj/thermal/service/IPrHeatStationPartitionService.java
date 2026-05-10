package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrHeatStationPartition;

import java.util.List;

public interface IPrHeatStationPartitionService extends IService<PrHeatStationPartition> {

    List<PrHeatStationPartition> selectByStationId(String stationId);
}
