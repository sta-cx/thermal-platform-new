package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtTaskSettingLog;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogItemVo;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogVo;

import java.util.List;

/**
 * 调控设定日志Mapper
 */
public interface HtTaskSettingLogMapper extends BaseMapperPlus<HtTaskSettingLog, HtTaskSettingLogVo> {

    /**
     * 根据主表ID查询子表明细
     */
    List<HtTaskSettingLogItemVo> selectItemsByMainId(@Param("mainId") String mainId);
}
