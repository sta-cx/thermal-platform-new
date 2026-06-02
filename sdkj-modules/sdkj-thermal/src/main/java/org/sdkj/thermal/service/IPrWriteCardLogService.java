package org.sdkj.thermal.service;

import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.bo.PrWriteCardLogBo;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;

public interface IPrWriteCardLogService {

    /** 写卡成功后插入日志，返回主键 */
    Long insertWriteCardLog(PrWriteCardLogBo bo);

    /** 写卡记录分页查询（两跳筛选 + enrich orgName/operatorName） */
    TableDataInfo<PrValveOperationLogVo> selectPageList(PrWriteCardLogBo bo, PageQuery pageQuery);
}
