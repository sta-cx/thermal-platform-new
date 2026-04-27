package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;

import java.util.List;

public interface IPrCompanyService extends IService<PrCompany> {

    List<PrCompany> listCompanies();

    List<SysOrganization> getOrganizationsByCompanyId(String companyId);
}
