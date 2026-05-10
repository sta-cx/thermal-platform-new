package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportHeat;

import java.util.List;

public interface IPrImportHeatService extends IService<PrImportHeat> {

    Integer importData(List<PrImportHeat> objects);

    void updateHouseId();

    void check(Integer r);

    String getNull(Integer r);

    boolean deleteData();

    boolean submitData();

    Integer select();

    List<PrImportHeat> selectByOrgId(String orgId);
}
