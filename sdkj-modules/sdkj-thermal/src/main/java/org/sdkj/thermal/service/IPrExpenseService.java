package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.PmParkingSpace;
import org.sdkj.thermal.domain.vo.PrExpenseVo;

import org.sdkj.thermal.domain.dto.MarkedPaymentResult;

import java.util.List;
import java.util.Map;

/**
 * 费用明细 Service 接口
 * 迁移自旧系统 PrExpenseService
 */
public interface IPrExpenseService extends IService<PrExpense> {

    /**
     * 分页查询费用明细列表
     */
    TableDataInfo<PrExpenseVo> selectPageList(String orgId, String buildingId, String unitCode,
                                              String itemGroup, String itemCode, String search, String isCharged,
                                              String parkingId, String startTime, String endTime,
                                              String startDate, String endDate, PageQuery pageQuery);

    /**
     * 查询房屋费用明细列表
     */
    List<PrExpenseVo> selectHouseExpenseList(String orgId, String buildingId, String unitCode,
                                             String itemGroup, String itemCode, String search);

    /**
     * 查询房屋取暖费明细
     */
    PrExpenseVo selectHeatExpenseByHouseId(Long houseId);

    /**
     * 生成取暖费明细
     */
    boolean insertData(List<PrHouseExpense> list);

    /**
     * 批量生成费用明细
     */
    boolean insertDatall(List<PrHouseExpense> list);

    /**
     * 生成临时费用明细
     */
    boolean insertDataLs(List<PrHouseExpense> list);

    /**
     * 批量生成车位费用明细
     */
    boolean insertDatallCw(List<PmParkingSpace> list);

    /**
     * 设置优惠
     */
    boolean setPreferential(List<PrExpense> list, String type, String scale, String price, String reason, String times);

    /**
     * 设置免收
     */
    boolean setIsFree(List<PrExpense> list, String reason, String times);

    /**
     * 设置延期
     */
    boolean setLastDate(List<PrExpense> list, String reason, String days);

    /**
     * 设置报停
     */
    boolean setBaotingDate(List<PrExpense> list, String type, String scale, String price, String reason);

    /**
     * 设置复供
     */
    boolean setFugongDate(List<PrExpense> list, String reason, String days);

    /**
     * 设置退费
     */
    boolean setTuifei(List<PrExpense> list, String type, String scale, String price, String reason);

    /**
     * 删除费用明细
     */
    boolean deleteDate(List<PrExpense> list);

    /**
     * 修改费用明细标准
     */
    boolean updateDatall(List<PrExpense> list);

    /**
     * 重新计算费用明细
     */
    boolean recalculate(String orgId);

    /**
     * 重新计算车位费用明细
     */
    boolean recalculateCw(String orgId);

    /**
     * 设置计算状态
     */
    boolean setCalStatus(Long houseId);

    /**
     * 批量更新阶梯单价
     */
    boolean updateStepPrice(String orgId);

    /**
     * 批量更新单价
     */
    boolean updatePrice(String orgId);

    /**
     * 批量计算公式
     */
    boolean updateFormula(String orgId);

    /**
     * 批量计算公式（车位）
     */
    boolean updateFormulaCw(String orgId);

    /** 车位费用列表查询 */
    TableDataInfo<PrExpenseVo> parkingList(String orgId, String buildingId, String unitCode,
        String itemGroup, String itemCode, String search, String isCharged,
        String parkingId, String startTime, String endTime, PageQuery pageQuery);

    /** 车位空间费用查询 */
    List<Map<String, Object>> parkingExpenseList(String orgId, String buildingId,
        String unitCode, String itemGroup, String itemCode, String parkingId, String search);

    /** 全部房屋费用查询 */
    List<Map<String, Object>> houseExpenseAllList(String orgId, String search);

    /** 费用操作日志 */
    TableDataInfo<Map<String, Object>> expenseLog(String orgId, String buildingId,
        String unitCode, String parentId, String type, String startTime, String endTime, String search,
        PageQuery pageQuery);

    // ========== 滞纳金计算方法 ==========

    /**
     * 滞纳金计算 - 起收日期
     */
    boolean updateLatefeeQs(String orgId, String latefeeFormula, Long standardId);

    /**
     * 滞纳金计算 - 到期日期
     */
    boolean updateLatefeeJs(String orgId, String latefeeFormula, Long standardId);

    /**
     * 滞纳金计算 - 指定日期
     */
    boolean updateLatefeeZd(String orgId, String latefeeFormula, Long standardId,
                             java.util.Date latefeeStartdate);

    /**
     * 滞纳金计算 - 数据核查
     */
    boolean updateLatefeeSJHC(String orgId, String latefeeFormula, Long standardId,
                              String year, String month);

    /**
     * 滞纳金计算后更新最终金额
     */
    boolean updateFinalMoneyAfterLateFee(String orgId, Long standardId);

    /**
     * AI Tool 调用的标记缴费入口。
     *
     * <p>校验规则：
     * <ul>
     *   <li>expenseId 对应的费用条目必须存在</li>
     *   <li>isCharged 必须为 0 或 null（未缴费），已缴费则拒绝重复标记</li>
     * </ul>
     *
     * @param expenseId  费用条目 ID（必填）
     * @param note       备注（可空）
     * @param operatorId 操作者用户 ID
     * @return 标记结果
     * @throws IllegalArgumentException expenseId 不存在或已缴费
     */
    MarkedPaymentResult markPaidFromAi(Long expenseId, String note, Long operatorId);
}
