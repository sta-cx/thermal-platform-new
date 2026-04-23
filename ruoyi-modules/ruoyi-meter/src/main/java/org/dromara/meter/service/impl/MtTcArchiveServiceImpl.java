package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtTcArchive;
import org.dromara.meter.domain.vo.MtTcArchiveVo;
import org.dromara.meter.mapper.MtTcArchiveMapper;
import org.dromara.meter.service.IMtTcArchiveService;
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
            baseMapper.insertMeterToAgent(entity);
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        int count = baseMapper.countAllocatedToOtherCompany((String) id);
        if (count > 0) {
            throw new RuntimeException("该温控器已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch((String) id);
        return super.removeById(id);
    }

}
