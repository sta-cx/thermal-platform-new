package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportRecord;

import java.util.List;

public interface PrImportRecordMapper extends BaseMapperPlus<PrImportRecord, PrImportRecord> {

    void insertList(@Param("list") List<PrImportRecord> lists);

    void updateHouseId(@Param("companyId") String companyId, @Param("create") String create);

    void updateItemId(@Param("companyId") String companyId, @Param("create") String create);

    void updateMeterInfo(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportRecord> selectNoOrgId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportRecord> selectNoHouseId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportRecord> selectNoItemId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportRecord> selectNoMeterNum(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportRecord> checkAmountError(@Param("companyId") String companyId, @Param("create") String create);

    boolean deleteData(@Param("companyId") String companyId, @Param("create") String create);

    void submitData(@Param("companyId") String companyId, @Param("create") String create);

    void deleteImportRecordData(@Param("companyId") String companyId, @Param("create") String create);
}
