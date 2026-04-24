package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatReading;
import org.sdkj.thermal.domain.vo.PrHeatReadingVo;

import java.io.Serializable;

public interface IPrHeatReadingService extends IService<PrHeatReading> {

    PrHeatReadingVo selectById(Serializable id);

    TableDataInfo<PrHeatReadingVo> selectPageList(String companyId, String orgId, String buildingId,
                                                   String unitCode, String meterArcCode, String search,
                                                   String startTime, String endTime, String type,
                                                   String valveType, String parentId, PageQuery pageQuery);

    TableDataInfo<PrHeatReadingVo> selectPageListTrend(String companyId, String orgId, String startTime,
                                                        String endTime, PageQuery pageQuery);
}
