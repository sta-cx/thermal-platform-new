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
        if (entity.getId() != null && !entity.getId().isEmpty()) {
            lqw.eq(MtHeatArchive::getId, entity.getId());
        }
        if (entity.getSortId() != null && !entity.getSortId().isEmpty()) {
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
            int inserted = baseMapper.insertMeterToAgent(entity);
            if (inserted == 0) {
                throw new RuntimeException("未找到默认代理商公司，无法自动分配");
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        int count = baseMapper.countAllocatedToOtherCompany((String) id);
        if (count > 0) {
            throw new RuntimeException("该仪表已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch((String) id);
        return super.removeById(id);
    }

}
