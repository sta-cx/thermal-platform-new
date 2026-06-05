package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportRecord;

import java.util.List;

public interface PrImportRecordMapper extends BaseMapperPlus<PrImportRecord, PrImportRecord> {

    void insertList(@Param("list") List<PrImportRecord> lists);

    void updateHouseId(@Param("create") String create);

    void updateItemId(@Param("create") String create);

    void updateMeterInfo(@Param("create") String create);

    List<PrImportRecord> selectNoOrgId(@Param("create") String create);

    List<PrImportRecord> selectNoHouseId(@Param("create") String create);

    List<PrImportRecord> selectNoItemId(@Param("create") String create);

    List<PrImportRecord> selectNoMeterNum(@Param("create") String create);

    List<PrImportRecord> checkAmountError(@Param("create") String create);

    List<String> selectImportedOrgIds(@Param("create") String create);

    boolean deleteData(@Param("create") String create);

    void submitData(@Param("create") String create);

    void deleteImportRecordData(@Param("create") String create);
}
