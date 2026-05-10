package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportHeatTemp;

import java.util.List;

public interface IPrImportHeatTempService extends IService<PrImportHeatTemp> {

    Integer importData(List<PrImportHeatTemp> objects);

    void updateHouseId();

    void check();

    String getNull(Integer r);

    boolean deleteData();

    boolean submitData();

    Integer select();

    List<PrImportHeatTemp> selectByOrgId(String orgId);
}
