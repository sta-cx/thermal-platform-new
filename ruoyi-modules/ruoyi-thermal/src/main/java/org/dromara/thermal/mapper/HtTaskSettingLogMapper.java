package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtTaskSettingLog;
import org.dromara.thermal.domain.vo.HtTaskSettingLogItemVo;
import org.dromara.thermal.domain.vo.HtTaskSettingLogVo;

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
