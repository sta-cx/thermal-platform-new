package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrHeatStation;

import java.util.List;

public interface IPrHeatStationService extends IService<PrHeatStation> {

    List<PrHeatStation> selectByCompanyId(String companyId);

    List<PrHeatStation> selectByOrgId(String orgId);
}
