package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportValve;

import java.util.List;

/**
 * 阀门导入服务接口
 */
public interface IPrImportValveService extends IService<PrImportValve> {

    Integer importData(List<PrImportValve> objects);

    void updateHouseId();

    void check(Integer r);

    String getNull(Integer r);

    boolean deleteData();

    boolean submitData();

    Integer select();

    List<PrImportValve> selectByCompanyIdOrgId(String companyId, String orgId);
}
