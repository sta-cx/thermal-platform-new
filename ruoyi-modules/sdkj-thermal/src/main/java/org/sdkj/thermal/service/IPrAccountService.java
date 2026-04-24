package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 个人账户 Service 接口
 * 迁移自旧系统 PrAccountService
 */
public interface IPrAccountService {

    /** 分页查询个人账户列表 */
    List<PrAccountBalanceVo> pageList(String companyId, String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode);

    /** 开户 */
    boolean insertData(List<String> houseIds, String itemGroup, String itemCode, String payment);

    /** 查找没有开户的房屋 */
    List<PrAccountBalanceVo> noAccount(String companyId, String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode);

    /** 充值查询 */
    List<PrAccountBalanceVo> getAccount(String companyId, String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode, String userId);

    /** 充值操作 */
    boolean updateData(List<Map<String, String>> houses, String itemGroup, String itemCode, String payment);

    /** 账户退费 */
    void refundData(Map<String, String> houses, Map<String, String> record, Map<String, String> info);

    /** 转存 */
    boolean transfer(List<String> houseIds, String payment, String itemGroup, String itemCode,
        String makeInvoice, String invoice);

    /** 查询个人账户余额 */
    BigDecimal getPersonAccount(String companyId, String orgId, String userId);

    /** 查询账户对账单列表 */
    List<PrAccountBalanceVo> pageAccountStatementList(String companyId, String orgId, String buildingId,
        String unitCode, String itemGroup, String itemCode, String searchPhone);
}
