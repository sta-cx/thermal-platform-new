package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.vo.HtAlertVo;

import java.util.List;
import java.util.Map;

/**
 * 报警记录 Mapper
 * 迁移自旧系统 HtAlertMapper
 */
@OrgPermission
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

    /**
     * 按报警类型和DTU维度统计数量
     * @param companyId 公司ID
     * @return DTU维度类型统计列表
     */
    List<Map<String, Object>> selectTypeCountDtu(@Param("companyId") String companyId);

}
