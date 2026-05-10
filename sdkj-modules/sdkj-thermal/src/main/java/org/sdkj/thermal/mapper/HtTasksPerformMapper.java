package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.ValveData;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 调控执行记录Mapper
 */
public interface HtTasksPerformMapper extends BaseMapperPlus<HtTasksPerform, HtTasksPerformVo> {

    /**
     * 根据仪表ID分页查询执行记录
     */
    List<HtTasksPerformVo> selectByMeterId(String meterId);

    /**
     * 根据仪表ID查询执行记录详情
     */
    List<HtTasksPerformVo> selectByMeterIdDetail(String meterId);

    // ==================== 设备档案更新方法 ====================

    /**
     * 更新阀门档案
     */
    void updateValveArchive(@Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                            @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                            @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                            @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                            @Param("csq") Integer csq, @Param("reportInterval") Integer reportInterval,
                            @Param("reportingUnit") Integer reportingUnit, @Param("validTime") Integer validTime,
                            @Param("totalDegree") Integer totalDegree, @Param("dtuStatus") Integer dtuStatus,
                            @Param("meterInfo") String meterInfo, @Param("insFlow") BigDecimal insFlow);

    /**
     * 批量更新阀门档案
     */
    void updateValveArchiveBatch(@Param("meterInfos") List<ValveData> meterInfos);

    /**
     * 更新单元阀门档案
     */
    void updateUnitiValveArchive(@Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                                 @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                                 @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                                 @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                                 @Param("csq") Integer csq, @Param("reportInterval") Integer reportInterval,
                                 @Param("reportingUnit") Integer reportingUnit, @Param("validTime") Integer validTime,
                                 @Param("totalDegree") Integer totalDegree, @Param("dtuStatus") Integer dtuStatus,
                                 @Param("meterInfo") String meterInfo);

    /**
     * 批量更新单元阀门档案
     */
    void updateUnitiValveArchiveBatch(@Param("meterDataList") List<ValveData> valveDataList);

    /**
     * 更新温度档案
     */
    void updateTempArchive(@Param("meterNum") String meterNum, @Param("temperature") BigDecimal temperature,
                           @Param("humi") Integer humi, @Param("voltage") BigDecimal voltage,
                           @Param("readTime") String readTime, @Param("reportingInterval") Integer reportingInterval,
                           @Param("reportingUnit") Integer reportingUnit, @Param("validTime") Integer validTime,
                           @Param("csq") Integer csq, @Param("totalDegree") Integer totalDegree,
                           @Param("reportSuccNum") Integer reportSuccNum, @Param("collectInterval") Integer collectInterval,
                           @Param("collectUnit") Integer collectUnit, @Param("collectTime") Integer collectTime,
                           @Param("collectDate") String collectDate, @Param("movPlace") String movPlace,
                           @Param("meterInfo") String meterInfo, @Param("valveStatus") String valveStatus);

    /**
     * 更新热力档案
     */
    void updateHotArchive(@Param("meterNum") String meterNum, @Param("outTemp") BigDecimal outTemp,
                          @Param("inTemp") BigDecimal inTemp, @Param("voltage") BigDecimal voltage,
                          @Param("readTime") String readTime, @Param("voltageStatus") Integer voltageStatus,
                          @Param("valveStatus") Integer valveStatus, @Param("totalFlow") BigDecimal totalFlow,
                          @Param("csq") Integer csq, @Param("totalHeat") BigDecimal totalHeat,
                          @Param("flowRate") BigDecimal flowRate, @Param("currentFlow") BigDecimal currentFlow,
                          @Param("thermalPower") BigDecimal thermalPower, @Param("currentPower") BigDecimal currentPower,
                          @Param("dtuStatus") Integer dtuStatus, @Param("status1") String status1,
                          @Param("meterInfo") String meterInfo);

    /**
     * 更新单元热力档案
     */
    void updateUnitHotArchive(@Param("meterNum") String meterNum, @Param("outTemp") BigDecimal outTemp,
                              @Param("inTemp") BigDecimal inTemp, @Param("voltage") BigDecimal voltage,
                              @Param("readTime") String readTime, @Param("voltageStatus") Integer voltageStatus,
                              @Param("valveStatus") Integer valveStatus, @Param("totalFlow") BigDecimal totalFlow,
                              @Param("csq") Integer csq, @Param("totalHeat") BigDecimal totalHeat,
                              @Param("flowRate") BigDecimal flowRate, @Param("currentFlow") BigDecimal currentFlow,
                              @Param("thermalPower") BigDecimal thermalPower, @Param("currentPower") BigDecimal currentPower,
                              @Param("dtuStatus") Integer dtuStatus, @Param("status1") String status1,
                              @Param("meterInfo") String meterInfo);

