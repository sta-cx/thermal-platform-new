package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.HtScope;
import org.dromara.thermal.domain.vo.HtTasksVo;
import org.dromara.thermal.mapper.HtTasksMapper;
import org.dromara.thermal.mapper.HtScopeMapper;
import org.dromara.thermal.service.IHtTasksService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 调控任务服务实现
 */
@Service
@RequiredArgsConstructor
public class HtTasksServiceImpl extends ServiceImpl<HtTasksMapper, HtTasks> implements IHtTasksService {

    private final HtTasksMapper baseMapper;
    private final HtScopeMapper scopeMapper;

    @Override
    public TableDataInfo<HtTasksVo> selectPageList(LambdaQueryWrapper<HtTasks> lqw, PageQuery pageQuery) {
        Page<HtTasksVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtTasks> selectAllByOrgId(String orgId) {
        return baseMapper.selectAllByOrgId(orgId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithScope(HtTasks entity, List<String> scopeIds) {
        boolean saved = save(entity);
        if (saved && scopeIds != null && !scopeIds.isEmpty()) {
            for (String scopeId : scopeIds) {
                if (scopeId == null || scopeId.isBlank()) continue;
                HtScope scope = new HtScope();
                scope.setTasksId(String.valueOf(entity.getId()));
                scope.setId(scopeId);
                scopeMapper.insert(scope);
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithScope(HtTasks entity, List<String> scopeIds) {
        boolean updated = updateById(entity);
        if (updated) {
            // 删除旧的范围记录
            baseMapper.deleteScopeByTaskId(entity.getId());
            // 插入新的范围记录
            if (scopeIds != null && !scopeIds.isEmpty()) {
                for (String scopeId : scopeIds) {
                    if (scopeId == null || scopeId.isBlank()) continue;
                    HtScope scope = new HtScope();
                    scope.setTasksId(String.valueOf(entity.getId()));
                    scope.setId(scopeId);
                    scopeMapper.insert(scope);
                }
            }
        }
        return updated;
    }
}
