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

    /**
     * 统计已分配给其他公司的仪表数量
     * @param archiveId 档案ID
     * @return 分配数量
     */
    int countAllocatedToOtherCompany(@Param("archiveId") Long archiveId);

    /**
     * 删除仪表匹配记录
     * @param archiveId 档案ID
     * @return 删除数量
     */
    int deleteMeterMatch(@Param("archiveId") Long archiveId);

    /**
     * 将仪表分配给代理商公司
     * @param entity 档案实体（包含createBy）
     * @return 插入数量
     */
    int insertMeterToAgent(@Param("entity") MtHeatArchive entity);

    /**
     * 级联更新 pr_heat_hot_archive 的 meter_arc_name
     * @param archiveId 档案ID
     * @param name 新名称
     * @return 更新行数
     */
    int syncNameToHeatHotArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

    /**
     * 级联更新 pr_heat_unit_hot_archive 的 meter_arc_name
     * @param archiveId 档案ID
     * @param name 新名称
     * @return 更新行数
     */
    int syncNameToHeatUnitHotArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

}
