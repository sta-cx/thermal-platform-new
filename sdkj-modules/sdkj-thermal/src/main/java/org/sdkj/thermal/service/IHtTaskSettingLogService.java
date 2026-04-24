package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTaskSettingLog;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogVo;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogItemVo;

import java.util.List;

/**
 * 调控设定日志服务接口
 */
public interface IHtTaskSettingLogService extends IService<HtTaskSettingLog> {

    /**
     * 分页查询设定日志
     */
    TableDataInfo<HtTaskSettingLogVo> selectPageList(LambdaQueryWrapper<HtTaskSettingLog> lqw, PageQuery pageQuery);

    /**
     * 根据主表ID查询子表明细
     */
    List<HtTaskSettingLogItemVo> selectItemsByMainId(String mainId);
}
