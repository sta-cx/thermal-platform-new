package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrUseCardLog;
import org.sdkj.thermal.domain.vo.PrUseCardLogVo;

/**
 * 写卡日志 Service 接口
 */
public interface IPrUseCardLogService extends IService<PrUseCardLog> {

    /**
     * 分页查询写卡日志列表
     */
    TableDataInfo<PrUseCardLogVo> selectPageList(LambdaQueryWrapper<PrUseCardLog> lqw, PageQuery pageQuery);

    /**
     * 变更阀门状态
     */
    boolean changeValveStatus(Long meterId, Integer valveStatus);
}
