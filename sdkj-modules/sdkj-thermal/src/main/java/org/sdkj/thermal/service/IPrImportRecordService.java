package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportRecord;

import java.util.List;

public interface IPrImportRecordService extends IService<PrImportRecord> {

    Integer importData(List<PrImportRecord> objects);

    void updateIds();

    String check(Integer r);

    boolean deleteData();

    boolean submitData();
}
