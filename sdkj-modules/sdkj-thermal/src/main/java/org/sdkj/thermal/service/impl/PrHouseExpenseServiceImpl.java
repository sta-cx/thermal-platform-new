package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;
import org.sdkj.thermal.domain.vo.PrHouseExpenseVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrHouseExpenseMapper;
import org.sdkj.thermal.service.IPrHouseExpenseService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房屋费用项目绑定 Service 实现
 * 迁移自旧系统 PrHouseExpenseServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrHouseExpenseServiceImpl extends OrgScopedServiceImpl<PrHouseExpenseMapper, PrHouseExpense> implements IPrHouseExpenseService {

    private final PrHouseExpenseMapper baseMapper;

    @Override
    public TableDataInfo<PrHouseExpenseVo> selectPageList(String orgId, String buildingId,
                                                          String unitCode, String itemGroup, String itemCode,
                                                          String search, PageQuery pageQuery) {
        Page<PrHouseExpenseVo> page = pageQuery.build();
        baseMapper.selectPageList(page, orgId, buildingId, unitCode, itemGroup, itemCode, search);
        return TableDataInfo.build(page);
    }

    @Override
    public List<PrHouseVo> selectUnboundHouses(String orgId, String buildingId, String unitCode,
                                               String itemGroup, String itemCode, String search) {
        return baseMapper.selectUnboundHouses(orgId, buildingId, unitCode, itemGroup, itemCode, search);
    }

    @Override
    public List<PrExpenseItemVo> selectUnboundItems(String orgId, String roomNum) {
        return baseMapper.selectUnboundItems(orgId, roomNum);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsert(List<PrHouseExpense> list, String itemGroup, String itemCode, String orgId) {
        for (PrHouseExpense item : list) {
            if (itemGroup != null && !itemGroup.isEmpty()) {
                item.setItemGroup(itemGroup);
            }
            if (itemCode != null && !itemCode.isEmpty()) {
                item.setItemCode(itemCode);
            }
            item.setOrgId(orgId);
        }
        return saveBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdate(List<PrHouseExpense> list) {
        return updateBatchById(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<PrHouseExpense> list) {
        if (list == null || list.isEmpty()) return false;
        List<Long> ids = list.stream().map(PrHouseExpense::getId).toList();
        return removeByIds(ids);
    }
}
