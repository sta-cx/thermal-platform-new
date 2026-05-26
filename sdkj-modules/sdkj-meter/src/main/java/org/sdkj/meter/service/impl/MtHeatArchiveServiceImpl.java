package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtHeatArchive;
import org.sdkj.meter.domain.vo.MtHeatArchiveVo;
import org.sdkj.meter.mapper.MtHeatArchiveMapper;
import org.sdkj.meter.service.IMtHeatArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 热力表档案 Service 实现
 * 迁移自旧系统 MtHeatArchiveServiceImpl
 */
@Service
@RequiredArgsConstructor
public class MtHeatArchiveServiceImpl extends ServiceImpl<MtHeatArchiveMapper, MtHeatArchive> implements IMtHeatArchiveService {

    private final MtHeatArchiveMapper baseMapper;

    @Override
    public TableDataInfo<MtHeatArchiveVo> selectPageList(LambdaQueryWrapper<MtHeatArchive> lqw, PageQuery pageQuery) {
        Page<MtHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<MtHeatArchiveVo> getHeatList() {
        return baseMapper.selectVoList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<MtHeatArchiveVo> queryMtHeatArchive(MtHeatArchive entity) {
        LambdaQueryWrapper<MtHeatArchive> lqw = new LambdaQueryWrapper<>();
        if (entity.getId() != null) {
            lqw.eq(MtHeatArchive::getId, entity.getId());
        }
        if (entity.getSortId() != null) {
            lqw.eq(MtHeatArchive::getSortId, entity.getSortId());
        }
        if (entity.getName() != null && !entity.getName().isEmpty()) {
            lqw.eq(MtHeatArchive::getName, entity.getName());
        }
        if (entity.getCode() != null && !entity.getCode().isEmpty()) {
            lqw.eq(MtHeatArchive::getCode, entity.getCode());
        }
        lqw.orderByAsc(MtHeatArchive::getSeq).orderByDesc(MtHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtHeatArchive entity) {
        boolean saved = super.save(entity);
        if (saved) {
            baseMapper.insertMeterToAgent(entity);
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        Long archiveId = id instanceof Long ? (Long) id : Long.valueOf(id.toString());
        baseMapper.deleteMeterMatch(archiveId);
        return super.removeById(id);
    }

    /**
     * 更新热力表档案，同时级联同步名称到物业配表记录。
     * 当 name 字段发生变化时，同步更新 pr_heat_hot_archive 和 pr_heat_unit_hot_archive 的 meter_arc_name。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MtHeatArchive entity) {
        // 先查出旧记录，判断名称是否变化
        MtHeatArchive old = baseMapper.selectById(entity.getId());
        boolean updated = super.updateById(entity);
        if (updated && old != null && entity.getName() != null
            && !entity.getName().equals(old.getName())) {
            baseMapper.syncNameToHeatHotArchive(entity.getId(), entity.getName());
            baseMapper.syncNameToHeatUnitHotArchive(entity.getId(), entity.getName());
        }
        return updated;
    }

}
