package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrRepairRecord;
import org.sdkj.thermal.mapper.PrRepairRecordMapper;
import org.sdkj.thermal.service.IPrRepairRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PrRepairRecordServiceImpl extends ServiceImpl<PrRepairRecordMapper, PrRepairRecord>
        implements IPrRepairRecordService {

    private final PrRepairRecordMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dispatch(String[] ids, String dispatchId, String isReject, String rejectReason, String dispatchMoney) {
        for (String id : ids) {
            PrRepairRecord record = new PrRepairRecord();
            record.setId(id);
            record.setDispatchId(dispatchId);
            record.setRepairStatus(2);
            record.setDispatchTime(new Date());
            if (dispatchMoney != null) {
                record.setDispatchMoney(new BigDecimal(dispatchMoney));
            }
            if ("1".equals(isReject)) {
                record.setIsReject(1);
                record.setRejectReason(rejectReason);
                record.setRepairStatus(4);
            }
            baseMapper.updateById(record);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateService(String id, String value, String type) {
        PrRepairRecord record = new PrRepairRecord();
        record.setId(id);
        switch (type) {
            case "attitude" -> record.setServiceAttitude(value);
            case "quality" -> record.setServiceQuality(value);
            case "efficiency" -> record.setServiceEfficiency(value);
            default -> { return false; }
        }
        return baseMapper.updateById(record) > 0;
    }

    @Override
    public Map<String, Object> getAllTypeCount(String companyId) {
        LambdaQueryWrapper<PrRepairRecord> lqw = new LambdaQueryWrapper<PrRepairRecord>()
            .eq(PrRepairRecord::getCompanyId, companyId);
        long total = baseMapper.selectCount(lqw);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        return result;
    }

    @Override
    public String generateRepairNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "WX" + date;
        String maxNo = baseMapper.selectMaxRepairNoByPrefix(prefix);
        int nextSeq = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            try {
                nextSeq = Integer.parseInt(maxNo.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + String.format("%04d", nextSeq);
    }
}
