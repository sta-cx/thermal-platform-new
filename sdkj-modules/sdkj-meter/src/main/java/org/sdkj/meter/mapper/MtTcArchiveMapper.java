package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtTcArchive;
import org.sdkj.meter.domain.vo.MtTcArchiveVo;

import java.util.List;

/**
 * 温控器档案 Mapper
 * 迁移自旧系统 MtTcArchiveMapper
 */
public interface MtTcArchiveMapper extends BaseMapperPlus<MtTcArchive, MtTcArchiveVo> {

    /** 删除仪表匹配记录 */
    int deleteMeterMatch(@Param("archiveId") Long archiveId);

    /** 新增档案时写入 mt_meter_match */
    int insertMeterToAgent(@Param("entity") MtTcArchive entity);

    /** 查询所有启用的温控器 */
    List<MtTcArchiveVo> selectAllEnabled();

    /** 级联更新 pr_heat_temp_archive 的 meter_arc_name */
    int syncNameToHeatTempArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

}
