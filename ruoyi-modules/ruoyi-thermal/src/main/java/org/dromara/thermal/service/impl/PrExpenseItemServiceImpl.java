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
import org.dromara.thermal.domain.vo.PrExpenseItemVo;
import org.dromara.thermal.mapper.PrExpenseItemMapper;
import org.dromara.thermal.mapper.PrStandardMapper;
import org.dromara.thermal.service.IPrExpenseItemService;
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
        List<PrExpenseItemVo> list = baseMapper.selectPageList(orgId, itemGroup);
        // 手动分页
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrExpenseItemVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrExpenseItemVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
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
            throw new RuntimeException("该收费项目下存在收费标准，无法删除！");
        }
        return removeById(id);
    }

    @Override
    public boolean isItemName(String companyId, String orgId, String itemName, String id) {
        List<PrExpenseItemVo> duplicates = baseMapper.checkItemName(companyId, orgId, itemName, id);
        return duplicates.isEmpty();
    }
}
