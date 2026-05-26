package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtCentratorArchive;
import org.sdkj.meter.domain.vo.MtCentratorArchiveVo;

/**
 * 集中器档案 Mapper
 * 迁移自旧系统 MtCentratorArchiveMapper
 */
public interface MtCentratorArchiveMapper extends BaseMapperPlus<MtCentratorArchive, MtCentratorArchiveVo> {

    /** 删除仪表匹配记录 */
    int deleteMeterMatch(@Param("archiveId") Long archiveId);

    /** 新增档案时写入 mt_meter_match */
    int insertMeterToAgent(@Param("entity") MtCentratorArchive entity);

}
