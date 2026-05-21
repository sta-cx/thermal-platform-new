package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;

public interface IPrValveOperationLogService extends IService<PrValveOperationLog> {
    TableDataInfo<PrValveOperationLogVo> selectPageList(PageQuery pageQuery, String orgId, String meterNum);
}
