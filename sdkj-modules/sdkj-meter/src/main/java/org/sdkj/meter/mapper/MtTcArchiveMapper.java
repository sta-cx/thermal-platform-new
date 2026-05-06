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
     * 将温控器分配给代理商公司
     * @param entity 档案实体（包含createBy）
     * @return 插入数量
     */
    int insertMeterToAgent(@Param("entity") MtTcArchive entity);

    /**
     * 查询所有启用的温控器
     * @return 温控器列表
     */
    List<MtTcArchiveVo> selectAllEnabled();

    /**
     * 级联更新 pr_heat_temp_archive 的 meter_arc_name
     * @param archiveId 档案ID
     * @param name 新名称
     * @return 更新行数
     */
    int syncNameToHeatTempArchive(@Param("archiveId") Long archiveId, @Param("name") String name);

}
