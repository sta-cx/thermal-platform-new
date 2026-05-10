package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportHeat;

import java.util.List;

public interface PrImportHeatMapper extends BaseMapperPlus<PrImportHeat, PrImportHeat> {

    void insert(@Param("list") List<PrImportHeat> lists);

    void updateHouseId(@Param("create") String create);

    void updateMeter(@Param("create") String create);

    void updateStandard(@Param("create") String create);

    List<PrImportHeat> selectNoHouseId(@Param("create") String create);

    List<PrImportHeat> selectNoMeterNum(@Param("create") String create);

    List<PrImportHeat> selectRepateMeterNum(@Param("create") String create);

    List<PrImportHeat> findNoArchiveId(@Param("create") String create);

    List<PrImportHeat> findNoStandardId(@Param("create") String create);

    List<PrImportHeat> findErrorMoney(@Param("create") String create);

    List<PrImportHeat> selectMeterSerial(@Param("create") String create);

    List<PrImportHeat> selectNumericalErrors(@Param("create") String create);

    List<PrImportHeat> hasAllHouse(@Param("create") String create);

    List<PrImportHeat> hasAllReadly(@Param("create") String create);

    Integer select(@Param("create") String create);

    List<PrImportHeat> selectByOrgId(@Param("orgId") String orgId);

    boolean deleteData(@Param("create") String create);

    void submitData(@Param("create") String create);

    void deleteImportHeatData(@Param("create") String create);

    void deleteHeatByNoHouseId(@Param("create") String create);
}
