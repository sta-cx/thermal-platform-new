package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtElectricArchive;
import org.sdkj.meter.domain.vo.MtElectricArchiveVo;

/**
 * 电表档案 Mapper
 * 迁移自旧系统 MtElectricArchiveMapper
 */
public interface MtElectricArchiveMapper extends BaseMapperPlus<MtElectricArchive, MtElectricArchiveVo> {

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
     * 将仪表分配给代理商公司
     * @param entity 档案实体（包含createBy）
     * @return 插入数量
     */
    int insertMeterToAgent(@Param("entity") MtElectricArchive entity);

}
