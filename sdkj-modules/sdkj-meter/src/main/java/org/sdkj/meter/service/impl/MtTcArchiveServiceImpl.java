package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
            throw new RuntimeException("该温控器已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch((String) id);
        return super.removeById(id);
    }

}
