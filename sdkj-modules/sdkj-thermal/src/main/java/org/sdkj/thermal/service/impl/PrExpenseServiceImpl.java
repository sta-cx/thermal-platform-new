package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.PmParkingSpace;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.vo.PrExpenseVo;
import org.sdkj.thermal.mapper.PrExpenseMapper;
import org.sdkj.thermal.service.IPrExpenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 费用明细 Service 实现
 * 迁移自旧系统 PrExpenseServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrExpenseServiceImpl extends ServiceImpl<PrExpenseMapper, PrExpense> implements IPrExpenseService {

    private final PrExpenseMapper baseMapper;

    @Override
    public TableDataInfo<PrExpenseVo> selectPageList(String companyId, String orgId, String buildingId, String unitCode,
                                                     String itemGroup, String itemCode, String search, String isCharged,
                                                     String parkingId, String startTime, String endTime,
                                                     String startDate, String endDate, PageQuery pageQuery) {
        List<PrExpenseVo> list = baseMapper.selectPageList(companyId, orgId, buildingId, unitCode,
            itemGroup, itemCode, search, isCharged, parkingId, startTime, endTime);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrExpenseVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrExpenseVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }

    @Override
    public List<PrExpenseVo> selectHouseExpenseList(String companyId, String orgId, String buildingId, String unitCode,
                                                    String itemGroup, String itemCode, String search) {
        return baseMapper.selectHouseExpenseList(companyId, orgId, buildingId, unitCode, itemGroup, itemCode, search);
    }

    @Override
    public PrExpenseVo selectHeatExpenseByHouseId(String houseId) {
        return baseMapper.selectHeatExpenseByHouseId(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(List<PrHouseExpense> list) {
        if (list == null || list.isEmpty()) return false;

        List<PrExpense> expenses = new ArrayList<>();
        Date now = new Date();
        String userId = String.valueOf(LoginHelper.getUserId());

        for (PrHouseExpense he : list) {
            if (he.getHouseId() == null || he.getStandardId() == null) continue;

            PrStandard std = baseMapper.selectStandardById(he.getStandardId());
            if (std == null) continue;

            int months = calcCycleMonths(he.getOpenTime(), he.getCloseTime());
            if (months <= 0) continue;

            for (int i = 0; i < months; i++) {
                PrExpense e = new PrExpense();
                e.setHouseId(he.getHouseId());
                e.setItemGroup(he.getItemGroup());
                e.setItemCode(he.getItemCode());
                e.setItemName(std.getName());
                e.setStandardId(he.getStandardId());

                Date monthStart = addMonths(he.getOpenTime(), i);
                Date monthEnd = addMonths(he.getOpenTime(), i + 1);
                e.setStartDate(monthStart);
                e.setExpireDate(monthEnd);

                e.setStandardPrice(std.getStandardPrice());
                e.setMaxMoney(std.getMaxMoney());
                e.setMoneyFormula(std.getMoneyFormula());
                e.setIsFree(0);
                e.setIsCharged(0);
                e.setIsClosed(0);
                e.setIsCalc("0");
                e.setOrgId(he.getOrgId());
                e.setCompanyId(he.getCompanyId());
                e.setRecordId("");
                e.setChargedTime(null);
                e.setPaidIn(BigDecimal.ZERO);
                e.setReceivable(BigDecimal.ZERO);
                e.setMoney(BigDecimal.ZERO);
                e.setPreferential(BigDecimal.ZERO);
                e.setDeduction(BigDecimal.ZERO);
                e.setLatefee(BigDecimal.ZERO);
                e.setFinalMoney(BigDecimal.ZERO);
                e.setOverdueDay(0);
                e.setCreateBy(Long.valueOf(userId));
                e.setCreateTime(now);

                expenses.add(e);
            }
        }

        if (expenses.isEmpty()) return false;
        return saveBatch(expenses, 500);
    }

    private int calcCycleMonths(Date start, Date end) {
        if (start == null || end == null) return 0;
        long diff = end.getTime() - start.getTime();
        long days = diff / (1000L * 60 * 60 * 24);
        return (int) Math.max(1, Math.ceil(days / 30.0));
    }

    private Date addMonths(Date date, int months) {
        if (date == null) return null;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.add(java.util.Calendar.MONTH, months);
        return cal.getTime();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDatall(List<PrHouseExpense> list) {
        if (list == null || list.isEmpty()) return false;
        return insertData(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDataLs(List<PrHouseExpense> list) {
        if (list == null || list.isEmpty()) return false;

        List<PrExpense> expenses = new ArrayList<>();
        Date now = new Date();
        String userId = String.valueOf(LoginHelper.getUserId());

        for (PrHouseExpense he : list) {
            PrExpense e = new PrExpense();
            e.setHouseId(he.getHouseId());
            e.setItemGroup(he.getItemGroup());
            e.setItemCode(he.getItemCode());
            e.setStandardId(he.getStandardId());
            e.setStartDate(he.getOpenTime());
            e.setExpireDate(he.getCloseTime());
            e.setMoney(he.getMoney() != null ? he.getMoney() : BigDecimal.ZERO);
            e.setReceivable(he.getMoney() != null ? he.getMoney() : BigDecimal.ZERO);
            e.setIsFree(0);
            e.setIsCharged(0);
            e.setIsClosed(0);
            e.setIsCalc("1");
            e.setOrgId(he.getOrgId());
            e.setCompanyId(he.getCompanyId());
            e.setPaidIn(BigDecimal.ZERO);
            e.setPreferential(BigDecimal.ZERO);
            e.setDeduction(BigDecimal.ZERO);
            e.setLatefee(BigDecimal.ZERO);
            e.setFinalMoney(BigDecimal.ZERO);
            e.setCreateBy(Long.valueOf(userId));
            e.setCreateTime(now);
            expenses.add(e);
        }
        return saveBatch(expenses, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDatallCw(List<PmParkingSpace> list) {
        // TODO: 车位费用需要完整的 pr_expense 生成逻辑
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setPreferential(List<PrExpense> list, String type, String scale, String price, String reason, String times) {
        if (list == null || list.isEmpty()) return false;
        BigDecimal scaleBd = null;
        BigDecimal priceBd = null;
        if (scale != null && !scale.isEmpty()) {
            scaleBd = new BigDecimal(scale);
        }
        if (price != null && !price.isEmpty()) {
            priceBd = new BigDecimal(price);
        }
        for (PrExpense e : list) {
            BigDecimal preferential = BigDecimal.ZERO;
            BigDecimal money = e.getMoney() != null ? e.getMoney() : BigDecimal.ZERO;
            BigDecimal deduction = e.getDeduction() != null ? e.getDeduction() : BigDecimal.ZERO;

            if ("比例".equals(type) && scaleBd != null) {
                preferential = money.multiply(scaleBd).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            } else if ("金额".equals(type) && priceBd != null) {
                preferential = priceBd;
            } else if (scaleBd != null) {
                // Fallback: if type is not explicitly matched but scale is provided, treat as percentage
                preferential = money.multiply(scaleBd).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            }

            e.setPreferential(preferential);
            if (reason != null && !reason.isEmpty()) {
                e.setReason(reason);
            }
            // Recalculate receivable = money - preferential - deduction
            BigDecimal receivable = money.subtract(preferential).subtract(deduction);
            if (receivable.compareTo(BigDecimal.ZERO) < 0) {
                receivable = BigDecimal.ZERO;
            }
            e.setReceivable(receivable);
            updateById(e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setIsFree(List<PrExpense> list, String reason, String times) {
        for (PrExpense e : list) {
            e.setIsFree(1);
            e.setReason(reason);
            updateById(e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setLastDate(List<PrExpense> list, String reason, String days) {
        if (list == null || days == null) return false;
        int d = Integer.parseInt(days);
        for (PrExpense e : list) {
            if (e.getExpireDate() != null) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(e.getExpireDate());
                cal.add(java.util.Calendar.DAY_OF_MONTH, d);
                e.setExpireDate(cal.getTime());
            }
            if (reason != null && !reason.isEmpty()) e.setReason(reason);
            updateById(e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setBaotingDate(List<PrExpense> list, String type, String scale, String price, String reason) {
        if (list == null) return false;
        for (PrExpense e : list) {
            if (scale != null && !scale.isEmpty()) {
                e.setPreferential(new BigDecimal(scale));
            }
            if (price != null && !price.isEmpty()) {
                e.setDeduction(new BigDecimal(price));
            }
            e.setIsFree(2);
            if (reason != null && !reason.isEmpty()) e.setReason(reason);
            updateById(e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setFugongDate(List<PrExpense> list, String reason, String days) {
        if (list == null) return false;
        int d = days != null && !days.isEmpty() ? Integer.parseInt(days) : 0;
        for (PrExpense e : list) {
            e.setIsFree(0);
            if (reason != null && !reason.isEmpty()) e.setReason(reason);
            if (d > 0 && e.getExpireDate() != null) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(e.getExpireDate());
                cal.add(java.util.Calendar.DAY_OF_MONTH, d);
                e.setExpireDate(cal.getTime());
            }
            updateById(e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setTuifei(List<PrExpense> list, String type, String scale, String price, String reason) {
        if (list == null) return false;
        for (PrExpense e : list) {
            if (scale != null && !scale.isEmpty()) {
                e.setPreferential(new BigDecimal(scale));
            }
            if (price != null && !price.isEmpty()) {
                e.setDeduction(new BigDecimal(price));
            }
            e.setIsFree(3);
            if (reason != null && !reason.isEmpty()) e.setReason(reason);
            updateById(e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDate(List<PrExpense> list) {
        if (list == null || list.isEmpty()) return false;
        List<String> ids = list.stream().map(PrExpense::getId).toList();
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDatall(List<PrExpense> list) {
        for (PrExpense e : list) {
            if (e.getStandardIdNew() != null) e.setStandardId(e.getStandardIdNew());
            if (e.getStandardPriceNew() != null) e.setStandardPrice(e.getStandardPriceNew());
            if (e.getMaxMoneyNew() != null) e.setMaxMoney(e.getMaxMoneyNew());
            if (e.getMoneyFormulaNew() != null) e.setMoneyFormula(e.getMoneyFormulaNew());
            updateById(e);
        }
        return true;
    }

    @Override
    public boolean recalculate(String companyId, String orgId) {
        boolean a = baseMapper.updateStepPrice(companyId, orgId) > 0;
        a &= baseMapper.updateMoney(companyId, orgId) > 0;
        a &= baseMapper.updateFormula(companyId, orgId) > 0;
        return a;
    }

    @Override
    public boolean recalculateCw(String companyId, String orgId) {
        return baseMapper.updateFormulaCw(companyId, orgId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setCalStatus(String houseId) {
        return baseMapper.updateCalStatus(houseId) > 0;
    }

    @Override
    public boolean updateStepPrice(String companyId, String orgId) {
        return baseMapper.updateStepPrice(companyId, orgId) > 0;
    }

    @Override
    public boolean updatePrice(String companyId, String orgId) {
        return baseMapper.updateMoney(companyId, orgId) > 0;
    }

    @Override
    public boolean updateFormula(String companyId, String orgId) {
        return baseMapper.updateFormula(companyId, orgId) > 0;
    }

    @Override
    public boolean updateFormulaCw(String companyId, String orgId) {
        return baseMapper.updateFormulaCw(companyId, orgId) > 0;
    }

    @Override
    public TableDataInfo<PrExpenseVo> parkingList(String companyId, String orgId, String buildingId,
            String unitCode, String itemGroup, String itemCode, String search, String isCharged,
            String parkingId, String startTime, String endTime, PageQuery pageQuery) {
        List<PrExpenseVo> list = baseMapper.selectParkingPageList(companyId, orgId, buildingId, unitCode,
            itemGroup, itemCode, search, isCharged, parkingId, startTime, endTime);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrExpenseVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrExpenseVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }

    @Override
    public List<Map<String, Object>> parkingExpenseList(String companyId, String orgId, String buildingId,
            String unitCode, String itemGroup, String itemCode, String parkingId, String search) {
        return baseMapper.selectParkingSpaceExpenseList(companyId, orgId, buildingId, unitCode,
            itemGroup, itemCode, parkingId, search);
    }

    @Override
    public List<Map<String, Object>> houseExpenseAllList(String companyId, String orgId, String search) {
        return baseMapper.selectHouseExpenseAllList(companyId, orgId, search);
    }

    @Override
    public TableDataInfo<Map<String, Object>> expenseLog(String companyId, String orgId, String buildingId,
            String unitCode, String parentId, String type, String startTime, String endTime, String search,
            PageQuery pageQuery) {
        List<Map<String, Object>> list = baseMapper.selectExpenseLog(companyId, orgId, buildingId,
            unitCode, parentId, type, startTime, endTime, search);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<Map<String, Object>> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<Map<String, Object>> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<Map<String, Object>> wechatOrderList(String companyId, String orgId, String buildingId,
            String unitCode, String parentId, String type, String startTime, String endTime, String search,
            PageQuery pageQuery) {
        List<Map<String, Object>> list = baseMapper.selectWechatOrderList(companyId, orgId, buildingId,
            unitCode, parentId, type, startTime, endTime, search);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<Map<String, Object>> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<Map<String, Object>> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }
}
