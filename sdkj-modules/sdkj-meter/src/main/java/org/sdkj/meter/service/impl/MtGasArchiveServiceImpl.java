package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtGasArchive;
import org.sdkj.meter.domain.vo.MtGasArchiveVo;
import org.sdkj.meter.mapper.MtGasArchiveMapper;
import org.sdkj.meter.service.IMtGasArchiveService;
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
            throw new ServiceException("该仪表已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch(archiveId);
        return super.removeById(id);
    }

}
