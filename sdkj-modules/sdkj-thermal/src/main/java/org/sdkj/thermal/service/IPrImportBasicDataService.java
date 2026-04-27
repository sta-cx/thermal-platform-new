package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.PrImportBasicData;
import org.sdkj.thermal.domain.PrImportBasicDataByCode;

import java.util.List;

public interface IPrImportBasicDataService extends IService<PrImportBasicData> {

    Integer importData(List<PrImportBasicData> objects);

    R check(long size);

    boolean deleteData();

    boolean submitData();

    long count();

    /**
     * 按房屋编码导入数据：匹配已有房屋，更新房屋信息及用户档案
     * @return 导入结果统计信息
     */
    R<String> importDataByHeatCode(List<PrImportBasicDataByCode> objects);

    /**
     * 检查指定房屋编码的房屋是否存在
     */
    boolean isCheckHouse(String code);
}
