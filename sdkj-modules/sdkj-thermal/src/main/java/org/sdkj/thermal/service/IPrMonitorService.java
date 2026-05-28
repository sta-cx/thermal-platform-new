package org.sdkj.thermal.service;

import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.bo.MonitorBo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;

/**
 * 运行监控 Service
 */
public interface IPrMonitorService {

    /** 热表实时列表（户级） */
    TableDataInfo<PrHeatArchiveVo> heatList(MonitorBo bo, PageQuery pageQuery);

    /** 阀门实时列表（户级） */
    TableDataInfo<PrHeatValveArchiveVo> valveList(MonitorBo bo, PageQuery pageQuery);

    /** 单元热表实时列表 */
    TableDataInfo<PrHeatUnitHotArchiveVo> unitHeatList(MonitorBo bo, PageQuery pageQuery);

    /** 单元阀门实时列表 */
    TableDataInfo<PrHeatUnitValveArchiveVo> unitValveList(MonitorBo bo, PageQuery pageQuery);

    /** 温采器实时列表 */
    TableDataInfo<PrHeatTempArchiveVo> tempList(MonitorBo bo, PageQuery pageQuery);

    /** 聚合统计 */
    MonitorAggregateVo aggregate(MonitorBo bo);
}
