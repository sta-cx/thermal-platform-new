package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrRepairPerson;

import java.util.List;

public interface IPrRepairPersonService extends IService<PrRepairPerson> {

    List<PrRepairPerson> selectByCompanyId(String companyId);

    List<PrRepairPerson> selectByOrgId(String orgId, String companyId);
}
