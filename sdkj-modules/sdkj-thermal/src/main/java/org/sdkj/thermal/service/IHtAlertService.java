package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.vo.HtAlertVo;

import java.util.List;
import java.util.Map;

/**
 * 报警记录 Service 接口
 * 迁移自旧系统 HtAlertService
 */
public interface IHtAlertService extends IService<HtAlert> {

    /**
     * 分页查询报警记录列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<HtAlertVo> selectPageList(LambdaQueryWrapper<HtAlert> lqw, PageQuery pageQuery);

    /**
     * 查询仪表的异常报警列表
     * @param meterId 仪表ID
     * @return 异常报警列表
     */
    List<HtAlertVo> selectAbnormalAlarmList(String meterId);

    /**
     * 按报警类型统计数量
     * @param companyId 公司ID
     * @return 类型统计列表
     */
    List<Map<String, Object>> selectTypeCount(String companyId);

}
