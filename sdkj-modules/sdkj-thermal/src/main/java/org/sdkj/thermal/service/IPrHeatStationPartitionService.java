package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatStationPartition;

import java.util.List;

public interface IPrHeatStationPartitionService extends IService<PrHeatStationPartition> {

    TableDataInfo<PrHeatStationPartition> selectPageList(String search, String stationId, PageQuery pageQuery);

    List<PrHeatStationPartition> selectByStationId(String stationId);
}
