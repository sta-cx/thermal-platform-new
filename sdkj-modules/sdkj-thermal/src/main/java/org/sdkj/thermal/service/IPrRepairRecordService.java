package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrRepairRecord;

import java.util.List;
import java.util.Map;

public interface IPrRepairRecordService extends IService<PrRepairRecord> {

    boolean dispatch(String[] ids, String dispatchId, String isReject, String rejectReason, String dispatchMoney);

    boolean updateService(String id, String value, String type);

    Map<String, Object> getAllTypeCount();

    String generateRepairNo();

    /**
     * 批量增加报修项目
     */
    boolean insertRepairItems(List<PrRepairRecord> records);

    /**
     * 查询房屋是否欠费
     */
    boolean getHouseIsOwe(String houseId);

    /**
     * 更新维修服务数据
     */
    boolean updateDataService(PrRepairRecord record);
}
