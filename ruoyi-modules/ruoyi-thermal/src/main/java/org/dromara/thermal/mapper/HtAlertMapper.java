package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtAlert;
import org.dromara.thermal.domain.vo.HtAlertVo;

import java.util.List;
import java.util.Map;

/**
 * 报警记录 Mapper
 * 迁移自旧系统 HtAlertMapper
 */
public interface HtAlertMapper extends BaseMapperPlus<HtAlert, HtAlertVo> {

    /**
     * 查询仪表的异常报警列表
     * @param meterId 仪表ID
     * @return 异常报警列表
     */
    List<HtAlertVo> selectAbnormalAlarmList(@Param("meterId") String meterId);

    /**
     * 按报警类型统计数量
     * @param companyId 公司ID
     * @return 类型统计列表
     */
    List<Map<String, Object>> selectTypeCount(@Param("companyId") String companyId);

}
