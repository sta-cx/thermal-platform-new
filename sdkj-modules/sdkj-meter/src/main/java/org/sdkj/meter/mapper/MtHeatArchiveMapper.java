package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtHeatArchive;
import org.sdkj.meter.domain.vo.MtHeatArchiveVo;

/**
 * 热力表档案 Mapper
 * 迁移自旧系统 MtHeatArchiveMapper
 */
public interface MtHeatArchiveMapper extends BaseMapperPlus<MtHeatArchive, MtHeatArchiveVo> {

    /** 删除仪表匹配记录 */
    int deleteMeterMatch(@Param("archiveId") Long archiveId);

    /** 新增档案时写入 mt_meter_match */
    int insertMeterToAgent(@Param("entity") MtHeatArchive entity);

    /** 级联更新 pr_heat_hot_archive 的 meter_arc_name */
    int syncNameToHeatHotArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

    /** 级联更新 pr_heat_unit_hot_archive 的 meter_arc_name */
    int syncNameToHeatUnitHotArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

}
