package org.sdkj.thermal.service;

import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.bo.ChangeHistoryQueryBo;
import org.sdkj.thermal.domain.bo.MonitorBo;
import org.sdkj.thermal.domain.bo.MonitorManualControlBo;
import org.sdkj.thermal.domain.bo.MonitorOtherCodeReadBo;
import org.sdkj.thermal.domain.bo.MonitorOtherCodeWriteBo;
import org.sdkj.thermal.domain.bo.MonitorSetValveGroupBo;
import org.sdkj.thermal.domain.bo.MonitorXunceBo;
import org.sdkj.thermal.domain.bo.SpecialHouseBo;
import org.sdkj.thermal.domain.bo.StopSupplyBo;
import org.sdkj.thermal.domain.vo.ChangeHistoryVo;
import org.sdkj.thermal.domain.vo.HouseDeviceDetailVo;
import org.sdkj.thermal.domain.vo.MonitorAggregateVo;
import org.sdkj.thermal.domain.vo.MonitorExportVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;

import java.util.List;
import java.util.Map;

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

    /** 生成虚拟温采器（为无设备的房屋创建虚拟记录） */
    int generateVirtualDevice(String orgId);

    /** 导出户间数据 */
    List<MonitorExportVo> exportList(MonitorBo bo);

    // ========== M-BE 端点 ==========

    /** 房屋设备详情 */
    HouseDeviceDetailVo houseDetail(Long houseId);

    /** 手动阀门控制（委托 heat-archive） */
    R<Void> manualControl(MonitorManualControlBo bo);

    /** 巡测（委托 heat-archive） */
    R<Void> xunce(MonitorXunceBo bo);

    /** 5参数设置（委托 heat-archive） */
    R<Void> setValveGroup(MonitorSetValveGroupBo bo);

    /** 第三方编码批量读取 */
    Map<String, String> readOtherCode(MonitorOtherCodeReadBo bo);

    /** 第三方编码批量写入 */
    void writeOtherCode(MonitorOtherCodeWriteBo bo);

    /** 变更历史查询 */
    List<ChangeHistoryVo> changeHistory(ChangeHistoryQueryBo bo);

    /** 特殊户设定/取消 */
    void specialHouse(SpecialHouseBo bo);

    /** 停供设定/取消 */
    void stopSupply(StopSupplyBo bo);
}
