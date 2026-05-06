package org.sdkj.thermal.service.impl;

import org.sdkj.common.core.exception.ServiceException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.vo.PrExpenseItemVo;
import org.sdkj.thermal.mapper.PrExpenseItemMapper;
import org.sdkj.thermal.mapper.PrStandardMapper;
import org.sdkj.thermal.service.IPrExpenseItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 费用项目 Service 实现
 * 迁移自旧系统 PrExpenseItemServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrExpenseItemServiceImpl extends ServiceImpl<PrExpenseItemMapper, PrExpenseItem> implements IPrExpenseItemService {

    private final PrExpenseItemMapper baseMapper;
    private final PrStandardMapper standardMapper;

    @Override
    public TableDataInfo<PrExpenseItemVo> selectPageList(String orgId, String itemGroup, PageQuery pageQuery) {
        Page<PrExpenseItemVo> page = pageQuery.build();
        baseMapper.selectPageList(page, orgId, itemGroup);
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(PrExpenseItem item) {
        // 自动编号：非分组5时，取该分组下最大 itemCode + 1
        if (!"5".equals(item.getItemGroup())) {
            String maxCode = baseMapper.queryMaxItemCode(item.getItemGroup(), item.getOrgId());
            String nextCode = (maxCode == null)
                ? item.getItemGroup() + "01"
                : String.valueOf(Integer.parseInt(maxCode) + 1);
            item.setItemCode(nextCode);
        }
        item.setCreateBy(LoginHelper.getUserId());
        return save(item);
    }

    @Override
    public PrExpenseItemVo selectById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<PrExpenseItemVo> selectByItemCode(String companyId, String orgId, String itemGroup, String itemCode) {
        return baseMapper.selectByItemCode(companyId, orgId, itemGroup, itemCode);
    }

    @Override
    public List<PrExpenseItemVo> selectByItemGroup(String companyId, String orgId, String itemGroup, String userId) {
        if (StrUtil.isNotBlank(userId)) {
            return baseMapper.selectByItemGroupAndUserId(companyId, orgId, itemGroup, userId);
        }
        return baseMapper.selectByItemGroup(companyId, orgId, itemGroup);
    }

    @Override
    public List<PrExpenseItemVo> getItemCodesByItemGroup(String companyId, String orgId, String itemGroup) {
        return baseMapper.selectByItemGroup(companyId, orgId, itemGroup);
    }

    @Override
    public List<PrExpenseItemVo> selectByCompanyAndOrg(String companyId, String orgId) {
        return baseMapper.selectByCompanyAndOrg(companyId, orgId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData(String id, String itemCode, String orgId) {
        // 检查该收费项目下是否存在收费标准
        long count = standardMapper.countByItemCode(itemCode, orgId);
        if (count > 0) {
            throw new ServiceException("该收费项目下存在收费标准，无法删除！");
        }
        return removeById(id);
    }

    @Override
    public boolean isItemName(String companyId, String orgId, String itemName, String id) {
        List<PrExpenseItemVo> duplicates = baseMapper.checkItemName(companyId, orgId, itemName, id);
        return duplicates.isEmpty();
    }
}
