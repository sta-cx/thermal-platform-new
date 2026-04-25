package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.PrImportBasicData;

import java.util.List;

public interface IPrImportBasicDataService extends IService<PrImportBasicData> {

    Integer importData(List<Object> objects);

    R check(long size);

    boolean deleteData();

    boolean submitData();

    long count();
}