    /**
     * 更新DTU状态
     */
    void updateDtu(@Param("meterNum") String meterNum, @Param("dtuStatus") Integer dtuStatus,
                   @Param("meterInfo") String meterInfo);

    /**
     * 更新单元DTU状态
     */
    void updateDtuUnit(@Param("meterNum") String meterNum, @Param("dtuStatus") Integer dtuStatus,
                       @Param("meterInfo") String meterInfo);

    /**
     * 更新热力DTU状态
     */
    void updateDtuHot(@Param("meterNum") String meterNum, @Param("dtuStatus") Integer dtuStatus);

    /**
     * 更新单元热力DTU状态
     */
    void updateDtuHotUnit(@Param("meterNum") String meterNum, @Param("dtuStatus") Integer dtuStatus);

    // ==================== 回读数据插入方法 ====================

    /**
     * 插入阀门回读数据
     */
    void insertReading(@Param("id") Long id, @Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                       @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                       @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                       @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                       @Param("reportInterval") Integer reportInterval, @Param("reportingUnit") Integer reportingUnit,
                       @Param("validTime") Integer validTime, @Param("totalDegree") Integer totalDegree,
                       @Param("csq") Integer csq, @Param("rouseNum") Integer rouseNum, @Param("duration") Integer duration,
                       @Param("insFlow") BigDecimal insFlow);

    /**
     * 批量插入回读数据
     */
    void batchInsertReading(@Param("valveDataList") List<ValveData> valveDataList);

    /**
     * 插入YT系列阀门回读数据
     */
    void insertYTReading(@Param("id") Long id, @Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                         @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                         @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                         @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                         @Param("reportInterval") Integer reportInterval, @Param("reportingUnit") Integer reportingUnit,
                         @Param("csq") Integer csq, @Param("valveModel") String valveModel,
                         @Param("userSetTemp") BigDecimal userSetTemp, @Param("roomTemp") BigDecimal roomTemp,
                         @Param("avgTemp") BigDecimal avgTemp, @Param("workTime") Integer workTime,
                         @Param("totalOpenTime") Integer totalOpenTime, @Param("coldFlg") Integer coldFlg,
                         @Param("wkqLock") Integer wkqLock, @Param("tempLow") Integer tempLow,
                         @Param("tempHigh") Integer tempHigh, @Param("insFlow") BigDecimal insFlow);

    /**
     * 插入温度回读数据
     */
    void insertTempReading(@Param("id") Long id, @Param("meterNum") String meterNum, @Param("temperature") BigDecimal temperature,
                           @Param("humi") Integer humi, @Param("voltage") BigDecimal voltage,
                           @Param("readTime") String readTime, @Param("reportingInterval") Integer reportingInterval,
                           @Param("reportingUnit") Integer reportingUnit, @Param("validTime") Integer validTime,
                           @Param("csq") Integer csq, @Param("totalDegree") Integer totalDegree,
                           @Param("reportSuccNum") Integer reportSuccNum, @Param("collectInterval") Integer collectInterval,
                           @Param("collectUnit") Integer collectUnit, @Param("collectTime") Integer collectTime,
                           @Param("collectDate") String collectDate, @Param("movPlace") String movPlace,
                           @Param("valveStatus") String valveStatus);

    /**
     * 插入热力回读数据
     */
    void insertHotReading(@Param("id") Long id, @Param("meterNum") String meterNum, @Param("outTemp") BigDecimal outTemp,
                          @Param("inTemp") BigDecimal inTemp, @Param("voltage") BigDecimal voltage,
                          @Param("readTime") String readTime, @Param("voltageStatus") Integer voltageStatus,
                          @Param("valveStatus") Integer valveStatus, @Param("totalFlow") BigDecimal totalFlow,
                          @Param("csq") Integer csq, @Param("totalHeat") BigDecimal totalHeat,
                          @Param("flowRate") BigDecimal flowRate, @Param("currentFlow") BigDecimal currentFlow,
                          @Param("thermalPower") BigDecimal thermalPower, @Param("currentPower") BigDecimal currentPower,
                          @Param("status1") String status1);

    // ==================== 房屋/单元状态更新方法 ====================

    /**
     * 更新房屋阀门状态
     */
    void updateValveHouse(@Param("meterNum") String meterNum, @Param("actualStatus") String actualStatus,
                          @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                          @Param("meterInfo") String meterInfo);

    /**
     * 通过GUID更新房屋阀门状态
     */
    void updateValveHouseByGuid(@Param("guid") String guid, @Param("meterNum") String meterNum);

    /**
     * 更新单元阀门状态
     */
    void updateValveUnit(@Param("meterNum") String meterNum, @Param("actualStatus") String actualStatus,
                         @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                         @Param("meterInfo") String meterInfo);

    /**
     * 通过GUID更新单元阀门状态
     */
    void updateValveUnitByGuid(@Param("guid") String guid, @Param("meterNum") String meterNum);

