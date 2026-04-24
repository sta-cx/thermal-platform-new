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
}
