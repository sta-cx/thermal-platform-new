package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtGasArchive;
import org.dromara.meter.domain.vo.MtGasArchiveVo;
import org.dromara.meter.mapper.MtGasArchiveMapper;
import org.dromara.meter.service.IMtGasArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 燃气表档案 Service 实现
 * 迁移自旧系统 MtGasArchiveServiceImpl
 */
@Service
@RequiredArgsConstructor
public class MtGasArchiveServiceImpl extends ServiceImpl<MtGasArchiveMapper, MtGasArchive> implements IMtGasArchiveService {

    private final MtGasArchiveMapper baseMapper;

    @Override
    public TableDataInfo<MtGasArchiveVo> selectPageList(LambdaQueryWrapper<MtGasArchive> lqw, PageQuery pageQuery) {
        Page<MtGasArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtGasArchive entity) {
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
            throw new RuntimeException("该仪表已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch((String) id);
        return super.removeById(id);
    }

}
