package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrCompanyVo;

import java.util.List;

public interface PrCompanyMapper extends BaseMapperPlus<PrCompany, PrCompanyVo> {

    List<SysOrganization> selectOrganizationsByCompanyId(@Param("companyId") String companyId);
}
