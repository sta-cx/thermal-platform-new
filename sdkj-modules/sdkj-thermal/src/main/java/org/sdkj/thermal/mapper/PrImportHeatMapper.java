package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportHeat;

import java.util.List;

public interface PrImportHeatMapper extends BaseMapperPlus<PrImportHeat, PrImportHeat> {

    void insert(@Param("list") List<PrImportHeat> lists);

    void updateHouseId(@Param("create") String create, @Param("companyId") String companyId);

    void updateMeter(@Param("create") String create, @Param("companyId") String companyId);

    void updateStandard(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> selectNoHouseId(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> selectNoMeterNum(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> selectRepateMeterNum(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> findNoArchiveId(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> findNoStandardId(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> findErrorMoney(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> selectMeterSerial(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> selectNumericalErrors(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> hasAllHouse(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> hasAllReadly(@Param("create") String create, @Param("companyId") String companyId);

    Integer select(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeat> selectByCompanyIdOrgId(@Param("companyId") String companyId, @Param("orgId") String orgId);

    boolean deleteData(@Param("create") String create, @Param("companyId") String companyId);

    void submitData(@Param("create") String create, @Param("companyId") String companyId);

    void deleteImportHeatData(@Param("create") String create, @Param("companyId") String companyId);

    void deleteHeatByNoHouseId(@Param("create") String create, @Param("companyId") String companyId);
}
