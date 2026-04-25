package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportUnitHeat;

import java.util.List;

public interface PrImportUnitHeatMapper extends BaseMapperPlus<PrImportUnitHeat, PrImportUnitHeat> {

    void insert(@Param("list") List<PrImportUnitHeat> lists);

    void updateOrgId(@Param("create") String create, @Param("companyId") String companyId);

    void updateBuildingId(@Param("create") String create, @Param("companyId") String companyId);

    void updateUnitId(@Param("create") String create, @Param("companyId") String companyId);

    void updateMeter(@Param("create") String create, @Param("companyId") String companyId);

    void updateStandard(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> queryNoOrgIds(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> queryNoBuildingIds(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> queryNoUnitIds(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> selectNoMeterNum(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> selectRepateMeterNum(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> findNoArchiveId(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> findNoStandardId(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> findErrorMoney(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> selectMeterSerial(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> selectNumericalErrors(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> hasAllUnit(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> hasAllReadly(@Param("create") String create, @Param("companyId") String companyId);

    Integer select(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportUnitHeat> selectByCompanyIdOrgId(@Param("companyId") String companyId, @Param("orgId") String orgId);

    boolean deleteData(@Param("create") String create, @Param("companyId") String companyId);

    void submitData(@Param("create") String create, @Param("companyId") String companyId);

    void deleteImportUnitHeatData(@Param("create") String create, @Param("companyId") String companyId);
}
