package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatCommandValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatCommandValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatCommandValveArchiveMapper;
import org.sdkj.thermal.service.IPrHeatCommandValveArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 户间控制阀门配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatCommandValveArchiveServiceImpl extends ServiceImpl<PrHeatCommandValveArchiveMapper, PrHeatCommandValveArchive> implements IPrHeatCommandValveArchiveService {

    private final PrHeatCommandValveArchiveMapper baseMapper;

    @Override
    public PrHeatCommandValveArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatCommandValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                                      String unit, String search, String parentId,
                                                                      String valveCategory,
                                                                      PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatCommandValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatCommandValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatCommandValveArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatCommandValveArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatCommandValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatCommandValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatCommandValveArchive::getIsChanged, 0);
        if ("card".equals(valveCategory)) {
            lqw.inSql(PrHeatCommandValveArchive::getArchiveId, "SELECT id FROM mt_tc_valve WHERE type = '1'");
        } else if ("switch".equals(valveCategory)) {
            lqw.inSql(PrHeatCommandValveArchive::getArchiveId, "SELECT id FROM mt_tc_valve WHERE type != '1'");
        }
        lqw.orderByDesc(PrHeatCommandValveArchive::getCreateTime);
        Page<PrHeatCommandValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatCommandValveArchive entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> ids) {
        return super.removeByIds(ids);
    }
}
