package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportHeatTemp;

import java.util.List;

public interface PrImportHeatTempMapper extends BaseMapperPlus<PrImportHeatTemp, PrImportHeatTemp> {

    void insert(@Param("list") List<PrImportHeatTemp> lists);

    void updateHouseId(@Param("create") String create, @Param("companyId") String companyId);

    void updateMeter(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeatTemp> selectNoHouseId(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeatTemp> selectNoMeterNum(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeatTemp> selectRepateMeterNum(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeatTemp> findNoArchiveId(@Param("create") String create, @Param("companyId") String companyId);

    Integer select(@Param("create") String create, @Param("companyId") String companyId);

    List<PrImportHeatTemp> selectByCompanyIdOrgId(@Param("companyId") String companyId, @Param("orgId") String orgId);

    boolean deleteData(@Param("create") String create, @Param("companyId") String companyId);

    void submitData(@Param("create") String create, @Param("companyId") String companyId);

    void deleteImportHeatTempData(@Param("create") String create, @Param("companyId") String companyId);
}
