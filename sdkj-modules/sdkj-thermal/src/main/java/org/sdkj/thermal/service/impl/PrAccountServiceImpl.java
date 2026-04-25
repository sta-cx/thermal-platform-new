package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrAccountBalance;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.vo.PrAccountBalanceVo;
import org.sdkj.thermal.mapper.PrAccountBalanceMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.service.IPrAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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
        if (houseIds == null || houseIds.isEmpty()) return false;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();
        int paymentType = payment != null ? Integer.parseInt(payment) : 1;

        for (String houseId : houseIds) {
            // Query existing balance records for this house
            LambdaQueryWrapper<PrAccountBalance> qw = new LambdaQueryWrapper<>();
            qw.eq(PrAccountBalance::getHouseId, houseId);
            List<PrAccountBalance> balances = balanceMapper.selectList(qw);

            for (PrAccountBalance source : balances) {
                BigDecimal balance = source.getBalance();
                if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                // Cannot transfer to same item
                if (source.getItemGroup().equals(itemGroup) && source.getItemCode().equals(itemCode)) {
                    continue;
                }

                // Deduct from source
                balanceMapper.updateBalance(source.getUserId(), houseId,
                    source.getItemGroup(), source.getItemCode(), balance.negate());

                // Create transaction record for source deduction (type 3 = 转移/转存)
                PrTransactionRecord sourceRecord = new PrTransactionRecord();
                sourceRecord.setSerialNum("TFR" + System.currentTimeMillis() + "-S");
                sourceRecord.setTransactionType(3);
                sourceRecord.setPaymentType(paymentType);
                sourceRecord.setAmount(balance.negate());
                sourceRecord.setPaidAmount(balance.negate());
                sourceRecord.setStatus(0);
                sourceRecord.setHouseId(houseId);
                sourceRecord.setUserId(source.getUserId());
                sourceRecord.setItemGroup(source.getItemGroup());
                sourceRecord.setItemCode(source.getItemCode());
                sourceRecord.setTransactionTime(now);
                sourceRecord.setOperatorId(String.valueOf(userId));
                sourceRecord.setNotes("转存-转出");
                transactionMapper.insert(sourceRecord);

                // Add to target item
                LambdaQueryWrapper<PrAccountBalance> targetQw = new LambdaQueryWrapper<>();
                targetQw.eq(PrAccountBalance::getHouseId, houseId)
                    .eq(PrAccountBalance::getUserId, source.getUserId())
                    .eq(PrAccountBalance::getItemGroup, itemGroup)
                    .eq(PrAccountBalance::getItemCode, itemCode);
                PrAccountBalance target = balanceMapper.selectOne(targetQw);

                if (target != null) {
                    balanceMapper.updateBalance(source.getUserId(), houseId,
                        itemGroup, itemCode, balance);
                } else {
                    // Create new target balance record
                    PrAccountBalance newBalance = new PrAccountBalance();
                    newBalance.setHouseId(houseId);
                    newBalance.setUserId(source.getUserId());
                    newBalance.setItemGroup(itemGroup);
                    newBalance.setItemCode(itemCode);
                    newBalance.setBalance(balance);
                    newBalance.setOrgId(source.getOrgId());
                    newBalance.setCompanyId(source.getCompanyId());
                    newBalance.setCreateBy(userId);
                    newBalance.setCreateTime(now);
                    balanceMapper.insert(newBalance);
                }

                // Create transaction record for target addition
                PrTransactionRecord targetRecord = new PrTransactionRecord();
                targetRecord.setSerialNum("TFR" + System.currentTimeMillis() + "-T");
                targetRecord.setTransactionType(3);
                targetRecord.setPaymentType(paymentType);
                targetRecord.setAmount(balance);
                targetRecord.setPaidAmount(balance);
                targetRecord.setStatus(0);
                targetRecord.setHouseId(houseId);
                targetRecord.setUserId(source.getUserId());
                targetRecord.setItemGroup(itemGroup);
                targetRecord.setItemCode(itemCode);
                targetRecord.setTransactionTime(now);
                targetRecord.setOperatorId(String.valueOf(userId));
                targetRecord.setNotes("转存-转入");
                targetRecord.setOriginalRecordId(sourceRecord.getId());
                transactionMapper.insert(targetRecord);
            }
        }
        return true;
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

    @Override
    public Map<String, Object> getHouseDeposit(String companyId, String orgId, String buildingId,
            String unitCode, String search) {
        return balanceMapper.selectHouseDeposit(companyId, orgId, buildingId, unitCode, search);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveDeposit(Map<String, Object> depositVo) {
        return balanceMapper.saveDepositTransaction(depositVo);
    }

    @Override
    public Map<String, Object> pageListImportData(int pageNum, int pageSize) {
        return Map.of("msg", "导入预览功能尚未实现，需要 pr_import_account 临时表支持");
    }

    @Override
    public Map<String, Object> importExcelData(Object file) {
        return Map.of("msg", "数据导入功能尚未实现，需要 EasyExcel 集成");
    }

    @Override
    public boolean deleteImportData() {
        return balanceMapper.deleteImportStagingData() > 0;
    }
}
