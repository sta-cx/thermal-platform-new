package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportUnitHeat;

import java.util.List;

public interface PrImportUnitHeatMapper extends BaseMapperPlus<PrImportUnitHeat, PrImportUnitHeat> {

    void insert(@Param("list") List<PrImportUnitHeat> lists);

    void updateOrgId(@Param("create") String create);

    void updateBuildingId(@Param("create") String create);

    void updateUnitId(@Param("create") String create);

    void updateMeter(@Param("create") String create);

    void updateStandard(@Param("create") String create);

    List<PrImportUnitHeat> queryNoOrgIds(@Param("create") String create);

    List<PrImportUnitHeat> queryNoBuildingIds(@Param("create") String create);

    List<PrImportUnitHeat> queryNoUnitIds(@Param("create") String create);

    List<PrImportUnitHeat> selectNoMeterNum(@Param("create") String create);

    List<PrImportUnitHeat> selectRepateMeterNum(@Param("create") String create);

    List<PrImportUnitHeat> findNoArchiveId(@Param("create") String create);

    List<PrImportUnitHeat> findNoStandardId(@Param("create") String create);

    List<PrImportUnitHeat> findErrorMoney(@Param("create") String create);

    List<PrImportUnitHeat> selectMeterSerial(@Param("create") String create);

    List<PrImportUnitHeat> selectNumericalErrors(@Param("create") String create);

    List<PrImportUnitHeat> hasAllUnit(@Param("create") String create);

    List<PrImportUnitHeat> hasAllReadly(@Param("create") String create);

    Integer select(@Param("create") String create);

    List<PrImportUnitHeat> selectByOrgId(@Param("orgId") String orgId);

    List<String> selectImportedOrgIds(@Param("create") String create);

    boolean deleteData(@Param("create") String create);

    void submitData(@Param("create") String create);

    void deleteImportUnitHeatData(@Param("create") String create);
}
