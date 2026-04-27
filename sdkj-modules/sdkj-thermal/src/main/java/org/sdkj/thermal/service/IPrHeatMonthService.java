package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatMonth;
import org.sdkj.thermal.domain.vo.PrHeatMonthVo;

import java.io.Serializable;

/**
 * 热表月记录 Service 接口
 */
public interface IPrHeatMonthService extends IService<PrHeatMonth> {

    /**
     * 根据ID查询月记录详情
     */
    PrHeatMonthVo selectById(Serializable id);

    /**
     * 分页查询热表月记录列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param search 搜索关键字（表号/档案名称）
     * @param isCharged 是否计费
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatMonthVo> selectPageList(String companyId, String orgId, String buildingId,
                                                 String unitCode, String search, String isCharged,
                                                 String startTime, String endTime, PageQuery pageQuery);

    /**
     * 生成热表月表数据（5步流程）
     * 1. insertPrHeatMonth - 插入月表记录
     * 2. updateStartReading - 更新起始读数
     * 3. updateQty - 更新用量
     * 4. setFee - 计算费用
     * 5. updateArrearage - 更新欠费状态
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 是否成功
     */
    boolean generateHeatMonth(String companyId, String orgId);

    /**
     * 从日表聚合生成月表数据
     * 默认生成上月数据，force=true时重算当月
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param force 是否强制重算当月
     * @return 结果消息
     */
    String setHeat(String companyId, String orgId, Boolean force);
}
