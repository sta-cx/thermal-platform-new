package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;

/**
 * 热力档案 Mapper
 */
public interface PrHeatArchiveMapper extends BaseMapperPlus<PrHeatArchive, PrHeatArchiveVo> {

    void importData(@Param("companyId") String companyId, @Param("createBy") String createBy);
}
