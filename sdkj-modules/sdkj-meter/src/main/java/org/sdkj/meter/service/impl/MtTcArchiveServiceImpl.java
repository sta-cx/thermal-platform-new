package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtTcArchive;
import org.sdkj.meter.domain.vo.MtTcArchiveVo;
import org.sdkj.meter.mapper.MtTcArchiveMapper;
import org.sdkj.meter.service.IMtTcArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 温控器档案 Service 实现
 * 迁移自旧系统 MtTcArchiveServiceImpl
 */
@Service
@RequiredArgsConstructor
public class MtTcArchiveServiceImpl extends ServiceImpl<MtTcArchiveMapper, MtTcArchive> implements IMtTcArchiveService {

    private final MtTcArchiveMapper baseMapper;

    @Override
    public TableDataInfo<MtTcArchiveVo> selectPageList(LambdaQueryWrapper<MtTcArchive> lqw, PageQuery pageQuery) {
        Page<MtTcArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<MtTcArchiveVo> selectList(LambdaQueryWrapper<MtTcArchive> lqw) {
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtTcArchive entity) {
        boolean saved = super.save(entity);
        if (saved) {
            int inserted = baseMapper.insertMeterToAgent(entity);
            if (inserted == 0) {
                throw new ServiceException("未找到默认代理商公司，无法自动分配");
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        Long archiveId = id instanceof Long ? (Long) id : Long.valueOf(id.toString());
        int count = baseMapper.countAllocatedToOtherCompany(archiveId);
        if (count > 0) {
            throw new ServiceException("该温控器已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch(archiveId);
        return super.removeById(id);
    }

    /**
     * 更新温控器档案，同时级联同步名称到物业配表记录。
     * 当 name 字段发生变化时，同步更新 pr_heat_temp_archive 的 meter_arc_name。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MtTcArchive entity) {
        MtTcArchive old = baseMapper.selectById(entity.getId());
        boolean updated = super.updateById(entity);
        if (updated && old != null && entity.getName() != null
            && !entity.getName().equals(old.getName())) {
            baseMapper.syncNameToHeatTempArchive(entity.getId(), entity.getName());
        }
        return updated;
    }

}