    /**
     * 更新房屋温度
     */
    void updateTempHouse(@Param("meterNum") String meterNum, @Param("temperature") BigDecimal temperature);

    /**
     * 更新房屋流量
     */
    void updateHotHouse(@Param("meterNum") String meterNum, @Param("flowRate") BigDecimal flowRate);

    // ==================== 阀门档案状态更新方法 ====================

    /**
     * 通过GUID更新阀门档案状态
     */
    void updateValveArchiveStatusByGuid(@Param("guid") String guid, @Param("meterNum") String meterNum);

    /**
     * 通过GUID更新单元阀门档案状态
     */
    void updateUnitValveArchiveStatusByGuid(@Param("guid") String guid, @Param("meterNum") String meterNum);

    /**
     * 通过GUID更新命令阀门档案状态
     */
    void updateCommandValveArchiveStatusByGuid(@Param("guid") String guid, @Param("meterNum") String meterNum);

    /**
     * 通过GUID更新命令单元阀门档案状态
     */
    void updateCommandUnitiValveArchiveStatusByGuid(@Param("guid") String guid, @Param("meterNum") String meterNum);

    /**
     * 更新命令阀门档案
     */
    void updateCommandValveArchive(@Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                                   @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                                   @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                                   @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                                   @Param("csq") Integer csq, @Param("reportInterval") Integer reportInterval,
                                   @Param("reportingUnit") Integer reportingUnit, @Param("validTime") Integer validTime,
                                   @Param("totalDegree") Integer totalDegree, @Param("dtuStatus") Integer dtuStatus,
                                   @Param("meterInfo") String meterInfo);

    /**
     * 更新命令单元阀门档案
     */
    void updateCommandUnitiValveArchive(@Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                                        @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                                        @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                                        @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                                        @Param("csq") Integer csq, @Param("reportInterval") Integer reportInterval,
                                        @Param("reportingUnit") Integer reportingUnit, @Param("validTime") Integer validTime,
                                        @Param("totalDegree") Integer totalDegree, @Param("dtuStatus") Integer dtuStatus,
                                        @Param("meterInfo") String meterInfo);

    // ==================== 其他更新方法 ====================

    /**
     * 更新热力阀门范围状态
     */
    void updateHeatValveScopeStatus(@Param("meterNum") String meterNum, @Param("meterInfo") String meterInfo,
                                    @Param("valveStatus") int valveStatus);

    /**
     * 批量更新阀门范围状态
     */
    boolean updateValveScopeStatusList(@Param("list") List<HtTasksPerform> htTasksPerformList);

    /**
     * 批量插入任务执行记录
     */
    void insertList(@Param("list") List<HtTasksPerform> list);

    /**
     * 批量更新任务执行记录
     */
    void updateListById(@Param("list") List<HtTasksPerform> htTasksPerforms);

    /**
     * 批量更新任务执行记录（无线模式）
     */
    void updateListByIdAndRadio(@Param("list") List<HtTasksPerform> htTasksPerforms);

    /**
     * 通过回控更新
     */
    void updateByreturnControl(@Param("guid") String guid, @Param("meterNum") String meterNum,
                               @Param("command") int command, @Param("valveStatus") int valveStatus);

    /**
     * 通过回控更新（无线模式）
     */
    void updateByreturnControlByRadio(@Param("guid") String guid, @Param("meterNum") String meterNum,
                                      @Param("command") int command, @Param("valveStatus") int valveStatus);

