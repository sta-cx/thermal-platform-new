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

    /**
     * 统计已分配给其他公司的仪表数量
     * @param archiveId 档案ID
     * @return 分配数量
     */
    int countAllocatedToOtherCompany(@Param("archiveId") String archiveId);

    /**
     * 删除仪表匹配记录
     * @param archiveId 档案ID
     * @return 删除数量
     */
    int deleteMeterMatch(@Param("archiveId") String archiveId);

    /**
     * 将阀门分配给代理商公司
     * @param entity 档案实体（包含createBy）
     * @return 插入数量
     */
    int insertMeterToAgent(@Param("entity") MtTcValve entity);

    /**
     * 根据当前用户所属公司查询阀门列表
     * @param userId 用户ID
     * @return 阀门列表
     */
    List<MtTcValveVo> selectValvesByUserCompany(@Param("userId") Long userId);

    /**
     * 级联更新 pr_heat_valve_archive 的 meter_arc_name
     * @param archiveId 档案ID
     * @param name 新名称
     * @return 更新行数
     */
    int syncNameToHeatValveArchive(@Param("archiveId") String archiveId, @Param("name") String name);

    /**
     * 级联更新 pr_heat_unit_valve_archive 的 meter_arc_name
     * @param archiveId 档案ID
     * @param name 新名称
     * @return 更新行数
     */
    int syncNameToHeatUnitValveArchive(@Param("archiveId") String archiveId, @Param("name") String name);

}
