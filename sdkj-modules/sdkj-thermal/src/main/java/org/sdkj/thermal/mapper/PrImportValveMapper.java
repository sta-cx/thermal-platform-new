package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportValve;

import java.util.List;

/**
 * 阀门导入 Mapper
 * 迁移自旧系统 PrImportValveMapper
 */
public interface PrImportValveMapper extends BaseMapperPlus<PrImportValve, PrImportValve> {

    /**
     * 批量插入导入数据
     */
    void insert(@Param("list") List<PrImportValve> lists);

    /**
     * 更新房屋ID
     */
    void updateHouseId(@Param("create") String create);

    /**
     * 更新仪表档案ID
     */
    void updateMeter(@Param("create") String create);

    /**
     * 更新控制阀门仪表档案ID
     */
    void updateCommandMeter(@Param("create") String create);

    /**
     * 查询无房屋ID的记录
     */
    List<PrImportValve> selectNoHouseId(@Param("create") String create);

    /**
     * 查询表号为空的记录
     */
    List<PrImportValve> selectNoMeterNum(@Param("create") String create);

    /**
     * 查询表号重复的记录
     */
    List<PrImportValve> getRepateMeter2(@Param("create") String create);

    /**
     * 查询无档案ID的记录
     */
    List<PrImportValve> findNoArchiveId(@Param("create") String create);

    /**
     * 查询已绑定阀门的房屋
     */
    List<PrImportValve> selectHasAllHouse(@Param("create") String create);

    /**
     * 查询表号已存在的记录
     */
    List<PrImportValve> selectHasAllReadly(@Param("create") String create);

    /**
     * 查询待提交记录数
     */
    Integer select(@Param("create") String create);

    /**
     * 按公司和小区查询房屋列表
     */
    List<PrImportValve> selectByOrgId(@Param("orgId") String orgId);

    List<String> selectImportedOrgIds(@Param("create") String create);

    /**
     * 删除导入数据
     */
    boolean deleteData(@Param("create") String create);

    /**
     * 提交数据到正式表
     */
    void submitData(@Param("create") String create);

    /**
     * 删除已提交的导入数据
     */
    void deleteImportValveData(@Param("create") String create);

    /**
     * 删除无房屋ID的阀门配表数据
     */
    void deleteHeatValveByNoHouseId(@Param("create") String create);
}
