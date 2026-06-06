package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrRepairRecord;
import org.sdkj.thermal.mapper.HtAlertMapper;
import org.sdkj.thermal.mapper.PrRepairRecordMapper;
import org.sdkj.thermal.service.IPrExpenseService;
import org.sdkj.thermal.service.IPrRepairRecordService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PrRepairRecordServiceImpl extends OrgScopedServiceImpl<PrRepairRecordMapper, PrRepairRecord>
        implements IPrRepairRecordService {

    private final PrRepairRecordMapper baseMapper;
    private final IPrExpenseService expenseService;
    private final HtAlertMapper htAlertMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrRepairRecord record) {
        boolean ok = super.save(record);
        if (ok && record.getAlertIds() != null && !record.getAlertIds().isEmpty()) {
            htAlertMapper.markInMaintenance(
                record.getAlertIds(),
                record.getId().toString(),
                LoginHelper.getUserId());
        }
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        htAlertMapper.clearInMaintenance(id.toString(), LoginHelper.getUserId());
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dispatch(String[] ids, String dispatchId, String isReject, String rejectReason, String dispatchMoney) {
        Arrays.stream(ids).forEach(id -> {
            PrRepairRecord record = new PrRepairRecord();
            record.setId(Long.valueOf(id));
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
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateService(String id, String value, String type) {
        LambdaUpdateWrapper<PrRepairRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PrRepairRecord::getId, id);

        switch (type) {
            case "attitude" -> wrapper.set(PrRepairRecord::getServiceAttitude, value);
            case "quality" -> wrapper.set(PrRepairRecord::getServiceQuality, value);
            case "efficiency" -> wrapper.set(PrRepairRecord::getServiceEfficiency, value);
            default -> { return false; }
        }

        wrapper.set(PrRepairRecord::getEvaluationTime, new Date());
        boolean result = baseMapper.update(null, wrapper) > 0;

        if (result) {
            baseMapper.update(null, new LambdaUpdateWrapper<PrRepairRecord>()
                .eq(PrRepairRecord::getId, id)
                .set(PrRepairRecord::getRepairStatus, 5));
        }
        return result;
    }

    @Override
    public Map<String, Object> getAllTypeCount() {
        long total = baseMapper.selectCount(new LambdaQueryWrapper<>());
        return Map.of("total", total);
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

    // known limitation: saveBatch 不经过重写的 save()，不触发 HtAlert 维修中联动；批量导入场景不带 alertIds，无实际影响
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertRepairItems(List<PrRepairRecord> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return saveBatch(records);
    }

    @Override
    public boolean getHouseIsOwe(String houseId) {
        if (!StringUtils.hasText(houseId)) {
            return false;
        }
        // Query expense records for the house that are not yet charged
        // isCharged=0 means unpaid, isCharged=1 means paid
        long unpaidCount = expenseService.count(
            new LambdaQueryWrapper<PrExpense>()
                .eq(PrExpense::getHouseId, houseId)
                .eq(PrExpense::getIsCharged, 0)
        );
        return unpaidCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDataService(PrRepairRecord record) {
        if (record.getId() == null) {
            return false;
        }
        LambdaUpdateWrapper<PrRepairRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PrRepairRecord::getId, record.getId());

        if (record.getServiceObject() != null) {
            wrapper.set(PrRepairRecord::getServiceObject, record.getServiceObject());
        }
        if (record.getServiceResult() != null) {
            wrapper.set(PrRepairRecord::getServiceResult, record.getServiceResult());
        }
        if (record.getGetMaterial() != null) {
            wrapper.set(PrRepairRecord::getGetMaterial, record.getGetMaterial());
        }
        if (record.getWhyFailure() != null) {
            wrapper.set(PrRepairRecord::getWhyFailure, record.getWhyFailure());
        }
        if (record.getAlertStatus() != null) {
            wrapper.set(PrRepairRecord::getAlertStatus, record.getAlertStatus());
        }
        // If service result is "0" (completed), update status and completion time
        if ("0".equals(record.getServiceResult())) {
            wrapper.set(PrRepairRecord::getRepairStatus, 3);
            wrapper.set(PrRepairRecord::getCompletionTime, new Date());
        }

        return baseMapper.update(null, wrapper) > 0;
    }
}
