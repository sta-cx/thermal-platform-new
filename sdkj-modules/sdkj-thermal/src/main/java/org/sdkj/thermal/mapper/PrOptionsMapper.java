package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrOptions;
import org.sdkj.thermal.domain.vo.PrOptionsVo;

/**
 * 系统选项 Mapper
 */
@OrgPermission
public interface PrOptionsMapper extends BaseMapperPlus<PrOptions, PrOptionsVo> {

    /**
     * 根据组织ID查询系统选项
     */
    PrOptionsVo selectByOrgId(@Param("orgId") String orgId);
}
