package org.dromara.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.thermal.domain.PrAccountBalance;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;
import org.dromara.thermal.mapper.PrAccountBalanceMapper;
import org.dromara.thermal.mapper.PrTransactionRecordMapper;
import org.dromara.thermal.service.IPrAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 个人账户 Service 实现
 * 迁移自旧系统 PrAccountServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrAccountServiceImpl implements IPrAccountService {

    private final PrAccountBalanceMapper balanceMapper;
    private final PrTransactionRecordMapper transactionMapper;

    @Override
    public List<PrAccountBalanceVo> pageList(String companyId, String orgId, String buildingId,
            String unitCode, String search, String itemGroup, String itemCode) {
        return balanceMapper.selectAccountList(
            companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(List<String> houseIds, String itemGroup, String itemCode, String payment) {
        if (houseIds == null || houseIds.isEmpty()) return false;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (String houseId : houseIds) {
            PrAccountBalance balance = new PrAccountBalance();
            balance.setHouseId(houseId);
            balance.setItemGroup(itemGroup);
            balance.setItemCode(itemCode);
            balance.setBalance(BigDecimal.ZERO);
            balance.setCreateBy(userId);
            balance.setCreateTime(now);
            balanceMapper.insert(balance);

            PrTransactionRecord record = new PrTransactionRecord();
            record.setSerialNum("ACC" + System.currentTimeMillis());
            record.setTransactionType(1);
            record.setPaymentType(payment != null ? Integer.parseInt(payment) : 1);
            record.setAmount(BigDecimal.ZERO);
            record.setPaidAmount(BigDecimal.ZERO);
            record.setStatus(0);
            record.setHouseId(houseId);
            record.setItemGroup(itemGroup);
            record.setItemCode(itemCode);
            record.setTransactionTime(now);
            record.setOperatorId(String.valueOf(userId));
            record.setNotes("开户");
            transactionMapper.insert(record);
        }
        return true;
    }

    @Override
    public List<PrAccountBalanceVo> noAccount(String companyId, String orgId, String buildingId,
            String unitCode, String search, String itemGroup, String itemCode) {
        return balanceMapper.selectNoAccountList(
            companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode);
    }

    @Override
    public List<PrAccountBalanceVo> getAccount(String companyId, String orgId, String buildingId,
            String unitCode, String search, String itemGroup, String itemCode, String userId) {
        return balanceMapper.selectAccountList(
            companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode)
            .stream()
            .filter(vo -> userId == null || userId.equals(vo.getUserId()))
            .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateData(List<Map<String, String>> houses, String itemGroup, String itemCode, String payment) {
        if (houses == null || houses.isEmpty()) return false;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (Map<String, String> house : houses) {
            String houseId = house.get("houseId");
            String amountStr = house.get("amount");
            if (houseId == null || amountStr == null) continue;

            BigDecimal amount = new BigDecimal(amountStr);
            balanceMapper.updateBalance(
                house.get("userId"), houseId, itemGroup, itemCode, amount);

            PrTransactionRecord record = new PrTransactionRecord();
            record.setSerialNum("RCH" + System.currentTimeMillis());
            record.setTransactionType(1);
            record.setPaymentType(payment != null ? Integer.parseInt(payment) : 1);
            record.setAmount(amount);
            record.setPaidAmount(amount);
            record.setStatus(0);
            record.setHouseId(houseId);
            record.setUserId(house.get("userId"));
            record.setItemGroup(itemGroup);
            record.setItemCode(itemCode);
            record.setTransactionTime(now);
            record.setOperatorId(String.valueOf(userId));
            record.setNotes("充值");
            transactionMapper.insert(record);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundData(Map<String, String> houses, Map<String, String> record, Map<String, String> info) {
        if (houses == null) return;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();
        BigDecimal amount = info != null && info.get("amount") != null
            ? new BigDecimal(info.get("amount")) : BigDecimal.ZERO;

        for (Map.Entry<String, String> entry : houses.entrySet()) {
            String houseId = entry.getKey();
            String houseUserId = entry.getValue();

            balanceMapper.updateBalance(houseUserId, houseId,
                record != null ? record.get("itemGroup") : null,
                record != null ? record.get("itemCode") : null,
                amount.negate());

            PrTransactionRecord refundRecord = new PrTransactionRecord();
            refundRecord.setSerialNum("REF" + System.currentTimeMillis());
            refundRecord.setTransactionType(2);
            refundRecord.setAmount(amount);
            refundRecord.setPaidAmount(amount);
            refundRecord.setStatus(0);
            refundRecord.setHouseId(houseId);
            refundRecord.setUserId(houseUserId);
            refundRecord.setItemGroup(record != null ? record.get("itemGroup") : null);
            refundRecord.setItemCode(record != null ? record.get("itemCode") : null);
            refundRecord.setTransactionTime(now);
            refundRecord.setOperatorId(String.valueOf(userId));
            refundRecord.setNotes("退费");
            transactionMapper.insert(refundRecord);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transfer(List<String> houseIds, String payment, String itemGroup, String itemCode,
            String makeInvoice, String invoice) {
        return false;
    }

    @Override
    public BigDecimal getPersonAccount(String companyId, String orgId, String userId) {
        return balanceMapper.selectBalanceByUser(companyId, orgId, userId);
    }

    @Override
    public List<PrAccountBalanceVo> pageAccountStatementList(String companyId, String orgId, String buildingId,
            String unitCode, String itemGroup, String itemCode, String searchPhone) {
        return balanceMapper.selectAccountList(
            companyId, orgId, buildingId, unitCode, searchPhone, itemGroup, itemCode);
    }
}
