package org.dromara.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.thermal.domain.PrExpenseItem;
import org.dromara.thermal.domain.PrStandard;
import org.dromara.thermal.domain.PrStandardPrice;
import org.dromara.thermal.domain.vo.PrStandardVo;
import org.dromara.thermal.mapper.PrStandardMapper;
import org.dromara.thermal.service.IPrStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 收费标准 Service 实现
 * 迁移自旧系统 PrStandardServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrStandardServiceImpl extends ServiceImpl<PrStandardMapper, PrStandard> implements IPrStandardService {

    private final PrStandardMapper baseMapper;

    @Override
    public TableDataInfo<PrStandardVo> selectPageList(String orgId, String type, PageQuery pageQuery) {
        List<PrStandardVo> list = baseMapper.selectPageList(orgId, type);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrStandardVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrStandardVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(PrStandard standard) {
        standard.setCreateBy(LoginHelper.getUserId());
        boolean saved = save(standard);
        if (saved && standard.getPrStandardPrice() != null && !standard.getPrStandardPrice().isEmpty()) {
            for (PrStandardPrice price : standard.getPrStandardPrice()) {
                price.setStandardId(standard.getId());
                price.setCreateBy(LoginHelper.getUserId());
            }
            baseMapper.insertPriceList(standard.getPrStandardPrice());
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateData(PrStandard standard) {
        PrStandard existing = getById(standard.getId());
        if (existing == null) {
            throw new RuntimeException("收费标准不存在");
        }
        boolean updated = updateById(standard);
        if (updated) {
            // 删除旧的单价数据，重新插入
            baseMapper.deletePricesByStandardId(standard.getId());
            if (standard.getPrStandardPrice() != null && !standard.getPrStandardPrice().isEmpty()) {
                for (PrStandardPrice price : standard.getPrStandardPrice()) {
                    price.setStandardId(standard.getId());
                    price.setUpdateBy(LoginHelper.getUserId());
                }
                baseMapper.insertPriceList(standard.getPrStandardPrice());
            }
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData(String id) {
        // 检查是否被房屋、水表、电表、热表关联
        int houseCount = baseMapper.countHouseBindings(id);
        if (houseCount > 0) {
            throw new RuntimeException("该收费标准已关联房屋，无法删除");
        }
        // 删除关联的单价
        baseMapper.deletePricesByStandardId(id);
        return removeById(id);
    }

    @Override
    public PrStandard selectDetailById(String id) {
        PrStandard standard = getById(id);
        if (standard != null) {
            List<PrStandardPrice> prices = baseMapper.selectPriceListAll(id);
            standard.setPrStandardPrice(prices);
        }
        return standard;
    }

    @Override
    public List<PrStandardVo> selectByItemCode(String itemCode, String orgId) {
        return baseMapper.selectByItemCode(itemCode, orgId);
    }

    @Override
    public List<PrStandardVo> findEleStandard(String companyId, String orgId) {
        return baseMapper.selectEleStandard(companyId, orgId);
    }

    @Override
    public List<PrStandardVo> findWaterStandard(String companyId, String orgId) {
        return baseMapper.selectWaterStandard(companyId, orgId);
    }

    @Override
    public List<PrStandardVo> findHeatStandard(String companyId, String orgId) {
        return baseMapper.selectHeatStandard(companyId, orgId);
    }

    @Override
    public boolean isName(String companyId, String orgId, String itemGroup, String name, String id) {
        return baseMapper.checkName(companyId, orgId, itemGroup, name, id).isEmpty();
    }

    @Override
    public PurchaseCheckResult checkPurchase(String meterId, String standardId, String companyId, String orgId, BigDecimal amount) {
        PrStandard standard = getById(standardId);
        if (standard == null) {
            return new PurchaseCheckResult(false, null);
        }

        // 判断是否超出单次最大金额
        if (standard.getLimitedSingleMoney() != null && standard.getLimitedSingleMoney().compareTo(BigDecimal.ZERO) > 0) {
            if (amount.compareTo(standard.getLimitedSingleMoney()) > 0) {
                return new PurchaseCheckResult(true, "超出单次购买最大金额！");
            }
        }

        // 判断是否限购
        if (standard.getIsLimited() == null || standard.getIsLimited() != 1) {
            return new PurchaseCheckResult(false, null);
        }

        // 按月
        if ("1".equals(standard.getLimitedType())) {
            int num = baseMapper.getPurchaseNumMonth(meterId, companyId, orgId);
            if (num >= standard.getLimitedTimes()) {
                return new PurchaseCheckResult(true, "超出限购次数，本月不可再次购买！");
            }
            BigDecimal usedAmount = baseMapper.getPurchaseAmountMonth(meterId, companyId, orgId);
            BigDecimal remaining = standard.getLimitedMoney().subtract(usedAmount).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(remaining) > 0) {
                return new PurchaseCheckResult(true, "超出限购金额，本月还可购买" + remaining + "元。");
            }
        }
        // 按季度
        else if ("2".equals(standard.getLimitedType())) {
            int num = baseMapper.getPurchaseNumQuarter(meterId, companyId, orgId);
            if (num >= standard.getLimitedTimes()) {
                return new PurchaseCheckResult(true, "超出限购次数，本季度不可再次购买！");
            }
            BigDecimal usedAmount = baseMapper.getPurchaseAmountQuarter(meterId, companyId, orgId);
            BigDecimal remaining = standard.getLimitedMoney().subtract(usedAmount).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(remaining) > 0) {
                return new PurchaseCheckResult(true, "超出限购金额，本季度还可购买" + remaining + "元。");
            }
        }
        // 按年
        else if ("3".equals(standard.getLimitedType())) {
            int num = baseMapper.getPurchaseNumYear(meterId, companyId, orgId);
            if (num >= standard.getLimitedTimes()) {
                return new PurchaseCheckResult(true, "超出限购次数，本年度不可再次购买！");
            }
            BigDecimal usedAmount = baseMapper.getPurchaseAmountYear(meterId, companyId, orgId);
            BigDecimal remaining = standard.getLimitedMoney().subtract(usedAmount).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(remaining) > 0) {
                return new PurchaseCheckResult(true, "超出限购金额，本年度还可购买" + remaining + "元。");
            }
        }

        return new PurchaseCheckResult(false, null);
    }

    @Override
    public PrExpenseItem getExpenseItemByStandardId(String companyId, String orgId, String standardId) {
        return baseMapper.selectExpenseItemByStandardId(companyId, orgId, standardId);
    }

    @Override
    public TableDataInfo<PrStandardVo> selectByItemName(String orgIdCopy, String itemName, PageQuery pageQuery) {
        List<PrStandardVo> list = baseMapper.selectByItemName(orgIdCopy, itemName);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrStandardVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrStandardVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }
}
