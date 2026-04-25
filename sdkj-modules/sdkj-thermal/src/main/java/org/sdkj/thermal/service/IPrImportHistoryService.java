package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportHistory;

import java.util.List;

public interface IPrImportHistoryService extends IService<PrImportHistory> {

    Integer importData(List<Object> objects);

    void updateIds();

    String check(Integer r);

    boolean deleteData();

    boolean submitData();

    Integer select();
}
