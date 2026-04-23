package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtCentratorArchive;
import org.dromara.meter.domain.vo.MtCentratorArchiveVo;
import org.dromara.meter.mapper.MtCentratorArchiveMapper;
import org.dromara.meter.service.IMtCentratorArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 集中器档案 Service 实现
 * 迁移自旧系统 MtCentratorArchiveServiceImpl
 */
@Service
@RequiredArgsConstructor
public class MtCentratorArchiveServiceImpl extends ServiceImpl<MtCentratorArchiveMapper, MtCentratorArchive> implements IMtCentratorArchiveService {

    private final MtCentratorArchiveMapper baseMapper;

    @Override
    public TableDataInfo<MtCentratorArchiveVo> selectPageList(LambdaQueryWrapper<MtCentratorArchive> lqw, PageQuery pageQuery) {
        Page<MtCentratorArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtCentratorArchive entity) {
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
