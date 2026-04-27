package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrWechatBindRecord;

/**
 * 微信绑定记录 Service
 */
public interface IPrWechatBindRecordService extends IService<PrWechatBindRecord> {

    int getCountByHouseId(String houseId, String createBy);

    int insertData(String houseId, String heatPayCode, String wxOpenId, String orgId,
                   String orgName, String buildingId, String buildingName, String unitCode,
                   String roomNum, String companyId, String createBy);
}
