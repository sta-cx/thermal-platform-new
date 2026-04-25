package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrRepairRecord;

import java.util.Map;

public interface IPrRepairRecordService extends IService<PrRepairRecord> {

    boolean dispatch(String[] ids, String dispatchId, String isReject, String rejectReason, String dispatchMoney);

    boolean updateService(String id, String value, String type);

    Map<String, Object> getAllTypeCount(String companyId);

    String generateRepairNo();
}
