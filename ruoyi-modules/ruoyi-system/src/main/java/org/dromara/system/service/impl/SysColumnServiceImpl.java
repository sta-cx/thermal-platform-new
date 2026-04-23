package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.system.domain.SysColumn;
import org.dromara.system.domain.vo.SysColumnVo;
import org.dromara.system.mapper.SysColumnMapper;
import org.dromara.system.service.ISysColumnService;
import org.springframework.stereotype.Service;

/**
 * 用户自定义表格列服务实现
 */
@Service
@RequiredArgsConstructor
public class SysColumnServiceImpl implements ISysColumnService {

    private final SysColumnMapper baseMapper;

    @Override
    public SysColumnVo getByUserIdAndPageName(Long userId, String pageName) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SysColumn>()
            .eq(SysColumn::getUserId, userId)
            .eq(SysColumn::getPageName, pageName));
    }

    @Override
    public int saveOrUpdate(SysColumn sysColumn) {
        SysColumn existing = baseMapper.selectOne(new LambdaQueryWrapper<SysColumn>()
            .eq(SysColumn::getUserId, sysColumn.getUserId())
            .eq(SysColumn::getPageName, sysColumn.getPageName()));
        if (existing != null) {
            sysColumn.setId(existing.getId());
            return baseMapper.updateById(sysColumn);
        }
        return baseMapper.insert(sysColumn);
    }

    @Override
    public int deleteById(Long id) {
        return baseMapper.deleteById(id);
    }
}
