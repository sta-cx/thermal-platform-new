package org.sdkj.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.system.domain.SysColumn;
import org.sdkj.system.domain.vo.SysColumnVo;
import org.sdkj.system.mapper.SysColumnMapper;
import org.sdkj.system.service.ISysColumnService;
import org.springframework.stereotype.Service;

/**
 * 用户自定义表格列服务实现
 */
@Service
public class SysColumnServiceImpl extends ServiceImpl<SysColumnMapper, SysColumn> implements ISysColumnService {

    @Override
    public SysColumnVo getByUserIdAndPageName(Long userId, String pageName) {
        return getBaseMapper().selectVoOne(new LambdaQueryWrapper<SysColumn>()
            .eq(SysColumn::getUserId, userId)
            .eq(SysColumn::getPageName, pageName));
    }

    @Override
    public synchronized boolean saveOrUpdate(SysColumn sysColumn) {
        // 基于 userId+pageName 唯一约束做 upsert
        // synchronized 防止并发插入导致重复键异常
        SysColumn existing = getBaseMapper().selectOne(new LambdaQueryWrapper<SysColumn>()
            .eq(SysColumn::getUserId, sysColumn.getUserId())
            .eq(SysColumn::getPageName, sysColumn.getPageName()));
        if (existing != null) {
            sysColumn.setId(existing.getId());
            return getBaseMapper().updateById(sysColumn) > 0;
        }
        return getBaseMapper().insert(sysColumn) > 0;
    }

    @Override
    public boolean deleteById(Long id, Long userId) {
        return remove(new LambdaQueryWrapper<SysColumn>()
            .eq(SysColumn::getId, id)
            .eq(SysColumn::getUserId, userId));
    }
}
