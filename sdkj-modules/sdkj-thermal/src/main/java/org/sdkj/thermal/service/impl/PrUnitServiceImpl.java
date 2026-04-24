package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrUnit;
import org.sdkj.thermal.domain.vo.PrUnitVo;
import org.sdkj.thermal.mapper.PrUnitMapper;
import org.sdkj.thermal.service.IPrUnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单元信息 Service 实现
 * 迁移自旧系统 PrUnitServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrUnitServiceImpl extends ServiceImpl<PrUnitMapper, PrUnit> implements IPrUnitService {

    private final PrUnitMapper baseMapper;

    @Override
    public PrUnitVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrUnitVo> selectPageList(String search, String buildingId, String orgId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.like(PrUnit::getName, search.trim())
                .or().like(PrUnit::getCode, search.trim()));
        }
        if (buildingId != null && !buildingId.trim().isEmpty()) {
            lqw.eq(PrUnit::getBuildingId, buildingId);
        }
        if (orgId != null && !orgId.trim().isEmpty()) {
            lqw.eq(PrUnit::getOrgId, orgId);
        }
        lqw.orderByAsc(PrUnit::getSeq).orderByDesc(PrUnit::getCreateTime);
        Page<PrUnitVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean validateName(String name, String id) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrUnit::getName, name);
        if (id != null && !id.trim().isEmpty()) {
            lqw.ne(PrUnit::getId, id);
        }
        return baseMapper.selectCount(lqw) > 0;
    }

    @Override
    public List<PrUnitVo> selectByBuildingId(String buildingId) {
        LambdaQueryWrapper<PrUnit> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrUnit::getBuildingId, buildingId);
        lqw.orderByAsc(PrUnit::getSeq);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrUnit entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

}
