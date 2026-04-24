package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrExpense;
import org.dromara.thermal.domain.PrHouseExpense;
import org.dromara.thermal.domain.PmParkingSpace;
import org.dromara.thermal.domain.vo.PrExpenseVo;

import java.util.List;

/**
 * 费用明细 Service 接口
 * 迁移自旧系统 PrExpenseService
 */
public interface IPrExpenseService extends IService<PrExpense> {

    /**
     * 分页查询费用明细列表
     */
    TableDataInfo<PrExpenseVo> selectPageList(String companyId, String orgId, String buildingId, String unitCode,
                                              String itemGroup, String itemCode, String search, String isCharged,
                                              String parkingId, String startTime, String endTime,
                                              String startDate, String endDate, PageQuery pageQuery);

    /**
     * 查询房屋费用明细列表
     */
    List<PrExpenseVo> selectHouseExpenseList(String companyId, String orgId, String buildingId, String unitCode,
                                             String itemGroup, String itemCode, String search);

    /**
     * 查询房屋取暖费明细
     */
    PrExpenseVo selectHeatExpenseByHouseId(String houseId);

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
    boolean recalculate(String companyId, String orgId);

    /**
     * 重新计算车位费用明细
     */
    boolean recalculateCw(String companyId, String orgId);

    /**
     * 设置计算状态
     */
    boolean setCalStatus(String houseId);

    /**
     * 批量更新阶梯单价
     */
    boolean updateStepPrice(String companyId, String orgId);

    /**
     * 批量更新单价
     */
    boolean updatePrice(String companyId, String orgId);

    /**
     * 批量计算公式
     */
    boolean updateFormula(String companyId, String orgId);

    /**
     * 批量计算公式（车位）
     */
    boolean updateFormulaCw(String companyId, String orgId);
}
