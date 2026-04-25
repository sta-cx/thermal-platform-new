package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.dto.PrHeatVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 房屋热表配表 Service 接口
 */
public interface IPrHeatArchiveService extends IService<PrHeatArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatArchiveVo selectById(Serializable id);

    /**
     * 分页查询房屋热表配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param search 搜索关键字（表号/档案名称）
     * @param archiveId 档案ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                   String unitCode, String search, String archiveId,
                                                   PageQuery pageQuery);

    /**
     * 查询公司下所有热表档案
     * @param companyId 公司ID
     * @return 热表档案列表
     */
    List<PrHeatArchiveVo> queryCompanyHeat(String companyId);

    /**
     * 仪表更换（涉及余额转移）
     * @param newHeatArchive 新表信息
     * @return 是否成功
     */
    boolean replaceHeatMeter(PrHeatArchive newHeatArchive);

    /**
     * 仪表充值
     * @param heatArchive 热表档案信息
     * @param paymentMethod 支付方式
     * @return 交易记录信息
     */
    Object recharge(PrHeatArchive heatArchive, String paymentMethod);

    /**
     * 手动调控
     * @param prHeatVoList 仪表列表
     * @param switch1 开关（true=开，false=关）
     * @param scale 数值
     * @param adjust 调控类型
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @param intervall 上报间隔
     * @param unit 间隔单位
     * @param duration 有效时长
     * @return 是否成功
     */
    boolean manualControl(List<PrHeatVo> prHeatVoList, boolean switch1, Integer scale, String adjust,
                         String orgId, String companyId, String intervall, String unit, String duration);

    /**
     * 实时数据查询
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param search 搜索关键字
     * @param pageQuery 分页参数
     * @return 实时数据列表
     */
    TableDataInfo<PrHeatArchiveVo> realTimeData(String companyId, String orgId, String buildingId,
                                                 String unitCode, String search, PageQuery pageQuery);

    /**
     * 综合查询
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param search 搜索关键字
     * @param moneyType 金额类型
     * @param valveStatus 阀门状态
     * @param pageQuery 分页参数
     * @return 综合查询结果
     */
    TableDataInfo<PrHeatArchiveVo> zonghe(String companyId, String orgId, String buildingId,
                                          String unitCode, String search, String moneyType,
                                          String valveStatus, PageQuery pageQuery);

    /**
     * 巡测
     * @param prHeatVoList 仪表列表
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @return 是否成功
     */
    boolean xunce(List<PrHeatVo> prHeatVoList, String orgId, String companyId);

    /**
     * 设置阀门组号
     * @param prHeatVoList 阀门列表
     * @param commandParam 组号参数
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @return 是否成功
     */
    boolean setValveGroupParam(List<PrHeatVo> prHeatVoList, String commandParam, String orgId, String companyId);

    /**
     * 查询仪表
     * @param search 搜索关键字
     * @param companyId 公司ID
     * @return 仪表列表
     */
    List<PrHeatArchiveVo> findMeter(String search, String companyId);

    /**
     * 计算余额及用量
     * @param id 配表ID
     * @return 上月月末读数
     */
    BigDecimal calculate(String id);

    /**
     * 导出全部配表数据
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 导出数据列表
     */
    List<PrHeatArchiveVo> exportAll(String companyId, String orgId);

    /**
     * 导入修改配表
     * @param uuid 导入批次号
     * @return 是否成功
     */
    boolean importData(String uuid);

    /**
     * 收费明细报表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param search 搜索关键字
     * @return 报表数据
     */
    List<PrHeatArchiveVo> selectReport(String companyId, String orgId, String buildingId,
                                       String unitCode, String startTime, String endTime, String search);

    /**
     * 仪表历史数据查询报表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param search 搜索关键字
     * @return 报表数据
     */
    List<PrHeatArchiveVo> selectMeterReport(String companyId, String orgId, String buildingId,
                                            String unitCode, String startTime, String endTime, String search);
}
