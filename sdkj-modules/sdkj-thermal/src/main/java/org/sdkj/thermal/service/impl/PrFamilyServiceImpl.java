package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrFamily;
import org.sdkj.thermal.domain.vo.PrFamilyVo;
import org.sdkj.thermal.mapper.PrFamilyMapper;
import org.sdkj.thermal.service.IPrFamilyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 家庭成员信息 Service 实现
 * 迁移自旧系统 PrFamilyServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrFamilyServiceImpl extends ServiceImpl<PrFamilyMapper, PrFamily> implements IPrFamilyService {

    private final PrFamilyMapper baseMapper;

    @Override
    public TableDataInfo<PrFamilyVo> selectPageList(String search, String houseId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrFamily> lqw = new LambdaQueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.like(PrFamily::getName, search.trim())
                .or().like(PrFamily::getUserIdNo, search.trim())
                .or().like(PrFamily::getFamilyIdNo, search.trim()));
        }
        if (houseId != null && !houseId.trim().isEmpty()) {
            lqw.eq(PrFamily::getHouseId, houseId);
        }
        lqw.orderByDesc(PrFamily::getCreateTime);
        Page<PrFamilyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrFamily entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

}
