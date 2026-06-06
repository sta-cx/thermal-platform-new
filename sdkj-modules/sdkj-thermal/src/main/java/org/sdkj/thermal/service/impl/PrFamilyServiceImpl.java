package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrFamily;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.vo.PrFamilyVo;
import org.sdkj.thermal.mapper.PrFamilyMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IPrFamilyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 家庭成员信息 Service 实现
 * 迁移自旧系统 PrFamilyServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrFamilyServiceImpl extends ServiceImpl<PrFamilyMapper, PrFamily> implements IPrFamilyService {

    private final PrFamilyMapper baseMapper;
    private final PrHouseMapper houseMapper;

    @Override
    public PrFamilyVo selectById(Serializable id) {
        PrFamilyVo family = baseMapper.selectVoById(id);
        if (family == null) {
            return null;
        }
        return canAccessHouse(family.getHouseId()) ? family : null;
    }

    @Override
    public TableDataInfo<PrFamilyVo> selectPageList(String search, String houseId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrFamily> lqw = new LambdaQueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.like(PrFamily::getName, search.trim())
                .or().like(PrFamily::getUserIdNo, search.trim())
                .or().like(PrFamily::getFamilyIdNo, search.trim()));
        }
        if (houseId != null && !houseId.trim().isEmpty()) {
            Long parsedHouseId = parseHouseId(houseId);
            if (!canAccessHouse(parsedHouseId)) {
                return TableDataInfo.build(List.of());
            }
            lqw.eq(PrFamily::getHouseId, parsedHouseId);
        } else {
            List<Long> houseIds = accessibleHouseIds();
            if (houseIds.isEmpty()) {
                return TableDataInfo.build(List.of());
            }
            lqw.in(PrFamily::getHouseId, houseIds);
        }
        lqw.orderByDesc(PrFamily::getCreateTime);
        Page<PrFamilyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrFamily entity) {
        assertHouseAccessible(entity.getHouseId());
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(PrFamily entity) {
        assertExistingFamilyAccessible(entity.getId());
        assertHouseAccessible(entity.getHouseId());
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        assertExistingFamilyAccessible(id);
        return super.removeById(id);
    }

    private Long parseHouseId(String houseId) {
        try {
            return Long.valueOf(houseId.trim());
        } catch (NumberFormatException e) {
            throw new ServiceException("房屋ID格式错误");
        }
    }

    private List<Long> accessibleHouseIds() {
        return houseMapper.selectList(new LambdaQueryWrapper<PrHouse>()
                .select(PrHouse::getId))
            .stream()
            .map(PrHouse::getId)
            .toList();
    }

    private boolean canAccessHouse(Long houseId) {
        return houseId != null && houseMapper.selectById(houseId) != null;
    }

    private void assertHouseAccessible(Long houseId) {
        if (!canAccessHouse(houseId)) {
            throw new ServiceException("无权操作房屋数据");
        }
    }

    private void assertExistingFamilyAccessible(Serializable id) {
        PrFamily existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new ServiceException("家庭成员不存在");
        }
        assertHouseAccessible(existing.getHouseId());
    }

}
