package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportHeatTemp;

import java.util.List;

public interface PrImportHeatTempMapper extends BaseMapperPlus<PrImportHeatTemp, PrImportHeatTemp> {

    void insert(@Param("list") List<PrImportHeatTemp> lists);

    void updateHouseId(@Param("create") String create);

    void updateMeter(@Param("create") String create);

    List<PrImportHeatTemp> selectNoHouseId(@Param("create") String create);

    List<PrImportHeatTemp> selectNoMeterNum(@Param("create") String create);

    List<PrImportHeatTemp> selectRepateMeterNum(@Param("create") String create);

    List<PrImportHeatTemp> findNoArchiveId(@Param("create") String create);

    Integer select(@Param("create") String create);

    List<PrImportHeatTemp> selectByOrgId(@Param("orgId") String orgId);

    boolean deleteData(@Param("create") String create);

    void submitData(@Param("create") String create);

    void deleteImportHeatTempData(@Param("create") String create);
}
