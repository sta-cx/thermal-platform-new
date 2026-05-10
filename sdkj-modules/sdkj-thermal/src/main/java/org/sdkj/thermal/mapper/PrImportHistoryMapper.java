package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportHistory;

import java.util.List;

public interface PrImportHistoryMapper extends BaseMapperPlus<PrImportHistory, PrImportHistory> {

    void insert(@Param("list") List<PrImportHistory> lists);

    void updateHouseId(@Param("create") String create);

    void updateStandardId(@Param("create") String create);

    List<PrImportHistory> selectNoHouseId(@Param("create") String create);

    List<PrImportHistory> selectNoStandardId(@Param("create") String create);

    Integer select(@Param("create") String create);

    boolean deleteData(@Param("create") String create);

    void submitData(@Param("create") String create);

    void deleteImportHistoryData(@Param("create") String create);
}
