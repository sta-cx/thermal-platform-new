package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.AgCompany;
import org.sdkj.thermal.domain.vo.AgCompanyVo;

/**
 * 代理商公司 Mapper
 * 基础 CRUD 使用 BaseMapperPlus 提供，仅保留复杂查询
 */
public interface AgCompanyMapper extends BaseMapperPlus<AgCompany, AgCompanyVo> {
}
