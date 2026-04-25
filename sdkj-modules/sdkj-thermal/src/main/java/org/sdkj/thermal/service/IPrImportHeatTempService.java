package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportHeatTemp;

import java.util.List;

public interface IPrImportHeatTempService extends IService<PrImportHeatTemp> {

    Integer importData(List<Object> objects);

    void updateHouseId();

    void check();

    String getNull(Integer r);

    boolean deleteData();

    boolean submitData();

    Integer select();

    List<PrImportHeatTemp> selectByCompanyIdOrgId(String companyId, String orgId);
}
