package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportUnitHeat;

import java.util.List;

public interface IPrImportUnitHeatService extends IService<PrImportUnitHeat> {

    Integer importData(List<PrImportUnitHeat> objects);

    void updateIds();

    void check(Integer r);

    String getNull(Integer r);

    boolean deleteData();

    boolean submitData();

    Integer select();

    List<PrImportUnitHeat> selectByCompanyIdOrgId(String companyId, String orgId);
}
