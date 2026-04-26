package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.dto.PrHouseByPayVo;
import org.sdkj.thermal.domain.dto.LtValveDataResponse;
import org.sdkj.thermal.domain.dto.YunGuDataResponse;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 户间阀门配表 Service 接口
 */
public interface IPrHeatValveArchiveService extends IService<PrHeatValveArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatValveArchiveVo selectById(Serializable id);

    /**
     * 分页查询户间阀门配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（房屋ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                        String unit, String search, String parentId,
                                                        PageQuery pageQuery);

    /**
     * 批量设置阀门开关/查询/制动状态
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param valveStatus 阀门状态: "1"→开(100), "2"→关(0), "3"→开度, "4"→查询, "5"→制动, "51"→特殊
     * @return 是否成功
     */
    boolean batchSetValveStatus(List<PrHouseByPayVo> houseList, String valveStatus);

    /**
     * 批量设置阀门开度
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param opening 开度值
     * @return 是否成功
     */
    boolean batchSetValveOpening(List<PrHouseByPayVo> houseList, String opening);

    /**
     * 批量设置上报周期
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param interval 间隔
     * @param unit 单位
     * @param valid 有效时间
     * @return 是否成功
     */
    boolean batchSetValveCycle(List<PrHouseByPayVo> houseList, String interval, String unit, String valid);

    /**
     * 查询全部阀门信息（用于导出）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 阀门配表列表
     */
    List<PrHeatValveArchiveVo> listAll(String companyId, String orgId);

    /**
     * 导入阀门配表
     * @param file Excel 文件
     * @return 导入结果
     */
    R<Void> importValveArchive(MultipartFile file) throws IOException;

    // ========== 第三方 API（云谷/新奥） ==========

    /**
     * 云谷阀门控制：按 manuId（meterNum）创建任务下发
     * @param manuId    设备编号
     * @param value     开度值（0-100）
     * @return true=下发成功
     */
    boolean yunguValveControl(String manuId, int value);

    /**
     * 云谷批量数据同步：按 manuId 列表查询热表+阀门数据
     * @param manuIdList 设备编号列表（最多100个）
     * @return 云谷格式响应列表
     */
    List<YunGuDataResponse> yunguBatchSync(List<String> manuIdList);

    /**
     * 新奥阀门数据查询
     * @param meterNums 仪表编号列表（最多50个）
     * @return 阀门数据响应列表
     */
    List<LtValveDataResponse> getLTValveData(List<String> meterNums);

    // ========== 卡表管理 ==========

    /**
     * 卡表分页查询
     */
    TableDataInfo<PrHeatValveArchiveVo> pageListHeatCard(String companyId, String orgId, String buildingId,
                                                          String unit, String meterArcCode, String payStatus,
                                                          String search, String parentId, String writeCardStatus,
                                                          PageQuery pageQuery);

    /**
     * 更新写卡状态
     */
    boolean updateValveStatus(String id);

    // ========== 设备查询 ==========

    /**
     * 按表号查询设备
     */
    List<PrHeatValveArchiveVo> queryMeterByMeterNum(String meterNum, String orgId, String code);

    /**
     * 按阀门号查询
     */
    List<PrHeatValveArchiveVo> queryValveByMeterNum(String meterNum);

    /**
     * 按房屋ID查卡阀
     */
    List<PrHeatValveArchiveVo> queryCardMeterByHouseId(String houseId);

    /**
     * 按房号查卡阀
     */
    List<PrHeatValveArchiveVo> queryCardMeterByRoomNum(String orgId, String buildingId, String unitCode, String search);

    /**
     * 按房屋ID获取阀门数据
     */
    PrHeatValveArchiveVo getValveDataByHouseId(String houseId);

    // ========== 信息同步 ==========

    /**
     * 同步户阀信息到采集平台
     */
    boolean valveInformationSynchronization(String orgId, String companyId);

    /**
     * 获取同步数据列表（用于导出Excel）
     */
    List<PrHeatValveArchiveVo> listSyncData(String companyId, String orgId);

    // ========== 蓝牙控制日志 ==========

    /**
     * 蓝牙阀门控制日志
     */
    void insertValveControlLogByBluetooth(String meterNum, String type, String opening);

    // ========== 一键新增 ==========

    /**
     * 新增用户和阀门信息（事务性：PrHouse + PrFamily + PrHeatValveArchive）
     */
    String insertUserAndValveInfo(String companyId, String orgId, String orgName,
                                  String buildingId, String buildingName, String unitCode,
                                  String roomNum, String floor, String otherCode, String payStatus,
                                  String userName, String phone,
                                  String gfloorArea, String nfloorArea, String heatingArea,
                                  String meterNum);
}
