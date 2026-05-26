package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrHeatDaily;
import org.sdkj.thermal.domain.vo.PrHeatDailyVo;

import java.util.Date;
import java.util.List;

/**
 * 热表日记录 Mapper
 */
@OrgPermission
public interface PrHeatDailyMapper extends BaseMapperPlus<PrHeatDaily, PrHeatDailyVo> {

    /**
     * 步骤1: 标记有效的抄表记录
     * 根据表号匹配，将抄表档案中能够匹配到表号的is_valid=1
     */
    boolean setIsValid(@Param("date") Date date, @Param("orgId") String orgId);

    /**
     * 步骤2a: 清空旧日表数据
     */
    boolean deleteDaily(@Param("date") Date date, @Param("orgId") String orgId);

    /**
     * 步骤2b: 生成新的日表记录
     * 将抄表档案中is_valid=1的热表数据插入到日表
     */
    boolean setHeatDaily(@Param("date") Date date, @Param("orgId") String orgId);

    /**
     * 步骤3: 更新日表单价及金额，将配表信息的当前读数作为上次读数更新到日表
     */
    boolean setSteps(@Param("date") Date date, @Param("orgId") String orgId);

    /**
     * 步骤4: 计算日表用量，计算金额
     */
    boolean setQtyStepsN(@Param("date") Date date, @Param("orgId") String orgId);

    /**
     * 步骤5: 更新配表档案的当前读数
     */
    boolean setCurrentReading(@Param("date") Date date, @Param("orgId") String orgId);

}
