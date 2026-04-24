package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrOptions;
import org.dromara.thermal.domain.vo.PrOptionsVo;

/**
 * 系统选项 Mapper
 */
public interface PrOptionsMapper extends BaseMapperPlus<PrOptions, PrOptionsVo> {

    /**
     * 根据组织ID查询系统选项
     */
    PrOptionsVo selectByOrgId(@Param("orgId") String orgId);
}
