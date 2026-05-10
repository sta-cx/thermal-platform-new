package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrWechatBindRecord;
import org.sdkj.thermal.mapper.PrWechatBindRecordMapper;
import org.sdkj.thermal.service.IPrWechatBindRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信绑定记录 Service 实现
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PrWechatBindRecordServiceImpl
        extends ServiceImpl<PrWechatBindRecordMapper, PrWechatBindRecord>
        implements IPrWechatBindRecordService {

    private final PrWechatBindRecordMapper baseMapper;

    @Override
    public int getCountByHouseId(String houseId, String createBy) {
        return baseMapper.getCountByHouseId(houseId, createBy);
    }

    @Override
    public int insertData(String houseId, String heatPayCode, String wxOpenId, String orgId,
                          String orgName, String buildingId, String buildingName, String unitCode,
                          String roomNum, String createBy) {
        return baseMapper.insertData(houseId, heatPayCode, wxOpenId, orgId, orgName,
                buildingId, buildingName, unitCode, roomNum, createBy);
    }
}
