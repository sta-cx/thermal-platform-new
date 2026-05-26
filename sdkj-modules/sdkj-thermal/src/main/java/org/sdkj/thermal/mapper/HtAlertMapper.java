package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.vo.HtAlertVo;

import java.util.Date;
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
     * @return 类型统计列表
     */
    List<Map<String, Object>> selectTypeCount();

    /**
     * 按报警类型和DTU维度统计数量
     * @return DTU维度类型统计列表
     */
    List<Map<String, Object>> selectTypeCountDtu();

    /** 把指定告警标记为"维修中"，关联到 repairRecordId */
    default int markInMaintenance(List<Long> alertIds, String repairRecordId, Long userId) {
        return this.update(null, new LambdaUpdateWrapper<HtAlert>()
            .in(HtAlert::getId, alertIds)
            .set(HtAlert::getInMaintenance, repairRecordId)
            .set(HtAlert::getUpdateBy, userId)
            .set(HtAlert::getUpdateTime, new Date()));
    }

    /** 取消"维修中"标记（repair 删除时反向操作） */
    default int clearInMaintenance(String repairRecordId, Long userId) {
        return this.update(null, new LambdaUpdateWrapper<HtAlert>()
            .eq(HtAlert::getInMaintenance, repairRecordId)
            .set(HtAlert::getInMaintenance, null)
            .set(HtAlert::getUpdateBy, userId)
            .set(HtAlert::getUpdateTime, new Date()));
    }

}
