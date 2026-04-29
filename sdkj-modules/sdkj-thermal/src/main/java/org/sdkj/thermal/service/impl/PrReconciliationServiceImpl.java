package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.PrReconciliationDiff;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.PrWechatBill;
import org.sdkj.thermal.mapper.PrReconciliationDiffMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.mapper.PrWechatBillMapper;
import org.sdkj.thermal.service.IPrReconciliationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 微信对账 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrReconciliationServiceImpl implements IPrReconciliationService {

    private final PrWechatBillMapper wechatBillMapper;
    private final PrReconciliationDiffMapper diffMapper;
    private final PrTransactionRecordMapper transactionRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrWechatBill downloadBill(LocalDate billDate, String billType, String operator) {
        String billDateStr = billDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 检查是否已下载
        PrWechatBill existing = wechatBillMapper.selectByDateAndType(billDateStr, billType);
        if (existing != null && existing.getDownloadStatus() != null && existing.getDownloadStatus() == 1) {
            log.info("账单已下载: {} {}", billDateStr, billType);
            return existing;
        }

        if (existing != null) {
            // 已存在记录但未下载完成，重置状态
            existing.setDownloadStatus(1);
            existing.setDownloadTime(new Date());
            existing.setOperator(operator);
            existing.setCheckStatus(0);
            wechatBillMapper.updateById(existing);
            return existing;
        }

        // 创建新账单记录
        // TODO: Phase 6 — 调用微信下载账单 API（/pay/downloadbill），解析 CSV 写入表
        PrWechatBill bill = new PrWechatBill();
        bill.setBillDate(billDateStr);
        bill.setBillType(billType);
        bill.setDownloadStatus(1);
        bill.setDownloadTime(new Date());
        bill.setCheckStatus(0);
        bill.setOperator(operator);
        bill.setDelFlag("0");
        bill.setTotalCount(0);
        bill.setSuccessCount(0);
        bill.setDiffCount(0);
        wechatBillMapper.insert(bill);
        log.info("账单记录已创建: id={}, date={}, type={}", bill.getId(), billDateStr, billType);
        return bill;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reconcileBill(LocalDate billDate, String billType, String operator) {
        String billDateStr = billDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 1. 确保账单已下载
        PrWechatBill wechatBill = downloadBill(billDate, billType, operator);
        if (wechatBill.getDownloadStatus() == null || wechatBill.getDownloadStatus() != 1) {
            throw new RuntimeException("账单未下载成功，无法对账");
        }

        // 2. TODO: Phase 6 — 解析微信账单文件，获取账单明细列表
        //    当前简化：查询本地同期交易记录作为对账依据
        List<PrTransactionRecord> localRecords = transactionRecordMapper.selectList(
            new LambdaQueryWrapper<PrTransactionRecord>()
                .between(PrTransactionRecord::getCreateTime,
                    Date.from(billDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(billDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        );

        // 3. 清理旧差异记录
        List<PrReconciliationDiff> oldDiffs = diffMapper.selectByBillId(wechatBill.getId());
        if (!oldDiffs.isEmpty()) {
            for (PrReconciliationDiff old : oldDiffs) {
                diffMapper.deleteById(old.getId());
            }
        }

        // 4. 执行对账（简化版 — 以本地交易记录为基准）
        int totalCount = localRecords.size();
        int successCount = 0;
        int diffCount = 0;
        Date now = new Date();

        for (PrTransactionRecord record : localRecords) {
            // TODO: Phase 6 — 逐条对比账单记录与本地交易记录
            //    匹配规则：(out_trade_no, amount, transaction_time)
            //    当前简化：假设所有记录匹配
            successCount++;
        }

        // 对账结果中可能存在微信有但本地无的记录（漏单）
        // TODO: Phase 6 — 从微信账单文件中解析出所有订单号，与本地比对

        // 5. 更新账单对账状态
        wechatBill.setCheckStatus(diffCount > 0 ? 2 : 1);
        wechatBill.setCheckTime(now);
        wechatBill.setTotalCount(totalCount);
        wechatBill.setSuccessCount(successCount);
        wechatBill.setDiffCount(diffCount);
        wechatBillMapper.updateById(wechatBill);

        // 6. 构建对账结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("billId", wechatBill.getId());
        result.put("billDate", billDateStr);
        result.put("billType", billType);
        result.put("totalRecords", totalCount);
        result.put("matchCount", successCount);
        result.put("diffCount", diffCount);
        result.put("checkStatus", wechatBill.getCheckStatus());

        return result;
    }

    @Override
    public List<PrReconciliationDiff> queryDiffs(String billId) {
        return diffMapper.selectByBillId(billId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleDiff(String diffId, String handleRemark, String handler) {
        PrReconciliationDiff diff = diffMapper.selectById(diffId);
        if (diff == null) {
            throw new RuntimeException("差异记录不存在");
        }
        diff.setHandleStatus("1");
        diff.setHandleRemark(handleRemark);
        diff.setHandler(handler);
        diff.setHandleTime(new Date());
        diffMapper.updateById(diff);
        log.info("对账差异已处理: diffId={}, handler={}", diffId, handler);
    }

    @Override
    public List<PrReconciliationDiff> queryUnHandleDiffs() {
        return diffMapper.selectUnHandleDiffs();
    }
}
