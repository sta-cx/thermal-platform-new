package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrUseCardLog;
import org.dromara.thermal.domain.vo.PrUseCardLogVo;

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
    boolean changeValveStatus(String meterId, Integer valveStatus);
}
