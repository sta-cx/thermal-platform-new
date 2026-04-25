package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrRepairRecord;

public interface PrRepairRecordMapper extends BaseMapperPlus<PrRepairRecord, PrRepairRecord> {

    @Select("SELECT MAX(repair_no) FROM pr_repair_record WHERE repair_no LIKE CONCAT(#{prefix}, '%')")
    String selectMaxRepairNoByPrefix(@Param("prefix") String prefix);
}
