package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.PrReconciliationDiff;
import org.sdkj.thermal.domain.PrWechatBill;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 微信对账 Service 接口
 */
public interface IPrReconciliationService {

    /**
     * 下载微信账单
     */
    PrWechatBill downloadBill(LocalDate billDate, String billType, String operator);

    /**
     * 执行对账
     */
    Map<String, Object> reconcileBill(LocalDate billDate, String billType, String operator);

    /**
     * 查询对账差异记录
     */
    List<PrReconciliationDiff> queryDiffs(Long billId);

    /**
     * 处理对账差异
     */
    void handleDiff(String diffId, String handleRemark, String handler);

    /**
     * 查询未处理的差异记录
     */
    List<PrReconciliationDiff> queryUnHandleDiffs();
}
