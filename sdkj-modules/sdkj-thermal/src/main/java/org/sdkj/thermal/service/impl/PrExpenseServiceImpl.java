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
import org.sdkj.thermal.domain.PrStandardPrice;
import org.sdkj.thermal.domain.dto.MarkedPaymentResult;
import org.sdkj.thermal.domain.vo.PrExpenseVo;
import org.sdkj.thermal.mapper.PrExpenseMapper;
import org.sdkj.thermal.mapper.PrStandardMapper;
import org.sdkj.thermal.service.IPrExpenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 费用明细 Service 实现
 * 迁移自旧系统 PrExpenseServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrExpenseServiceImpl extends ServiceImpl<PrExpenseMapper, PrExpense> implements IPrExpenseService {

    private final PrExpenseMapper baseMapper;
    private final org.sdkj.thermal.mapper.PrStandardMapper standardMapper;

    private static final Pattern FORMULA_PATTERN = Pattern.compile("^[a-zA-Z0-9._+\\-*/()\\s]+$");

    private void validateFormula(String formula) {
        if (formula == null || !FORMULA_PATTERN.matcher(formula).matches()) {
            throw new IllegalArgumentException("公式格式不合法: " + formula);
        }
    }

    @Override
    public TableDataInfo<PrExpenseVo> selectPageList(String orgId, String buildingId, String unitCode,
                                                     String itemGroup, String itemCode, String search, String isCharged,
                                                     String parkingId, String startTime, String endTime,
                                                     String startDate, String endDate, PageQuery pageQuery) {
        Page<PrExpenseVo> page = pageQuery.build();
        baseMapper.selectPageList(page, orgId, buildingId, unitCode,
            itemGroup, itemCode, search, isCharged, parkingId, startTime, endTime);
        return TableDataInfo.build(page);
    }

    @Override
    public List<PrExpenseVo> selectHouseExpenseList(String orgId, String buildingId, String unitCode,
                                                    String itemGroup, String itemCode, String search) {
        return baseMapper.selectHouseExpenseList(orgId, buildingId, unitCode, itemGroup, itemCode, search);
    }

    @Override
    public PrExpenseVo selectHeatExpenseByHouseId(Long houseId) {
        return baseMapper.selectHeatExpenseByHouseId(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(List<PrHouseExpense> list) {
        if (list == null || list.isEmpty()) return false;

        List<PrExpense> expenses = new ArrayList<>();
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (PrHouseExpense he : list) {
            if (he.getHouseId() == null || he.getStandardId() == null) continue;

            PrStandard std = baseMapper.selectStandardById(he.getStandardId());
            if (std == null) {
                continue;
            }

            int months = calcCycleMonths(he.getOpenTime(), he.getCloseTime());
            if (months <= 0) {
                continue;
            }

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
                e.setRecordId(null);
                e.setChargedTime(null);
                e.setPaidIn(BigDecimal.ZERO);
                e.setReceivable(BigDecimal.ZERO);
                e.setMoney(BigDecimal.ZERO);
                e.setPreferential(BigDecimal.ZERO);
                e.setDeduction(BigDecimal.ZERO);
                e.setLatefee(BigDecimal.ZERO);
                e.setFinalMoney(BigDecimal.ZERO);
                e.setOverdueDay(0);
                e.setCreateBy(userId);
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

        List<PrExpense> expenses = new ArrayList<>();
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (PrHouseExpense he : list) {
            if (he.getHouseId() == null || he.getStandardId() == null) continue;

            PrStandard std = baseMapper.selectStandardById(he.getStandardId());
            if (std == null) {
                continue;
            }

            // 获取标准单价列表
            List<PrStandardPrice> priceList = standardMapper.selectPriceListAll(he.getStandardId());

            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            // 根据生成规则生成费用明细
            String generateRule = std.getGenerateRule();
            Integer cycles = std.getCycles() != null ? std.getCycles() : 1;

            if ("1".equals(generateRule) || "2".equals(generateRule)) {
                // 规则1: 月对月, 规则2: 自然月
                for (int i = 0; i < cycles; i++) {
                    PrExpense e = new PrExpense();
                    e.setHouseId(he.getHouseId());
                    e.setItemGroup(he.getItemGroup());
                    e.setItemCode(he.getItemCode());
                    e.setItemName(he.getItemName() != null ? he.getItemName() : std.getName());
                    e.setStandardId(he.getStandardId());

                    if ("1".equals(generateRule)) {
                        // 月对月
                        calStart.setTime(he.getOpenTime());
                        calStart.add(Calendar.MONTH, i);
                        calEnd.setTime(he.getOpenTime());
                        calEnd.add(Calendar.MONTH, i + 1);
                    } else {
                        // 自然月
                        calStart.setTime(he.getOpenTime());
                        calStart.add(Calendar.MONTH, i);
                        calEnd.setTime(he.getOpenTime());
                        calEnd.add(Calendar.MONTH, i);
                        calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }

                    e.setStartDate(calStart.getTime());
                    e.setExpireDate(calEnd.getTime());
                    e.setLastDate(calStart.getTime());
                    e.setYear(String.valueOf(calStart.get(Calendar.YEAR)));
                    e.setMonth(String.valueOf(calStart.get(Calendar.MONTH) + 1));
                    e.setQty(1);

                    if (priceList != null && !priceList.isEmpty()) {
                        e.setPriceFormula(priceList.get(0).getPriceFormula());
                        e.setMoney(priceList.get(0).getStandardPrice());
                    }
                    e.setStandardPrice(std.getStandardPrice());
                    e.setMaxMoney(std.getMaxMoney());
                    e.setMoneyFormula(std.getMoneyFormula());
                    e.setIsCalc("0");
                    e.setIsFree(0);
                    e.setIsCharged(0);
                    e.setIsClosed(0);
                    e.setOrgId(he.getOrgId());
                    e.setPaidIn(BigDecimal.ZERO);
                    e.setPreferential(BigDecimal.ZERO);
                    e.setDeduction(BigDecimal.ZERO);
                    e.setLatefee(BigDecimal.ZERO);
                    e.setFinalMoney(BigDecimal.ZERO);
                    e.setReceivable(BigDecimal.ZERO);
                    e.setOverdueDay(0);
                    e.setCreateBy(userId);
                    e.setCreateTime(now);

                    expenses.add(e);
                }
            } else if ("3".equals(generateRule)) {
                // 规则3: 固定期限
                PrExpense e = new PrExpense();
                e.setHouseId(he.getHouseId());
                e.setItemGroup(he.getItemGroup());
                e.setItemCode(he.getItemCode());
                e.setItemName(he.getItemName() != null ? he.getItemName() : std.getName());
                e.setStandardId(he.getStandardId());
                e.setStartDate(he.getOpenTime());
                e.setExpireDate(he.getCloseTime());
                e.setLastDate(he.getOpenTime());

                // 计算月数
                int months = calcCycleMonths(he.getOpenTime(), he.getCloseTime());
                e.setQty(months > 0 ? months : 1);

                e.setPriceFormula("pr_expense.standard_price");
                e.setStandardPrice(std.getStandardPrice());
                e.setReceivable(std.getStandardPrice());
                e.setFinalMoney(std.getStandardPrice());
                e.setMoneyFormula("pr_expense.standard_price");
                e.setMaxMoney(std.getMaxMoney());
                e.setIsCalc("1");
                e.setIsFree(0);
                e.setIsCharged(0);
                e.setIsClosed(0);
                e.setOrgId(he.getOrgId());
                e.setPaidIn(BigDecimal.ZERO);
                e.setPreferential(BigDecimal.ZERO);
                e.setDeduction(BigDecimal.ZERO);
                e.setLatefee(BigDecimal.ZERO);
                e.setOverdueDay(0);
                e.setCreateBy(userId);
                e.setCreateTime(now);

                // 设置年月
                if (he.getOpenTime() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(he.getOpenTime());
                    e.setYear(String.valueOf(cal.get(Calendar.YEAR)));
                    e.setMonth(String.valueOf(cal.get(Calendar.MONTH) + 1));
                }

                expenses.add(e);
            } else if ("4".equals(generateRule)) {
                // 规则4: 按年生成
                for (int i = 0; i < cycles; i++) {
                    PrExpense e = new PrExpense();
                    e.setHouseId(he.getHouseId());
                    e.setItemGroup(he.getItemGroup());
                    e.setItemCode(he.getItemCode());
                    e.setItemName(he.getItemName() != null ? he.getItemName() : std.getName());
                    e.setStandardId(he.getStandardId());

                    calStart.setTime(he.getOpenTime());
                    calStart.add(Calendar.MONTH, i * 12);
                    calEnd.setTime(he.getOpenTime());
                    calEnd.add(Calendar.MONTH, i * 12 + 12);

                    e.setStartDate(calStart.getTime());
                    e.setExpireDate(calEnd.getTime());
                    e.setLastDate(calStart.getTime());
                    e.setYear(String.valueOf(calStart.get(Calendar.YEAR)));
                    e.setMonth(String.valueOf(calStart.get(Calendar.MONTH) + 1));
                    e.setQty(12);

                    if (priceList != null && !priceList.isEmpty()) {
                        e.setPriceFormula(priceList.get(0).getPriceFormula());
                        e.setMoney(priceList.get(0).getStandardPrice());
                    }
                    e.setStandardPrice(std.getStandardPrice());
                    e.setMaxMoney(std.getMaxMoney());
                    e.setMoneyFormula(std.getMoneyFormula());
                    e.setIsCalc("0");
                    e.setIsFree(0);
                    e.setIsCharged(0);
                    e.setIsClosed(0);
                    e.setOrgId(he.getOrgId());
                    e.setPaidIn(BigDecimal.ZERO);
                    e.setPreferential(BigDecimal.ZERO);
                    e.setDeduction(BigDecimal.ZERO);
                    e.setLatefee(BigDecimal.ZERO);
                    e.setFinalMoney(BigDecimal.ZERO);
                    e.setReceivable(BigDecimal.ZERO);
                    e.setOverdueDay(0);
                    e.setCreateBy(userId);
                    e.setCreateTime(now);

                    expenses.add(e);
                }
            } else if ("5".equals(generateRule)) {
                // 规则5: 按季度生成
                for (int i = 0; i < cycles; i++) {
                    PrExpense e = new PrExpense();
                    e.setHouseId(he.getHouseId());
                    e.setItemGroup(he.getItemGroup());
                    e.setItemCode(he.getItemCode());
                    e.setItemName(he.getItemName() != null ? he.getItemName() : std.getName());
                    e.setStandardId(he.getStandardId());

                    calStart.setTime(he.getOpenTime());
                    calStart.add(Calendar.MONTH, i * 3);
                    calEnd.setTime(he.getOpenTime());
                    calEnd.add(Calendar.MONTH, i * 3 + 3);

                    e.setStartDate(calStart.getTime());
                    e.setExpireDate(calEnd.getTime());
                    e.setLastDate(calStart.getTime());
                    e.setYear(String.valueOf(calStart.get(Calendar.YEAR)));
                    e.setMonth(String.valueOf(calStart.get(Calendar.MONTH) + 1));
                    e.setQty(3);

                    if (priceList != null && !priceList.isEmpty()) {
                        e.setPriceFormula(priceList.get(0).getPriceFormula());
                        e.setMoney(priceList.get(0).getStandardPrice());
                    }
                    e.setStandardPrice(std.getStandardPrice());
                    e.setMaxMoney(std.getMaxMoney());
                    e.setMoneyFormula(std.getMoneyFormula());
                    e.setIsCalc("0");
                    e.setIsFree(0);
                    e.setIsCharged(0);
                    e.setIsClosed(0);
                    e.setOrgId(he.getOrgId());
                    e.setPaidIn(BigDecimal.ZERO);
                    e.setPreferential(BigDecimal.ZERO);
                    e.setDeduction(BigDecimal.ZERO);
                    e.setLatefee(BigDecimal.ZERO);
                    e.setFinalMoney(BigDecimal.ZERO);
                    e.setReceivable(BigDecimal.ZERO);
                    e.setOverdueDay(0);
                    e.setCreateBy(userId);
                    e.setCreateTime(now);

                    expenses.add(e);
                }
            } else if ("6".equals(generateRule)) {
                // 规则6: 按半年生成
                for (int i = 0; i < cycles; i++) {
                    PrExpense e = new PrExpense();
                    e.setHouseId(he.getHouseId());
                    e.setItemGroup(he.getItemGroup());
                    e.setItemCode(he.getItemCode());
                    e.setItemName(he.getItemName() != null ? he.getItemName() : std.getName());
                    e.setStandardId(he.getStandardId());

                    calStart.setTime(he.getOpenTime());
                    calStart.add(Calendar.MONTH, i * 6);
                    calEnd.setTime(he.getOpenTime());
                    calEnd.add(Calendar.MONTH, i * 6 + 6);

                    e.setStartDate(calStart.getTime());
                    e.setExpireDate(calEnd.getTime());
                    e.setLastDate(calStart.getTime());
                    e.setYear(String.valueOf(calStart.get(Calendar.YEAR)));
                    e.setMonth(String.valueOf(calStart.get(Calendar.MONTH) + 1));
                    e.setQty(6);

                    if (priceList != null && !priceList.isEmpty()) {
                        e.setPriceFormula(priceList.get(0).getPriceFormula());
                        e.setMoney(priceList.get(0).getStandardPrice());
                    }
                    e.setStandardPrice(std.getStandardPrice());
                    e.setMaxMoney(std.getMaxMoney());
                    e.setMoneyFormula(std.getMoneyFormula());
                    e.setIsCalc("0");
                    e.setIsFree(0);
                    e.setIsCharged(0);
                    e.setIsClosed(0);
                    e.setOrgId(he.getOrgId());
                    e.setPaidIn(BigDecimal.ZERO);
                    e.setPreferential(BigDecimal.ZERO);
                    e.setDeduction(BigDecimal.ZERO);
                    e.setLatefee(BigDecimal.ZERO);
                    e.setFinalMoney(BigDecimal.ZERO);
                    e.setReceivable(BigDecimal.ZERO);
                    e.setOverdueDay(0);
                    e.setCreateBy(userId);
                    e.setCreateTime(now);

                    expenses.add(e);
                }
            }

            // 每 10000 条批量插入一次
            if (expenses.size() >= 10000) {
                saveBatch(expenses, 500);
                expenses.clear();
            }
        }

        if (!expenses.isEmpty()) {
            return saveBatch(expenses, 500);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDataLs(List<PrHouseExpense> list) {
        if (list == null || list.isEmpty()) return false;

        List<PrExpense> expenses = new ArrayList<>();
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

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
            e.setPaidIn(BigDecimal.ZERO);
            e.setPreferential(BigDecimal.ZERO);
            e.setDeduction(BigDecimal.ZERO);
            e.setLatefee(BigDecimal.ZERO);
            e.setFinalMoney(BigDecimal.ZERO);
            e.setCreateBy(userId);
            e.setCreateTime(now);
            expenses.add(e);
        }
        return saveBatch(expenses, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDatallCw(List<PmParkingSpace> list) {
        if (list == null || list.isEmpty()) return false;
        List<PrExpense> expenses = new ArrayList<>();
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (PmParkingSpace space : list) {
            PrExpense e = new PrExpense();
            e.setHouseId(space.getId());
            e.setItemGroup("parking");
            e.setItemCode("parking_fee");
            e.setItemName("车位费");
            e.setStandardId(space.getStandardId());
            e.setStartDate(now);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, 1);
            e.setExpireDate(cal.getTime());
            e.setStandardPrice(space.getStandardPrice());
            e.setMoney(space.getStandardPrice());
            e.setReceivable(space.getStandardPrice());
            e.setIsFree(0);
            e.setIsCharged(0);
            e.setIsClosed(0);
            e.setIsCalc("1");
            e.setOrgId(space.getOrgId());
            e.setPaidIn(BigDecimal.ZERO);
            e.setPreferential(BigDecimal.ZERO);
            e.setDeduction(BigDecimal.ZERO);
            e.setLatefee(BigDecimal.ZERO);
            e.setFinalMoney(BigDecimal.ZERO);
            e.setOverdueDay(0);
            e.setCreateBy(userId);
            e.setCreateTime(now);
            expenses.add(e);
        }
        return saveBatch(expenses, 500);
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
                preferential = money.multiply(scaleBd).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            } else if ("金额".equals(type) && priceBd != null) {
                preferential = priceBd;
            } else if (scaleBd != null) {
                // Fallback: if type is not explicitly matched but scale is provided, treat as percentage
                preferential = money.multiply(scaleBd).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
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
        List<Long> ids = list.stream().map(PrExpense::getId).toList();
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
    public boolean recalculate(String orgId) {
        boolean a = baseMapper.updateStepPrice(orgId) > 0;
        a &= baseMapper.updateMoney(orgId) > 0;
        a &= baseMapper.updateFormula(orgId) > 0;
        return a;
    }

    @Override
    public boolean recalculateCw(String orgId) {
        return baseMapper.updateFormulaCw(orgId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setCalStatus(Long houseId) {
        return baseMapper.updateCalStatus(houseId) > 0;
    }

    @Override
    public boolean updateStepPrice(String orgId) {
        // 查询所有收费标准
        List<org.sdkj.thermal.domain.vo.PrStandardVo> standardList = standardMapper.selectPageList(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 1000), orgId, null);

        if (standardList == null || standardList.isEmpty()) return false;

        boolean result = true;
        for (org.sdkj.thermal.domain.vo.PrStandardVo standardVo : standardList) {
            PrStandard standard = standardMapper.selectById(standardVo.getId());
            if (standard == null) {
                continue;
            }

            // 取暖费、临时费用不计算单价
            if ("6".equals(standard.getItemGroup()) || "3".equals(standard.getItemGroup())) {
                continue;
            }

            // 没有阶梯（step_maxgrade = 1）
            if (standard.getStepMaxgrade() == null || standard.getStepMaxgrade() == 1) {
                // 设置基本单价（已经在 updateStepPrice SQL 中处理）
                result &= baseMapper.updateStepPrice(orgId) > 0;
            }
            // 建筑面积阶梯
            else if ("1".equals(standard.getStepType())) {
                result &= baseMapper.setStandardPriceJzmj(standard.getId()) > 0;
            }
            // 使用面积阶梯
            else if ("2".equals(standard.getStepType())) {
                result &= baseMapper.setStandardPriceSymj(standard.getId()) > 0;
            }
            // 楼层阶梯
            else if ("3".equals(standard.getStepType())) {
                result &= baseMapper.setStandardPriceLc(standard.getId()) > 0;
            }
        }
        return result;
    }

    @Override
    public boolean updatePrice(String orgId) {
        return baseMapper.updateMoney(orgId) > 0;
    }

    @Override
    public boolean updateFormula(String orgId) {
        return baseMapper.updateFormula(orgId) > 0;
    }

    @Override
    public boolean updateFormulaCw(String orgId) {
        return baseMapper.updateFormulaCw(orgId) > 0;
    }

    @Override
    public TableDataInfo<PrExpenseVo> parkingList(String orgId, String buildingId,
            String unitCode, String itemGroup, String itemCode, String search, String isCharged,
            String parkingId, String startTime, String endTime, PageQuery pageQuery) {
        Page<PrExpenseVo> page = pageQuery.build();
        baseMapper.selectParkingPageList(page, orgId, buildingId, unitCode,
            itemGroup, itemCode, search, isCharged, parkingId, startTime, endTime);
        return TableDataInfo.build(page);
    }

    @Override
    public List<Map<String, Object>> parkingExpenseList(String orgId, String buildingId,
            String unitCode, String itemGroup, String itemCode, String parkingId, String search) {
        return baseMapper.selectParkingSpaceExpenseList(orgId, buildingId, unitCode,
            itemGroup, itemCode, parkingId, search);
    }

    @Override
    public List<Map<String, Object>> houseExpenseAllList(String orgId, String search) {
        return baseMapper.selectHouseExpenseAllList(orgId, search);
    }

    @Override
    public TableDataInfo<Map<String, Object>> expenseLog(String orgId, String buildingId,
            String unitCode, String parentId, String type, String startTime, String endTime, String search,
            PageQuery pageQuery) {
        Page<Map<String, Object>> page = pageQuery.build();
        baseMapper.selectExpenseLog(page, orgId, buildingId,
            unitCode, parentId, type, startTime, endTime, search);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<Map<String, Object>> wechatOrderList(String orgId, String buildingId,
            String unitCode, String parentId, String type, String startTime, String endTime, String search,
            PageQuery pageQuery) {
        Page<Map<String, Object>> page = pageQuery.build();
        baseMapper.selectWechatOrderList(page, orgId, buildingId,
            unitCode, parentId, type, startTime, endTime, search);
        return TableDataInfo.build(page);
    }

    // ========== 滞纳金计算方法实现 ==========

    @Override
    public boolean updateLatefeeQs(String orgId, String latefeeFormula, Long standardId) {
        validateFormula(latefeeFormula);
        boolean result = baseMapper.updateLatefeeQs(orgId, latefeeFormula, standardId) > 0;
        // 计算滞纳金后更新最终金额
        if (result) {
            baseMapper.updateFinalMoneyAfterLateFee(orgId, standardId);
        }
        return result;
    }

    @Override
    public boolean updateLatefeeJs(String orgId, String latefeeFormula, Long standardId) {
        validateFormula(latefeeFormula);
        boolean result = baseMapper.updateLatefeeJs(orgId, latefeeFormula, standardId) > 0;
        // 计算滞纳金后更新最终金额
        if (result) {
            baseMapper.updateFinalMoneyAfterLateFee(orgId, standardId);
        }
        return result;
    }

    @Override
    public boolean updateLatefeeZd(String orgId, String latefeeFormula, Long standardId,
                                    java.util.Date latefeeStartdate) {
        validateFormula(latefeeFormula);
        boolean result = baseMapper.updateLatefeeZd(orgId, latefeeFormula, standardId, latefeeStartdate) > 0;
        // 计算滞纳金后更新最终金额
        if (result) {
            baseMapper.updateFinalMoneyAfterLateFee(orgId, standardId);
        }
        return result;
    }

    @Override
    public boolean updateLatefeeSJHC(String orgId, String latefeeFormula, Long standardId,
                                      String year, String month) {
        validateFormula(latefeeFormula);
        boolean result = baseMapper.updateLatefeeSJHC(orgId, latefeeFormula, standardId, year, month) > 0;
        // 计算滞纳金后更新最终金额
        if (result) {
            baseMapper.updateFinalMoneyAfterLateFee(orgId, standardId);
        }
        return result;
    }

    @Override
    public boolean updateFinalMoneyAfterLateFee(String orgId, Long standardId) {
        return baseMapper.updateFinalMoneyAfterLateFee(orgId, standardId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarkedPaymentResult markPaidFromAi(Long expenseId, String note, Long operatorId) {
        if (expenseId == null) {
            throw new IllegalArgumentException("费用条目 ID 不能为空");
        }

        PrExpense expense = baseMapper.selectById(expenseId);
        if (expense == null) {
            throw new IllegalArgumentException("费用条目 " + expenseId + " 不存在，请核实后重试");
        }

        if (Integer.valueOf(1).equals(expense.getIsCharged())) {
            throw new IllegalArgumentException("费用条目 " + expenseId + " 已是缴费状态，无需重复标记");
        }

        expense.setIsCharged(1);
        expense.setChargedTime(new Date());
        if (expense.getReceivable() != null) {
            expense.setPaidIn(expense.getReceivable());
        }
        expense.setUpdateBy(operatorId);

        baseMapper.updateById(expense);

        String summary = String.format("费用条目 %s 已标记为缴费，金额 %s",
            expenseId,
            expense.getFinalMoney() != null ? expense.getFinalMoney().toPlainString() + " 元" : "未知");

        return new MarkedPaymentResult(expenseId, expense.getFinalMoney(), summary, "PAID");
    }
}
