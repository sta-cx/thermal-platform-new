package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtTcValve;
import org.sdkj.meter.domain.vo.MtTcValveVo;

import java.util.List;

/**
 * 阀门档案 Mapper
 * 迁移自旧系统 MtTcValveMapper
 */
public interface MtTcValveMapper extends BaseMapperPlus<MtTcValve, MtTcValveVo> {

    /** 删除仪表匹配记录 */
    int deleteMeterMatch(@Param("archiveId") Long archiveId);

    /** 新增档案时写入 mt_meter_match */
    int insertMeterToAgent(@Param("entity") MtTcValve entity);

    /** 查询所有已建立 mt_meter_match 关联的阀门 */
    List<MtTcValveVo> selectAllMatchedValves();

    /** 级联更新 pr_heat_valve_archive 的 meter_arc_name */
    int syncNameToHeatValveArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

    /** 级联更新 pr_heat_unit_valve_archive 的 meter_arc_name */
    int syncNameToHeatUnitValveArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

}
