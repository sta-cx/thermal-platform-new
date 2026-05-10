package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportUnitValve;

import java.util.List;

/**
 * 单元阀门导入 Mapper
 * 迁移自旧系统 PrImportUnitValveMapper
 */
public interface PrImportUnitValveMapper extends BaseMapperPlus<PrImportUnitValve, PrImportUnitValve> {

    /**
     * 批量插入导入数据
     */
    void insert(@Param("list") List<PrImportUnitValve> lists);

    /**
     * 更新小区ID
     */
    void updateOrgId(@Param("create") String create);

    /**
     * 更新楼宇ID
     */
    void updateBuildingId(@Param("create") String create);

    /**
     * 更新单元ID
     */
    void updateUnitId(@Param("create") String create);

    /**
     * 更新仪表档案ID
     */
    void updateMeter(@Param("create") String create);

    /**
     * 更新控制阀门仪表档案ID
     */
    void updateCommandMeter(@Param("create") String create);

    /**
     * 查询无小区ID的记录
     */
    List<PrImportUnitValve> selectNoOrgIds(@Param("create") String create);

    /**
     * 查询无楼宇ID的记录
     */
    List<PrImportUnitValve> selectNoBuildingIds(@Param("create") String create);

    /**
     * 查询单元ID为空的记录
     */
    List<PrImportUnitValve> selectNoUnitId(@Param("create") String create);

    /**
     * 查询表号为空的记录
     */
    List<PrImportUnitValve> selectNoMeterNum(@Param("create") String create);

    /**
     * 查询表号重复的记录
     */
    List<PrImportUnitValve> getRepateUnitMeter2(@Param("create") String create);

    /**
     * 查询无档案ID的记录
     */
    List<PrImportUnitValve> findNoArchiveId(@Param("create") String create);

    /**
     * 查询已绑定阀门的单元
     */
    List<PrImportUnitValve> selectHasAllByUnit(@Param("create") String create);

    /**
     * 查询表号已存在的记录
     */
    List<PrImportUnitValve> selectHasAllReadly(@Param("create") String create);

    /**
     * 查询待提交记录数
     */
    Integer select(@Param("create") String create);

    /**
     * 按公司和小区查询单元列表
     */
    List<PrImportUnitValve> selectByOrgId(@Param("orgId") String orgId);

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
    void deleteImportUnitValveData(@Param("create") String create);
}
