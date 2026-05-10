package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.bo.RefundDataBo;
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
    List<PrAccountBalanceVo> pageList(String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode);

    /**
     * 账户开户
     */
    boolean insertData(List<Long> houseIds, String itemGroup, String itemCode, String payment);

    /** 查找没有开户的房屋 */
    List<PrAccountBalanceVo> noAccount(String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode);

    /** 充值查询 */
    List<PrAccountBalanceVo> getAccount(String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode, String userId);

    /** 充值操作 */
    boolean updateData(List<Map<String, String>> houses, String itemGroup, String itemCode, String payment);

    /** 账户退费 */
    void refundData(RefundDataBo bo);

    /** 转存 */
    boolean transfer(List<Long> houseIds, String payment, String itemGroup, String itemCode,
        String makeInvoice, String invoice);

    /** 查询个人账户余额 */
    BigDecimal getPersonAccount(String orgId, Long userId);

    /** 查询账户对账单列表 */
    List<PrAccountBalanceVo> pageAccountStatementList(String orgId, String buildingId,
        String unitCode, String itemGroup, String itemCode, String searchPhone);

    /** 查询房屋押金信息 */
    Map<String, Object> getHouseDeposit(String orgId, String buildingId,
        String unitCode, String search);

    /** 保存押金缴费 */
    Map<String, Object> saveDeposit(Map<String, Object> depositVo);

    /** 导入账户数据预览 */
    Map<String, Object> pageListImportData(int pageNum, int pageSize);

    /** 导入Excel数据 */
    Map<String, Object> importExcelData(Object file);

    /** 删除导入的临时数据 */
    boolean deleteImportData();
}
