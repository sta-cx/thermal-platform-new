package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.PrTransactionRecordSub;
import org.sdkj.thermal.domain.vo.PrExpenseVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrExpenseMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordSubMapper;
import org.sdkj.thermal.service.ISingleChargeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 单笔收费 Service 实现
 * 迁移自旧系统 SingleChargeServiceImpl
 */
@Service
@RequiredArgsConstructor
public class SingleChargeServiceImpl implements ISingleChargeService {

    private final StringRedisTemplate stringRedisTemplate;
    private final PrExpenseMapper expenseMapper;
    private final PrHouseMapper houseMapper;
    private final PrTransactionRecordMapper transactionRecordMapper;
    private final PrTransactionRecordSubMapper transactionRecordSubMapper;

    @Override
    public List<PrHouseVo> getHouse(String search, String companyId, String orgId, String buildingId) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHouse::getCompanyId, companyId);
        lqw.eq(PrHouse::getOrgId, orgId);
        if (buildingId != null && !buildingId.isEmpty()) {
            lqw.eq(PrHouse::getBuildingId, buildingId);
        }
        if (search != null && !search.isEmpty()) {
            lqw.like(PrHouse::getRoomNum, search);
        }
        return houseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHouseVo> getHouseRoomId(String search, String companyId, String orgId,
                                          String buildingId, String unitCode, String roomNum) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHouse::getCompanyId, companyId);
        lqw.eq(PrHouse::getOrgId, orgId);
        if (buildingId != null && !buildingId.isEmpty()) lqw.eq(PrHouse::getBuildingId, buildingId);
        if (unitCode != null && !unitCode.isEmpty()) lqw.eq(PrHouse::getUnitCode, unitCode);
        if (roomNum != null && !roomNum.isEmpty()) lqw.like(PrHouse::getRoomNum, roomNum);
        if (search != null && !search.isEmpty()) lqw.like(PrHouse::getUserName, search);
        return houseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrExpenseVo> pageList(String houseId) {
        return expenseMapper.selectHouseExpenseList(null, null, null, null, null, null, null)
            .stream()
            .filter(vo -> houseId == null || houseId.equals(vo.getHouseId()))
            .toList();
    }

    @Override
    public PrExpenseVo getDetail(String houseId, String code, String group, Integer isFree) {
        LambdaQueryWrapper<PrExpense> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrExpense::getHouseId, houseId);
        if (code != null && !code.isEmpty()) lqw.eq(PrExpense::getItemCode, code);
        if (group != null && !group.isEmpty()) lqw.eq(PrExpense::getItemGroup, group);
        if (isFree != null) lqw.eq(PrExpense::getIsFree, isFree);
        lqw.orderByDesc(PrExpense::getStartDate);
        List<PrExpense> list = expenseMapper.selectList(lqw);
        if (list.isEmpty()) return null;
        PrExpenseVo vo = new PrExpenseVo();
        org.springframework.beans.BeanUtils.copyProperties(list.get(0), vo);
        return vo;
    }

    @Override
    public Object selectCycle(String houseId, Integer sums, String group, String code,
                              Integer isFree, Integer index, String ids) {
        String key = "selectCycle:" + houseId + ":" + index;
        stringRedisTemplate.opsForValue().set(key, ids, 5, TimeUnit.MINUTES);
        return ids;
    }

    @Override
    public Object selectYear(String houseId, String year, Integer index, String id) {
        String monthKey = "selectCycle:" + houseId + ":" + index;
        String yearKey = "selectYear:" + houseId + ":" + index;
        stringRedisTemplate.delete(monthKey);
        stringRedisTemplate.opsForValue().set(yearKey, id, 5, TimeUnit.MINUTES);
        return id;
    }

    @Override
    public int getHasFinished(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return Math.toIntExact(expenseMapper.selectCount(
            new LambdaQueryWrapper<PrExpense>()
                .in(PrExpense::getId, ids)
                .eq(PrExpense::getIsCharged, 1)
        ));
    }

    @Override
    public Map<String, Object> queryPayByHouseId(String houseId) {
        LambdaQueryWrapper<PrExpense> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrExpense::getHouseId, houseId);
        List<PrExpense> list = expenseMapper.selectList(lqw);

        int total = list.size();
        int charged = 0;
        int uncharged = 0;
        double paidTotal = 0;
        double receivableTotal = 0;

        for (PrExpense e : list) {
            receivableTotal += e.getReceivable() != null ? e.getReceivable().doubleValue() : 0;
            if (e.getIsCharged() != null && e.getIsCharged() == 1) {
                charged++;
                paidTotal += e.getPaidIn() != null ? e.getPaidIn().doubleValue() : 0;
            } else {
                uncharged++;
            }
        }

        return Map.of(
            "total", total,
            "charged", charged,
            "uncharged", uncharged,
            "paidTotal", paidTotal,
            "receivableTotal", receivableTotal
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHousePayStatus(String houseId, String orgId) {
        LambdaQueryWrapper<PrExpense> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrExpense::getHouseId, houseId);
        long unpaid = expenseMapper.selectCount(lqw.eq(PrExpense::getIsCharged, 0));
        if (unpaid == 0) {
            expenseMapper.updateHousePayStatus(List.of(houseId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object singleCharge(PrExpenseVo expenseVo) {
        // 1. 校验参数
        if (expenseVo.getLists() == null || expenseVo.getLists().isEmpty()) {
            return Map.of("success", false, "msg", "无费用");
        }

        // 2. 收集所有待收费的 expense ID（去重）
        Set<String> allIds = new LinkedHashSet<>();
        for (PrExpenseVo item : expenseVo.getLists()) {
            if (item.getId() != null && !item.getId().isEmpty()) {
                // id 可能是逗号分隔的多个 ID
                String[] parts = item.getId().split(",");
                for (String part : parts) {
                    allIds.add(part.trim());
                }
            }
        }
        if (allIds.isEmpty()) {
            return Map.of("success", false, "msg", "无费用明细");
        }

        // 3. 校验是否存在已收费用
        int hasFinished = getHasFinished(new ArrayList<>(allIds));
        if (hasFinished > 0) {
            return Map.of("success", false, "msg", "存在已收费用，请核对后再次收费");
        }

        // 4. 确定交易时间
        Date transactionTime;
        if ("1".equals(expenseVo.getIsCollection()) && expenseVo.getCollectionTime() != null) {
            transactionTime = expenseVo.getCollectionTime();
        } else {
            transactionTime = new Date();
        }

        // 5. 生成流水号
        String serialNum = generateSerialNum();

        // 6. 查询房屋信息获取 companyId/orgId
        PrHouse house = houseMapper.selectById(expenseVo.getHouseId());
        if (house == null) {
            return Map.of("success", false, "msg", "房屋信息不存在");
        }

        // 7. 创建交易记录主表
        PrTransactionRecord record = new PrTransactionRecord();
        record.setSerialNum(serialNum);
        record.setTransactionType(1); // 1-收费
        record.setPaymentType(expenseVo.getPaymentType() != null ? expenseVo.getPaymentType() : 1);
        record.setAmount(expenseVo.getReceivable() != null ? expenseVo.getReceivable() : BigDecimal.ZERO);
        record.setPaidAmount(expenseVo.getPaidIn() != null ? expenseVo.getPaidIn() : BigDecimal.ZERO);
        record.setStatus(0); // 0-正常
        record.setHouseId(expenseVo.getHouseId());
        record.setUserId(expenseVo.getUserId());
        record.setOrgId(house.getOrgId());
        record.setCompanyId(house.getCompanyId());
        record.setTransactionTime(transactionTime);
        record.setNotes("单笔收费");

        transactionRecordMapper.insert(record);

        // 8. 创建交易记录子表 + 更新费用明细
        List<String> idList = new ArrayList<>(allIds);
        for (String expenseId : idList) {
            PrExpense expense = expenseMapper.selectById(expenseId);
            if (expense == null) {
                continue;
            }

            // 子记录
            PrTransactionRecordSub sub = new PrTransactionRecordSub();
            sub.setMainId(record.getId());
            sub.setExpenseId(expenseId);
            sub.setAmount(expense.getReceivable() != null ? expense.getReceivable() : BigDecimal.ZERO);
            sub.setItemName(expense.getItemName());
            transactionRecordSubMapper.insert(sub);

            // 更新费用明细为已收
            expense.setIsCharged(1);
            expense.setChargedTime(transactionTime);
            expense.setRecordId(record.getId());
            expense.setPaidIn(expense.getReceivable());
            expenseMapper.updateById(expense);
        }

        // 9. 更新房屋缴费状态
        updateHousePayStatus(expenseVo.getHouseId(), house.getOrgId());

        // 10. 返回交易记录
        return Map.of("success", true, "record", Map.of(
            "id", record.getId(),
            "serialNum", record.getSerialNum(),
            "transactionTime", record.getTransactionTime(),
            "paidAmount", record.getPaidAmount()
        ));
    }

    /**
     * 生成交易流水号: TXN + yyyyMMddHHmmss
     */
    private String generateSerialNum() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "TXN" + sdf.format(new Date());
    }
}