    /**
     * 更新范围状态
     */
    void updateHtScope(@Param("guid") String guid, @Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 更新温度档案状态
     */
    void updataTemp(@Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 更新温度档案状态S
     */
    void updataTempS(@Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 更新热力阀门
     */
    void updataHeatValve(@Param("meterNum") String meterNum, @Param("channelNum") int channelNum,
                         @Param("meterInfo") String meterInfo, @Param("valveStatus") int valveStatus);

    /**
     * 更新热力阀门S
     */
    void updataHeatValveS(@Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 更新单元阀门S
     */
    void updataUnitValveS(@Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 更新单元阀门
     */
    void updataUnitValve(@Param("meterNum") String meterNum, @Param("channelNum") int channelNum,
                         @Param("meterInfo") String meterInfo, @Param("valveStatus") int valveStatus);

    /**
     * 更新热力DTU
     */
    void updataHeatDtu(@Param("dtuNum") String dtuNum, @Param("channelNum") int channelNum,
                       @Param("valveStatus") int valveStatus);

    /**
     * 更新热力DTU S
     */
    void updataHeatDtuS(@Param("dtuNum") String dtuNum, @Param("valveStatus") int valveStatus);

    /**
     * 插入热力告警
     */
    void inserHtAlert(@Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 插入单元热力告警
     */
    void inserHtAlertUnit(@Param("meterNum") String meterNum, @Param("valveStatus") int valveStatus);

    /**
     * 通过DTU状态更新阀门
     */
    void updateByDtuStatusV(@Param("meterNum") String meterNum);

    /**
     * 通过DTU状态更新单元阀门
     */
    void updateByDtuStatusV1(@Param("meterNum") String meterNum);

    /**
     * 通过DTU状态更新热力
     */
    void updateByDtuStatusH(@Param("meterNum") String meterNum);

    /**
     * 通过DTU状态更新单元热力
     */
    void updateByDtuStatusH1(@Param("meterNum") String meterNum);

    /**
     * 更新YT阀门档案31D1
     */
    void updateYTValveArchive31D1(@Param("meterNum") String meterNum, @Param("valveTime") String valveTime,
                                  @Param("reportInterval") Integer reportInterval, @Param("reportingUnit") Integer reportingUnit,
                                  @Param("coldFlg") Integer coldFlg, @Param("wkqLock") Integer wkqLock,
                                  @Param("tempLow") Integer tempLow, @Param("tempHigh") Integer tempHigh,
                                  @Param("meterInfo") String meterInfo);

    /**
     * 更新YT阀门档案1F90
     */
    void updateYTValveArchive1F90(@Param("meterNum") String meterNum, @Param("valveTime") String valveTime,
                                  @Param("userSetTemp") BigDecimal userSetTemp, @Param("roomTemp") BigDecimal roomTemp,
                                  @Param("avgTemp") BigDecimal avgTemp, @Param("workTime") Integer workTime,
                                  @Param("totalOpenTime") Integer totalOpenTime, @Param("meterInfo") String meterInfo);

    /**
     * 更新YT阀门档案2190
     */
    void updateYTValveArchive2190(@Param("meterNum") String meterNum, @Param("valveStatus") String valveStatus,
                                  @Param("settingStatus") String settingStatus, @Param("actualStatus") String actualStatus,
                                  @Param("inTemp") BigDecimal inTemp, @Param("outTemp") BigDecimal outTemp,
                                  @Param("voltage") BigDecimal voltage, @Param("valveTime") String valveTime,
                                  @Param("csq") Integer csq, @Param("reportInterval") Integer reportInterval,
                                  @Param("reportingUnit") Integer reportingUnit, @Param("valveModel") String valveModel,
                                  @Param("userSetTemp") BigDecimal userSetTemp, @Param("roomTemp") BigDecimal roomTemp,
                                  @Param("avgTemp") BigDecimal avgTemp, @Param("workTime") Integer workTime,
                                  @Param("totalOpenTime") Integer totalOpenTime, @Param("coldFlg") Integer coldFlg,
                                  @Param("wkqLock") Integer wkqLock, @Param("tempLow") Integer tempLow,
                                  @Param("tempHigh") Integer tempHigh, @Param("meterInfo") String meterInfo);

    /**
     * 通过DTU更新热力档案
     */
    void updateHotArchiveDtu(@Param("dtuNum") String dtuNum, @Param("status") Integer status,
                             @Param("collectDate") String collectDate);

    /**
     * 通过DTU更新单元热力档案
     */
    void updateUnitHotArchiveDtu(@Param("dtuNum") String dtuNum, @Param("status") Integer status,
                                 @Param("collectDate") String collectDate);

    /**
     * 通过DTU更新阀门档案
     */
    void updateValveArchiveDtu(@Param("dtuNum") String dtuNum, @Param("status") Integer status,
                               @Param("collectDate") String collectDate);

    /**
     * 通过DTU更新单元阀门档案
     */
    void updateUnitiValveArchiveDtu(@Param("dtuNum") String dtuNum, @Param("status") Integer status,
                                    @Param("collectDate") String collectDate);

    /**
     * 更新DTU档案
     */
    void updateDtuArchive(@Param("dtuNum") String dtuNum, @Param("status") Integer status,
                          @Param("collectDate") String collectDate);

    /**
     * 插入阀门开关日志
     */
    void insertValveOCLog(@Param("list") List<HtTasksPerform> list);

    /**
     * 查询阀门信息
     */
    List<Object> selectValveInfo(@Param("list") List<String> meterIds);

    /**
     * 查询列表N
     */
    List<HtTasksPerform> selectListN();

    /**
     * 查询最新的任务执行记录
     */
    HtTasksPerform queryLatestTasksPerform(@Param("tasksId") Long tasksId, @Param("groupId") String groupId);

    /**
     * 获取任务最后执行的回报率
     */
    Integer getTaskLastPerformReportRate(@Param("tasksId") Long tasksId, @Param("groupId") String groupId);

    /**
     * 查询阀门档案（关联房屋信息）
     * 用于控制反馈时判断缴费状态和是否特殊户
     */
    PrHeatValveArchive querHeatValveArchive(@Param("meterNum") String meterNum);
}
